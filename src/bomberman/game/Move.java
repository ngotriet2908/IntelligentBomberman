package bomberman.game;

public enum Move {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1),
    STAY(0, 0),
    BOMB(-1, -1);

    private final Point point;

    Move(int x, int y) {
        this.point = new Point(x, y);
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
