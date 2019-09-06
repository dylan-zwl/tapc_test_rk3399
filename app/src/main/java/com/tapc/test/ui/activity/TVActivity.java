/**
 *
 */
package com.tapc.test.ui.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.tapc.test.R;
import com.tapc.test.ui.base.BaseActivity;
import com.tapc.test.ui.event.ManualTestFinishedEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TVActivity extends BaseActivity implements SurfaceHolder.Callback {
    @BindView(R.id.tv_surfaceview)
    SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_tv;
    }

    @Override
    protected void initView() {
        super.initView();
        initCamera();
    }

    @SuppressWarnings("deprecation")
    void initCamera() {
        mCamera = Camera.open(0);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            long startTime = System.currentTimeMillis();
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            long endTime = System.currentTimeMillis();
            Log.e("TV", "CameraDevice close: " + (endTime - startTime) + "ms");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getWidth();
        int width = display.getHeight();
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size preSize = getCloselyPreSize(true, width, height,
                parameters.getSupportedPreviewSizes());
        parameters.setPreviewSize(preSize.width, preSize.height);
        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @OnClick(R.id.tv_test_finish)
    protected void testFinish() {
        EventBus.getDefault().post(new ManualTestFinishedEvent());
        this.finish();
    }

    /**
     * 通过对比得到与宽高比最接近的预览尺寸（如果有相同尺寸，优先选择）
     *
     * @param isPortrait 是否竖屏
     * @param surfaceWidth 需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList 需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    public static Camera.Size getCloselyPreSize(boolean isPortrait, int surfaceWidth,
                                                int surfaceHeight, List<Camera.Size> preSizeList) {
        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Camera.Size size : preSizeList) {
            if ((size.width == reqTmpWidth) && (size.height == reqTmpHeight)) {
                return size;
            }
        }

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }
}
