package bomberman.player.rlAgents.qLearning.simple2;

import bomberman.game.*;
import bomberman.player.AbstractPlayer;
import bomberman.player.rlAgents.RLPlayer;
import bomberman.player.rlAgents.result.Result;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleQLearningPlayer2 extends RLPlayer {

    private static final double EPSILON = 0.1;
    private static final double DISCOUNT_FACTOR = 0.95;
    private static final double LEARNING_RATE = 0.3;
    private Map<QPair2, Double> qTable;
    private SimpleState2 prevSimpleState;
    private Move chosenAction;
    private final Random random;
    private int generations;

    public SimpleQLearningPlayer2(ColorType playerColor, String name) {
        super(playerColor, name);
        random = new Random();
        this.generations = 0;

        try {
            FileInputStream fi = new FileInputStream("src/bomberman/player/rlAgents/qLearning/simple2/trainData/" + this.getName());
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
            FileOutputStream fileOut = new FileOutputStream("src/bomberman/player/rlAgents/qLearning/simple2/trainData/" + this.getName());
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(qTable);
            objectOut.close();
            System.out.println("The Object  was successfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Double getOrCreate(QPair2 pair) {
        if (!qTable.containsKey(pair)) {
            qTable.put(pair, 0.0);
            return 0.0;
        } else {
//            System.out.println("get");
            return qTable.get(pair);
        }
    }

    private SimpleState2 extractState(Game game) {
        return new SimpleState2(
                this.getTile(),
                (int) game.getBombs().stream().filter(bomb -> bomb.getOwner().equals(this)).count()
        );
    }

    private List<Move> getPossibleActions(Game game) {
//        List<Move> validMoves = new ArrayList<>();
//        validMoves.add(Move.STAY);
//        validMoves.add(Move.LEFT);
//        validMoves.add(Move.RIGHT);
//        validMoves.add(Move.UP);
//        validMoves.add(Move.DOWN);
//
//        if (game.getBombs().stream()
//                .noneMatch(bomb -> bomb.getOwner().equals(this) && bomb.getTile().equals(this.getTile()))) {
//            validMoves.add(Move.BOMB);
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
        List<Move> possibleActions = getPossibleActions(game);
        Move chosenMove = Move.STAY;
        if (random.nextDouble() <= EPSILON) {
//            System.out.println("choose random action");
            chosenMove = possibleActions.get(random.nextInt(possibleActions.size()));
        } else {
            Double tmpMax = -100000000.0;
            List<Move> optimalMoves = new ArrayList<>();
            for(Move move: possibleActions) {
                QPair2 nextState = new QPair2(prevSimpleState, move);
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
        SimpleState2 thisState = extractState(game);
        List<Move> possibleActions = getPossibleActions(game);
        Double maxNextQ = -100000000.0;
        for(Move move: possibleActions) {
            QPair2 nextState = new QPair2(thisState, move);
            Double tmp = getOrCreate(nextState);
            if (tmp > maxNextQ) {
                maxNextQ = tmp;
            }
        }
        QPair2 qPair = new QPair2(prevSimpleState, chosenAction);
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

    public static void main(String[] args) {
        SimpleQLearningPlayer2 qLearningPlayer2 = new SimpleQLearningPlayer2(ColorType.GREEN, "SimpleQ4");
        for(Map.Entry<QPair2, Double> entry: qLearningPlayer2.qTable.entrySet()) {
            if (entry.getValue() > 0.0)
                System.out.println(entry.getKey().toString() + " | " + entry.getValue().toString());
        }
    }
}
