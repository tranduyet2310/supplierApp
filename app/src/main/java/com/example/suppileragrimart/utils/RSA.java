package com.example.suppileragrimart.utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import android.util.Base64;

import javax.crypto.Cipher;

public class RSA {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSA() {
    }

    public void init() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair keyPair = generator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String encrypt(String message) throws Exception {
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

    public String decrypt(String encryptedMessage) throws Exception {
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage, StandardCharsets.UTF_8);
    }

    public String encryptWithDestinationKey(String rsaPublicKey, String message) throws Exception {
        byte[] publicKeyInBytes = decode(rsaPublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyInBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey resultRSAKey = keyFactory.generatePublic(keySpec);

        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, resultRSAKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

    public PrivateKey getOriginalPrivateKey(String privateKeyValue) throws Exception {
        byte[] privateKeyBytes = decode(privateKeyValue);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    public PublicKey getOriginalPublicKey(String publicKeyValue) throws Exception {
        byte[] publicKeyBytes = decode(publicKeyValue);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    public String getPublicKey() {
        return encode(publicKey.getEncoded());
    }

    public String getPrivateKey() {
        return encode(privateKey.getEncoded());
    }

    private String encode(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    private byte[] decode(String data) {
        return Base64.decode(data, Base64.NO_WRAP);
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
