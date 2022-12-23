package com.giorgio.aoc.day8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

public class Day08 {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/data/day8/input.txt");
        System.out.println("all visible trees: " + countAllVisibleTrees(path));
        System.out.println("best scenic: " + computeBestScenic(path));
    }

    public static long countAllVisibleTrees(Path path) throws IOException {
        int[][] map = initMap(path);
        int [][] visibleFromNorth = getVisibleMatrixFromNorth(map);
        int [][] visibleFromSouth = getVisibleMatrixFromSouth(map);
        int [][] visibleFromEast = getVisibleMatrixFromEast(map);
        int [][] visibleFromWest = getVisibleMatrixFromWest(map);

        return IntStream.range(0, map.length)
                .flatMap(i -> IntStream.range(0, map[i].length)
                        .filter(j -> map[i][j] > visibleFromNorth[i][j] ||
                                map[i][j] > visibleFromSouth[i][j] ||
                                map[i][j] > visibleFromEast[i][j] ||
                                map[i][j] > visibleFromWest[i][j]))
                .count();
    }

    public static int computeBestScenic(Path path) throws IOException {
        int[][] map = initMap(path);
        return IntStream.range(0, map.length)
                .flatMap(i -> IntStream.range(0, map[i].length)
                        .map(j -> computeScenicNorth(map, i, j)
                                * computeScenicSouth(map, i, j)
                                * computeScenicEast(map, i , j)
                                * computeScenicWest(map, i , j)))
                .max()
                .orElse(0);
    }

    private static int[][] initMap(Path path) throws IOException {
        List<String> allLines = Files.readAllLines(path);
        int[][] map = new int[allLines.size()][];
        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            map[i] = line.chars()
                    .mapToObj(c -> (char)c)
                    .map(Object::toString)
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }
        return map;
    }

    static int[][] getVisibleMatrixFromWest(int[][] map) {
        int[][] visibleMatrix = new int[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            visibleMatrix[i][0] = -1;
            for (int j = 1; j < map[0].length; j++) {
                visibleMatrix[i][j] = Math.max(visibleMatrix[i][j-1], map[i][j-1]);
            }
        }
        return visibleMatrix;
    }

    static int[][] getVisibleMatrixFromEast(int[][] map) {
        int[][] visibleMatrix = new int[map.length][map[0].length];
        for (int i = map.length - 1; i >= 0; i--) {
            visibleMatrix[i][map[0].length - 1] = -1;
            for (int j = map[0].length - 2; j >= 0 ; j--) {
                visibleMatrix[i][j] = Math.max(visibleMatrix[i][j+1], map[i][j+1]);
            }
        }
        return visibleMatrix;
    }

    static int[][] getVisibleMatrixFromNorth(int[][] map) {
        int[][] visibleMatrix = new int[map.length][map[0].length];
        for (int i = 0; i < map[0].length; i++) {
            visibleMatrix[0][i] = -1;
            for (int j = 1; j < map.length; j++) {
                visibleMatrix[j][i] = Math.max(visibleMatrix[j-1][i], map[j-1][i]);
            }
        }
        return visibleMatrix;
    }

    static int[][] getVisibleMatrixFromSouth(int[][] map) {
        int[][] visibleMatrix = new int[map.length][map[0].length];
        for (int i = map[0].length - 1; i >= 0 ; i--) {
            visibleMatrix[map.length - 1][i] = -1;
            for (int j = map.length - 2; j >= 0 ; j--) {
                visibleMatrix[j][i] = Math.max(visibleMatrix[j+1][i], map[j+1][i]);
            }
        }
        return visibleMatrix;
    }

    static int computeScenicNorth(int[][] map, int i, int j) {
        int count = 0;
        for (int k = i - 1; k >= 0; k--) {
            count++;
            if (map[i][j] <= map[k][j]) {
                break;
            }
        }
        return count;
    }

    static int computeScenicSouth(int[][] map, int i, int j) {
        int count = 0;
        for (int k = i + 1; k < map.length; k++) {
            count++;
            if (map[i][j] <= map[k][j]) {
                break;
            }
        }
        return count;
    }

    static int computeScenicWest(int[][] map, int i, int j) {
        int count = 0;
        for (int k = j - 1; k >= 0; k--) {
            count++;
            if (map[i][j] <= map[i][k]) {
                break;
            }
        }
        return count;
    }

    static int computeScenicEast(int[][] map, int i, int j) {
        int count = 0;
        for (int k = j + 1; k < map[i].length; k++) {
            count++;
            if (map[i][j] <= map[i][k]) {
                break;
            }
        }
        return count;
    }
}
