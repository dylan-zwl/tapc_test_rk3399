package com.tapc.test.ui.entity;

public class TestMessageItem {
    private String message;
    private int messageType = MessageType.SHOW_MSG_NOMAL;

    public TestMessageItem(int messageType, String message) {
        this.message = message;
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
