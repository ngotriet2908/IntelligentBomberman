package bomberman.game;

public enum ColorType {
    BLUE("Blue"),
    RED("Red"),
    YELLOW("Yellow"),
    GREEN("Green");

    private final String name;

    ColorType(String name) {
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
