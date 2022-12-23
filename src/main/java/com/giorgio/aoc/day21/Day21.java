package com.giorgio.aoc.day21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day21 {
    private final static Pattern NUMBER = Pattern.compile("([a-z]+): (\\d+)");
    private final static Pattern OPERATION = Pattern.compile("([a-z]+): ([a-z]+) ([-+*/]) ([a-z]+)");


    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/data/day21/input.txt");
        List<String> lines = Files.readAllLines(path);
        Map<String, Expression> monkeys = new HashMap<>();
        for (String line : lines) {
            Matcher numberMatcher = NUMBER.matcher(line);
            if (numberMatcher.matches()) {
                String name = numberMatcher.group(1);
                Number number = new Number(numberMatcher.group(2));
                monkeys.put(name, number);
                continue;
            }
            Matcher expressionMatcher = OPERATION.matcher(line);
            if (expressionMatcher.matches()) {
                String name = expressionMatcher.group(1);
                String op1 = expressionMatcher.group(2);
                String operator = expressionMatcher.group(3);
                String op2 = expressionMatcher.group(4);
                Operation operation = new Operation(op1, op2, operator, monkeys);
                monkeys.put(name, operation);
            }
        }
        System.out.println(monkeys.get("root").value());

        Operation root = (Operation) monkeys.get("root");
        System.out.println(root.forceEqual("humn"));
    }

    interface Expression {
        Long value();
    }

    record Number(Long value) implements Expression {
        public Number(String value) {
            this(Long.parseLong(value));
        }
    }

    record Operation(String op1, String op2, String operator,
                     Map<String, Expression> expressions) implements Expression {

        @Override
        public Long value() {
            Long operand1 = expressions.get(op1).value();
            Long operand2 = expressions.get(op2).value();
            return switch (operator) {
                case "+": yield operand1 + operand2;
                case "-": yield operand1 - operand2;
                case "*": yield operand1 * operand2;
                case "/": yield operand1 / operand2;
                default: throw new IllegalArgumentException();
            };
        }

        public Long forceEqual(String monkeyToChange) {
            if (expressions.get(op1) instanceof Operation operand1) {
                if (operand1.uses(monkeyToChange)) {
                    Long operand2 = expressions.get(op2).value();
                    return operand1.reverse(monkeyToChange, operand2);
                }
            } else if (expressions.get(op1) instanceof Operation operand2) {
                if (operand2.uses(monkeyToChange)) {
                    Long operand1 = expressions.get(op2).value();
                    return operand2.reverse(monkeyToChange, operand1);
                }
            }
            return null;
        }

        private boolean uses(String name) {
            if (List.of(op1, op2).contains(name)) {
                return true;
            }
            return Stream.of(op1, op2)
                    .map(expressions::get)
                    .filter(Operation.class::isInstance)
                    .map(Operation.class::cast)
                    .anyMatch(o -> o.uses(name));
        }

        private Long reverse(String monkeyToChange, Long expectedValue) {
            if (expressions.get(op1) instanceof Operation operand1) {
                if (operand1.uses(monkeyToChange)) {
                    Long operand2 = expressions.get(op2).value();
                    return operand1.reverse(monkeyToChange, reverseFirstOperand(expectedValue, operand2));
                }
            }
            if (expressions.get(op2) instanceof Operation operand2) {
                if (operand2.uses(monkeyToChange)) {
                    Long operand1 = expressions.get(op1).value();
                    return operand2.reverse(monkeyToChange, reverseSecondOperand(expectedValue, operand1));
                }
            }
            if (op1.equals(monkeyToChange)) {
                Long operand2 = expressions.get(op2).value();
                return reverseFirstOperand(expectedValue, operand2);
            }
            if (op2.equals(monkeyToChange)) {
                Long operand1 = expressions.get(op1).value();
                return reverseSecondOperand(expectedValue, operand1);
            }
            return expectedValue;
        }

        private Long reverseSecondOperand(Long expectedValue, Long operand1) {
            return switch (operator) {
                case "+": yield expectedValue - operand1;
                case "-": yield operand1 - expectedValue;
                case "*": yield expectedValue / operand1;
                case "/": yield operand1 / expectedValue;
                default: throw new IllegalArgumentException();
            };
        }

        private Long reverseFirstOperand(Long expectedValue, Long operand2) {
            return switch (operator) {
                case "+": yield expectedValue - operand2;
                case "-": yield expectedValue + operand2;
                case "*": yield expectedValue / operand2;
                case "/": yield expectedValue * operand2;
                default: throw new IllegalArgumentException();
            };
        }
    }
}