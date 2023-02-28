package com.giorgio.aoc.day19;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day19Old {


    private final static Pattern BLUEPRINT = Pattern.compile("Blueprint (\\d+): Each ore robot costs (\\d+)+ ore. Each clay robot costs (\\d)+ ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day19/input.txt");
        List<String> lines = Files.readAllLines(path);
        System.out.println(lines.stream().map(Day19Old::parse).mapToLong(b -> (long)b.id() * evaluateBluePrint(b, 24)).sum());
        System.out.println(lines.stream().map(Day19Old::parse).limit(3).mapToLong(b -> evaluateBluePrint(b, 32)).reduce(1, (a, b) -> a * b));
    }

    private static int evaluateBluePrint(BluePrint bluePrint, int initialTime) {
        Status initialStatus = new Status(0,0,0,0,1,0,0,0,initialTime);
        Queue<Status> queue = new LinkedList<>();
        queue.add(initialStatus);
        Set<Status> seen = new HashSet<>();
        int answer = 0;
        int maxOreRobotsNeeded = bluePrint.maxOreCost();
        while (!queue.isEmpty()) {
            var status = queue.poll();
            if (status.amountGeode() > answer) {
                answer = status.amountGeode();
            }
            int time = status.time();
            if (time != 0) {
                int robotOre = Math.min(status.robotOre(), maxOreRobotsNeeded);
                int robotClay = Math.min(status.robotClay(), bluePrint.maxClayCost());
                int robotObsidian = Math.min(status.robotObsidian(), bluePrint.maxObsidianCost());
                int robotGeode = status.robotGeode();
                int amountOre = Math.min(status.amountOre(), maxOreRobotsNeeded * time - robotOre * (time - 1));
                int amountClay = Math.min(status.amountClay(), bluePrint.obsidianRobot().getClayCost() * time - robotClay * (time - 1));
                int amountObsidian = Math.min(status.amountObsidian(), bluePrint.geodeRobot().getObsidianCost() * time - robotObsidian * (time - 1));
                int amountGeode = status.amountGeode();
                status = new Status(amountOre, amountClay, amountObsidian, amountGeode, robotOre, robotClay, robotObsidian, robotGeode, time);
                if (!seen.contains(status)) {
                    seen.add(status);
                    Status s = new Status(amountOre + robotOre, amountClay + robotClay, amountObsidian + robotObsidian, amountGeode + robotGeode, robotOre, robotClay, robotObsidian, robotGeode, time - 1);
                    queue.add(s);
                    if (amountOre >= bluePrint.oreRobot().getOreCost()) {
                        s = new Status(amountOre - bluePrint.oreRobot().getOreCost() + robotOre, amountClay + robotClay, amountObsidian + robotObsidian, amountGeode + robotGeode, robotOre + 1, robotClay, robotObsidian, robotGeode, time - 1);
                        queue.add(s);
                    }
                    if (amountOre >= bluePrint.clayRobot().getOreCost()) {
                        s = new Status(amountOre - bluePrint.clayRobot().getOreCost() + robotOre, amountClay + robotClay, amountObsidian + robotObsidian, amountGeode + robotGeode, robotOre, robotClay + 1, robotObsidian, robotGeode, time - 1);
                        queue.add(s);
                    }
                    if (amountOre >= bluePrint.obsidianRobot().getOreCost()
                            && amountClay >= bluePrint.obsidianRobot.getClayCost()) {
                        s = new Status(amountOre - bluePrint.obsidianRobot().getOreCost() + robotOre, amountClay - bluePrint.obsidianRobot().getClayCost() + robotClay, amountObsidian + robotObsidian, amountGeode + robotGeode, robotOre, robotClay, robotObsidian + 1, robotGeode, time - 1);
                        queue.add(s);
                    }
                    if (amountOre >= bluePrint.geodeRobot().getOreCost()
                            && amountObsidian >= bluePrint.geodeRobot().getObsidianCost()) {
                        s = new Status(amountOre - bluePrint.geodeRobot().getOreCost() + robotOre, amountClay + robotClay, amountObsidian - bluePrint.geodeRobot().getObsidianCost() + robotObsidian, amountGeode + robotGeode, robotOre, robotClay, robotObsidian, robotGeode + 1, time - 1);
                        queue.add(s);
                    }
                }
            }

        }
        return answer;
    }

    private static BluePrint parse(String line) {
        Matcher matcher = BLUEPRINT.matcher(line);
        if (matcher.matches()) {
            int bluePrintId = Integer.parseInt(matcher.group(1));
            Robot oreRobot = new Robot(Integer.parseInt(matcher.group(2)));
            Robot clayRobot = new Robot(Integer.parseInt(matcher.group(3)));
            ObsidianRobot obsidianRobot = new ObsidianRobot(Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)));
            GeodeRobot geodeRobot = new GeodeRobot(Integer.parseInt(matcher.group(6)), Integer.parseInt(matcher.group(7)));
            return new BluePrint(bluePrintId,oreRobot, clayRobot, obsidianRobot, geodeRobot);
        }
        return null;
    }

    record BluePrint(int id, Robot oreRobot, Robot clayRobot, ObsidianRobot obsidianRobot, GeodeRobot geodeRobot) {
        public int maxOreCost() {
            return Stream.of(oreRobot, clayRobot, obsidianRobot, geodeRobot).mapToInt(Robot::getOreCost).max().orElse(0);
        }

        public int maxClayCost() {
            return obsidianRobot.getClayCost();
        }

        public int maxObsidianCost() {
            return geodeRobot.getObsidianCost();
        }
    }
    static class Robot {
        private final int oreCost;

        public Robot(int oreCost) {
            this.oreCost = oreCost;
        }

        @Override
        public String toString() {
            return "Robot[oreCost=" + oreCost+"]";
        }

        public int getOreCost() {
            return oreCost;
        }
    }

    static class ObsidianRobot extends Robot {
        private final int clayCost;

        public ObsidianRobot(int oreCost, int clayCost) {
            super(oreCost);
            this.clayCost = clayCost;
        }

        @Override
        public String toString() {
            return "Robot[oreCost=" + super.oreCost+", clayCost=" + clayCost + "]";
        }

        public int getClayCost() {
            return clayCost;
        }
    }

    static class GeodeRobot extends Robot {
        private final int obsidianCost;

        public GeodeRobot(int oreCost, int obsidianCost) {
            super(oreCost);
            this.obsidianCost = obsidianCost;
        }

        @Override
        public String toString() {
            return "Robot[oreCost=" + super.oreCost+", obsidianCost=" + obsidianCost + "]";
        }

        public int getObsidianCost() {
            return obsidianCost;
        }
    }
    record Status(int amountOre, int amountClay, int amountObsidian, int amountGeode,
                  int robotOre, int robotClay, int robotObsidian, int robotGeode, int time) {
    }
}