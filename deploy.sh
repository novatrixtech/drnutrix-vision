#!/usr/bin/env bash
mvn package
heroku plugins:install heroku-cli-deploy
heroku deploy:war target/vision.war dry-mountain-27773