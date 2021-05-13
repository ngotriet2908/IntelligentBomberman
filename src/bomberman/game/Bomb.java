package bomberman.game;

import bomberman.player.AbstractPlayer;

import java.util.Objects;

public class Bomb {
//    private Point coordinate;
    private Tile tile;
    private int countDown;
    private int blastRadius;
    private AbstractPlayer owner;

    public Bomb(Tile tile, int countDown, int blastRadius, AbstractPlayer owner) {
        this.tile = tile;
        this.countDown = countDown;
        this.blastRadius = blastRadius;
        this.owner = owner;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int getCountDown() {
        return countDown;
    }

    public void setCountDown(int countDown) {
        this.countDown = countDown;
    }

    public AbstractPlayer getOwner() {
        return owner;
    }

    public void setOwner(AbstractPlayer owner) {
        this.owner = owner;
    }

    public int getBlastRadius() {
        return blastRadius;
    }

    public void setBlastRadius(int blastRadius) {
        this.blastRadius = blastRadius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bomb bomb = (Bomb) o;
        return Objects.equals(tile, bomb.tile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tile);
    }
}
