package bomberman.player;

import bomberman.game.ColorType;
import bomberman.game.Game;
import bomberman.game.Move;

public class SimplePlayer extends AbstractPlayer{

    public SimplePlayer(ColorType playerColor) {
        super(playerColor);
    }

    @Override
    public Move determineMove(Game game) {
        return Move.STAY;
    }
}
