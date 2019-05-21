package com.meta.adbhelper.controller;

import com.meta.adbhelper.views.CenterCommonTab;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class CenterController {
    public static void initCenterViews(TabPane center_panel) {
        if (center_panel == null) {
            throw new IllegalArgumentException("中间root布局不能为null");
        }

        ObservableList<Tab> tabs = center_panel.getTabs();

        tabs.clear();
//        Tab e = new Tab();
//        e.setText("厉害了");
//        tabs.add(e);
//
//        CenterCommonTab centerCommonTab = new CenterCommonTab("好厉害");
//        tabs.add(centerCommonTab);

        List<Node> nodeArr = new ArrayList<Node>();
        Button btnInstall = new Button("安装Apk");
        btnInstall.setLayoutX(80);
        btnInstall.setLayoutY(80);
        nodeArr.add(btnInstall);
        Node[] nodes = nodeArr.toArray(new Node[nodeArr.size()]);


        Tab tab = new Tab("常用", new Pane(nodes));
        tab.setClosable(false);
        tabs.add(tab);
    }
}
