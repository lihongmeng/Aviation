package com.jxntv.record.recorder.helper;

import android.Manifest;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.idst.nui.AsrResult;
import com.alibaba.idst.nui.CommonUtils;
import com.alibaba.idst.nui.Constants;
import com.alibaba.idst.nui.INativeFileTransCallback;
import com.alibaba.idst.nui.NativeNui;
import com.google.gson.Gson;
import com.jxntv.async.GlobalExecutor;
import com.jxntv.base.callback.AudioRecordListener;
import com.jxntv.base.permission.PermissionCallback;
import com.jxntv.base.permission.PermissionManager;
import com.jxntv.base.utils.StorageUtils;
import com.jxntv.record.recorder.helper.audio.AliAudioTransfer;
import com.jxntv.record.recorder.helper.audio.AliAudioTransferFlashResultSentence;
import com.jxntv.record.recorder.helper.audio.PcmToWav;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.AppManager;
import com.jxntv.utils.AsyncUtils;
import com.jxntv.utils.DeviceId;
import com.jxntv.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 音频录制类
 */
public class AudioRecordManager implements INativeFileTransCallback {

    // <editor-folder desc="单例模式">

    public static AudioRecordManager manager;


    /**
     * 获取manager单例
     */
    public static AudioRecordManager getInstance() {
        if (manager == null) {
            synchronized (AudioRecordManager.class) {
                if (manager == null) {
                    manager = new AudioRecordManager();
                }
            }
        }
        return manager;
    }

