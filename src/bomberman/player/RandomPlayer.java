package bomberman.player;

import bomberman.game.ColorType;
import bomberman.game.Game;
import bomberman.game.Move;

import java.util.Random;

public class RandomPlayer extends AbstractPlayer{

    public RandomPlayer(ColorType playerColor) {
        super(playerColor);
    }

    @Override
    public Move determineMove(Game game) {
        Random random = new Random();
        while(true) {
//            int prob = random.nextInt(100) + 1;
            if (random.nextInt(100) + 1 < 10) {
                return Move.BOMB;
            }
            if (random.nextInt(100) + 1 < 20) {
                return Move.UP;
            }
            if (random.nextInt(100) + 1 < 20) {
                return Move.DOWN;
            }
            if (random.nextInt(100) + 1 < 20) {
                return Move.LEFT;
            }
            if (random.nextInt(100) + 1 < 20) {
                return Move.RIGHT;
            }
            if (random.nextInt(100) + 1 < 20) {
                return Move.STAY;
            }
        }
    }
}
