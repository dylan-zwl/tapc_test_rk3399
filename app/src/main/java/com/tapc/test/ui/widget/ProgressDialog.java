package com.tapc.test.ui.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tapc.test.R;
import com.tapc.test.ui.base.BaseSystemView;
import com.tapc.test.ui.event.GetMcuVersionEvent;
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
        mProgress.setProgress(0);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (mProgress.getProgress() == 100) {
                    EventBus.getDefault().post(new GetMcuVersionEvent());
                }
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
}
