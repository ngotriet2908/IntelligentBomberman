package bomberman.player.rlAgents.qLearning.simple;

import bomberman.game.TileType;

import java.io.Serializable;
import java.util.Objects;

public class TileState implements Serializable {
    private boolean hasOpponent;
    private boolean isHere;
    private int bombCd;
    private TileType tileType;

    public TileState(boolean hasOpponent, boolean isHere, int bombCd, TileType tileType) {
        this.hasOpponent = hasOpponent;
        this.isHere = isHere;
        this.bombCd = bombCd;
        this.tileType = tileType;
    }

    public boolean isHasOpponent() {
        return hasOpponent;
    }

    public void setHasOpponent(boolean hasOpponent) {
        this.hasOpponent = hasOpponent;
    }

    public boolean isHere() {
        return isHere;
    }

    public void setHere(boolean here) {
        isHere = here;
    }

    public int getBombCd() {
        return bombCd;
    }

    public void setBombCd(int bombCd) {
        this.bombCd = bombCd;
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileState state = (TileState) o;
        return hasOpponent == state.hasOpponent && isHere == state.isHere && bombCd == state.bombCd && tileType == state.tileType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasOpponent, isHere, bombCd, tileType);
    }
}
