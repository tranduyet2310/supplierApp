package com.example.suppileragrimart.model;

public class AESResponse {
    private String rsaPublicKey;
    private String aesKey;
    private String iv;
    private String rsaPublicKeyServer;

    public AESResponse() {
    }

    public String getRsaPublicKey() {
        return rsaPublicKey;
    }

    public void setRsaPublicKey(String rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getRsaPublicKeyServer() {
        return rsaPublicKeyServer;
    }

    public void setRsaPublicKeyServer(String rsaPublicKeyServer) {
        this.rsaPublicKeyServer = rsaPublicKeyServer;
    }
}
