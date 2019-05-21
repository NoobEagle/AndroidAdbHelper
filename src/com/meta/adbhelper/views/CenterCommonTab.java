package com.meta.adbhelper.views;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;

public class CenterCommonTab extends Tab {


    public CenterCommonTab() {
        super();
        init();
    }

    public CenterCommonTab(String text) {
        super(text);
        init();
    }

    public CenterCommonTab(String text, Node content) {
        super(text, content);
        init();
    }

    private void init() {
        Pane pane = new Pane();
        ObservableList<Node> children = pane.getChildren();

        // 一键获取ANR日志
        Button btnANR = new Button();
        btnANR.setText("一键获取ANR日志");


        // 安装
        Button btnInstall = new Button();
        btnInstall.autosize();
        ObjectProperty<Pos> posObjectProperty = btnInstall.alignmentProperty();
        posObjectProperty.set(Pos.CENTER);
        btnInstall.setText("安装apk");


        children.add(btnANR);
        children.add(btnInstall);
        setContent(pane);
    }
}
