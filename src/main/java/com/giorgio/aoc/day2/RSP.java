package com.giorgio.aoc.day2;

import java.util.Arrays;
import java.util.Map;

public enum RSP {
    ROCK("A", "X", 1),
    PAPER("B", "Y", 2),
    SCISSORS("C", "Z", 3);
    private final String opponent;
    private final String mine;
    private final int point;
    private static final Map<RSP,RSP> beat = Map.of(
            ROCK, SCISSORS,
            PAPER, ROCK,
            SCISSORS, PAPER);

    RSP(String opponent, String mine, int point) {
        this.opponent = opponent;
        this.mine = mine;
        this.point = point;
    }
    public static RSP opponent(String opponent) {
        return Arrays.stream(values()).filter(it -> it.opponent.equals(opponent)).findAny().orElse(null);
    }

    public static RSP mine(String mine) {
        return Arrays.stream(values()).filter(it -> it.mine.equals(mine)).findAny().orElse(null);
    }

    public static RSP neededToBeat(RSP opponent) {
        return needTo(opponent, 6);
    }

    public static RSP neededToDraw(RSP opponent) {
        return needTo(opponent, 3);
    }

    public static RSP neededToLose(RSP opponent) {
        return needTo(opponent, 0);
    }

    private static RSP needTo(RSP opponent, int point) {
        return Arrays.stream(RSP.values()).filter(it -> it.beat(opponent) == point).findAny().orElse(null);
    }

    public int getPoint() {
        return point;
    }

    public int beat(RSP opponent) {
        if (this == opponent) {
            return 3;
        }
        if (beat.get(this) == opponent) {
            return 6;
        }
        return 0;
    }
}
