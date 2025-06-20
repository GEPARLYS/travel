package com.tourismwebsite.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author J
 * @Date 2025/6/16  17:34
 * @Version 1.0
 */
public class CalculateUtil {
    public static int getExpireSeconds(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1).toLocalDate().atStartOfDay();
        Duration duration = Duration.between(now, tomorrow);
        return (int) duration.getSeconds();
    }
    
}
