package com.example.minesweeper;

import android.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Board {
    private Field[][] fields;
    private final Map<Pair<Integer, Integer>, Field> mines = new HashMap<>();
    private final Map<Pair<Integer, Integer>, Field> remainingFields = new HashMap<>();

    private final int rows;
    private final int cols;
    private final int amountOfMines;
    private int flaggedCorrectly;
    private int flaggedTotal;
    private int revealed;
    private boolean gameOver;
    private boolean gameWon;
    private boolean firstClick;

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Board(int rows, int cols, int amountOfMines) {
        this.rows = rows;
        this.cols = cols;
        this.amountOfMines = amountOfMines;
        fields = new Field[rows][cols];
        initializeFields();
        flaggedCorrectly = 0;
        revealed = 0;
        gameOver = false;
        gameWon = false;
        firstClick = true;
    }

    private void initializeFields() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                fields[i][j] = new Field();
                remainingFields.put(new Pair<>(i, j), fields[i][j]);
            }
        }
    }

    private void setupBoard(int safeRow, int safeCol) {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < amountOfMines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);

            if ((row == safeRow && col == safeCol) || fields[row][col].isMine()) {
                continue;
            }

            fields[row][col].setMine(true);
            minesPlaced++;
            remainingFields.remove(new Pair<>(row, col));
            mines.put(new Pair<>(row, col), fields[row][col]);
            updateSurroundingMineCounts(row, col);
        }
    }

    private void updateSurroundingMineCounts(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (isValidPosition(newRow, newCol) && !fields[newRow][newCol].isMine()) {
                    fields[newRow][newCol].incrementMinesAround();
                }
            }
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
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

    public boolean isGameWon() {
        return gameWon;
    }

    public int getFlaggedTotal() {
        return Math.max(amountOfMines - flaggedTotal, 0);
    }

    public Map<Pair<Integer, Integer>, Field> revealField(int row, int col) {
        if (firstClick) {
            firstClick = false;
            setupBoard(row, col);
        }

        Map<Pair<Integer, Integer>, Field> toReveal = new HashMap<>();
        revealFieldRecursive(row, col, toReveal);

        if (gameOver) {
            return handleGameOver(toReveal);
        }

        if (revealed == rows * cols - amountOfMines) {
            handleGameWon(toReveal);
        }

        return toReveal;
    }

    private void revealFieldRecursive(int row, int col, Map<Pair<Integer, Integer>, Field> toReveal) {
        if (!isValidPosition(row, col) || fields[row][col].isRevealed() || fields[row][col].isFlagged()) {
            return;
        }

        fields[row][col].setRevealed(true);
        toReveal.put(new Pair<>(row, col), fields[row][col]);
        revealed++;

        if (fields[row][col].isMine()) {
            fields[row][col].setExploded(true);
            gameOver = true;
            return;
        }

        if (fields[row][col].getMinesAround() == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    revealFieldRecursive(row + i, col + j, toReveal);
                }
            }
        }
    }

    private Map<Pair<Integer, Integer>, Field> handleGameOver(Map<Pair<Integer, Integer>, Field> toReveal) {
        Map<Pair<Integer, Integer>, Field> mines = new HashMap<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (fields[i][j].isMine()) {
                    mines.put(new Pair<>(i, j), fields[i][j]);
                }
            }
        }
        mines.forEach((k, v) -> v.setMine(true));
        toReveal.keySet().forEach(k -> toReveal.get(k).setExploded(true));
        return mines;
    }

    private void handleGameWon(Map<Pair<Integer, Integer>, Field> toReveal) {
        gameOver = true;
        gameWon = true;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (fields[i][j].isMine()) {
                    fields[i][j].defuseAll();
                    toReveal.put(new Pair<>(i, j), fields[i][j]);
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
                flaggedTotal--;
            } else {
                if (fields[row][col].isMine()) {
                    flaggedCorrectly++;
                }
                flaggedTotal++;
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
