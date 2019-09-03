package com.tapc.test.model.test;

import android.app.Activity;
import android.os.SystemClock;

import com.tapc.platform.model.device.controller.HardwareStatusController;
import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;

import io.reactivex.ObservableEmitter;

public class SafeKeyTest extends BaseTest {
    private static final int LOCK_STATUS = 0x00;
    private static final int UNLOCK_STATUS = 0xff;

    public SafeKeyTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        return Commands.REGISTER_SKEY_AGING;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        int status = testSafekey(LOCK_STATUS);
        if (status == HardwareStatusController.SAFEKEY_MASK_VALUE) {
            status = testSafekey(UNLOCK_STATUS);
            if (status == 0) {
                testItem.setStatus(TestSatus.OK);
            } else {
                testItem.setStatus(TestSatus.FAIL);
            }
        } else {
            testItem.setStatus(TestSatus.FAIL);
        }
        stop();
    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);
    }

    private int testSafekey(int cmd) {
        uartCtl.sendStartTestCommand(commands, cmd, 0);
        SystemClock.sleep(500);
        return MachineController.getInstance().getSafeKeyStatus();
    }
}
