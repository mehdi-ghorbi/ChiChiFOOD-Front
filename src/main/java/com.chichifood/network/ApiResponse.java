package com.chichifood.network;

public class ApiResponse {
    private int statusCode;
    private String body;
    private Object data;

    public ApiResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
        this.data = null;
    }

    public ApiResponse(int statusCode, Object data) {
        this.statusCode = statusCode;
        this.data = data;
        this.body = null;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public Object getData() {
        return data;
    }
}
