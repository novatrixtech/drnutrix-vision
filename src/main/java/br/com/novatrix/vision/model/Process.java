package br.com.novatrix.vision.model;


public class Process {
    private Type type;
    private String result;

    public Process(Type type, String result) {
        this.type = type;
        this.result = result;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Process result1 = (Process) o;

        if (type != result1.type) return false;
        return result != null ? result.equals(result1.result) : result1.result == null;
    }

    @Override
    public int hashCode() {
        int result1 = type != null ? type.hashCode() : 0;
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        return result1;
    }

    @Override
    public String toString() {
        return "Process{" +
                "type=" + type +
                ", result='" + result + '\'' +
                '}';
    }
}
