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

    public static UartCtl getInstance() {
        if (sUartCtl == null) {
            sUartCtl = new UartCtl();
        }
        return sUartCtl;
    }

    private UartCtl() {
        isOpened = open();
        if (isOpened) {
            ReadThread readThread = new ReadThread();
            readThread.start();
        }
    }

    public boolean open() {
        try {
            mFileInputStream = new FileInputStream(DEVICE_NAME);
            mFileOutputStream = new FileOutputStream(DEVICE_NAME);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        isOpened = false;
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

    class ReadThread extends Thread {

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

    public void sendStartTestCommand(Commands command, int data1, int data2) {
        byte[] data = new byte[3];
        data[0] = TestUMcuCmd.START;
        data[1] = (byte) data1;
        data[2] = (byte) data2;
        sendCommand(command, data);
    }

    public void sendClearTestCommand(Commands command) {
        byte[] data = new byte[3];
        data[0] = TestUMcuCmd.CLEAR_RES;
        sendCommand(command, data);
    }

    public void sendStopTestCommand(Commands command) {
        byte[] data = new byte[3];
        data[0] = TestUMcuCmd.STOP;
        sendCommand(command, data);
    }
}
