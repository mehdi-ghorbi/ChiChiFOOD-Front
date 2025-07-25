package com.chichifood.model;


public enum OrderStatus {
    SUBMITTED("submitted"),
    UNPAID_AND_CANCELLED("unpaid and cancelled"),
    WAITING_VENDOR("waiting vendor"),
    CANCELLED("cancelled"),
    FINDING_COURIER("finding courier"),
    ON_THE_WAY("on the way"),
    COMPLETED("completed");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OrderStatus fromString(String text) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + text);
    }
}

