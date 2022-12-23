package com.giorgio.aoc.day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

public class Day02 {

    public static void main(String[] args) throws IOException {
        System.out.println("firstStrategy: " + firstStrategy());
        System.out.println("secondStrategy: " + secondStrategy());
    }

    public static int firstStrategy() throws IOException {
        try (Stream<String> lines = Files.lines(
                Path.of("src/main/resources/data/day2/input.txt"))) {
            return lines.mapToInt(line -> {
                RSP opponent = RSP.opponent(line.split(" ")[0]);
                RSP mine = RSP.mine(line.split(" ")[1]);
                return mine.getPoint() + mine.beat(opponent);
            }).sum();
        }
    }

    public static int secondStrategy() throws IOException {
        try (Stream<String> lines = Files.lines(
                Path.of("src/main/resources/data/day2/input.txt"))) {
            return lines.mapToInt(line -> {
                RSP opponent = RSP.opponent(line.split(" ")[0]);
                String needTo = line.split(" ")[1];
                RSP mine = switch (needTo) {
                    case "X" -> RSP.neededToLose(opponent);
                    case "Y" -> RSP.neededToDraw(opponent);
                    default -> RSP.neededToBeat(opponent);
                };
                return mine.getPoint() + mine.beat(opponent);
            }).sum();
        }

    }
    //15508

    enum RSP {
        ROCK("A", "X", 1),
        PAPER("B", "Y", 2),
        SCISSORS("C", "Z", 3);
        private final String opponent;
        private final String mine;
        private final int point;
        private static final Map<RSP, RSP> beat = Map.of(
                ROCK, SCISSORS,
                PAPER, ROCK,
                SCISSORS, PAPER);

        RSP(String opponent, String mine, int point) {
            this.opponent = opponent;
            this.mine = mine;
            this.point = point;
        }

        public static RSP opponent(String opponent) {
            return Arrays.stream(values()).filter(it -> it.opponent.equals(opponent)).findAny().orElse(null);
        }

        public static RSP mine(String mine) {
            return Arrays.stream(values()).filter(it -> it.mine.equals(mine)).findAny().orElse(null);
        }

        public static RSP neededToBeat(RSP opponent) {
            return needTo(opponent, 6);
        }

        public static RSP neededToDraw(RSP opponent) {
            return needTo(opponent, 3);
        }

        public static RSP neededToLose(RSP opponent) {
            return needTo(opponent, 0);
        }

        private static RSP needTo(RSP opponent, int point) {
            return Arrays.stream(RSP.values()).filter(it -> it.beat(opponent) == point).findAny().orElse(null);
        }

        public int getPoint() {
            return point;
        }

        public int beat(RSP opponent) {
            if (this == opponent) {
                return 3;
            }
            if (beat.get(this) == opponent) {
                return 6;
            }
            return 0;
        }
    }
}
