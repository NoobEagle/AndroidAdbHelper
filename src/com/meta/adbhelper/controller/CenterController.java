package com.meta.adbhelper.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class CenterController {
    public static void initViews(TabPane center_panel) {
        if (center_panel == null) {
            throw new IllegalArgumentException("中间root布局不能为null");
        }

        ObservableList<Tab> tabs = center_panel.getTabs();
        for (Tab tab : tabs) {
//            tab.
        }

    }
}
