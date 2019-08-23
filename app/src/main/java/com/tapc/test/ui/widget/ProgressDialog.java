package com.tapc.test.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.tapc.test.R;
import com.tapc.test.ui.base.BaseSystemView;
import com.tapc.test.ui.event.ProgressDialogEvent;
import com.tapc.test.utils.WindowManagerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;


public class ProgressDialog extends BaseSystemView {
    @BindView(R.id.progress)
    SeekBar mProgress;
    @BindView(R.id.progress_percent)
    TextView mPercent;
    @BindView(R.id.progress_message)
    TextView mMessage;

    public ProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.widget_progress;
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return WindowManagerUtils.getLayoutParams(0, 0, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, Gravity.CENTER);
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        mProgress.setProgress(0);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

    public int getProgress() {
        return mProgress.getProgress();
    }

    public void setProgress(int progress) {
        mProgress.setProgress(progress);
        mPercent.setText(progress + "%");
    }

    public void setMessage(String message) {
        if (message == null) {
            message = "";
        }
        mMessage.setText(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(ProgressDialogEvent event) {
        if (event.getVisibility() == View.VISIBLE) {
            setProgress(event.getProgress());
            setMessage(event.getMessage());
            show();
        } else {
            setProgress(0);
            setMessage(null);
            hide();
        }
    }
}
