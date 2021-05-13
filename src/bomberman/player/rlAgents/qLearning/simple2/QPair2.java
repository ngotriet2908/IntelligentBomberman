package bomberman.player.rlAgents.qLearning.simple2;

import bomberman.game.Move;

import java.io.Serializable;
import java.util.Objects;

public class QPair2 implements Serializable {
    private SimpleState2 simpleState;
    private Move move;

    public QPair2(SimpleState2 simpleState, Move move) {
        this.simpleState = simpleState;
        this.move = move;
    }

    public SimpleState2 getSimpleState() {
        return simpleState;
    }

    public void setSimpleState(SimpleState2 simpleState) {
        this.simpleState = simpleState;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    @Override
    public String toString() {
        return "QPair2{" +
                "simpleState=" + simpleState +
                ", move=" + move +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QPair2 qPair2 = (QPair2) o;
        return Objects.equals(simpleState, qPair2.simpleState) && move == qPair2.move;
    }

    @Override
    public int hashCode() {
        return Objects.hash(simpleState, move);
    }
}
