package com.giorgio.aoc.day14;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MainOld {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day14/input.txt");
        List<String> lines = Files.readAllLines(path);
        partOne(lines);
        partTwo(lines);

    }

    private static void partOne(List<String> lines) {
        List<List<Position>> paths = lines.stream().map(MainOld::parsePath).toList();
        int minX = paths.stream().flatMap(List::stream).mapToInt(Position::x).min().orElseThrow();
        int maxX = paths.stream().flatMap(List::stream).mapToInt(Position::x).max().orElseThrow() + 1;
        int maxY = paths.stream().flatMap(List::stream).mapToInt(Position::y).max().orElseThrow() ;
        char map[][] = new char[maxY][];
        for (int i = 0; i < map.length; i++) {
            map[i] = new char[maxX-minX];
            Arrays.fill(map[i], '.');
        }
        for (List<Position> path: paths) {
            for(int i = 0; i < path.size() - 1; i++) {
                Position start = path.get(i);
                Position end = path.get(i + 1);
                if (start.x() == end.x()) {
                    if (start.y() > end.y()) {
                        IntStream.rangeClosed(end.y(), start.y()).forEach(y ->
                                map[y - 1][start.x() - minX] = '#'
                        );
                    } else {
                        IntStream.rangeClosed(start.y(), end.y()).forEach(y ->
                                map[y - 1][start.x() - minX] = '#'
                        );
                    }
                } else {
                    if (start.x() < end.x()) {
                        IntStream.rangeClosed(start.x(), end.x()).forEach(x ->
                                map[start.y() - 1][x - minX] = '#'
                        );
                    } else {
                        IntStream.rangeClosed( end.x(), start.x()).forEach(x ->
                                map[start.y() - 1][x - minX] = '#'
                        );
                    };
                }
            }
        }
        System.out.println();
        int count = 0;
        while (sand(map, minX)) {
            count++;
        }
        System.out.println(count);
    }

    private static void partTwo(List<String> lines) {
        List<List<Position>> paths = lines.stream().map(MainOld::parsePath).toList();
        int minX = paths.stream().flatMap(List::stream).mapToInt(Position::x).min().orElseThrow() - 200;
        int maxX = paths.stream().flatMap(List::stream).mapToInt(Position::x).max().orElseThrow() + 1 + 200;
        int maxY = paths.stream().flatMap(List::stream).mapToInt(Position::y).max().orElseThrow() + 3;
        char map[][] = new char[maxY][];
        for (int i = 0; i < map.length - 1; i++) {
            map[i] = new char[maxX-minX];
            Arrays.fill(map[i], '.');
        }
        map[maxY - 1] = new char[maxX-minX];
        Arrays.fill(map[maxY - 1], '#');
        for (List<Position> path: paths) {
            for(int i = 0; i < path.size() - 1; i++) {
                Position start = path.get(i);
                Position end = path.get(i + 1);
                if (start.x() == end.x()) {
                    if (start.y() > end.y()) {
                        IntStream.rangeClosed(end.y(), start.y()).forEach(y ->
                                map[y][start.x() - minX] = '#'
                        );
                    } else {
                        IntStream.rangeClosed(start.y(), end.y()).forEach(y ->
                                map[y][start.x() - minX] = '#'
                        );
                    }
                } else {
                    if (start.x() < end.x()) {
                        IntStream.rangeClosed(start.x(), end.x()).forEach(x ->
                                map[start.y()][x - minX] = '#'
                        );
                    } else {
                        IntStream.rangeClosed( end.x(), start.x()).forEach(x ->
                                map[start.y()][x - minX] = '#'
                        );
                    };
                }
            }
        }
       System.out.println();
        int count = 0;
        while (sandTillBlocked(map, minX)) {
            count++;
        }
        //draw(map);

        int aa = 0;
        for(int i = 0; i < map.length; i++) {
            for (int j = 0; j <map[i].length; j++) {
                if (map[i][j] == 'o') {
                    aa++;
                }
            }
        }
        System.out.println(aa);
        System.out.println(count);
    }

    private static boolean sand(char[][] map, int minX) {
        Position sandPosition = new Position(500-minX, 0);
        while (isInsideMap(map, sandPosition) && isNotLanded(map,sandPosition)) {

            if (map[sandPosition.y + 1][sandPosition.x()] == '.') {
                sandPosition = new Position(sandPosition.x, sandPosition.y + 1);
            } else if (map[sandPosition.y + 1][sandPosition.x() - 1] == '.') {
                sandPosition = new Position(sandPosition.x - 1, sandPosition.y + 1);
            } else if (map[sandPosition.y + 1][sandPosition.x() + 1] == '.') {
                sandPosition = new Position(sandPosition.x + 1, sandPosition.y + 1);
            }
        }
        boolean isInside = isInsideMap(map, sandPosition);
        if (isInside) {
            map[sandPosition.y()][sandPosition.x()] = 'o';
        }
        return isInside;
    }

    private static boolean sandTillBlocked(char[][] map, int minX) {
        Position sandPosition = new Position(500-minX, 0);
        while (isInsideMap(map, sandPosition) && isNotLanded(map,sandPosition)) {

            if (map[sandPosition.y + 1][sandPosition.x()] == '.') {
                sandPosition = new Position(sandPosition.x, sandPosition.y + 1);
            } else if (map[sandPosition.y + 1][sandPosition.x() - 1] == '.') {
                sandPosition = new Position(sandPosition.x - 1, sandPosition.y + 1);
            } else if (map[sandPosition.y + 1][sandPosition.x() + 1] == '.') {
                sandPosition = new Position(sandPosition.x + 1, sandPosition.y + 1);
            }
        }
        boolean isInside = isInsideMap(map, sandPosition);
        if (isInside) {
            map[sandPosition.y()][sandPosition.x()] = 'o';
        }
        //draw(map);
        return sandPosition.y != 0;
    }

    private static void draw(char[][] map) {
        System.out.println();
        for (int i = 0; i < map.length; i++) {
            System.out.println(map[i]);
        }
        System.out.println();
    }

    private static boolean isInsideMap(char[][] map, Position sandPosition) {
        return sandPosition.x() > 0 && sandPosition.x() < map[0].length - 2 && sandPosition.y() < map.length - 1;
    }

    private static boolean isNotLanded(char[][] map, Position sandPosition) {
        if (sandPosition.y() < map.length - 1) {
            return Stream.of(sandPosition.x - 1, sandPosition.x, sandPosition.x + 1)
                    .anyMatch(x -> map[sandPosition.y() + 1][x] == '.');
        }
        return false;
    }

    private static List<Position> parsePath(String path) {
        return Arrays.stream(path.split(" -> ")).map(Position::new).toList();
    }

    record Position(int x, int y){
        public Position(String coordinates) {
            this(Integer.parseInt(coordinates.split(",")[0]),
                    Integer.parseInt(coordinates.split(",")[1]));
        }
    };
}