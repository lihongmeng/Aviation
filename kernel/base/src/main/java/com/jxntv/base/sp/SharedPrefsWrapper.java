package com.jxntv.base.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.jxntv.runtime.GVideoRuntime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * sp包装类，便于使用，业务方可根据需要继承
 */
public class SharedPrefsWrapper implements SharedPreferences {

    /** 默认sp名 */
    private static String DEFAULT_NAME = "default";
    /** 持有的sp */
    private SharedPreferences mSp;

    /**
     * 构造函数
     */
    public SharedPrefsWrapper(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            fileName = DEFAULT_NAME;
        }
        mSp = GVideoRuntime.getAppContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    @Override
    public Map<String, ?> getAll() {
        return mSp.getAll();
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return mSp.getString(key, defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return mSp.getStringSet(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return mSp.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return mSp.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return mSp.contains(key);
    }

    @Override
    public Editor edit() {
        return mSp.edit();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mSp.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mSp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * 扩展能力 -- 设置string
     */
    public void putString(String key, String value) {
        mSp.edit().putString(key, value).apply();
    }

    /**
     * 扩展能力 -- 设置string set
     */
    public void putStringSet(String key, Set<String> values) {
        mSp.edit().putStringSet(key, values).apply();
    }

    /**
     * 扩展能力 -- 设置int
     */
    public void putInt(String key, int value) {
        mSp.edit().putInt(key, value).apply();
    }

    /**
     * 扩展能力 -- 设置long
     */
    public void putLong(String key, long value) {
        mSp.edit().putLong(key, value).apply();
    }

    /**
     * 扩展能力 -- 设置float
     */
    public void putFloat(String key, float value) {
        mSp.edit().putFloat(key, value).apply();
    }

    /**
     * 扩展能力 -- 设置boolean
     */
    public void putBoolean(String key, boolean value) {
        mSp.edit().putBoolean(key, value).apply();
    }

    /**
     * 扩展能力 -- 移出资源
     */
    public void remove(String key) {
        mSp.edit().remove(key).apply();
    }
}
