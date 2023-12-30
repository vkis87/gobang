package main;

public class Move {
    private int x;
    private int y;

    private int chessColor;

    public Move(int x, int y, int chessColor) {
        this.chessColor = chessColor;
        this.x = x;
        this.y = y;
    }

    // 位置
    public int getPositionX() {
        return this.x;
    }
    public int getPositionY() {
        return this.y;
    }

    // 顏色 1= white, 0= black
    public int getChessColor() {
        return this.chessColor;
    }
}
