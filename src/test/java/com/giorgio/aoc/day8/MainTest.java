package com.giorgio.aoc.day8;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


class MainTest {
    @Test
    void countAllVisibleTrees() throws IOException {
        Path path = Paths.get("src/main/resources/data/day8/input.txt");
        long result = Day08.countAllVisibleTrees(path);
        assertThat(result).isEqualTo(1825);
    }

    @Test
    void countAllVisibleTrees2() throws IOException {
        Path path = Paths.get("src/main/resources/data/day8/input.test.txt");
        long result = Day08.countAllVisibleTrees(path);
        assertThat(result).isEqualTo(21);
    }

    @Test
    void computeBestScenic() throws IOException {
        Path path = Paths.get("src/main/resources/data/day8/input.txt");
        int result = Day08.computeBestScenic(path);
        assertThat(result).isEqualTo(235200);
    }

    @Test
    void computeBestScenic2() throws IOException {
        Path path = Paths.get("src/main/resources/data/day8/input.test.txt");
        int result = Day08.computeBestScenic(path);
        assertThat(result).isEqualTo(8);
    }
    
    @ParameterizedTest
    @MethodSource("paramsFor_computeScenicWest")
    public void computeScenicWest(int i, int j, int expected) {
        int[][] map = new int[][] {
                new int[] {3,0,3,7,3},
                new int[] {2,5,5,1,2},
                new int[] {6,5,3,3,2},
                new int[] {3,3,5,4,9},
                new int[] {3,5,3,9,0}
        };
        int result = Day08.computeScenicWest(map, i, j);
        assertThat(result).isEqualTo(expected);
    }

    public static Stream<Arguments> paramsFor_computeScenicWest() {
        return Stream.of(
                Arguments.of(1, 0, 0),
                Arguments.of(4, 0, 0),
                Arguments.of(0, 1, 1),
                Arguments.of(0, 3, 3),
                Arguments.of(3, 4, 4),
                Arguments.of(4, 4, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("paramsFor_computeScenicEast")
    public void computeScenicEast(int i, int j, int expected) {
        int[][] map = new int[][] {
                new int[] {3,0,3,7,3},
                new int[] {2,5,5,1,2},
                new int[] {6,5,3,3,2},
                new int[] {3,3,5,4,9},
                new int[] {3,5,3,9,0}
        };
        int result = Day08.computeScenicEast(map, i, j);
        assertThat(result).isEqualTo(expected);
    }

    public static Stream<Arguments> paramsFor_computeScenicEast() {
        return Stream.of(
                Arguments.of(0, 4, 0),
                Arguments.of(4, 4, 0),
                Arguments.of(0, 0, 2),
                Arguments.of(2, 0, 4),
                Arguments.of(3, 1, 1),
                Arguments.of(4, 0, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("paramsFor_computeScenicNorth")
    public void computeScenicNorth(int i, int j, int expected) {
        int[][] map = new int[][] {
                new int[] {3,0,3,7,3},
                new int[] {2,5,5,1,2},
                new int[] {6,5,3,3,2},
                new int[] {3,3,5,4,9},
                new int[] {3,5,3,9,0}
        };
        int result = Day08.computeScenicNorth(map, i, j);
        assertThat(result).isEqualTo(expected);
    }

    public static Stream<Arguments> paramsFor_computeScenicNorth() {
        return Stream.of(
                Arguments.of(0, 0, 0),
                Arguments.of(0, 4, 0),
                Arguments.of(2, 0, 2),
                Arguments.of(2, 2, 1),
                Arguments.of(3, 4, 3),
                Arguments.of(4, 3, 4)
        );
    }

    @ParameterizedTest
    @MethodSource("paramsFor_computeScenicSouth")
    public void computeScenicSouth(int i, int j, int expected) {
        int[][] map = new int[][] {
                new int[] {3,0,3,7,3},
                new int[] {2,5,5,1,2},
                new int[] {6,5,3,3,2},
                new int[] {3,3,5,4,9},
                new int[] {3,5,3,9,0}
        };
        int result = Day08.computeScenicSouth(map, i, j);
        assertThat(result).isEqualTo(expected);
    }

    public static Stream<Arguments> paramsFor_computeScenicSouth() {
        return Stream.of(
                Arguments.of(0, 3, 4),
                Arguments.of(2, 0, 2),
                Arguments.of(2, 2, 1),
                Arguments.of(3, 4, 1),
                Arguments.of(4, 3, 0),
                Arguments.of(4, 4, 0)
        );
    }
}