package bomberman.player.rlAgents.result;

public class Result {
    private boolean isValidMove;
    private boolean isKilled;
    private int destroyedWalls;
    private int getDestroyedPlayers;
    private boolean diedByOwn;

    public Result(boolean isValidMove, boolean isKilled, int destroyedWalls, int getDestroyedPlayers, boolean diedByOwn) {
        this.isValidMove = isValidMove;
        this.isKilled = isKilled;
        this.destroyedWalls = destroyedWalls;
        this.getDestroyedPlayers = getDestroyedPlayers;
        this.diedByOwn = diedByOwn;
    }

    public boolean isValidMove() {
        return isValidMove;
    }

    public void setValidMove(boolean validMove) {
        isValidMove = validMove;
    }

    public boolean isKilled() {
        return isKilled;
    }

    public void setKilled(boolean killed) {
        isKilled = killed;
    }

    public int getDestroyedWalls() {
        return destroyedWalls;
    }

    public void setDestroyedWalls(int destroyedWalls) {
        this.destroyedWalls = destroyedWalls;
    }

    public int getGetDestroyedPlayers() {
        return getDestroyedPlayers;
    }

    public void incKill() {
        this.getDestroyedPlayers++;
    }

    public void incDestroyedWalls() {
        this.destroyedWalls++;
    }

    public void setGetDestroyedPlayers(int getDestroyedPlayers) {
        this.getDestroyedPlayers = getDestroyedPlayers;
    }

    public boolean isDiedByOwn() {
        return diedByOwn;
    }

    public void setDiedByOwn(boolean diedByOwn) {
        this.diedByOwn = diedByOwn;
    }

    @Override
    public String toString() {
        return "Result{" +
                "isValidMove=" + isValidMove +
                ", isKilled=" + isKilled +
                ", destroyedWalls=" + destroyedWalls +
                ", getDestroyedPlayers=" + getDestroyedPlayers +
                ", diedByOwn=" + diedByOwn +
                '}';
    }
}
