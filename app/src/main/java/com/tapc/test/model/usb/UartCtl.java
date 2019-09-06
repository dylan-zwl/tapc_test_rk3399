package com.tapc.test.model.usb;


import android.os.SystemClock;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.CommunicationPacket;
import com.tapc.platform.model.device.controller.uart.ReceiveDataHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observer;

public class UartCtl {
    private static UartCtl sUartCtl;
    private static final String DEVICE_NAME = "/dev/ttyXRUSB0";

    private boolean isOpened = false;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private int mUsbIndex = -1;
    private ReadThread mReadThread;

    public static UartCtl getInstance() {
        if (sUartCtl == null) {
            sUartCtl = new UartCtl();
        }
        return sUartCtl;
    }

    public void setMasterUsb() {
        sUartCtl = null;
        sUartCtl = this;
    }

    public void setUsbIndex(int usb) {
        mUsbIndex = usb;
    }

    public int getUsbIndex() {
        return mUsbIndex;
    }

    private UartCtl() {
//        isOpened = open(DEVICE_NAME);
//        if (isOpened) {
//            mReadThread = new ReadThread();
//            mReadThread.start();
//        }
    }

    public UartCtl(String deviceName) {
        isOpened = open(deviceName);
        if (isOpened) {
            mReadThread = new ReadThread();
            mReadThread.start();
        }
    }

    public boolean open(String deviceName) {
        try {
            mFileInputStream = new FileInputStream(deviceName);
            mFileOutputStream = new FileOutputStream(deviceName);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        isOpened = false;

        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        SystemClock.sleep(100);
        if (mFileInputStream != null) {
            try {
                mFileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return isOpened;
    }

    //发送命令
    public boolean sendCommand(Commands commands, byte[] data) {
        CommunicationPacket com = new CommunicationPacket(commands);
        com.setData(data);
        byte[] sendDate = com.getPacketByteArray();
        try {
            if (mFileOutputStream != null) {
                mFileOutputStream.write(sendDate);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //接收命令
    ReceiveDataHandler mReceiveDataHandler;

    public class ReadThread extends Thread {

        ReadThread() {
            this.setPriority(MAX_PRIORITY);
            if (mReceiveDataHandler == null) {
                mReceiveDataHandler = new ReceiveDataHandler();
            }
        }

        public void run() {
            if (mFileInputStream != null) {
                byte[] dataArray = new byte[1];
                while (isConnected()) {
                    try {
                        if (mFileInputStream.read(dataArray, 0, 1) != 0) {
                            mReceiveDataHandler.handleReceivedByte(dataArray[0]);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void subscribeDataReceivedNotification(Observer o) {
        if (mReceiveDataHandler != null) {
            mReceiveDataHandler.subscribeDataReceivedNotification(o);
        }
    }

    public void unsubscribeDataReceivedNotification(Observer o) {
        if (mReceiveDataHandler != null) {
            mReceiveDataHandler.unsubscribeDataReceivedNotification(o);
        }
    }

    //下发测试板开始测试命令
    public void sendStartTestCommand(Commands command, int data1, int data2) {
        byte[] data = new byte[3];
        data[0] = TestUMcuCmd.START;
        data[1] = (byte) data1;
        data[2] = (byte) data2;
        sendCommand(command, data);
    }

    //下发测试板清除数据命令
    public void sendClearTestCommand(Commands command) {
        byte[] data = new byte[3];
        data[0] = TestUMcuCmd.CLEAR_RES;
        sendCommand(command, data);
    }

    //下发测试板停止测试命令
    public void sendStopTestCommand(Commands command) {
        byte[] data = new byte[3];
        data[0] = TestUMcuCmd.STOP;
        sendCommand(command, data);
    }
}
