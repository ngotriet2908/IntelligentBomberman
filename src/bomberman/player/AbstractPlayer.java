package bomberman.player;

import bomberman.game.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractPlayer {
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
