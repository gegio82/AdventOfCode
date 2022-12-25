package com.giorgio.aoc.day25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Day25 {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/data/day25/input.txt");
        List<String> lines = Files.readAllLines(path);
        long total = lines.stream().mapToLong(Day25::fromSnafuNumber).sum();
        String totalToSnafu = toSnafuNumber(total);
        System.out.println(totalToSnafu);

    }

    static long fromSnafuNumber(String snafuNumber) {
        long value = 0;
        for (int i = 0; i < snafuNumber.length(); i++) {
            char digit = snafuNumber.charAt(snafuNumber.length() - 1 - i);
            long digitOrder = pow(5, i);
            long digitValue = switch (digit) {
                case '=' -> -2;
                case '-' -> -1;
                case '0' -> 0;
                case '1' -> 1;
                case '2' -> 2;
                default -> throw new IllegalArgumentException();
            } * digitOrder;
            value += digitValue;
        }
        return value;
    }

    static String toBase5(int number) {
        return Integer.toString(10, 5);
    }

    static String toSnafuNumber(long number) {
        StringBuilder sb = new StringBuilder();
        String base5 = Long.toString(number, 5);
        int riporto = 0;
        for (int i = base5.length() - 1; i >= 0; i-- ) {
            char atI = (char)((int)base5.charAt(i) + riporto);
            if (atI == '3') {
                sb.insert(0, '=');
                riporto = 1;
            } else if (atI == '4') {
                sb.insert(0, '-');
                riporto = 1;
            } else if (atI == '5') {
                sb.insert(0, '0');
                riporto = 1;
            } else {
                sb.insert(0, atI);
                riporto = 0;
            }
        }
        if (riporto == 1) {
            sb.insert(0, 1);
        }
        return sb.toString();
    }

    private static long pow(long base, int exp) {
        if (exp == 0) {
            return 1;
        }
        if (exp == 1) {
            return base;
        }
        return base * (pow(base, exp - 1));
    }

}