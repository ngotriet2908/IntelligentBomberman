package bomberman.game;

import java.io.Serializable;
import java.util.Objects;

public class Tile implements Serializable {
    private Point coordinate;
    private TileType tileType;

    public Tile(Point coordinate, TileType tileType) {
        this.coordinate = coordinate;
        this.tileType = tileType;
    }

    public Tile(Point coordinate) {
        this.coordinate = coordinate;
        this.tileType = TileType.FREE;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }

//    @Override
//    public String toString() {
//        return tileType.toString();
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return Objects.equals(coordinate, tile.coordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate);
    }

    @Override
    public String toString() {
        return tileType.toString();
    }
}
