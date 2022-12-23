package com.giorgio.aoc.day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day01 {

    public static void main(String[] args) throws IOException {
        System.out.println("top: " + top());
        System.out.println("top3: " + top3());
    }

    public static int top() throws IOException {
        int currentWeight = 0;
        int max = 0;

        List<String> lines = Files.readAllLines(
                Path.of("src/main/resources/data/day1/input.txt"));
        for(String line: lines) {
            if (line.isBlank()) {
                if (currentWeight > max) {
                    max = currentWeight;
                }
                currentWeight = 0;
            } else {
                int weight = Integer.parseInt(line);
                currentWeight += weight;
            }
        }

        return max;
    }
    //74198

    public static int top3() throws IOException {
        int currentWeight = 0;
        List<Integer> bags = new ArrayList<>();
        List<String> lines = Files.readAllLines(
                Path.of("src/main/resources/data/day1/input.txt"));
        for (String line: lines) {
            if (line.isBlank()) {
                bags.add(currentWeight);
                currentWeight = 0;
            } else {
                int weight = Integer.parseInt(line);
                currentWeight += weight;
            }
        }
        return bags.stream().sorted(Comparator.reverseOrder()).limit(3).mapToInt(Integer::intValue).sum();
    }
    //209914
}
