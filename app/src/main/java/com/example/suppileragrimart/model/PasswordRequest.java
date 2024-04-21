package com.example.suppileragrimart.model;

public class PasswordRequest {
    private String currentPass;
    private String newPass;

    public PasswordRequest() {
    }

    public PasswordRequest(String currentPass, String newPass) {
        this.currentPass = currentPass;
        this.newPass = newPass;
    }

    public String getCurrentPass() {
        return currentPass;
    }

    public void setCurrentPass(String currentPass) {
        this.currentPass = currentPass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }
}
