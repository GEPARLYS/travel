package com.tourismwebsite.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author j
 * @Date 2019/12/12 15:25
 * @Version 1.0
 */
public class DateUtil {
    public static String getCurrentDate(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}
