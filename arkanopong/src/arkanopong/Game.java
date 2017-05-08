/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arkanopong;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

import static java.awt.event.KeyEvent.*;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    //    private Client client;
    private List<Pad> pads = Collections.synchronizedList(new ArrayList<>());
    private List<Block> blocks = Collections.synchronizedList(new ArrayList<>());
    private List<Ball> balls = Collections.synchronizedList(new ArrayList<>());
    private final Object gameInfoLock = new Object();
    private static final int BOARD_SIZE = 600;
    private static final int CORNER_SIZE = 90;
    private static final int BLOCK_POINTS = 5;
    private static final int LOST_BALL_POINTS = 20;
    private int scoreBoard[] = new int[4];
    private static final int PORT = 1067;

    public Game() {
        for (int i = 0; i < 4; i++) {
            scoreBoard[i] = 0;
        }

//        for(double i=-0.2; i<=0.2; i+=0.003)
//            balls.add(new Ball(300, 300, (0.5 + i) * Math.PI, PadType.DOWN));

//        readBlocksFromFile();
        addDefaultBlocks();
        createInvincibleBlocks();

        //for(double i=200; i<=400; i+=2)
        //    balls.add(new Ball(i, 100, 5, 1, 0.5*Math.PI));

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            for (int i = 0; i < 4; i++) {
                Socket socket = serverSocket.accept();

                final int finalI = i;
                new Thread(() -> serveClient(finalI, socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        client = new Client(PadType.UP);
//        client = new Client("localhost");
    }

    private void serveClient(int padID, Socket socket) {
        DataInputStream dataInputStream;
        ObjectOutputStream objectOutputStream;
        PadType padType = PadType.valueOf(padID);
        Pad thisPad = new Pad(padType, BOARD_SIZE);
        pads.add(thisPad);
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(padID);
            objectOutputStream.writeObject(BOARD_SIZE);
            objectOutputStream.writeObject(null);

            long tick = System.currentTimeMillis();
            int key;
            while (pads.size() != 4) {
                Thread.sleep(10);
            }
            while (true) {
                long tack = tick;
                tick = System.currentTimeMillis();
                long toe = tick - tack;

                key = dataInputStream.readInt();
                synchronized (gameInfoLock) {
                    thisPad.clearMovementDirection();

                    if (key == VK_RIGHT) {
                        thisPad.moveRight(toe);
                    } else if (key == VK_LEFT) {
                        thisPad.moveLeft(toe);
                    } else if (key == ' ') {
                        releaseBall(padType);
                    }
                }

                synchronized (gameInfoLock) {
                    GameStatus gameStatus = new GameStatus(pads, blocks, balls, scoreBoard);
                    objectOutputStream.reset();
                    objectOutputStream.writeObject(gameStatus);
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int getBoardSize() {
        return BOARD_SIZE;
    }

    public static int getCornerSize() {
        return CORNER_SIZE;
    }

    public static int getBlockPoints() {
        return BLOCK_POINTS;
    }

    public static int getLostBallPoints() {
        return LOST_BALL_POINTS;
    }

//    public Client getClient() {
//        return client;
//    }

    private void createInvincibleBlocks() {
        blocks.add(new Block(0, 0, 30, CORNER_SIZE, true, BOARD_SIZE));
        blocks.add(new Block(0, 0, CORNER_SIZE, 30, true, BOARD_SIZE));
        blocks.add(new Block(BOARD_SIZE - CORNER_SIZE, 0, CORNER_SIZE, 30, true, BOARD_SIZE));
        blocks.add(new Block(BOARD_SIZE - 30, 0, 30, CORNER_SIZE, true, BOARD_SIZE));
        blocks.add(new Block(0, BOARD_SIZE - CORNER_SIZE, 30, CORNER_SIZE, true, BOARD_SIZE));
        blocks.add(new Block(0, BOARD_SIZE - 30, CORNER_SIZE, 30, true, BOARD_SIZE));
        blocks.add(new Block(BOARD_SIZE - 30, BOARD_SIZE - CORNER_SIZE, 30, CORNER_SIZE, true, BOARD_SIZE));
        blocks.add(new Block(BOARD_SIZE - CORNER_SIZE, BOARD_SIZE - 30, CORNER_SIZE, 30, true, BOARD_SIZE));
    }

    private void readBlocksFromFile() {
        try {
            File file = new File("plik.txt");
            Scanner in = new Scanner(file);

            int x, y, width;
            int ile = Integer.parseInt(in.nextLine());
            for (int i = 0; i < ile; i++) {
                x = Integer.parseInt(in.nextLine());
                y = Integer.parseInt(in.nextLine());
                width = Integer.parseInt(in.nextLine());
                Block b = new Block(x, y, width, BOARD_SIZE);
                this.blocks.add(b);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void addDefaultBlocks() {
        int x, y, width;
        int ile = 36;
        for (int i = 0; i*i < ile; i++) {
            for (int j = 0; j*j < ile; j++) {
                x = 150+50*i;
                y = 150+50*j;
                width = 50;
                Block b = new Block(x, y, width, BOARD_SIZE);
                this.blocks.add(b);
            }
        }
    }

    public void ballLost() {
        List<Ball> ballsToBeRemoved = new ArrayList<>();
        PadType padType;
        for (Ball ball : balls) {
            padType = ball.lostBall();
            if (padType != null) {
                ballsToBeRemoved.add(ball);
                scoreBoard[padType.ordinal()] -= getLostBallPoints();
            }
        }

        for (Ball ball : ballsToBeRemoved)
            balls.remove(ball);
    }

    public void blockCollisions() {
        List<Block> blocksToBeRemoved = new ArrayList<>();

        for (Ball ball : balls) {
            boolean bounced = false;
            for (Block block : blocks) {
                if (ball.collisionUDAngle(block) != 0) {
                    ball.bounceUD();
                    bounced = true;
                } else if (ball.collisionLRAngle(block) != 0) {
                    ball.bounceLR();
                    bounced = true;
                } else if (ball.cornerCollisionAngle(block) != 0) {
                    ball.bounceLR();
                    ball.bounceUD();
                    bounced = true;
                }

                if (bounced) {
                    if (!block.isInvincible()) {
                        blocksToBeRemoved.add(block);
                        scoreBoard[ball.getWhose().ordinal()] += getBlockPoints();
                    }
                    break;
                }
            }
        }

        for (Block block : blocksToBeRemoved)
            blocks.remove(block);
    }

    public void padCollisions() {
        for (Ball ball : balls) {
            for (Pad pad : pads) {
                double UDAngle = ball.collisionUDAngle(pad);
                double LRAngle = ball.collisionLRAngle(pad);
                double cornerAngle = ball.cornerCollisionAngle(pad);

                if (pad.getPadType() == PadType.DOWN || pad.getPadType() == PadType.UP) {
                    if (UDAngle != 0)
                        ball.setAngle(UDAngle);
                    if (LRAngle != 0)
                        ball.bounceLR();
                } else {
                    if (UDAngle != 0)
                        ball.bounceUD();
                    if (LRAngle != 0)
                        ball.setAngle(LRAngle);
                }

                if (cornerAngle != 0)
                    ball.setAngle(cornerAngle);

                if (UDAngle != 0 || LRAngle != 0 || cornerAngle != 0)
                    ball.setWhose(pad.getPadType());
            }
        }
    }

    public void collisions() {
        blockCollisions();
        padCollisions();
    }

    private Pad findPad(PadType padType) {
        for (Pad pad : pads)
            if (pad.getPadType() == padType)
                return pad;
        throw new IllegalStateException("Pad not found");
    }

    private void releaseBall(PadType padType) {
//        Pad mypad = findPad(client.getPadType());
        Pad myPad = findPad(padType);
        if (myPad.isWithBall()) {
//            switch (client.getPadType()) {
            switch (padType) {
                case DOWN:
                    balls.add(new Ball(myPad.getX() + myPad.getPadWidth() / 2, myPad.getY(), Ball.getAngleInit(), PadType.DOWN));
                    myPad.startBall();
                    break;
                case LEFT:
                    balls.add(new Ball(myPad.getX() + myPad.getPadHeight(), myPad.getY() + myPad.getPadWidth() / 2, Math.PI * 3 / 2 + Ball.getAngleInit(), PadType.LEFT));
                    myPad.startBall();
                    break;
                case UP:
                    balls.add(new Ball(myPad.getX() + myPad.getPadWidth() / 2, myPad.getY() + myPad.getPadHeight(), Math.PI + Ball.getAngleInit(), PadType.UP));
                    myPad.startBall();
                    break;
                case RIGHT:
                    balls.add(new Ball(myPad.getX(), myPad.getY() + myPad.getPadWidth() / 2, Math.PI / 2 + Ball.getAngleInit(), PadType.RIGHT));
                    myPad.startBall();
                    break;

            }
        }
    }

    public void play() throws InterruptedException, IOException {
        long tick = System.currentTimeMillis();
        while (true) {
            long tack = tick;
            tick = System.currentTimeMillis();
            long toe = tick - tack;

            synchronized (gameInfoLock) {
                balls.forEach(ball -> ball.setTickTack(toe));
                collisions();
                ballLost();

                balls.forEach(Ball::move);
            }
        }
    }

}
