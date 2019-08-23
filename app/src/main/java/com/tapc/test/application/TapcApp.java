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
    }

    public static TapcApp getInstance() {
        return sTapcApp;
    }

}
