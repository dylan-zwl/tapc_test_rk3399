package com.tapc.test.model.test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;
import com.tapc.test.utils.IntentUtil;

import io.reactivex.ObservableEmitter;

public class ErpTest extends BaseTest {
    private int mScreenOnCount = 0;
    private int mScreenOffCount = 0;

    public ErpTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        return null;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        mScreenOnCount = 0;
        mScreenOffCount = 0;

        IntentUtil.registerReceiver(activity, mBroadcastReceiver, Intent.ACTION_SCREEN_ON,
                Intent.ACTION_SCREEN_ON);
        MachineController.getInstance().enterErpStatus(5);
        SystemClock.sleep(6000);
        activity.unregisterReceiver(mBroadcastReceiver);

        if (mScreenOnCount > 0 && mScreenOffCount > 0) {
            testItem.setStatus(TestSatus.OK);
        } else {
            testItem.setStatus(TestSatus.FAIL);
        }
    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case Intent.ACTION_SCREEN_ON:
                        mScreenOnCount++;
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        mScreenOffCount++;
                        break;
                }
            }
        }
    };
}
