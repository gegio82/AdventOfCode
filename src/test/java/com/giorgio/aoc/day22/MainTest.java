package com.giorgio.aoc.day22;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class MainTest {


    @ParameterizedTest
    @MethodSource("paramsFor_move")
    public void move(Day22.Direction direction, Day22.Position startPosition, int howFar, Day22.Position expected) throws IOException {
        Path path = Paths.get("src/main/resources/data/day22/input.test.txt");
        List<String> lines = Files.readAllLines(path);
        List<String> mapDescription = lines.subList(0, lines.size() - 2);
        Day22.Map map = new Day22.Map(mapDescription);

        Day22.Position newPosition = map.move(startPosition, direction, howFar);

        assertThat(newPosition).isEqualTo(expected);
    }

    public static Stream<Arguments> paramsFor_move() {
        return Stream.of(
                Arguments.of(Day22.Direction.DOWN, new Day22.Position(10, 0), 5, new Day22.Position(10,5)),
                Arguments.of(Day22.Direction.DOWN, new Day22.Position(8, 0), 5, new Day22.Position(8,1)),
                Arguments.of(Day22.Direction.DOWN, new Day22.Position(8, 10), 5, new Day22.Position(8,1)),
                Arguments.of(Day22.Direction.RIGHT, new Day22.Position(10, 3), 1, new Day22.Position(10,4)),
                Arguments.of(Day22.Direction.RIGHT, new Day22.Position(10, 3), 5, new Day22.Position(10,4)),
                Arguments.of(Day22.Direction.RIGHT, new Day22.Position(10, 0), 5, new Day22.Position(12,0)),
                Arguments.of(Day22.Direction.UP, new Day22.Position(10, 1), 1, new Day22.Position(10,0)),
                Arguments.of(Day22.Direction.UP, new Day22.Position(10, 1), 3, new Day22.Position(10,11)),
                Arguments.of(Day22.Direction.UP, new Day22.Position(10, 4), 5, new Day22.Position(12,3)),
                Arguments.of(Day22.Direction.LEFT, new Day22.Position(10, 0), 2, new Day22.Position(8,0)),
                Arguments.of(Day22.Direction.LEFT, new Day22.Position(8, 1), 2, new Day22.Position(10,1)),
                Arguments.of(Day22.Direction.LEFT, new Day22.Position(10, 5), 10, new Day22.Position(4,5))
        );
    }
}