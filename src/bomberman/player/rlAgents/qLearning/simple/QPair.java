package bomberman.player.rlAgents.qLearning.simple;

import bomberman.game.Move;

import java.io.Serializable;
import java.util.Objects;

public class QPair implements Serializable {
    private SimpleState simpleState;
    private Move move;

    public QPair(SimpleState simpleState, Move move) {
        this.simpleState = simpleState;
        this.move = move;
    }

    public SimpleState getState() {
        return simpleState;
    }

    public void setState(SimpleState simpleState) {
        this.simpleState = simpleState;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QPair qPair = (QPair) o;
        return Objects.equals(simpleState, qPair.simpleState) && move == qPair.move;
    }

    @Override
    public int hashCode() {
        return Objects.hash(simpleState, move);
    }
}
