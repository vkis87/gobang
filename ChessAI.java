package main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class ChessAI extends java.util.Vector {
    private int []Bestmove=new int[2];
    private int AI_SEARCH_DEPTH = 4;
    private int AI_LIMITED_MOVE_NUM = 20;
    private int FIVE,FOUR,THREE,TWO,SFOUR,STHREE,STWO;
    private  int SCORE_MAX = 0x7fffffff,SCORE_MIN = -1 * SCORE_MAX;
    private int SCORE_FIVE=100000, SCORE_FOUR=10000, SCORE_SFOUR =1000;
    private int SCORE_THREE=100, SCORE_STHREE=10, SCORE_TWO=8, SCORE_STWO =  2;
    private int [][][]record=new int[15][15][4];
    private int [][]count=new int[2][8];
    
    // 檢查是否獲勝
    public boolean CheckWin(int [][]board,int turn){
        if(Evaluate(board,turn)>=SCORE_FIVE)
            return true;
        else
            return false;
    }
    class CHESS_TYPE{
        int NONE = 0,
                SLEEP_TWO = 1,
                LIVE_TWO = 2,
                SLEEP_THREE = 3,
                LIVE_THREE = 4,
                CHONG_FOUR = 5,
                LIVE_FOUR = 6,
                LIVE_FIVE = 7;
    }

    class Point{
        int score,x,y;
        private Point(int score,int x,int y){
            this.score=score;
            this.x=x;
            this.y=y;
        }
    }

    private void Reset(){
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                for (int k = 0; k < 4; k++) {
                    record[i][j][k]=0;
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                count[i][j]=0;
            }
        }
    }

    public ChessAI(){
        CHESS_TYPE type=new CHESS_TYPE();
        FIVE=type.LIVE_FIVE;
        FOUR=type.LIVE_FOUR;
        THREE=type.LIVE_THREE;
        TWO=type.LIVE_TWO;
        SFOUR=type.CHONG_FOUR;
        STHREE=type.SLEEP_THREE;
        STWO=type.SLEEP_TWO;
        Bestmove[0]=8;
        Bestmove[1]=8;
        Reset();
    }
    // 找到最佳搜尋結果，並且return最佳走步
    public int[] FindBestmove(int [][]board,int turn){
        int score=Alpha_Beta(board,turn,AI_SEARCH_DEPTH,SCORE_MIN,SCORE_MAX);
//        System.out.println(score);
        return Bestmove;
    }
    // 審局函數
    private int Evaluate(int [][]board,int turn){
        int score,mine,opponent;
        Reset();
        if (turn==1){
            mine=1;
            opponent=2;
        }
        else{
            mine=2;
            opponent=1;
        }
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y <15 ; y++) {
                if(board[x][y]==mine){
//                    System.out.println("X+Y"+x+" "+y);
                    EvaluatePoint(board,x,y,mine,opponent,false);
                }
                else if(board[x][y]==opponent){
//                    System.out.println("X+Y"+x+" "+y);
                    EvaluatePoint(board,x,y,opponent,mine,false);
                }
            }
        }
        int []mine_count=count[mine-1];
        int []opponent_count=count[opponent-1];
        score=GetScore(mine_count,opponent_count);
        return score;
    }


    private void EvaluatePoint(int[][]board,int x,int y,int mine,int opponent,boolean ex){
        int [][]offset=new int[][]{{1,0},{0,1},{1,1},{1,-1}};//从左到右判断一条线
//        if(ex)
//            System.out.println(111);
        for (int i = 0; i < 4; i++) {
            if (record[x][y][i]==0 ||ex){
                AnalysisLine(board,x,y,i,offset[i],mine,opponent);
            }
        }

    }


    private int AnalysisLine(int [][]board,int x,int y,int index,int []offset,int mine,int opponent){
        int right=4,left=4;
        int empty=0;
        int []line=GetLine(board,x,y,offset,mine,opponent);
//        for (int i = 0; i < 9; i++) {
//            System.out.print(line[i]);
//        }
//        System.out.println();
        //记录己方连在一起的棋子
        while (right<8){
            if(line[right+1]!=mine){
                break;
            }
            right+=1;
        }
        while (left>0){
            if (line[left-1]!=mine)
                break;
            left-=1;
        }
        //记录己方的空格
        int right_range=right,left_range=left;
        while (right_range<8){
            if (line[right_range+1]==opponent)
                break;
            right_range+=1;
        }
        while (left_range>0){
            if (line[left_range-1]==opponent)
                break;
            left_range-=1;
        }
        int chess_range = right_range - left_range + 1;//己方有效棋子
        if(chess_range<5){
            setRecord(x, y, left_range, right_range, index, offset);
            return 0;
        }
        //记录已经分析过的棋子
        setRecord( x, y, left, right, index, offset);
        //记录一色点
        int m_range = right - left + 1;
        //长连
        if(m_range>=5)
            count[mine-1][FIVE]+=1;
        //活四和冲四
//         Live Four : XMMMMX
////       Chong Four : XMMMMP, PMMMMX
        if(m_range==4){
            boolean left_empty =false, right_empty = false;
            if (line[left - 1] == empty)
                left_empty = true;
            if (line[right + 1] == empty)
                right_empty = true;
            if (left_empty && right_empty)
                count[mine-1][FOUR] += 1;
            else if (left_empty || right_empty)
                count[mine-1][SFOUR] += 1;
        }
//        # Chong Four : MXMMM, MMMXM, the two types can both exist
//        # Live Three : XMMMXX, XXMMMX
//        # Sleep Three : PMMMX, XMMMP, PXMMMXP
        if(m_range==3){
            boolean left_empty=false,  right_empty = false;
            boolean left_four=false , right_four = false;
            if (line[left - 1] == empty){
                if (line[left - 2] == mine){//  # MXMMM
                    setRecord( x, y, left - 2, left - 1, index, offset);
                    count[mine-1][SFOUR] += 1;
                    left_four = true;
                }
                left_empty=true;
            }
            if (line[right + 1] == empty){
                if (line[right + 2] == mine){//MMMXM
                    setRecord( x, y, right + 1, right + 2, index, offset);
                    count[mine-1][SFOUR] += 1;
                    right_four = true;
                }
                right_empty = true;
            }
            if (left_four || right_four){

            }
            else if(left_empty && right_empty){
                if (chess_range > 5)       //# XMMMXX, XXMMMX
                    count[mine-1][THREE] += 1;
                else   //# PXMMMXP
                    count[mine-1][STHREE] += 1;
            }
            else if(left_empty || right_empty) //PMMMX, XMMMP
                count[mine-1][STHREE] += 1;
        }
//        # Chong Four: MMXMM, only check right direction
////        # Live Three: XMXMMX, XMMXMX the two types can both exist
////        # Sleep Three: PMXMMX, XMXMMP, PMMXMX, XMMXMP
////        # Live Two: XMMX
////        # Sleep Two: PMMX, XMMP
        if(m_range==2){
            boolean left_empty =false, right_empty = false;
            boolean left_three = false,right_three = false;
            if (line[left - 1] == empty) {
                if (line[left - 2] == mine) {
                    setRecord(x, y, left - 2, left - 1, index, offset);
                    if (line[left - 3] == empty) {
                        if (line[right + 1] == empty) { //XMXMMX
                            count[mine - 1][THREE] += 1;
                        } else {
                            count[mine-1][STHREE] += 1;
                        }
                        left_three = true;
                    } else if (line[left - 3] == opponent) { //# PMXMMX
                        if (line[right + 1] == empty) {
                            count[mine - 1][STHREE] += 1;
                            left_three = true;
                        }
                    }
                }
                left_empty = true;
            }
            if(line[right + 1] == empty){
                if(line[right + 2] == mine){
                    if(line[right + 3] == mine){ //MMXMM
                        setRecord( x, y, right + 1, right + 2, index, offset);
                        count[mine-1][SFOUR] += 1;
                        right_three = true;
                    }
                    else if(line[right + 3] == empty){
                        if (left_empty){ //XMMXMX
                            count[mine-1][THREE] += 1;
                        }
                        else
                            count[mine-1][STHREE]+=1;
                        right_three = true;
                    }
                    else if(left_empty){ //XMMXMP
                        count[mine-1][STHREE] += 1;
                        right_three = true;
                    }
                }
                right_empty = true;

            }
            if(left_three || right_three){

            }
            else if(left_empty && right_empty){ //XMMX
                count[mine-1][TWO] += 1;
            }
            else if(left_empty || right_empty){ //# PMMX, XMMP
                count[mine-1][STWO] += 1;
            }
        }
//        Live Two: XMXMX, XMXXMX only check right direction
//        # Sleep Two: PMXMX, XMXMP
        if(m_range==1){
            boolean left_empty =false,right_empty = false;
            if(line[left - 1] == empty){
                if(line[left - 2] == mine){
                    if(line[left - 3] == empty){
                        if(line[right + 1] == opponent){ //XMXMP
                            count[mine-1][STWO] += 1;
                        }
                    }
                }
                left_empty = true;
            }
            if(line[right + 1] == empty){
                if(line[right + 2] == mine){
                    if(line[right + 3] == empty){
                        if(left_empty){ //XMXMX
                            count[mine-1][TWO] += 1;
                        }
                        else {  //PMXMX
                            count[mine-1][STWO] += 1;
                        }
                    }
                }
                else  if(line[right + 2] == empty){
                    if(line[right + 3] == mine && line[right + 4] == empty){ //XMXXMX
                        count[mine-1][TWO] += 1;
                    }
                }
            }
        }
        return 0;


    }


    private void setRecord(int x,int  y,int  left, int right, int dir_index, int []dir_offset){
        int tmp_x = x + (-5 + left) * dir_offset[1];
        int tmp_y = y + (-5 + left) * dir_offset[0];
        for (int i=left;i<=right;i++){
            tmp_x += dir_offset[1];
            tmp_y += dir_offset[0];
        }
        record[tmp_x][tmp_y][dir_index] = 1;
    }


    private int GetScore(int []mine_count,int []op_count){
        int score=0,mscore=0,oscore=0;
        if(mine_count[FIVE]>0)
            return SCORE_FIVE;
        if(op_count[FIVE]>0)
            return -SCORE_FIVE;
        if(mine_count[SFOUR]>=2)
            mine_count[FOUR]+=1;
        if(op_count[SFOUR]>=2)
            op_count[FOUR]+=1;
        if(mine_count[FOUR]>0)
            return 9050;
        if(mine_count[SFOUR]>0)
            return 9040;
        if(op_count[FOUR]>0)
            return -9030;
        if(op_count[SFOUR]>0&&op_count[THREE]>0)
            return -9020;
        if(mine_count[THREE]>0&&op_count[SFOUR]==0)
            return 9010;
        if (op_count[THREE] > 1 && mine_count[THREE] == 0 && mine_count[STHREE] == 0)
            return -9000;
        if (op_count[SFOUR] > 0)
            oscore += 400;

        if (mine_count[THREE] > 1)
            mscore += 500;
        else if (mine_count[THREE] > 0)
            mscore += 100;
        if (op_count[THREE] > 1)
            oscore += 2000;
        else if(op_count[THREE]>0)
            oscore += 400;

        if (mine_count[STHREE] > 0)
            mscore += mine_count[STHREE] * 10;
        if (op_count[STHREE] > 0)
            oscore += op_count[STHREE] * 10;
        if (mine_count[TWO] > 0)
            mscore += mine_count[TWO] * 6;
        if (op_count[TWO] > 0)
            oscore += op_count[TWO] * 6;

        if (mine_count[STWO] > 0)
            mscore += mine_count[STWO] * 2;
        if (op_count[STWO] > 0)
            oscore += op_count[STWO] * 2;

        score=mscore-oscore;
        return score;
    }


    private int[] GetLine(int [][]board, int x, int y, int []offset, int mine, int opponent){
        int []line=new int[9];
        int tmp_x = x + (-5 * offset[1]);
        int tmp_y = y + (-5 * offset[0]);
        for (int i = 0; i < 9; i++) {
            tmp_x += offset[1];
            tmp_y += offset[0];
            if (tmp_x < 0 ||tmp_x >= 15 ||tmp_y < 0 || tmp_y >= 15){
                line[i] = opponent ; //# set out of range as opponent chess
            }
            else
                line[i] = board[tmp_x][tmp_y];
        }

        return line;

    }


    private int  Alpha_Beta(int [][]board,int turn,int depth,int alpha,int beta){
        int op_turn;
        int score=Evaluate(board,turn);
        if(depth<=0||Math.abs(score)>=SCORE_FIVE) {
//            System.out.println("score"+score);
            return score;
        }
        Vector<Point> moves=Genmove(board,turn);
        int []bestmove={0,0};
        if(moves.size()==0)
            return score;
//        System.out.println("11112");
        for (Point point:moves){
//            System.out.println(point.x+" "+point.y);
            board[point.x][point.y]=turn;
            if(turn==1)
                op_turn=2;
            else
                op_turn=1;
            score=-Alpha_Beta(board,op_turn,depth-1,-beta,-alpha);
            board[point.x][point.y]=0;
            if(score>alpha){
                alpha=score;
                bestmove[0]=point.x;
                bestmove[1]=point.y;
                if(alpha>=beta)
                    break;
            }

        }
        if(depth==AI_SEARCH_DEPTH)
            Bestmove=bestmove;
        return alpha;

    }

    private boolean Check_doublethree(int[][] board, Move move)
    {
        ArrayList<ArrayList<Integer>> testTingPosition = genTestingPositions(board, move, false);
        int counter = 0;
        for (ArrayList<Integer> array : testTingPosition) {
            if (checkLivingThreeInArray(move.getChessColor(), 1, array)) {
                counter++;
                if (counter == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean Check_doublefour(int[][] board, Move move)
    {
        ArrayList<ArrayList<Integer>> testTingPosition = genTestingPositions(board, move, false);
        int counter = 0;
        for (ArrayList<Integer> array : testTingPosition) {
            counter += checkFourInArray(move.getChessColor(), 1, array);
            if (counter == 2) {
                return true;
            }
        }
        return false;
    }

    private boolean Check_six(int[][] board, Move move)
    {
        ArrayList<ArrayList<Integer>> testTingPosition = genTestingPositions(board, move, true);
        for (ArrayList<Integer> array : testTingPosition) {
            if (checkSixInArray(move.getChessColor(), array)) {
                return true;
            }
        }
        return false;
    }

    private boolean Forbidden_check(int[][] board, int x, int y, int radius)
    {
        Move move = new Move(x, y, radius);
        if(Check_six(board, move))
        {
            System.out.println("長連");
            return true;
        }
        else if(Check_doublefour(board, move))
        {
            System.out.println("雙四");
            return true;
        }
        else if(Check_doublethree(board, move))
        {
            System.out.println("雙三");
            return true;
        }
        return false;
    }

    private ArrayList<ArrayList<Integer>> genTestingPositions(int[][] board, Move move, boolean sixMode) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        int[][] board_curr = Arrays.copyOf(board, board.length);
        for (int i = 0; i < board.length; i++) {
            board_curr[i] = Arrays.copyOf(board[i], board[i].length);
        }
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
        if(xMax >= board_curr.length) xMax = board_curr.length-1;
        if(yMax >= board_curr[0].length) yMax = board_curr[0].length-1;
        if(xMin < 0) xMin = 0;
        if(yMin < 0) yMin = 0;
        // 落子後的盤面
        board_curr[x][y] = move.getChessColor();
        // 斜向
        int i = x;
        int j = y;
        while (i > xMin && j > yMin) {
            i--;
            j--;
        }
        ArrayList<Integer> buf = new ArrayList<Integer>();
        while (i <= xMax && j <= yMax) {
            buf.add(board_curr[i][j]);
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
            buf.add(board_curr[i][j]);
            i++;
            j--;
        }
        result.add(buf);
        // 縱向
        i = xMin;
        j = y;
        buf = new ArrayList<Integer>();
        while (i <= xMax) {
            buf.add(board_curr[i][j]);
            i++;
        }
        result.add(buf);
        // 橫向
        i = x;
        j = yMin;
        buf = new ArrayList<Integer>();
        while (j <= yMax) {
            buf.add(board_curr[i][j]);
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
        boolean canWin = false;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) == targetValue) {
                counter++;
                if(counter == 1) missCounter = 0;
                if (counter == 4 && (canWin || (i+1 < array.size()) && array.get(i+1) == backgroundValue)) {
                    counter = 1;
                    missCounter = 0;
                    numOfFour++;
                }
            } else if (array.get(i) != backgroundValue) {
                counter = 0;
                missCounter = 0;
                canWin = false;
            } else if (array.get(i) == backgroundValue) {
                if(missCounter == 0) canWin = true;
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
        boolean living = false;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) == targetValue) {
                counter++;
                if(counter == 1) missCounter = 0;
                if (living && counter == 3 && (array.size()-1 >= i+1) && (array.get(i+1) == backgroundValue)) {
                    counter = 1;
                    missCounter = 0;
                    numOfLivingThree++;
                }
            } else if (array.get(i) != backgroundValue) {
                counter = 0;
                missCounter = 0;
                living = false;
            } else if (array.get(i) == backgroundValue) {
                if(counter == 0) living = true;
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


    private Vector<Point> Genmove (int [][]board, int turn){
        Vector<Point> moves=new Vector<Point>();
        Vector<Point> fives=new Vector<>();
        Vector<Point> mfours=new Vector<Point>();
        Vector<Point> ofours=new Vector<>();
        Vector<Point> msfours=new Vector<>();
        Vector<Point> osfours=new Vector<>();
        int radius=1;
        int mine,opponent;
        if(turn==1){
            mine=1;
            opponent=2;
        }
        else{
            mine=2;
            opponent=1;
        }
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                if(turn == 1 && Forbidden_check(board, x, y, radius))
                {
                    continue;
                }
                if(board[x][y]==0&&Hasneighbor(board,x,y,radius)){
//                    System.out.println("xy"+x+" "+y);
                    int []score=EvaluatePointScore(board,x,y,mine,opponent);
                    int mscore=score[0],oscore=score[1];
                    Point point =new Point(Math.max(mscore,oscore),x,y);
//                    System.out.println("score="+mscore+" "+oscore);
                    if(mscore>=SCORE_FIVE||oscore>=SCORE_FIVE)
                        fives.add(point);
                    else if(mscore>=SCORE_FOUR)
                        mfours.add(point);
                    else if(oscore>=SCORE_FOUR)
                        ofours.add(point);
                    else if(mscore>=SCORE_SFOUR)
                        msfours.add(point);
                    else if(oscore>=SCORE_SFOUR)
                        osfours.add(point);
                    moves.add(point);
                }
            }
        }
        if (fives.size()>0)
            return fives;
        if (mfours.size()>0)
            return mfours;
        if(ofours.size()>0 ){
            if(msfours.size()==0)
                return ofours;
            else{
                ofours.addAll(mfours);
                return ofours;
            }
        }
        moves=Sort(moves);
        if(AI_SEARCH_DEPTH>2&&moves.size()>AI_LIMITED_MOVE_NUM)
            moves.subList(AI_LIMITED_MOVE_NUM,moves.size()).clear();
        return moves;
    }

    private boolean Hasneighbor(int [][]board,int x,int y,int radius){
        int start_x=x-radius,end_x=x+radius;
        int start_y=y-radius,end_y=y+radius;
        for (int i = start_x; i <=end_x; i++) {
            for (int j = start_y; j <= end_y; j++) {
                if(i>=0&&i<15&&j>=0&&j<15){
                    if (board[i][j]!=0){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int[] EvaluatePointScore(int [][]board,int x,int y,int mine ,int opponent){
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                count[i][j]=0;
            }
        }
        board[x][y]=mine;
        EvaluatePoint(board,x,y,mine,opponent,true);
        int []mine_count=count[mine-1];
//        System.out.print("count");
//        for (int i = 0; i < mine_count.length; i++) {
//            System.out.print(mine_count[i]);
//        }
//        System.out.println();
        board[x][y]=opponent;
        EvaluatePoint(board,x,y,opponent,mine,true);
        int []op_count=count[opponent-1];
//        System.out.print("count");
//        for (int i = 0; i < op_count.length; i++) {
//            System.out.print(op_count[i]);
//        }
//        System.out.println();
        board[x][y]=0;

        int mscore=GetPointScore(mine_count);
        int oscore=GetPointScore(op_count);
        int []score=new int[2];
        score[0]=mscore;
        score[1]=oscore;
        return score;
    }

    private int GetPointScore(int []count1){
        int score=0;
        if(count1[FIVE]>0)
            return SCORE_FIVE;
        if(count1[FOUR]>0)
            return SCORE_FOUR;

        if(count1[SFOUR]>1)
            score+=count1[SFOUR]*SCORE_SFOUR;
        else if(count1[SFOUR]>0&&count1[THREE]>0)
            score += count1[SFOUR] * SCORE_SFOUR;
        else if(count1[SFOUR]>0)
            score+=SCORE_THREE;

        if(count1[THREE]>1)
            score+=5*SCORE_THREE;
        else if(count1[THREE]>0)
            score+=SCORE_THREE;

        if(count1[STHREE]>0)
            score+=count1[STHREE]*SCORE_STHREE;

        if(count1[TWO]>0)
            score+=count1[TWO]*SCORE_TWO;

        if(count1[STWO]>0)
            score+=count1[STWO]*SCORE_STWO;
        return score;
    }

    private Vector<Point> Sort(Vector<Point> points){
        for (int i = 0; i < points.size() - 1; i++) {
            int temp=i;
            for (int j = i+1; j < points.size(); j++) {
                if(points.get(temp).score<=points.get(j).score)
                    temp=j;
            }
            if(temp!=i){
                Point point=points.get(temp);
                points.set(temp,points.get(i));
                points.set(i,point);
            }
        }
        return points;
    }

    // 座標轉換
    private void Coor_trans(String instruction, int[] result)
    {
        // D -> 3
        // 4 -> 11
        String row = instruction.substring(1);
        String col = instruction.substring(0, 1);
        result[0] = 15 - Integer.parseInt(row);// row
        result[1] = col.charAt(0) - 'A';// column
        
    }
   public static void main(String[] args) {
       try (Scanner sc = new Scanner(System.in)) {
           ChessAI AI=new ChessAI();
           int [][]board=new int[15][15];
           int turn=2, op_turn = 1;
           for (int i = 0; i < 15; i++) {
               for (int j = 0; j < 15; j++) {
                   board[i][j]=0;
               }
           }
           String[] row_sign = {"1", "2", "3", "4", "5", "6", "7", "8"
                                , "9", "10", "11", "12", "13", "14", "15"};
           String[] col_sign = {"A", "B", "C", "D", "E", "F", "G", "H", "I"
                                ,"J", "K", "L", "M", "N", "O"};
           int[] result = {0, 0};
           int[] Bestmove;
           String instruction = "";
           String winner = "";
           while (!instruction.equals("bye")) {
                System.out.println("輸入對手是先手(1)或後手(2)");
                instruction = sc.nextLine();
                if(instruction.equals("bye"))
                {
                    return;
                }
                if(instruction.substring(0).equals("1"))
                {
                    turn = 2;
                    op_turn = 1;
                }
                else if(instruction.substring(0).equals("2"))
                {
                    turn = 1;
                    op_turn = 2;
                }
                else
                {}
                while (!AI.CheckWin(board, turn) && !instruction.equals("bye")) {
                    
                    if(turn == 2)
                    {
                        instruction = sc.nextLine();
                        if(instruction.equals("bye"))
                        {
                            return;
                        }
                        AI.Coor_trans(instruction, result);
                        System.out.println(result[0] + " " + result[1]);
                        board[result[0]][result[1]] = op_turn;
                        for (int i = 0; i < 15; i++) {
                            for (int j = 0; j < 15; j++) {
                                if(j == 0)
                                {
                                    System.out.printf("%2s", row_sign[14-i]);
                                }
                                System.out.printf("%5s", board[i][j]);
                            }
                            System.out.println("\n");
                        }
                        for(int i = 0;i < 15;i++)
                        {
                            if(i == 0)
                            {
                                System.out.print("  ");
                            }
                            System.out.printf("%5s", col_sign[i]);
                        }
                        System.out.println("\n");
                        if(AI.CheckWin(board, op_turn))
                        {
                            winner = "black";
                            System.out.printf("Opponent: %s%s\n", col_sign[14-result[0]], row_sign[result[1]]);
                            break;
                        }
                        Bestmove=AI.FindBestmove(board,turn);
                        System.out.println(Bestmove[0] + " " + Bestmove[1]);
                        board[Bestmove[0]][Bestmove[1]] = turn;
                        for (int i = 0; i < 15; i++) {
                            for (int j = 0; j < 15; j++) {
                                if(j == 0)
                                {
                                    System.out.printf("%2s", row_sign[14-i]);
                                }
                                System.out.printf("%5s", board[i][j]);
                            }
                            System.out.println("\n");
                        }
                        for(int i = 0;i < 15;i++)
                        {
                            if(i == 0)
                            {
                                System.out.print("  ");
                            }
                            System.out.printf("%5s", col_sign[i]);
                        }
                        System.out.printf("\nMine: %s%s\n", col_sign[14-Bestmove[0]], row_sign[Bestmove[1]]);
                        System.out.printf("Opponent: %s%s\n", col_sign[14-result[0]], row_sign[result[1]]);
                        if(AI.CheckWin(board, turn))
                        {
                            winner = "white";
                            break;
                        }
                    }
                    else
                    {
                        Bestmove=AI.FindBestmove(board,turn);
                        System.out.println(Bestmove[0]);
                        System.out.println(Bestmove[1]);
                        board[Bestmove[0]][Bestmove[1]] = turn;
                       for (int i = 0; i < 15; i++) {
                            for (int j = 0; j < 15; j++) {
                                if(j == 0)
                                {
                                    System.out.printf("%2s", row_sign[14-i]);
                                }
                                System.out.printf("%5s", board[i][j]);
                            }
                            System.out.println("\n");
                        }
                        for(int i = 0;i < 15;i++)
                        {
                            if(i == 0)
                            {
                                System.out.print("  ");
                            }
                            System.out.printf("%5s", col_sign[i]);
                        }
                        System.out.println("\n");
                        if(AI.CheckWin(board, turn))
                        {
                            winner = "black";
                            System.out.printf("\nMine: %s%s\n", col_sign[14-Bestmove[0]], row_sign[Bestmove[1]]);
                            break;
                        }
                        instruction = sc.nextLine();
                        if(instruction.equals("bye"))
                        {
                            return;
                        }
                        AI.Coor_trans(instruction, result);
                        System.out.println(result[0] + " " + result[1]);
                        board[result[0]][result[1]] = op_turn;
                        for (int i = 0; i < 15; i++) {
                            for (int j = 0; j < 15; j++) {
                                if(j == 0)
                                {
                                    System.out.printf("%2s", row_sign[14-i]);
                                }
                                System.out.printf("%5s", board[i][j]);
                            }
                            System.out.println("\n");
                        }
                        for(int i = 0;i < 15;i++)
                        {
                            if(i == 0)
                            {
                                System.out.print("  ");
                            }
                            System.out.printf("%5s", col_sign[i]);
                        }
                        System.out.printf("\nMine: %s%s\n", col_sign[14-Bestmove[0]], row_sign[Bestmove[1]]);
                        System.out.printf("Opponent: %s%s\n", col_sign[14-result[0]], row_sign[result[1]]);
                        if(AI.CheckWin(board, op_turn))
                        {
                            winner = "white";
                            break;
                        }
                    }
                }
                
                System.out.println("Winner is " + winner);
           }
           sc.close();
    }
   }
}