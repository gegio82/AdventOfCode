package com.giorgio.aoc.day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day12 {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/data/day12/input.txt");
        List<String> lines = Files.readAllLines(path);
        char[][] map = new char[lines.size()][];
        Position startPosition = null;
        Position endPosition = null;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            map[i] = new char[line.length()];
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                if (c == 'S') {
                    startPosition = new Position(i, j);
                    map[i][j] = 'a';
                } else if (c == 'E') {
                    endPosition = new Position(i, j);
                    map[i][j] = 'z';
                } else {
                    map[i][j] = c;
                }
            }
        }
        assert startPosition != null;
        assert endPosition != null;
        System.out.println("From start position: " + computeDistanceMatrixFrom(map, startPosition)[endPosition.x][endPosition.y]);

        List<Position> candidateStartPosition = IntStream.range(0, map.length).boxed()
                .flatMap(i -> IntStream.range(0, map[i].length)
                        .filter(j -> map[i][j] == 'a')
                        .mapToObj(j -> new Position(i, j)))
                .toList();

        final Position finalEndPosition = endPosition;
        System.out.println("Shorter distance from any a-level position: " +
                candidateStartPosition.stream()
                .map(p -> computeDistanceMatrixFrom(map, p))
                .mapToInt(distance -> distance[finalEndPosition.x][finalEndPosition.y])
                .min().orElseThrow());
    }

    private static int[][] computeDistanceMatrixFrom(char[][] map, Position startPosition) {
        int[][] distance = new int[map.length][];
        for (int i = 0; i < distance.length; i++) {
            distance[i] = new int[map[i].length];
            Arrays.fill(distance[i], Integer.MAX_VALUE);
        }
        Queue<Position> queue = new LinkedList<>();
        queue.add(startPosition);
        distance[startPosition.x][startPosition.y] = 0;
        while (!queue.isEmpty()) {
            Position currentPosition = queue.poll();
            currentPosition.reachableNeighbours(map)
                    .filter(p -> distance[p.x][p.y] == Integer.MAX_VALUE)
                    .forEach(p -> {
                        distance[p.x][p.y] = distance[currentPosition.x][currentPosition.y] + 1;
                        queue.offer(p);
                    });
        }
        return distance;
    }

    record Position(int x, int y) {
        Stream<Position> reachableNeighbours(char[][] map) {
            return Stream.of(new Position(x - 1, y), new Position(x + 1, y), new Position(x,y - 1), new Position(x, y + 1))
                            .filter(p -> p.x >= 0)
                            .filter(p -> p.x < map.length)
                            .filter(p -> p.y >= 0)
                            .filter(p -> p.y < map[p.x].length)
                            .filter(p -> map[p.x][p.y] <= map[x][y] + 1);
        }
    }
}