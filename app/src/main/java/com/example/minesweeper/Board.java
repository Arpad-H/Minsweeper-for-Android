package com.example.minesweeper;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private Field[][] fields;
    private Map<Pair<Integer, Integer>, Field> mines = new HashMap<>();

    public Map<Pair<Integer, Integer>, Field> getRemainingFields() {
        return remainingFields;
    }

    private Map<Pair<Integer, Integer>, Field> remainingFields = new HashMap<>();
    private int rows;
    private int cols;
    private int amountOfMines;
    private int flaggedCorrectly;
    private int flaggedTotal;

    public int getFlaggedTotal() {
        return Math.max(amountOfMines-flaggedTotal, 0);
    }

    private int revealed;
    private boolean gameOver;

    public boolean isGameWon() {
        return gameWon;
    }

    private boolean gameWon;

    public Board(int rows, int cols, int amountOfMines) {
        this.rows = rows;
        this.cols = cols;
        this.amountOfMines = amountOfMines;
        fields = new Field[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                fields[i][j] = new Field();
                remainingFields.put(new Pair<>(i, j), fields[i][j]);
            }
        }
        flaggedCorrectly = 0;
        revealed = 0;
        gameOver = false;
        gameWon = false;
    }

    public void setupBoard() {
        for (int i = 0; i < amountOfMines; i++) {
            int row = (int) (Math.random() * rows);
            int col = (int) (Math.random() * cols);
            if (fields[row][col].isMine()) {
                i--;
            } else {
                fields[row][col].setMine(true);
                remainingFields.remove(new Pair<>(row, col));
                mines.put(new Pair<>(row, col), fields[row][col]);
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        if (row + j >= 0 && row + j < rows && col + k >= 0 && col + k < cols) {
                            fields[row + j][col + k].setMinesAround(fields[row + j][col + k].getMinesAround() + 1);
                        }
                    }
                }
            }
        }
    }

    public Field getField(int row, int col) {
        return fields[row][col];
    }


    public Map<Pair<Integer, Integer>, Field> getMines() {
        return mines;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Map<Pair<Integer, Integer>, Field> revealField(int row, int col) {
        Map<Pair<Integer, Integer>, Field> toReveal = new HashMap<>();
        revealField(row, col, toReveal);
        if (gameOver) {
            mines.forEach((k, v) -> mines.get(k).setMine(true));
            toReveal.keySet().forEach(k -> toReveal.get(k).setExploded(true));
            return mines;
        }
        if (revealed == rows * cols - amountOfMines) {
            gameOver = true;
            gameWon = true;
            mines.forEach((k, v) -> mines.get(k).defuseAll());
            toReveal.putAll(mines);
        }

       toReveal.forEach((k, v) -> v.setMinesAround(v.getMinesAround()));
        return toReveal;
    }

    private void revealField(int row, int col, Map<Pair<Integer, Integer>, Field> toReveal) {
        if (fields[row][col].isRevealed() || fields[row][col].isFlagged()) {
            return;
        }
        toReveal.put(new Pair<>(row, col), fields[row][col]);
        fields[row][col].setRevealed(true);
        revealed++;
        if (fields[row][col].isMine()) {
            fields[row][col].setExploded(true);
            gameOver = true;
            toReveal.clear();
            toReveal.put(new Pair<>(row, col), fields[row][col]);
            return ;
        }
        if (fields[row][col].getMinesAround() == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (row + i >= 0 && row + i < rows && col + j >= 0 && col + j < cols) {
                        revealField(row + i, col + j, toReveal);
                       remainingFields.remove(new Pair<>(row + i, col + j));
                    }
                }
            }
        }

    }

    public Pair<Integer, Integer> toggleFlag(int row, int col) {
        if (!fields[row][col].isRevealed()) {
            if (fields[row][col].isFlagged()) {
                if (fields[row][col].isMine()) {
                    flaggedCorrectly--;
                }
                fields[row][col].setFlagged(!fields[row][col].isFlagged());
                flaggedTotal --;
            } else {
                if (fields[row][col].isMine()) {
                    flaggedCorrectly++;
                }
                flaggedTotal ++;
                fields[row][col].setFlagged(!fields[row][col].isFlagged());
            }
        }

        if (flaggedCorrectly == amountOfMines) {
            gameOver = true;
            gameWon = true;
        }
        return new Pair<>(row, col);
    }
}
