package com.tapc.test.ui.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.tapc.test.R;
import com.tapc.test.ui.base.BaseSystemView;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;
import com.tapc.test.utils.WindowManagerUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;


public class TestResultDialog extends BaseSystemView {
    @BindView(R.id.test_result_title)
    TextView mTitle;

    private TestItem mTestItem;

    public TestResultDialog(Context context, TestItem item) {
        super(context);
        mTestItem = item;
        mTitle.setText(item.getName());
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.widget_test_result;
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

    @OnClick(R.id.test_result_success)
    protected void successOnClick() {
        mTestItem.setStatus(TestSatus.OK);
        EventBus.getDefault().post(mTestItem);
    }

    @OnClick(R.id.test_result_success)
    protected void failOnClick() {
        mTestItem.setStatus(TestSatus.FAIL);
        EventBus.getDefault().post(mTestItem);
    }
}
