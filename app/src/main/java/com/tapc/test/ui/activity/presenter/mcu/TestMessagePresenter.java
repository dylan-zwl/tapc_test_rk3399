package com.tapc.test.ui.activity.presenter.mcu;

import android.content.Context;
import android.text.TextUtils;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.test.ui.adpater.TestAdapter;
import com.tapc.test.ui.adpater.TestMessageAdapter;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestMessageItem;

import java.util.ArrayList;
import java.util.List;

public class TestMessagePresenter {
    private Context mContext;
    private TestMessageAdapter mAdapter;
    private List<TestMessageItem> mTestList;

    public TestMessagePresenter(Context context, RecyclerView recyclerView) {
        mContext = context;
        mTestList = new ArrayList<>();
        mAdapter = new TestMessageAdapter(mTestList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);
    }

    public void addMessage(TestMessageItem item) {
        mTestList.add(item);
        mAdapter.notifyDataSetChanged();
    }

    public void cleanMessage() {
        mTestList.clear();
        mAdapter.notifyDataSetChanged();
    }

}
