package com.meta.adbhelper;

import javafx.stage.Stage;

public class Context {

    private static Stage stage;

    public static void setStage(Stage stage) {
        Context.stage = stage;
    }

    public static Stage getStage() {
        return stage;
    }
}
