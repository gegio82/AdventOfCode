package com.giorgio.aoc.day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private final static Pattern MONKEY = Pattern.compile("^Monkey ([0-9]+):$");
    private final static Pattern STARTING_ITEMS = Pattern.compile("^\\s+Starting items: ([\\d+,\\s]*)$");
    private final static Pattern OPERATION = Pattern.compile("^\\s+Operation: new = old ([-+*/]) (.+)$");
    private final static Pattern TEST = Pattern.compile("^\\s+Test: divisible by (\\d+)$");
    private final static Pattern IF_TRUE_THROW_TO = Pattern.compile("^\\s+If true: throw to monkey (\\d+)$");
    private final static Pattern IF_FALSE_THROW_TO = Pattern.compile("^\\s+If false: throw to monkey (\\d+)$");


    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/Users/ggerosa/Exercises/AdventOfCode/src/main/resources/data/day11/input.txt");
        List<String> instructions = Files.readAllLines(path);
        System.out.println("first: " + execute1(instructions));
        System.out.println("second: " + execute2(instructions));
    }

    public static long execute1(List<String> instructions) {
        List<Monkey> monkeys = initMonkeys(instructions);
        return computeMonkeyBusinessLevel(monkeys, 20, w -> w / 3);
    }

    public static long execute2(List<String> instructions) {
        List<Monkey> monkeys = initMonkeys(instructions);
        int mod = monkeys.stream().mapToInt(m -> m.test).distinct().reduce(1, (a, b) -> a * b);
        return computeMonkeyBusinessLevel(monkeys, 10000, w -> w % mod);
    }

    private static Long computeMonkeyBusinessLevel(List<Monkey> monkeys,
                                                   int rounds,
                                                   Function<Long, Long> mitigationFunction) {
        Map<Integer, Integer> inspections = new HashMap<>();

        for (int i = 0; i < rounds; i++) {
            for (Monkey monkey : monkeys) {
                int inspected = 0;
                while (!monkey.items.isEmpty()) {
                    inspected++;
                    long worryLevel = monkey.operator
                            .andThen(mitigationFunction)
                            .apply(monkey.items.poll());
                    int throwTo = monkey.throwTo(worryLevel);
                    monkeys.get(throwTo).items.offer(worryLevel);
                }
                if (inspected > 0) {
                    int oldInspected = inspections.getOrDefault(monkey.number, 0);
                    inspections.put(monkey.number, oldInspected + inspected);
                }
            }
        }
        return inspections.values().stream().sorted(Comparator.reverseOrder()).limit(2).map(Integer::longValue).reduce(1L, (a, b) -> a * b);
    }

    private static List<Monkey> initMonkeys(List<String> instructions) {
        List<Monkey> monkeys = new ArrayList<>();
        for (int i = 0; i < instructions.size(); i++) {
            final Matcher monkeyMatcher = MONKEY.matcher(instructions.get(i++));
            if (monkeyMatcher.matches()) {
                int monkeyId = Integer.parseInt(monkeyMatcher.group(1));
                final Matcher itemsMatcher = STARTING_ITEMS.matcher(instructions.get(i++));
                if (itemsMatcher.matches()) {
                    List<Long> startingItems = Arrays.stream(itemsMatcher.group(1).split(","))
                            .map(String::trim)
                            .map(Long::parseLong)
                            .toList();
                    Function<Long, Long> operation = parseOperation(instructions.get(i++));
                    final Matcher testMatcher = TEST.matcher(instructions.get(i++));
                    if (testMatcher.matches()) {
                        int test = Integer.parseInt(testMatcher.group(1));
                        final Matcher conditionMatchedMatcher = IF_TRUE_THROW_TO.matcher(instructions.get(i++));
                        if (conditionMatchedMatcher.matches()) {
                            int monkeyWhenCondition = Integer.parseInt(conditionMatchedMatcher.group(1));
                            final Matcher conditionNotMatchedMatcher = IF_FALSE_THROW_TO.matcher(instructions.get(i++));
                            if (conditionNotMatchedMatcher.matches()) {
                                int monkeyWhenNotCondition = Integer.parseInt(conditionNotMatchedMatcher.group(1));
                                Monkey monkey = new Monkey(monkeyId, startingItems, operation, test, monkeyWhenCondition, monkeyWhenNotCondition);
                                monkeys.add(monkey);
                            }
                        }
                    }
                }
            }
        }
        return monkeys;
    }

    private static Function<Long, Long> parseOperation(String line) {
        Matcher matcher = OPERATION.matcher(line);
        if (matcher.matches()) {

            return switch (matcher.group(1)) {
                case "-" -> x -> x - operand(matcher.group(2), x);
                case "+" -> x -> x + operand(matcher.group(2), x);
                case "*" -> x -> x * operand(matcher.group(2), x);
                case "/" -> x -> x / operand(matcher.group(2), x);
                default -> throw new IllegalArgumentException();
            };
        }
        throw new IllegalArgumentException();
    }

    private static long operand(String value, long x) {
        if ("old".equals(value)) {
            return x;
        }
        return Long.parseLong(value);
    }

    static class Monkey {
        private final int number;
        private final Queue<Long> items;
        private final Function<Long, Long> operator;
        private final int test;
        private final int monkeyWhenCondition;
        private final int monkeyWhenNotCondition;

        public Monkey(int number, List<Long> startingItems, Function<Long, Long> operator, int test, int monkeyWhenCondition, int monkeyWhenNotCondition) {
            this.number = number;
            this.items = new LinkedList<>(startingItems);
            this.operator = operator;
            this.test = test;
            this.monkeyWhenCondition = monkeyWhenCondition;
            this.monkeyWhenNotCondition = monkeyWhenNotCondition;
        }

        public int throwTo(long worryLevel) {
            if (worryLevel % test == 0) {
                return monkeyWhenCondition;
            } else {
                return monkeyWhenNotCondition;
            }
        }
    }
}





