package com.leiyx.microchat.monolith.friend.exception;

/**
 * @author Cast
 */
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
    public UserNotFoundException(){
        super("User not found");
    }
}
