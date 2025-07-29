package com.chichifood.model;

    public enum PaymentMethod {
        WALLET("wallet"),
        ONLINE("online");

        private final String value;

        PaymentMethod(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static PaymentMethod fromString(String text) {
            for (PaymentMethod method : PaymentMethod.values()) {
                if (method.value.equalsIgnoreCase(text)) {
                    return method;
                }
            }
            throw new IllegalArgumentException("Unknown payment method: " + text);
        }

}
