/**
 * SysUtils.java[v 1.0.0]
 * classes:com.jht.tapc.platform.utils.SysUtils
 * fch Create of at 2015年4月23日 下午3:03:32
 */
package com.tapc.test.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

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

    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     *
     * @param path 要安装的apk文件的路径
     * @return 安装成功返回true，安装失败返回false。
     */
    public static String command(String command) {
        StringBuilder result = new StringBuilder();
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su 548 ");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            command = command + " \n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            result.append(msg);
        } catch (Exception e) {
            result.append(e.getMessage());
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                result.append(e.getMessage());
            }
        }
        return result.toString();
    }
}
