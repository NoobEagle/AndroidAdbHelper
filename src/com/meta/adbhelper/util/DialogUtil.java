package com.meta.adbhelper.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class DialogUtil {
    /**
     * 弹出一个通用的确定对话框
     *
     * @param p_header  对话框的信息标题
     * @param p_message 对话框的信息
     * @return 用户点击了是或否
     */
    public static boolean showWarningDialog(Stage stage, String p_header, String p_message) {
//        按钮部分可以使用预设的也可以像这样自己 new 一个
        Alert _alert = new Alert(Alert.AlertType.WARNING);
//        设置窗口的标题
        _alert.setTitle(p_header);
        _alert.setHeaderText(p_message);
//        设置对话框的 icon 图标，参数是主窗口的 stage
        _alert.initOwner(stage);
//        showAndWait() 将在对话框消失以前不会执行之后的代码
        Optional<ButtonType> _buttonType = _alert.showAndWait();
//        根据点击结果返回
//        if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
        return true;
//        } else {
//            return false;
//        }
    }

    /**
     * 弹出一个通用的确定和取消对话框
     *
     * @param p_header  对话框的信息标题
     * @param p_message 对话框的信息
     * @return 用户点击了是或否
     */
    public static boolean showSelectDialog(Stage stage, String p_header, String p_message) {
//        按钮部分可以使用预设的也可以像这样自己 new 一个
        Alert _alert = new Alert(Alert.AlertType.INFORMATION, p_message, new ButtonType("确定", ButtonBar.ButtonData.YES), new ButtonType("取消", ButtonBar.ButtonData.NO));
//        设置窗口的标题
        _alert.setTitle(p_header);
        _alert.setHeaderText(p_message);
//        设置对话框的 icon 图标，参数是主窗口的 stage
        _alert.initOwner(stage);
//        showAndWait() 将在对话框消失以前不会执行之后的代码
        Optional<ButtonType> _buttonType = _alert.showAndWait();
//        根据点击结果返回
        if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
            return true;
        } else {
            return false;
        }
    }

    //    弹出一个信息对话框
    public static boolean showSelectInformationDialog(Stage stage, String p_header, String p_message) {
        Alert _alert = new Alert(Alert.AlertType.INFORMATION, p_header, new ButtonType("确定", ButtonBar.ButtonData.YES), new ButtonType("取消", ButtonBar.ButtonData.NO));
        _alert.setTitle("信息");
        _alert.setHeaderText(p_header);
        _alert.setContentText(p_message);
        _alert.initOwner(stage);
        Optional<ButtonType> _buttonType = _alert.showAndWait();
        if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
            return true;
        } else {
            return false;
        }
    }

    //    弹出一个信息对话框
    public static void showInformationDialog(Stage stage, String p_header, String p_message) {
        Alert _alert = new Alert(Alert.AlertType.INFORMATION);
        _alert.setTitle("信息");
        _alert.setHeaderText(p_header);
        _alert.setContentText(p_message);
        _alert.initOwner(stage);
        _alert.show();
    }
}
