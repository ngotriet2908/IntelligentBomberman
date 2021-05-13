package bomberman.player.rlAgents.qLearning.simple1;

import bomberman.game.Move;

import java.io.Serializable;
import java.util.Objects;

public class QPair1 implements Serializable {
    private SimpleState1 simpleState;
    private Move move;

    public QPair1(SimpleState1 simpleState, Move move) {
        this.simpleState = simpleState;
        this.move = move;
    }

    public SimpleState1 getSimpleState() {
        return simpleState;
    }

    public void setSimpleState(SimpleState1 simpleState) {
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
        QPair1 qPair1 = (QPair1) o;
        return Objects.equals(simpleState, qPair1.simpleState) && move == qPair1.move;
    }

    @Override
    public int hashCode() {
        return Objects.hash(simpleState, move);
    }
}
