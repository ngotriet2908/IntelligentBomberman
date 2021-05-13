package bomberman.player.rlAgents.qLearning.simple2;

import bomberman.game.Point;
import bomberman.game.Tile;

import java.io.Serializable;
import java.util.Objects;

public class SimpleState2 implements Serializable {
    private Tile tile;
    private int placedBombs;

    public SimpleState2(Tile tile, int placedBombs) {
        this.tile = tile;
        this.placedBombs = placedBombs;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int getPlacedBombs() {
        return placedBombs;
    }

    public void setPlacedBombs(int placedBombs) {
        this.placedBombs = placedBombs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleState2 that = (SimpleState2) o;
        return placedBombs == that.placedBombs && Objects.equals(tile, that.tile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tile, placedBombs);
    }

    @Override
    public String toString() {
        return "SimpleState2{" +
                "tile=" + tile +
                ", placedBombs=" + placedBombs +
                '}';
    }
}
