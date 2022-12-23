package com.giorgio.aoc.day23;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day23 {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/data/day23/input.txt");
        List<String> lines = Files.readAllLines(path);


        System.out.println(partOne(lines));

        System.out.println(partTwo(lines));
    }

    private static Engine getEngine(Set<Position> elves) {
        Queue<Rule> rules = new LinkedList<>();
        rules.add(new Rule(position -> position.neighboursNorth().noneMatch(elves::contains), Position::moveNorth));
        rules.add(new Rule(position -> position.neighboursSouth().noneMatch(elves::contains), Position::moveSouth));
        rules.add(new Rule(position -> position.neighboursWest().noneMatch(elves::contains), Position::moveWest));
        rules.add(new Rule(position -> position.neighboursEast().noneMatch(elves::contains), Position::moveEast));
        Engine engine = new Engine(rules);
        return engine;
    }

    private static Set<Position> getElves(List<String> lines) {
        Set<Position> elves = new HashSet<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == '#') {
                    elves.add(new Position(j, i));
                }
            }
        }
        return elves;
    }

    private static int partOne(List<String> lines) {
        Set<Position> elves = getElves(lines);
        Engine engine = getEngine(elves);
        for (int i = 0; i < 10; i++) {
            List<Move> candidateMoves = elves.stream()
                    .filter(elf -> elf.neighbours().anyMatch(elves::contains))
                    .flatMap(elf -> engine.selectMove(elf).stream())
                    .toList();
            List<Move> moves = candidateMoves.stream()
                    .filter(move -> candidateMoves.stream().filter(m -> m.to().equals(move.to())).count() == 1)
                    .toList();
            moves.stream().map(Move::from).toList().forEach(elves::remove);
            elves.addAll(moves.stream().map(Move::to).toList());
            engine.rotateRules();
            //draw(elves, i);
        }
        int x0 = elves.stream().mapToInt(Position::x).min().orElseThrow();
        int y0 = elves.stream().mapToInt(Position::y).min().orElseThrow();

        int x1 = elves.stream().mapToInt(Position::x).max().orElseThrow();
        int y1 = elves.stream().mapToInt(Position::y).max().orElseThrow();

        int emptySquares = ((x1 - x0 + 1) * (y1 - y0 + 1)) - elves.size();
        return emptySquares;
    }

    private static int partTwo(List<String> lines) {
        Set<Position> elves = getElves(lines);
        Engine engine = getEngine(elves);
        int i = 1;
        while(true) {
            List<Move> candidateMoves = elves.stream()
                    .filter(elf -> elf.neighbours().anyMatch(elves::contains))
                    .flatMap(elf -> engine.selectMove(elf).stream())
                    .toList();
            List<Move> moves = candidateMoves.stream()
                    .filter(move -> candidateMoves.stream().filter(m -> m.to().equals(move.to())).count() == 1)
                    .toList();
            if (moves.isEmpty()) {
                return i;
            }
            moves.stream().map(Move::from).toList().forEach(elves::remove);
            elves.addAll(moves.stream().map(Move::to).toList());
            engine.rotateRules();
            i++;
            // draw(elves, i);

        }
    }

    private static void draw(Set<Position> elves, int i) {
        int x0 = elves.stream().mapToInt(Position::x).min().orElseThrow();
        int y0 = elves.stream().mapToInt(Position::y).min().orElseThrow();

        int x1 = elves.stream().mapToInt(Position::x).max().orElseThrow();
        int y1 = elves.stream().mapToInt(Position::y).max().orElseThrow();
        System.out.println(i + "  ---");
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                if (elves.contains(new Position(x,y))) {
                    System.out.print('#');
                } else {
                    System.out.print('_');
                }
            }
            System.out.println();
        }
        System.out.println("  ---");
    }

    record Rule(Predicate<Position> condition, Function<Position, Position> decision) {
    }

    record Engine(Queue<Rule> rules) {
        public Optional<Move> selectMove(Position position) {
            return rules.stream()
                    .filter(rule -> rule.condition().test(position))
                    .map(rule -> rule.decision().apply(position))
                    .map(newPosition -> new Move(position, newPosition))
                    .findFirst();
        }

        public void rotateRules() {
            rules.offer(rules.poll());
        }
    }

    record Position(int x, int y) {
        Stream<Position> neighbours() {
            return IntStream.rangeClosed(-1, 1).boxed()
                    .flatMap(i -> IntStream.rangeClosed(-1, 1)
                            .mapToObj(j -> new Position(x + i, y + j))
                            .filter(Predicate.not(this::equals)));

        }

        Position moveNorth() {
            return new Position(x, y - 1);
        }

        Position moveSouth() {
            return new Position(x, y + 1);
        }

        Position moveEast() {
            return new Position(x + 1, y);
        }

        Position moveWest() {
            return new Position(x - 1, y);
        }

        Stream<Position> neighboursNorth() {
            return IntStream.rangeClosed(-1, 1)
                    .mapToObj(j -> new Position(x + j, y - 1));
        }

        Stream<Position> neighboursSouth() {
            return IntStream.rangeClosed(-1, 1)
                    .mapToObj(j -> new Position(x + j, y + 1));
        }

        Stream<Position> neighboursEast() {
            return IntStream.rangeClosed(-1, 1)
                    .mapToObj(j -> new Position(x + 1, y + j));
        }

        Stream<Position> neighboursWest() {
            return IntStream.rangeClosed(-1, 1)
                    .mapToObj(j -> new Position(x - 1, y + j));
        }
    }

    record Move(Position from, Position to) {
    }
}