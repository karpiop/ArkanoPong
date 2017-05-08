/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arkanopong;


import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

enum PadType {
    DOWN(0),
    LEFT(1),
    UP(2),
    RIGHT(3);
    private int value;
    private static Map map = new HashMap<>();

    private PadType(int value) {
        this.value = value;
    }

    static {
        for (PadType padType : PadType.values()) {
            map.put(padType.value, padType);
        }
    }

    public static PadType valueOf(int padType) {
        return (PadType) map.get(padType);
    }

    public int getValue() {
        return value;
    }
}

public class Pad extends Rectangle implements Serializable {

    //private int position; // pozycja na ekranie
    private PadType padType;
    private double speed;
    private int maxPosition;
    private static final double DEAD_END = 20 * Math.PI / 180;
    private static final double DEAD_MIDDLE = 10 * Math.PI / 180;
    private static final int PAD_WIDTH_INIT = 80;
    private static final int PAD_HEIGHT_INIT = 20;
    private boolean withBall = true;
    private Color color;
    private int movementDirection;


    public Pad(PadType padType, int gameBoardSize) {
        super();
        GAME_BOARD_SIZE = gameBoardSize;
        switch (padType) {
            case DOWN:
                setX(GAME_BOARD_SIZE / 2 - PAD_WIDTH_INIT / 2);
                setY(GAME_BOARD_SIZE - PAD_HEIGHT_INIT - 10);
                setWidth(PAD_WIDTH_INIT);
                setHeight(PAD_HEIGHT_INIT);
                break;
            case LEFT:
                setX(10);
                setY(GAME_BOARD_SIZE / 2 - PAD_WIDTH_INIT / 2);
                setWidth(PAD_HEIGHT_INIT);
                setHeight(PAD_WIDTH_INIT);
                break;
            case UP:
                setX(GAME_BOARD_SIZE / 2 - PAD_WIDTH_INIT / 2);
                setY(10);
                setWidth(PAD_WIDTH_INIT);
                setHeight(PAD_HEIGHT_INIT);
                break;
            case RIGHT:
                setX(GAME_BOARD_SIZE - PAD_HEIGHT_INIT - 10);
                setY(GAME_BOARD_SIZE / 2 - PAD_WIDTH_INIT / 2);
                setWidth(PAD_HEIGHT_INIT);
                setHeight(PAD_WIDTH_INIT);
                break;
        }

        speed = 0.05;
        maxPosition = GAME_BOARD_SIZE;
        this.padType = padType;
        color = Color.BLUE;
        movementDirection = 0;
    }

    public Pad(Pad pad, PadType clientPadType, int gameBoardSize) {
        super(pad, clientPadType, gameBoardSize);
        this.padType = pad.convertPadType(pad.getPadType(), clientPadType);
        speed = pad.getSpeed();
        maxPosition = GAME_BOARD_SIZE;
        this.withBall = pad.isWithBall();
        this.color = pad.getColor();
        this.movementDirection = pad.getMovementDirection();
    }

    private PadType convertPadType(PadType thisPadType, PadType clientPadType) {
        int length = PadType.values().length;
        int clientValue = clientPadType.getValue();
        int thisValue = thisPadType.getValue();
        return PadType.valueOf((thisValue + length - clientValue) % length);
    }

    public boolean isWithBall() {
        return withBall;
    }

    public void startBall() {
        withBall = false;
        color = Color.GRAY;
    }

    public int getPadWidth() {
        return (padType == PadType.DOWN || padType == PadType.UP) ? getWidth() : getHeight();
    }

    public int getPadHeight() {
        return (padType == PadType.DOWN || padType == PadType.UP) ? getHeight() : getWidth();
    }

    public void move(long time) {
        if(movementDirection < 0)
            moveLeft(time);
        else if(movementDirection > 0)
            moveRight(time);
    }

    public void moveLeft(long time) {
        if (getPosition() - this.speed * time > Game.getCornerSize())
            setPosition(getPosition() - this.speed * time);
        else
            setPosition(Game.getCornerSize());
        movementDirection = -1;
    }

    public void moveRight(long time) {
        if (getPosition() + this.speed * time < this.maxPosition - getPadWidth() - Game.getCornerSize())
            setPosition(getPosition() + this.speed * time);
        else
            setPosition(this.maxPosition - getPadWidth() - Game.getCornerSize());
        movementDirection = 1;
    }

