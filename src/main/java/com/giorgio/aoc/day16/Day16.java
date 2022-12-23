package com.giorgio.aoc.day16;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day16 {
    private final static Pattern VALVE = Pattern.compile("Valve ([A-Z]+) has flow rate=([0-9]+); tunnels? leads? to valves? ([A-Z]+(, [A-Z]+)*)");
    private static final List<String> valveNames = new ArrayList<>();
    private static final Map<String, Integer> cache = new HashMap<>();
    private static int[][] distance;
    private static Map<String, Valve> valves;

    public static void main(String[] args) throws IOException {
        valves = new HashMap<>();
        Path path = Paths.get("src/main/resources/data/day16/input.txt");
        List<String> lines = Files.readAllLines(path);

        for (String line: lines) {
            Matcher matcher = VALVE.matcher(line);
            if (matcher.matches()) {
                String valveName = matcher.group(1);
                valveNames.add(valveName);
                int flowRate = Integer.parseInt(matcher.group(2));
                List<String> leadTo = Arrays.stream(matcher.group(3).split(",")).map(String::trim).toList();
                Valve valve = new Valve(valveName, flowRate, leadTo);
                valves.put(valveName, valve);
            }
        }

        distance = new int[valveNames.size()][];
        for (int i = 0; i < valveNames.size(); i++) {
            distance[i] = new int[valveNames.size()];
            for (int j = 0; j < valveNames.size(); j++) {
                distance[i][j] = Integer.MAX_VALUE;
            }
        }
        for (int i = 0; i < valveNames.size(); i++) {
            Valve valve = valves.get(valveNames.get(i));
            for (String to: valve.leadTo) {
                int j = valveNames.indexOf(to);
                distance[i][j] = 1;
            }
        }

        for (int k = 0; k < valveNames.size(); k++) {
            for (int i = 0; i < valveNames.size(); i++) {
                for (int j = 0; j < valveNames.size(); j++) {
                    if (distance[i][k] != Integer.MAX_VALUE && distance[k][j] != Integer.MAX_VALUE) {
                        distance[i][j] = Math.min(distance[i][j], distance[i][k] + distance[k][j]);
                    }
                }
            }
        }

        System.out.println(computePressure("AA", 30, valveNames.stream().filter(v -> valves.get(v).flowRate > 0).collect(Collectors.toSet()), false));
        System.out.println(computePressure("AA", 26, valveNames.stream().filter(v -> valves.get(v).flowRate > 0).collect(Collectors.toSet()), true));
    }

    private static int computePressure(String currentValve, int remainingTime, Set<String> valvesToOpen, boolean withElephant) {
        int result = withElephant ? computePressure("AA", 26, valvesToOpen, false) : 0;
        for (String nextValve: valvesToOpen) {
            int remainingTimeAfterOpeningValve = remainingTime - distance[valveNames.indexOf(currentValve)][valveNames.indexOf(nextValve)] - 1;
            if (remainingTimeAfterOpeningValve >= 0) {
                final Set<String> remainingSet = valvesToOpen.stream().filter(Predicate.not(nextValve::equals)).collect(Collectors.toSet());
                result = Math.max(result, valves.get(nextValve).flowRate * remainingTimeAfterOpeningValve + executeCaching(nextValve, remainingTimeAfterOpeningValve, remainingSet, withElephant, Day16::computePressure));
            }
        }
        return result;
    }

    private static int executeCaching(String valve, int time, Set<String> set, boolean withElephant, computePressureInterface function) {
        final String key = valve + time + set + withElephant;
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            int result = function.computePressure(valve, time, set, withElephant);
            cache.put(key, result);
            return result;
        }
    }

    record Valve(String name, int flowRate, List<String> leadTo) {
    }

    private interface computePressureInterface {
        int computePressure(String s, int t, Set<String> set, boolean isElephant);
    }
}