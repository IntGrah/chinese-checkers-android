package com.example.chinesecheckers;

import android.util.Log;
import com.example.chinesecheckers.util.CallBack;
import com.example.chinesecheckers.util.Move;
import com.example.chinesecheckers.util.Piece;
import com.example.chinesecheckers.util.Util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
    Game(String fen, CallBack callBack) {
        this.callBack = callBack;
        importFEN(fen);
    }

    private final CallBack callBack;
    private Node node;
    private final ArrayList<Move> log = new ArrayList<>();

    public void play(Move move) {
        node = node.move(move);
        log.add(move);
        renderBoard(move);
        renderMoveText();
    }

    void playBestMove(int depth) {
        Move bestMove;
        if (node.lastX() != 13 && node.lastO() != 3) {
            bestMove = node.bestMove(depth);
            play(bestMove);
        }
    }

    public void importFEN(String fen) {
        if (fen == null) {
            fen = Util.STARTING_POSITION;
        }
        Piece[][] board = new Piece[9][9];
        String[] split = null;
        Piece turn = null;
        Matcher matcher = Pattern.compile("(.*) ([xo])").matcher(fen);
        if (matcher.find()) {
            split = matcher.group(1).split("/");
            turn = matcher.group(2).equals("x") ? Piece.X : Piece.O;
        }

        int y = 0;
        for (String row : split) {
            int x = 0;
            for (int i = 0; i < row.length(); ++i) {
                char ch = row.charAt(i);
                if (ch == 'x') {
                    board[y][x++] = Piece.X;
                } else if (ch == 'o') {
                    board[y][x++] = Piece.O;
                } else {
                    int spaces = Integer.parseInt(String.valueOf(ch));
                    for (int j = 0; j < spaces; ++j) {
                        board[y][x++] = Piece.EMPTY;
                    }
                }
            }
            y++;
        }

        node = new Node(board, turn);
        log.clear();
        renderBoard(null);
        renderMoveText();
    }

    public String exportFEN() {
        StringBuilder fen = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                fen.append(node.itemAt(new int[]{j, i}).toString());
            }
            if (i == 8) {
                break;
            }
            fen.append("/");
        }
        fen.append(" ");
        fen.append(this.node.getTurn().toString());
        Matcher m = Pattern.compile(" {1,9}").matcher(fen);
        StringBuffer buffer = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(buffer, "" + m.group().length());
        }
        return buffer.toString();
    }

    private void renderBoard(Move lastMove) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                int[] point = new int[] {j, i};
                stringBuilder.append(node.itemAt(point).toString());
            }
            stringBuilder.append("\n");
        }
        callBack.setBoard(stringBuilder.toString());
    }

    private void renderMoveText() {
        StringBuilder stringBuilder = new StringBuilder();
        int plyCount = 0;
        for (Move move : log) {
            if (plyCount++ % 2 == 0) {
                stringBuilder.append((plyCount + 1) / 2);
                stringBuilder.append(". ");
            }
            stringBuilder.append(move.toSAN());
            stringBuilder.append(" ");
        }
        callBack.setMoveText(stringBuilder.toString());
    }
}
