package com.tapc.test.model.test;

import android.app.Activity;
import android.os.SystemClock;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestResult;
import com.tapc.test.ui.entity.TestSatus;

import java.util.Observable;

import io.reactivex.ObservableEmitter;

public class USBTest extends BaseTest {

    private int mReceiveCommandCount = 0;
    private int mMaxTestNumber = 10;

    public USBTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        return Commands.REGISTER_WHT_AGING;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        mReceiveCommandCount = 0;
        for (int i = 0; i < mMaxTestNumber; i++) {
            uartCtl.sendStartTestCommand(commands, 0, 0);
            SystemClock.sleep(100);
        }

        if (mReceiveCommandCount >= (mMaxTestNumber - 2)) {
            testItem.setStatus(TestSatus.OK);
        } else {
            testItem.setStatus(TestSatus.FAIL);
        }
        stop();
    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);
        mReceiveCommandCount++;
    }
}
