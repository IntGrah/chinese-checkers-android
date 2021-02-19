package com.example.chinesecheckers;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.chinesecheckers.util.CallBack;
import com.example.chinesecheckers.util.Util;

public class Main extends AppCompatActivity implements CallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.main);
        board = findViewById(R.id.board);
        moveText = findViewById(R.id.moveText);
        playBestMove = findViewById(R.id.playBestMove);
        playBestMove.setOnClickListener(view -> playBestMove());
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(view -> {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        });
        game = new Game(Util.STARTING_POSITION, this);
    }

    Context context;
    Game game;
    private TextView board;
    private TextView moveText;
    private Button playBestMove;
    private Button settings;

    public void playBestMove() {
        game.playBestMove(4);
    }

    @Override
    public void setBoard(String text) {
        board.setText(text);
    }

    @Override
    public void setMoveText(String text) {
        moveText.setText(text);
    }
}