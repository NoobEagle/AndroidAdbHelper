package com.meta.adbhelper.controller;

import com.meta.adbhelper.bean.DeviceInfo;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SingleDeviceInfoImpl {

    private VBox leftVBox;
    private VBox rightVBox;

    public SingleDeviceInfoImpl(Tab device_info_tab, DeviceInfo deviceInfo) {
        initInfoViews(device_info_tab);
        updateInfo(deviceInfo);
    }

    public void updateInfo(DeviceInfo deviceInfo) {
        ObservableList<Node> leftVBoxChildren = leftVBox.getChildren();
        leftVBoxChildren.remove(0, leftVBoxChildren.size());
        // 要展示在左边的系统信息都在这里设置
        leftVBoxChildren.add(getText("序列号：" + deviceInfo.getDeviceId()));
        leftVBoxChildren.add(getText("手机品牌：" + deviceInfo.getDeviceBrand()));
        leftVBoxChildren.add(getText("手机型号：" + deviceInfo.getDeviceModel()));
        leftVBoxChildren.add(getText("系统版本：" + deviceInfo.getSystemVersion()));

        ObservableList<Node> rightVBoxChildren = rightVBox.getChildren();
        rightVBoxChildren.remove(0, rightVBoxChildren.size());
        // 要展示在右边的系统信息，都在这里设置
    }

    private Text getText(String info) {
        Text text = new Text(info);
        // 设置字体的属性都在这里
        return text;
    }

    private void initInfoViews(Tab device_info_tab) {
        HBox value = new HBox();
        leftVBox = new VBox();
        rightVBox = new VBox();
        value.getChildren().add(leftVBox);
        value.getChildren().add(rightVBox);
        device_info_tab.setContent(value);
    }

}
