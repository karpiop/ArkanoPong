package arkanopong;

import java.util.List;

/**
 * Created by Piotr on 11-May-16.
 */
public class SendGameInfo {

    private List<Pad> pads;
    private List<Block> blocks;
    private List<Ball> balls;
    private int scoreBoard[] = new int[4];

    public SendGameInfo(List<Pad> pads, List<Block> blocks, List<Ball> balls, int[] scoreBoard) {
        this.pads = pads;
        this.blocks = blocks;
        this.balls = balls;
        this.scoreBoard = scoreBoard;
    }
}
