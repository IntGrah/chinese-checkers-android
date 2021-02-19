package com.example.chinesecheckers.util;

public enum Piece {
    X (1),
    O (-1),
    EMPTY (0),
    OUT (0);

    Piece(int value) {
        this.value = value;
    }
    private final int value;

    public int getValue() {
        return value;
    }

    public String toString() {
        switch (this) {
            case X: return "x";
            case O: return "o";
            case EMPTY: return " ";
            default: return "";
        }
    }
}