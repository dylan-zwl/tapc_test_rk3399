package com.tapc.test.model.test;

import android.app.Activity;
import android.os.SystemClock;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;

import io.reactivex.ObservableEmitter;

public class CommonTest extends BaseTest {

    public CommonTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        return null;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {

    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);

    }
}
