package com.example.suppileragrimart.utils;

public enum CropsStatus {
    MAKE_LAND(0),
    SOW_SEED(1),
    GERMINATION(2),
    TAKE_CARE(3),
    FLOWERING(4),
    PEST_PREVENTION(5),
    FRUITING(6),
    HARVEST(7);

    private final int value;

    private CropsStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
