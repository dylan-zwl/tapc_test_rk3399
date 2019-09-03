package com.tapc.test.model.test;

import android.app.Activity;
import android.os.SystemClock;
import android.text.TextUtils;

import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;

import io.reactivex.ObservableEmitter;

public class McuConnectTest extends BaseTest {

    public McuConnectTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        return null;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        MachineController.getInstance().sendCtlVersionCmd(null);
        SystemClock.sleep(200);
        String recvMcuVersion = MachineController.getInstance().getCtlVersionValue();
        if (!TextUtils.isEmpty(recvMcuVersion)) {
            testItem.setStatus(TestSatus.OK);
        } else {
            testItem.setStatus(TestSatus.FAIL);
            testCallback.handleMessage(MessageType.SHOW_MSG_ERROR, "与主板MCU通讯失败，请查看否烧录程序！" +
                    "\n请先查看好此问题才能继续测试！");
        }
    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);

    }
}
