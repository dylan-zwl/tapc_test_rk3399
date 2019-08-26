package com.tapc.test.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.tapc.test.R;
import com.tapc.test.ui.base.BaseActivity;
import com.tapc.test.ui.event.BrightnessResultEvent;
import com.tapc.test.ui.widget.TouchView;
import com.tapc.test.ui.widget.TouchView2;


import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TouchTestActivity extends BaseActivity {
    @BindView(R.id.touch_test_finish)
    Button touchTestFinish;
    @BindView(R.id.touch_view)
    TouchView touView;
    @BindView(R.id.touch_view2)
    TouchView2 touView2;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_touch;
    }

    @Override
    protected void initView() {
        super.initView();
        Settings.System.putInt(getContentResolver(), "show_touches", 1);
        Settings.System.putInt(getContentResolver(), "pointer_location", 1);
    }

    @OnClick(R.id.touch_test_finish)
    protected void testFinish(View v) {
        Settings.System.putInt(getContentResolver(), "show_touches", 0);
        Settings.System.putInt(getContentResolver(), "pointer_location", 0);
        EventBus.getDefault().post(new BrightnessResultEvent());
        this.finish();
    }
}
