package com.giorgio.aoc.day18;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day18 {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/data/day18/input.txt");
        List<String> lines = Files.readAllLines(path);
        Set<Cube> cubes = lines.stream()
                .map(Cube::new)
                .collect(Collectors.toSet());

        int surfacesExposed = 0;
        for (Cube cube: cubes) {
            surfacesExposed += 6 - cube.neighbours().filter(cubes::contains).count();
        }
        System.out.println(surfacesExposed);
        int minX = cubes.stream().mapToInt(Cube::x).min().orElseThrow();
        int maxX = cubes.stream().mapToInt(Cube::x).max().orElseThrow();
        int minY = cubes.stream().mapToInt(Cube::y).min().orElseThrow();
        int maxY = cubes.stream().mapToInt(Cube::y).max().orElseThrow();
        int minZ = cubes.stream().mapToInt(Cube::z).min().orElseThrow();
        int maxZ = cubes.stream().mapToInt(Cube::z).max().orElseThrow();
        Set<Cube> empty = IntStream.range(minX + 1, maxX).boxed()
                .flatMap(x -> IntStream.range(minY + 1, maxY).boxed()
                        .flatMap(y -> IntStream.range(minZ + 1, maxZ)
                                .mapToObj(z -> new Cube(x,y,z))
                                .filter(Predicate.not(cubes::contains))))
                .collect(Collectors.toSet());
        Set<Cube> emptyInternalRegion = getInternalEmptyRegion(empty, cubes);
        int internalSurfaces = 0;
        for (Cube cube: emptyInternalRegion) {
            internalSurfaces += cube.neighbours().filter(cubes::contains).count();
        }
        System.out.println(surfacesExposed - internalSurfaces);
    }

    private static Set<Cube> getInternalEmptyRegion(Set<Cube> empty, Set<Cube> cubes) {
        Set<Cube> internalEmptyRegion = new HashSet<>();
        Iterator<Cube> iterator = empty.iterator();
        Set<Cube> alreadyProcessed = new HashSet<>();
        while (iterator.hasNext()) {
            Cube current = iterator.next();
            if (!alreadyProcessed.contains(current)) {
                Set<Cube> currentZone = new HashSet<>();
                boolean isInternalEmptyRegion = checkInternalEmptyRegion(current, currentZone, empty, cubes);
                if (isInternalEmptyRegion) {
                    internalEmptyRegion.addAll(currentZone);
                }
                alreadyProcessed.addAll(currentZone);
            }
        }
        return internalEmptyRegion;
    }

    private static boolean checkInternalEmptyRegion(Cube current,
                                                     Set<Cube> currentZone,
                                                     Set<Cube> empty,
                                                     Set<Cube> cubes) {
        currentZone.add(current);
        Set<Cube> neighbours = current.neighbours().collect(Collectors.toSet());
        boolean isInternal = true;
        for (Cube neighbour: neighbours) {
            if (!cubes.contains(neighbour) && !currentZone.contains(neighbour)) {
                if (empty.contains(neighbour)) {
                    if (!checkInternalEmptyRegion(neighbour, currentZone, empty, cubes)) {
                        isInternal = false;
                    }
                } else {
                    isInternal = false;
                }
            }
        }
        return isInternal;
    }

    record Cube(int x, int y, int z) {
        public Cube(String s) {
            this(Arrays.stream(s.split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray());
        }
        public Cube(int[] coords) {
            this(coords[0], coords[1], coords[2]);
        }

        public Stream<Cube> neighbours() {
            return Stream.of(
                    new Cube(x-1, y, z),
                    new Cube(x+1, y, z),
                    new Cube(x, y-1, z),
                    new Cube(x, y+1, z),
                    new Cube(x, y, z-1),
                    new Cube(x, y, z+1));
        }
    }
}