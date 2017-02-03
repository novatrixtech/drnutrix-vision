package br.com.novatrix.vision.controller;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.com.caelum.vraptor.view.Results;
import br.com.novatrix.vision.model.Process;
import br.com.novatrix.vision.model.Type;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.*;

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UploadController {
   private final Result result;

    /**
     * @deprecated CDI eyes only
     */
    protected UploadController() {
        this(null);
    }

    @Inject
    public UploadController(Result result) {
        this.result = result;
    }

    @Get("/")
    public void index() {
        result.use(Results.http()).body("Funciona");
    }

    @Post("/upload")
    public void upload(UploadedFile file) {
        List<Process> results = new ArrayList<>();
        try {
            VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
            service.setApiKey("73a993246fcf7893052e3f278e676b1c3cfb70a7");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            file.writeTo(outputStream);
            ClassifyImagesOptions.Builder builder = new ClassifyImagesOptions.Builder();
            ClassifyImagesOptions options = builder.classifierIds("foods_347689306")
                    .images(outputStream.toByteArray(), file.getFileName()).build();
            System.out.println("vou mandar reposta para o bluemix");
            VisualClassification classification = service.classify(options).execute();
            System.out.println("Recebi reposta do bluemix");
            List<ImageClassification> images = classification.getImages();
            for (ImageClassification image : images) {
                ImageProcessingError error = image.getError();
                if (error != null) {
                    results.add(new Process(Type.ERROR, error.getDescription()));
                    continue;
                }
                if (image.getClassifiers().isEmpty()) {
                    results.add(new Process(Type.ERROR, "could not classify the image"));
                    continue;
                }
                for (VisualClassifier classifier : image.getClassifiers()) {
                    if (classifier.getId().equals("foods_347689306")) {
                        VisualClassifier.VisualClass validClass = null;
                        for (VisualClassifier.VisualClass visualClass : classifier.getClasses()) {
                            if (validClass == null) {
                                validClass = visualClass;
                            } else {
                                String[] splitVisual = visualClass.getTypeHierarchy().split("/");
                                String[] splitValid = validClass.getTypeHierarchy().split("/");
                                if (visualClass.getScore() > 0.75) {
                                    if (splitVisual.length >= splitValid.length ){
                                        if (visualClass.getScore() > validClass.getScore()) {
                                            validClass = visualClass;
                                        }
                                    }
                                } else if (visualClass.getScore() > validClass.getScore()) {
                                    validClass = visualClass;
                                }
                            }
                        }
                        if (validClass == null) {
                            results.add(new Process(Type.ERROR, " it's not a food"));
                        } else {
                            results.add(new Process(Type.SUCCESS, validClass.getName()));
                        }
                    }
                }
            }

        } catch (Exception e) {
            results.add(new Process(Type.ERROR, e.getMessage()));
        }
        result.use(Results.json()).withoutRoot().from(results).recursive().serialize();
    }
}
