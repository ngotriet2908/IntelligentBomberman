package bomberman.player;

import bomberman.game.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractPlayer {

    protected static final int REWARD_MOVE = -1;
    protected static final int REWARD_BOMB = -1;
    protected static final int REWARD_STAY = -2;
    protected static final int REWARD_INVALID_MOVE = -5;
    protected static final int REWARD_KILLED = -300;
    //    private static final int REWARD_KILLED_BY_OWN = -1000;
    protected static final int REWARD_KILLED_BY_OWN = -300;
    protected static final int REWARD_KILL = +100;
    protected static final int REWARD_DESTROY_TILE = +30;

    private List<Bomb> placedBombs;
//    private Point coordinate;
    private Tile tile;
    private ColorType playerColor;
    private boolean alive;

    public AbstractPlayer(ColorType playerColor) {
        this.playerColor = playerColor;
        this.placedBombs = new ArrayList<>();
    }

    public abstract Move determineMove(Game game);

    public List<Bomb> getPlacedBombs() {
        return placedBombs;
    }

    public void setPlacedBombs(List<Bomb> placedBombs) {
        this.placedBombs = placedBombs;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public ColorType getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ColorType playerColor) {
        this.playerColor = playerColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractPlayer that = (AbstractPlayer) o;
        return playerColor == that.playerColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerColor);
    }
}