    public double getPosition() {
        switch (padType) {
            case DOWN:
                return getX();
            case LEFT:
                return getY();
            case UP:
                return GAME_BOARD_SIZE - getX() - getPadWidth();
            case RIGHT:
                return GAME_BOARD_SIZE - getY() - getPadWidth();
        }
        return -1;
    }

    private void setPosition(double position) {
        switch (padType) {
            case DOWN:
                setX(position);
                return;
            case LEFT:
                setY(position);
                return;
            case UP:
                setX(GAME_BOARD_SIZE - position - getPadWidth());
                return;
            case RIGHT:
                setY(GAME_BOARD_SIZE - position - getPadWidth());
                return;
        }
    }

    public PadType getPadType() {
        return padType;
    }

    public double getSpeed() {
        return speed;
    }

    public Color getColor() {
        return color;
    } 

    private double angleChange(double bouncePoint) {
        //option1 - linear
//        double ret = (2*DEAD_MIDDLE + 2*DEAD_END - Math.PI)*bouncePoint + Math.PI - DEAD_END;
//        return bouncePoint <= 0.5 ? ret : ret - 2*DEAD_MIDDLE;
        return (2*DEAD_MIDDLE + 2*DEAD_END - Math.PI)*bouncePoint + Math.PI - DEAD_END - 2*DEAD_MIDDLE/2*(Math.signum(bouncePoint - 0.5) + 1);
        //(2*dM + 2*dE - pi)*x + pi - dE - 2*dM/2*(sgn(x-1/2)+1)

        //option2 - arccosinar XD
//        if(bouncePoint <= 0.5 )
//            return Math.acos(4*bouncePoint-1)/2 + Math.PI/2;
//        else
//            return Math.acos(4*bouncePoint-3)/2;

//        return Math.acos(4 * (bouncePoint - 0.5) - Math.signum(bouncePoint - 0.5)) / 2 + ((Math.PI / 2 / 2) * (-Math.signum(bouncePoint - 0.5) + 1));
        //acos(4(x-1/2) - sgn(x-1/2))/2 + pi/2/2*(-sgn(x-1/2) +1)

        //option3 - circular
//        if(bouncePoint <= 0.25)
//            return -Math.PI*Math.sqrt(1-Math.pow(4*bouncePoint - 1, 2))/4 + Math.PI;
//        if(bouncePoint <= 0.5)
//            return Math.PI*Math.sqrt(1-Math.pow(4*bouncePoint - 1, 2))/4 + Math.PI/2;
//        if(bouncePoint <= 0.75)
//            return -Math.PI*Math.sqrt(1-Math.pow(4*bouncePoint - 3, 2))/4 + Math.PI/2;
//
//        return Math.PI*Math.sqrt(1-Math.pow(4*bouncePoint - 3, 2))/4;

//        return -Math.PI/4*Math.sqrt(1-Math.pow(4*(Math.abs(bouncePoint-0.5)-0.25),2))*Math.signum(-(Math.abs(bouncePoint-0.5)-0.25))*Math.signum(bouncePoint-0.5) - (Math.PI/4*Math.signum(Math.abs(bouncePoint-0.5)-0.25) + Math.PI/4) * Math.signum(bouncePoint-0.5)+Math.PI/2;

        //-pi/4*sqrt(1-(4*(|x-0.5|-0.25))^2)*sgn(-(|x-0.5|-0.25))*sgn(x-0.5) - (pi/4*sgn(abs(x-0.5)-0.25) + pi/4) * sgn(x-0.5)+pi/2
    }

    @Override
    public double collision(Ball ball) {
        double bouncePoint;
        switch (padType) {
            case DOWN:
                bouncePoint = ball.getNextPositionX() - getPosition();
                break;
            case LEFT:
                bouncePoint = ball.getNextPositionY() - getPosition();
                break;
            case UP:
                bouncePoint = GAME_BOARD_SIZE - ball.getNextPositionX() - getPosition();
                break;
            case RIGHT:
                bouncePoint = GAME_BOARD_SIZE - ball.getNextPositionY() - getPosition();
                break;
            default:
                return 0;
        }
        bouncePoint /= (double) getPadWidth();

        double angle = angleChange(bouncePoint);

        switch (padType) {
            case DOWN:
                return angle;
            case LEFT:
                return angle + Math.PI * 3 / 2;
            case UP:
                return angle + Math.PI;
            case RIGHT:
                return angle + Math.PI / 2;
        }

        return 0;
    }

    public void clearMovementDirection() {
        this.movementDirection = 0;
    }

    public int getMovementDirection() {
        return movementDirection;
    }
}
