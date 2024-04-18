package com.example.suppileragrimart.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.suppileragrimart.utils.CropsStatus;
import com.google.gson.annotations.SerializedName;

public class FieldDetail implements Parcelable {
    @SerializedName("id")
    private long id;
    @SerializedName("cropsStatus")
    private CropsStatus cropsStatus;
    @SerializedName("dateCreated")
    private String dateCreated;
    @SerializedName("details")
    private String details;

    public FieldDetail() {
    }

    protected FieldDetail(Parcel in) {
        id = in.readLong();
        dateCreated = in.readString();
        details = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(dateCreated);
        dest.writeString(details);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FieldDetail> CREATOR = new Creator<FieldDetail>() {
        @Override
        public FieldDetail createFromParcel(Parcel in) {
            return new FieldDetail(in);
        }

        @Override
        public FieldDetail[] newArray(int size) {
            return new FieldDetail[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CropsStatus getCropsStatus() {
        return cropsStatus;
    }

    public void setCropsStatus(CropsStatus cropsStatus) {
        this.cropsStatus = cropsStatus;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
