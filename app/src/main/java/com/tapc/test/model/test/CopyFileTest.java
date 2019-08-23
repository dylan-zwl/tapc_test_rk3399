package com.tapc.test.model.test;

import android.app.Activity;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.application.Config;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;
import com.tapc.test.utils.SysUtils;

import io.reactivex.ObservableEmitter;

public class CopyFileTest extends BaseTest {
    private String mFilePath;

    public CopyFileTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        return null;
    }

    @Override
    protected void init() {
        super.init();
        switch (testItem) {
            case UDISK:
                mFilePath = Config.UDISK_FILE_PATH;
                break;
            case TF:
                mFilePath = Config.IN_SD_FILE_PATH;
                break;
        }
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        boolean result = SysUtils.testCopyFile(activity, "test.ogg", mFilePath);
        if (result) {
            testItem.setStatus(TestSatus.OK);
        } else {
            testItem.setStatus(TestSatus.FAIL);
        }
        if (!result && mFilePath.equals(Config.UDISK_FILE_PATH)) {
            //testCallback.handleMessage(MessageType.SHOW_MSG_ERROR, "USB测试失败");
        }
        stop();
    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);
    }
}
