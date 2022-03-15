package com.hzlz.aviation.kernel.stat.stat.db;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * 数据库日期转换器
 */
public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
