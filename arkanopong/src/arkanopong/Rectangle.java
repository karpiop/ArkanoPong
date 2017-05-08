package arkanopong;

import java.io.Serializable;

public class Rectangle implements Serializable{

    private double x;
    private double y;
    private int width;
    private int height;
    protected static int GAME_BOARD_SIZE;


    public Rectangle() {
    }

    public Rectangle(int x, int y, int width, int height, int gameBoardSize) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        GAME_BOARD_SIZE = gameBoardSize;
    }

    public Rectangle(Rectangle rectangle, PadType padType, int gameBoardSize) {        
        GAME_BOARD_SIZE = gameBoardSize;
        switch (padType) {
            case DOWN:
                x = rectangle.getX();
                y = rectangle.getY();
                width = rectangle.getWidth();
                height = rectangle.getHeight();
                return;
            case LEFT:
                x = rectangle.getY();
                y = GAME_BOARD_SIZE - rectangle.getX() - rectangle.getWidth();
                width = rectangle.getHeight();
                height = rectangle.getWidth();
                return;
            case UP:
                x = GAME_BOARD_SIZE - rectangle.getX() - rectangle.getWidth();
                y = GAME_BOARD_SIZE - rectangle.getY() - rectangle.getHeight();
                width = rectangle.getWidth();
                height = rectangle.getHeight();
                return;
            case RIGHT:
                x = GAME_BOARD_SIZE - rectangle.getY() - rectangle.getHeight();
                y = rectangle.getX();
                width = rectangle.getHeight();
                height = rectangle.getWidth();
                return;
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double collision(Ball ball) {
        return 1;
    }
}
