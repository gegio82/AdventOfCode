package com.giorgio.aoc.day7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day07 {
    private final static Pattern CD_COMMAND = Pattern.compile("\\$ cd ([a-zA-Z/.]+)");
    private final static Pattern LS_COMMAND = Pattern.compile("\\$ ls");
    private final static Pattern FILE = Pattern.compile("(\\d+) (.+)");
    private final static Pattern DIR = Pattern.compile("dir ([a-zA-Z/]+)");
    public static void main(String[] args) throws IOException {
        execute();
    }

    public static void execute() throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get("src/main/resources/data/day7/input.txt"));
        List<Directory> allDirectories = new ArrayList<>();
        Directory currentDir = null;
        Directory root = new Directory(null, "/");
        allDirectories.add(root);
        for (int i = 0; i < allLines.size(); i++) {
            String command = allLines.get(i);
            Matcher cdMatcher = CD_COMMAND.matcher(command);
            if (cdMatcher.matches()) {
                String dirName = cdMatcher.group(1);
                if (dirName.equals("..")) {
                    currentDir = Optional.ofNullable(currentDir)
                            .map(Directory::parent)
                            .orElse(root);
                } else {
                    currentDir = Optional.ofNullable(currentDir)
                            .flatMap(dir -> dir.getSubDirectory(dirName))
                            .orElse(root);
                }
            }
            Matcher lsMatcher = LS_COMMAND.matcher(command);
            if (lsMatcher.matches()) {
                assert currentDir != null;
                String fsObject = allLines.get(i + 1);

                Matcher fileMatcher = FILE.matcher(fsObject);
                Matcher dirMatcher = DIR.matcher(fsObject);
                while((fileMatcher.matches() || dirMatcher.matches()) && i < allLines.size() - 2) {
                    i++;
                    if (fileMatcher.matches()) {
                        int size = Integer.parseInt(fileMatcher.group(1));
                        String fileName = fileMatcher.group(2);
                        currentDir.addChild(new File(fileName, size));
                    } else if (dirMatcher.matches()) {
                        String dirName = dirMatcher.group(1);
                        final Directory child = new Directory(currentDir, dirName);
                        currentDir.addChild(child);
                        allDirectories.add(child);
                    }
                    fsObject = allLines.get(i + 1);
                    fileMatcher = FILE.matcher(fsObject);
                    dirMatcher = DIR.matcher(fsObject);
                }
            }
        }

        int dirSmallerThen100000TotalSize = allDirectories.stream().filter(it -> it.size() < 100000).mapToInt(Directory::size).sum();
        System.out.println("dirSmallerThen100000TotalSize: " + dirSmallerThen100000TotalSize);

        int freeSpace = 70000000 - root.size();
        int requiredSpace = 30000000;

        Directory candidateToDelete = allDirectories.stream()
                .filter(it -> it.size() >= (requiredSpace - freeSpace))
                .min(Comparator.comparingInt(Directory::size))
                .orElseThrow();
        System.out.println("need for free: " + (requiredSpace - freeSpace));
        System.out.println("best candidate: " + candidateToDelete.name());
        System.out.println("best candidate size: " + candidateToDelete.size());

    }

    interface FSObject {
        int size();
        String name();
    }

    record File(String name, int size) implements FSObject {
    }

    static class Directory implements FSObject {
        private final Directory parent;
        private final List<FSObject> children = new ArrayList<>();
        private final String name;

        public Directory(Directory parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        public String toString() {
            return "dir " + name + " " + children;
        }

        public void addChild(FSObject child) {
            this.children.add(child);
        }

        public Optional<Directory> getSubDirectory(String name) {
            return children.stream()
                    .filter(Directory.class::isInstance)
                    .map(Directory.class::cast)
                    .filter(dir -> dir.name().equals(name))
                    .findFirst();
        }

        @Override
        public int size() {
            return children.stream().mapToInt(FSObject::size).sum();
        }

        @Override
        public String name() {
            return name;
        }

        public Directory parent() {
            return parent;
        }
    }
}
