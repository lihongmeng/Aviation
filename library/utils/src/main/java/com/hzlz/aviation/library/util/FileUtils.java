package com.hzlz.aviation.library.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * 文件工具类
 */
public class FileUtils {

    /**
     * 本地文件buffer size
     */
    private static final int FILE_STREAM_BUFFER_SIZE = 4096;

    /**
     * 拷贝文件
     *
     * @param src 源文件
     * @param dst 目标文件
     * @return 拷贝的字节数
     */
    public static long copyFile(File src, File dst) {
        if (null == src || null == dst) {
            return 0;
        }

        if (!src.exists()) {
            return 0;
        }

        long size = 0;

        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(src);
            os = new FileOutputStream(dst);
            size = copyStream(is, os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSafely(is);
            closeSafely(os);
        }

        return size;
    }

    /**
     * 从输入流中读取字节写入输出流
     *
     * @param is 输入流
     * @param os 输出流
     * @return 复制大字节数
     */
    public static long copyStream(InputStream is, OutputStream os) {
        if (null == is || null == os) {
            return 0;
        }

        try {
            final int defaultBufferSize = 1024 * 3;
            byte[] buf = new byte[defaultBufferSize];
            long size = 0;
            int len = 0;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
                size += len;
            }
            os.flush();
            return size;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 尝试读取文件，若失败返回""
     *
     * @param file 尝试读取的文件
     * @return 尝试读取内容
     */
    public static String readFile(File file) {
        try {
            return readInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 尝试读取文件，若失败返回""；读取完毕会关闭流
     *
     * @param inputStream 文件流
     * @return 尝试读取内容
     */
    public static String readInputStream(InputStream inputStream) {
        if (inputStream == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String str = "";
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 计算文件md5值
     *
     * @param file      文件
     * @param upperCase true：大写；false，小写字符串
     * @return 文件md5值
     */
    public static String toMd5(File file, boolean upperCase) {
        InputStream is = null;
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            is = new FileInputStream(file);
            byte[] buffer = new byte[FILE_STREAM_BUFFER_SIZE];
            int read = 0;
            while ((read = is.read(buffer)) > 0) {
                algorithm.update(buffer, 0, read);
            }
            return toHex(algorithm.digest(), "", upperCase);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSafely(is);
        }

        return null;
    }

    /**
     * 将byte转为十六进制
     *
     * @param bytes     byte数组
     * @param separator 分割
     * @param upperCase 是否大写
     * @return 十六进制string
     */
    public static String toHex(byte[] bytes, String separator, boolean upperCase) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(0xFF & b);
            if (upperCase) {
                str = str.toUpperCase();
            }
            if (str.length() == 1) {
                hexString.append("0");
            }
            hexString.append(str).append(separator);
        }
        return hexString.toString();
    }

    /**
     * 关闭closeable
     *
     * @param closeable 待关闭的closeable对象
     */
    private static void closeSafely(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建新文件
     *
     * @param saveFile 指定的新文件
     */
    public static boolean createNewFileSafely(File saveFile) {
        if (saveFile == null || saveFile.exists()) {
            return false;
        }
        File saveFileParent = saveFile.getParentFile();
        if (saveFileParent != null && !saveFileParent.exists()) {
            saveFileParent.mkdirs();
        }
        try {
            return saveFile.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }


    public static String getSize(final File file) {
        if (file == null) return "";
        if (file.isDirectory()) {
            return getDirSize(file);
        }
        return getFileSize(file);
    }

    private static String getDirSize(final File dir) {
        long len = getDirLength(dir);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    private static String getFileSize(final File file) {
        long len = getFileLength(file);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    private static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format(Locale.getDefault(), "%.2fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format(Locale.getDefault(), "%.2fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format(Locale.getDefault(), "%.2fMB", (double) byteNum / (1024 * 1024));
        } else {
            return String.format(Locale.getDefault(), "%.2fGB", (double) byteNum / (1024 * 1024 * 1024));
        }
    }

    public static long getLength(final String filePath) {
        return getLength(getFileByPath(filePath));
    }

    public static long getLength(final File file) {
        if (file == null) return 0;
        if (file.isDirectory()) {
            return getDirLength(file);
        }
        return getFileLength(file);
    }

    private static long getDirLength(final File dir) {
        if (!isDir(dir)) return -1;
        long len = 0;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    len += getDirLength(file);
                } else {
                    len += file.length();
                }
            }
        }
        return len;
    }

    public static long getFileLength(final String filePath) {
        return getFileLength(getFileByPath(filePath));
    }

    private static long getFileLength(final File file) {
        if (!isFile(file)) return -1;
        return file.length();
    }

    public static File getFileByPath(final String filePath) {
        return TextUtils.isEmpty(filePath) ? null : new File(filePath);
    }

    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }

    public static boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    public static boolean delete(final String filePath) {
        return delete(getFileByPath(filePath));
    }

    public static boolean delete(final File file) {
        if (file == null) return false;
        if (file.isDirectory()) {
            return deleteDir(file);
        }
        return deleteFile(file);
    }

    private static boolean deleteDir(final File dir) {
        if (dir == null) return false;
        // dir doesn't exist then return true
        if (!dir.exists()) return true;
        // dir isn't a directory then return false
        if (!dir.isDirectory()) return false;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return dir.delete();
    }

    private static boolean deleteFile(final File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    /**
     * 保存drawable到本地地址
     *
     * @param savePath 待保存的地址
     * @param drawable 待保存的图片
     * @return 保存的文件
     */
    @Nullable
    public static File saveDrawableFile(String savePath, Drawable drawable) {
        if (TextUtils.isEmpty(savePath) || drawable == null) {
            return null;
        }
        File saveFile = new File(savePath);
        if (saveFile.exists()) {
            delete(saveFile);
        }
        if (!createNewFileSafely(saveFile)) {
            return null;
        }
        try {
            FileOutputStream out = new FileOutputStream(saveFile);
            if (((BitmapDrawable) drawable).getBitmap()
                    .compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        return saveFile;

    }

    /**
     * 处理文件名，去掉后缀（.xxx)，需确认文件名类型才可使用本方法
     *
     * @param fileName 待处理的文件名
     * @return 处理完成的文件名
     */
    public static String getFileNameWithOutSuffix(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static boolean createDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dirPath.endsWith(File.separator)) {
            //不是以 路径分隔符 "/" 结束，则添加路径分隔符 "/"
            dirPath = dirPath + File.separator;
            dir = new File(dirPath);
        }
        if (dir.exists()) {
            return true;
        }
        //创建文件夹
        return dir.mkdirs();
    }
}
