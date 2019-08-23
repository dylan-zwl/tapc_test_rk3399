package com.tapc.test.model.mcu;

/**
 * Created by Administrator on 2017/3/17.
 */

public class UpdateConttract {
    public interface View {
        void updateProgress(int percent, String msg);

        void updateCompleted(boolean isSuccess, String msg);
    }

    public interface UpdatePresenter {
        void update(String filePath);
    }
}
