package com.tapc.test.model.test;

import android.app.Activity;
import android.os.SystemClock;
import android.text.TextUtils;

import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.activity.BrightnessTestActivity;
import com.tapc.test.ui.base.BaseActivity;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;
import com.tapc.test.utils.IntentUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.ObservableEmitter;

public class BrightnessTest extends BaseTest {

    public BrightnessTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        return Commands.REGISTER_WHT_AGING;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        IntentUtil.startActivity(activity, BrightnessTestActivity.class);
        testItem.setStatus(TestSatus.NG);
        while (testItem.getStatus() == TestSatus.NG) {
            SystemClock.sleep(200);
        }
        stop();
    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void testFinished() {
        
    }

}
