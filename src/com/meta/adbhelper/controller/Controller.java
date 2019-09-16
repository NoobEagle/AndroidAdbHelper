package com.meta.adbhelper.controller;

import com.meta.adbhelper.adb.AdbBridge;
import com.meta.adbhelper.bean.DeviceInfo;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Tab;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    public ListView device_list;
    @FXML
    public Tab device_info_tab;
    @FXML
    public Tab device_tool_tab;
    @FXML
    public Tab device_control_tab;
    @FXML
    public Tab device_apk_manager_tab;
    @FXML
    public Tab device_file_manager_tab;
    @FXML
    public Tab device_log_tab;
    private SingleDeviceInfoImpl singleDeviceInfo;


    /**
     * 初始化
     */
    public void init() {
        System.out.println("尝试 成功");
        MultipleSelectionModel selectionModel = device_list.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(selectModListener);
    }

    public void clickRefresh() {
        List<DeviceInfo> deviceInfos = AdbBridge.getDevicesList();
        AdbBridge.setClientChangeListener(this::updateDeviceListUI);
        updateDeviceListUI(deviceInfos);
    }

    /**
     * 更新设备列表的UI
     *
     * @param deviceInfos
     */
    private void updateDeviceListUI(List<DeviceInfo> deviceInfos) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("刷新");
        for (DeviceInfo deviceInfo : deviceInfos) {
            String deviceFlag = deviceInfo.getDeviceFlag();
            strings.add(deviceFlag);
            System.out.println("当然 添加了 item：" + deviceFlag);
        }
        ObservableList<String> objects = FXCollections.observableArrayList(strings);
        device_list.setItems(objects);

        MultipleSelectionModel selectionModel = device_list.getSelectionModel();
        try {
            selectionModel.selectedItemProperty().removeListener(selectModListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectionModel.selectedItemProperty().addListener(selectModListener);
        selectionModel.select(strings.size() - 1);
    }

    private ChangeListener selectModListener = (observable, oldValue, newValue) -> {
        // mods选择监听
        if (newValue == null) {
            // 处理清空的情况
            return;
        }
        if (newValue.equals("刷新")) {
            clickRefresh();
        } else {
            DeviceInfo deviceInfo = AdbBridge.onLineDevicesMap.get(newValue);
            if (singleDeviceInfo == null) {
                singleDeviceInfo = new SingleDeviceInfoImpl(device_info_tab, deviceInfo);
            } else {
                singleDeviceInfo.updateInfo(deviceInfo);
            }
        }
        System.out.println("选中的设备：" + newValue);
    };
}
