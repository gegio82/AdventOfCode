package com.giorgio.aoc.day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day05 {

    private final static Pattern MOVE_COMMAND = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");
    public static void main(String[] args) throws IOException {
        System.out.println(execute(Day05::move));
        System.out.println(execute(Day05::moveKeepingOrder));
    }

    public static String execute(Mover mover) throws IOException {
        List<Stack<Character>> stacks = new ArrayList<>();
        List<String> allLines = Files.readAllLines(Paths.get("src/main/resources/data/day5/input.txt"));
        boolean start = false;
        for (String line : allLines) {
            if (line.isBlank()) {
                start = true;
            } else if (start) {
                Matcher matcher = MOVE_COMMAND.matcher(line);
                if (matcher.matches()) {
                    int howMany = Integer.parseInt(matcher.group(1));
                    int from = Integer.parseInt(matcher.group(2));
                    int to = Integer.parseInt(matcher.group(3));
                    mover.move(stacks, howMany, from, to);
                }
            } else {
                for (int i = 0; 1 + i * 4 < line.length(); i++) {
                    if (stacks.size() <= i) {
                        for (int j = stacks.size(); j <= i; j++) {
                            stacks.add(new Stack<>());
                        }
                    }
                    char current = line.charAt(1 + i * 4);
                    if (Character.isLetter(current)) {
                        stacks.get(i).add(0, current);
                    }
                }
            }
        }
        return stacks.stream().map(Stack::peek).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining());
    }

    private static void move(List<Stack<Character>> stacks, int howMany, int from, int to) {
        for (int i = 0; i < howMany; i++) {
            Character c = stacks.get(from - 1).pop();
            stacks.get(to - 1).push(c);
        }
    }

    private static void moveKeepingOrder(List<Stack<Character>> stacks, int howMany, int from, int to) {
        Stack<Character> temp = new Stack<>();
        for (int i = 0; i < howMany; i++) {
            Character c = stacks.get(from - 1).pop();
            temp.push(c);
        }
        for (int i = 0; i < howMany; i++) {
            Character c = temp.pop();
            stacks.get(to - 1).push(c);
        }
    }

    interface Mover {
        void move(List<Stack<Character>> stacks, int howMany, int from, int to);
    }
}
