package com.tapc.test.application;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import java.util.LinkedList;

public class VoiceInputService {
    protected int mInbufsize;
    private float mVoiceVolume = 0.1f;
    private AudioRecord mInrec;
    private byte[] mInbytes;
    private LinkedList<byte[]> mInq;
    private int mOutbufsize;
    private AudioTrack mOuttrk;
    private byte[] mOutbytes;
    private Thread mRecord;
    private Thread mPlay;
    private boolean isRun = false;

    public void start() {
        isRun = true;
        initVoice();
        mRecord = new Thread(new RecordSound());
        mPlay = new Thread(new PlayRecord());
        mRecord.start();
        mPlay.start();
    }

    public void stop() {
        isRun = false;
    }

    private void initVoice() {
        mInbufsize = AudioRecord.getMinBufferSize(44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        mInrec = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                mInbufsize);
        mInbytes = new byte[mInbufsize];
        mInq = new LinkedList<byte[]>();

        mOutbufsize = AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);

        mOuttrk = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                mOutbufsize, AudioTrack.MODE_STREAM);
        mOuttrk.setStereoVolume(mVoiceVolume, mVoiceVolume);
        mOutbytes = new byte[mOutbufsize];
    }

    private class RecordSound implements Runnable {
        @Override
        public void run() {
            while (isRun) {
                try {
                    byte[] bytes_pkg;
                    mInrec.startRecording();
                    while (true) {
                        if (mInq.size() < 1000) {
                            mInrec.read(mInbytes, 0, mInbufsize);
                            bytes_pkg = mInbytes.clone();
                            mInq.add(bytes_pkg);
                        } else {
                            Thread.sleep(100);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mInrec.stop();
            }
        }
    }

    private class PlayRecord implements Runnable {
        @Override
        public void run() {
            while (isRun) {
                try {
                    byte[] bytes_pkg = null;
                    mOuttrk.play();
                    while (true) {
                        if (mInq.size() > 0) {
                            mOutbytes = mInq.getFirst();
                            if (mOutbytes.length > 0) {
                                bytes_pkg = mOutbytes.clone();
//                                mInq.removeFirst();
                                mOuttrk.write(bytes_pkg, 0, bytes_pkg.length);
                            } else {
                                Thread.sleep(100);
                            }
                        } else {
                            Thread.sleep(100);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mOuttrk.stop();
            }
        }
    }
}
