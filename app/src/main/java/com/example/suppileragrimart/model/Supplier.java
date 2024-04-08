package com.example.suppileragrimart.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Supplier implements Parcelable {
    private long id;
    private String contactName;
    private String shopName;
    private String email;
    private String phone;
    private String cccd;
    private String tax_number;
    private String address;
    private String province;
    private String password;
    private String sellerType;
    private String bankAccountNumber;
    private String bankAccountOwner;
    private String bankName;
    private String bankBranchName;
    private String avatar;
    private String rsaPublicKey;
    private String aesKey;
    private String iv;

    public Supplier() {
    }

    public Supplier(long id, String contactName, String email, String phone, String password, String avatar) {
        this.id = id;
        this.contactName = contactName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.avatar = avatar;
    }

    protected Supplier(Parcel in) {
        id = in.readLong();
        contactName = in.readString();
        shopName = in.readString();
        email = in.readString();
        phone = in.readString();
        cccd = in.readString();
        tax_number = in.readString();
        address = in.readString();
        province = in.readString();
        password = in.readString();
        sellerType = in.readString();
        bankAccountNumber = in.readString();
        bankAccountOwner = in.readString();
        bankName = in.readString();
        bankBranchName = in.readString();
        avatar = in.readString();
        rsaPublicKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(contactName);
        dest.writeString(shopName);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(cccd);
        dest.writeString(tax_number);
        dest.writeString(address);
        dest.writeString(province);
        dest.writeString(password);
        dest.writeString(sellerType);
        dest.writeString(bankAccountNumber);
        dest.writeString(bankAccountOwner);
        dest.writeString(bankName);
        dest.writeString(bankBranchName);
        dest.writeString(avatar);
        dest.writeString(rsaPublicKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Supplier> CREATOR = new Creator<Supplier>() {
        @Override
        public Supplier createFromParcel(Parcel in) {
            return new Supplier(in);
        }

        @Override
        public Supplier[] newArray(int size) {
            return new Supplier[size];
        }
    };

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

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getTax_number() {
        return tax_number;
    }

    public void setTax_number(String tax_number) {
        this.tax_number = tax_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSellerType() {
        return sellerType;
    }

    public void setSellerType(String sellerType) {
        this.sellerType = sellerType;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountOwner() {
        return bankAccountOwner;
    }

    public void setBankAccountOwner(String bankAccountOwner) {
        this.bankAccountOwner = bankAccountOwner;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
}
