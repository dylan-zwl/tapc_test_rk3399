package com.tapc.test.model.test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;

import com.tapc.platform.model.device.controller.HardwareStatusController;
import com.tapc.platform.model.device.controller.MachieStatusController;
import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;
import com.tapc.test.utils.IntentUtil;

import io.reactivex.ObservableEmitter;

public class ControlCmnTest extends BaseTest {

    public ControlCmnTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        return Commands.REGISTER_LCB_COM_AGING;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        IntentUtil.registerReceiver(activity, mStatusReceiver,
                HardwareStatusController.DEVICE_ERROR_STATUS);
        uartCtl.sendStartTestCommand(commands, 0, 0);
        //需得下发命令到下控
        MachineController.getInstance().stopMachine(0);
        MachineController.getInstance().startMachine(100, 100);
        SystemClock.sleep(2000);
        activity.unregisterReceiver(mStatusReceiver);
    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);

    }

    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(HardwareStatusController.DEVICE_ERROR_STATUS, 0);
            Log.e("StatusReceiver", "onReceive " + String.format("%x", status));
            if (status != 0) {
                testItem.setStatus(TestSatus.OK);
            } else {
                testItem.setStatus(TestSatus.FAIL);
            }
        }
    };
}
