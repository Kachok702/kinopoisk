package org.example.VKR.util;

public class MovieErrorResponse {
    private final String message;
    private final long timestamp;

    public MovieErrorResponse(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
