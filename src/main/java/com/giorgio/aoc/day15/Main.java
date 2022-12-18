package com.giorgio.aoc.day15;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Main {
    private final static Pattern SENSOR = Pattern.compile("Sensor at x=([+-]?\\d+), y=([+-]?\\d+): closest beacon is at x=([+-]?\\d+), y=([+-]?\\d+)");

    public static void main(String[] args) throws IOException {

        Path path = Paths.get("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day15/input.txt");
        List<String> lines = Files.readAllLines(path);

        final long positionWithoutBeaconAtGivenLine = countPositionWhereBeaconNotAllowedAtLine(lines, 2000000);
        System.out.println("Position without beacon at given line: " + positionWithoutBeaconAtGivenLine);

        findHiddenBeaconInRange(lines, 4000000)
                .map(Position::getTuningFrequency)
                .ifPresentOrElse(tuningFrequency -> System.out.println("Tuning Frequency: " + tuningFrequency),
                        () -> System.out.println("Not Found"));

    }

    private static long countPositionWhereBeaconNotAllowedAtLine(List<String> lines, int givenLine) {
        Set<Integer> intersectionsWithGivenLine = new HashSet<>();
        Set<Integer> beaconOnTheGivenLine = new HashSet<>();
        for (String line: lines) {
            Matcher matcher = SENSOR.matcher(line);
            if (matcher.matches()) {
                Position sensorPosition = new Position(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                Position beaconPosition = new Position(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
                if (beaconPosition.y == givenLine) {
                    beaconOnTheGivenLine.add(beaconPosition.x);
                }
                int distance = Math.abs(beaconPosition.x - sensorPosition.x) + Math.abs(beaconPosition.y - sensorPosition.y);
                int yDistanceProjectionWithGivenLine = Math.abs(sensorPosition.y - givenLine);
                if (distance >= yDistanceProjectionWithGivenLine) {
                    int xDistanceProjectionWithGivenLine = distance - yDistanceProjectionWithGivenLine;
                    IntStream.rangeClosed(sensorPosition.x - xDistanceProjectionWithGivenLine, sensorPosition.x + xDistanceProjectionWithGivenLine)
                            .forEach(intersectionsWithGivenLine::add);
                }
            }
        }
        return intersectionsWithGivenLine.stream().filter(Predicate.not(beaconOnTheGivenLine::contains)).count();
    }

    private static Optional<Position> findHiddenBeaconInRange(List<String> lines, int maxValue) {
        Map<Position, Integer> distancesFromSensor = new HashMap<>();

        for (String line : lines) {
            Matcher matcher = SENSOR.matcher(line);
            if (matcher.matches()) {
                Position sensorPosition = new Position(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                Position beaconPosition = new Position(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
                int distance = Math.abs(beaconPosition.x - sensorPosition.x) + Math.abs(beaconPosition.y - sensorPosition.y);
                distancesFromSensor.put(sensorPosition, distance);
            }
        }

        for (int y = 0; y <= maxValue; y++) {
            SortedSet<Interval> intervals = new TreeSet<>();
            for (Map.Entry<Position, Integer> distanceFromSensor : distancesFromSensor.entrySet()) {
                Position sensorPosition = distanceFromSensor.getKey();
                int distance = distanceFromSensor.getValue();
                int yDistanceProjectionWithGivenLine = Math.abs(sensorPosition.y - y);
                if (distance >= yDistanceProjectionWithGivenLine) {
                    int xDistanceProjectionWithGivenLine = distance - yDistanceProjectionWithGivenLine;
                    intervals.add(new Interval(sensorPosition.x - xDistanceProjectionWithGivenLine, sensorPosition.x + xDistanceProjectionWithGivenLine));
                }
            }

            Interval current = null;
            for (Interval next: intervals) {
                if (current != null) {
                    if (current.end >= next.start) {
                        if (next.end > current.end) {
                            current = next;
                        }
                    } else {
                        int x = current.end + 1;
                        if (x >= 0 && x <= maxValue) {
                            return Optional.of(new Position(x,y));
                        } else {
                            current = next;
                        }
                    }
                } else {
                    current = next;
                    if (current.start > 0) {
                        return Optional.of(new Position(0, y));
                    }
                }
            }
            if (current != null && current.end < 4000000L) {
                return Optional.of(new Position(current.end + 1, y));
            }
        }
        return Optional.empty();
    }

    record Position(int x, int y) {
        public long getTuningFrequency() {
            return x * 4000000L + y;
        }
    }

    record Interval(int start, int end) implements Comparable<Interval>{

        @Override
        public int compareTo(Interval o) {
            int compareStart = Integer.compare(start, o.start);
            if (compareStart != 0) {
                return compareStart;
            } else {
                return Integer.compare(end, o.end);
            }
        }
    }
}