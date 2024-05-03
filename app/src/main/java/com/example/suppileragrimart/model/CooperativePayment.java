package com.example.suppileragrimart.model;

import com.example.suppileragrimart.utils.OrderStatus;
import com.google.gson.annotations.SerializedName;

public class CooperativePayment {
    @SerializedName("id")
    private Long id;
    @SerializedName("orderNumber")
    private String orderNumber;
    @SerializedName("orderStatus")
    private OrderStatus orderStatus;
    @SerializedName("paymentMethod")
    private String paymentMethod;
    @SerializedName("paymentStatus")
    private String paymentStatus;
    @SerializedName("total")
    private Long total;
    @SerializedName("userId")
    private Long userId;
    @SerializedName("supplierId")
    private Long supplierId;
    @SerializedName("cooperationId")
    private Long cooperationId;
    @SerializedName("dateCreated")
    private String dateCreated;
    @SerializedName("dateUpdated")
    private String dateUpdated;
    @SerializedName("supplierContactName")
    private String supplierContactName;
    @SerializedName("cropsName")
    private String cropsName;
    @SerializedName("requireYield")
    private Double requireYield;
    @SerializedName("userFullName")
    private String userFullName;

    public CooperativePayment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getCooperationId() {
        return cooperationId;
    }

    public void setCooperationId(Long cooperationId) {
        this.cooperationId = cooperationId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getSupplierContactName() {
        return supplierContactName;
    }

    public void setSupplierContactName(String supplierContactName) {
        this.supplierContactName = supplierContactName;
    }

    public String getCropsName() {
        return cropsName;
    }

    public void setCropsName(String cropsName) {
        this.cropsName = cropsName;
    }

    public Double getRequireYield() {
        return requireYield;
    }

    public void setRequireYield(Double requireYield) {
        this.requireYield = requireYield;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}
