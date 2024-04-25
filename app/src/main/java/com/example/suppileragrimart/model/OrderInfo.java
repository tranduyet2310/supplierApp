package com.example.suppileragrimart.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.suppileragrimart.utils.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderInfo implements Parcelable {
    private long id;
    private String dateCreated;
    private OrderStatus orderStatus;
    private long addressId;
    private long userId;
    private String orderNumber;
    private List<OrderProductDto> productList;

    public OrderInfo() {
    }

    protected OrderInfo(Parcel in) {
        id = in.readLong();
        dateCreated = in.readString();
        addressId = in.readLong();
        userId = in.readLong();
        orderNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(dateCreated);
        dest.writeLong(addressId);
        dest.writeLong(userId);
        dest.writeString(orderNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel in) {
            return new OrderInfo(in);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<OrderProductDto> getProductList() {
        return productList;
    }

    public void setProductList(List<OrderProductDto> productList) {
        this.productList = productList;
    }
}
