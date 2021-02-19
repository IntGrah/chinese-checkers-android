package com.example.chinesecheckers.util;

public class Util {
    public static final int[][] SINGLE_VECTORS = new int[][] {{0, 1}, {1, 0}, {1, -1}, {-1, 1}, {-1, 0}, {0, -1}};
    public static final int[][] DOUBLE_VECTORS = new int[][] {{0, 2}, {2, 0}, {2, -2}, {-2, 2}, {-2, 0}, {0, -2}};
    public static final String STARTING_POSITION = "xxxx5/xxx6/xx7/x8/9/8o/7oo/6ooo/5oooo x";

    private Util() {}

    public static String toLetter(int value) {
        switch (value) {
            case 0: return "a";
            case 1: return "b";
            case 2: return "c";
            case 3: return "d";
            case 4: return "e";
            case 5: return "f";
            case 6: return "g";
            case 7: return "h";
            case 8: return "i";
        }
        return null;
    }

    public static int[] add(int[] start, int[] vector) {
        return new int[] {start[0] + vector[0], start[1] + vector[1]};
    }

    public static int magnitude(int[] start, int[] end) {
        return end[0] - start[0] + end[1] - start[1];
    }
}
