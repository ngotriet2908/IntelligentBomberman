package bomberman.player.rlAgents.deepLearning;

import bomberman.game.*;
import bomberman.player.AbstractPlayer;
import bomberman.player.rlAgents.RLPlayer;
import bomberman.player.rlAgents.qLearning.simple.SimpleState;
import bomberman.player.rlAgents.qLearning.simple3.SimpleState3;
import bomberman.player.rlAgents.qLearning.simple3.SimpleState3Tile;
import bomberman.player.rlAgents.qLearning.simple3.SimpleState3Type;
import bomberman.player.rlAgents.result.Result;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class DeepLearningPlayer extends RLPlayer {
    private int port;
    private BufferedReader in;
    private PrintWriter out;
    private ServerSocket server;
    private int killNum;
    private int killedNum;
    private int killedByOwnNum;
    private List<Integer> rewardList;
    private final List<Move> possibleMoves;
    private Move currentMove;
    public DeepLearningPlayer(ColorType playerColor, String name, int port) {
        super(playerColor, name);
        this.port = port;
        try {
            this.server = new ServerSocket(port);
            System.out.println("wait for connection on port " + port);
            Socket client = server.accept();
            System.out.println("got connection on port " + port);
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.out = new PrintWriter(client.getOutputStream(),true);
            out.println(name + ":");
        } catch (IOException e) {
            e.printStackTrace();
        }
        possibleMoves = List.of(Move.LEFT, Move.RIGHT, Move.DOWN, Move.UP, Move.STAY, Move.BOMB);
        rewardList = new ArrayList<>();
        this.killNum = 0;
        this.killedNum = 0;
        this.killedByOwnNum = 0;
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


    @Override
    public Move determineMove(Game game) {
        if (game.isEnded() || !this.isAlive()) {
            System.out.println("Sus1: done 1");
            return Move.STAY;
        }

//        System.out.println("request move " + game.getTickCount());
        out.println("M:" + extractState(game));
        Move moveE = Move.STAY;
        try {
            String move = in.readLine();
//            System.out.println(move);
            int moveD = Integer.parseInt(move);
            switch (moveD) {
                case 0 -> moveE = Move.STAY;
                case 1 -> moveE = Move.LEFT;
                case 2 -> moveE = Move.RIGHT;
                case 3 -> moveE = Move.UP;
                case 4 -> moveE = Move.DOWN;
                case 5 -> moveE = Move.BOMB;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("return move");
        currentMove = moveE;
        return moveE;
    }

    private double[][] getDangerMap(Game game) {

        List<Bomb> q = new ArrayList<>(game.getBombs());
        int[][] regionMatrix = new int[game.getSize()][game.getSize()];
        Map<Integer, Integer> regionToDangerMatrix = new HashMap<>();
        Map<Integer, Boolean> regionToIsYours = new HashMap<>();

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
                if (!regionToDangerMatrix.containsKey(regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y])) {
                    regionToDangerMatrix.put(
                            regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y],
                            bomb.getCountDown());
                    regionToIsYours.put(
                            regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y],
                            bomb.getOwner().equals(this)
                    );
                } else if (bomb.getCountDown() < regionToDangerMatrix.get(regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y])) {
                    regionToIsYours.put(
                            regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y],
                            bomb.getOwner().equals(this)
                    );
                    regionToDangerMatrix.put(
                            regionMatrix[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y],
                            bomb.getCountDown()
                    );
                }
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

        double[][] dangerMap = new double[game.getSize()][game.getSize()];
