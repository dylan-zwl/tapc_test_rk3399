package com.tapc.test.model.test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

import com.tapc.platform.model.device.controller.HardwareStatusController;
import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.activity.MainActivity;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;

import io.reactivex.ObservableEmitter;

public class KeyboardTest extends BaseTest {
    private int mKeyCode = 0;

    public KeyboardTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        return Commands.REGISTER_I2C_AGING;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        IntentFilter keyFilter = new IntentFilter(HardwareStatusController.DEVICE_ERROR_STATUS);
        activity.registerReceiver(mBroadcastReceiver, keyFilter);

        mKeyCode = 0;
        uartCtl.sendStartTestCommand(commands, 0, 0);
        SystemClock.sleep(2000);

        activity.unregisterReceiver(mBroadcastReceiver);

        if (mKeyCode != 0) {
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

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mKeyCode = intent.getIntExtra("HardwareStatusController.DEVICE_ERROR_STATUS", 0);
        }
    };
}
