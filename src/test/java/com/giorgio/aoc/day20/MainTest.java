package com.giorgio.aoc.day20;


import com.giorgio.aoc.common.data.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MainTest {

    @ParameterizedTest
    @MethodSource("paramsFor_moveTop")
    public void moveTop(int topValue, List<Long> expectedNewPosition){
        Queue<Pair<Integer,Long>> queue = new LinkedList<>();
        Stream.of(new Pair<>(0,(long)topValue),
                        new Pair<>(1, 2L),
                        new Pair<>(2, -3L),
                        new Pair<>(3, 3L),
                        new Pair<>(4, -2L),
                        new Pair<>(5, 0L),
                        new Pair<>(6, 4L)).forEach(queue::add);

        Day20.moveTop(queue);

        assertThat(print(queue)).containsExactly(expectedNewPosition.toArray(new Long[expectedNewPosition.size()]));
    }

    public static Stream<Arguments> paramsFor_moveTop() {
        return Stream.of(
                Arguments.of(1, List.of(0L, 4L, 2L, 1L, -3L, 3L, -2L)),
                Arguments.of(-1, List.of(0L, -1L, 4L, 2L, -3L, 3L, -2L)),
                Arguments.of(10, List.of(0L, 4L, 2L, -3L, 3L, -2L, 10L)),
                Arguments.of(-10, List.of(0L, 4L, 2L, -3L, -10L, 3L, -2L))
        );
    }

    private List<Long> print(Queue<Pair<Integer, Long>> queue) {
        while(queue.peek().second() != 0L) {
            queue.add(queue.poll());
        }
        return queue.stream().map(Pair::second).toList();

    }
}