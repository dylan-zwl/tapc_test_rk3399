package com.tapc.test.model.usb;


import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.CommunicationPacket;
import com.tapc.platform.model.device.controller.uart.ReceiveDataHandler;

import java.util.Observer;

public class UartCtl {
    private static UartCtl sUartCtl;

    public static UartCtl getInstance() {
        if (sUartCtl == null) {
            sUartCtl = new UartCtl();
        }
        return sUartCtl;
    }

    private UartCtl() {

    }

    public boolean open() {
        return false;
    }

    public void close() {

    }

    public boolean isConnected() {
        return false;
    }

    public boolean sendCommand(Commands commands, byte[] data) {
        CommunicationPacket com = new CommunicationPacket(commands);
        com.setData(data);
        byte[] sendDate = com.getPacketByteArray();
        sendData(sendDate.length, sendDate);
        return true;
    }

    private void sendData(int length, byte[] sendDate) {

    }

    ReceiveDataHandler mReceiveDataHandler;

    class ReadThread extends Thread {
        ReadThread() {
            this.setPriority(MAX_PRIORITY);
            if (mReceiveDataHandler == null) {
                mReceiveDataHandler = new ReceiveDataHandler();
            }
        }

        public void run() {
            int readcount = 0;
//            while (true && isUsbConnect()) {
//                readcount = ftDev.getQueueStatus();
//                if (readcount > 0) {
//                    byte[] usbdata = new byte[readcount];
//                    ftDev.read(usbdata, readcount);
//                    for (int i = 0; i < readcount; i++) {
//                        mReceiveDataHandler.handleReceivedByte(usbdata[i]);
//                    }
//                }
//                SystemClock.sleep(20);
//            }
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
