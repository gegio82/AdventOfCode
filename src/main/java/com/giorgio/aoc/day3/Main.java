package com.giorgio.aoc.day3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println(first());
        System.out.println(second());
    }

    public static int first() throws IOException {

        try (Stream<String> lines = Files.lines(
                Path.of("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day3/input.txt"))) {
            return lines.map(Main::splitCompartment)
                    .mapToInt(compartments -> IntStream.rangeClosed('A', 'z')
                            .filter(Character::isAlphabetic)
                            .mapToObj(i -> (char)i)
                            .map(c -> Character.toString(c))
                            .filter(c -> compartments.stream().allMatch(comp -> comp.contains(c)))
                            .map(it -> it.charAt(0))
                            .mapToInt(Main::getPoint)
                            .sum())
                    .sum();
        }
    }

    private static List<String> splitCompartment(String rucksack) {
        return List.of(rucksack.substring(0, rucksack.length()/2),
                rucksack.substring(rucksack.length()/2));
    }


    public static int second() throws IOException {
        File file = new File("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day3/input.txt");
        int count = 0;
        int total = 0;
        List<String> values = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)){;
            while (scanner.hasNext()) {
                String line = scanner.next();
                values.add(line);
                if (count == 2) {
                    for (char i = 'A'; i <= 'z'; i++) {
                        String s = Character.toString(i);
                        if (values.stream().allMatch(it -> it.contains(s))) {
                            total += getPoint(i);
                        }
                    }
                    values.clear();
                }
                count = (count + 1) % 3 ;
            }
        }
        return total;
    }

    public static int getPoint(char c) {
        if (c > 'a' && c <='z') {
            return c - 'a' + 1;
        } else {
            return c - 'A' + 27;
        }
    }
}
