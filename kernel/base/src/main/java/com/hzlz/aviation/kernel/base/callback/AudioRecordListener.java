package com.hzlz.aviation.kernel.base.callback;

public interface AudioRecordListener {
        /**
         * 音量大小
         */
        void volumeSize(double volume);

        /**
         * 录音结果
         *
         * @param filePath   .wav格式录音文件地址
         * @param contentTxt 语音文字
         */
        void result(String filePath, String contentTxt);

        /**
         * 错误信息
         */
        void error(String errorMessage);

        /**
         * 录音停止
         */
        void onStop();

        /**
         * 某些环节正在加载
         *
         * @param loadingMessage 显示加载文本
         */
        void onLoadingStart(String loadingMessage);

        /**
         * 某些环节结束加载
         */
        void onLoadingEnd();

    }
