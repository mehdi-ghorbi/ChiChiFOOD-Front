package com.chichifood.model;

public enum TransactionStatus {
    SUCCESS("success"),
    FAILED("failed");

    private final String value;

    TransactionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TransactionStatus fromString(String text) {
        for (TransactionStatus status : TransactionStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown transaction status: " + text);
    }
}