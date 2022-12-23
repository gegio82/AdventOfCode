package com.giorgio.aoc.day22;

import com.giorgio.aoc.common.data.Pair;
import com.giorgio.aoc.common.data.Triple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Day22 {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("src/main/resources/data/day22/input.txt");
        List<String> lines = Files.readAllLines(path);
        List<String> mapDescription = lines.subList(0, lines.size() - 2);
        String instructionList = lines.get(lines.size() - 1);
        Map map = new Map(mapDescription);
        List<Object> instructions = parseInstructions(instructionList);

        partOne(map, instructions);
        partTwo(map, instructions);
    }

    private static void partTwo(Map map, List<Object> instructions) {
        java.util.Map<Integer, Cube> cubes = map.defineCubes ();

        Direction direction = Direction.RIGHT;
        Position position = map.firstElementInRow(0);

        for (Object instruction: instructions) {
            if (instruction instanceof Integer howFar) {
                Pair<Position, Direction> moveResult = map.moveInCube(position, direction, howFar, cubes);
                position = moveResult.first();
                direction = moveResult.second();
            } else if (instruction instanceof Character d) {
                direction = direction.turn(d);
            }
        }

        System.out.println(position);
        System.out.println(direction);
        System.out.println(1000 * (position.y() + 1) + 4 * (position.x() + 1) + direction.value );
    }

    private static void partOne(Map map, List<Object> instructions) {
        Direction direction = Direction.RIGHT;
        Position position = map.firstElementInRow(0);
        for (Object instruction: instructions) {
            if (instruction instanceof Integer howFar) {
                position = map.move(position, direction, howFar);
            } else if (instruction instanceof Character d) {
                direction = direction.turn(d);
            }

        }

        System.out.println(position);
        System.out.println(direction);
        System.out.println(1000 * (position.y() + 1) + 4 * (position.x() + 1) + direction.value );
    }

    private static List<Object> parseInstructions(String instructionList) {
        List<Object> instructions = new LinkedList<>();
        String temp = "";
        for (int i = 0; i < instructionList.length(); i++) {
            char c = instructionList.charAt(i);
            if (Character.isDigit(c)) {
                temp += c;
            }
            if (c == 'R' || c == 'L') {
                if (!temp.isEmpty()) {
                    instructions.add(Integer.parseInt(temp));
                    temp = "";
                }
                instructions.add(c);
            }
        }
        instructions.add(Integer.parseInt(temp));
        return instructions;
    }

    enum Direction {
        UP(3), RIGHT(0), DOWN(1), LEFT(2);

        private static List<Direction> directions = Arrays.asList(Direction.values());

        private int value;

        Direction(int value) {
            this.value = value;
        }

        public Direction turnRight() {
            int index = directions.indexOf(this);
            return directions.get((index + 1) % directions.size());
        }

        public Direction turnLeft() {
            int index = directions.indexOf(this);
            return directions.get((index - 1 + directions.size()) % directions.size());
        }

        public Direction turn(char c) {
            if (c == 'R') {
                return turnRight();
            }
            if (c == 'L') {
                return turnLeft();
            }
            throw new IllegalArgumentException();
        }
    }

    record Position(int x, int y) {
        public boolean isInside(Cube cube) {
            return x >= cube.topLeft().x() && y >= cube.topLeft().y() && x < cube.topLeft().x() + cube.size() && y < cube.topLeft().y() + cube.size();
        }
    }

    record Map(char[][] map) {
        public Map(List<String> mapDescription) {
            this(parse(mapDescription));
        }

        public java.util.Map<Integer, Cube> defineCubes() {
            return java.util.Map.of(
                    5, new Cube(5, new Position(50,0),
                            java.util.Map.of(
                                    Direction.UP, new Pair<>(1, Direction.RIGHT),
                                    Direction.DOWN, new Pair<>(4, Direction.DOWN),
                                    Direction.LEFT, new Pair<>(2, Direction.RIGHT),
                                    Direction.RIGHT, new Pair<>(6, Direction.RIGHT))),
                    6, new Cube(6, new Position(100, 0),
                            java.util.Map.of(
                                    Direction.UP, new Pair<>(1, Direction.UP),
                                    Direction.DOWN, new Pair<>(4, Direction.LEFT),
                                    Direction.LEFT, new Pair<>(5, Direction.LEFT),
                                    Direction.RIGHT, new Pair<>(3, Direction.LEFT))),
                    4, new Cube(4, new Position(50, 50),
                            java.util.Map.of(
                                    Direction.UP, new Pair<>(5, Direction.UP),
                                    Direction.DOWN, new Pair<>(3, Direction.DOWN),
                                    Direction.LEFT, new Pair<>(2, Direction.DOWN),
                                    Direction.RIGHT, new Pair<>(6, Direction.UP))),
                    2, new Cube(2, new Position(0, 100),
                            java.util.Map.of(
                                    Direction.UP, new Pair<>(4, Direction.RIGHT),
                                    Direction.DOWN, new Pair<>(1, Direction.DOWN),
                                    Direction.LEFT, new Pair<>(5, Direction.RIGHT),
                                    Direction.RIGHT, new Pair<>(3, Direction.RIGHT))),
                    3, new Cube(3, new Position(50, 100),
                            java.util.Map.of(
                                    Direction.UP, new Pair<>(4, Direction.UP),
                                    Direction.DOWN, new Pair<>(1, Direction.LEFT),
                                    Direction.LEFT, new Pair<>(2, Direction.LEFT),
                                    Direction.RIGHT, new Pair<>(6, Direction.LEFT))),
                    1, new Cube(1, new Position(0, 150),
                            java.util.Map.of(
                                    Direction.UP, new Pair<>(2, Direction.UP),
                                    Direction.DOWN, new Pair<>(6, Direction.DOWN),
                                    Direction.LEFT, new Pair<>(5, Direction.DOWN),
                                    Direction.RIGHT, new Pair<>(3, Direction.UP))));
        }


        public void print(Position currentPosition) {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (currentPosition.x() == j && currentPosition.y() == i) {
                        System.out.print("X");
                    } else {
                        System.out.print(map[i][j]);
                    }
                }
                System.out.println();
            }
        }

        public Position move(Position position, Direction direction, int howFar) {
            int i = 1;
            Position currentPosition = position;
            Position nextPosition = position;
            while (i <= howFar) {
                do {
                    nextPosition = step(nextPosition, direction);
                } while (map[nextPosition.y()].length <= nextPosition.x()
                        || map[nextPosition.y()][nextPosition.x()] == ' ');
                if (map[nextPosition.y()][nextPosition.x()] == '#') {
                    return currentPosition;
                }

                currentPosition = nextPosition;
                i++;
            }
            return nextPosition;
        }

        public Pair<Position, Direction> moveInCube(Position position, Direction direction, int howFar, java.util.Map<Integer, Cube> cubes) {
            int i = 1;
            Position currentPosition = position;
            Position nextPosition = position;
            Direction currentDirection = direction;
            Direction nextDirection = direction;
            Cube currentCube = cubes.values().stream().filter(position::isInside).findAny().orElseThrow();
            Cube nextCube = currentCube;
            while (i <= howFar) {
                nextPosition = simpleStep(currentPosition, currentDirection);

                if (!nextPosition.isInside(currentCube)) {
                     Triple <Position, Direction, Cube> next = currentCube.operateTransitionTo(currentPosition, currentDirection, cubes);
                     nextPosition = next.first();
                     nextDirection = next.second();
                     nextCube = next.third();
                }
                if (map[nextPosition.y()][nextPosition.x()] == '#') {
                    return new Pair(currentPosition, currentDirection);
                }

                currentPosition = nextPosition;
                currentDirection = nextDirection;
                currentCube = nextCube;

                i++;
            }
            return new Pair(nextPosition, currentDirection);
        }

        public Position step(Position position, Direction direction) {
            return switch (direction) {
                case UP -> new Position(position.x(), (position.y() - 1 + map.length) % map.length);
                case DOWN -> new Position(position.x(), (position.y() + 1) % map.length);
                case RIGHT -> new Position((position.x() + 1) % map[position.y()].length, position.y());
                case LEFT -> new Position((position.x() - 1 + map[position.y()].length) % map[position.y()].length, position.y());
            };
        }

        private Position simpleStep(Position position, Direction direction) {
            return switch (direction) {
                case UP -> new Position(position.x(), (position.y() - 1 ));
                case DOWN -> new Position(position.x(), (position.y() + 1));
                case RIGHT -> new Position((position.x() + 1) , position.y());
                case LEFT -> new Position((position.x() - 1), position.y());
            };
        }

        private Position firstElementInRow(int row) {
            for (int i = 0; i < map[row].length; i++) {
                if (map[row][i] != ' ') {
                    return new Position(i, row);
                }
            }
            return null;
        }
    }

    private static char[][] parse(List<String> mapDescription) {
        char[][] map = new char[mapDescription.size()][];
        for (int i = 0; i < mapDescription.size(); i++) {
            String line = mapDescription.get(i);
            map[i] = line.toCharArray();
        }
        return map;
    }

    record Cube(int id, int size, Position topLeft, java.util.Map<Direction, Pair<Integer, Direction>> transitions) {
        public Cube(int id, Position topLeft, java.util.Map<Direction, Pair<Integer, Direction>> transitions) {
            this(id, 50, topLeft, transitions);
        }

        public Triple<Position, Direction, Cube> operateTransitionTo(Position position, Direction direction, java.util.Map<Integer, Cube> cubes) {
            Position normalized = new Position(position.x() - topLeft.x(), position.y() - topLeft().y());

            Pair<Integer, Direction> target = transitions.get(direction);

            Cube targetCube = cubes.get(target.first());
            Position targetPosition;
            if (direction == Direction.UP) {
                if (target.second() == Direction.UP) {
                    targetPosition = new Position(targetCube.topLeft().x() + normalized.x(), targetCube.topLeft().y() + targetCube.size() - 1);
                } else if (target.second() == Direction.DOWN) {
                    targetPosition = new Position(targetCube.topLeft().x() + normalized.x(), targetCube.topLeft().y());
                } else if (target.second() == Direction.LEFT) {
                    targetPosition = new Position(targetCube.topLeft().x() + targetCube.size() - 1, targetCube.topLeft().y() - normalized.x() + targetCube.size() - 1);
                } else {
                    targetPosition = new Position(targetCube.topLeft().x(), targetCube.topLeft().y() + normalized.x());
                }
            } else if (direction == Direction.DOWN) {
                if (target.second() == Direction.UP) {
                    targetPosition = new Position(targetCube.topLeft().x() + normalized.x(), targetCube.topLeft().y() + targetCube.size() - 1);
                } else if (target.second() == Direction.DOWN) {
                    targetPosition = new Position(targetCube.topLeft().x() + normalized.x(), targetCube.topLeft().y());
                } else if (target.second() == Direction.LEFT) {
                    targetPosition = new Position(targetCube.topLeft().x() + targetCube.size() - 1, targetCube.topLeft().y() + normalized.x() );
                } else {
                    targetPosition = new Position(targetCube.topLeft().x(), targetCube.topLeft().y() - normalized.x() + targetCube.size() - 1);
                }
            } else if (direction == Direction.LEFT) {
                if (target.second() == Direction.UP) {
                    targetPosition = new Position(targetCube.topLeft().x() - normalized.y() + targetCube.size() - 1, targetCube.topLeft().y() + targetCube.size() - 1);
                } else if (target.second() == Direction.DOWN) {
                    targetPosition = new Position(targetCube.topLeft().x() + normalized.y() , targetCube.topLeft().y());
                } else if (target.second() == Direction.LEFT) {
                    targetPosition = new Position(targetCube.topLeft().x() + targetCube.size() - 1, targetCube.topLeft().y() + normalized.y());
                } else {
                    targetPosition = new Position(targetCube.topLeft().x(), targetCube.topLeft().y() - normalized.y() + targetCube.size() - 1);
                }
            } else {
                if (target.second() == Direction.UP) {
                    targetPosition = new Position(targetCube.topLeft().x() + normalized.y(), targetCube.topLeft().y() + targetCube.size() - 1);
                } else if (target.second() == Direction.DOWN) {
                    targetPosition = new Position(targetCube.topLeft().x() - normalized.y() + targetCube.size() - 1, targetCube.topLeft().y());
                } else if (target.second() == Direction.LEFT) {
                    targetPosition = new Position(targetCube.topLeft().x() + targetCube.size() - 1, targetCube.topLeft().y() - normalized.y() + targetCube.size() - 1);
                } else {
                    targetPosition = new Position(targetCube.topLeft().x(), targetCube.topLeft().y() + normalized.y());
                }
            }
            return new Triple<>(targetPosition, target.second(), targetCube);

        }
    }
}