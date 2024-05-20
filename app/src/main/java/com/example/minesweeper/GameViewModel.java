package com.example.minesweeper;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;



import java.util.Map;

public class GameViewModel extends AndroidViewModel {
    private Board board;
    private final MutableLiveData<GameState> gameState = new MutableLiveData<>();
    private final MutableLiveData<Integer> elapsedTime = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> remainingMines = new MutableLiveData<>(0);
    private SquareButton[][] buttonGrid;

    public GameViewModel(@NonNull Application application) {
        super(application);
    }

    public void initializeBoard(Difficulty difficulty) {
        board = new Board(difficulty.getRows(), difficulty.getCols(), difficulty.getMines());
       // board.setupBoard();
        buttonGrid = new SquareButton[difficulty.getRows()][difficulty.getCols()];
        remainingMines.setValue(difficulty.getMines());
    }

    public int getNumRows() {
        return board.getRows();
    }

    public int getNumCols() {
        return board.getCols();
    }

    public LiveData<GameState> getGameState() {
        return gameState;
    }

    public LiveData<Integer> getElapsedTime() {
        return elapsedTime;
    }

    public LiveData<Integer> getRemainingMines() {
        return remainingMines;
    }

    public void incrementElapsedTime() {
        elapsedTime.postValue(elapsedTime.getValue() + 1);
    }

    public void addButton(int row, int col, SquareButton button) {
        buttonGrid[row][col] = button;
    }

    public void revealField(int row, int col) {
        Map<Pair<Integer, Integer>, Field> toReveal = board.revealField(row, col);
        for (Map.Entry<Pair<Integer, Integer>, Field> entry : toReveal.entrySet()) {
            SquareButton button = buttonGrid[entry.getKey().first][entry.getKey().second];
            button.updateButtonImg(entry.getValue());
            button.disable();
        }
        updateGameState();
    }

    public void toggleFlag(int row, int col) {
        board.toggleFlag(row, col);
        buttonGrid[row][col].updateButtonImg(board.getField(row, col));
        remainingMines.postValue(board.getFlaggedTotal());
        updateGameState();
    }

    private void updateGameState() {
        if (board.isGameOver()) {
            gameState.postValue(new GameState(board.isGameWon(), true));
        }
    }
}
