package arkanopong;

import java.io.Serializable;

public class Block extends Rectangle implements Serializable {

    private boolean invincible = false;

    public Block(int x, int y, int width, int gameBoardSize) {
        super(x, y, width, width, gameBoardSize);
    }

    public Block(int x, int y, int width, int height, boolean invincible, int gameBoardSize) {
        super(x, y, width, height, gameBoardSize);
        this.invincible = invincible;
    }

    public Block(Block block, PadType padType, int gameBoardSize) {
        super(block, padType, gameBoardSize);
        this.invincible = block.isInvincible();
    }

    public boolean isInvincible() {
        return invincible;
    }
}
