package com.tapc.test.ui.activity.presenter.mcu;

import android.content.Context;
import android.text.TextUtils;

import com.tapc.platform.model.device.controller.IOUpdateController;
import com.tapc.platform.model.device.controller.MachineController;
import com.tapc.test.utils.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/3/17.
 */

public class McuPresenter implements UpdateConttract.UpdatePresenter {
    private Context mContext;
    private UpdateConttract.View mView;
    private MachineController mController;

    public McuPresenter(Context context, UpdateConttract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void update(String filePath) {
        String mcuFileName = FileUtils.getFilename(filePath, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                if (name.startsWith("rom") && name.endsWith(".bin")) {
                    return true;
                }
                return false;
            }
        });

        if (!TextUtils.isEmpty(mcuFileName)) {
            final File file = new File(filePath, mcuFileName);
            if (file != null && file.exists()) {
                //开始升级
                mView.updateProgress(0, "");
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                mController = MachineController.getInstance();
                mController.updateMCU(file.getAbsolutePath(),
                        new IOUpdateController.IOUpdateListener() {
                            @Override
                            public void onProgress(int process, String msg) {
                                mView.updateProgress(process, msg);
                            }

                            @Override
                            public void successful(String msg) {
                                mView.updateCompleted(true, msg);
                                countDownLatch.countDown();
                            }

                            @Override
                            public void failed(String msg) {
                                mView.updateCompleted(false, msg);
                                countDownLatch.countDown();
                            }
                        });
                try {
                    boolean result = countDownLatch.await(180, TimeUnit.SECONDS);
                    if (!result) {
                        mView.updateCompleted(false, "升级超时");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        mView.updateCompleted(false, "没有文件，请查看是否TF卡或U盘根目录下是否有ROM.bin文件");
    }
}
