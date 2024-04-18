package com.example.suppileragrimart.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Warehouse implements Parcelable {
    private Long id;
    private String warehouseName;
    private String contact;
    private String phone;
    private String email;
    private String province;
    private String district;
    private String commune;
    private String detail;
    private String supplierContactName;
    @SerializedName("active")
    private boolean isActive;

    public Warehouse() {
    }

    protected Warehouse(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        warehouseName = in.readString();
        contact = in.readString();
        phone = in.readString();
        email = in.readString();
        province = in.readString();
        district = in.readString();
        commune = in.readString();
        detail = in.readString();
        supplierContactName = in.readString();
        isActive = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(warehouseName);
        dest.writeString(contact);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(province);
        dest.writeString(district);
        dest.writeString(commune);
        dest.writeString(detail);
        dest.writeString(supplierContactName);
        dest.writeByte((byte) (isActive ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Warehouse> CREATOR = new Creator<Warehouse>() {
        @Override
        public Warehouse createFromParcel(Parcel in) {
            return new Warehouse(in);
        }

        @Override
        public Warehouse[] newArray(int size) {
            return new Warehouse[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getSupplierContactName() {
        return supplierContactName;
    }

    public void setSupplierContactName(String supplierContactName) {
        this.supplierContactName = supplierContactName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
