package bomberman.player.rlAgents.qLearning.simple3;

import bomberman.game.Move;

import java.io.Serializable;
import java.util.Objects;

public class QPair3 implements Serializable {
    private SimpleState3 simpleState;
    private Move move;

    public QPair3(SimpleState3 simpleState, Move move) {
        this.simpleState = simpleState;
        this.move = move;
    }

    public SimpleState3 getSimpleState() {
        return simpleState;
    }

    public void setSimpleState(SimpleState3 simpleState) {
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
        QPair3 qPair3 = (QPair3) o;
        return Objects.equals(simpleState, qPair3.simpleState) && move == qPair3.move;
    }

    @Override
    public int hashCode() {
        return Objects.hash(simpleState, move);
    }

    @Override
    public String toString() {
        return "QPair3{" +
                "simpleState=" + simpleState +
                ", move=" + move +
                '}';
    }
}
