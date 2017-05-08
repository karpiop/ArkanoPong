/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arkanopong;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Ball implements Serializable {
    private double x;
    private double y;
    private static double speed = 0.05;
    private static double radius = 5;
    private static final double ANGLE_INIT = Math.PI * 110 / 180;
    private double angle; // 0 right, pi/2 up
    private PadType whose;
    private long tick;

    public Ball(double x, double y, double angle, PadType whose) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.whose = whose;
    }

    public Ball(Ball ball, PadType padType) {
        switch (padType) {
            case DOWN:
                x = ball.getX();
                y = ball.getY();
                angle = ball.getAngle();
                break;
            case LEFT:
                x = ball.getY();
                y = Game.getBoardSize() - ball.getX();
                angle = ball.getAngle() + Math.PI / 2;
                break;
            case UP:
                x = Game.getBoardSize() - ball.getX();
                y = Game.getBoardSize() - ball.getY();
                angle = ball.getAngle() + Math.PI;
                break;
            case RIGHT:
                x = Game.getBoardSize() - ball.getY();
                y = ball.getX();
                angle = ball.getAngle() + Math.PI * 3 / 2;
                break;
        }
        speed = ball.getSpeed();
        radius = ball.getRadius();
        whose = ball.getWhose();
        tick = 1;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public double getSpeed() {
        return speed;
    }

    public int getRadius() {
        return (int) radius;
    }

    public double getAngle() {
        return angle;
    }

    public static double getAngleInit() {
        return ANGLE_INIT;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public PadType getWhose() {
        return whose;
    }

    public void setWhose(PadType whose) {
        this.whose = whose;
    }

    public void setTickTack(long toe) {
        this.tick = toe;
    }

    public int getNextPositionX() {
        return (int) (x + Math.cos(angle) * speed * tick);
    }

    public int getNextPositionY() {
        return (int) (y + Math.sin(angle) * speed * tick);
    }

    public void move() {
        x += Math.cos(angle) * speed * tick;
        y -= Math.sin(angle) * speed * tick;
    }

    public void bounceUD() {
        angle = 2 * Math.PI - angle;
    }

    public void bounceLR() {
        angle = (Math.PI - angle) % (2 * Math.PI);
    }

    public PadType lostBall() {
        if (x < 0)
            return PadType.LEFT;
        else if (y < 0)
            return PadType.UP;
        else if (x > Game.getBoardSize())
            return PadType.RIGHT;
        else if (y > Game.getBoardSize())
            return PadType.DOWN;

        return null;
    }

    private boolean ifCollisionFromDown(Rectangle rectangle) {
        return (this.getNextPositionY() - this.getRadius() > rectangle.getY() + rectangle.getHeight())
                && (this.getY() - this.getRadius() <= rectangle.getY() + rectangle.getHeight());
    }

    private boolean ifCollisionFromUp(Rectangle rectangle) {
        return (this.getNextPositionY() + this.getRadius() < rectangle.getY())
                && (this.getY() + this.getRadius() >= rectangle.getY());
    }

    private boolean ifCollisionFromLeft(Rectangle rectangle) {
        if ((this.getNextPositionX() + this.getRadius() > rectangle.getX())
                && this.getX() + this.getRadius() <= rectangle.getX())
            return true;
        else
            return false;
    }

    private boolean ifCollisionFromRight(Rectangle rectangle) {
        if ((this.getNextPositionX() - this.getRadius() < rectangle.getX() + rectangle.getWidth())
                && (this.getX() - this.getRadius() >= rectangle.getX() + rectangle.getWidth()))
            return true;
        else return false;
    }

    private boolean ifBetweenX(Rectangle rectangle) {
        return (Math.abs(this.getNextPositionX() - rectangle.getX() - rectangle.getWidth() / 2) < rectangle.getWidth() / 2);
    }

    private boolean ifBetweenY(Rectangle rectangle) {
        return Math.abs(this.getNextPositionY() - rectangle.getY() - rectangle.getHeight() / 2) < rectangle.getHeight() / 2;
    }

    public double collisionUDAngle(Rectangle rectangle) {
        if (!(ifBetweenX(rectangle) && (ifCollisionFromDown(rectangle) || ifCollisionFromUp(rectangle))))
            return 0;

        return rectangle.collision(this);
    }

    public double collisionLRAngle(Rectangle rectangle) {
        if (!(ifBetweenY(rectangle) && (ifCollisionFromLeft(rectangle) || ifCollisionFromRight(rectangle))))
            return 0;

        return rectangle.collision(this);
    }

    public double cornerCollisionAngle(Rectangle rectangle) {
        List<Point> rectangleCornerList = new ArrayList<>();
        rectangleCornerList.add(new Point((int) rectangle.getX(), (int) rectangle.getY()));
        rectangleCornerList.add(new Point((int) rectangle.getX(), (int) rectangle.getY() + rectangle.getHeight()));
        rectangleCornerList.add(new Point((int) rectangle.getX() + rectangle.getWidth(), (int) rectangle.getY()));
        rectangleCornerList.add(new Point((int) rectangle.getX() + rectangle.getWidth(), (int) rectangle.getY() + rectangle.getHeight()));

        for (Point rectanglePoint : rectangleCornerList) {
            if (Math.pow(rectanglePoint.getX() - this.getNextPositionX(), 2) + Math.pow(rectanglePoint.getY() - this.getNextPositionY(), 2) <= Math.pow(this.getRadius(), 2))
                return rectangle.collision(this);
        }

        return 0;
    }

}
