package bomberman.player.rlAgents.qLearning.simple3;

import java.io.Serializable;
import java.util.Objects;

public class SimpleState3Tile implements Serializable {
    private SimpleState3Type tileType;
    private int dangerLevel;
    private boolean hasOpponent;

    public SimpleState3Tile(SimpleState3Type tileType, int dangerLevel, boolean hasOpponent) {
        this.tileType = tileType;
        this.dangerLevel = dangerLevel;
        this.hasOpponent = hasOpponent;
    }

    public SimpleState3Type getTileType() {
        return tileType;
    }

    public void setTileType(SimpleState3Type tileType) {
        this.tileType = tileType;
    }

    public int getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(int dangerLevel) {
        if (dangerLevel > 0) {
            this.dangerLevel = 1;
        } else {
            this.dangerLevel = 0;
        }
    }

    public boolean isHasOpponent() {
        return hasOpponent;
    }

    public void setHasOpponent(boolean hasOpponent) {
        this.hasOpponent = hasOpponent;
        this.hasOpponent = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleState3Tile that = (SimpleState3Tile) o;
        return dangerLevel == that.dangerLevel && hasOpponent == that.hasOpponent && tileType == that.tileType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tileType, dangerLevel, hasOpponent);
    }

    @Override
    public String toString() {
        return "{" + tileType + ", " + dangerLevel + ", " + hasOpponent + "}";
    }
}
