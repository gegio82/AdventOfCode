package com.giorgio.aoc.day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("firstStrategy: " + firstStrategy());
        System.out.println("secondStrategy: " + secondStrategy());
    }

    public static int firstStrategy() throws IOException {
        try (Stream<String> lines = Files.lines(
                Path.of("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day2/input.txt"))) {
            return lines.mapToInt(line -> {
                RSP opponent = RSP.opponent(line.split(" ")[0]);
                RSP mine = RSP.mine(line.split(" ")[1]);
                return mine.getPoint() + mine.beat(opponent);
            }).sum();
        }
    }

    public static int secondStrategy() throws IOException {
        try (Stream<String> lines = Files.lines(
                Path.of("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day2/input.txt"))) {
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
}
