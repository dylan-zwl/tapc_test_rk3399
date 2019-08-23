package com.tapc.test.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapc.platform.jni.Driver;
import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.platform.model.device.controller.uart.UARTController;
import com.tapc.test.R;
import com.tapc.test.application.Config;
import com.tapc.test.model.base.ITestCallback;
import com.tapc.test.model.test.USBTest;
import com.tapc.test.ui.adpater.TestAdapter;
import com.tapc.test.ui.base.BaseRecyclerViewAdapter;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.widget.MenuBar;
import com.tapc.test.ui.widget.MessageDialog;
import com.tapc.test.ui.widget.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements ITestCallback {
    @BindView(R.id.test_recyclerview)
    RecyclerView mListView;

    private Activity mActivity;
    private TestAdapter mAdapter;
    private List<TestItem> mTestList;
    private MenuBar mMenuBar;
    private MessageDialog mMessageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        initMachineCtl();

        mMenuBar = new MenuBar(this);
        mMenuBar.show();

        mMessageDialog = new MessageDialog(this);

        mTestList = new ArrayList<>();
//        for (TestItem item : TestItem.values()) {
//            mTestList.add(item);
//        }
        mTestList.add(TestItem.USB);

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

    private void initMachineCtl() {
        Driver.openUinput(Driver.UINPUT_DEVICE_NAME);
        String deviceName = "";
        switch (Config.DEVICE_TYPE) {
            case RK3399:
                Driver.KEY_EVENT_TYPE = 0;
                deviceName = "/dev/ttyS1";
                break;
        }
        UARTController.DEVICE_NAME = deviceName;
        Driver.initCom(deviceName, 115200);

        MachineController controller = MachineController.getInstance();
        controller.initController(this);
        controller.start();
    }
}
