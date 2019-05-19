package com.meta.adbhelper.controller;

import com.meta.adbhelper.Context;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Controller {
    public TabPane center_panel;
    public HBox top_panel;
    public VBox left_panel;
    public ListView left_list_panel;
    public TabPane right_panel;
    public TabPane bottom_panel;


    /**
     * 初始化
     */
    public void init() {
        initTopView();
        initLeftView();
        initBottomView();
        initRightView();
        initCenterView();
    }

    /**
     * 初始化中间的布局
     */
    private void initCenterView() {
        CenterController.initViews(center_panel);
    }

    /**
     * 初始化右边的布局
     */
    private void initRightView() {

    }

    /**
     * 初始化下边的布局
     */
    private void initBottomView() {

    }

    /**
     * 初始化左边的布局
     */
    private void initLeftView() {

    }

    /**
     * 初始化顶部的布局
     */
    private void initTopView() {

    }
}
