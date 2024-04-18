package com.example.suppileragrimart.utils;

public enum OrderStatus {
    PROCESSING(0),
    CONFIRMED(1),
    DELIVERING(2),
    COMPLETED(3),
    CANCELLED(4);

    private final int value;
    private OrderStatus(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
