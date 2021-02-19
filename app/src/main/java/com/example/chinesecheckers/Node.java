package com.example.chinesecheckers;

import android.util.Log;
import com.example.chinesecheckers.util.Move;
import com.example.chinesecheckers.util.Piece;
import com.example.chinesecheckers.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
    private static final HashMap<String, Float> transpositionTable = new HashMap<>();

    Node(Piece[][] board, Piece turn) {
        this.board = board;
        this.turn = turn;
    }

    private final Piece[][] board;
    private final Piece turn;
    private float[] evaluations;
    private Move[] moves;
    private ArrayList<int[]> ends;

    public Move bestMove(int depth) throws IllegalArgumentException {
        moves = findAllMoves();
        if (depth < 1) {
            throw new IllegalArgumentException();
        }
        float evaluation = evaluate(depth, -1000f, 1000f, true);
        ArrayList<Float> list = new ArrayList<>();
        for (float element : evaluations) {
            list.add(element);
        }
        int n = list.indexOf(evaluation);
        return moves[n];
    }

    private float evaluate(int depth, float alpha, float beta, boolean root) {
        if (root) {
            transpositionTable.clear();
        }
        if (turn == Piece.O && lastX() == 13) {
            return 500f + depth;
        } else if (turn == Piece.X && lastO() == 3) {
            return -500f - depth;
        }
        if (depth == 0) {
            return heuristicEvaluate();
        }

        String serial = serialize(depth);
        Float serialValue = transpositionTable.get(serial);
        if (serialValue != null) {
            return serialValue;
        }

        Move[] moves = findAllMoves();
        float[] evaluations = new float[moves.length];

        float value;

        if (turn == Piece.X) {
            value = -1000f;
            for (int i = 0; i < moves.length; ++i) {
                float evaluation = move(moves[i]).evaluate(depth - 1, alpha, beta, false);
                if (evaluation > value) {
                    value = evaluation;
                }
                if (value > alpha) {
                    alpha = value;
                }
                evaluations[i] = evaluation;
                if (alpha >= beta) {
                    break;
                }
            }
        } else {
            value = 1000f;
            for (int i = 0; i < moves.length; ++i) {
                float evaluation = move(moves[i]).evaluate(depth - 1, alpha, beta, false);
                if (evaluation < value) {
                    value = evaluation;
                }
                if (value < beta) {
                    alpha = value;
                }
                evaluations[i] = evaluation;
                if (alpha >= beta) {
                    break;
                }
            }
        }
        if (root) {
            this.moves = moves;
            this.evaluations = evaluations;
        }
        transpositionTable.put(serial, value);
        return value;
    }

    private String serialize(int depth) {
        StringBuilder string = new StringBuilder("" + depth + turn.toString());
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                string.append(board[i][j].toString());
            }
        }
        return string.toString();
    }

    private float heuristicEvaluate() {
        float value = -176f;
        int lastX = 13;
        int lastO = 3;
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                Piece piece = board[i][j];
                if (piece != Piece.EMPTY) {
                    value += piece.getValue() * Math.abs(i - j);
                    if (piece == Piece.X) {
                        value += i + j;
                        if (i + j < lastX) {
                            lastX = i + j;
                        }
                    } else {
                        if (i + j > lastO) {
                            lastO = i + j;
                        }
                    }
                }
            }
        }
        return value + lastX + lastO;
    }

    public int lastX() {
        int last = 13;
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (board[i][j] == Piece.X && i + j < last) {
                    last = i + j;
                }
            }
        }
        return last;
    }

    public int lastO() {
        int last = 3;
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (board[i][j] == Piece.O && i + j > last) {
                    last = i + j;
                }
            }
        }
        return last;
    }

    private Move[] findAllMoves() {
        ArrayList<Move> allMoves = new ArrayList<>();
        int[][] positions = new int[10][2];
        int found = 0;
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (board[i][j] == turn) {
                    positions[found++] = new int[] {j, i};
                }
            }
        }

        for (int[] position : positions) {
            int[][] ends = findPieceEnds(position);
            ArrayList<Move> moves = new ArrayList<>();
            for (int[] end : ends) {
                Piece piece = board[position[1]][position[0]];
                int magnitude = Util.magnitude(position, end);
                if (piece == Piece.X && magnitude > -2 || piece == Piece.O && magnitude < 2) {
                    moves.add(new Move(position, end));
                }
            }
            this.ends = null;
            allMoves.addAll(moves);
        }
        allMoves.sort((a, b) -> {
            int absA = Util.magnitude(a.start, a.end);
            int absB = Util.magnitude(b.start, b.end);
            if (absA > absB) {
                return -1;
            } else if (absA < absB) {
                return -1;
            }
            return 0;
        });
        Move[] arr = new Move[] {};
        return allMoves.toArray(arr);
    }

    public int[][] findPieceEnds(int[] start) {
        ends = new ArrayList<>();
        findMinorMoves(start);
        findMajorMoves(start, 6);
        int[][] arr = new int[][] {};
        return ends.toArray(arr);
    }

    private void findMinorMoves(int[] start) {
        for (int[] vector : Util.SINGLE_VECTORS) {
            if (itemAt(Util.add(start, vector)) == Piece.EMPTY) {
                ends.add(Util.add(start, vector));
            }
        }
    }

    private void findMajorMoves(int[] start, int from) {
        for (int i = 0; i< 6; ++i) {
            if (from == i) {
                continue;
            }
            int[] jump = Util.DOUBLE_VECTORS[i];
            Piece over = itemAt(Util.add(start, Util.SINGLE_VECTORS[i]));
            if (itemAt(Util.add(start, jump)) == Piece.EMPTY && (over == Piece.X || over == Piece.O)) {
                int[] test = Util.add(start, jump);
                boolean unique = true;
                for (int[] end : ends) {
                    if (test[0] == end[0] && test[1] == end[1]) {
                        unique = false;
                        break;
                    }
                }
                if (unique) {
                    ends.add(test);
                    findMajorMoves(test, 6 - i);
                }
            }
        }
    }

    public Piece itemAt(int[] point) {
        if (0 <= point[0] && point[0] < 9 && 0 <= point[1] && point[1] < 9) {
            return board[point[1]][point[0]];
        }
        return Piece.OUT;
    }

    public Piece getTurn() {
        return turn;
    }

    public Node move(Move move) {
        Piece[][] board = new Piece[9][9];
        for (int i = 0; i < 9; ++i) {
            board[i] = this.board[i].clone();
        }
        if (move != null) {
            int[] start = move.start;
            int[] end = move.end;
            Piece temp = board[start[1]][start[0]];
            board[start[1]][start[0]] = Piece.EMPTY;
            board[end[1]][end[0]] = temp;
        }
        return new Node(board, turn == Piece.X ? Piece.O : Piece.X);
    }
}
