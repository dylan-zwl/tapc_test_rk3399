package com.tapc.test.application;

import android.app.Application;

import com.tapc.platform.jni.Driver;
import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.platform.model.device.controller.uart.UARTController;


public class TapcApp extends Application {
    private static TapcApp sTapcApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sTapcApp = this;
        initMachineCtl();
    }

    public static TapcApp getInstance() {
        return sTapcApp;
    }

    private void initMachineCtl() {
        Driver.openUinput(Driver.UINPUT_DEVICE_NAME);
        String deviceName = "";
        switch (Config.DEVICE_TYPE) {
            case RK3399:
                Driver.KEY_EVENT_TYPE = 0;
                deviceName = "/dev/ttyS1";
                break;
        }
        UARTController.DEVICE_NAME = deviceName;
        Driver.initCom(deviceName, 115200);
        Driver.initCom("/dev/ttyXRUSB0", 115200);
        Driver.initCom("/dev/ttyXRUSB1", 115200);

        MachineController controller = MachineController.getInstance();
        controller.initController(this);
        controller.start();
    }
}
