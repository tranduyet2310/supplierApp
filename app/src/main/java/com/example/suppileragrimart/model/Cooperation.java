package com.example.suppileragrimart.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.suppileragrimart.utils.OrderStatus;
import com.google.gson.annotations.SerializedName;

public class Cooperation implements Parcelable {
    @SerializedName("id")
    private long id;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("description")
    private String description;
    @SerializedName("cropsName")
    private String cropsName;
    @SerializedName("requireYield")
    private double requireYield;
    @SerializedName("investment")
    private String investment;
    @SerializedName("contact")
    private String contact;
    @SerializedName("userId")
    private long userId;
    @SerializedName("supplierId")
    private long supplierId;
    @SerializedName("supplierShopName")
    private String shopName;
    @SerializedName("supplierPhone")
    private String supplierPhone;
    @SerializedName("fieldId")
    private long fieldId;
    @SerializedName("cooperationStatus")
    private OrderStatus cooperationStatus;
    @SerializedName("supplierContactName")
    private String supplierName;
    @SerializedName("addressId")
    private Long addressId;
    @SerializedName("paymentStatus")
    private String paymentStatus;
    public Cooperation() {
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    protected Cooperation(Parcel in) {
        id = in.readLong();
        fullName = in.readString();
        description = in.readString();
        cropsName = in.readString();
        requireYield = in.readDouble();
        investment = in.readString();
        contact = in.readString();
        userId = in.readLong();
        supplierId = in.readLong();
        shopName = in.readString();
        supplierPhone = in.readString();
        fieldId = in.readLong();
        supplierName = in.readString();
        if (in.readByte() == 0) {
            addressId = null;
        } else {
            addressId = in.readLong();
        }
        paymentStatus = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(fullName);
        dest.writeString(description);
        dest.writeString(cropsName);
        dest.writeDouble(requireYield);
        dest.writeString(investment);
        dest.writeString(contact);
        dest.writeLong(userId);
        dest.writeLong(supplierId);
        dest.writeString(shopName);
        dest.writeString(supplierPhone);
        dest.writeLong(fieldId);
        dest.writeString(supplierName);
        if (addressId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(addressId);
        }
        dest.writeString(paymentStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Cooperation> CREATOR = new Creator<Cooperation>() {
        @Override
        public Cooperation createFromParcel(Parcel in) {
            return new Cooperation(in);
        }

        @Override
        public Cooperation[] newArray(int size) {
            return new Cooperation[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCropsName() {
        return cropsName;
    }

    public void setCropsName(String cropsName) {
        this.cropsName = cropsName;
    }

    public double getRequireYield() {
        return requireYield;
    }

    public void setRequireYield(double requireYield) {
        this.requireYield = requireYield;
    }

    public String getInvestment() {
        return investment;
    }

    public void setInvestment(String investment) {
        this.investment = investment;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public long getFieldId() {
        return fieldId;
    }

    public void setFieldId(long fieldId) {
        this.fieldId = fieldId;
    }

    public OrderStatus getCooperationStatus() {
        return cooperationStatus;
    }

    public void setCooperationStatus(OrderStatus cooperationStatus) {
        this.cooperationStatus = cooperationStatus;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

}

