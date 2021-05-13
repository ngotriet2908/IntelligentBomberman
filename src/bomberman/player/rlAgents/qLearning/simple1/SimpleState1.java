package bomberman.player.rlAgents.qLearning.simple1;

import bomberman.game.Point;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class SimpleState1 implements Serializable {
    private SimpleState1Tile[] surrounding;
    private int placedBombs;

    public SimpleState1(SimpleState1Tile[] surrounding, int placedBombs) {
        this.surrounding = surrounding;
        this.placedBombs = placedBombs;
        for(int i = 0; i < surrounding.length; i++) {
            surrounding[i] = new SimpleState1Tile(false, SimpleState1Type.FREE);
        }
    }

    public SimpleState1Tile[] getSurrounding() {
        return surrounding;
    }

    public void setSurrounding(SimpleState1Tile[] surrounding) {
        this.surrounding = surrounding;
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
        SimpleState1 that = (SimpleState1) o;
        return placedBombs == that.placedBombs && Arrays.equals(surrounding, that.surrounding);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(placedBombs);
        result = 31 * result + Arrays.hashCode(surrounding);
        return result;
    }
}
