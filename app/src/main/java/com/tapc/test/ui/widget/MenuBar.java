package com.tapc.test.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;


import com.tapc.platform.jni.Driver;
import com.tapc.test.R;
import com.tapc.test.application.Config;
import com.tapc.test.ui.activity.presenter.mcu.McuPresenter;
import com.tapc.test.ui.activity.presenter.mcu.UpdateConttract;
import com.tapc.test.ui.base.BaseSystemView;
import com.tapc.test.ui.event.GetMcuVersionEvent;
import com.tapc.test.ui.event.StartTestEvent;
import com.tapc.test.utils.WindowManagerUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.OnClick;


public class MenuBar extends BaseSystemView {
    private ProgressDialog mProgressDialog;
    private Handler mHandler;

    public MenuBar(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.widget_menu;
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        int height = (int) getResources().getDimension(R.dimen.menu_height);
        return WindowManagerUtils.getLayoutParams(0, 0, WindowManager.LayoutParams.MATCH_PARENT,
                height, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    @Override
    protected void initView() {
        super.initView();
        mHandler = new Handler();
        mProgressDialog = new ProgressDialog(mContext);
    }

    @OnClick(R.id.menu_update_mcu)
    protected void updateMcu() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                showProgressDialog(View.VISIBLE, 0, "");
                McuPresenter mcuPresenter = new McuPresenter(mContext, new UpdateConttract.View() {
                    @Override
                    public void updateProgress(int percent, String msg) {
                        showProgressDialog(View.VISIBLE, percent, "");
                    }

                    @Override
                    public void updateCompleted(boolean isSuccess, String msg) {
                        showProgressDialog(View.VISIBLE, mProgressDialog.getProgress(), msg);
                    }
                });
                Config.EX_SD_FILE_PATH = "/storage/sdcard0/";
                mcuPresenter.update(Config.EX_SD_FILE_PATH);
            }
        }).start();
    }

    private void showProgressDialog(final int visibility, final int progress,
                                    final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.setProgress(progress);
                mProgressDialog.setMessage(message);
                if (visibility == View.VISIBLE) {
                    mProgressDialog.show();
                } else {
                    mProgressDialog.hide();
                }
            }
        });
    }

    @OnClick(R.id.menu_setting)
    protected void setting() {
        mContext.startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    @OnClick(R.id.menu_exit)
    protected void exit() {
        System.exit(0);
    }

    @OnClick(R.id.menu_uninstall)
    protected void uninstall() {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        mContext.startActivity(intent);
    }

    @OnClick(R.id.menu_start)
    protected void startTest() {
        EventBus.getDefault().post(new StartTestEvent());
    }

    @OnClick(R.id.menu_back)
    protected void back() {
        Driver.back();
    }
}
