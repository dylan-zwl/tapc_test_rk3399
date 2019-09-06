package com.tapc.test.model.test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

import com.tapc.platform.model.device.controller.HardwareStatusController;
import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.model.usb.RecvTestResult;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;

import io.reactivex.ObservableEmitter;

public class KeyboardTest extends BaseTest {

    public KeyboardTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        Commands commands = null;
        switch (testItem) {
            case I2C_KEYBOARD:
                commands = Commands.REGISTER_I2C_AGING;
                break;
            case MARTIX_KEYBOARD:
                commands = Commands.REGISTER_KEY_AGING;
                break;
        }
        return commands;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        uartCtl.sendStartTestCommand(commands, 0, 0);
        SystemClock.sleep(3000);
    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);
        if (testResult == RecvTestResult.ATS_SUCC) {
            testItem.setStatus(TestSatus.OK);
        } else {
            testItem.setStatus(TestSatus.FAIL);
        }
    }
}
