package com.example.minesweeper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private Bitmap bitmap;
    private Canvas canvas;
    private ImageView imageView;
    private Paint paint;
    public static final String EXTRA_DIFFICULTY = "com.example.minesweeper.DIFFICULTY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupDifficultySelectionButtons();
    }

    private void setupDifficultySelectionButtons() {
        Button btnEasy = findViewById(R.id.btn_easy);
        Button btnMedium = findViewById(R.id.btn_medium);
        Button btnHard = findViewById(R.id.btn_hard);

        btnEasy.setOnClickListener(v -> startGame(Difficulty.EASY));
        btnMedium.setOnClickListener(v -> startGame(Difficulty.MEDIUM));
        btnHard.setOnClickListener(v -> startGame(Difficulty.HARD));
    }

    private void startGame(Difficulty difficulty) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty.name());
        startActivity(intent);
    }


}