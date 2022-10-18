package com.georgeracu.blog.code.examples;

/**
 * Hello world!
 */
public class App {

    private static final String EXPECTED_VALUE = "blah";

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            OptionalExample.handleStrings();
        }
    }

    /**
     * Checks if the values passed is equal to the value expected. Should not throw
     * a NPE when input value is null, it should return false
     *
     * @param other The value to be verified if is equal to expected
     * @return true when matched, false otherwise
     */
    public static boolean isExpectedValue(String other) {
        return EXPECTED_VALUE.equals(other);
    }
}
