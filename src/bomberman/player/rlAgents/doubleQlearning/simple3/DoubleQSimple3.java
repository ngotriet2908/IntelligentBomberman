package bomberman.player.rlAgents.doubleQlearning.simple3;

import bomberman.game.*;
import bomberman.player.AbstractPlayer;
import bomberman.player.SimplePlayer;
import bomberman.player.rlAgents.RLPlayer;
import bomberman.player.rlAgents.qLearning.simple1.SimpleQLearningPlayer1;
import bomberman.player.rlAgents.qLearning.simple1.SimpleState1Type;
import bomberman.player.rlAgents.qLearning.simple3.QPair3;
import bomberman.player.rlAgents.qLearning.simple3.SimpleState3;
import bomberman.player.rlAgents.qLearning.simple3.SimpleState3Tile;
import bomberman.player.rlAgents.qLearning.simple3.SimpleState3Type;
import bomberman.player.rlAgents.result.Result;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DoubleQSimple3 extends RLPlayer {

    private static final int REWARD_MOVE = -1;
    private static final int REWARD_BOMB = -3;
    private static final int REWARD_INVALID_MOVE = -30;
    private static final int REWARD_KILLED = -300;
    //    private static final int REWARD_KILLED_BY_OWN = -1000;
    private static final int REWARD_KILLED_BY_OWN = -400;
    //    private static final int REWARD_KILL = +1000;
    private static final int REWARD_KILL = +700;
    private static final int REWARD_DESTROY_TILE = +50;

        private static final boolean SHOW_LOG = false;
//    private static final boolean SHOW_LOG = true;
//        private static final boolean DISABLE_EPSILON = false;
    private static final boolean DISABLE_EPSILON = true;

    private static final double EPSILON = 0.1;
    private static final double DISCOUNT_FACTOR = 0.98;
    private static final double LEARNING_RATE = 0.2;
    private Map<QPair3, Double> qTable1;
    private Map<QPair3, Double> qTable2;
    private SimpleState3 prevSimpleState;
    private Move chosenAction;
    private final Random random;
    private final List<Move> possibleMoves;
    private int generations;
    private int killNum;
    private int killedNum;
    //    private int killedNum;
    public DoubleQSimple3(ColorType playerColor, String name) {
        super(playerColor, name);
        random = new Random();
        possibleMoves = List.of(Move.LEFT, Move.RIGHT, Move.DOWN, Move.UP, Move.STAY, Move.BOMB);
        this.generations = 0;
        this.killNum = 0;
        this.killedNum = 0;
        try {
            FileInputStream fi = new FileInputStream("src/bomberman/player/rlAgents/doubleQlearning/simple3/trainData/" + this.getName() + "_1");
            ObjectInputStream oi = new ObjectInputStream(fi);
            qTable1 = (HashMap)oi.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            qTable1 = new HashMap<>();
        }
        System.out.println("q1:" + qTable1.size());

        try {
            FileInputStream fi = new FileInputStream("src/bomberman/player/rlAgents/doubleQlearning/simple3/trainData/" + this.getName() + "_2");
            ObjectInputStream oi = new ObjectInputStream(fi);
            qTable2 = (HashMap)oi.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            qTable2 = new HashMap<>();
        }
        System.out.println("q2:" + qTable1.size());
    }

    public void saveQTableToFile() {
        System.out.println("kills: " + killNum);
        System.out.println("killeds: " + killedNum);
        if (DISABLE_EPSILON) return;
        try {
            FileOutputStream fileOut = new FileOutputStream("src/bomberman/player/rlAgents/doubleQlearning/simple3/trainData/" + this.getName() + "_1");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(qTable1);
            objectOut.close();
            System.out.println("The Object  was successfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            FileOutputStream fileOut = new FileOutputStream("src/bomberman/player/rlAgents/doubleQlearning/simple3/trainData/" + this.getName() + "_2");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(qTable2);
            objectOut.close();
            System.out.println("The Object  was successfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Double getOrCreate1(QPair3 pair) {
        if (!qTable1.containsKey(pair)) {
            qTable1.put(pair, 0.0);
            return 0.0;
        } else {
//            System.out.println("get");
            return qTable1.get(pair);
        }
    }

    private Double getOrCreate2(QPair3 pair) {
        if (!qTable2.containsKey(pair)) {
            qTable2.put(pair, 0.0);
            return 0.0;
        } else {
//            System.out.println("get");
            return qTable2.get(pair);
        }
    }

    private int[][] getDangerMap(Game game) {

        List<Bomb> q = new ArrayList<>(game.getBombs());
        int[][] regionMatrix = new int[game.getSize()][game.getSize()];
        Map<Integer, Integer> regionToDangerMatrix = new HashMap<>();

        int index = 1;
        int qIndex = 0;
        while(qIndex < q.size()) {
            Bomb bomb = q.get(qIndex);
            qIndex++;
            if (regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y] == 0) {
                regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y] = index;
                regionToDangerMatrix.put(index, bomb.getCountDown());
                index++;
            } else {
                regionToDangerMatrix.put(
                        regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y],
                        Math.min(
                                regionToDangerMatrix.get(regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y]),
                                bomb.getCountDown()
                        )
                );
            }

            //up
            for (int i = 1; i <= bomb.getBlastRadius(); i++) {
                Tile tile = game.getBoard()[bomb.getTile().getCoordinate().x - i][bomb.getTile().getCoordinate().y];
                if (tile.getTileType() != TileType.UNBREAKABLE_TILE) {
                    regionMatrix[tile.getCoordinate().x][tile.getCoordinate().y] =
                            regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y];
                }
                if (bfsBomb(q, bomb, tile, game.getBombs())) break;

            }

            //down
            for (int i = 1; i <= bomb.getBlastRadius(); i++) {
                Tile tile = game.getBoard()[bomb.getTile().getCoordinate().x + i][bomb.getTile().getCoordinate().y];
                if (tile.getTileType() != TileType.UNBREAKABLE_TILE) {
                    regionMatrix[tile.getCoordinate().x][tile.getCoordinate().y] =
                            regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y];
                }
                if (bfsBomb(q, bomb, tile, game.getBombs())) break;
            }

            //left
            for (int i = 1; i <= bomb.getBlastRadius(); i++) {
                Tile tile = game.getBoard()[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y - i];
                if (tile.getTileType() != TileType.UNBREAKABLE_TILE) {
                    regionMatrix[tile.getCoordinate().x][tile.getCoordinate().y] =
                            regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y];
                }
                if (bfsBomb(q, bomb, tile, game.getBombs())) break;
            }

            //right
            for (int i = 1; i <= bomb.getBlastRadius(); i++) {
                Tile tile = game.getBoard()[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y + i];
                if (tile.getTileType() != TileType.UNBREAKABLE_TILE) {
                    regionMatrix[tile.getCoordinate().x][tile.getCoordinate().y] =
                            regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y];
                }
                if (bfsBomb(q, bomb, tile, game.getBombs())) break;
            }

            index++;
        }

        int[][] dangerMap = new int[game.getSize()][game.getSize()];
        for(int i = 0; i < game.getSize(); i++) {
            for(int j = 0; j < game.getSize(); j++) {
                dangerMap[i][j] =  regionToDangerMatrix.getOrDefault(regionMatrix[i][j], 0);
                if (dangerMap[i][j] != 0) {
                    dangerMap[i][j] = Game.BOMB_COUNT_DOWN - dangerMap[i][j] + 1;
                }
            }
        }

        return dangerMap;
    }

    private boolean bfsBomb(List<Bomb> willExplodeQueue, Bomb bomb, Tile tile, List<Bomb> bombs) {

        if (tile.getTileType() == TileType.UNBREAKABLE_TILE) {
            return true;
        } else {
            for(Bomb bomb1: bombs) {
                if (bomb1.getTile().equals(tile) && !willExplodeQueue.contains(bomb1) ) {
                    willExplodeQueue.add(bomb1);
                }
            }
            return tile.getTileType() == TileType.BREAKABLE_TILE;
        }
    }

    private SimpleState3 extractState(Game game) {
        int[][] dangerMap = getDangerMap(game);

        int minDist = 100000000;
        Point minVec = new Point(-100,-100);
        SimpleState3 simpleState = new SimpleState3(new SimpleState3Tile[5], minVec, 0, new ArrayList<>());
//        simpleState.setPlacedBombs(
//                (int) game.getBombs()
//                        .stream()
//                        .filter(bomb -> {
//                            if (bomb.getOwner() == null) return false;
//                            return bomb.getOwner().equals(this);
//                        }).count());
//        simpleState.setPlacedBombs(getMinStepToOpponent(game));
        game.getBombs().forEach(bomb -> {
            if (bomb.getOwner() != null && bomb.getOwner().equals(this)) {
                simpleState.getBombLists().add(new Point(
                        bomb.getTile().getCoordinate().x - this.getTile().getCoordinate().x,
                        bomb.getTile().getCoordinate().y - this.getTile().getCoordinate().y
                ));
            }
        });
        simpleState.getBombLists().sort(Comparator.comparing(bomb -> bomb.x*1000 + bomb.y));
        for(AbstractPlayer player: game.getPlayers()) {
            if (player.equals(this)) continue;
            int dist = Math.abs(player.getTile().getCoordinate().x - this.getTile().getCoordinate().x) +
                    Math.abs(player.getTile().getCoordinate().y - this.getTile().getCoordinate().y);
            if (dist < minDist) {
                minDist = dist;
                minVec = new Point(
                        player.getTile().getCoordinate().x - this.getTile().getCoordinate().x,
                        player.getTile().getCoordinate().y - this.getTile().getCoordinate().y
                );
            }
        }

        simpleState.setVectorToNearestOpponent(minVec);
        Optional<Bomb> bomb1 = game.getBombs().stream().filter(bomb -> bomb.getTile().equals(this.getTile())).findFirst();
        simpleState.getSurrounding()[0].setTileType(bomb1.map(bomb -> SimpleState3Type.BLOCKED).orElse(SimpleState3Type.FREE));
        simpleState.getSurrounding()[0].setDangerLevel(dangerMap[this.getTile().getCoordinate().x][this.getTile().getCoordinate().y]);

        editStateTile(
                simpleState.getSurrounding()[1],
                game,
                game.getBoard()[this.getTile().getCoordinate().x + Move.RIGHT.getPoint().x]
                        [this.getTile().getCoordinate().y + Move.RIGHT.getPoint().y],
                dangerMap
        );

        editStateTile(
                simpleState.getSurrounding()[2],
                game,
                game.getBoard()[this.getTile().getCoordinate().x + Move.UP.getPoint().x]
                        [this.getTile().getCoordinate().y + Move.UP.getPoint().y],
                dangerMap
        );

        editStateTile(
                simpleState.getSurrounding()[3],
                game,
                game.getBoard()[this.getTile().getCoordinate().x + Move.LEFT.getPoint().x]
                        [this.getTile().getCoordinate().y + Move.LEFT.getPoint().y],
                dangerMap
        );

        editStateTile(
                simpleState.getSurrounding()[4],
                game,
                game.getBoard()[this.getTile().getCoordinate().x + Move.DOWN.getPoint().x]
                        [this.getTile().getCoordinate().y + Move.DOWN.getPoint().y],
                dangerMap
        );
//        if (game.getBombs().size() > 0)
//            printArray(dangerMap);
//        System.out.println(simpleState);



        return simpleState;
    }

    private int getMinStepToOpponent(Game game) {
        Map<Point, Integer> d = new HashMap<>();
        for(int i = 0; i < game.getSize(); i++) {
            for(int j = 0; j < game.getSize(); j++) {
                d.put(game.getBoard()[i][j].getCoordinate(), -1);
            }
        }

        List<Point> q= new ArrayList<>();
        q.add(this.getTile().getCoordinate());
        d.put(this.getTile().getCoordinate(), 0);
        List<Move> fourCorner = List.of(Move.LEFT, Move.RIGHT, Move.UP, Move.DOWN);
//        System.out.println("added root");
        while(q.size() > 0) {
            Point point = q.remove(0);

            for(Move move: fourCorner) {
                Point nextMove = new Point(point.x + move.getPoint().x, point.y + move.getPoint().y);

                boolean occupied = game.getBoard()[nextMove.x][nextMove.y].getTileType() != TileType.FREE;

                for(Bomb bomb: game.getBombs()) {
                    if (bomb.getTile().getCoordinate().equals(nextMove)) {
                        occupied = true;
                        break;
                    }
                }

                if (occupied) continue;

                if (d.get(nextMove) == -1) {
                    d.put(nextMove, d.get(point) + 1);
                    for(AbstractPlayer player: game.getPlayers()) {
                        if (player.isAlive() && !player.equals(this) && nextMove.equals(player.getTile().getCoordinate())) {
                            return d.get(nextMove);
                        }
                    }
                    q.add(nextMove);
                }
            }
        }
        return -1;
    }

    private void printArray(int[][] x) {
        System.out.println("---------------------");
        for(int[] y: x) {
            System.out.println(Arrays.toString(y));
        }
        System.out.println("---------------------");
    }

    private void editStateTile(SimpleState3Tile simpleState1Tile, Game game, Tile newTile, int[][] dangerMap) {
        Optional<Bomb> bomb1 = game.getBombs().stream().filter(bomb -> bomb.getTile().equals(newTile)).findFirst();
        SimpleState3Type tileType = SimpleState3Type.FREE;
        switch (newTile.getTileType()) {
            case BREAKABLE_TILE -> tileType = SimpleState3Type.WALL;
            case UNBREAKABLE_TILE -> tileType = SimpleState3Type.BLOCKED;
        }

//        simpleState1Tile.setTileType(bomb1.map(bomb -> SimpleState3Type.BLOCKED).orElse(tileType));
        if (bomb1.isPresent()) {
            tileType = SimpleState3Type.BLOCKED;
        }

//        if (game.getPlayers()
//                .stream().anyMatch(abstractPlayer -> !abstractPlayer.equals(this) && abstractPlayer.getTile().equals(newTile))) {
//            tileType = SimpleState3Type.BLOCKED;
//        }

        simpleState1Tile.setDangerLevel(dangerMap[newTile.getCoordinate().x][newTile.getCoordinate().y]);
//        simpleState1Tile.setHasOpponent(game.getPlayers()
//                .stream().anyMatch(abstractPlayer -> !abstractPlayer.equals(this) && abstractPlayer.getTile().equals(newTile))
//        );
        simpleState1Tile.setTileType(tileType);
    }

    private List<Move> getPossibleActions(SimpleState3 simpleState1) {
        List<Move> validMoves = new ArrayList<>();
        validMoves.add(Move.STAY);

        if (simpleState1.getSurrounding()[0].getTileType() == SimpleState3Type.FREE) {
            validMoves.add(Move.BOMB);
        }
        if (simpleState1.getSurrounding()[1].getTileType() == SimpleState3Type.FREE) {
            validMoves.add(Move.RIGHT);
        }
        if (simpleState1.getSurrounding()[2].getTileType() == SimpleState3Type.FREE) {
            validMoves.add(Move.UP);
        }
        if (simpleState1.getSurrounding()[3].getTileType() == SimpleState3Type.FREE) {
            validMoves.add(Move.LEFT);
        }
        if (simpleState1.getSurrounding()[4].getTileType() == SimpleState3Type.FREE) {
            validMoves.add(Move.DOWN);
        }
        if (SHOW_LOG) {
            System.out.println(simpleState1);
            System.out.println(Arrays.toString(validMoves.toArray()));
        }
        return validMoves;
    }

    private int rewardFunctionMove(int tickCount) {
//        System.out.println(Math.max((int)(REWARD_MOVE*(1 + tickCount*1.0/10)), -10));
        return Math.max((int)(REWARD_MOVE*(1 + tickCount*1.0/10)), -5);
    }

    private int rewardFunctionBomb(int tickCount) {
//        System.out.println(Math.max((int)(REWARD_BOMB*(1 + tickCount*1.0/10)), -10));
        return Math.max((int)(REWARD_BOMB*(1 + tickCount*1.0/10)), -6);
    }

    private int getReward(Result result, int tileNum, int tickCount) {
        int killedReward = 0;

        if (result.isDiedByOwn()) {
            killedReward = REWARD_KILLED_BY_OWN;
        }
        else if (result.isKilled()) {
            killedReward = REWARD_KILLED;
        }
//        if (!result.isValidMove()) System.out.println("not a valid move");
//        int reward = (((result.isValidMove()) ? (chosenAction == Move.BOMB)? REWARD_BOMB : REWARD_MOVE
        int reward = (((result.isValidMove()) ? (chosenAction == Move.BOMB)? rewardFunctionBomb(tickCount) : rewardFunctionMove(tickCount)
                : REWARD_INVALID_MOVE) +
                killedReward +
                REWARD_KILL * result.getGetDestroyedPlayers() +
                REWARD_DESTROY_TILE * result.getDestroyedWalls());
//        if (reward > 0) {
//            System.out.println(reward);
//        }
        if (result.getGetDestroyedPlayers() > 0) {
//            System.out.println("killed reward: " + reward);
            killNum++;
//            System.out.println("Killed opponent");
        }

        if (result.isDiedByOwn()) {
            killedNum++;
//            System.out.println("Killed opponent");
        }
        return reward;
    }

    @Override
    public Move determineMove(Game game) {
        prevSimpleState = extractState(game);
        List<Move> possibleActions = getPossibleActions(prevSimpleState);
        Move chosenMove;
        if (random.nextDouble() <= EPSILON && !DISABLE_EPSILON) {
//            System.out.println("choose random action");
            chosenMove = possibleActions.get(random.nextInt(possibleActions.size()));
        } else {
            Double tmpMax = -100000000.0;
            List<Move> optimalMoves = new ArrayList<>();
            for(Move move: possibleActions) {
                QPair3 nextState = new QPair3(prevSimpleState, move);
                Double tmp = getOrCreate1(nextState);
                if (tmp > tmpMax) {
                    tmpMax = tmp;
                    optimalMoves = new ArrayList<>();
                    optimalMoves.add(move);
                } else if (tmp.compareTo(tmpMax) == 0) {
                    optimalMoves.add(move);
                }
            }

            for(Move move: possibleActions) {
                QPair3 nextState = new QPair3(prevSimpleState, move);
                Double tmp = getOrCreate2(nextState);
                if (tmp > tmpMax) {
                    tmpMax = tmp;
                    optimalMoves = new ArrayList<>();
                    optimalMoves.add(move);
                } else if (tmp.compareTo(tmpMax) == 0) {
                    optimalMoves.add(move);
                }
            }

            if (optimalMoves.size() <= 0) {
                System.out.println("Error: agent produce null move");
                chosenMove = possibleActions.get(random.nextInt(possibleActions.size()));
            } else {
                chosenMove = optimalMoves.get(random.nextInt(optimalMoves.size()));
            }
        }
        chosenAction = chosenMove;
        return chosenAction;
    }

    @Override
    public void updateResult(Result result, Game game) {
        if (DISABLE_EPSILON) return;
        generations++;
        SimpleState3 thisState = extractState(game);
        List<Move> possibleActions = getPossibleActions(thisState);
        Double maxNextQ = -100000000.0;
        List<Double> possibleQ = new ArrayList<>();

        if (random.nextDouble() <= 0.5) {
            for(Move move: possibleActions) {
                QPair3 nextState = new QPair3(thisState, move);
                Double tmp = getOrCreate1(nextState);
                possibleQ.add(tmp);
                if (tmp > maxNextQ) {
                    maxNextQ = tmp;
                }
            }
            QPair3 qPair = new QPair3(prevSimpleState, chosenAction);
            Double currentQ = getOrCreate1(qPair);

            int walls = 0;
            for(int i = 0; i < game.getSize(); i++) {
                for(int j = 0; j < game.getSize(); j++) {
                    walls += (game.getBoard()[i][j].getTileType() == TileType.BREAKABLE_TILE)? 1 : 0;
                }
            }

            int reward = getReward(result, walls, game.getTickCount());
//        if (true) {
//        if (reward > -1) {
//            System.out.println(reward);
//        }
            double newQ = currentQ + LEARNING_RATE*(reward + DISCOUNT_FACTOR*maxNextQ - currentQ);
            qTable1.put(qPair, newQ);
        } else {
            for(Move move: possibleActions) {
                QPair3 nextState = new QPair3(thisState, move);
                Double tmp = getOrCreate2(nextState);
                possibleQ.add(tmp);
                if (tmp > maxNextQ) {
                    maxNextQ = tmp;
                }
            }
            QPair3 qPair = new QPair3(prevSimpleState, chosenAction);
            Double currentQ = getOrCreate2(qPair);

            int walls = 0;
            for(int i = 0; i < game.getSize(); i++) {
                for(int j = 0; j < game.getSize(); j++) {
                    walls += (game.getBoard()[i][j].getTileType() == TileType.BREAKABLE_TILE)? 1 : 0;
                }
            }

            int reward = getReward(result, walls, game.getTickCount());
//        if (true) {
//        if (reward > -1) {
//            System.out.println(reward);
//        }
            double newQ = currentQ + LEARNING_RATE*(reward + DISCOUNT_FACTOR*maxNextQ - currentQ);
            qTable2.put(qPair, newQ);
        }


    }

//    public static void main(String[] args) {
//        DoubleQSimple3 qLearningPlayer2 = new DoubleQSimple3(ColorType.GREEN, "SimpleQ2");
//        for(Map.Entry<QPair3, Double> entry: qLearningPlayer2.qTable.entrySet()) {
////            if (entry.getKey().getSimpleState().getPlacedBombs() > 0) {
////
////            }
//            System.out.println(entry.getKey() + "| r: " + entry.getValue());
//        }
//        qLearningPlayer2.saveQTableToFile();
//    }
}
