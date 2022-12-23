package com.giorgio.aoc.day20;

import com.giorgio.aoc.common.data.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;

public class Day20 {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/data/day20/input.txt");
        List<String> lines = Files.readAllLines(path);
        System.out.println(partOne(lines));
        System.out.println(partTwo(lines));
    }

    private static long partOne(List<String> lines) {

        Queue<Pair<Integer, Long>> queue = new LinkedList<>();
        for (int i = 0; i < lines.size(); i++) {
            queue.add(new Pair<>(i, Long.parseLong(lines.get(i))));
        }
        for (int i = 0; i < lines.size(); i++) {
            rotateTillIndexOnTop(queue, i);
            moveTop(queue);
        }
        var list = queue.stream().map(Pair::second).toList();
        return IntStream.rangeClosed(1,3).mapToLong(i -> list.get((list.indexOf(0L) + i * 1000) % list.size())).sum();
    }

    private static long partTwo(List<String> lines) {
        Queue<Pair<Integer, Long>> queue = new LinkedList<>();
        for (int i = 0; i < lines.size(); i++) {
            queue.add(new Pair<>(i, Long.parseLong(lines.get(i)) * 811589153L));
        }
        for (int k = 0; k < 10; k++) {
            for (int index = 0; index < lines.size(); index++) {
                rotateTillIndexOnTop(queue, index);
                moveTop(queue);
            }
        }
        var list = queue.stream().map(Pair::second).toList();
        return IntStream.rangeClosed(1, 3).mapToLong(i -> list.get((list.indexOf(0L) + i * 1000) % list.size())).sum();
    }

    static void moveTop(Queue<Pair<Integer, Long>> queue) {
        var top = queue.poll();
        var elementsToPop = top.second() % (queue.size());
        if (elementsToPop < 0) {
            elementsToPop += queue.size();
        }
        for (int j = 0; j < elementsToPop; j++) {
            queue.add(queue.poll());
        }
        queue.add(top);
    }

    static void rotateTillIndexOnTop(Queue<Pair<Integer, Long>> queue, int index) {
        while(queue.peek().first() != index) {
            queue.add(queue.poll());
        }
    }
}
