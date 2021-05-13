package bomberman.game;

import bomberman.player.AbstractPlayer;
import bomberman.player.rlAgents.RLPlayer;
import bomberman.player.rlAgents.qLearning.simple.SimpleQLearningPlayer;
import bomberman.player.rlAgents.qLearning.simple1.SimpleQLearningPlayer1;
import bomberman.player.rlAgents.qLearning.simple2.SimpleQLearningPlayer2;
import bomberman.player.rlAgents.qLearning.simple3.SimpleQLearningPlayer3;
import bomberman.player.rlAgents.result.Result;
import sample.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Game implements Runnable{
    private static final int STATE_DELAY = 50;
//    private static final int STATE_DELAY = 10;
    public static final int BOMB_COUNT_DOWN = 3;
    private static final int MAX_TURN = 100;
    private static final int BOMB_BLAST_RADIUS = 1;
    private List<AbstractPlayer> players;
    private int size;
    private List<Bomb> bombs;
    private Tile[][] board;
    private int tickCount;
    private boolean paused;
    private boolean ended;
    private Controller controller;
    private List<Tile> explosions;
    private boolean needRender;
    private boolean autoReset;
    private Random random;
    private int loopCount;
    private int maxLoop;
    private Map<AbstractPlayer, Result> resultMap;
    private Map<Tile, AbstractPlayer> explosionMap;
    private boolean isAnimationDisabled;

    public Game(List<AbstractPlayer> players, int size, boolean autoReset, int maxLoop, boolean isAnimationDisabled) {
        this.players = players;
        this.size = size;
        this.board = new Tile[size][size];
        this.autoReset = autoReset;
        this. random = new Random();
        this.maxLoop = maxLoop;
        this.loopCount = 0;
        this.isAnimationDisabled = isAnimationDisabled;
    }

    public void initialise(String filename) {
        List<String[]> rows = new ArrayList<>();
        this.tickCount = 0;
        this.paused = false;
        this.ended = true;
        this.explosions = new ArrayList<>();
        this.explosionMap = new HashMap<>();
        this.loopCount++;
        try {
            File myObj = new File("src/bomberman/boardSamples/" + filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                rows.add(data.split(" "));
//                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        this.size = rows.size();
        this.board = new Tile[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < rows.get(i).length; j++) {
                switch (rows.get(i)[j]) {
                    case "U" -> this.board[i][j] = new Tile(new Point(i, j), TileType.UNBREAKABLE_TILE);
                    case "B" -> this.board[i][j] = new Tile(new Point(i, j), TileType.BREAKABLE_TILE);
                    case "F" -> this.board[i][j] = new Tile(new Point(i, j), TileType.FREE);
                }
            }
        }

        for(AbstractPlayer player : players) {
            switch (player.getPlayerColor()) {
                case RED -> player.setTile(this.board[1][1]);
//                case RED -> player.setTile(this.board[1 + random.nextInt(size - 3)][1 + random.nextInt(size - 3)]);
                case BLUE -> player.setTile(this.board[1][size - 2]);
                case YELLOW -> player.setTile(this.board[size - 2][1]);
                case GREEN -> player.setTile(this.board[size - 2][size - 2]);
            }
            player.setAlive(true);
        }
//        Bomb bomb1 = new Bomb(this.board[1][1], BOMB_COUNT_DOWN, 2, players.get(0));
//        Bomb bomb2 = new Bomb(this.board[1][2], 4, 2, players.get(0));
//        this.bombs = List.of(bomb1, bomb2);
        this.bombs = new ArrayList<>();
        this.resultMap = new HashMap<>();
        players.forEach(abstractPlayer -> {
            resultMap.put(abstractPlayer, null);
        });
        players.forEach(abstractPlayer -> resultMap.put(abstractPlayer, new Result(true, false, 0, 0, false)));
        this.needRender = false;
    }

    public void processExplosions(List<Bomb> remainingBombs) {

        this.players.stream().filter(AbstractPlayer::isAlive).forEach(abstractPlayer -> {
            resultMap.get(abstractPlayer).setDestroyedWalls(0);
            resultMap.get(abstractPlayer).setGetDestroyedPlayers(0);
            resultMap.get(abstractPlayer).setKilled(false);
            resultMap.get(abstractPlayer).setValidMove(true);
        });

        List<Bomb> willExplodeQueue = new ArrayList<>();
        for(Bomb bomb: bombs) {
            bomb.setCountDown(bomb.getCountDown() - 1);
            if (bomb.getCountDown() <= 0) willExplodeQueue.add(bomb);
        }

        explosions = new ArrayList<>();
        explosionMap = new HashMap<>();
        int index = 0;
        while(index < willExplodeQueue.size()) {
            Bomb bomb = willExplodeQueue.get(index);
            if (!explosions.contains(bomb.getTile())) {
                explosions.add(bomb.getTile());
                explosionMap.put(bomb.getTile(), bomb.getOwner());
            }

            //up
            for (int i = 1; i <= bomb.getBlastRadius(); i++) {
                Tile tile = this.board[bomb.getTile().getCoordinate().x - i][bomb.getTile().getCoordinate().y];
                if (bfsBomb(willExplodeQueue, bomb, tile)) break;
            }

            //down
            for (int i = 1; i <= bomb.getBlastRadius(); i++) {
                Tile tile = this.board[bomb.getTile().getCoordinate().x + i][bomb.getTile().getCoordinate().y];
                if (bfsBomb(willExplodeQueue, bomb, tile)) break;
            }

            //left
            for (int i = 1; i <= bomb.getBlastRadius(); i++) {
                Tile tile = this.board[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y - i];
                if (bfsBomb(willExplodeQueue, bomb, tile)) break;
            }

            //right
            for (int i = 1; i <= bomb.getBlastRadius(); i++) {
                Tile tile = this.board[bomb.getTile().getCoordinate().x][bomb.getTile().getCoordinate().y + i];
                if (bfsBomb(willExplodeQueue, bomb, tile)) break;
            }

            index++;
        }

        for(Tile tile: explosions) {
            for(AbstractPlayer player: players) {
                if (player.getTile().equals(tile)) {
                    resultMap.get(player).setKilled(true);
                    AbstractPlayer murderer = explosionMap.get(tile);
//                    System.out.println("player " + player.getPlayerColor() + " is killed in tile " + tile.getCoordinate() + " by " + murderer);
                    if (murderer != null ) {
                        if (!murderer.equals(player)) {
                            resultMap.get(murderer).incKill();
                        } else {
                            resultMap.get(murderer).setDiedByOwn(true);
                        }
                    }
                    player.setAlive(false);
//                    System.out.println("result when killed: " + resultMap.get(player));
                }
            }
        }


        for(Bomb bomb: bombs) {
            if (!willExplodeQueue.contains(bomb)) remainingBombs.add(bomb);
        }
    }

    private boolean bfsBomb(List<Bomb> willExplodeQueue, Bomb bomb, Tile tile) {
        if (tile.getTileType() == TileType.UNBREAKABLE_TILE) {
            return true;
        } else {
            if (!explosions.contains(tile)) {
                for(Bomb bomb1: bombs) {
                    if (bomb1.getTile().equals(tile) && !willExplodeQueue.contains(bomb1) ) {
                        willExplodeQueue.add(bomb1);
                    }
                }
                explosions.add(tile);
                explosionMap.put(tile, bomb.getOwner());
                return tile.getTileType() == TileType.BREAKABLE_TILE;
            }
        }
        return false;
    }

    public String processMoves(Map<AbstractPlayer, Tile> updatedPlayer, List<Bomb> willAddBomb) {
        String logs = "---------Moves----------\n";
        for(AbstractPlayer player: players) {
            if (!player.isAlive()) continue;
            Move move = player.determineMove(this);
            logs += "player " + player.getPlayerColor().toString() + ": " + move.name() + "\n";
            if (move == Move.BOMB) {
                boolean alreadyHasBomb = false;
                for (Bomb bomb: bombs) {
                    if (bomb.getTile().equals(player.getTile())) {
                        alreadyHasBomb = true;
                        break;
                    }
                }

                //check if there will be bomb placed there
                for (Bomb bomb: willAddBomb) {
                    if (bomb.getTile().equals(player.getTile())) {
                        alreadyHasBomb = true;
                        break;
                    }
                }

                if (!alreadyHasBomb) {
//                    System.out.println("added bomb");
                    willAddBomb.add(new Bomb(player.getTile(), BOMB_COUNT_DOWN, BOMB_BLAST_RADIUS, player));
                } else {
                    resultMap.get(player).setValidMove(false);
                }
                continue;
            }

            if (move == Move.STAY) {
                continue;
            }

            Tile newTile = board[player.getTile().getCoordinate().x + move.getPoint().x][player.getTile().getCoordinate().y + move.getPoint().y];
            if (newTile.getTileType() != TileType.FREE) {
                resultMap.get(player).setValidMove(false);
                continue;
            }

            boolean collideWithPlayer = false;
            for (AbstractPlayer player1: players) {
                if (player1.getTile().equals(newTile)) {
                    collideWithPlayer = true;
                    break;
                }
            }
            if (collideWithPlayer) continue;

            // check with already updated player
            collideWithPlayer = false;
            for (AbstractPlayer player1: players) {
                if (updatedPlayer.containsKey(player1) && updatedPlayer.get(player1).equals(newTile)) {
                    collideWithPlayer = true;
                    break;
                }
            }
            if (collideWithPlayer) continue;

            // check if there was a bomb there
            boolean collideWithBomb = false;
            for (Bomb bomb: bombs) {
                if (bomb.getTile().equals(newTile)) {
                    collideWithBomb = true;
                    break;
                }
            }
            if (collideWithBomb) continue;

            updatedPlayer.put(player, newTile);
        }
        logs += "--------------------------\n";
        return logs;
    }

    public void addBombRandomly(List<Bomb> willAddBomb) {
        if (tickCount < MAX_TURN) return;
        for (AbstractPlayer player : players) {
            if (player.isAlive()) {

                // check % for putting additional bombs only check when turn > 100
                if (random.nextDouble() > (tickCount - MAX_TURN)*1.0/(MAX_TURN + (tickCount - MAX_TURN))) continue;

                boolean alreadyHasBomb = false;
                for (Bomb bomb: bombs) {
                    if (bomb.getTile().equals(player.getTile())) {
                        alreadyHasBomb = true;
                        break;
                    }
                }
                if (!alreadyHasBomb) {
//                    System.out.println("added bomb by random at tick " + tickCount + ", with prob = " + (tickCount - MAX_TURN)*1.0/(MAX_TURN + (tickCount - MAX_TURN)));
                    willAddBomb.add(new Bomb(player.getTile(), BOMB_COUNT_DOWN, BOMB_BLAST_RADIUS, null));
                }
            }
        }
    }

    public void updateBoard(List<Bomb> remainingBombs) {
//        System.out.println("Explosion tiles: " + explosions.size());
        for(Tile tile: explosions) {
            if (tile.getTileType() == TileType.BREAKABLE_TILE) {
                AbstractPlayer murderer = explosionMap.get(tile);
                if (murderer != null) {
//                    System.out.println("inc break wall");
                    resultMap.get(murderer).incDestroyedWalls();
                }
                tile.setTileType(TileType.FREE);
            }
        }
        bombs = new ArrayList<>();
        bombs.addAll(remainingBombs);
    }

    public void updatedPlayer(Map<AbstractPlayer, Tile> updatedPlayer, List<Bomb> willAddBombs) {
       for(Map.Entry<AbstractPlayer, Tile> entry: updatedPlayer.entrySet()) {
           entry.getKey().setTile(entry.getValue());
       }
       bombs.addAll(willAddBombs);
    }

    public String updatePlayersResult(Map<AbstractPlayer, Result> resultMap) {
        String logs = "---------Result----------\n";
        for(AbstractPlayer abstractPlayer: players) {
            if (abstractPlayer instanceof RLPlayer && resultMap.containsKey(abstractPlayer)) {
                Result result = resultMap.get(abstractPlayer);
//                System.out.println("updating Q table of " + abstractPlayer.getPlayerColor() + " with result " + result);
                logs += "player " + abstractPlayer.getPlayerColor().toString() + ": " + result.toString() + "\n";
                ((RLPlayer) abstractPlayer).updateResult(result, this);
            }
        }
        logs += "--------------------------\n";
        return logs;
    }

    private void waitForRendering() {
//        System.out.println("wait for rendering");
        needRender = true;
        while (needRender) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("finished rendering");
        try {
            Thread.sleep(STATE_DELAY);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        do {
            initialise("board2.txt");
//            initialise("board1.txt");
//            System.out.println("Loop: " + loopCount);
            if (((loopCount*1.0/maxLoop)*100) % 10 == 0) {
                System.out.println("progress: " + (int)((loopCount*1.0/maxLoop)*100) + "%");
            }
            ended = false;

            if (!isAnimationDisabled) controller.logsTextField.setText("");

            while(!ended) {
                tickCount++;

                int alivePlayers = 0;
                for(AbstractPlayer player : players) {
                    if (player.isAlive()) alivePlayers++;
                }

                if (alivePlayers <= 1) {
                    if (!isAnimationDisabled) controller.logsTextField.appendText("Game ended \n");
                    ended = true;
                    break;
                }


                if (!isAnimationDisabled) controller.logsTextField.appendText("Tick: " + tickCount + "\n");

                if (!isAnimationDisabled) waitForRendering();

                List<Bomb> remainingBombs = new ArrayList<>();
                List<Bomb> willAddBombs = new ArrayList<>();
                Map<AbstractPlayer, Tile> updatedPlayers = new HashMap<>();

                processExplosions(remainingBombs);

                if (!isAnimationDisabled) waitForRendering();
                String logs = processMoves(updatedPlayers, willAddBombs);

//                addBombRandomly(willAddBombs);
                updateBoard(remainingBombs);
                if (!isAnimationDisabled) waitForRendering();
                explosions = new ArrayList<>();
                updatedPlayer(updatedPlayers, willAddBombs);

                logs += updatePlayersResult(this.resultMap);
                if (!isAnimationDisabled) controller.logsTextField.appendText(logs);
                if (!isAnimationDisabled) waitForRendering();

                if (!isAnimationDisabled) {
                    try {
                        Thread.sleep(STATE_DELAY);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }

//            System.out.println("finished rendering 1");
            }
//            System.out.println("game ended");
        } while(loopCount < maxLoop);
        for(AbstractPlayer abstractPlayer: players) {
            if (abstractPlayer instanceof SimpleQLearningPlayer) {
                ((SimpleQLearningPlayer) abstractPlayer).saveQTableToFile();
            } else if (abstractPlayer instanceof SimpleQLearningPlayer1) {
                ((SimpleQLearningPlayer1) abstractPlayer).saveQTableToFile();
            } else if (abstractPlayer instanceof SimpleQLearningPlayer2) {
                ((SimpleQLearningPlayer2) abstractPlayer).saveQTableToFile();
            } else if (abstractPlayer instanceof SimpleQLearningPlayer3) {
                ((SimpleQLearningPlayer3) abstractPlayer).saveQTableToFile();
            }
        }
//        System.out.println("loop ended");
    }

    public List<AbstractPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<AbstractPlayer> players) {
        this.players = players;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public void setBombs(List<Bomb> bombs) {
        this.bombs = bombs;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public int getTickCount() {
        return tickCount;
    }

    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public String boardToString() {
        StringBuilder result = new StringBuilder();
        for(Tile[] x: board) {
            result.append(Arrays.toString(x)).append("\n");
        }
        return result.toString();
    }

    public void setBoard(Tile[][] board) {
        this.board = board;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public List<Tile> getExplosions() {
        return explosions;
    }

    public void setExplosions(List<Tile> explosions) {
        this.explosions = explosions;
    }

    public boolean isNeedRender() {
        return needRender;
    }

    public void setNeedRender(boolean needRender) {
        this.needRender = needRender;
    }

    public boolean isAutoReset() {
        return autoReset;
    }

    public void setAutoReset(boolean autoReset) {
        this.autoReset = autoReset;
    }
}
