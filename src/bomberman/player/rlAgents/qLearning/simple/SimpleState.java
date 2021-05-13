package bomberman.player.rlAgents.qLearning.simple;

import java.io.Serializable;
import java.util.Arrays;

public class SimpleState implements Serializable {
    private TileState[][] board;

    public SimpleState(TileState[][] board) {
        this.board = board;
    }

    public TileState[][] getBoard() {
        return board;
    }

    public void setBoard(TileState[][] board) {
        this.board = board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleState simpleState = (SimpleState) o;
        return Arrays.deepEquals(board, simpleState.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
