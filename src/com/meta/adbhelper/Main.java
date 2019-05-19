package com.meta.adbhelper;

import com.meta.adbhelper.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = getClass().getClassLoader().getResource("com/meta/adbhelper/sample.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(resource);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("AdbHelper");
        Scene value = new Scene(root);
        primaryStage.setScene(value);
        Controller controller = fxmlLoader.getController();
//        primaryStage.getIcons().add(new Image(
//                Main.class.getClassLoader().getResourceAsStream("resource/timg.jpg")));
        primaryStage.show();
        Context.setStage(primaryStage);
//        Context
        controller.init();

//        Parent root = FXMLLoader.load(getClass().getResource("com/meta/adbhelper/sample.fxml"));
//        primaryStage.setTitle("AdbHelper");
//        primaryStage.setScene(new Scene(root, 600, 400));
//        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
