package com.tapc.test.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.tapc.test.R;
import com.tapc.test.ui.base.BaseSystemView;
import com.tapc.test.utils.WindowManagerUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;


public class MessageDialog extends BaseSystemView {
    @BindView(R.id.message_test_item)
    TextView mMessage;

    public MessageDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.widget_message;
    }

    @Override
    public WindowManager.LayoutParams getLayoutParams() {
        return WindowManagerUtils.getLayoutParams(0, 0, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, Gravity.CENTER);
    }

    @Override
    protected void initView() {
        super.initView();

    }

    public void setMessage(String text) {
        show();
        mMessage.setText(text);
    }

    @OnClick(R.id.stop_test)
    protected void stopTest() {
        System.exit(0);
    }
}
