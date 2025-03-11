package com.example.exception;

/**
 * Custom exception to let user know that a duplicate username was
 * found in the username and that theirs is invalid. 
 */

public class DuplicateUsernameException extends RuntimeException {
    
    public DuplicateUsernameException(String message){
        super(message);
    }
}
