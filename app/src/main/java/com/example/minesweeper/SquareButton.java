package com.example.minesweeper;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;



public class SquareButton extends androidx.appcompat.widget.AppCompatButton {
    private int row;
    private int col;
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public SquareButton(Context context, int row, int col) {
        super(context);
        this.row = row;
        this.col = col;
    }

    public SquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = right - left;
        setHeight(width);
    }

    public void updateButtonImg(Field field) {
        setBackground(ResourcesCompat.getDrawable(getResources(), field.getSpriteID(), null));
    }

    public void disable() {
        setEnabled(false);
    }
}


