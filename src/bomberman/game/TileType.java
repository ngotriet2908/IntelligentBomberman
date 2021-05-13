package bomberman.game;

public enum TileType {
    FREE("F"),
    UNBREAKABLE_TILE("U"),
    BREAKABLE_TILE("B");

    private final String name;

    TileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
