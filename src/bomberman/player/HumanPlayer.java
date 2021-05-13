package bomberman.player;

import bomberman.game.ColorType;
import bomberman.game.Game;
import bomberman.game.Move;
import sample.Controller;

public class HumanPlayer extends AbstractPlayer{

    private Controller controller;

    public HumanPlayer(ColorType playerColor, Controller controller) {
        super(playerColor);
        this.controller = controller;
    }

    @Override
    public Move determineMove(Game game) {
        if (controller.getEnteredMove() == null) {
            return Move.STAY;
        } else {
            Move move = controller.getEnteredMove();
            controller.setEnteredMove(null);
            return move;
        }
    }
}
