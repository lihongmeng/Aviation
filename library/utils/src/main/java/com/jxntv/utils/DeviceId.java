package com.jxntv.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

/**
 * 设备 id 工具类<br/>
 * <ul> 存储的位置
 * <li>1. 内部存储</li>
 * <li>2. 外部存储（私有）</li>
 * <li>3. 外部存储（公有）</li>
 * </ul>
 *
 *
 * @since 2020-03-05 16:21
 */
public final class DeviceId {
  //<editor-fold desc="常量">
  private static final String DEVICE_ID_FILE_NAME = ".device_id.txt";
  //</editor-fold>

  //<editor-fold desc="属性">
  /*@Nullable*/
  private static volatile String sDeviceId;
  private static volatile boolean sHasInitialized;
  @SuppressLint("StaticFieldLeak")
  private static Context sContext;
  private static String sExternalStorageDirectoryName;
  //</editor-fold>

  //<editor-fold desc="私有构造函数">
  private DeviceId(Initialization initialization) {
    sContext = initialization.mContext;
    sExternalStorageDirectoryName = initialization.mExternalStorageDirectoryName;
  }
  //</editor-fold>

  //<editor-fold desc="初始化">
  public static final class Initialization {
    private Context mContext;
    private String mExternalStorageDirectoryName;

    private Initialization(Context context) {
      mContext = context;
    }

    public Initialization externalStorageDirectoryName(String name) {
      if (TextUtils.isEmpty(name)) {
        throw new NullPointerException("external storage directory name is empty");
      }
      if (name.contains("/")) {
        throw new IllegalArgumentException("external storage directory name can not contain '/' ");
      }
      mExternalStorageDirectoryName = name;
      return this;
    }

    public void init() {
      if (mExternalStorageDirectoryName == null) {
        throw new NullPointerException("external storage directory name is null for device id");
      }
      sHasInitialized = true;
      new DeviceId(this);
    }
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 初始化
   */
  public static Initialization init(Context context) {
    if (sHasInitialized) {
      throw new UnsupportedOperationException("DeviceId ");
    }
    return new Initialization(context);
  }

