package bomberman.player.rlAgents;

import bomberman.game.ColorType;
import bomberman.game.Game;
import bomberman.game.Move;
import bomberman.player.AbstractPlayer;
import bomberman.player.rlAgents.result.Result;

import java.util.ArrayList;
import java.util.List;

public abstract class RLPlayer extends AbstractPlayer {

    private String name;
    protected int reward;
    protected List<Double> rewards;
    protected boolean DISABLE_EPSILON;
    protected final List<Move> possibleMoves;

    public RLPlayer(ColorType playerColor, String name) {
        super(playerColor);
        this.name = name;
        rewards = new ArrayList<>();
        DISABLE_EPSILON = false;
        possibleMoves = List.of(Move.LEFT, Move.RIGHT, Move.DOWN, Move.UP, Move.STAY, Move.BOMB);
    }

    public abstract void updateResult(Result result, Game game);

    public void startNewGame(boolean isTraining, int generation, int episode) {
        DISABLE_EPSILON = !isTraining;
        reward = 0;
    }

    public void endAGame(boolean isTraining, int generation, int episode) {
//        rewards.add((double) reward);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReward() {
        return reward;
    }

    public List<Double> getRewards() {
        return rewards;
    }
}
