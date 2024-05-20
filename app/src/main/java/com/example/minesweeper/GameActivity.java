package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.minesweeper.logic.Difficulty;
import com.example.minesweeper.ui.SquareButton;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {
    private GameViewModel gameViewModel;
    private GridLayout minefieldGrid;
    private Timer timer = new Timer();
    private int height, width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        extractDifficultyFromIntent();
        initializeScreenDimensions();

        setupSmileyButton();
        setupMinefieldGrid();

        bindGameViewModelObservers();

        startTimer();
    }

    private void bindGameViewModelObservers() {
        gameViewModel.getGameState().observe(this, gameState -> {
            if (gameState.isGameOver()) {
                endGame(gameState.isGameWon());
            }
        });
        gameViewModel.getElapsedTime().observe(this, this::updateElapsedTime);
        gameViewModel.getRemainingMines().observe(this, this::updateRemainingMines);
    }

    private void extractDifficultyFromIntent() {
        Intent intent = getIntent();
        String difficultyName = intent.getStringExtra(MainActivity.EXTRA_DIFFICULTY);
        Difficulty difficulty = Difficulty.valueOf(difficultyName);
        gameViewModel.initializeBoard(difficulty);
    }

    private void initializeScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }

    private void setupSmileyButton() {
        ImageButton smiley = findViewById(R.id.gameStateIndicator);
        smiley.setOnClickListener(v -> restartGame());
    }

    private void setupMinefieldGrid() {
        minefieldGrid = findViewById(R.id.gridLayout);
        minefieldGrid.setRowCount(gameViewModel.getNumRows());
        minefieldGrid.setColumnCount(gameViewModel.getNumCols());

        populateMinefieldWithButtons();
    }

    private void populateMinefieldWithButtons() {
        int buttonDim = calculateButtonDimension();
        for (int i = 0; i < gameViewModel.getNumRows(); i++) {
            for (int j = 0; j < gameViewModel.getNumCols(); j++) {
                SquareButton button = new SquareButton(this, i, j);
                setupButtonLayout(button, buttonDim);
                setupButtonListeners(button);
                minefieldGrid.addView(button);
                gameViewModel.addButton(i, j, button);
            }
        }
    }

    private int calculateButtonDimension() {
        return (int) (Math.min(height / gameViewModel.getNumRows(), width / gameViewModel.getNumCols()) * 0.8);
    }

    private void setupButtonLayout(SquareButton button, int dimension) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = dimension;
        params.height = dimension;
        params.setMargins(2, 2, 2, 2);
        button.setLayoutParams(params);
        button.setBackground(getDrawable(R.drawable.tile_concealed));
    }

    private void setupButtonListeners(SquareButton button) {
        button.setOnClickListener(v -> gameViewModel.revealField(button.getRow(), button.getCol()));
        button.setOnLongClickListener(v -> {
            gameViewModel.toggleFlag(button.getRow(), button.getCol());
            return true;
        });
    }

    private void startTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                gameViewModel.incrementElapsedTime();
            }
        }, 0, 1000);
    }

    private void updateElapsedTime(int secondsElapsed) {
        runOnUiThread(() -> {
            ImageView imgOnes = findViewById(R.id.time_ones);
            ImageView imgTens = findViewById(R.id.time_tens);
            ImageView imgHundreds = findViewById(R.id.time_hundreds);
            updateTimerImages(secondsElapsed, imgOnes, imgTens, imgHundreds);
        });
    }

    private void updateRemainingMines(int remainingMines) {
        runOnUiThread(() -> {
            ImageView imgOnes = findViewById(R.id.mines_ones);
            ImageView imgTens = findViewById(R.id.mines_tens);
            ImageView imgHundreds = findViewById(R.id.mines_hundreds);
            updateTimerImages(remainingMines, imgOnes, imgTens, imgHundreds);
        });
    }

    private void updateTimerImages(int value, ImageView imgOnes, ImageView imgTens, ImageView imgHundreds) {
        imgOnes.setImageResource(getResources().getIdentifier("display_" + value % 10, "drawable", getPackageName()));
        imgTens.setImageResource(getResources().getIdentifier("display_" + (value / 10) % 10, "drawable", getPackageName()));
        imgHundreds.setImageResource(getResources().getIdentifier("display_" + (value / 100) % 10, "drawable", getPackageName()));
    }

    private void endGame(boolean win) {
        timer.cancel();
        timer.purge();
        ImageButton smiley = findViewById(R.id.gameStateIndicator);
        smiley.setImageResource(win ? R.drawable.game_won : R.drawable.game_over);
        showGameOverDialog(win);
    }

    private void showGameOverDialog(boolean win) {
        String message = win ? getResources().getString(R.string.message_win) : getResources().getString(R.string.message_loss);
        String title = win ? getResources().getString(R.string.title_win) : getResources().getString(R.string.title_loss);
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.btn_back_to_main_menu), (dialog, which) -> returnToMainMenu())
                .setNegativeButton(getResources().getString(R.string.btn_play_again), (dialog, which) -> restartGame())
                .setCancelable(false)
                .show();
    }

    private void returnToMainMenu() {
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void restartGame() {
        finish();
        startActivity(getIntent());
    }
}
