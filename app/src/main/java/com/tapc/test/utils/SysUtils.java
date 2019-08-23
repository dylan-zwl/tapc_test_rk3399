/**
 * SysUtils.java[v 1.0.0]
 * classes:com.jht.tapc.platform.utils.SysUtils
 * fch Create of at 2015年4月23日 下午3:03:32
 */
package com.tapc.test.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SysUtils {

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (Exception e) {
        }
        return versionName;
    }


    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    static public boolean testCopyFile(Context context, String file, String newPath) {
        try {
            int byteread = 0;
            String newFilePath = newPath + file;
            InputStream inStream = context.getResources().getAssets().open(file);
            FileOutputStream fs = new FileOutputStream(newFilePath);
            byte[] buffer = new byte[1444];
            while ((byteread = inStream.read(buffer)) < 0) {
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();
            File newFile = new File(newFilePath);
            if (newFile.exists()) {
                newFile.delete();
                return true;
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
        return false;
    }
}
