package com.tapc.test.model.test;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.Log;

import com.tapc.platform.model.device.controller.uart.Commands;
import com.tapc.platform.model.device.controller.uart.ReceivePacket;
import com.tapc.test.R;
import com.tapc.test.application.VoiceInputService;
import com.tapc.test.model.base.BaseTest;
import com.tapc.test.model.usb.RecvTestResult;
import com.tapc.test.model.usb.TestUMcuCmd;
import com.tapc.test.ui.entity.MessageType;
import com.tapc.test.ui.entity.TestItem;
import com.tapc.test.ui.entity.TestSatus;
import com.tapc.test.ui.entity.TestVolumeStatus;
import com.tapc.test.ui.entity.TestWay;
import com.tapc.test.utils.SysUtils;

import java.util.Observable;

import io.reactivex.ObservableEmitter;

public class SoundTest extends BaseTest {
    //测试静音
    private static final int TEST_VOLUME_MUTE = 0;
    //测试最大音
    private static final int TEST_VOLUME_MAX = 1;

    private AudioManager mAudioManager;
    //播放音乐时测得音量值范围
    private int mMinVoltage = 100;
    private int mMaxVoltage = 1000;
    //设置音量为低音量，高音量状态测试
    private int mSetVolumeL = 0;
    private int mSetVolumeH = 0;

    //喇叭播放要测试左右声音通道
    private boolean isSpeakerOutput = false;

    private byte[] mTestResultData;

    public SoundTest(Activity activity, TestItem item) {
        super(activity, item);
    }

    @Override
    public Commands getCommand() {
        Commands commands = null;
        switch (testItem) {
            case AUDIO_IN:
                commands = Commands.REGISTER_AUDIO_IN_HR_AGING;
                break;
            case SPEAKER:
                commands = Commands.REGISTER_NO_IN_HR_AGING;
                break;
            case EARPHONE:
                commands = Commands.REGISTER_NO_IN_EP_AGING;
                break;
        }
        return commands;
    }

    @Override
    public void testProcess(ObservableEmitter<Object> emitter) {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        }
        initVolume();
        uartCtl.sendClearTestCommand(commands);

        if (testItem == TestItem.SPEAKER || testItem == TestItem.EARPHONE) {
            startMediaPlayer();
        } else {
//            startVoiceInput();
        }
        if (testItem == TestItem.SPEAKER || testItem == TestItem.AUDIO_IN) {
            isSpeakerOutput = true;
        }
        if (isSpeakerOutput) {
            setVolume(TestWay.RIGHT, TEST_VOLUME_MUTE);
        } else {
            setVolume(TestWay.LEFT_RIGHT, TEST_VOLUME_MUTE);
        }

        while (testItem.getStatus() == TestSatus.IN_TESTING) {
            SystemClock.sleep(200);
        }

