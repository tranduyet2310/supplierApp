package com.example.suppileragrimart.model;

import com.google.gson.annotations.SerializedName;

public class MessageResponse {
    @SerializedName("successful")
    private boolean isSuccessful;
    private String message;

    public MessageResponse() {
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
