package com.meta.adbhelper.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Client;
import com.android.ddmlib.Device;
import com.android.ddmlib.IDevice;
import com.meta.adbhelper.bean.DeviceInfo;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdbBridge {
    public static final String ADB_PATH = "/Users/sw/Document/IdeaProjects/AndroidAdbHelper/tool/adb";
    public static final String AAPT_PATH = "/Users/sw/Document/IdeaProjects/AndroidAdbHelper/tool/aapt";
    public static final String AAPT2_PATH = "/Users/sw/Document/IdeaProjects/AndroidAdbHelper/tool/aapt2";


    public static List<DeviceInfo> onLineDevices = new ArrayList<>();
    public static Map<String, DeviceInfo> onLineDevicesMap = new HashMap<>();


    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSS");
    static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("HH");
    private static ClientChangeCallback clientChangeListener;


    public static boolean connect() {
        boolean isConnected = false;
        System.out.println("准备开始连接adb");
        AndroidDebugBridge.init(false);
//        System.out.println("Adb初始化完成");
//        // /Users/sw/Library/Android/sdk/platform-tools
//        String adbPath = "";
//        Map<String, String> getenv = System.getenv();
//        for (Map.Entry<String, String> stringStringEntry : getenv.entrySet()) {
//            String key = stringStringEntry.getKey();
//            String value = stringStringEntry.getValue();
//            System.out.println("完美：" + key + " value:" + value);
//            if (key.equalsIgnoreCase("path")) {
//                if (!TextUtil.isEmpty(value)) {
//                    String[] split = value.split(":");
//                    for (String s : split) {
//                        if (s.endsWith("platform-tools") || s.endsWith("platform-tools/")) {
//                            adbPath = s;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        Properties properties = System.getProperties();
//        Set<String> strings = properties.stringPropertyNames();
//        System.out.println("好的哇，属性？：" + strings);
//        // os.name  java.home
//        System.out.println("好像获取到了路径？ " + adbPath);
////        AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(adbPath, false);
        AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(ADB_PATH, false);
        AndroidDebugBridge.addClientChangeListener(new AndroidDebugBridge.IClientChangeListener() {
            @Override
            public void clientChanged(Client client, int i) {
                println("clientChanged " + client + "  i:" + i);
            }
        });
        AndroidDebugBridge.addDebugBridgeChangeListener(new AndroidDebugBridge.IDebugBridgeChangeListener() {
            @Override
            public void bridgeChanged(AndroidDebugBridge androidDebugBridge) {
                System.out.println("bridgeChanged " + androidDebugBridge);
            }
        });
        AndroidDebugBridge.addDeviceChangeListener(listener);
        waitForDevice(bridge);
        System.out.println("获取adb连接操作 " + bridge);
        isConnected = bridge.isConnected();
        System.out.println("是否连接：" + isConnected);
        Device[] devices = (Device[]) bridge.getDevices();
        System.out.println("数量：" + devices.length);
        for (Device device : devices) {
            System.out.println(device.getAvdName());
        }

        return isConnected;
    }

    /**
     * 等待adb初始化完成
     *
     * @param bridge
     */
    private static void waitForDevice(AndroidDebugBridge bridge) {
        int count = 0;
        while (!bridge.hasInitialDeviceList()) {
            try {
                Thread.sleep(100);
                count++;
            } catch (InterruptedException ignored) {
            }
            if (count > 300) {
                System.err.print("Time out");
                break;
            }
        }
    }

    /**
     * 上下线监听（初始化时也有回调
     */
    private static AndroidDebugBridge.IDeviceChangeListener listener = new AndroidDebugBridge.IDeviceChangeListener() {
        @Override
        public void deviceConnected(IDevice iDevice) {
            // 设备上线
            println("deviceConnected " + iDevice.getSerialNumber() + "   iDevice.isOnline:" + iDevice.isOnline());
            if (iDevice.isOnline()) {
                updateOnlineDevice(iDevice);
            }
        }

        @Override
        public void deviceDisconnected(IDevice iDevice) {
            // 设备下线
            println("deviceDisconnected " + iDevice.getSerialNumber() + "   iDevice.isOnline:" + iDevice.isOnline());
            updateOfflineDevice(iDevice);
        }

        @Override
        public void deviceChanged(IDevice iDevice, int i) {
            println("deviceChanged " + iDevice.getSerialNumber() + "  i:" + i + "   iDevice.isOnline:" + iDevice.isOnline());
            if (iDevice.isOnline()) {
                updateOnlineDevice(iDevice);
            }
        }
    };


    private static void updateOnlineDevice(IDevice iDevice) {
        DeviceInfo deviceInfo;
        try {
            String serialNumber = iDevice.getSerialNumber();
            String name = iDevice.getName();
            String brand = iDevice.getProperty("ro.product.brand");
            String model = iDevice.getProperty(IDevice.PROP_DEVICE_MODEL);
            String systemVersion = iDevice.getProperty(IDevice.PROP_BUILD_VERSION);
            String systemVersionNumber = iDevice.getProperty(IDevice.PROP_BUILD_VERSION_NUMBER);
            String rom = iDevice.getProperty(IDevice.PROP_DEVICE_MANUFACTURER);
            String deviceFlag = brand + "_" + model + "_" + systemVersion;
            deviceInfo = new DeviceInfo();
            deviceInfo.setDeviceBrand(brand);
            deviceInfo.setDeviceModel(model);
            deviceInfo.setDeviceName(name);
            deviceInfo.setDeviceId(serialNumber);
            deviceInfo.setRom(rom);
            deviceInfo.setSystemApi(Integer.parseInt(systemVersionNumber));
            deviceInfo.setSystemVersion(systemVersion);
            deviceInfo.setTimestamp(new Date());
            deviceInfo.setDeviceFlag(deviceFlag);
            onLineDevices.add(deviceInfo);
            onLineDevicesMap.put(deviceFlag, deviceInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (clientChangeListener != null) {
            clientChangeListener.chang(onLineDevices);
        }
    }

    private static void updateOfflineDevice(IDevice iDevice) {
        DeviceInfo d = null;
        for (DeviceInfo onLineDevice : onLineDevices) {
            String serialNumber = iDevice.getSerialNumber();
            if (onLineDevice.getDeviceId().equalsIgnoreCase(serialNumber)) {
                d = onLineDevice;
                break;
            }
        }
        if (d != null) {
            onLineDevices.remove(d);
            onLineDevicesMap.remove(d.getDeviceFlag());
        }
        if (clientChangeListener != null) {
            clientChangeListener.chang(onLineDevices);
        }
    }


    private static void println(String s) {
        System.out.println(s);
    }

    public static void setClientChangeListener(ClientChangeCallback clientChangeListener) {
        AdbBridge.clientChangeListener = clientChangeListener;
    }

    public static List<DeviceInfo> getDevicesList() {
        return onLineDevices;
    }

    public interface ClientChangeCallback {
        void chang(List<DeviceInfo> list);
    }
}
