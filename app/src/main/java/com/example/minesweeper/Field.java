package com.example.minesweeper;


import com.example.minesweeper.R;

public class Field {
    private boolean isMine;
    private int minesAround;
    private boolean isRevealed;
    private boolean isFlagged;
    private boolean isExploded;
    private int spriteID;

    public Field() {
        isMine = false;
        minesAround = 0;
        isRevealed = false;
        isFlagged = false;
        spriteID = R.drawable.tile_concealed;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
        if (mine ) {
            spriteID = R.drawable.tile_mine_unexploded;
        }
    }

    public int getMinesAround() {
        return minesAround;
    }

    public void setMinesAround(int minesAround) {
        this.minesAround = minesAround;
        if (isMine) return;

        switch (minesAround) {
            case 1:
                spriteID = R.drawable.tile_1;
                break;
            case 2:
                spriteID = R.drawable.tile_2;
                break;
            case 3:
                spriteID = R.drawable.tile_3;
                break;
            case 4:
                spriteID = R.drawable.tile_4;
                break;
            case 5:
                spriteID = R.drawable.tile_5;
                break;
            case 6:
                spriteID = R.drawable.tile_6;
                break;
            case 7:
                spriteID = R.drawable.tile_7;
                break;
            case 8:
                spriteID = R.drawable.tile_8;
                break;
        }
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
        if (revealed && minesAround == 0 ){
            spriteID = R.drawable.tile_revealed;
        }
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
        if (flagged) {
            spriteID = R.drawable.tile_flagged;
        } else {

            spriteID = R.drawable.tile_concealed;
        }
    }
    public int getSpriteID() {
        return spriteID;
    }

    public boolean isExploded() {
        return isExploded;
    }
    public void setExploded(boolean exploded) {
        isExploded = exploded;
        spriteID = R.drawable.tile_mine_exploded;
    }
    public void defuseAll() {

            spriteID = R.drawable.tile_flagged;

    }
    public void incrementMinesAround() {
        minesAround++;
        setMinesAround(minesAround);
    }
}
