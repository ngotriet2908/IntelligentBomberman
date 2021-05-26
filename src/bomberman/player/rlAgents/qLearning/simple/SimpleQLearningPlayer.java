package bomberman.player.rlAgents.qLearning.simple;

import bomberman.game.*;
import bomberman.player.AbstractPlayer;
import bomberman.player.rlAgents.RLPlayer;
import bomberman.player.rlAgents.result.Result;

import java.io.*;
import java.util.*;

public class SimpleQLearningPlayer extends RLPlayer {

    private static final int REWARD_MOVE = -1;
    private static final int REWARD_INVALID_MOVE = -3;
    private static final int REWARD_KILLED = -300;
    private static final int REWARD_KILL = +100;
    private static final int REWARD_DESTROY_TILE = +30;
//    private static final boolean DISABLE_EPSILON = false;
    private static final boolean DISABLE_EPSILON = true;

    private static final double EPSILON = 0.1;
    private static final double DISCOUNT_FACTOR = 0.98;
    private static final double LEARNING_RATE = 0.1;
    private Map<QPair, Double> qTable;
    private SimpleState prevSimpleState;
    private Move chosenAction;
    private final Random random;
    private final List<Move> possibleMoves;
    private int generations;

    public SimpleQLearningPlayer(ColorType playerColor, String name) {
        super(playerColor, name);
        random = new Random();
        possibleMoves = List.of(Move.LEFT, Move.RIGHT, Move.DOWN, Move.UP, Move.STAY, Move.BOMB);
        this.generations = 0;

        try {
            FileInputStream fi = new FileInputStream("src/bomberman/player/rlAgents/qLearning/simple/trainData/" + this.getName());
            ObjectInputStream oi = new ObjectInputStream(fi);
            qTable = (HashMap)oi.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            qTable = new HashMap<>();
        }
        System.out.println(qTable.size());
    }

