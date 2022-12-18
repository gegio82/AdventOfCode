package com.giorgio.aoc.day17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day17/input.txt");
        String gasSequence = Files.readString(path);
        partOne(gasSequence);
        partTwo(gasSequence);
    }

    public static void partOne(String gasSequence) throws IOException {
        long rockCount = 0L;
        int gasIndex = 0;
        int top = 0;

        Set<Position> grid = IntStream.range(0, 7)
                .mapToObj(x -> new Position(x, 0))
                .collect(Collectors.toSet());

        while (rockCount < 2022L) {
            final Shape shape = Shape.values()[(int) (rockCount % Shape.values().length)];
            final Rock rock = shape.drawRock(2, top + 4);
            boolean added = false;
            while (!added) {
                if (gasSequence.charAt(gasIndex) == '<') {
                    rock.moveLeft();
                    if (rock.positions.stream().anyMatch(grid::contains)) {
                        rock.moveRight();
                    }
                } else {
                    rock.moveRight();
                    if (rock.positions.stream().anyMatch(grid::contains)) {
                        rock.moveLeft();
                    }
                }
                gasIndex = (gasIndex + 1) % gasSequence.length();
                rock.moveDown();

                if (rock.positions.stream().anyMatch(grid::contains)) {
                    rock.moveUp();
                    grid.addAll(rock.positions);
                    top = grid.stream().mapToInt(Position::y).max().orElse(0);

                    rockCount++;
                    added = true;

                    //draw(grid, top);
                }
            }
        }
        System.out.println("Part 1: " + top);
    }

    public static void partTwo(String gasSequence) {
        long rockCount = 0L;
        int gasIndex = 0;
        int top = 0;

        long totalRocksToDrop = 1000000000000L;
        long increased = 0L;

        Map<Status, Pair<Long,Integer>> statuses = new HashMap<>();
        Set<Position> grid = IntStream.range(0, 7)
                .mapToObj(x -> new Position(x, 0))
                .collect(Collectors.toSet());

        while (rockCount < totalRocksToDrop) {
            final Shape shape = Shape.values()[(int) (rockCount % Shape.values().length)];
            final Rock rock = shape.drawRock(2, top + 4);
            boolean added = false;
            while (!added) {
                if (gasSequence.charAt(gasIndex) == '<') {
                    rock.moveLeft();
                    if (rock.positions.stream().anyMatch(grid::contains)) {
                        rock.moveRight();
                    }
                } else {
                    rock.moveRight();
                    if (rock.positions.stream().anyMatch(grid::contains)) {
                        rock.moveLeft();
                    }
                }
                gasIndex = (gasIndex + 1) % gasSequence.length();
                rock.moveDown();

                if (rock.positions.stream().anyMatch(grid::contains)) {
                    rock.moveUp();
                    grid.addAll(rock.positions);
                    top =   grid.stream().mapToInt(Position::y).max().orElse(0);
                    final int finalTop = top;
                    Set<Position> topGrid = grid.stream()
                                 .map(p -> new Position(p.x(),  finalTop - p.y()))
                            .filter(p -> p.y() <= 20 )
                            .collect(Collectors.toSet());

                    Status status = new Status(gasIndex, shape, topGrid);
                    if (statuses.containsKey(status)) {
                        Pair<Long, Integer> pair = statuses.get(status);
                        long oldRockCount = pair.first;
                        int oldTop = pair.second;
                        int increaseForLoop = top - oldTop;
                        long rockCountInCycle = rockCount - oldRockCount;
                        long requiredCyclesToGetEnoughRocks = (totalRocksToDrop - rockCount) / rockCountInCycle;
                        increased += requiredCyclesToGetEnoughRocks * increaseForLoop;
                        rockCount += rockCountInCycle * requiredCyclesToGetEnoughRocks;
                    }
                    statuses.put(status, new Pair<>(rockCount, top));

                    rockCount++;
                    added = true;
                }
            }
        }
        System.out.println("Part 2: " + (top + increased));
    }

    private static final void draw(Set<Position> grid, int top) {
        for (int y = top; y >= 0; y--) {
            for (int x = 0; x < 7; x++) {
                System.out.print(grid.contains(new Position(x,y))? 'X' : '.');
            }
            System.out.println();
        }
        System.out.println();
    }

    static class Rock {

        Rock(Set<Position> positions) {
            this.positions = positions;
        }
        Set<Position> positions;

        public void moveLeft() {
            if (positions.stream().noneMatch(p -> p.x == 0)) {
                positions = positions.stream().map(p -> new Position(p.x - 1, p.y)).collect(Collectors.toSet());
            }
        }
        public void moveRight() {
            if (positions.stream().noneMatch(p -> p.x == 6)) {
                positions = positions.stream().map(p -> new Position(p.x + 1, p.y)).collect(Collectors.toSet());
            }
        }
        public void moveUp() {
            positions = positions.stream().map(p -> new Position(p.x, p.y + 1)).collect(Collectors.toSet());
        }
        public void moveDown() {
            positions = positions.stream().map(p -> new Position(p.x, p.y - 1)).collect(Collectors.toSet());
        }

    }

    enum Shape {
        SHAPE1((x,y) -> IntStream.range(x, x + 4).mapToObj(i -> new Position(i, y)).collect(Collectors.toSet())),
        SHAPE2((x,y) -> Set.of(new Position(x + 1, y), new Position(x, y + 1), new Position(x + 1, y + 1), new Position(x + 2, y + 1), new Position(x + 1, y + 2))),
        SHAPE3((x,y) -> Set.of(new Position(x, y), new Position(x + 1, y), new Position(x + 2, y), new Position(x + 2, y + 1), new Position(x + 2, y + 2))),
        SHAPE4((x,y) -> IntStream.range(y, y + 4).mapToObj(i -> new Position(x, i)).collect(Collectors.toSet())),
        SHAPE5((x,y) -> Set.of(new Position(x, y), new Position(x, y + 1), new Position(x + 1, y), new Position(x + 1, y + 1)));

        BiFunction<Integer, Integer, Set<Position>> drawingFunction;

        Shape(BiFunction<Integer, Integer, Set<Position>> drawingFunction) {
            this.drawingFunction = drawingFunction;
        }

        public Rock drawRock(int x, int y) {
            return new Rock(drawingFunction.apply(x, y));
        }
    }

    record Position(int x, int y){}

    record Status(int index, Shape shape, Set<Position> topGrid){}

    record Pair<P,Q>(P first, Q second){}
}