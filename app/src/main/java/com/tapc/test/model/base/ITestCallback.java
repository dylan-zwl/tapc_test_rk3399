package com.tapc.test.model.base;


import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestResult;

/**
 * Created by Administrator on 2017/5/4.
 */

public interface ITestCallback {
    void handleMessage(int type, String text);

    void setTestResult(TestItem item);
}