    public void saveQTableToFile() {
        if (DISABLE_EPSILON) return;
        try {
            FileOutputStream fileOut = new FileOutputStream("src/bomberman/player/rlAgents/qLearning/simple/trainData/" + this.getName());
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(qTable);
            objectOut.close();
            System.out.println("The Object  was successfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Double getOrCreate(QPair pair) {
        if (!qTable.containsKey(pair)) {
            qTable.put(pair, 0.0);
            return 0.0;
        } else {
//            System.out.println("get");
            return qTable.get(pair);
        }
    }

    private boolean isPlayerHere(Game game, Point point) {
        for(AbstractPlayer player: game.getPlayers()) {
            if (player.equals(this)) {
                return point.equals(player.getTile().getCoordinate());
            }
        }
        return false;
    }

    private boolean isOpponentHere(Game game, Point point) {
        for(AbstractPlayer player: game.getPlayers()) {
            if (!player.equals(this)) {
                if (point.equals(player.getTile().getCoordinate())) return true;
            }
        }
        return false;
    }

    private int getBombCd(Game game, Point point) {
        for(Bomb bomb: game.getBombs()) {
            if (bomb.getTile().getCoordinate().equals(point)) {
                if (bomb.getCountDown() <= 1) return 0;
                return 1;
            }
        }
        return -1;
    }

    private SimpleState extractState(Game game) {
        SimpleState simpleState = new SimpleState(new TileState[game.getSize()][game.getSize()]);
        for(int i = 0; i < game.getSize(); i++) {
            for(int j = 0; j < game.getSize(); j++) {
                Point currentPoint = new Point(i, j);
                simpleState.getBoard()[i][j] = new TileState(
                        isOpponentHere(game, currentPoint),
                        isPlayerHere(game, currentPoint),
                        getBombCd(game, currentPoint),
                        game.getBoard()[i][j].getTileType()
                );
            }
        }
        return simpleState;
    }

    private List<Move> getPossibleActions(Game game) {
        List<Move> validMoves = new ArrayList<>();
        for(Move move: possibleMoves) {
            if (move == Move.BOMB) {
                boolean alreadyHasBomb = false;
                for (Bomb bomb: game.getBombs()) {
                    if (bomb.getTile().equals(this.getTile())) {
                        alreadyHasBomb = true;
                        break;
                    }
                }
                if (!alreadyHasBomb) {
                    validMoves.add(move);
                }
                continue;
            }

            if (move == Move.STAY) {
                validMoves.add(move);
                continue;
            }

            Tile newTile = game.getBoard()[this.getTile().getCoordinate().x + move.getPoint().x][this.getTile().getCoordinate().y + move.getPoint().y];
            if (newTile.getTileType() != TileType.FREE) {
                continue;
            }

            boolean collideWithPlayer = false;
            for (AbstractPlayer player1: game.getPlayers()) {
                if (player1.getTile().equals(newTile)) {
                    collideWithPlayer = true;
                    break;
                }
            }
            if (collideWithPlayer) continue;

            boolean collideWithBomb = false;
            for (Bomb bomb: game.getBombs()) {
                if (bomb.getTile().equals(newTile)) {
                    collideWithBomb = true;
                    break;
                }
            }
            if (collideWithBomb) continue;
            validMoves.add(move);
        }
        return validMoves;
    }

    private int getReward(Result result) {
//        if (result.isKilled()) return REWARD_KILLED;
        int reward = ((result.isValidMove()) ? REWARD_MOVE : REWARD_INVALID_MOVE) +
                ((result.isKilled()) ? REWARD_KILLED : 0) +
                REWARD_KILL * result.getGetDestroyedPlayers() +
                REWARD_DESTROY_TILE * result.getDestroyedWalls();
//        if (true) {
//            System.out.println(result.toString() + "| r: " + reward);
//        }
        return reward;
    }

    @Override
    public Move determineMove(Game game) {
        prevSimpleState = extractState(game);
        List<Move> possibleActions = getPossibleActions(game);
        Move chosenMove = Move.STAY;
        if (random.nextDouble() <= EPSILON && !DISABLE_EPSILON) {
//            System.out.println("choose random action");
            chosenMove = possibleActions.get(random.nextInt(possibleActions.size()));
        } else {
            Double tmpMax = -100000000.0;
            List<Move> optimalMoves = new ArrayList<>();
            for(Move move: possibleActions) {
                QPair nextState = new QPair(prevSimpleState, move);
                Double tmp = getOrCreate(nextState);
                if (tmp > tmpMax) {
                    tmpMax = tmp;
                    optimalMoves = new ArrayList<>();
                    optimalMoves.add(move);
                } else if (tmp.equals(tmpMax)) {
                    optimalMoves.add(move);
                }
            }
            if (optimalMoves.size() <= 0) {
                System.out.println("Error: agent produce null move");
            } else {
                chosenMove = optimalMoves.get(random.nextInt(optimalMoves.size()));
            }
        }
        chosenAction = chosenMove;
        return chosenAction;
    }

    @Override
    public void updateResult(Result result, Game game) {
        generations++;
        SimpleState thisState = extractState(game);
        List<Move> possibleActions = getPossibleActions(game);
        double maxNextQ = -100000000.0;
        for(Move move: possibleActions) {
            QPair nextState = new QPair(thisState, move);
            double tmp = getOrCreate(nextState);
            if (tmp > maxNextQ) {
                maxNextQ = tmp;
            }
        }
        QPair qPair = new QPair(prevSimpleState, chosenAction);
        double currentQ = getOrCreate(qPair);
        int reward = getReward(result);
//        if (true) {
//        if (reward > -1) {
//            System.out.println(reward);
//        }
        double newQ = currentQ + LEARNING_RATE*(reward*1.0 + DISCOUNT_FACTOR * maxNextQ - currentQ);
        qTable.put(qPair, newQ);
    }

    @Override
    public void startNewGame(boolean isTraining, int generation, int episode) {

    }
}
