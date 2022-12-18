package com.giorgio.aoc.day9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private final static Pattern MOVE_COMMAND = Pattern.compile("([RULD]) (\\d+)");

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day9/input.txt");
        List<String> instructions = Files.readAllLines(path);
        System.out.println("2 nodes rope: " + execute(instructions, 2));
        System.out.println("10 nodes rope: " + execute(instructions, 10));
    }

    public static int execute(List<String> instructions, int ropeLength) {
        Position[] rope = initRope(ropeLength);
        Set<Position> tailPositions = new HashSet<>();
        tailPositions.add(rope[ropeLength - 1]);

        for (String instruction: instructions) {
            Matcher matcher = MOVE_COMMAND.matcher(instruction);
            if (matcher.matches()) {
                Direction direction = Direction.valueOf(matcher.group(1));
                int distance = Integer.parseInt(matcher.group(2));
                tailPositions.addAll(move(rope, direction, distance));
            }
        }
        return tailPositions.size();
    }

    private static Set<Position> move(Position[] rope, Direction direction, int distance) {
        Set<Position> tailPositions = new HashSet<>();
        for (int i = 0; i < distance; i++) {
            moveHead(rope, direction);
            moveTail(rope);
            tailPositions.add(rope[rope.length - 1]);
        }
        return tailPositions;
    }

    private static Position[] initRope(int ropeLength) {
        Position[] rope = new Position[ropeLength];
        for (int i = 0; i < rope.length; i++) {
            rope[i] = new Position(0,0);
        }
        return rope;
    }

    private static void moveHead(Position[] rope, Direction direction) {
        rope[0] = rope[0].move(direction);
    }

    private static void moveTail(Position[] rope) {
        for (int i = 1; i < rope.length; i++){
            if (rope[i].distanceFrom(rope[i - 1]) > 1) {
                rope[i] = new Position(rope[i].x() + Integer.signum(rope[i - 1].x() - rope[i].x()),
                        rope[i].y()  + Integer.signum(rope[i - 1].y() - rope[i].y()));
            }
        }
    }

    record Position(int x, int y) {
        Position move(Direction direction) {
            return switch (direction) {
                case R: yield new Position(this.x() + 1, this.y());
                case U: yield new Position(this.x(), this.y() - 1);
                case L: yield new Position(this.x() - 1, this.y());
                case D: yield new Position(this.x(), this.y() + 1);
            };
        }

        int distanceFrom(Position other) {
            return Math.max(Math.abs(this.x() - other.x()),
                    Math.abs(this.y() - other.y()));
        }
    }

    enum Direction {
        R,U,L,D;
    }

}
