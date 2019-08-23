package com.tapc.test.ui.event;

public class ProgressDialogEvent {
    private int progress;
    private int visibility;
    private String message;

    public ProgressDialogEvent() {
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
