package com.giorgio.aoc.day13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Day13 {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/data/day13/input.txt");
        List<String> lines = Files.readAllLines(path);
        partOne(lines);
        partTwo(lines);
    }

    private static void partOne(List<String> lines) {
        List<List<Object>> toCompare = new ArrayList<>();
        int index = 1;
        int answer = 0;
        for(String line: lines) {
            if  (line.isBlank()) {
                continue;
            }
            toCompare.add(parseInput(line));
            if (toCompare.size() == 2) {
                final int result = compare(toCompare.get(0), toCompare.get(1));
                if (result == -1) {
                    answer += index;
                }
                toCompare.clear();
                index++;
            }
        }
        System.out.println(answer);
    }

    private static void partTwo(List<String> lines) {
        List<List<Object>> allLists = new ArrayList<>();
        List<Object> FIRST_DELIMITER = List.of(List.of(2));
        List<Object> SECOND_DELIMITER = List.of(List.of(6));
        for(String line: lines) {
            if  (line.isBlank()) {
                continue;
            }
            allLists.add(parseInput(line));
        }
        allLists.add(FIRST_DELIMITER);
        allLists.add(SECOND_DELIMITER);

        allLists.sort(Day13::compare);

        System.out.println((allLists.indexOf(FIRST_DELIMITER) + 1) * (allLists.indexOf(SECOND_DELIMITER) + 1));
    }

    private static int compare(List<Object> firstList, List<Object> secondList) {
        for (int i = 0; i < firstList.size() && i < secondList.size(); i++) {
            Object elementFirstList = firstList.get(i);
            Object elementSecondList = secondList.get(i);
            int compared = 0;
            if (elementFirstList instanceof Integer && elementSecondList instanceof Integer) {
                compared = ((Integer)elementFirstList).compareTo((Integer) elementSecondList);
            } else if (elementFirstList instanceof List && elementSecondList instanceof Integer) {
                compared = compare((List<Object>) elementFirstList, List.of(elementSecondList));
            } else if (elementFirstList instanceof Integer && elementSecondList instanceof List) {
                compared = compare(List.of(elementFirstList), (List<Object>)elementSecondList);
            } else if (elementFirstList instanceof List && elementSecondList instanceof List) {
                compared = compare((List<Object>)elementFirstList, (List<Object>)elementSecondList);
            }
            if (compared != 0) {
                return compared;
            }
        }
        return Integer.compare(firstList.size(), secondList.size());
    }

    private static List<Object> parseInput(String input) {
        Stack<List<Object>> stack = new Stack<>();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ',') {
                //Do nothing
            } else if (input.charAt(i) == '[') {
                final ArrayList<Object> sublist = new ArrayList<>();
                if (!stack.isEmpty()) {
                    stack.peek().add(sublist);
                }

                stack.push(sublist);
            } else if (input.charAt(i) == ']') {
                List<Object> list = stack.pop();
                if (stack.isEmpty()) {
                    return list;
                }
            } else {
                StringBuilder number = new StringBuilder();
                while (Character.isDigit(input.charAt(i))) {
                    number.append(input.charAt(i));
                    i++;
                }
                i--;
                stack.peek().add(Integer.parseInt(number.toString()));
            }
        }
        return null;
    }
}