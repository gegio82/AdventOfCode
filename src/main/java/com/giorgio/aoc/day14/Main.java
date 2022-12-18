package com.giorgio.aoc.day14;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day14/input.txt");
        List<String> lines = Files.readAllLines(path);

        //count refers to the first unit of sand `falling forever`, I am looking for last one `still laying`
        System.out.println("Part 1: " + (execute(lines, maxY -> p -> Optional.empty()) - 1));

        System.out.println("Part 2: " + execute(lines,
                maxY -> pos -> Optional.of(pos).filter(p -> p.y == maxY).map(p -> '#')));
    }

    private static int execute(List<String> lines,
                               Function<Integer, Function<Position, Optional<Character>>> defaultFunctionFactory) {
        List<List<Position>> paths = lines.stream().map(Main::parsePath).toList();
        int maxY = paths.stream().flatMap(List::stream).mapToInt(Position::y).max().orElseThrow() + 2;
        Function<Position, Optional<Character>> defaultFunction = defaultFunctionFactory.apply(maxY);
        Map<Position, Character> map = new MapWithDefaultValues<>(generateMap(paths), defaultFunction);

        int count = 1;
        while (dropUnitOfSand(map, maxY) ) {
            count++;
        }
        return count;
    }

    private static Map<Position, Character> generateMap(List<List<Position>> paths) {
        Map<Position, Character> map = new HashMap<>();
        for (List<Position> path: paths) {
            for(int i = 0; i < path.size() - 1; i++) {
                Position start = path.get(i);
                Position end = path.get(i + 1);
                if (start.x() == end.x()) {
                    IntStream.rangeClosed(Math.min(start.y(), end.y()), Math.max(start.y(), end.y()))
                            .forEach(y -> map.put(new Position(start.x(), y), '#'));
                } else {
                    IntStream.rangeClosed(Math.min(start.x(), end.x()), Math.max(start.x(), end.x()))
                            .forEach(x -> map.put(new Position(x, start.y()),'#'));
                }
            }
        }
        return map;
    }

    private static boolean dropUnitOfSand(Map<Position, Character> map, int maxY) {
        Position sandPosition = new Position(500, 0);
        while (sandPosition.y() <= maxY && isNotLanded(map,sandPosition)) {
            if (!map.containsKey(new Position(sandPosition.x(), sandPosition.y + 1))) {
                sandPosition = new Position(sandPosition.x, sandPosition.y + 1);
            } else if (!map.containsKey(new Position(sandPosition.x() - 1, sandPosition.y + 1))) {
                sandPosition = new Position(sandPosition.x - 1, sandPosition.y + 1);
            } else if (!map.containsKey(new Position(sandPosition.x() + 1, sandPosition.y + 1))) {
                sandPosition = new Position(sandPosition.x + 1, sandPosition.y + 1);
            }
        }
        map.put(sandPosition, 'o');
        return sandPosition.y > 0 && sandPosition.y < maxY;
    }

    private static boolean isNotLanded(Map<Position, Character> map, Position sandPosition) {
        return Stream.of(sandPosition.x - 1, sandPosition.x, sandPosition.x + 1)
                    .anyMatch(x -> !map.containsKey(new Position(x, sandPosition.y() + 1)));
    }

    private static List<Position> parsePath(String path) {
        return Arrays.stream(path.split(" -> ")).map(Position::new).toList();
    }

    record Position(int x, int y){
        public Position(String coordinates) {
            this(Integer.parseInt(coordinates.split(",")[0]),
                    Integer.parseInt(coordinates.split(",")[1]));
        }
    }

    static class MapWithDefaultValues<K,V> extends HashMap<K, V>{

        private final Function<K, Optional<V>> defaultFunction;

        MapWithDefaultValues(Map<K, V> map, Function<K, Optional<V>> defaultFunction) {
            super(map);
            this.defaultFunction = defaultFunction;
        }

        @Override
        public boolean containsKey(Object key) {
            return defaultFunction.apply((K)key).isPresent() || super.containsKey(key);
        }

        @Override
        public V get(Object key) {
            return defaultFunction.apply((K)key).orElseGet(() -> super.get(key));
        }
    }

    enum Status {
        BLOCKED, FALLING_FOREVER, STILL_LAYING;
    }
}