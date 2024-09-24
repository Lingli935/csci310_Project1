package com.example.minesweeper;

public class Cell {
    private boolean hasMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int numberOfAdjacentMines;

    public Cell(){
        hasMine = false;
        isRevealed = false;
        isFlagged = false;
        numberOfAdjacentMines = -1;
    }

    public boolean getHasMine() {return hasMine;}
    public boolean getIsRevealed() {return isRevealed;}
    public boolean getIsFlagged() {return isFlagged;}
    public int getNumberOfAdjacentMines() { return numberOfAdjacentMines; }

    public void setMine() {hasMine = true;}
    public void setRevealed(boolean rl) {isRevealed = rl;}
    public void setFlagged(boolean fl) {isFlagged = fl;}
    public void setNumberOfAdjacentMines(int value) { numberOfAdjacentMines = value; }

    public void toggleFlag() { isFlagged = !isFlagged; }

}

//ThisCell = new Cell();
//ThisCell.setNumberOfAdjacentMines(2)