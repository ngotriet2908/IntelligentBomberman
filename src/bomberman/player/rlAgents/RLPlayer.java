package bomberman.player.rlAgents;

import bomberman.game.ColorType;
import bomberman.game.Game;
import bomberman.player.AbstractPlayer;
import bomberman.player.rlAgents.result.Result;

public abstract class RLPlayer extends AbstractPlayer {

    private String name;

    public RLPlayer(ColorType playerColor, String name) {
        super(playerColor);
        this.name = name;
    }

    public abstract void updateResult(Result result, Game game);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
