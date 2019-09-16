package com.meta.adbhelper.controller;

import com.meta.adbhelper.bean.DeviceInfo;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SingleDeviceInfoImpl {

    public SingleDeviceInfoImpl(Tab device_info_tab, DeviceInfo deviceInfo) {
        initInfoViews(device_info_tab);
        device_info_tab.setContent(new Text());
    }

    private void initInfoViews(Tab device_info_tab) {
        HBox value = new HBox();
        VBox leftVBox = new VBox();
        VBox rightVBox = new VBox();



        value.getChildren().add(leftVBox);
        value.getChildren().add(rightVBox);
        device_info_tab.setContent(value);
    }

    public void updateInfo(DeviceInfo deviceInfo) {

    }
}
