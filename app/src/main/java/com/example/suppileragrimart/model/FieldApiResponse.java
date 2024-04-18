package com.example.suppileragrimart.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FieldApiResponse implements Parcelable {
    @SerializedName("id")
    private long id;
    @SerializedName("season")
    private String season;
    @SerializedName("cropsName")
    private String cropsName;
    @SerializedName("cropsType")
    private String cropsType;
    @SerializedName("area")
    private String area;
    @SerializedName("estimateYield")
    private Double estimateYield;
    @SerializedName("fieldDetails")
    private ArrayList<FieldDetail> fieldDetails;
    @SerializedName("estimatePrice")
    private long estimatePrice;
    @SerializedName("isComplete")
    private boolean isComplete;

    public FieldApiResponse() {
    }

    protected FieldApiResponse(Parcel in) {
        id = in.readLong();
        season = in.readString();
        cropsName = in.readString();
        cropsType = in.readString();
        area = in.readString();
        if (in.readByte() == 0) {
            estimateYield = null;
        } else {
            estimateYield = in.readDouble();
        }
        estimatePrice = in.readLong();
        isComplete = in.readByte() != 0;
    }

    public static final Creator<FieldApiResponse> CREATOR = new Creator<FieldApiResponse>() {
        @Override
        public FieldApiResponse createFromParcel(Parcel in) {
            return new FieldApiResponse(in);
        }

        @Override
        public FieldApiResponse[] newArray(int size) {
            return new FieldApiResponse[size];
        }
    };

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public long getEstimatePrice() {
        return estimatePrice;
    }

    public void setEstimatePrice(long estimatePrice) {
        this.estimatePrice = estimatePrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getCropsName() {
        return cropsName;
    }

    public void setCropsName(String cropsName) {
        this.cropsName = cropsName;
    }

    public String getCropsType() {
        return cropsType;
    }

    public void setCropsType(String cropsType) {
        this.cropsType = cropsType;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Double getEstimateYield() {
        return estimateYield;
    }

    public void setEstimateYield(Double estimateYield) {
        this.estimateYield = estimateYield;
    }

    public ArrayList<FieldDetail> getFieldDetails() {
        return fieldDetails;
    }

    public void setFieldDetails(ArrayList<FieldDetail> fieldDetails) {
        this.fieldDetails = fieldDetails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(season);
        dest.writeString(cropsName);
        dest.writeString(cropsType);
        dest.writeString(area);
        if (estimateYield == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(estimateYield);
        }
        dest.writeLong(estimatePrice);
        dest.writeByte((byte) (isComplete ? 1 : 0));
    }
}
