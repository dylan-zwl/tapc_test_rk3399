package com.tapc.test.model.test;

import android.app.Activity;
import android.os.SystemClock;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.ui.activity.BrightnessTestActivity;
import com.tapc.test.ui.activity.TVActivity;
import com.tapc.test.ui.activity.TftTestActivity;
import com.tapc.test.ui.activity.TouchTestActivity;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;
import com.tapc.test.ui.event.ManualTestFinishedEvent;
import com.tapc.test.ui.widget.MenuBar;
import com.tapc.test.ui.widget.TestResultDialog;
import com.tapc.test.utils.IntentUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.ObservableEmitter;

public class ManualTest extends BaseTest {
    private static final String SHOW_MENU_BAR = "show_menu_bar";
    private static final String HIDE_MENU_BAR = "hide_menu_bar";
    private MenuBar mMenuBar;


    public ManualTest(Activity activity, TestItem item) {
        super(activity, item);
        setProgressDialogVisibility(false);
    }

    @Override
    public Commands getCommand() {
        return null;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        emitter.onNext(HIDE_MENU_BAR);
        SystemClock.sleep(500);
        EventBus.getDefault().register(this);
        switch (testItem) {
            case BACKLIGHT:
                IntentUtil.startActivity(activity, BrightnessTestActivity.class);
                break;
            case TFT_COLOR:
                IntentUtil.startActivity(activity, TftTestActivity.class);
                break;
            case TOUCHSCREEN:
                IntentUtil.startActivity(activity, TouchTestActivity.class);
                break;
            case TV:
                IntentUtil.startActivity(activity, TVActivity.class);
                break;
            default:
                testItem.setStatus(TestSatus.FAIL);
                break;
        }
        while (testItem.getStatus() == TestSatus.IN_TESTING) {
            SystemClock.sleep(200);
        }
        EventBus.getDefault().unregister(this);
        emitter.onNext(SHOW_MENU_BAR);
    }

    @Override
    public void handleTestProcessData(Object o) {
        super.handleTestProcessData(o);
        if (mMenuBar == null) {
            return;
        }
        String cmd = (String) o;
        switch (cmd) {
            case SHOW_MENU_BAR:
                mMenuBar.show();
                break;
            case HIDE_MENU_BAR:
                mMenuBar.dismiss();
                break;
        }
    }

    @Override
    protected void receiveCommands(int testResult) {
        super.receiveCommands(testResult);
    }


    public void setMenuBar(MenuBar menuBar) {
        mMenuBar = menuBar;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void testFinished(ManualTestFinishedEvent event) {
        TestResultDialog testResultDialog = new TestResultDialog(activity, testItem);
        testResultDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void recvTestResult(TestItem item) {
        testItem.setStatus(item.getStatus());
    }
}
