package main;

import java.util.ArrayList;

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
        if(this.initiative && (this.checkSix(core, move))) {
            this.checkMessage = "長連";
            return true;    
        }
        if(this.checkDoubleThree(core, move)) {
            this.checkMessage = "雙三";
            return true;
        }
        if(this.checkDoubleFour(core, move)) {
            this.checkMessage = "雙四";
            return true;
        }
        return false;
    }

    // 長連
    public boolean checkSix(Core core, Move move) {
        return false;
    }

    // 雙四
    public boolean checkDoubleFour(Core core, Move move) {
        ArrayList<int[]> testTimgPosition = genTestingPositions(core, move);
        int counter = 0;
        for(int[] array : testTimgPosition) {
            if(checkLivingFourInArray(move.getChessColor(), 0, array)) {
                counter++;
                if(counter == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    // 雙三
    public boolean checkDoubleThree(Core core, Move move) {
        ArrayList<int[]> testTimgPosition = genTestingPositions(core, move);
        int counter = 0;
        for(int[] array : testTimgPosition) {
            if(checkLivingThreeInArray(move.getChessColor(), 0, array)) {
                counter++;
                if(counter == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private ArrayList<int[]> genTestingPositions(Core core, Move move) {
        int[][] board = core.getCore();
        ArrayList<int[]> result = new ArrayList<int[]>();
        int x = move.getPositionX();
        int y = move.getPositionY();    
        for (int i = 0; i < 6; i++) {
            // 斜方向
            try {
                int[] buf = new int[6];
                for (int j = 0; j < 6; j++) {
                    buf[j] = board[x + i - j][y + i - j];
                }
                result.add(buf);
            } catch (IndexOutOfBoundsException e) {}
            try {
                int[] buf = new int[6];
                for (int j = 0; j < 6; j++) {
                    buf[j] = board[x - i + j][y - i + j];
                }
                result.add(buf);
            } catch (IndexOutOfBoundsException e) {}
            try {
                int[] buf = new int[6];
                for (int j = 0; j < 6; j++) {
                    buf[j] = board[x + i - j][y - i + j];
                }
                result.add(buf);
            } catch (IndexOutOfBoundsException e) {}
            try {
                int[] buf = new int[6];
                for (int j = 0; j < 6; j++) {
                    buf[j] = board[x - i + j][y + i - j];
                }
                result.add(buf);
            } catch (IndexOutOfBoundsException e) {}

            // 直方向
            try {
                int[] buf = new int[6];
                for (int j = 0; j < 6; j++) {
                    buf[j] = board[x + i - j][y];
                }
                result.add(buf);
            } catch (IndexOutOfBoundsException e) {}
            try {
                int[] buf = new int[6];
                for (int j = 0; j < 6; j++) {
                    buf[j] = board[x - i + j][y];
                }
                result.add(buf);
            } catch (IndexOutOfBoundsException e) {}
            try {
                int[] buf = new int[6];
                for (int j = 0; j < 6; j++) {
                    buf[j] = board[x][y + i - j];
                }
                result.add(buf);
            } catch (IndexOutOfBoundsException e) {}
            try {
                int[] buf = new int[6];
                for (int j = 0; j < 6; j++) {
                    buf[j] = board[x][y - i + j];
                }
                result.add(buf);
            } catch (IndexOutOfBoundsException e) {}
        }
        return result;
    }

    private boolean checkLivingFourInArray(int targetValue, int backgroundValue, int[] array) {
        if(array.length != 6) {
            return false;
        }
        return ( array[0] == backgroundValue )
                && ( array[1] == targetValue)
                && ( array[2] == targetValue)
                && ( array[3] == targetValue)
                && ( array[4] == targetValue)
                && ( array[5] == backgroundValue);
    }

    private boolean checkLivingThreeInArray(int targetValue, int backgroundValue, int[] array) {
        if(array.length != 6) {
            return false;
        }
        return (( array[0] == backgroundValue )
                && ( array[1] == backgroundValue)
                && ( array[2] == targetValue)
                && ( array[3] == targetValue)
                && ( array[4] == targetValue)
                && ( array[5] == backgroundValue))
            ||(( array[0] == backgroundValue )
                && ( array[1] == targetValue)
                && ( array[2] == backgroundValue)
                && ( array[3] == targetValue)
                && ( array[4] == targetValue)
                && ( array[5] == backgroundValue))
            ||(( array[0] == backgroundValue )
                && ( array[1] == targetValue)
                && ( array[2] == targetValue)
                && ( array[3] == backgroundValue)
                && ( array[4] == targetValue)
                && ( array[5] == backgroundValue))
            ||(( array[0] == backgroundValue )
                && ( array[1] == targetValue)
                && ( array[2] == targetValue)
                && ( array[3] == targetValue)
                && ( array[4] == backgroundValue)
                && ( array[5] == backgroundValue));
    }

    public void setInitiative(boolean initiative) {
        this.initiative = initiative;
    }
    public boolean getInitiative() {
        return this.initiative;
    }
}
