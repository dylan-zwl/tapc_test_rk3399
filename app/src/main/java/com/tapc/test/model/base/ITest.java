package com.tapc.test.model.base;

import android.app.Activity;


import com.tapc.test.ui.entity.TestItem;

import java.util.Observer;

/**
 * Created by Administrator on 2017/5/4.
 */

public interface ITest extends Observer {

    void start();

    void stop();
}
