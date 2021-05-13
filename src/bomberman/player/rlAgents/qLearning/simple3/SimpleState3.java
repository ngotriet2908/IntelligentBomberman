package bomberman.player.rlAgents.qLearning.simple3;

import bomberman.game.Point;
import bomberman.player.rlAgents.qLearning.simple1.SimpleState1Tile;
import bomberman.player.rlAgents.qLearning.simple1.SimpleState1Type;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SimpleState3 implements Serializable {
    private SimpleState3Tile[] surrounding;
    private Point vectorToNearestOpponent;
    private List<Point> bombLists;
    private int placedBombs;

    public SimpleState3(SimpleState3Tile[] surrounding, Point vectorToNearestOpponent, int placedBombs, List<Point> bombs) {
        this.surrounding = surrounding;
        this.vectorToNearestOpponent = vectorToNearestOpponent;
        for(int i = 0; i < surrounding.length; i++) {
            surrounding[i] = new SimpleState3Tile(SimpleState3Type.FREE, 0, false);
        }
        this.placedBombs = 0;
        this.bombLists = bombs;
    }

    public SimpleState3Tile[] getSurrounding() {
        return surrounding;
    }

    public void setSurrounding(SimpleState3Tile[] surrounding) {
        this.surrounding = surrounding;
    }

    public Point getVectorToNearestOpponent() {
        return vectorToNearestOpponent;
    }

    public void setVectorToNearestOpponent(Point vectorToNearestOpponent) {
        this.vectorToNearestOpponent = vectorToNearestOpponent;
//        this.vectorToNearestOpponent = new Point(0, 0);
    }

    public int getPlacedBombs() {
        return placedBombs;
    }

    public void setPlacedBombs(int placedBombs) {
//        this.placedBombs = placedBombs;
        this.placedBombs = 0;
    }

    public List<Point> getBombLists() {
        return bombLists;
    }

    public void setBombLists(List<Point> bombLists) {
        this.bombLists = bombLists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleState3 that = (SimpleState3) o;
        return placedBombs == that.placedBombs && Arrays.equals(surrounding, that.surrounding) && Objects.equals(vectorToNearestOpponent, that.vectorToNearestOpponent) && Objects.equals(bombLists, that.bombLists);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(vectorToNearestOpponent, bombLists, placedBombs);
        result = 31 * result + Arrays.hashCode(surrounding);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleState3{" +
                "surrounding=" + Arrays.toString(surrounding) +
                ", vectorToNearestOpponent=" + vectorToNearestOpponent +
                ", bombLists=" + bombLists +
                ", placedBombs=" + placedBombs +
                '}';
    }
}
