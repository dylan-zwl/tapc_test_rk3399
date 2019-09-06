package com.tapc.test.ui.activity;

import android.view.View;

import com.tapc.test.R;
import com.tapc.test.ui.base.BaseActivity;
import com.tapc.test.ui.event.ManualTestFinishedEvent;
import com.tapc.test.ui.widget.TftColorView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class TftTestActivity extends BaseActivity {
    @BindView(R.id.tftcolor_view)
    TftColorView mTftColorView;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_tft;
    }

    @Override
    protected void initView() {
        super.initView();
        mTftColorView.init(this);
    }

    @OnClick(R.id.tftcolor_view)
    protected void viewOnClick(View v) {
        if (mTftColorView.getIndex() >= TftColorView.CHECK_ITEM) {
            EventBus.getDefault().post(new ManualTestFinishedEvent());
            this.finish();
        }
        mTftColorView.nextTestColor();
    }
}
