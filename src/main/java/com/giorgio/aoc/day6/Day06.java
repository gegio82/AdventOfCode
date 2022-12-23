package com.giorgio.aoc.day6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day06 {

    public static void main(String[] args) throws IOException {
        System.out.println(execute(4));
        System.out.println(execute(14));
    }

    public static int execute(int charNumber) throws IOException {
        String line = Files.readString(Paths.get("src/main/resources/data/day6/input.txt"));
        int position = -1;
        for (int i = 0; i < line.length() - charNumber; i++) {
            if (line.substring(i, i + charNumber).chars().distinct().count() == charNumber) {
                position = i + charNumber;
                break;
            }
        }
        return position;
    }

}
