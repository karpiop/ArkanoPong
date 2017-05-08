/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arkanopong;

import javax.swing.*;
import javax.xml.ws.spi.Invoker;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;

public class Client extends JComponent implements KeyListener {
    private boolean openedProperly = true;
    private static final long MIN_PING_TO_PREDICT = 20;
    private int key;
    private List<Pad> pads = new LinkedList<>();
    private PadType padType;
    private List<Block> blocks = new LinkedList<>();
    private List<Ball> balls = new LinkedList<>();
    private final Object padsLock = new Object();
    private final Object blocksLock = new Object();
    private final Object ballsLock = new Object();
    private int scoreBoard[] = new int[4];
    private DataOutputStream dataOutputStream;
    private static final int PORT = 1067;
    private ObjectInputStream objectInputStream;
    private boolean waitToStart = true;
    private int GAME_BOARD_SIZE;
    private long lastRefreshed = 0;
    private long lastRepainted = 0;
    private final Object lastRepaintedLock = new Object();
    private LinkedList<Long> pingList = new LinkedList<>();
    private int ping = 0;

    public Client(String server) {
        int padT;
        try {
            Socket socket = new Socket(server, PORT);

            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            padT = (int) objectInputStream.readObject();

            padType = PadType.valueOf(padT);
            GAME_BOARD_SIZE = (int) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            openedProperly = false;
            e.printStackTrace();
            return;
        }

        this.setFocusable(true);
        this.addKeyListener(this);
        key = 0;
        try {
            dataOutputStream.writeInt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOpenedProperly() {
        return openedProperly;
    }

    public int getKey() {
        return key;
    }

    public PadType getPadType() {
        return padType;
    }

    public void createWindow(JFrame window) {
        window.add(this);
        window.getContentPane().add(this);
        window.setFocusable(true);
        window.addKeyListener(this);

        setVisible(true);

    }

    public void readFromSocket() throws InterruptedException {
        while (true) {
            try {
                GameStatus gameStatus = (GameStatus) objectInputStream.readObject();
                if (gameStatus != null) {
                    new Thread(() -> refresh(gameStatus)).run();
                    waitToStart = false;
                }

                dataOutputStream.writeInt(key);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
//            Thread.sleep(300);
        }
    }

    public void refresh(GameStatus status) {
        List<Pad> pads = status.getPads();
        List<Block> blocks = status.getBlocks();
        List<Ball> balls = status.getBalls();
        int scoreBoard[] = status.getScoreBoard();
//        synchronized (lastRepaintedLock) {
            synchronized (padsLock) {
                this.pads.clear();
                for (Pad pad : pads)
                    this.pads.add(new Pad(pad, this.padType, GAME_BOARD_SIZE));
            }
            synchronized (blocksLock) {
                this.blocks.clear();
                for (Block block : blocks)
                    this.blocks.add(new Block(block, this.padType, GAME_BOARD_SIZE));
            }
            synchronized (ballsLock) {
                this.balls.clear();
                for (Ball ball : balls)
                    this.balls.add(new Ball(ball, this.padType));
            }

            this.scoreBoard = scoreBoard;

            lastRefreshed = System.currentTimeMillis();
            lastRepainted = lastRefreshed;
            repaint();
//        }
    }

    public void painterThread() {
        while (true) {
//            repaint();

            /*long tick = System.currentTimeMillis();

            if (tick - lastRepainted > MIN_PING_TO_PREDICT) {
                synchronized (lastRepaintedLock) {
                    synchronized (padsLock) {
                        for (Pad pad : pads) {
                            pad.move(tick - lastRepainted);
                        }
                    }
                    synchronized (ballsLock) {
                        for (Ball ball : balls) {
                            ball.setTickTack(tick - lastRepainted);
                            ball.move();
                        }
                    }
                    lastRepainted = System.currentTimeMillis();

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }*/

            repaint();

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void paintPads(Graphics g) {
        for (Pad pad : pads) {
            g.setColor(pad.getColor());
            g.fillRect((int) pad.getX(), (int) pad.getY(), pad.getWidth(), pad.getHeight());
        }
    }

    private void paintBlocks(Graphics g) {
        g.setColor(Color.RED);
        for (Block block : blocks) {
            if (block.isInvincible()) {
                g.setColor(Color.GREEN);
                g.fillRect((int) block.getX(), (int) block.getY(), block.getWidth(), block.getHeight());
            } else {
                g.setColor(Color.BLACK);
                g.fillRect((int) block.getX(), (int) block.getY(), block.getWidth(), block.getHeight());
                g.setColor(Color.RED);
                g.fillRect((int) block.getX() + 1, (int) block.getY() + 1, block.getWidth() - 2, block.getHeight() - 2);
            }
        }
    }

    private void paintBalls(Graphics g) {
        g.setColor(Color.BLUE);
        for (Ball ball : balls)
            g.fillOval(ball.getX() - ball.getRadius(), ball.getY() - ball.getRadius(), 2 * ball.getRadius(), 2 * ball.getRadius());
    }

    private void paintPoints(Graphics g) {
        g.setFont(new Font("default", Font.BOLD, 14));
        g.drawString("Punktacja", 670, 100);
        for (int i = 0; i < 4; i++) {
            if (padType.ordinal() == i)
                g.setFont(new Font("default", Font.BOLD, 12));
            else
                g.setFont(new Font("default", Font.PLAIN, 12));
            g.drawString("Gracz " + (i + 1) + ":", 650, 120 + i * 20);
            g.drawString(Integer.toString(scoreBoard[i]), 700, 120 + i * 20);
        }
    }

    @Override
    public void paint(Graphics g) {
        int max=0;
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, GAME_BOARD_SIZE, GAME_BOARD_SIZE);
        boolean allPadsWithoutBalls = true;
        synchronized (padsLock) {
            for (Pad pad : pads) {
                if (pad.isWithBall()) {
                    allPadsWithoutBalls = false;
                    break;
                }
            }
        }

        if (waitToStart) {
            g.drawString("OCZEKIWANIE NA PODŁĄCZENIE WSZYSTKICH GRACZY", 100, 280);
            return;
        } else if (balls.isEmpty() && allPadsWithoutBalls) {
            
            g.setFont(new Font("default",Font.PLAIN, 20));
            for(int i=1; i<4; i++)
                if(scoreBoard[i]>scoreBoard[max])
                    max=i;
            if(padType.ordinal()==max)
            {
                g.setColor(Color.GREEN);
                g.fillRect(0,0, GAME_BOARD_SIZE,GAME_BOARD_SIZE);
                g.setColor(Color.BLACK);
                g.drawString("WYGRANA", 270, 280);
            }
            else
            {
                g.setColor(Color.RED);
                g.fillRect(0,0, GAME_BOARD_SIZE,GAME_BOARD_SIZE);
                g.setColor(Color.BLACK);
                g.drawString("PRZEGRANA", 260, 280);
            }
            g.drawString("KONIEC GRY", 260, 100);
            
            
            paintPoints(g);
            return;
        }

        pingList.add(System.currentTimeMillis() - lastRefreshed);
        if (pingList.size() == 200) {
            int pingAvg = 0;
            for (Long ping : pingList) {
                pingAvg += ping;
            }
            ping = pingAvg / pingList.size();
            pingList.clear();
        }

        g.drawString("Ping: " + ping, 670, 70);


        paintPoints(g);

        synchronized (padsLock) {
            paintPads(g);
        }
        synchronized (blocksLock) {
            paintBlocks(g);
        }
        synchronized (ballsLock) {
            paintBalls(g);
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int a = ke.getKeyCode();
        if (a == VK_LEFT || a == VK_RIGHT || a == ' ')
            key = a;
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        key = 0;
    }
}
