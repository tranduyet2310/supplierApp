package com.example.suppileragrimart.model;

import com.google.gson.annotations.SerializedName;

public class UserAddress {
    @SerializedName("id")
    private long id;
    @SerializedName("contactName")
    private String contactName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("province")
    private String province;
    @SerializedName("district")
    private String district;
    @SerializedName("commune")
    private String commune;
    @SerializedName("details")
    private String details;
    @SerializedName("userFullName")
    private String userFullName;

    public UserAddress() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}
