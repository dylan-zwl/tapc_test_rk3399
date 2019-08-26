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
import com.tapc.test.model.test.ManualTest;
import com.tapc.test.model.test.CopyFileTest;
import com.tapc.test.model.test.USBTest;
import com.tapc.test.ui.activity.presenter.mcu.TestMessagePresenter;
import com.tapc.test.ui.adpater.TestAdapter;
import com.tapc.test.ui.base.BaseRecyclerViewAdapter;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestMessageItem;
import com.tapc.test.ui.widget.MenuBar;
import com.tapc.test.ui.widget.MessageDialog;
import com.tapc.test.utils.SysUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements ITestCallback {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initVersionView();

    }

    private void initVersionView() {
        String appVersion = "TAPC 测试软件 : v" + SysUtils.getVersionName(this);
        mAppVersionTv.setText(appVersion);

        MachineController.getInstance().sendCtlVersionCmd(null);
        SystemClock.sleep(500);
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

    private void initView() {
        ButterKnife.bind(this);
        mActivity = this;

        mMenuBar = new MenuBar(this);
        mMenuBar.show();

        mMessageDialog = new MessageDialog(this);

        mMessagePresenter = new TestMessagePresenter(this, mMessageRecyclerView);

        mTestList = new ArrayList<>();
        for (TestItem item : TestItem.values()) {
            mTestList.add(item);
        }
//        mTestList.add(TestItem.USB);
//        mTestList.add(TestItem.TF);
//        mTestList.add(TestItem.UDISK);
//
//        mTestList.add(TestItem.BACKLIGHT);
//        mTestList.add(TestItem.TFT_COLOR);
//        mTestList.add(TestItem.TOUCHSCREEN);

        mAdapter = new TestAdapter(mTestList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3,
                RecyclerView.VERTICAL, false);
        mListView.setLayoutManager(gridLayoutManager);
        mListView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<TestItem>() {
            @Override
            public void onItemClick(View view, TestItem testItem) {
                switch (testItem) {
                    case USB:
                        USBTest usbTest = new USBTest(mActivity, testItem);
                        usbTest.setTestCallback(MainActivity.this);
                        usbTest.start();
                        break;
                    case TF:
                        CopyFileTest tfTest = new CopyFileTest(mActivity, testItem);
                        tfTest.setTestCallback(MainActivity.this);
                        tfTest.start();
                        break;
                    case UDISK:
                        CopyFileTest udiskTest = new CopyFileTest(mActivity, testItem);
                        udiskTest.setTestCallback(MainActivity.this);
                        udiskTest.start();
                        break;
                    case TFT_COLOR:
                    case TOUCHSCREEN:
                    case BACKLIGHT:
                        ManualTest manualTest = new ManualTest(mActivity, testItem);
                        manualTest.setTestCallback(MainActivity.this);
                        manualTest.setMenuBar(mMenuBar);
                        manualTest.start();
                        break;
                }
            }
        });
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
                        mMessageDialog.hide();
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
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
