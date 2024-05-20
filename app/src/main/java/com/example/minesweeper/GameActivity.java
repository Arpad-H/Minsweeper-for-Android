package com.example.minesweeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    private static int NUM_ROWS;
    private static int NUM_COLS;
    private SquareButton buttonGrid[][];
    private Timer timer = new Timer();
    private int secondsElapsed = 0;
    Board board;
    Difficulty difficulty;
    int height;
    int width;
    GridLayout minefieldGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        extractDifficultyFromIntent();
        getScreenDimmensions();

        NUM_COLS = difficulty.getCols();
        NUM_ROWS = difficulty.getRows();
        buttonGrid = new SquareButton[NUM_ROWS][NUM_COLS];
        board = new Board(NUM_ROWS, NUM_COLS, difficulty.getMines());
        board.setupBoard();

        initializeGridLayout();
        populateMinefieldWithButtons();

        setupSmileyButton();
        updateElapsedTimeTimer();
        updateMinesRemaining();
    }

    private void updateElapsedTimeTimer() {
        this.timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        updateTimer();
                    }
                }
                , 0, 1000);
    }

    private void setupSmileyButton() {
        ImageButton smiley = findViewById(R.id.gameStateIndicator);
        smiley.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });
    }

    private void initializeGridLayout() {
        minefieldGrid = findViewById(R.id.gridLayout);
        minefieldGrid.setRowCount(NUM_ROWS);
        minefieldGrid.setColumnCount(NUM_COLS);
    }

    private void populateMinefieldWithButtons() {
        int buttonDim = (int) (Math.min(height / NUM_ROWS, width / NUM_COLS) * 0.8);
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                SquareButton button = new SquareButton(this, i, j);

                // Set button layout parameters
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i, 1.0f);
                params.columnSpec = GridLayout.spec(j, 1.0f);
                params.width = buttonDim;
                params.height = buttonDim;
                params.setMargins(2, 2, 2, 2);
                button.setLayoutParams(params);

                button.setBackground(getDrawable(R.drawable.tile_concealed));
                button.setOnClickListener(v -> {

                    Map<Pair<Integer, Integer>, Field> toReveal = board.revealField(button.row, button.col);
                    for (Pair<Integer, Integer> pair : toReveal.keySet()) {
                        SquareButton sb = buttonGrid[pair.first][pair.second];
                        sb.updateButtonImg(Objects.requireNonNull(toReveal.get(pair)));
                        sb.disable();
                    }
                    if (board.isGameOver()) endGame(board.isGameWon());

                });
                button.setOnLongClickListener(v -> {
                    Log.d("LongClick", "Long click");
                    Pair<Integer, Integer> p = board.toggleFlag(button.row, button.col);
                    buttonGrid[p.first][p.second].updateButtonImg(Objects.requireNonNull(board.getField(p.first, p.second)));
                    if (board.isGameOver()) endGame(board.isGameWon());
                    updateMinesRemaining();
                    return true;
                });
                buttonGrid[i][j] = button;
                minefieldGrid.addView(button);
            }


        }
    }

    private void getScreenDimmensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }

    private void extractDifficultyFromIntent() {
        Intent intent = getIntent();
        String difficultyName = intent.getStringExtra(MainActivity.EXTRA_DIFFICULTY);
        difficulty = Difficulty.valueOf(difficultyName);
    }

    private void updateTimer() {
        runOnUiThread(() -> {
            ImageView img_ones = findViewById(R.id.time_ones);
            ImageView img_tens = findViewById(R.id.time_tens);
            ImageView img_hundreds = findViewById(R.id.time_hundreds);
            img_ones.setImageResource(getResources().getIdentifier("display_" + secondsElapsed % 10, "drawable", getPackageName()));
            img_tens.setImageResource(getResources().getIdentifier("display_" + (secondsElapsed / 10) % 10, "drawable", getPackageName()));
            img_hundreds.setImageResource(getResources().getIdentifier("display_" + (secondsElapsed / 100) % 10, "drawable", getPackageName()));
            secondsElapsed++;

        });
    }

    public void endGame(boolean win) {
        timer.cancel();
        timer.purge();
        if (win) {
            ImageButton smiley = findViewById(R.id.gameStateIndicator);
            smiley.setImageResource(R.drawable.game_won);
            for (Pair<Integer, Integer> pair : board.getRemainingFields().keySet()) {
                SquareButton sb = buttonGrid[pair.first][pair.second];
                sb.updateButtonImg(Objects.requireNonNull(board.getField(pair.first, pair.second)));
                sb.disable();
            }
            showGameOverDialog(true);

        } else {
            ImageButton smiley = findViewById(R.id.gameStateIndicator);
            smiley.setImageResource(R.drawable.game_over);
            showGameOverDialog(false);
        }

    }

    private void updateMinesRemaining() {
        runOnUiThread(() -> {
            ImageView img_ones = findViewById(R.id.mines_ones);
            ImageView img_tens = findViewById(R.id.mines_tens);
            ImageView img_hundreds = findViewById(R.id.mines_hundreds);
            img_ones.setImageResource(getResources().getIdentifier("display_" + board.getFlaggedTotal() % 10, "drawable", getPackageName()));
            img_tens.setImageResource(getResources().getIdentifier("display_" + (board.getFlaggedTotal() / 10) % 10, "drawable", getPackageName()));
            img_hundreds.setImageResource(getResources().getIdentifier("display_" + (board.getFlaggedTotal() / 100) % 10, "drawable", getPackageName()));
        });
    }

    private void showGameOverDialog(boolean win) {
        String message = win ? "You won!" : "You lost!";
        String title = win ? "Victory!" : "Game Over";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Back to Main Menu", (dialog, which) -> {
                    Intent intent = new Intent(GameActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Play Again", (dialog, which) -> {
                    finish();
                    startActivity(getIntent());
                })
                .setCancelable(false)
                .show();
    }
}
