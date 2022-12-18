package com.giorgio.aoc.day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Main {
    private final static String NOOP = "noop";
    private final static Pattern ADD_COMMAND = Pattern.compile("addx ([+-]?\\d+)");

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day10/input.txt");
        List<String> instructions = Files.readAllLines(path);
        System.out.println("first: " + execute1(instructions));
        System.out.println(execute2(instructions));
    }

    public static int execute1(List<String> instructions) {
        int x = 1;
        int cycle = 0;
        int strength = 0;
        for (int i = 0; i < instructions.size(); i ++) {
            String instruction = instructions.get(i);
            if (instruction.equals(NOOP)) {
                cycle++;
                if ((cycle - 20) % 40 == 0) {
                    strength += x * cycle;
                }
            } else {
                Matcher matcher = ADD_COMMAND.matcher(instruction);
                if (matcher.matches()) {
                    cycle++;
                    if ((cycle - 20) % 40 == 0) {
                        strength += x * cycle;
                    }
                    cycle++;
                    if ((cycle - 20) % 40 == 0) {
                        strength += x * cycle;
                    }
                    int amount = Integer.parseInt(matcher.group(1));
                    x += amount;
                }
            }

        }
        return strength;
    }

    public static String execute2(List<String> instructions) {
        int x = 1;
        int cycle = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < instructions.size(); i ++) {
            String instruction = instructions.get(i);
            if (instruction.equals(NOOP)) {
                cycle = nextCycle(cycle, sb, x);
            } else {
                Matcher matcher = ADD_COMMAND.matcher(instruction);
                if (matcher.matches()) {
                    cycle = nextCycle(cycle, sb, x);
                    cycle = nextCycle(cycle, sb, x);
                    int amount = Integer.parseInt(matcher.group(1));
                    x += amount;
                }
            }

        }
        return sb.toString();
    }

    private static int nextCycle(int cycle, StringBuilder sb, int x) {
        cycle = cycle % 40 + 1;
        sb.append(draw(x, cycle));
        if (cycle % 40 == 0) {
            sb.append("\n");
        }
        return cycle;
    }

    private static char draw(int x, int cycle) {
        return IntStream.range(x, x + 3).boxed().filter(k -> k == cycle).findAny().map(i -> '#').orElse('.');
    }

}