        if (testItem == TestItem.SPEAKER || testItem == TestItem.EARPHONE) {
            stopMediaPlayer();
        } else {
//            stopVoiceInput();
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        ReceivePacket receivePacket = (ReceivePacket) o;
        if (receivePacket == null) {
            return;
        }
        if (receivePacket.getCommand() == commands) {
            mTestResultData = receivePacket.getData();
            if (mTestResultData != null && mTestResultData.length > 0) {
                byte rightResult = 0;
                byte leftResult = 0;
                if (mTestResultData.length >= 3) {
                    //右声道测试状态结果
                    rightResult = mTestResultData[1];
                    //左声道测试状态结果
                    leftResult = mTestResultData[2];
                }
                if (isSpeakerOutput) {
                    switch (mTestResultData[0]) {
                        case RecvTestResult.ATS_SUCC:
                            if (leftResult == TestVolumeStatus.SAG_TRG_FIN) {
                                setVolume(TestWay.LEFT, TEST_VOLUME_MAX);
                            } else if (leftResult == TestVolumeStatus.SAG_FAIL || leftResult == TestVolumeStatus.SAG_SUCC) {
                                checkVolumeData();
                            } else {
                                if (rightResult == TestVolumeStatus.SAG_TRG_FIN) {
                                    setVolume(TestWay.RIGHT, TEST_VOLUME_MAX);
                                } else if (rightResult == TestVolumeStatus.SAG_FAIL || rightResult == TestVolumeStatus.SAG_SUCC) {
                                    setVolume(TestWay.LEFT, TEST_VOLUME_MUTE);
                                }
                            }
                            break;
                        case RecvTestResult.ATS_FAIL:
                        case RecvTestResult.COMUNI_ERR:
                            testItem.setStatus(TestSatus.FAIL);
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (mTestResultData[0]) {
                        case RecvTestResult.ATS_SUCC:
                            if (rightResult == TestVolumeStatus.SAG_TRG_FIN
                                    && leftResult == TestVolumeStatus.SAG_TRG_FIN) {
                                setVolume(TestWay.LEFT_RIGHT, TEST_VOLUME_MAX);
                            } else if (rightResult == TestVolumeStatus.SAG_FAIL
                                    || leftResult == TestVolumeStatus.SAG_FAIL) {
                                checkVolumeData();
                            } else if (rightResult == TestVolumeStatus.SAG_SUCC
                                    && leftResult == TestVolumeStatus.SAG_SUCC) {
                                checkVolumeData();
                            }
                            break;
                        case RecvTestResult.ATS_FAIL:
                        case RecvTestResult.COMUNI_ERR:
                            testItem.setStatus(TestSatus.FAIL);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private boolean checkVolumeData() {
        boolean VolumeResult1 = false;
        boolean VolumeResult2 = false;
        boolean result = false;
        String msgStr = "电压值信息\n";

        int leftVolume1L =
                (int) ((mTestResultData[9] & 0xFF) | ((mTestResultData[10] & 0xFF) << 8));
        int rightVolume1L =
                (int) ((mTestResultData[5] & 0xFF) | ((mTestResultData[6] & 0xFF) << 8));

        int leftVolume1H =
                (int) ((mTestResultData[11] & 0xFF) | ((mTestResultData[12] & 0xFF) << 8));
        int rightVolume1H =
                (int) ((mTestResultData[7] & 0xFF) | ((mTestResultData[8] & 0xFF) << 8));

        if (isSpeakerOutput) {
            msgStr = msgStr + "通道1左声道：（" + leftVolume1L + "~" + leftVolume1H + "）mv\n";
            msgStr = msgStr + "通道1右声道：（" + rightVolume1L + "~" + rightVolume1H + "）mv\n";
        } else {
            msgStr = msgStr + "左声道：（" + leftVolume1L + "~" + leftVolume1H + "）mv\n";
            msgStr = msgStr + "右声道：（" + rightVolume1L + "~" + rightVolume1H + "）mv\n";
        }
        if (testItem == TestItem.AUDIO_IN) {
            //audio in 右声道声音通道不通，暂时不比较
            if (leftVolume1L < mMinVoltage && leftVolume1H > mMaxVoltage) {
                VolumeResult1 = true;
            }
        } else {
            if (leftVolume1L < mMinVoltage && rightVolume1L < mMinVoltage && leftVolume1H > mMaxVoltage
                    && rightVolume1H > mMaxVoltage) {
                VolumeResult1 = true;
            }
        }
        if (isSpeakerOutput) {
            int leftVolume2L =
                    (int) ((mTestResultData[17] & 0xFF) | ((mTestResultData[18] & 0xFF) << 8));
            int rightVolume2L =
                    (int) ((mTestResultData[13] & 0xFF) | ((mTestResultData[14] & 0xFF) << 8));

            int leftVolume2H =
                    (int) ((mTestResultData[19] & 0xFF) | ((mTestResultData[20] & 0xFF) << 8));
            int rightVolume2H =
                    (int) ((mTestResultData[15] & 0xFF) | ((mTestResultData[16] & 0xFF) << 8));

            msgStr = msgStr + "通道2左声道：（" + leftVolume2L + "~" + leftVolume2H + "）mv\n";
            msgStr = msgStr + "通道2右声道：（" + rightVolume2L + "~" + rightVolume2H + "）mv\n";
            if (testItem == TestItem.AUDIO_IN) {
                if (leftVolume2L < mMinVoltage && leftVolume2H > mMaxVoltage) {
                    VolumeResult2 = true;
                }
            } else {
                if (leftVolume2L < mMinVoltage && rightVolume2L < mMinVoltage && leftVolume2H > mMaxVoltage
                        && rightVolume2H > mMaxVoltage) {
                    VolumeResult2 = true;
                }
            }
        }
        if (isSpeakerOutput) {
            result = VolumeResult1 & VolumeResult2;
        } else {
            result = VolumeResult1;
        }
        if (result) {
            testItem.setStatus(TestSatus.OK);
            testCallback.handleMessage(MessageType.SHOW_MSG_NOMAL,
                    testItem.getName() + "测试信息:" + msgStr);
        } else {
            testItem.setStatus(TestSatus.FAIL);
            testCallback.handleMessage(MessageType.SHOW_MSG_ERROR,
                    testItem.getName() + "测试失败:" + msgStr);
        }
        return result;
    }

    private void initVolume() {
//        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        switch (testItem) {
            case AUDIO_IN:
                mSetVolumeL = 0;
                mSetVolumeH = 8;
                mMinVoltage = 100;
                mMaxVoltage = 700;
                break;
            case MP3_IN:
                mSetVolumeL = 0;
                mSetVolumeH = 10;
                mMinVoltage = 100;
                mMaxVoltage = 800;
                break;
            case SPEAKER:
                mSetVolumeL = 0;
                mSetVolumeH = 8;
                mMinVoltage = 100;
                mMaxVoltage = 800;
                break;
            case EARPHONE:
                mSetVolumeL = 0;
                mSetVolumeH = 10;
                mMinVoltage = 100;
                mMaxVoltage = 600;
                break;
            default:
                break;
        }
    }

    //testWay:左右声道，testValume:静音，放音状态
    private void setVolume(int testWay, int testValume) {
        int volume = 0;
        switch (testValume) {
            case TEST_VOLUME_MUTE:
                volume = mSetVolumeL;
                break;
            case TEST_VOLUME_MAX:
                volume = mSetVolumeH;
                break;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                AudioManager.FLAG_PLAY_SOUND);
        sendStartTestCommand(testWay, testValume);
    }

    private void sendStartTestCommand(int testWay, int testValume) {
        byte volumeR = 0x10;
        byte volumeL = 0x20;
        byte[] data = new byte[3];
        data[0] = TestUMcuCmd.START;
        if (testWay == TestWay.LEFT_RIGHT) {
            data[1] = (byte) (volumeR | volumeL | testValume);
        } else if (testWay == TestWay.RIGHT) {
            data[1] = (byte) (volumeR | testValume);
        } else if (testWay == TestWay.LEFT) {
            data[1] = (byte) (volumeL | testValume);
        }
        data[2] = (byte) (testValume * 25);
        uartCtl.sendCommand(commands, data);
    }

    private MediaPlayer mPlayer;

    private void stopMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void startMediaPlayer() {
        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(activity, R.raw.lr);
            mPlayer.setLooping(true);
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer players, int arg1, int arg2) {
                    stopMediaPlayer();
                    return false;
                }
            });
        }
        mPlayer.start();
    }

    private VoiceInputService mVoiceInputService;

    private void startVoiceInput() {
        mVoiceInputService = new VoiceInputService();
        mVoiceInputService.start();
    }

    private void stopVoiceInput() {
        if (mVoiceInputService != null) {
            mVoiceInputService.stop();
        }
    }
}
