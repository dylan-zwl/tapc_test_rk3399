package com.tapc.test.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.test.R;
import com.tapc.test.model.base.ITestCallback;
import com.tapc.test.model.test.ControlCmnTest;
import com.tapc.test.model.test.ErpTest;
import com.tapc.test.model.test.HeartTest;
import com.tapc.test.model.test.KeyboardTest;
import com.tapc.test.model.test.ManualTest;
import com.tapc.test.model.test.CopyFileTest;
import com.tapc.test.model.test.McuConnectTest;
import com.tapc.test.model.test.SafeKeyTest;
import com.tapc.test.model.test.SoundTest;
import com.tapc.test.model.test.USBTest;
import com.tapc.test.model.test.UartTest;
import com.tapc.test.ui.activity.presenter.mcu.TestMessagePresenter;
import com.tapc.test.ui.adpater.TestAdapter;
import com.tapc.test.ui.base.BaseActivity;
import com.tapc.test.ui.base.BaseRecyclerViewAdapter;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestMessageItem;
import com.tapc.test.ui.entity.TestSatus;
import com.tapc.test.ui.event.BrightnessResultEvent;
import com.tapc.test.ui.event.GetMcuVersionEvent;
import com.tapc.test.ui.event.StartTestEvent;
import com.tapc.test.ui.widget.MenuBar;
import com.tapc.test.ui.widget.MessageDialog;
import com.tapc.test.utils.RxjavaUtils;
import com.tapc.test.utils.SysUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity implements ITestCallback {
    @BindView(R.id.test_recyclerview)
    RecyclerView mListView;
    @BindView(R.id.test_msg_recyclerview)
    RecyclerView mMessageRecyclerView;
    @BindView(R.id.test_app_version)
    TextView mAppVersionTv;
    @BindView(R.id.test_other_version)
    TextView mOtherVersionTv;

    private Activity mActivity;
    private TestAdapter mAdapter;
    private List<TestItem> mTestList;
    private MenuBar mMenuBar;
    private MessageDialog mMessageDialog;

    private TestMessagePresenter mMessagePresenter;

    private Disposable mDisposable;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mActivity = this;

        mMenuBar = new MenuBar(this);
        mMenuBar.show();
        mMessageDialog = new MessageDialog(this);
        mMessagePresenter = new TestMessagePresenter(this, mMessageRecyclerView);

        initTestItemList();

        mAdapter = new TestAdapter(mTestList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3,
                RecyclerView.VERTICAL, false);
        mListView.setLayoutManager(gridLayoutManager);
        mListView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<TestItem>() {
            @Override
            public void onItemClick(View view, TestItem item) {
                test(item);
            }
        });

        initVersionView();
    }

    private void initTestItemList() {
        mTestList = new ArrayList<>();

        mTestList.add(TestItem.USB);
        mTestList.add(TestItem.MCU_COMMUNICATION);

        mTestList.add(TestItem.SAFEKEY);
        mTestList.add(TestItem.HEART);
        mTestList.add(TestItem.WIRELESS_HEART);
        mTestList.add(TestItem.KEYBOARD);
        mTestList.add(TestItem.CONTROL_COMMUNICATION);
        mTestList.add(TestItem.RFID_UART);
        mTestList.add(TestItem.LEU_UART);
        mTestList.add(TestItem.NB_NOT_UART);

        mTestList.add(TestItem.SPEAKER);
        mTestList.add(TestItem.EARPHONE);
        mTestList.add(TestItem.AUDIO_IN);

        mTestList.add(TestItem.TF);
//        mTestList.add(TestItem.UDISK);

        mTestList.add(TestItem.ERP);

        mTestList.add(TestItem.BACKLIGHT);
        mTestList.add(TestItem.TFT_COLOR);
        mTestList.add(TestItem.TOUCHSCREEN);
    }

    private void test(TestItem testItem) {
        switch (testItem) {
            case USB:
                USBTest usbTest = new USBTest(mActivity, testItem);
                usbTest.setTestCallback(this);
                usbTest.start();
                break;

            case MCU_COMMUNICATION:
                McuConnectTest mcuConnectTest = new McuConnectTest(mActivity, testItem);
                mcuConnectTest.setTestCallback(this);
                mcuConnectTest.start();
                break;
            case SAFEKEY:
                SafeKeyTest safeKeyTest = new SafeKeyTest(mActivity, testItem);
                safeKeyTest.setTestCallback(this);
                safeKeyTest.start();
                break;
            case HEART:
            case WIRELESS_HEART:
                HeartTest heartTest = new HeartTest(mActivity, testItem);
                heartTest.setTestCallback(this);
                heartTest.start();
                break;
            case KEYBOARD:
                KeyboardTest keyboardTest = new KeyboardTest(mActivity, testItem);
                keyboardTest.setTestCallback(this);
                keyboardTest.start();
                break;
            case CONTROL_COMMUNICATION:
                ControlCmnTest controlCmnTest = new ControlCmnTest(mActivity, testItem);
                controlCmnTest.setTestCallback(this);
                controlCmnTest.start();
                break;
            case SPEAKER:
            case EARPHONE:
            case AUDIO_IN:
                SoundTest soundTest = new SoundTest(mActivity, testItem);
                soundTest.setTestCallback(this);
                soundTest.start();
                break;

            case TF:
                CopyFileTest tfTest = new CopyFileTest(mActivity, testItem);
                tfTest.setTestCallback(this);
                tfTest.start();
                break;
            case UDISK:
                CopyFileTest udiskTest = new CopyFileTest(mActivity, testItem);
                udiskTest.setTestCallback(this);
                udiskTest.start();
                break;

            case RFID_UART:
            case LEU_UART:
            case NB_NOT_UART:
                UartTest uartTest = new UartTest(mActivity, testItem);
                uartTest.setTestCallback(this);
                uartTest.start();
                break;

            case TFT_COLOR:
            case TOUCHSCREEN:
            case BACKLIGHT:
                ManualTest manualTest = new ManualTest(mActivity, testItem);
                manualTest.setTestCallback(this);
                manualTest.setMenuBar(mMenuBar);
                manualTest.start();
                break;

            case ERP:
                ErpTest erpTest = new ErpTest(mActivity, testItem);
                erpTest.setTestCallback(this);
                erpTest.start();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void startAutoTest(StartTestEvent event) {
        autoTest();
    }

    private boolean isTesting = false;

    public void autoTest() {
        mDisposable = RxjavaUtils.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mTestList != null && mTestList.size() > 0) {
                    for (int index = 0; index < mTestList.size(); index++) {
                        isTesting = true;
                        TestItem testItem = mTestList.get(index);
                        test(testItem);
                        while (!testItem.isTestFinished()) {
                            SystemClock.sleep(20);
                        }
                    }
                }
            }
        }, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {

            }
        }, null);
    }

    @Override
    public void handleMessage(final int type, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case MessageType.SHOW_TEST_PROGRESS:
                        mMessageDialog.setMessage(text);
                        break;
                    case MessageType.HIDE_TEST_PROGRESS:
                        mMessageDialog.dismiss();
                        break;
                    case MessageType.SHOW_MSG_NOMAL:
                    case MessageType.SHOW_MSG_ERROR:
                        mMessagePresenter.addMessage(new TestMessageItem(type, text));
                        break;
                }
            }
        });
    }

    @Override
    public void setTestResult(TestItem item) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isTesting = false;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    //显示版本号
    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void testFinished(GetMcuVersionEvent event) {
        initVersionView();
    }

    private void initVersionView() {
        String appVersion = "TAPC 测试软件 : v" + SysUtils.getVersionName(this);
        mAppVersionTv.setText(appVersion);

        MachineController.getInstance().sendCtlVersionCmd(null);
        SystemClock.sleep(200);
        String recvMcuVersion = MachineController.getInstance().getCtlVersionValue();
        if (TextUtils.isEmpty(recvMcuVersion)) {
            recvMcuVersion = "请烧录Uboot MCU程序";
        } else if (recvMcuVersion.contains("1.1.1")) {
            recvMcuVersion = "请烧录MCU程序";
        }
        String mcuVersion = "MCU : " + recvMcuVersion;

        String osVersion = "  OS : " + android.os.Build.DISPLAY;
        mOtherVersionTv.setText(mcuVersion + osVersion);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
        super.onDestroy();
    }
}