    private AudioRecordManager() {
        handlerThread = new HandlerThread("AudioRecord_Manager_read_write_thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    // </editor-folder>

    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采样频率
    private final static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    //缓冲区字节大小
    private int bufferSizeInBytes;
    //录音对象
    private AudioRecord audioRecord;
    //文件名
    private String pcmFileName, wavFileName;
    //录音状态
    private Status status = Status.STATUS_NO_READY;
    private FileOutputStream fileOutputStream;
    private AudioRecordListener listener;

    // 写入文件数据
    private final ArrayList<byte[]> writeDataList = new ArrayList<byte[]>();

    private boolean mInit = false;

    private String aliKey, aliToken;

    private HandlerThread handlerThread;
    private Handler handler;
    private int taskId;

    public void setListener(AudioRecordListener listener) {
        this.listener = listener;
    }

    public void setAliConfig(String aliKey, String aliToken) {
        this.aliKey = aliKey;
        this.aliToken = aliToken;
    }

    /**
     * 创建音频
     */
    private void createAudio() {
        if (TextUtils.isEmpty(aliKey) || TextUtils.isEmpty(aliToken)) {
            if (listener != null) {
                listener.error("未设置key和token");
            }
            return;
        }

        // 录音数据暂存文件
        pcmFileName = StorageUtils.getRecordDirectory().getAbsolutePath() + "/test.pcm";

        bufferSizeInBytes = AudioRecord.getMinBufferSize(
                AUDIO_SAMPLE_RATE,
                AUDIO_CHANNEL,
                AUDIO_ENCODING
        );// 录音组件

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.CAMCORDER,
                AUDIO_SAMPLE_RATE,
                AUDIO_CHANNEL,
                AUDIO_ENCODING,
                bufferSizeInBytes
        );

        //获取工作路径
        String assets_path = CommonUtils.getModelPath(GVideoRuntime.getAppContext());
        String debug_path = StorageUtils.getCacheDirectory().getAbsolutePath() + "/debug_" + System.currentTimeMillis();
        createDir(assets_path);
        createDir(debug_path);
        if (!CommonUtils.copyAssetsData(GVideoRuntime.getAppContext())) {
            LogUtils.e("copyAssetsData fail");
            return;
        }

        int ret = NativeNui.GetInstance().initialize(
                this,
                genInitParams(assets_path, debug_path),
                Constants.LogLevel.LOG_LEVEL_VERBOSE
        );

        if (ret == Constants.NuiResultCode.SUCCESS) {
            mInit = true;
        }
        //设置相关识别参数，具体参考API文档
        NativeNui.GetInstance().setParams(genParams());

        this.status = Status.STATUS_READY;
        try {
            File file = new File(pcmFileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fileOutputStream = new FileOutputStream(pcmFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录音
     */
    public void startRecord() {
        PermissionManager
                .requestPermissions(AppManager.getAppManager().currentActivity(), new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(@NonNull Context context) {
                        createAudio();
                        resumeRecord();
                    }

                    @Override
                    public void onPermissionDenied(
                            @NonNull Context context,
                            @Nullable String[] grantedPermissions,
                            @NonNull String[] deniedPermission) {
                        Toast.makeText(context, "没有录音权限", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onStop();
                        }
                    }
                },Manifest.permission.RECORD_AUDIO);
    }

    public void resumeRecord() {
        if (status == Status.STATUS_NO_READY || TextUtils.isEmpty(pcmFileName)) {
            if (listener != null) {
                listener.error("请先初始化");
                return;
            }
        }
        if (status == Status.STATUS_START) {
            return;
        }
        if (!mInit && listener != null) {
            listener.error("阿里智能语音SDK初始化失败");
            return;
        }

        handler.post(readWriteRunnable);

        LogUtils.d("开始录音");
        status = Status.STATUS_START;
        audioRecord.startRecording();
    }

    private final Runnable readWriteRunnable = new Runnable() {
        @Override
        public void run() {
            if (audioRecord == null) return;
            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                handler.postDelayed(readWriteRunnable, 50);
                return;
            }
            short[] bytes = new short[bufferSizeInBytes];
            final int ret = audioRecord.read(bytes, 0, bufferSizeInBytes);

            if (listener != null) {
                listener.volumeSize(calculateVolume(bytes));
            }

            byte[] bys = new byte[bufferSizeInBytes * 2];
            //因为arm字节序问题，所以需要高低位交换
            for (int i = 0; i < bufferSizeInBytes; i++) {
                byte[] ss = getBytes(bytes[i]);
                bys[i * 2] = ss[0];
                bys[i * 2 + 1] = ss[1];
            }

            if (AudioRecord.ERROR_INVALID_OPERATION != ret) {
                if (fileOutputStream != null) {
                    try {
                        //写录音数据
                        fileOutputStream.write(bys);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            handler.postDelayed(readWriteRunnable, 50);
        }
    };

    public byte[] getBytes(short s) {
        byte[] buf = new byte[2];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) (s & 0x00ff);
            s >>= 8;
        }
        return buf;
    }

    private double calculateVolume(short[] buffer) {
        double sumVolume = 0.0;
        double avgVolume = 0.0;
        double volume = 0.0;
        for (short b : buffer) {
            sumVolume += Math.abs(b);
        }
        avgVolume = sumVolume / buffer.length;
        volume = Math.log10(avgVolume + 1) * 10;
        return volume;
    }

    public void pauseRecord() {
        if (status == Status.STATUS_START) {
            audioRecord.startRecording();
            status = Status.STATUS_PAUSE;
        } else {
            if (listener != null) {
                listener.error("没有在录音");
            }
        }
    }

    private String genDialogParams() {
        pcmFileName = StorageUtils.getRecordDirectory().getAbsolutePath() + "/test.pcm";
        String params = "";
        try {
            JSONObject dialog_param = new JSONObject();
            dialog_param.put("file_path", wavFileName);
            JSONObject nls_config = new JSONObject();
            nls_config.put("format", "wav");
            dialog_param.put("nls_config", nls_config);
            params = dialog_param.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("dialog params: " + params);
        return params;
    }

    public void stopRecord() {
        handler.removeCallbacksAndMessages(null);
        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY) {
            if (listener != null) {
                listener.error("录音尚未开始");
            }
        }
        if (status != Status.STATUS_STOP) {
            status = Status.STATUS_STOP;
            wavFileName = StorageUtils.getRecordDirectory()
                    .getAbsolutePath()
                    + "/"
                    + System.currentTimeMillis()
                    + ".wav";

            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                    fileOutputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (listener != null) {
                GlobalExecutor.execute(() -> {
                    boolean isChangeSuccess = PcmToWav.makePCMFileToWAVFile(
                            pcmFileName,
                            wavFileName,
                            false
                    );
                    AsyncUtils.runOnUIThread(() -> {
                        if (isChangeSuccess) {
                            if (listener != null) {
                                listener.onLoadingStart("正在识别");
                            }
                            NativeNui.GetInstance().startFileTranscriber(genDialogParams(), new byte[32]);
                        } else {
                            if (listener != null) {
                                listener.error("pcm文件转换错误");
                            }
                        }
                    });
                }, "makePCMFileToWAVFile", GlobalExecutor.PRIORITY_USER);
            }
        }
    }

    @Override
    public void onFileTransEventCallback(Constants.NuiEvent nuiEvent, int resultCode, int finish, AsrResult asrResult, String taskId) {


        if (nuiEvent == Constants.NuiEvent.EVENT_ASR_ERROR) {
            if (listener != null) {
                listener.onLoadingEnd();

                long code = geiFailedCode(asrResult.asrResult);
                // 如果发的是空语音，会返回这个错误码，但是要当做成功处理，语音内容就用“”
                if (code == 40270002 || code == 40270004) {
                    AsyncUtils.runOnUIThread(() -> {
                        if (listener != null) {
                            listener.onLoadingEnd();
                            listener.result(wavFileName, getVoiceContent(asrResult.asrResult));
                        }
                    });
                } else {
                    listener.error("录制失败");
                }
            }


        } else if (nuiEvent == Constants.NuiEvent.EVENT_FILE_TRANS_RESULT) {
            listener.onLoadingEnd();
            AsyncUtils.runOnUIThread(() -> {
                if (listener != null) {
                    listener.onLoadingEnd();
                    listener.result(wavFileName, getVoiceContent(asrResult.asrResult));
                }
            });
        }
        LogUtils.d("nuiEvent:" + nuiEvent + " |  " + new Gson().toJson(asrResult));
    }

    public void destroy() {
        status = Status.STATUS_STOP;
        wavFileName = StorageUtils.getRecordDirectory().getAbsolutePath()
                + "/"
                + System.currentTimeMillis()
                + ".wav";
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
                fileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        NativeNui.GetInstance().release();
    }

    /**
     * 阿里初始化参数
     */
    private String genInitParams(String workpath, String debugpath) {
        String str = "";
        try {
            JSONObject object = new JSONObject();
            object.put("app_key", aliKey);
            object.put("token", aliToken);
            object.put("url", "https://nls-gateway.cn-shanghai.aliyuncs.com/stream/v1/FlashRecognizer");
            // object.put("url", "wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1");
            // object.put("save_wav", true);
            object.put("device_id", DeviceId.get());
            object.put("workspace", workpath);
            object.put("debug_path", debugpath);
            str = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    private String genParams() {
        String params = "";
        try {
            JSONObject nls_config = new JSONObject();
            nls_config.put("enable_intermediate_result", true);
            // 参数可根据实际业务进行配置
            // nls_config.put("enable_punctuation_prediction", true);
            // nls_config.put("enable_inverse_text_normalization", true);
            // nls_config.put("max_sentence_silence", 800);
            // nls_config.put("enable_words", false);
            // nls_config.put("sample_rate", 16000);
            // nls_config.put("sr_format", "opus");
            JSONObject tmp = new JSONObject();
            tmp.put("nls_config", nls_config);
            tmp.put("service_type", Constants.kServiceTypeSpeechTranscriber);
            // 如果有HttpDns则可进行设置
            // tmp.put("direct_ip", Utils.getDirectIp());
            params = tmp.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    private String getVoiceContent(String jsonString) {
        AliAudioTransfer aliAudioTransfer = new Gson().fromJson(jsonString, AliAudioTransfer.class);
        if (aliAudioTransfer == null
                || aliAudioTransfer.flashResult == null
                || aliAudioTransfer.flashResult.sentences == null) {
            return "";
        }

        // 服务端的列表数据，按照时间，做一遍排序
        int length = aliAudioTransfer.flashResult.sentences.size();
        for (int i = 0; i < length - 1; i++) {
            for (int k = i; k < length - 1; k++) {
                AliAudioTransferFlashResultSentence tempI = aliAudioTransfer.flashResult.sentences.get(i);
                AliAudioTransferFlashResultSentence tempK = aliAudioTransfer.flashResult.sentences.get(k);
                AliAudioTransferFlashResultSentence temp = new AliAudioTransferFlashResultSentence();
                if (tempK.endTime <= tempI.beginTime) {
                    copyData(tempI, temp);
                    copyData(tempK, tempI);
                    copyData(temp, tempK);
                }
            }
        }

        StringBuilder result = new StringBuilder();
        long beginTime = -1;
        long endTime = -1;

        for (AliAudioTransferFlashResultSentence temp : aliAudioTransfer.flashResult.sentences) {
            if (temp == null) {
                continue;
            }
            // 排序后的数据，按照顺序拼接
            // 例如(A,B),(B,C),(B,D)
            // 只拼接(A,B),(B,C)
            if (temp.beginTime >= endTime) {
                endTime = temp.endTime;
                result.append(temp.text);
            }
        }
        return result.toString();
    }

    private void copyData(
            AliAudioTransferFlashResultSentence target,
            AliAudioTransferFlashResultSentence contain
    ) {
        contain.text = target.text;
        contain.beginTime = target.beginTime;
        contain.endTime = target.endTime;
        contain.channelId = target.channelId;
    }

    private long geiFailedCode(String jsonString) {
        AliAudioTransfer aliAudioTransfer = new Gson().fromJson(jsonString, AliAudioTransfer.class);
        if (aliAudioTransfer == null) {
            return -1;
        }
        return aliAudioTransfer.status;
    }

    /**
     * 录音对象的状态
     */
    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP
    }

    private int createDir(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            return 1;
        }

        if (!dirPath.endsWith(File.separator)) {//不是以 路径分隔符 "/" 结束，则添加路径分隔符 "/"
            dirPath = dirPath + File.separator;
        }
        //创建文件夹
        if (dir.mkdirs()) {
            return 0;
        }
        return -1;
    }

}
