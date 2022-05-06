package com.georgeracu.blog.example;

public record VehicleIdentificationNumber(String value) {

    public VehicleIdentificationNumber(final String value) {
        validateLengthOrThrow(value);
        this.value = value;
    }

    private void validateLengthOrThrow(final String input) {
        if (input != null && input.length() != 17) {
            throw new InvalidVINException("VIN length should be 17 characters");
        }
    }

    public String worldManufacturerIdentifier() {
        if (value != null) {
            return value.substring(0,3);
        } else {
            throw new InvalidVINException("Value has not been initialized");
        }
    }
}

class InvalidVINException extends RuntimeException {

    public InvalidVINException(String message) {
        super(message);
    }

}