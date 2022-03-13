package com.jxntv.live.liveroom.live.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.ioc.PluginManager;
import com.jxntv.live.dialog.MessageConfirmDialog;
import com.jxntv.utils.AppManager;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Module:   TCUtils
 * <p>
 * Function: 工具函数的集合类
 */
public class TCUtils {


    public static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) hex.append('0');
            hex.append(Integer.toHexString(i));
        }

        return hex.toString();
    }

    /**
     * @param username 用户名
     * @return 同上
     */
    public static boolean isUsernameVaild(String username) {
        return !username.matches("[0-9]+") && username.matches("^[a-zA-Z0-9_-]{4,24}$");
    }


    // 字符串截断
    public static String getLimitString(String source, int length) {
        if (null != source && source.length() > length) {
//            int reallen = 0;
            return source.substring(0, length) + "...";
        }
        return source;
    }

    // 字符串截断
    public static String getLimitStringWithoutNode(String source, int length) {
        if (null != source && source.length() > length) {
            return source.substring(0, length);
        }
        return source;
    }

    /**
     * 显示被踢下线通知
     *
     */
    public static void showKickOut(boolean isNeedFinish) {
//        因领导需求展示注释单设备登录功能
        Activity activity = AppManager.getAppManager().currentActivity();
        MessageConfirmDialog confirmDialog = new MessageConfirmDialog(activity);
        confirmDialog.setMessage("通知","您的账号已在其他地方登录，您被迫下线。");
        PluginManager.get(AccountPlugin.class).logout();
        confirmDialog.setSingleButton("确定", view -> {
            if (isNeedFinish && !activity.isFinishing()){
                activity.finish();
            }
        });
        confirmDialog.show();
    }

    /**
     * 根据比例转化实际数值为相对值
     *
     * @param gear 档位
     * @param max  最大值
     * @param curr 当前值
     * @return 相对值
     */
    public static int filtNumber(int gear, int max, int curr) {
        return curr / (max / gear);
    }



    /**
     * 滤镜定义
     */
    public static final int FILTERTYPE_NONE = 0;    //无特效滤镜

    /**
     * 绿幕定义
     *
     * @param index
     * @return
     */
    public static String getGreenFileName(int index) {
        String strGreenFileName;
        switch (index) {
            case 0:
                strGreenFileName = "";
                break;
            case 1:
                strGreenFileName = "green_1.mp4";
                break;
            case 2:
                strGreenFileName = "green_2.mp4";
                break;
            default:
                strGreenFileName = "";
                break;
        }
        return strGreenFileName;
    }

}
