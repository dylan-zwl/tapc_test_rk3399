package com.tapc.test.model.base;

import android.app.Activity;


import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.ReceivePacket;
import com.tapc.test.model.usb.UartCtl;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;
import com.tapc.test.utils.RxjavaUtils;

import java.util.Observable;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2017/5/4.
 */

public abstract class BaseTest implements ITest {
    protected Activity activity;
    protected Commands commands;
    protected TestItem testItem;
    protected UartCtl uartCtl = UartCtl.getInstance();
    protected Disposable disposable;
    protected ITestCallback testCallback;

    private boolean isShowProgressDialog = true;

    public BaseTest(Activity activity, TestItem item) {
        this.activity = activity;
        this.testItem = item;
        this.commands = getCommand();
        init();
    }

    protected void init() {
    }

    public abstract Commands getCommand();

    @Override
    public void start() {
        if (commands != null) {
            uartCtl.subscribeDataReceivedNotification(this);
        }
        if (isShowProgressDialog && testCallback != null && testItem != null) {
            testCallback.handleMessage(MessageType.SHOW_TEST_PROGRESS, testItem.getName());
        }
        testItem.setStatus(TestSatus.IN_TESTING);
        testItem.setTestFinished(false);
        creatDisposable();
    }

    @Override
    public void stop() {
        if (commands != null) {
            uartCtl.sendStopTestCommand(commands);
            uartCtl.unsubscribeDataReceivedNotification(this);
        }

        if (testCallback != null && testItem != null) {
            if (testItem.getStatus() == TestSatus.IN_TESTING) {
                testItem.setStatus(TestSatus.FAIL);
            }
            testCallback.setTestResult(testItem);
            testCallback.handleMessage(MessageType.HIDE_TEST_PROGRESS, "");
            testItem.setTestFinished(true);
        }
    }

    protected void receiveCommands(int testResult) {

    }

    protected void receiveCommands(int testResult, byte[] resultData) {

    }

    @Override
    public void update(Observable observable, Object o) {
        ReceivePacket receivePacket = (ReceivePacket) o;
        if (receivePacket == null) {
            return;
        }
        if (receivePacket.getCommand() == commands) {
            byte[] resultData = receivePacket.getData();
            if (resultData != null && resultData.length > 0) {
                receiveCommands(resultData[0]);
            }
        }
    }

    public void testProcess(ObservableEmitter<Object> emitter) {

    }

    public void handleTestProcessData(Object o) {

    }

    public void cancelDisposable() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void creatDisposable() {
        cancelDisposable();
        disposable = RxjavaUtils.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                testProcess(emitter);
                stop();
            }
        }, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                handleTestProcessData(o);
            }
        }, null);
    }

    public void setTestCallback(ITestCallback testCallback) {
        this.testCallback = testCallback;
    }

    protected void setProgressDialogVisibility(boolean isShowProgressDialog) {
        this.isShowProgressDialog = isShowProgressDialog;
    }
}
