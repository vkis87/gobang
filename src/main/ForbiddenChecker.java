package main;

import java.util.ArrayList;
import java.util.Arrays;

public class ForbiddenChecker {
    private boolean initiative;
    private String checkMessage;

    // 需要一個參數表示是否先手
    public ForbiddenChecker(boolean initiative) {
        this.initiative = initiative;
    }

    public String getCheckMessage() {
        return this.checkMessage;
    }

    // 檢查是否觸發禁手 並在this.message中留下訊息
    public boolean check(Core core, Move move) {
        this.checkMessage = "";
        if (this.initiative && (this.checkSix(core, move))) {
            this.checkMessage = "長連";
            return true;
        }
        if (this.initiative && this.checkDoubleFour(core, move)) {
            this.checkMessage = "雙四";
            return true;
        }
        if (this.initiative && this.checkDoubleThree(core, move)) {
            this.checkMessage = "雙三";
            return true;
        }
        return false;
    }

    // 長連
    public boolean checkSix(Core core, Move move) {
        ArrayList<ArrayList<Integer>> testTingPosition = genTestingPositions(core, move, true);
        for (ArrayList<Integer> array : testTingPosition) {
            if (checkSixInArray(move.getChessColor(), array)) {
                return true;
            }
        }
        return false;
    }

    // 雙四
    public boolean checkDoubleFour(Core core, Move move) {
        ArrayList<ArrayList<Integer>> testTingPosition = genTestingPositions(core, move, false);
        int counter = 0;
        for (ArrayList<Integer> array : testTingPosition) {
            counter += checkFourInArray(move.getChessColor(), 0, array);
            if (counter == 2) {
                return true;
            }
        }
        return false;
    }

    // 雙三
    public boolean checkDoubleThree(Core core, Move move) {
        ArrayList<ArrayList<Integer>> testTingPosition = genTestingPositions(core, move, false);
        int counter = 0;
        for (ArrayList<Integer> array : testTingPosition) {
            if (checkLivingThreeInArray(move.getChessColor(), 0, array)) {
                counter++;
                if (counter == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private ArrayList<ArrayList<Integer>> genTestingPositions(Core core, Move move, boolean sixMode) {
        int[][] board = Arrays.copyOf(core.getCore(), core.getCore().length);
        for (int i = 0; i < board.length; i++) {
            board[i] = Arrays.copyOf(core.getCore()[i], core.getCore()[i].length);
        }
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        int x = move.getPositionX();
        int y = move.getPositionY();
        int xMax = x + 4;
        int yMax = y + 4;
        int xMin = x - 4;
        int yMin = y - 4;
        // 偵測長連擴大範圍
        if(sixMode) {
            xMax++;
            yMax++;
            xMin--;
            yMin--;
        }
        if(xMax >= board.length) xMax = board.length-1;
        if(yMax >= board[0].length) yMax = board[0].length-1;
        if(xMin < 0) xMin = 0;
        if(yMin < 0) yMin = 0;
        // 落子後的盤面
        board[x][y] = move.getChessColor();
        // 斜向
        int i = x;
        int j = y;
        while (i > xMin && j > yMin) {
            i--;
            j--;
        }
        ArrayList<Integer> buf = new ArrayList<Integer>();
        while (i <= xMax && j <= yMax) {
            buf.add(board[i][j]);
            i++;
            j++;
        }
        result.add(buf);
        // 另一方向斜向
        i = x;
        j = y;
        while (i > xMin && j < yMax) {
            i--;
            j++;
        }
        buf = new ArrayList<Integer>();
        while (i <= xMax && j >= xMin) {
            buf.add(board[i][j]);
            i++;
            j--;
        }
        result.add(buf);
        // 縱向
        i = xMin;
        j = y;
        buf = new ArrayList<Integer>();
        while (i <= xMax) {
            buf.add(board[i][j]);
            i++;
        }
        result.add(buf);
        // 橫向
        i = x;
        j = yMin;
        buf = new ArrayList<Integer>();
        while (j <= yMax) {
            buf.add(board[i][j]);
            j++;
        }
        result.add(buf);
        return result;
    }

    private boolean checkSixInArray(int targetValue, ArrayList<Integer> array) {
        int counter = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) == targetValue) {
                counter++;
                if (counter >= 6) {
                    return true;
                }
            } else {
                counter = 0;
            }
        }
        return false;
    }

    private int checkFourInArray(int targetValue, int backgroundValue, ArrayList<Integer> array) {
        int numOfFour = 0;
        int counter = 0;
        int missCounter = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) == targetValue) {
                counter++;
                missCounter = 0;
                if (counter == 4) {
                    counter = 1;
                    missCounter = 0;
                    numOfFour++;
                }
            } else if (array.get(i) != backgroundValue) {
                counter = 0;
                missCounter = 0;
            } else if (array.get(i) == backgroundValue) {
                missCounter++;
                if (missCounter > 1) {
                    counter = 0;
                    missCounter = 0;
                }
            }
        }
        return numOfFour;
    }

    private boolean checkLivingThreeInArray(int targetValue, int backgroundValue, ArrayList<Integer> array) {
        int numOfLivingThree = 0;
        int counter = 0;
        int missCounter = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) == targetValue) {
                counter++;
                missCounter = 0;
                if (counter == 3) {
                    counter = 1;
                    missCounter = 0;
                    numOfLivingThree++;
                }
            } else if (array.get(i) != backgroundValue) {
                counter = 0;
                missCounter = 0;
            } else if (array.get(i) == backgroundValue) {
                missCounter++;
                if (missCounter > 1) {
                    counter = 0;
                    missCounter = 0;
                }
            }
        }
        // 單條線上有兩個活三 會成為不算四
        return (numOfLivingThree == 1);
    }

    public void setInitiative(boolean initiative) {
        this.initiative = initiative;
    }

    public boolean getInitiative() {
        return this.initiative;
    }
}
