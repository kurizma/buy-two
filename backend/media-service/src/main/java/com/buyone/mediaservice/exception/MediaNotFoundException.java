package com.buyone.mediaservice.exception;

public class MediaNotFoundException extends RuntimeException {
    public MediaNotFoundException() {
        super();
    }
    public MediaNotFoundException(String id) {
        super("Media not found: " + id);
    }
    public MediaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public MediaNotFoundException(Throwable cause) {
        super(cause);
    }
}
