package com.example.minesweeper;

public class GameState {
    private final boolean gameWon;
    private final boolean gameOver;

    public GameState(boolean gameWon, boolean gameOver) {
        this.gameWon = gameWon;
        this.gameOver = gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
