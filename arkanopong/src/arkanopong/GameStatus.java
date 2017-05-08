package arkanopong;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 2016-06-04.
 */
public class GameStatus implements Serializable {
    private java.util.List<Pad> pads;
    private List<Block> blocks;
    private List<Ball> balls;
    private int scoreBoard[];
    public GameStatus(java.util.List<Pad> pads, List<Block> blocks, List<Ball> balls, int scoreBoard[]){
        this.pads = pads;
        this.balls = balls;
        this.blocks = blocks;
        this.scoreBoard = scoreBoard;
    }
    public java.util.List<Pad> getPads() {return pads;}
    public List<Block> getBlocks() {return blocks;}
    public List<Ball> getBalls() {return balls;}
    public int[] getScoreBoard() {return scoreBoard;}
}
