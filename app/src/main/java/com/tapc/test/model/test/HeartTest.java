package com.tapc.test.model.test;

import android.app.Activity;
import android.os.SystemClock;

import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.application.Config;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;
import com.tapc.test.ui.entity.TestWay;

import io.reactivex.ObservableEmitter;

public class HeartTest extends BaseTest {
    private int mHearRate = 0;
    private int mCount = 0;

    public HeartTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        Commands commands = null;
        switch (testItem) {
            case HEART:
                commands = Commands.REGISTER_WHT_AGING;
                break;
            case WIRELESS_HEART:
                commands = Commands.REGISTER_WLHT_AGING;
                break;
        }
        return commands;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        mHearRate = 0;
        mCount = 0;
        int min = 50;
        int max = 120;
        boolean result = testHearRate(min, max, TestWay.NONE);
        if (result) {
            testItem.setStatus(TestSatus.OK);
        } else {
            testItem.setStatus(TestSatus.FAIL);
        }
        stop();
    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);
    }

    private boolean testHearRate(int min, int max, int way) {
        testHeartRateOpen(way);
        for (int i = 0; i < 40; i++) {
            SystemClock.sleep(500);
            mHearRate = MachineController.getInstance().getHeartRate();
            if (mHearRate >= min && mHearRate <= max) {
                mCount++;
                if (mCount > 4) {
                    return true;
                }
            }
        }
        return false;
    }

    private void testHeartRateOpen(int data) {
        uartCtl.sendStartTestCommand(commands, data, 0);
    }
}
