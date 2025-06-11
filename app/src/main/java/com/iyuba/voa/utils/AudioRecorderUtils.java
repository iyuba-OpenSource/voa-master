package com.iyuba.voa.utils;


import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.IntDef;

import com.elvishew.xlog.XLog;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

import me.goldze.mvvmhabit.utils.SDCardUtils;

public class AudioRecorderUtils {

    //文件路径
    private String filePath;
    //文件夹路径
    private String FolderPath;

    private MediaRecorder mMediaRecorder;
    private final String TAG = "AudioRecorderUtils";
    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;

    private OnAudioStatusUpdateListener audioStatusUpdateListener;
    private static Object mLock = new Object();
    private @RecordStatusT
    int status_record = RecordStatus.NONE;

    public static class RecordStatus {
        public static final int NONE = 0;
        public static final int RECORDING = 1;
    }


    /**
     * RecordStatus
     *
     * @return
     */
    public int getRecordStatus() {
        return status_record;
    }


    @IntDef({RecordStatus.NONE, RecordStatus.RECORDING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RecordStatusT {
    }

    private static AudioRecorderUtils instance;

    public static AudioRecorderUtils getInstance() {
        synchronized (mLock) {
            if (instance == null) {
                instance = new AudioRecorderUtils();
            }
        }
        return instance;
    }

    /**
     * 文件存储默认sdcard/record
     */
    private AudioRecorderUtils() {
        //默认保存路径为/sdcard/record/下
        this(SDCardUtils.getFileCachePath()
                + "/voaRecord/");
    }

    private AudioRecorderUtils(String filePath) {

        File path = new File(filePath);
        if (!path.exists())
            path.mkdirs();

        this.FolderPath = filePath;
    }

    private long startTime;
    private long endTime;


    /**
     * 开始录音 使用amr格式
     * 录音文件
     *
     * @return
     */
    public void startRecord() {
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {
            /* ②setAudioSource/setVideoSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // filePath = FolderPath + getNowTime() + ".amr" ;
            filePath = FolderPath + getNowTime() + ".3gp";

            /* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            // AudioRecord audioRecord.
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            updateMicStatus();
            status_record = RecordStatus.RECORDING;
            XLog.e(TAG, "startTime" + startTime);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            XLog.e(TAG, "call startAmr(File mRecAudioFile) failed! e =" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            XLog.e(TAG, "call startAmr(File mRecAudioFile) failed! e= " + e.getMessage());
        }
    }

    /**
     * 停止录音
     */
    public long stopRecord() {
        if (mMediaRecorder == null)
            return 0L;
        endTime = System.currentTimeMillis();

        //有一些网友反应在5.0以上在调用stop的时候会报错，翻阅了一下谷歌文档发现上面确实写的有可能会报错的情况，捕获异常清理一下就行了，感谢大家反馈！
        try {
            status_record = RecordStatus.NONE;
            if (null != mMediaRecorder) {

                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
            }
            mMediaRecorder = null;

            if (null != audioStatusUpdateListener)
                audioStatusUpdateListener.onStop(filePath);
            filePath = "";

        } catch (RuntimeException e) {
            e.printStackTrace();
            XLog.e(TAG, "call stopRecord() failed! e =" + e.getMessage());
            status_record = RecordStatus.NONE;
            if (null != mMediaRecorder) {

                mMediaRecorder.reset();
                mMediaRecorder.release();
            }

            mMediaRecorder = null;

            if (filePath != null) {

                File file = new File(filePath);
                if (file != null && file.exists())
                    file.delete();
            }

            filePath = "";

        }
        return endTime - startTime;
    }

    /**
     * 取消录音
     */
    public void cancelRecord() {

        try {
            status_record = RecordStatus.NONE;
            if (null != mMediaRecorder) {

                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
            }
            mMediaRecorder = null;

        } catch (RuntimeException e) {
            XLog.e(TAG, "call cancelRecord() failed! e =" + e.getMessage());
            status_record = RecordStatus.NONE;
            if (null != mMediaRecorder) {
                mMediaRecorder.reset();
                mMediaRecorder.release();

            }
            mMediaRecorder = null;
        }
        if (filePath != null) {

            File file = new File(filePath);

            if (file != null && file.exists())
                file.delete();
        }

        filePath = "";

    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }

    };


    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }


    public void removeListener() {
        mHandler.removeCallbacks(mUpdateMicStatusTimer);
        audioStatusUpdateListener = null;
        instance = null;
    }


    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {

        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface OnAudioStatusUpdateListener {
        /**
         * 录音中...
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        void onUpdate(double db, long time);

        /**
         * 停止录音
         *
         * @param filePath 保存路径
         */
        void onStop(String filePath);
    }


    public String getNowTime() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return "" + System.currentTimeMillis() / 1000;
        }
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;

        int d = c.get(Calendar.DAY_OF_MONTH);
        int h = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int s = c.get(Calendar.SECOND);
        StringBuffer sb = new StringBuffer("");
        sb.append(y);
        sb.append("_");
        sb.append(m);
        sb.append("_");
        sb.append(d);
        sb.append("-");
        sb.append(h);
        sb.append("_");
        sb.append(min);
        sb.append("_");
        sb.append(s);
        return sb.toString();
    }
}
