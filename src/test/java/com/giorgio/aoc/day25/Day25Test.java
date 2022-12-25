package com.giorgio.aoc.day25;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class Day25Test {
    @ParameterizedTest
    @MethodSource("paramsFor_parseSnafuNumber")
    public void parseSnafuNumber(String snafuNumber, int expected) {
        assertThat(Day25.fromSnafuNumber(snafuNumber)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("paramsFor_parseSnafuNumber")
    public void toSnafuNumber(String expectedSnafuNumber, int decimal) {
        assertThat(Day25.toSnafuNumber(decimal)).isEqualTo(expectedSnafuNumber);
    }

    public static Stream<Arguments> paramsFor_parseSnafuNumber() {
        return Stream.of(
                Arguments.of("1", 1),
                Arguments.of("2", 2),
                Arguments.of("1=", 3),
                Arguments.of("1-", 4),
                Arguments.of("10", 5),
                Arguments.of("11", 6),
                Arguments.of("12", 7),
                Arguments.of("2=", 8),
                Arguments.of("2-", 9),
                Arguments.of("20", 10),
                Arguments.of("1=0", 15),
                Arguments.of("1-0", 20),
                Arguments.of("110", 30),
                Arguments.of("2=0", 40),
                Arguments.of("1=11-2", 2022),
                Arguments.of("1-0---0", 12345),
                Arguments.of("1121-1110-1=0", 314159265)
        );
    }

}