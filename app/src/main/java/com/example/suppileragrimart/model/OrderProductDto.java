package com.example.suppileragrimart.model;

public class OrderProductDto {
    private long productId;
    private String productName;
    private long standardPrice;
    private long discountPrice;
    private String productImage;
    private String warehouseName;
    private long quantity;

    public OrderProductDto() {
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(long standardPrice) {
        this.standardPrice = standardPrice;
    }

    public long getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(long discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

}
