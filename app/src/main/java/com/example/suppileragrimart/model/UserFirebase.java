package com.example.suppileragrimart.model;

public class UserFirebase {
    private String uid;
    private String name;
    private String profileImage;
    private String phoneNumber;
    private String search;
    private String status;
    private Long idInServer;

    public UserFirebase() {
    }

    public UserFirebase(String uid, String name, String profileImage, String phoneNumber) {
        this.uid = uid;
        this.name = name;
        this.profileImage = profileImage;
        this.phoneNumber = phoneNumber;
    }

    public Long getIdInServer() {
        return idInServer;
    }

    public void setIdInServer(Long idInServer) {
        this.idInServer = idInServer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
