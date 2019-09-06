/**
 * java[v 1.0.0]
 * classes:com.oxbix.tapc.Config
 * fch Create of at 2015�?2�?3�? 下午5:10:26
 */
package com.tapc.test.application;

import android.os.Environment;

public class Config {
    public static String IN_SD_FILE_PATH = Environment.getExternalStorageDirectory().getPath() +
            "/";
    public static String EX_SD_FILE_PATH = "/storage/sdcard0/";
    public static String UDISK_FILE_PATH = "/storage/usb0/";

    public static DeviceType DEVICE_TYPE = DeviceType.RK3399;

    public enum DeviceType {
        RK3399
    }
}
