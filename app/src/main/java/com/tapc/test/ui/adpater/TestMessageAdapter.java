package com.tapc.test.ui.adpater;

import android.os.Build;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.tapc.test.R;
import com.tapc.test.ui.base.BaseRecyclerViewAdapter;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestMessageItem;
import com.tapc.test.ui.entity.TestSatus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/8/25.
 */

public class TestMessageAdapter extends BaseRecyclerViewAdapter<TestMessageAdapter.ViewHolder, TestMessageItem> implements View.OnClickListener {

    public TestMessageAdapter(List<TestMessageItem> list) {
        super(list);
    }

    @Override
    public int getContentView() {
        return R.layout.item_test_message;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        TestMessageItem item = mDatas.get(position);
        holder.message.setTag(item);
        holder.message.setOnClickListener(this);
        holder.message.setText(item.getMessage());
        switch (item.getMessageType()) {
            case MessageType.SHOW_MSG_NOMAL:
                holder.message.setTextColor(mContext.getColor(R.color.colorNg));
                break;
            case MessageType.SHOW_MSG_ERROR:
                holder.message.setTextColor(mContext.getColor(R.color.colorFAIL));
                break;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.test_item_message)
        TextView message;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
