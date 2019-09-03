package com.tapc.test.model.test;

import android.app.Activity;
import android.os.SystemClock;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;

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
            SystemClock.sleep(50);
        }

        if (mReceiveCommandCount == mMaxTestNumber) {
            testItem.setStatus(TestSatus.OK);
        } else {
            testItem.setStatus(TestSatus.FAIL);
            testCallback.handleMessage(MessageType.SHOW_MSG_ERROR, "与测试架通讯失败，请查看线是否联通或测试架是否烧录程序！" +
                    "\n请先查看好此问题才能继续测试！");
        }
        stop();
    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);
        mReceiveCommandCount++;
    }
}
