package com.tapc.test.model.test;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.ReceivePacket;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.model.usb.UartCtl;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;
import com.tapc.test.utils.FileUtils;

import java.io.File;
import java.util.Observable;

import io.reactivex.ObservableEmitter;

public class USBTest extends BaseTest {

    private int mReceiveCommandCount = 0;
    private int mMaxTestNumber = 2;
    private UartCtl mCurrentTestUartCtl;

    public USBTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        return Commands.REGISTER_USB_AGING;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        testItem.setStatus(TestSatus.OK);
        uartCtl.close();
        for (int i = 0; i < 2; i++) {
            String filePath = "/dev/ttyXRUSB" + i;
            if (!new File(filePath).exists()) {
                testItem.setStatus(TestSatus.FAIL);
                continue;
            }
            UartCtl uartCtl = new UartCtl(filePath);
            uartCtl.subscribeDataReceivedNotification(this);
            boolean result = test(uartCtl);
            uartCtl.unsubscribeDataReceivedNotification(this);
            if (uartCtl.getUsbIndex() != 0) {
                uartCtl.close();
            }
            if (!result) {
                testItem.setStatus(TestSatus.FAIL);
            }

            if (!result && uartCtl.getUsbIndex() == 0) {
                testCallback.handleMessage(MessageType.SHOW_MSG_ERROR, "与测试架通讯(Panel USB_2)" +
                        "失败，请查看线是否联通或测试架是否烧录程序！" + "\n请先查看好此问题才能继续测试！");
            }
            if (!result && uartCtl.getUsbIndex() == 1) {
                testCallback.handleMessage(MessageType.SHOW_MSG_ERROR, "USB2.0 通讯失败");
            }
        }
    }

    private boolean test(UartCtl uartCtl) {
        mCurrentTestUartCtl = uartCtl;
        mReceiveCommandCount = 0;
        for (int i = 0; i < mMaxTestNumber; i++) {
            uartCtl.sendStartTestCommand(commands, 0, 0);
            SystemClock.sleep(100);
        }
        if (mReceiveCommandCount >= mMaxTestNumber) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        ReceivePacket receivePacket = (ReceivePacket) o;
        if (receivePacket == null) {
            return;
        }
        if (receivePacket.getCommand() == commands) {
            byte[] testResultData = receivePacket.getData();
            if (testResultData != null && testResultData.length >= 2) {
                mReceiveCommandCount++;
                int usb = testResultData[1];
                if (mReceiveCommandCount == mMaxTestNumber) {
                    if (usb == 0) {
                        mCurrentTestUartCtl.setMasterUsb();
                        uartCtl = mCurrentTestUartCtl;
                    }
                    mCurrentTestUartCtl.setUsbIndex(usb);
                    Log.d("usb", "" + usb);
                }
            }
        }
    }
}
