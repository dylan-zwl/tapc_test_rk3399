package com.tapc.test.ui.entity;

public enum TestItem {
    FAN("风扇测试"),
    SAFEKEY("安全锁测试"),
    HEART("有线心跳测试"),
    WIRELESS_HEART("无线心跳测试"),
    CONTROL_COMMUNICATION("下控通讯测试"),
    MCU_COMMUNICATION("与MCU通讯测试"),

    KEYBOARD("按键口测试"),
    EARPHONE("耳机测试"),
    SPEAKER("喇叭测试"),
    MP3_IN("MP3 测试"),
    AUDIO_IN("AUDIO IN 测试"),
    TV("TV测试"),
    TF("TF卡测试"),
    USB("USB 测试"),
    BLUETOOTH("蓝牙测试"),
    WIFI("WIFI 测试"),

    ERP("待机测试"),
    UDISK("U盘测试"),
    LED("LED测试"),
    BAT("电池测试"),

    LEU_UART("LEU_UART测试"),
    NB_NOT_UART("NB_NOT_UART测试"),
    RFID_UART("RFID测试"),

    TOUCHSCREEN("触摸屏 测试"),
    BACKLIGHT("背光测试"),
    TFT_COLOR("TFT屏色彩测试");

    private String name = "";
    private int status = TestSatus.NG;
    private boolean isTestFinished;

    TestItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isTestFinished() {
        return isTestFinished;
    }

    public void setTestFinished(boolean testFinished) {
        isTestFinished = testFinished;
    }
}
