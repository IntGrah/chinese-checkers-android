package com.example.chinesecheckers.util;

public class Move {
    public Move(int[] start, int[] end) {
        this.start = start;
        this.end = end;
    }
    public int[] start;
    public int[] end;

    public String toSAN() {
        return Util.toLetter(start[0]) + (start[1] + 1) + Util.toLetter(end[0]) + (end[1] + 1);
    }
}
