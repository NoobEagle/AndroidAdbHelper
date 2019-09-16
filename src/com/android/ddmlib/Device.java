//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.android.ddmlib;

import com.android.ddmlib.IDevice.DeviceState;
import com.android.ddmlib.IDevice.DeviceUnixSocketNamespace;
import com.android.ddmlib.IDevice.Feature;
import com.android.ddmlib.IDevice.HardwareFeature;
import com.android.ddmlib.log.LogReceiver;
import com.android.sdklib.AndroidVersion;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Atomics;
import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Device implements IDevice {
    static final String RE_EMULATOR_SN = "emulator-(\\d+)";
    private final String mSerialNumber;
    private String mAvdName = null;
    private DeviceState mState = null;
    private boolean mIsRoot = false;
    private final PropertyFetcher mPropFetcher = new PropertyFetcher(this);
    private final Map<String, String> mMountPoints = new HashMap();
    private final BatteryFetcher mBatteryFetcher = new BatteryFetcher(this);
    private final List<Client> mClients = new ArrayList();
    private final Map<Integer, String> mClientInfo = new ConcurrentHashMap();
    private ClientTracker mClientTracer;
    private static final String LOG_TAG = "Device";
    private static final char SEPARATOR = '-';
    private static final String UNKNOWN_PACKAGE = "";
    private static final long GET_PROP_TIMEOUT_MS = 250L;
    private static final long INITIAL_GET_PROP_TIMEOUT_MS = 2000L;
    private static final int QUERY_IS_ROOT_TIMEOUT_MS = 1000;
    private static final long INSTALL_TIMEOUT_MINUTES;
    private SocketChannel mSocketChannel;
    private Integer mLastBatteryLevel = null;
    private long mLastBatteryCheckTime = 0L;
    private static final String SCREEN_RECORDER_DEVICE_PATH = "/system/bin/screenrecord";
    private static final long LS_TIMEOUT_SEC = 2L;
    private Boolean mHasScreenRecorder;
    private Set<String> mHardwareCharacteristics;
    private int mApiLevel;
    private AndroidVersion mVersion;
    private String mName;

    public String getSerialNumber() {
        return this.mSerialNumber;
    }

    public String getAvdName() {
        return this.mAvdName;
    }

    void setAvdName(String avdName) {
        if (!this.isEmulator()) {
            throw new IllegalArgumentException("Cannot set the AVD name of the device is not an emulator");
        } else {
            this.mAvdName = avdName;
        }
    }

    public String getName() {
        if (this.mName != null) {
            return this.mName;
        } else if (this.isOnline()) {
            this.mName = this.constructName();
            return this.mName;
        } else {
            return this.constructName();
        }
    }

    private String constructName() {
        String manufacturer;
        if (this.isEmulator()) {
            manufacturer = this.getAvdName();
            return manufacturer != null ? String.format("%s [%s]", manufacturer, this.getSerialNumber()) : this.getSerialNumber();
        } else {
            manufacturer = null;
            String model = null;

            try {
                manufacturer = this.cleanupStringForDisplay(this.getProperty("ro.product.manufacturer"));
                model = this.cleanupStringForDisplay(this.getProperty("ro.product.model"));
            } catch (Exception var4) {
            }

            StringBuilder sb = new StringBuilder(20);
            if (manufacturer != null) {
                sb.append(manufacturer);
                sb.append('-');
            }

            if (model != null) {
                sb.append(model);
                sb.append('-');
            }

            sb.append(this.getSerialNumber());
            return sb.toString();
        }
    }

    private String cleanupStringForDisplay(String s) {
        if (s == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder(s.length());

            for(int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if (Character.isLetterOrDigit(c)) {
                    sb.append(Character.toLowerCase(c));
                } else {
                    sb.append('_');
                }
            }

            return sb.toString();
        }
    }

    public DeviceState getState() {
        return this.mState;
    }

    void setState(DeviceState state) {
        this.mState = state;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.mPropFetcher.getProperties());
    }

    public int getPropertyCount() {
        return this.mPropFetcher.getProperties().size();
    }

    public String getProperty(String name) {
        Map<String, String> properties = this.mPropFetcher.getProperties();
        long timeout = properties.isEmpty() ? 2000L : 250L;
        Future future = this.mPropFetcher.getProperty(name);

        try {
            return (String)future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException var7) {
        } catch (ExecutionException var8) {
        } catch (TimeoutException var9) {
        }

        return null;
    }

    public boolean arePropertiesSet() {
        return this.mPropFetcher.arePropertiesSet();
    }

    public String getPropertyCacheOrSync(String name) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        Future future = this.mPropFetcher.getProperty(name);

        try {
            return (String)future.get();
        } catch (InterruptedException var4) {
        } catch (ExecutionException var5) {
        }

        return null;
    }

    public String getPropertySync(String name) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        Future future = this.mPropFetcher.getProperty(name);

        try {
            return (String)future.get();
        } catch (InterruptedException var4) {
        } catch (ExecutionException var5) {
        }

        return null;
    }

    public Future<String> getSystemProperty(String name) {
        return this.mPropFetcher.getProperty(name);
    }

    public boolean supportsFeature(Feature feature) {
        switch(feature) {
            case SCREEN_RECORD:
                if (!this.getVersion().isGreaterOrEqualThan(19)) {
                    return false;
                }

                if (this.mHasScreenRecorder == null) {
                    this.mHasScreenRecorder = this.hasBinary("/system/bin/screenrecord");
                }

                return this.mHasScreenRecorder;
            case PROCSTATS:
                return this.getVersion().isGreaterOrEqualThan(19);
            default:
                return false;
        }
    }

    public boolean supportsFeature(HardwareFeature feature) {
        if (this.mHardwareCharacteristics == null) {
            try {
                String characteristics = this.getProperty("ro.build.characteristics");
                if (characteristics == null) {
                    return false;
                }

                this.mHardwareCharacteristics = Sets.newHashSet(Splitter.on(',').split(characteristics));
            } catch (Exception var3) {
                this.mHardwareCharacteristics = Collections.emptySet();
            }
        }

        return this.mHardwareCharacteristics.contains(feature.getCharacteristic());
    }

    public AndroidVersion getVersion() {
        if (this.mVersion != null) {
            return this.mVersion;
        } else {
            try {
                String buildApi = this.getProperty("ro.build.version.sdk");
                if (buildApi == null) {
                    return AndroidVersion.DEFAULT;
                } else {
                    int api = Integer.parseInt(buildApi);
                    String codeName = this.getProperty("ro.build.version.codename");
                    this.mVersion = new AndroidVersion(api, codeName);
                    return this.mVersion;
                }
            } catch (Exception var4) {
                return AndroidVersion.DEFAULT;
            }
        }
    }

    private boolean hasBinary(String path) {
        CountDownLatch latch = new CountDownLatch(1);
        CollectingOutputReceiver receiver = new CollectingOutputReceiver(latch);

        try {
            this.executeShellCommand("ls " + path, receiver, 2L, TimeUnit.SECONDS);
        } catch (Exception var6) {
            return false;
        }

        try {
            latch.await(2L, TimeUnit.SECONDS);
        } catch (InterruptedException var5) {
            return false;
        }

        String value = receiver.getOutput().trim();
        return !value.endsWith("No such file or directory");
    }

    public String getMountPoint(String name) {
        String mount = (String)this.mMountPoints.get(name);
        if (mount == null) {
            try {
                mount = this.queryMountPoint(name);
                this.mMountPoints.put(name, mount);
            } catch (com.android.ddmlib.TimeoutException var4) {
            } catch (AdbCommandRejectedException var5) {
            } catch (ShellCommandUnresponsiveException var6) {
            } catch (IOException var7) {
            }
        }

        return mount;
    }

    private String queryMountPoint(String name) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        final AtomicReference<String> ref = Atomics.newReference();
        this.executeShellCommand("echo $" + name, new MultiLineReceiver() {
            public boolean isCancelled() {
                return false;
            }

            public void processNewLines(String[] lines) {
                String[] var2 = lines;
                int var3 = lines.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    String line = var2[var4];
                    if (!line.isEmpty()) {
                        ref.set(line);
                    }
                }

            }
        });
        return (String)ref.get();
    }

    public String toString() {
        return this.mSerialNumber;
    }

    public boolean isOnline() {
        return this.mState == DeviceState.ONLINE;
    }

    public boolean isEmulator() {
        return this.mSerialNumber.matches("emulator-(\\d+)");
    }

    public boolean isOffline() {
        return this.mState == DeviceState.OFFLINE;
    }

    public boolean isBootLoader() {
        return this.mState == DeviceState.BOOTLOADER;
    }

    public SyncService getSyncService() throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException {
        SyncService syncService = new SyncService(AndroidDebugBridge.getSocketAddress(), this);
        return syncService.openSync() ? syncService : null;
    }

    public FileListingService getFileListingService() {
        return new FileListingService(this);
    }

    public RawImage getScreenshot() throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException {
        return this.getScreenshot(0L, TimeUnit.MILLISECONDS);
    }

    public RawImage getScreenshot(long timeout, TimeUnit unit) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException {
        return AdbHelper.getFrameBuffer(AndroidDebugBridge.getSocketAddress(), this, timeout, unit);
    }

    public void startScreenRecorder(String remoteFilePath, ScreenRecorderOptions options, IShellOutputReceiver receiver) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException, ShellCommandUnresponsiveException {
        this.executeShellCommand(getScreenRecorderCommand(remoteFilePath, options), receiver, 0L, (TimeUnit)null);
    }

    static String getScreenRecorderCommand(String remoteFilePath, ScreenRecorderOptions options) {
        StringBuilder sb = new StringBuilder();
        sb.append("screenrecord");
        sb.append(' ');
        if (options.width > 0 && options.height > 0) {
            sb.append("--size ");
            sb.append(options.width);
            sb.append('x');
            sb.append(options.height);
            sb.append(' ');
        }

        if (options.bitrateMbps > 0) {
            sb.append("--bit-rate ");
            sb.append(options.bitrateMbps * 1000000);
            sb.append(' ');
        }

        if (options.timeLimit > 0L) {
            sb.append("--time-limit ");
            long seconds = TimeUnit.SECONDS.convert(options.timeLimit, options.timeLimitUnits);
            if (seconds > 180L) {
                seconds = 180L;
            }

            sb.append(seconds);
            sb.append(' ');
        }

        sb.append(remoteFilePath);
        return sb.toString();
    }

    public void executeShellCommand(String command, IShellOutputReceiver receiver) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        AdbHelper.executeRemoteCommand(AndroidDebugBridge.getSocketAddress(), command, this, receiver, DdmPreferences.getTimeOut());
    }

    public void executeShellCommand(String command, IShellOutputReceiver receiver, int maxTimeToOutputResponse) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        AdbHelper.executeRemoteCommand(AndroidDebugBridge.getSocketAddress(), command, this, receiver, maxTimeToOutputResponse);
    }

    public void executeShellCommand(String command, IShellOutputReceiver receiver, long maxTimeToOutputResponse, TimeUnit maxTimeUnits) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        AdbHelper.executeRemoteCommand(AndroidDebugBridge.getSocketAddress(), command, this, receiver, 0L, maxTimeToOutputResponse, maxTimeUnits);
    }

    public void executeShellCommand(String command, IShellOutputReceiver receiver, long maxTimeout, long maxTimeToOutputResponse, TimeUnit maxTimeUnits) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        AdbHelper.executeRemoteCommand(AndroidDebugBridge.getSocketAddress(), command, this, receiver, maxTimeout, maxTimeToOutputResponse, maxTimeUnits);
    }

    public void runEventLogService(LogReceiver receiver) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException {
        AdbHelper.runEventLogService(AndroidDebugBridge.getSocketAddress(), this, receiver);
    }

    public void runLogService(String logname, LogReceiver receiver) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException {
        AdbHelper.runLogService(AndroidDebugBridge.getSocketAddress(), this, logname, receiver);
    }

    public void createForward(int localPort, int remotePort) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException {
        AdbHelper.createForward(AndroidDebugBridge.getSocketAddress(), this, String.format("tcp:%d", localPort), String.format("tcp:%d", remotePort));
    }

    public void createForward(int localPort, String remoteSocketName, DeviceUnixSocketNamespace namespace) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException {
        AdbHelper.createForward(AndroidDebugBridge.getSocketAddress(), this, String.format("tcp:%d", localPort), String.format("%s:%s", namespace.getType(), remoteSocketName));
    }

    public void removeForward(int localPort, int remotePort) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException {
        AdbHelper.removeForward(AndroidDebugBridge.getSocketAddress(), this, String.format("tcp:%d", localPort), String.format("tcp:%d", remotePort));
    }

    public void removeForward(int localPort, String remoteSocketName, DeviceUnixSocketNamespace namespace) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException {
        AdbHelper.removeForward(AndroidDebugBridge.getSocketAddress(), this, String.format("tcp:%d", localPort), String.format("%s:%s", namespace.getType(), remoteSocketName));
    }

    Device(ClientTracker clientTracer, String serialNumber, DeviceState deviceState) {
        this.mClientTracer = clientTracer;
        this.mSerialNumber = serialNumber;
        this.mState = deviceState;
    }

    ClientTracker getClientTracker() {
        return this.mClientTracer;
    }

    public boolean hasClients() {
        synchronized(this.mClients) {
            return !this.mClients.isEmpty();
        }
    }

    public Client[] getClients() {
        synchronized(this.mClients) {
            return (Client[])this.mClients.toArray(new Client[this.mClients.size()]);
        }
    }

    public Client getClient(String applicationName) {
        synchronized(this.mClients) {
            Iterator var3 = this.mClients.iterator();

            Client c;
            do {
                if (!var3.hasNext()) {
                    return null;
                }

                c = (Client)var3.next();
            } while(!applicationName.equals(c.getClientData().getClientDescription()));

            return c;
        }
    }

    void addClient(Client client) {
        synchronized(this.mClients) {
            this.mClients.add(client);
        }

        this.addClientInfo(client);
    }

    List<Client> getClientList() {
        synchronized(this.mClients) {
            return this.mClients;
        }
    }

    void clearClientList() {
        synchronized(this.mClients) {
            this.mClients.clear();
        }

        this.clearClientInfo();
    }

    void removeClient(Client client, boolean notify) {
        this.mClientTracer.trackDisconnectedClient(client);
        synchronized(this.mClients) {
            this.mClients.remove(client);
        }

        if (notify) {
            AndroidDebugBridge.deviceChanged(this, 2);
        }

        this.removeClientInfo(client);
    }

    void setClientMonitoringSocket(SocketChannel socketChannel) {
        this.mSocketChannel = socketChannel;
    }

    SocketChannel getClientMonitoringSocket() {
        return this.mSocketChannel;
    }

    void update(int changeMask) {
        AndroidDebugBridge.deviceChanged(this, changeMask);
    }

    void update(Client client, int changeMask) {
        AndroidDebugBridge.clientChanged(client, changeMask);
        this.updateClientInfo(client, changeMask);
    }

    void setMountingPoint(String name, String value) {
        this.mMountPoints.put(name, value);
    }

    private void addClientInfo(Client client) {
        ClientData cd = client.getClientData();
        this.setClientInfo(cd.getPid(), cd.getClientDescription());
    }

    private void updateClientInfo(Client client, int changeMask) {
        if ((changeMask & 1) == 1) {
            this.addClientInfo(client);
        }

    }

    private void removeClientInfo(Client client) {
        int pid = client.getClientData().getPid();
        this.mClientInfo.remove(pid);
    }

    private void clearClientInfo() {
        this.mClientInfo.clear();
    }

    private void setClientInfo(int pid, String pkgName) {
        if (pkgName == null) {
            pkgName = "";
        }

        this.mClientInfo.put(pid, pkgName);
    }

    public String getClientName(int pid) {
        String pkgName = (String)this.mClientInfo.get(pid);
        return pkgName == null ? "" : pkgName;
    }

    public void pushFile(String local, String remote) throws IOException, AdbCommandRejectedException, com.android.ddmlib.TimeoutException, SyncException {
        SyncService sync = null;

        try {
            String targetFileName = getFileName(local);
            Log.d(targetFileName, String.format("Uploading %1$s onto device '%2$s'", targetFileName, this.getSerialNumber()));
            sync = this.getSyncService();
            if (sync == null) {
                throw new IOException("Unable to open sync connection!");
            }

            String message = String.format("Uploading file onto device '%1$s'", this.getSerialNumber());
            Log.d("Device", message);
            sync.pushFile(local, remote, SyncService.getNullProgressMonitor());
        } catch (com.android.ddmlib.TimeoutException var11) {
            Log.e("Device", "Error during Sync: timeout.");
            throw var11;
        } catch (SyncException var12) {
            Log.e("Device", String.format("Error during Sync: %1$s", var12.getMessage()));
            throw var12;
        } catch (IOException var13) {
            Log.e("Device", String.format("Error during Sync: %1$s", var13.getMessage()));
            throw var13;
        } finally {
            if (sync != null) {
                sync.close();
            }

        }

    }

    public void pullFile(String remote, String local) throws IOException, AdbCommandRejectedException, com.android.ddmlib.TimeoutException, SyncException {
        SyncService sync = null;

        try {
            String targetFileName = getFileName(remote);
            Log.d(targetFileName, String.format("Downloading %1$s from device '%2$s'", targetFileName, this.getSerialNumber()));
            sync = this.getSyncService();
            if (sync == null) {
                throw new IOException("Unable to open sync connection!");
            }

            String message = String.format("Downloading file from device '%1$s'", this.getSerialNumber());
            Log.d("Device", message);
            sync.pullFile(remote, local, SyncService.getNullProgressMonitor());
        } catch (com.android.ddmlib.TimeoutException var11) {
            Log.e("Device", "Error during Sync: timeout.");
            throw var11;
        } catch (SyncException var12) {
            Log.e("Device", String.format("Error during Sync: %1$s", var12.getMessage()));
            throw var12;
        } catch (IOException var13) {
            Log.e("Device", String.format("Error during Sync: %1$s", var13.getMessage()));
            throw var13;
        } finally {
            if (sync != null) {
                sync.close();
            }

        }

    }

    public void installPackage(String packageFilePath, boolean reinstall, String... extraArgs) throws InstallException {
        try {
            String remoteFilePath = this.syncPackageToDevice(packageFilePath);
            this.installRemotePackage(remoteFilePath, reinstall, extraArgs);
            this.removeRemotePackage(remoteFilePath);
        } catch (IOException var5) {
            throw new InstallException(var5);
        } catch (AdbCommandRejectedException var6) {
            throw new InstallException(var6);
        } catch (com.android.ddmlib.TimeoutException var7) {
            throw new InstallException(var7);
        } catch (SyncException var8) {
            throw new InstallException(var8);
        }
    }

    public void installPackages(List<File> apks, boolean reinstall, List<String> installOptions, long timeout, TimeUnit timeoutUnit) throws InstallException {
        try {
            SplitApkInstaller.create(this, apks, reinstall, installOptions).install(timeout, timeoutUnit);
        } catch (InstallException var8) {
            throw var8;
        } catch (Exception var9) {
            throw new InstallException(var9);
        }
    }

    public String syncPackageToDevice(String localFilePath) throws IOException, AdbCommandRejectedException, com.android.ddmlib.TimeoutException, SyncException {
        SyncService sync = null;

        String message;
        try {
            String packageFileName = getFileName(localFilePath);
            String remoteFilePath = String.format("/data/local/tmp/%1$s", packageFileName);
            Log.d(packageFileName, String.format("Uploading %1$s onto device '%2$s'", packageFileName, this.getSerialNumber()));
            sync = this.getSyncService();
            if (sync == null) {
                throw new IOException("Unable to open sync connection!");
            }

            message = String.format("Uploading file onto device '%1$s'", this.getSerialNumber());
            Log.d("Device", message);
            sync.pushFile(localFilePath, remoteFilePath, SyncService.getNullProgressMonitor());
            message = remoteFilePath;
        } catch (com.android.ddmlib.TimeoutException var11) {
            Log.e("Device", "Error during Sync: timeout.");
            throw var11;
        } catch (SyncException var12) {
            Log.e("Device", String.format("Error during Sync: %1$s", var12.getMessage()));
            throw var12;
        } catch (IOException var13) {
            Log.e("Device", String.format("Error during Sync: %1$s", var13.getMessage()));
            throw var13;
        } finally {
            if (sync != null) {
                sync.close();
            }

        }

        return message;
    }

    private static String getFileName(String filePath) {
        return (new File(filePath)).getName();
    }

    public void installRemotePackage(String remoteFilePath, boolean reinstall, String... extraArgs) throws InstallException {
        try {
            Device.InstallReceiver receiver = new Device.InstallReceiver();
            StringBuilder optionString = new StringBuilder();
            if (reinstall) {
                optionString.append("-r ");
            }

            if (extraArgs != null) {
                optionString.append(Joiner.on(' ').join(extraArgs));
            }

            String cmd = String.format("pm install %1$s \"%2$s\"", optionString.toString(), remoteFilePath);
            this.executeShellCommand(cmd, receiver, INSTALL_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            String error = receiver.getErrorMessage();
            if (error != null) {
                throw new InstallException(error);
            }
        } catch (com.android.ddmlib.TimeoutException var8) {
            throw new InstallException(var8);
        } catch (AdbCommandRejectedException var9) {
            throw new InstallException(var9);
        } catch (ShellCommandUnresponsiveException var10) {
            throw new InstallException(var10);
        } catch (IOException var11) {
            throw new InstallException(var11);
        }
    }

    public void removeRemotePackage(String remoteFilePath) throws InstallException {
        try {
            this.executeShellCommand(String.format("rm \"%1$s\"", remoteFilePath), new NullOutputReceiver(), INSTALL_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (IOException var3) {
            throw new InstallException(var3);
        } catch (com.android.ddmlib.TimeoutException var4) {
            throw new InstallException(var4);
        } catch (AdbCommandRejectedException var5) {
            throw new InstallException(var5);
        } catch (ShellCommandUnresponsiveException var6) {
            throw new InstallException(var6);
        }
    }

    public String uninstallPackage(String packageName) throws InstallException {
        try {
            Device.InstallReceiver receiver = new Device.InstallReceiver();
            this.executeShellCommand("pm uninstall " + packageName, receiver, INSTALL_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            return receiver.getErrorMessage();
        } catch (com.android.ddmlib.TimeoutException var3) {
            throw new InstallException(var3);
        } catch (AdbCommandRejectedException var4) {
            throw new InstallException(var4);
        } catch (ShellCommandUnresponsiveException var5) {
            throw new InstallException(var5);
        } catch (IOException var6) {
            throw new InstallException(var6);
        }
    }

    public void reboot(String into) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException {
        AdbHelper.reboot(into, AndroidDebugBridge.getSocketAddress(), this);
    }

    public boolean root() throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException, ShellCommandUnresponsiveException {
        if (!this.mIsRoot) {
            AdbHelper.root(AndroidDebugBridge.getSocketAddress(), this);
        }

        return this.isRoot();
    }

    public boolean isRoot() throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        if (this.mIsRoot) {
            return true;
        } else {
            CollectingOutputReceiver receiver = new CollectingOutputReceiver();
            this.executeShellCommand("echo $USER_ID", receiver, 1000L, TimeUnit.MILLISECONDS);
            String userID = receiver.getOutput().trim();
            this.mIsRoot = userID.equals("0");
            return this.mIsRoot;
        }
    }

    public Integer getBatteryLevel() throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException, ShellCommandUnresponsiveException {
        return this.getBatteryLevel(300000L);
    }

    public Integer getBatteryLevel(long freshnessMs) throws com.android.ddmlib.TimeoutException, AdbCommandRejectedException, IOException, ShellCommandUnresponsiveException {
        Future futureBattery = this.getBattery(freshnessMs, TimeUnit.MILLISECONDS);

        try {
            return (Integer)futureBattery.get();
        } catch (InterruptedException var5) {
            return null;
        } catch (ExecutionException var6) {
            return null;
        }
    }

    public Future<Integer> getBattery() {
        return this.getBattery(5L, TimeUnit.MINUTES);
    }

    public Future<Integer> getBattery(long freshnessTime, TimeUnit timeUnit) {
        return this.mBatteryFetcher.getBattery(freshnessTime, timeUnit);
    }

    public List<String> getAbis() {
        String abiList = this.getProperty("ro.product.cpu.abilist");
        if (abiList != null) {
            return Lists.newArrayList(abiList.split(","));
        } else {
            List<String> abis = Lists.newArrayListWithExpectedSize(2);
            String abi = this.getProperty("ro.product.cpu.abi");
            if (abi != null) {
                abis.add(abi);
            }

            abi = this.getProperty("ro.product.cpu.abi2");
            if (abi != null) {
                abis.add(abi);
            }

            return abis;
        }
    }

    public int getDensity() {
        String densityValue = this.getProperty("ro.sf.lcd_density");
        if (densityValue == null) {
            densityValue = this.getProperty("qemu.sf.lcd_density");
        }

        if (densityValue != null) {
            try {
                return Integer.parseInt(densityValue);
            } catch (NumberFormatException var3) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public String getLanguage() {
        return (String)this.getProperties().get("persist.sys.language");
    }

    public String getRegion() {
        return this.getProperty("persist.sys.country");
    }

    static {
        String installTimeout = System.getenv("ADB_INSTALL_TIMEOUT");
        long time = 4L;
        if (installTimeout != null) {
            try {
                time = Long.parseLong(installTimeout);
            } catch (NumberFormatException var4) {
            }
        }

        INSTALL_TIMEOUT_MINUTES = time;
    }

    static final class InstallReceiver extends MultiLineReceiver {
        private static final String SUCCESS_OUTPUT = "Success";
        private static final Pattern FAILURE_PATTERN = Pattern.compile("Failure\\s+\\[(.*)\\]");
        private String mErrorMessage = null;

        public InstallReceiver() {
        }

        public void processNewLines(String[] lines) {
            String[] var2 = lines;
            int var3 = lines.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String line = var2[var4];
                if (!line.isEmpty()) {
                    if (line.startsWith("Success")) {
                        this.mErrorMessage = null;
                    } else {
                        Matcher m = FAILURE_PATTERN.matcher(line);
                        if (m.matches()) {
                            this.mErrorMessage = m.group(1);
                        } else {
                            this.mErrorMessage = "Unknown failure (" + line + ")";
                        }
                    }
                }
            }

        }

        public boolean isCancelled() {
            return false;
        }

        public String getErrorMessage() {
            return this.mErrorMessage;
        }
    }
}
