package bomberman.player.rlAgents.qLearning.simple1;

import bomberman.game.*;
import bomberman.player.AbstractPlayer;
import bomberman.player.rlAgents.RLPlayer;
import bomberman.player.rlAgents.result.Result;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleQLearningPlayer1 extends RLPlayer {

    private static final boolean DISABLE_EPSILON = false;
//    private static final boolean DISABLE_EPSILON = true;

    private static final double EPSILON = 0.1;
    private static final double DISCOUNT_FACTOR = 0.98;
    private static final double LEARNING_RATE = 0.2;
    private Map<QPair1, Double> qTable;
    private SimpleState1 prevSimpleState;
    private Move chosenAction;
    private final Random random;
    private int generations;

    public SimpleQLearningPlayer1(ColorType playerColor, String name) {
        super(playerColor, name);
        random = new Random();
        this.generations = 0;

        try {
            FileInputStream fi = new FileInputStream("src/bomberman/player/rlAgents/qLearning/simple1/trainData/" + this.getName());
            ObjectInputStream oi = new ObjectInputStream(fi);
            qTable = (HashMap)oi.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            qTable = new HashMap<>();
        }
        System.out.println(qTable.size());
    }

    public void saveQTableToFile() {
        try {
            FileOutputStream fileOut = new FileOutputStream("src/bomberman/player/rlAgents/qLearning/simple1/trainData/" + this.getName());
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(qTable);
            objectOut.close();
            System.out.println("The Object  was successfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Double getOrCreate(QPair1 pair) {
        if (!qTable.containsKey(pair)) {
            qTable.put(pair, 0.0);
            return 0.0;
        } else {
//            System.out.println("get");
            return qTable.get(pair);
        }
    }

    private SimpleState1 extractState(Game game) {
        SimpleState1 simpleState = new SimpleState1(new SimpleState1Tile[5], 0);
        simpleState.setPlacedBombs(
                (int) game.getBombs()
                        .stream()
                        .filter(bomb -> {
                            if (bomb.getOwner() == null) return false;
                            return bomb.getOwner().equals(this);
                        }).count());

        int minDist = 100000000;
        for(AbstractPlayer player: game.getPlayers()) {
            if (player.equals(this)) continue;
            int dist = Math.abs(player.getTile().getCoordinate().x - this.getTile().getCoordinate().x) +
                    Math.abs(player.getTile().getCoordinate().y - this.getTile().getCoordinate().y);
            if (dist < minDist) minDist = dist;
        }

        Optional<Bomb> bomb1 = game.getBombs().stream().filter(bomb -> bomb.getTile().equals(this.getTile())).findFirst();
        simpleState.getSurrounding()[0].setTileType(bomb1.map(bomb -> (bomb.getCountDown() > 1) ? SimpleState1Type.BOMB :
                SimpleState1Type.BOMB_WILL_EXPLODE).orElse(SimpleState1Type.FREE));
        simpleState.getSurrounding()[0].setHasOpponent(false);

        editStateTile(
                simpleState.getSurrounding()[1],
                game,
                game.getBoard()[this.getTile().getCoordinate().x + Move.RIGHT.getPoint().x]
                        [this.getTile().getCoordinate().y + Move.RIGHT.getPoint().y]
        );

        editStateTile(
                simpleState.getSurrounding()[2],
                game,
                game.getBoard()[this.getTile().getCoordinate().x + Move.UP.getPoint().x]
                        [this.getTile().getCoordinate().y + Move.UP.getPoint().y]
        );

        editStateTile(
                simpleState.getSurrounding()[3],
                game,
                game.getBoard()[this.getTile().getCoordinate().x + Move.LEFT.getPoint().x]
                        [this.getTile().getCoordinate().y + Move.LEFT.getPoint().y]
        );

        editStateTile(
                simpleState.getSurrounding()[4],
                game,
                game.getBoard()[this.getTile().getCoordinate().x + Move.DOWN.getPoint().x]
                        [this.getTile().getCoordinate().y + Move.DOWN.getPoint().y]
        );

        return simpleState;
    }

    private void editStateTile(SimpleState1Tile simpleState1Tile, Game game, Tile newTile) {
        Optional<Bomb> bomb1 = game.getBombs().stream().filter(bomb -> bomb.getTile().equals(newTile)).findFirst();
        SimpleState1Type tileType = SimpleState1Type.FREE;
        switch (newTile.getTileType()) {
            case BREAKABLE_TILE -> tileType = SimpleState1Type.BREAKABLE;
            case UNBREAKABLE_TILE -> tileType = SimpleState1Type.UNBREAKABLE;
        }

        simpleState1Tile.setTileType(bomb1.map(bomb -> (bomb.getCountDown() > 1) ? SimpleState1Type.BOMB :
                SimpleState1Type.BOMB_WILL_EXPLODE).orElse(tileType));
        simpleState1Tile.setHasOpponent(game.getPlayers()
                .stream().anyMatch(abstractPlayer -> !abstractPlayer.equals(this) && abstractPlayer.getTile().equals(newTile))
        );
    }

    private List<Move> getPossibleActions(SimpleState1 simpleState1) {
//        List<Move> validMoves = new ArrayList<>();
//        validMoves.add(Move.STAY);
//
//        if (simpleState1.getSurrounding()[0].getTileType() == SimpleState1Type.FREE &&
//                !simpleState1.getSurrounding()[0].isHasOpponent()
//        ) {
//            validMoves.add(Move.BOMB);
//        }
//        if (simpleState1.getSurrounding()[1].getTileType() == SimpleState1Type.FREE &&
//                !simpleState1.getSurrounding()[1].isHasOpponent()) {
//            validMoves.add(Move.RIGHT);
//        }
//        if (simpleState1.getSurrounding()[2].getTileType() == SimpleState1Type.FREE &&
//                !simpleState1.getSurrounding()[2].isHasOpponent()) {
//            validMoves.add(Move.UP);
//        }
//        if (simpleState1.getSurrounding()[3].getTileType() == SimpleState1Type.FREE &&
//                !simpleState1.getSurrounding()[3].isHasOpponent()) {
//            validMoves.add(Move.LEFT);
//        }
//        if (simpleState1.getSurrounding()[4].getTileType() == SimpleState1Type.FREE &&
//                !simpleState1.getSurrounding()[4].isHasOpponent()) {
//            validMoves.add(Move.DOWN);
//        }
////        System.out.println(Arrays.toString(validMoves.toArray()));
//        return validMoves;
        return possibleMoves;
    }

    private int getReward(Result result) {
        if (result.isKilled()) return REWARD_KILLED;

        int reward = ((result.isValidMove()) ? REWARD_MOVE : REWARD_INVALID_MOVE) +
                REWARD_KILL * result.getGetDestroyedPlayers() +
                REWARD_DESTROY_TILE * result.getDestroyedWalls();
//        if (reward > 0) {
//            System.out.println(reward);
//        }
        return reward;
    }

    @Override
    public Move determineMove(Game game) {
        prevSimpleState = extractState(game);
        List<Move> possibleActions = getPossibleActions(prevSimpleState);
        Move chosenMove = Move.STAY;
        if (random.nextDouble() <= EPSILON && !DISABLE_EPSILON) {
//            System.out.println("choose random action");
            chosenMove = possibleActions.get(random.nextInt(possibleActions.size()));
        } else {
            Double tmpMax = -100000000.0;
            List<Move> optimalMoves = new ArrayList<>();
            for(Move move: possibleActions) {
                QPair1 nextState = new QPair1(prevSimpleState, move);
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
        SimpleState1 thisState = extractState(game);
        List<Move> possibleActions = getPossibleActions(thisState);
        Double maxNextQ = -100000000.0;
        for(Move move: possibleActions) {
            QPair1 nextState = new QPair1(thisState, move);
            Double tmp = getOrCreate(nextState);
            if (tmp > maxNextQ) {
                maxNextQ = tmp;
            }
        }
        QPair1 qPair = new QPair1(prevSimpleState, chosenAction);
        Double currentQ = getOrCreate(qPair);
        int reward = getReward(result);
//        if (true) {
//        if (reward > -1) {
//            System.out.println(reward);
//        }
        Double newQ = currentQ + LEARNING_RATE*(reward + DISCOUNT_FACTOR*maxNextQ - currentQ);
        qTable.put(qPair, newQ);
    }

    @Override
    public void startNewGame(boolean isTraining, int generation, int episode) {

    }

    @Override
    public List<Double> getRewards() {
        return null;
    }
}
