package com.hzlz.aviation.feature.account.presistence;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.kernel.base.sp.SharedPrefsWrapper;
import com.hzlz.aviation.kernel.network.NetworkManager;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 用户管理类
 *
 * @since 2020-02-02 17:35
 */
public final class UserManager {
    //<editor-fold desc="常量">
    private static final String CURRENT_USER_JSON_FILE = "current_user.json";
    //
    private static final String SP_FILE_NAME_USER = "user";
    private static final String SP_KEY_TOKEN = "token";
    //</editor-fold>

    //<editor-fold desc="属性">
    @Nullable
    private static User sCurrentUser;
    @Nullable
    private static String sToken;
    @NonNull
    private static SharedPrefsWrapper sUserSp = new SharedPrefsWrapper(SP_FILE_NAME_USER);

    //</editor-fold>

    //<editor-fold desc="私有构造函数">
    private UserManager() {
        throw new IllegalStateException("no instance !!!");
    }
    //</editor-fold>

    //<editor-fold desc="API">

    /**
     * 获取当前用户
     */
    @NonNull
    public static User getCurrentUser() {
        if (sCurrentUser == null) {
            sCurrentUser = getUserFromLocal();
            if (sCurrentUser == null) {
                sCurrentUser = new User();
            }
        }
        return sCurrentUser;
    }

    /**
     * 删除当前
     */
    @WorkerThread
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteCurrentUser() {
        File file = new File(GVideoRuntime.getApplication().getFilesDir(), CURRENT_USER_JSON_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 保存用户
     *
     * @param user 用户
     */
    @WorkerThread
    @SuppressWarnings("CharsetObjectCanBeUsed")
    public static void saveUser(@NonNull User user) {
        String userJson = NetworkManager.getInstance().getGson().toJson(user, User.class);
        File file = new File(GVideoRuntime.getApplication().getFilesDir(), CURRENT_USER_JSON_FILE);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(userJson.getBytes("UTF-8"));
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存 Token
     *
     * @param token Token
     */
    public static void saveToken(@Nullable String token) {
        sToken = token;
        sUserSp.putString(SP_KEY_TOKEN, token);
    }

    /**
     * 获取 Token
     *
     * @return Token
     */
    @Nullable
    public static String getToken() {
        if (sToken == null) {
            sToken = sUserSp.getString(SP_KEY_TOKEN, null);
        }
        return sToken;
    }

    /**
     * 是否已经登录
     *
     * @return 是否已经登录
     */
    public static boolean hasLoggedIn() {
        return getToken() != null;
    }
    //</editor-fold>

    //<editor-fold desc="内部方法">
    @Nullable
    private static User getUserFromLocal() {
        File file = new File(GVideoRuntime.getApplication().getFilesDir(), CURRENT_USER_JSON_FILE);
        if (file.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(file);
                return NetworkManager.getInstance().getGson().fromJson(reader, User.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            } catch (JsonIOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    //</editor-fold>
}
