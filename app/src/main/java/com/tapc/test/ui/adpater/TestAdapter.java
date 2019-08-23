package com.tapc.test.ui.adpater;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tapc.test.R;
import com.tapc.test.ui.base.BaseRecyclerViewAdapter;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/8/25.
 */

public class TestAdapter extends BaseRecyclerViewAdapter<TestAdapter.ViewHolder, TestItem> implements View.OnClickListener {

    public TestAdapter(List<TestItem> list) {
        super(list);
    }

    @Override
    public int getContentView() {
        return R.layout.item_test;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        TestItem item = mDatas.get(position);
        String name = item.getName();
        if (name != null) {
            holder.name.setText(name);
        }
        switch (item.getStatus()) {
            case TestSatus.NG:
                holder.status.setText("NG");
                holder.status.setTextColor(mContext.getResources().getColor(R.color.colorNg));
                break;
            case TestSatus.OK:
                holder.status.setText("OK");
                holder.status.setTextColor(mContext.getResources().getColor(R.color.colorOK));
                break;
            case TestSatus.FAIL:
                holder.status.setText("FAIL");
                holder.status.setTextColor(mContext.getResources().getColor(R.color.colorFAIL));
                break;
        }
        holder.layout.setTag(item);
        holder.layout.setOnClickListener(this);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.test_item_rl)
        RelativeLayout layout;
        @BindView(R.id.test_item_name)
        TextView name;
        @BindView(R.id.test_item_status)
        TextView status;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
