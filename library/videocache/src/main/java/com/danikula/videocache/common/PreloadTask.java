package com.danikula.videocache.common;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public class PreloadTask implements Runnable {

    /**
     * 原始地址
     */
    public String mRawUrl;

    /**
     * 列表中的位置
     */
    public int mPosition;

    /**
     * VideoCache服务器
     */
    public HttpProxyCacheServer mCacheServer;

    /**
     * 是否被取消
     */
    private boolean mIsCanceled;

    /**
     * 是否正在预加载
     */
    private boolean mIsExecuted;

    @Override
    public void run() {
        if (!mIsCanceled) {
            start();
        }
        mIsExecuted = false;
        mIsCanceled = false;
    }

    /**
     * 开始预加载
     */
    private void start() {
        HttpURLConnection connection = null;
        try {
            //获取HttpProxyCacheServer的代理地址
            String proxyUrl = mCacheServer.getProxyUrl(mRawUrl);
            URL url = new URL(proxyUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5_000);
            connection.setReadTimeout(5_000);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Logger.debug("连接本地代理服务成功");
                InputStream in = new BufferedInputStream(connection.getInputStream());
                int length;
                int read = -1;
                byte[] bytes = new byte[8 * 1024];
                while ((length = in.read(bytes)) != -1) {
                    read += length;
                    //预加载完成或者取消预加载
                    if (mIsCanceled || read >= PreloadManager.PRELOAD_LENGTH) {
                        Logger.debug("结束预加载：" + mPosition + "，" + mRawUrl);
                        break;
                    }
                }
                if (read == -1) {
                    //这种情况一般是预加载出错了，删掉缓存
                    Logger.debug("预加载失败：" + mPosition + "," + mRawUrl);
                    File cacheFile = mCacheServer.getCacheFile(mRawUrl);
                    if (cacheFile.exists()) {
                        cacheFile.delete();
                    }
                }
            } else {
                Logger.debug("连接代理服务失败：" + connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.debug("异常结束预加载："+e.getMessage()+","+e.getCause().getMessage()+","+mRawUrl);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 将预加载任务提交到线程池，准备执行
     */
    public void executeOn(ExecutorService executorService) {
        if (mIsExecuted) return;
        mIsExecuted = true;
        executorService.submit(this);
    }

    /**
     * 取消预加载任务
     */
    public void cancel() {
        if (mIsExecuted) {
            mIsCanceled = true;
        }
    }
}
