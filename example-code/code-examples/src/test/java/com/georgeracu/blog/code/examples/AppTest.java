package com.georgeracu.blog.code.examples;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for simple App.
 */
class AppTest {

    @ParameterizedTest(name = "Should return {1} when value is \"{0}\"")
    @CsvSource({",'false'","'','false'","'boo','false'","'blah','true'"})
    void shouldMatchExpectedValue(String otherValue, boolean expected) {
        assertThat(App.isExpectedValue(otherValue)).isEqualTo(expected);
    }
}