//        int[][] dangerMap = new int[game.getSize()][game.getSize()];
        for(int i = 0; i < game.getSize(); i++) {
            for(int j = 0; j < game.getSize(); j++) {
                dangerMap[i][j] =  regionToDangerMatrix.getOrDefault(regionMatrix[i][j], 0);
                if (dangerMap[i][j] != 0) {
                    dangerMap[i][j] = dangerMap[i][j] /Game.BOMB_COUNT_DOWN *
                            ((regionToIsYours.getOrDefault(regionMatrix[i][j], false)? 1 : -1));

//                    dangerMap[i][j] = dangerMap[i][j] *
//                            ((regionToIsYours.getOrDefault(regionMatrix[i][j], false)? 1 : -1));

                }
            }
        }

        return dangerMap;
    }

    private String extractState(Game game) {
        double[][] dangerMap = getDangerMap(game);
//        int[][] dangerMap = getDangerMap(game);

        int[][] tileType = new int[game.getSize()][game.getSize()];
        int[][] hasPlayer = new int[game.getSize()][game.getSize()];
        int[][] hasOpponent = new int[game.getSize()][game.getSize()];


        for(int i = 0; i < game.getSize(); i ++) {
            for (int j = 0; j < game.getSize(); j++) {
                hasPlayer[i][j] = (this.isAlive() && (i == this.getTile().getCoordinate().x) && (j == this.getTile().getCoordinate().y)) ? 1 : 0;
                hasOpponent[i][j] = 0;
                for(AbstractPlayer player: game.getPlayers()) {
                    if (player.isAlive() && !player.equals(this) &&
                            ((i == player.getTile().getCoordinate().x) && (j == player.getTile().getCoordinate().y))) {
                        hasOpponent[i][j] = 1;
                        break;
                    }
                }

                tileType[i][j] = (game.getBoard()[i][j].getTileType() == TileType.BREAKABLE_TILE)? 0 :
                        (game.getBoard()[i][j].getTileType() == TileType.UNBREAKABLE_TILE)? -1 : 1 ;
                for(Bomb bomb: game.getBombs()) {
                    if ((i == bomb.getTile().getCoordinate().x) && (j == bomb.getTile().getCoordinate().y)) {
                        tileType[i][j] = -1;
                        break;
                    }
                }
            }
        }

//        double[][] danger = new double[game.getSize()][game.getSize()];
//        List<String> vector = new ArrayList<>();
//        for(int i = 0; i < game.getSize(); i ++) {
//            for (int j = 0; j < game.getSize(); j++) {
//                int value = ((tileType[i][j] + 1) << 5) |
//                        (hasPlayer[i][j] << 4) |
//                        (hasOpponent[i][j] << 3) | (dangerMap[i][j] + Game.BOMB_COUNT_DOWN);
//
//                double std = (value*1.0)/128;
//                danger[i][j] = std;
//                vector.add(String.format("%.5f", std));
//            }
//        }

        List<String> vector = new ArrayList<>();
        for(int i = 0; i < game.getSize(); i ++) {
            for (int j = 0; j < game.getSize(); j++) {
                vector.add(String.valueOf(tileType[i][j]));
            }
        }
        for(int i = 0; i < game.getSize(); i ++) {
            for (int j = 0; j < game.getSize(); j++) {
                vector.add(String.valueOf(hasPlayer[i][j]));
            }
        }
        for(int i = 0; i < game.getSize(); i ++) {
            for (int j = 0; j < game.getSize(); j++) {
                vector.add(String.valueOf(hasOpponent[i][j]));
            }
        }
        for(int i = 0; i < game.getSize(); i ++) {
            for (int j = 0; j < game.getSize(); j++) {
                vector.add(String.format("%.1f", dangerMap[i][j]));
            }
        }
//
////        printIntMatrix(tileType);
////        printIntMatrix(hasPlayer);
////        printIntMatrix(hasOpponent);
////        printDoubleMatrix(dangerMap);
//
////        printDoubleMatrix(danger);

        return String.join("/", vector);

    }

    public void printIntMatrix(int[][] matrix) {
        for (int[] ints : matrix) {
            for (int anInt : ints) {
                System.out.printf("%4d", anInt);
            }
            System.out.println();
        }
    }
    public void printDoubleMatrix(double[][] matrix) {
        System.out.println();
        for (double[] ints : matrix) {
            for (double anInt : ints) {
                System.out.printf("  %7.5f", anInt);
            }
            System.out.println();
        }
        System.out.println();
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

    private int getReward(Result result) {
        if (result.isKilled()) {
            killedNum++;
        }
        if (result.getGetDestroyedPlayers() > 0) {
            killNum += result.getGetDestroyedPlayers();
        }

        if (result.isKilled()) return REWARD_KILLED;
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
    public void updateResult(Result result, Game game) {
//        if (game.isEnded() || this.isAlive()) {
//            System.out.println("Sus2: done 2");
//            return;
//        }
        String thisState = extractState(game);
        boolean actual_end = false;
        int alivePlayers = 0;
        for(AbstractPlayer player : game.getPlayers()) {
            if (player.isAlive()) alivePlayers++;
        }

        if (alivePlayers <= 1) {
            actual_end = true;
        }

        boolean ended = actual_end || !this.isAlive();
        if (ended) {
//            Collections.sort(rewardList);
//            Map<Integer, Integer> rewardCount = new HashMap<>();
//            rewardList.forEach(integer -> {
//                if (rewardCount.containsKey(integer)) {
//                    rewardCount.put(integer, rewardCount.get(integer) + 1);
//                } else {
//                    rewardCount.put(integer, 1);
//                }
//            });
//            String arrays = "[";
//            for(Map.Entry<Integer, Integer> entry: rewardCount.entrySet()) {
//                arrays += "(" + entry.getKey() + ", " + entry.getValue() + "), ";
//            }
//            arrays += "]";
//
//            int sum = rewardList.stream().reduce(0, Integer::sum);
//            System.out.println("Rewards: " +
//                    sum
//                    + ", " + arrays);
//
//            rewardList = new ArrayList<>();
        }
//        rewardList.add(getReward(result));
        reward += getReward(result);
        out.println("R:" + getReward(result) + ":" + ended + ":" + thisState);
        try {
            String status = in.readLine();
//            System.out.println(status);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("updated result >" + "R:" + getReward(result) + ":" + ended + ":" + thisState);
    }

    @Override
    public void startNewGame(boolean isTraining, int generation, int episode) {
        out.println("ST:" + isTraining + ":" + generation + ":" + episode);
        try {
            String status = in.readLine();
//            System.out.println(status);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reward = 0;
    }

    public void closeSocket() {
        System.out.println("kills: " + killNum);
        System.out.println("killeds: " + killedNum);
        try {
            out.println("C:");
            this.server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
