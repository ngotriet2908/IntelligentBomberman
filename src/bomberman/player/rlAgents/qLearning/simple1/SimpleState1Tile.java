package bomberman.player.rlAgents.qLearning.simple1;

import java.io.Serializable;
import java.util.Objects;

public class SimpleState1Tile implements Serializable {
    private boolean hasOpponent;
    private SimpleState1Type tileType;

    public SimpleState1Tile(boolean hasOpponent, SimpleState1Type tileType) {
        this.hasOpponent = hasOpponent;
        this.tileType = tileType;
    }

    public boolean isHasOpponent() {
        return hasOpponent;
    }

    public void setHasOpponent(boolean hasOpponent) {
        this.hasOpponent = hasOpponent;
    }

    public SimpleState1Type getTileType() {
        return tileType;
    }

    public void setTileType(SimpleState1Type tileType) {
        this.tileType = tileType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleState1Tile that = (SimpleState1Tile) o;
        return hasOpponent == that.hasOpponent && tileType == that.tileType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasOpponent, tileType);
    }
}