  /**
   * 获取 DeviceId
   *
   * @return DeviceId
   */
  public static String get() {
    if (sDeviceId == null) {
      synchronized (DeviceId.class) {
        if (sDeviceId == null) {
          create();
        }
      }
    }
    return sDeviceId;
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  public static void clear(){
    sDeviceId = null;
  }

  /**
   * 创建 deviceId
   */
  private static void create() {
    if (!sHasInitialized) {
      throw new IllegalStateException("call DeviceId#init to initialize !!!");
    }
    // 从外部手机江西台存储（公有）读取
    sDeviceId = readFromSJJXTExternalStorage();
    // 从外部存储（公有）读取
    if (sDeviceId == null) {
      sDeviceId = readFromExternalStorage();
    }
    // 从外部存储（私有）读取
    if (sDeviceId == null) {
      sDeviceId = readFromExternalPrivateStorage();
    }
    // 从内部存储读取
    if (sDeviceId == null) {
      sDeviceId = readFromInternalStorage();
    }
    // 创建 uuid
    if (sDeviceId == null) {
      createDeviceId();
    }
    // 写入内部存储
    writeToInternalStorage(sDeviceId);
    // 写入外部存储（公有）
    writeToExternalStorage(sDeviceId);
    // 写入外部存储（私有）
    writeToExternalPrivateStorage(sDeviceId);
  }

  private static void createDeviceId(){
    StringBuilder sbDeviceId = new StringBuilder();
    //获得AndroidId
    String androidid = getAndroidId(sContext);
    //获得设备序列号
    String serial = getSERIAL();
    //获得硬件uuid（根据硬件相关属性，生成uuid）
    String uuid = getDeviceUUID().replace("-", "");
    //追加androidid
    if (androidid != null && androidid.length() > 0) {
      sbDeviceId.append(androidid);
      sbDeviceId.append("|");
    }
    //追加serial
    if (serial != null && serial.length() > 0) {
      sbDeviceId.append(serial);
      sbDeviceId.append("|");
    }
    //追加硬件uuid
    if (uuid != null && uuid.length() > 0) {
      sbDeviceId.append(uuid);
    }
    //生成SHA1，统一DeviceId长度
    if (sbDeviceId.length() > 0) {
      try {
        byte[] hash = getHashByString(sbDeviceId.toString());
        String sha1 = bytesToHex(hash);
        if (sha1 != null && sha1.length() > 0) {
          //返回最终的DeviceId
          sDeviceId = sha1;
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    if (TextUtils.isEmpty(sDeviceId)){
      //如果以上硬件标识数据均无法获得，
      //则DeviceId默认使用系统随机数，这样保证DeviceId不为空
      sDeviceId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

  }

  /**
   * 读取文件
   *
   * @param file 文件
   * @return 文件内容
   */
  private static String readFile(File file) {
    try {
      if (file.exists()) {
        FileReader fr = null;
        BufferedReader br = null;
        try {
          fr = new FileReader(file);
          br = new BufferedReader(fr);
          StringBuilder builder = new StringBuilder();
          String line;
          while ((line = br.readLine()) != null) {
            builder.append(line);
          }
          return builder.toString();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
          return null;
        } catch (IOException e) {
          e.printStackTrace();
          return null;
        } finally {
          if (br != null) {
            try {
              br.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          if (fr != null) {
            try {
              fr.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 写文件
   *
   * @param file 文件
   * @param content 内容
   */
  @SuppressWarnings("CharsetObjectCanBeUsed")
  private static void writeFile(File file, String content) {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      fos.write(content.getBytes("UTF-8"));
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (fos != null) {
        try {
          fos.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static String readFromInternalStorage() {
    return readFile(new File(sContext.getFilesDir(), DEVICE_ID_FILE_NAME));
  }

  private static void writeToInternalStorage(String deviceId) {
    writeFile(new File(sContext.getFilesDir(), DEVICE_ID_FILE_NAME), deviceId);
  }

  private static String readFromExternalPrivateStorage() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        || hasWriteExternalStoragePermission()) {
      return readFile(new File(sContext.getExternalFilesDir(null), DEVICE_ID_FILE_NAME));
    }
    return null;
  }

  private static void writeToExternalPrivateStorage(String deviceId) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        || hasWriteExternalStoragePermission()) {
      writeFile(new File(sContext.getExternalFilesDir(null), DEVICE_ID_FILE_NAME), deviceId);
    }
  }

  private static String readFromExternalStorage() {
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      if (!hasWriteExternalStoragePermission()) {
        return null;
      }
      File directory = new File("/storage/emulated/0/" + sExternalStorageDirectoryName);
      try {
        if (directory.exists()) {
          return readFile(new File(directory, DEVICE_ID_FILE_NAME));
        }
      } catch (SecurityException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * 读取手机江西台设备id文件
   */
  private static String readFromSJJXTExternalStorage() {
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      if (!hasWriteExternalStoragePermission()) {
        return null;
      }
      File directory = new File("/storage/emulated/0/" + "comsobeycloudviewjiangxi");
      try {
        if (directory.exists()) {
          return readFile(new File(directory, "deviceId"));
        }
      } catch (SecurityException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private static void writeToExternalStorage(String deviceId) {
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      if (!hasWriteExternalStoragePermission()) {
        return;
      }
      File directory = new File("/storage/emulated/0/" + sExternalStorageDirectoryName);
      try {
        if (!directory.exists()) {
          directory.mkdir();
        }
        File file = new File(directory, DEVICE_ID_FILE_NAME);
        writeFile(file, deviceId);
      } catch (SecurityException e) {
        e.printStackTrace();
      }
    }
  }

  private static boolean hasWriteExternalStoragePermission() {
    return PackageManager.PERMISSION_GRANTED == sContext.checkPermission(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.os.Process.myPid(), Process
            .myUid()
    );
  }




  /**
   * 获得设备的AndroidId
   *
   * @param context 上下文
   * @return 设备的AndroidId
   */
  private static String getAndroidId(Context context) {
    try {
      return Settings.Secure.getString(context.getContentResolver(),
              Settings.Secure.ANDROID_ID);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "";
  }

  /**
   * 获得设备序列号（如：WTK7N16923005607）, 个别设备无法获取
   *
   * @return 设备序列号
   */
  private static String getSERIAL() {
    try {
      return Build.SERIAL;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "";
  }

  /**
   * 获得设备硬件uuid
   * 使用硬件信息，计算出一个随机数
   *
   * @return 设备硬件uuid
   */
  private static String getDeviceUUID() {
    try {
      String dev = "4519741" +
              Build.BOARD.length() % 10 +
              Build.BRAND.length() % 10 +
              Build.DEVICE.length() % 10 +
              Build.HARDWARE.length() % 10 +
              Build.ID.length() % 10 +
              Build.MODEL.length() % 10 +
              Build.PRODUCT.length() % 10 +
              Build.SERIAL.length() % 10;
      return new UUID(dev.hashCode(),
              Build.SERIAL.hashCode()).toString();
    } catch (Exception ex) {
      ex.printStackTrace();
      return "";
    }
  }

  /**
   * 取SHA1
   * @param data 数据
   * @return 对应的hash值
   */
  private static byte[] getHashByString(String data)
  {
    try{
      MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
      messageDigest.reset();
      messageDigest.update(data.getBytes("UTF-8"));
      return messageDigest.digest();
    } catch (Exception e){
      return "".getBytes();
    }
  }

  /**
   * 转16进制字符串
   * @param data 数据
   * @return 16进制字符串
   */
  private static String bytesToHex(byte[] data){
    StringBuilder sb = new StringBuilder();
    String stmp;
    for (int n = 0; n < data.length; n++){
      stmp = (Integer.toHexString(data[n] & 0xFF));
      if (stmp.length() == 1)
        sb.append("0");
      sb.append(stmp);
    }
    return sb.toString().toUpperCase(Locale.CHINA);
  }

  //</editor-fold>
}
