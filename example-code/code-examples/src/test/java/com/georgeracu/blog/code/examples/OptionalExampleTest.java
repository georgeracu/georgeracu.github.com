package com.georgeracu.blog.code.examples;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Author georgicaracu
 */
class OptionalExampleTest {

    @ParameterizedTest(name = "Mandatory argument \"{0}\" and optional argument\"{1}\" should return \"{2}\"")
    @CsvSource({"'boo','blah','blahboo'",
            "'boo',,'boo'",
            ",'blah','blah'"})
    void shouldTestNotOverloadedMethod(String mandatory, String optional, String expected) {
        final Optional<String> maybe = Optional.ofNullable(optional);
        final Optional<String> actual = OptionalExample.notOverloaded(mandatory, maybe);

        assertThat(actual)
                .isPresent()
                .hasValue(expected);
    }

    @ParameterizedTest(name = "Mandatory argument \"{0}\" and optional argument\"{1}\" should return \"{2}\"")
    @CsvSource({"'boo','blah','blahboo'",
            "'boo',,'boo'",
            ",'blah','blah'"})
    void shouldTestOverloadedMethod(String mandatory, String optional, String expected) {
        final Optional<String> actual = OptionalExample.overloaded(mandatory, optional);

        assertThat(actual)
                .isPresent()
                .hasValue(expected);
    }
}