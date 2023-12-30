package main;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author GodofOrange
 * @see 图形界面
 */
public class Windows extends JFrame implements MouseListener {
    public Core core;
    private static final long serialVersionUID = 1L;
    private int var = 2;
    private final int coreSizeX = 15;
    private final int coreSizeY = 15;
    // 先後手的禁手檢測器
    private int initiative = this.var;
    private ForbiddenChecker initiativeChecker;
    private ForbiddenChecker goteChecker;

    public Windows(String title) {
        super(title);
        core = new Core(coreSizeX, coreSizeY);
        this.setSize(800, 600);
        this.setLocation(400, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.addMouseListener(this);
        this.initiativeChecker = new ForbiddenChecker(true);
        this.goteChecker = new ForbiddenChecker(false);
    }

    @Override
    public void paint(Graphics g) {
        // TODO Auto-generated method stub
        super.paint(g);
        // 横
        for (int i = 0; i < coreSizeX; i++)
            g.drawLine(30, 60 + i * 30, 30 + (coreSizeY - 1) * 30, 60 + i * 30);
        // 竖线
        for (int i = 0; i < coreSizeY; i++)
            g.drawLine(30 + i * 30, 60, 30 + i * 30, 60 + (coreSizeX - 1) * 30);

        int[][] board = core.getCore();
        for (int i = 0; i < coreSizeX; i++) {
            for (int j = 0; j < coreSizeY; j++) {
                if (board[i][j] == 1)
                    g.drawOval(20 + i * 30, 50 + j * 30, 20, 20);
                if (board[i][j] == 2)
                    g.fillOval(20 + i * 30, 50 + j * 30, 20, 20);
            }
        }
        g.drawRect(690, 60, 50, 30);
        g.drawString("悔棋", 700, 80);
        g.drawRect(690, 120, 50, 30);
        g.drawString("开始", 700, 140);
        g.drawRect(690, 180, 50, 30);
        g.drawString("设置", 700, 200);
        g.drawString("Code by 秃桔子 QQ:1243137612", 600, 260);
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        if (e.getX() < (45 + (coreSizeY - 1) * 30)
        && e.getY() < (75 + (coreSizeX - 1) * 30)
        && e.getX() > 15 && e.getY() > 45 ) {
            int x = _CgetX(e.getX());
            int y = _CgetY(e.getY());
            if(!core.__CanInput(x, y)) {
                return;
            }
            // 檢查禁手
            Move move = new Move(x, y, var);
            if(var == this.initiative && this.initiativeChecker.check(core, move)) {
                JOptionPane.showMessageDialog(null, this.initiativeChecker.message, "禁手", JOptionPane.DEFAULT_OPTION);
            } else if(var != this.initiative && this.goteChecker.check(core, move)) {
                JOptionPane.showMessageDialog(null, this.goteChecker.message, "禁手", JOptionPane.DEFAULT_OPTION);
            }
            int a = core.ChessIt(x, y, var);
            this.repaint();
            if (a == 1) {
                JOptionPane.showMessageDialog(null,"白的赢了", "恭喜", JOptionPane.DEFAULT_OPTION);;
            }
            if(a==2) {
                JOptionPane.showMessageDialog(null,"黑的赢了", "恭喜", JOptionPane.DEFAULT_OPTION);;
            }
            if(a!=-1) {
                if(var==1) var=2;
                else if(var==2) var=1;
                // 印出盤面
                this.printCore();
            }
        }
        else if(e.getX()>690&&e.getX()<760&&e.getY()>60&&e.getY()<90) {
            core.RetChess();
            if(var==1) var=2;
            else if(var==2) var=1;
            this.repaint();
        }
        if(e.getX()>690&&e.getX()<760&&e.getY()>120&&e.getY()<150) {
            core.Restart();
            this.repaint();
        }
        if(e.getX()>690&&e.getX()<760&&e.getY()>180&&e.getY()<210) {
            Object[] options = {"白先","黑先"};
            int n = JOptionPane.showOptionDialog(null,"红先还是黑先？","游戏设置",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE, null,options,options[0]);
            if(n==0) this.var=1;
            if(n==1) this.var=2;
            this.initiative = this.var;
            this.core.Restart();
            this.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    private int _CgetX(int x) {
        x -= 30;
        if (x % 15 <= 7)
            return x / 30;
        else
            return x / 30 + 1;
    }

    private int _CgetY(int y) {
        y -= 60;
        if (y % 15 <= 7)
            return y / 30;
        else
            return y / 30 + 1;
    }

    private void printCore() {
        for (int[] cx : this.core.getCore()) {
            for (int cy : cx) {
                System.out.print(cy + "");
            }
            System.out.println();
        }
    }
}