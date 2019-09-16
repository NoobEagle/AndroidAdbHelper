package com.meta.adbhelper.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class ContainerController {
    /**
     * 日志
     */

    /**
     * container pane
     */
    @FXML
    private StackPane containerPane;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location The location used to resolve relative paths for the root object, or
     *                 <tt>null</tt> if the location is not known.
     */
    public void initialize(URL location) {
        try {
            Node load = FXMLLoader.load(location);
            containerPane.getChildren().addAll(load);

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Parent root = fxmlLoader.load();
            Controller controller = fxmlLoader.getController();
            controller.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
