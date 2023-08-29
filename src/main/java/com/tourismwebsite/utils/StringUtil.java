package com.tourismwebsite.utils;

/**
 * @Author j
 * @Date 2019/12/11 17:06
 * @Version 1.0
 */
public class StringUtil {
    public static boolean isEmpty(String str){
        if (str == null || "".equals(str) || "null".equals(str)){
            return true;
        }else{
            return false;
        }

    }
}
