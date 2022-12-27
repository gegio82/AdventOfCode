package com.giorgio.aoc.day24;

import com.giorgio.aoc.common.data.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public class Day24 {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/data/day24/input.txt");
        List<String> lines = Files.readAllLines(path);
        Set<Position> walls = new HashSet<>();
        Set<Pair<Direction, Position>> blizzards = new HashSet<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == '#') {
                    walls.add(new Position(j, i));
                } else if (line.charAt(j) != '.') {
                    blizzards.add(new Pair<>(Direction.parse(line.charAt(j)), new Position(j, i)));
                }
            }
        }
        System.out.println("Part One: " + partOne(walls, blizzards));

        System.out.println("Part Two: " +partTwo(walls, blizzards));
    }

    private static int partOne(Set<Position> walls, Set<Pair<Direction, Position>> blizzards) {
        Position startPosition = new Position(1, 0);
        Position endPosition = new Position(
                walls.stream().mapToInt(Position::x).max().orElseThrow() - 1,
                walls.stream().mapToInt(Position::y).max().orElseThrow());
        return computeTimeToReachDestination(walls, blizzards, startPosition, endPosition, 0);
    }

    private static int partTwo(Set<Position> walls, Set<Pair<Direction, Position>> blizzards) {
        Position startPosition = new Position(1, 0);
        Position endPosition = new Position(
                walls.stream().mapToInt(Position::x).max().orElseThrow() - 1,
                walls.stream().mapToInt(Position::y).max().orElseThrow());
        int time = computeTimeToReachDestination(walls, blizzards, startPosition, endPosition, 0);
        time = computeTimeToReachDestination(walls, blizzards, endPosition, startPosition, time);
        return computeTimeToReachDestination(walls, blizzards, startPosition, endPosition, time);
    }

    private static int computeTimeToReachDestination(Set<Position> walls, Set<Pair<Direction, Position>> blizzards,
                                                     Position startPosition, Position endPosition, int initialTime) {
        assert (!walls.contains(startPosition));
        assert (!walls.contains(endPosition));
        int maxX = walls.stream().mapToInt(Position::x).max().orElseThrow() - 1;
        int maxY = walls.stream().mapToInt(Position::y).max().orElseThrow() - 1;
        List<Position> EXITS = List.of(new Position (1, 0), new Position(maxX, maxY + 1));

        /*for (int i = 0; i < 10; i++ ) {
            final int time = i;
            System.out.println("--- Time " + i + " ---");
            Set<Pair<Direction, Position>> nextBlizzards = blizzards.stream()
                    .map(pair -> moveBlizzard(pair.first(), pair.second(), maxX, maxY, time))
                    .collect(Collectors.toSet());
            draw(endPosition, walls, nextBlizzards);
        }*/

        Set<Pair<Position, Integer>> seen = new HashSet<>();
        PriorityQueue<Pair<Position, Integer>> queue = new PriorityQueue<>((s1, s2) -> {
            int score1 = s1.second() + Math.abs(s1.first().x() - endPosition.x()) + Math.abs(s1.first().y() - endPosition.y());
            int score2 = s2.second() + Math.abs(s2.first().x() - endPosition.x()) + Math.abs(s2.first().y() - endPosition.y());
            return Integer.compare(score1, score2);
        });
        queue.add(new Pair<>(startPosition, initialTime));
        while (!queue.isEmpty()) {
            Pair<Position, Integer> status = queue.poll();
            if (!seen.add(status)) {
                continue;
            }
            Position currentPosition = status.first();
            Integer time = status.second();
            if (currentPosition.equals(endPosition)) {
                return time;
            }

            Set<Position> currentBlizzardsAroundCurrentPosition = blizzards.stream()
                    .map(pair -> moveBlizzard(pair.first(), pair.second(), maxX, maxY, time + 1))
                    .map(Pair::second)
                    .filter(it -> it.x() >= currentPosition.x() - 1 && it.x() <= currentPosition.x() + 1)
                    .filter(it -> it.y() >= currentPosition.y() - 1 && it.y() <= currentPosition.y() + 1)
                    .collect(Collectors.toSet());

            Set<Position> nextPositions = Stream.of(
                            currentPosition,
                            new Position(currentPosition.x() - 1, currentPosition.y()),
                            new Position(currentPosition.x() + 1, currentPosition.y()),
                            new Position(currentPosition.x(), currentPosition.y() - 1),
                            new Position(currentPosition.x(), currentPosition.y() + 1))

                    .filter(position -> EXITS.contains(position) ||
                            (position.x() > 0 && position.y() > 0 && position.x() <= maxX && position.y() <= maxY))
                    .filter(not(currentBlizzardsAroundCurrentPosition::contains))
                    .collect(Collectors.toSet());
            for (Position nextPosition : nextPositions) {
                final Pair<Position, Integer> newStatus = new Pair<>(nextPosition, time + 1);
                queue.offer(newStatus);
            }
        }
        return 0;
    }

    static Pair<Direction, Position> moveBlizzard(Direction direction, Position position, int maxX, int maxY, int time) {
        Position newPosition = switch (direction) {
            case LEFT -> {
                int newX = (position.x() - 1 - time) % (maxX);
                if (newX < 0) {
                    newX += maxX;
                }
                newX++;
                yield new Position(newX, position.y());
            }
            case RIGHT -> new Position(((position.x() - 1 + time) % maxX) + 1, position.y());
            case UP -> {
                int newY = (position.y() - 1 - time) % (maxY);
                if (newY < 0) {
                    newY += maxY;
                }
                newY++;
                yield new Position(position.x(), newY);
            }
            case DOWN -> new Position(position.x(), ((position.y() - 1 + time) % maxY) + 1);
        };
        return new Pair<>(direction, newPosition);
    }

    private static void draw(Position me, Set<Position> walls, Set<Pair<Direction, Position>> blizzards) {
        int maxX = walls.stream().mapToInt(Position::x).max().orElse(0);
        int maxY = walls.stream().mapToInt(Position::y).max().orElse(0);
        for (int i = 0; i <= maxY; i++) {
            for (int j = 0; j <= maxX; j++) {
                final Position position = new Position(j, i);
                if (walls.contains(position)) {
                    System.out.print('#');
                } else if (blizzards.stream().map(Pair::second).anyMatch(position::equals)) {
                    Set<Direction> blizzardDirections = blizzards.stream()
                            .filter(it -> it.second().equals(position))
                            .map(Pair::first)
                            .collect(Collectors.toSet());
                    if (blizzardDirections.size() > 1) {
                        System.out.print(blizzardDirections.size());
                    } else {
                        System.out.print(blizzardDirections.stream().findFirst().map(Direction::getSymbol).orElseThrow());
                    }
                } else if (position.equals(me)) {
                    System.out.print('E');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
    }

    record Position(int x, int y) {}

    enum Direction {
        UP('^'), DOWN('v'), LEFT('<'), RIGHT('>');
        final char symbol;

        Direction(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        public static Direction parse(char symbol) {
            return switch (symbol) {
                case '^' -> UP;
                case 'v' -> DOWN;
                case '>' -> RIGHT;
                case '<' -> LEFT;
                default -> throw new IllegalArgumentException();
            };
        }
    }
}