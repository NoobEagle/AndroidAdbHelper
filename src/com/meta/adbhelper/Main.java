package com.meta.adbhelper;

import com.meta.adbhelper.adb.AdbBridge;
import com.meta.adbhelper.controller.ContainerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        URL resource = getClass().getClassLoader().getResource("layout/main_ui.fxml");
        URL resource = getClass().getClassLoader().getResource("layout/container.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(resource);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("AdbHelper");
        Scene value = new Scene(root);
        value.getStylesheets().add(getClass().getClassLoader().getResource("css/main.css").toExternalForm());

        primaryStage.setAlwaysOnTop(true);  // 置顶模式
//        primaryStage.setFullScreen(true);   // 全屏模式
//        primaryStage.setIconified(true);    // 打开时，默认隐藏
        primaryStage.setScene(value);


        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("drawable/icon.png").openStream()));
        primaryStage.show();
        Context.setStage(primaryStage);

        URL mainUi = getClass().getClassLoader().getResource("layout/main_ui.fxml");
        ContainerController controller = fxmlLoader.getController();
        controller.initialize(mainUi);

        if (!AdbBridge.connect()) {
            // todo 这里需要提示一下，连接adb失败了
            System.out.println("ADB 连接失败了");
        }

//        PopupMenu pm = new PopupMenu();
//        //设置右键菜单
//        MenuItem mi = new MenuItem("exit");
//        //右键菜单监听
//        mi.addActionListener(e -> {
//            //点击右键菜单退出程序
//            System.exit(0);
//        });
//        pm.add(mi);
//        // 添加系统托盘图标.
//        SystemTray tray = SystemTray.getSystemTray();
//        BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource("drawable/icon.png"));
//        TrayIcon trayIcon = new TrayIcon(image, "Adb工具", pm);
//        trayIcon.setToolTip("Adb工具");
//        tray.add(trayIcon);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
