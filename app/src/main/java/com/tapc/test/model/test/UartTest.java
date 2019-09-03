package com.tapc.test.model.test;

import android.app.Activity;
import android.os.SystemClock;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.model.usb.RecvTestResult;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;

import io.reactivex.ObservableEmitter;

public class UartTest extends BaseTest {

    public UartTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        Commands commands = null;
        switch (testItem) {
            case RFID_UART:
                commands = Commands.REGISTER_RFID_UART_AGING;
                break;
            case LEU_UART:
                commands = Commands.REGISTER_LEU_UART_AGING;
                break;
            case NB_NOT_UART:
                commands = Commands.REGISTER_NB_NOT_UART_AGING;
                break;
        }
        return commands;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        uartCtl.sendStartTestCommand(commands, 0, 0);
        SystemClock.sleep(200);
        if (testItem.getStatus() == TestSatus.IN_TESTING) {
            testItem.setStatus(TestSatus.FAIL);
        }
    }

    @Override
    protected void receiveCommands(int testResult) {
        if (testResult == RecvTestResult.ATS_SUCC) {
            testItem.setStatus(TestSatus.OK);
        } else {
            testItem.setStatus(TestSatus.FAIL);
        }
    }
}
