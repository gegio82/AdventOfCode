package com.giorgio.aoc.day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Day04 {

    public static void main(String[] args) throws IOException {
        System.out.println(first());
        System.out.println(second());
    }

    public static long first() throws IOException {
        try (Stream<String> lines = Files.lines(
                Path.of("src/main/resources/data/day4/input.txt"))) {
            return lines.filter(line -> {
                Assignment secondAssignment = new Assignment(line.split(",")[1]);
                Assignment firstAssignment = new Assignment(line.split(",")[0]);
                return firstAssignment.contains(secondAssignment) || secondAssignment.contains(firstAssignment);
            }).count();
        }
    }

    public static long second() throws IOException {
        try (Stream<String> lines = Files.lines(
                Path.of("src/main/resources/data/day4/input.txt"))) {
            return lines.filter(line -> {
                Assignment secondAssignment = new Assignment(line.split(",")[1]);
                Assignment firstAssignment = new Assignment(line.split(",")[0]);
                return firstAssignment.overlaps(secondAssignment);
            }).count();
        }

    }

    record Assignment(int start, int end) {
        public Assignment(String assignment) {
            this(Integer.parseInt(assignment.split("-")[0]),
                    Integer.parseInt(assignment.split("-")[1]));
        }

        public boolean contains(Assignment other) {
            return (this.start <= other.start && this.end >= other.end);
        }

        public boolean overlaps(Assignment other) {
            return (this.start <= other.start && this.end >= other.start) ||
                    (other.start <= this.start && other.end >= this.start);
        }
    }
}
