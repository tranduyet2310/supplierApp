package com.example.suppileragrimart.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

public class ReviewInfo implements Parcelable {
    private Long id;
    private String feedBack;
    private BigDecimal rating;
    private String reviewDate;
    private String userFullName;
    private String productName;
    private Long productId;

    public ReviewInfo() {
    }

    protected ReviewInfo(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        feedBack = in.readString();
        reviewDate = in.readString();
        userFullName = in.readString();
        productName = in.readString();
        if (in.readByte() == 0) {
            productId = null;
        } else {
            productId = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(feedBack);
        dest.writeString(reviewDate);
        dest.writeString(userFullName);
        dest.writeString(productName);
        if (productId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(productId);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReviewInfo> CREATOR = new Creator<ReviewInfo>() {
        @Override
        public ReviewInfo createFromParcel(Parcel in) {
            return new ReviewInfo(in);
        }

        @Override
        public ReviewInfo[] newArray(int size) {
            return new ReviewInfo[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
