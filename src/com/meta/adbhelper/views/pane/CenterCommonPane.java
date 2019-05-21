package com.meta.adbhelper.views.pane;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class CenterCommonPane extends Pane {
    public CenterCommonPane() {
        init();
    }

    public CenterCommonPane(Node... children) {
//        super(children);
        init();
    }

    private void init() {
        ObservableList<Node> children = getChildren();

        Button button = new Button("安装apk");


        children.add(button);
    }

}
