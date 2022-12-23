package com.giorgio.aoc.day19;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day19 {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/data/day19/input.txt");
        List<String> lines = Files.readAllLines(path);
        System.out.println(partOne(lines));
        System.out.println(partTwo(lines));
    }

    public static int partOne(List<String> lines) {
        return lines.stream().map(BluePrint::parse)
                .map(blueprint -> new Engine(blueprint, 24))
                .mapToInt(ms -> ms.blueprint.id() * ms.simulate())
                .sum();
    }

    public static int partTwo(List<String> lines) {
        return lines.stream().map(BluePrint::parse)
                .limit(3)
                .map(blueprint -> new Engine(blueprint, 32))
                .mapToInt(Engine::simulate)
                .reduce(1,(a, b) -> a * b);
    }

    static final class Engine {
        private final BluePrint blueprint;
        private final int maxTime;
        private final int[] maxCosts;
        private int bestGeodes;

        public Engine(BluePrint blueprint, int maxTime) {
            this.blueprint = blueprint;
            this.maxTime = maxTime;
            this.maxCosts = IntStream.range(0, blueprint.costs[0].length)
                    .map(j -> Arrays.stream(blueprint.costs).mapToInt(ints -> ints[j]).max().orElse(0))
                    .toArray();
        }

        int simulate() {
            bestGeodes = 0;
            simulate(new int[4], new int[]{1, 0, 0, 0}, maxTime);
            return bestGeodes;
        }

        private int simulate(int[] resources, int[] robots, int remaining) {
            // Prune if we cannot mine more geodes than our current best (i.e. if we constantly made geode robots)
            int maxGeodes = resources[3] + remaining * robots[3] + (remaining * (remaining - 1)) / 2;
            if (maxGeodes <= bestGeodes) {
                return 0;
            }

            // Prune if we are out of time
            if (remaining == 0) {
                return resources[3];
            }

            // Make a geode robot if we have the resources right now
            if (resources[0] >= blueprint.costs[3][0] && resources[2] >= blueprint.costs[3][2]) {
                int[] nextResources = resources.clone();
                int[] nextRobots = robots.clone();
                for (int j = 0; j < 4; j++) {
                    nextResources[j] += robots[j] - blueprint.costs[3][j];
                }
                nextRobots[3]++;
                return simulate(nextResources, nextRobots, remaining - 1);
            }

            int best = resources[3] + robots[3] * remaining; // The most geodes we can get if we do nothing
            for (int i = 3; i >= 0; i--) {
                // Prune if we would not benefit from making the robot type
                if (i < 3 && robots[i] >= maxCosts[i]) {
                    continue;
                }

                // Prune if our time will expire before we can gather enough resources to make the robot type
                int delay = buildTime(blueprint.costs[i], resources, robots) + 1;
                if (delay > remaining - 1) {
                    continue;
                }

                // Recurse on building the robot type
                int[] nextResources = resources.clone();
                int[] nextRobots = robots.clone();
                for (int j = 0; j < 4; j++) {
                    nextResources[j] += robots[j] * delay - blueprint.costs[i][j];
                }
                nextRobots[i]++;
                best = Math.max(best, simulate(nextResources, nextRobots, remaining - delay));
            }
            bestGeodes = Math.max(bestGeodes, best);
            return best;
        }

        private static int buildTime(int[] costs, int[] resources, int[] robots) {
            int time = 0;
            for (int i = 0; i < 3; i++) {
                if (costs[i] == 0) {
                    continue;
                }
                if (costs[i] > 0 && robots[i] == 0) {
                    return Integer.MAX_VALUE;
                }
                time = Math.max(time, (costs[i] - resources[i] + robots[i] - 1) / robots[i]);
            }
            return time;
        }
    }

    record BluePrint(int id, int[][] costs) {

        private final static Pattern BLUEPRINT = Pattern.compile(
                "Blueprint (\\d+): Each ore robot costs (\\d+)+ ore. Each clay robot costs (\\d)+ ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");

        private static BluePrint parse(String line) {
            Matcher matcher = BLUEPRINT.matcher(line);
            if (matcher.matches()) {
                return new BluePrint(Integer.parseInt(matcher.group(1)),
                        new int[][] {
                                { Integer.parseInt(matcher.group(2)), 0, 0, 0 },
                                { Integer.parseInt(matcher.group(3)), 0, 0, 0 },
                                { Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)), 0, 0 },
                                { Integer.parseInt(matcher.group(6)), 0, Integer.parseInt(matcher.group(7)), 0 }
                        });
            }
            return null;
        }
    }
}
