package com.tourismwebsite.exception;

/**
 * @Author j
 * @Date 2023/8/25 13:25
 * @Version 1.0
 */
public class UserNameNotExistsException extends Exception {
    public UserNameNotExistsException(String message) {
        super(message);
    }
}
