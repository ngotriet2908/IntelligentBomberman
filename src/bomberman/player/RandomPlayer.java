package bomberman.player;

import bomberman.game.*;
import bomberman.player.rlAgents.qLearning.simple3.SimpleState3;
import bomberman.player.rlAgents.qLearning.simple3.SimpleState3Tile;
import bomberman.player.rlAgents.qLearning.simple3.SimpleState3Type;

import java.util.*;

public class RandomPlayer extends AbstractPlayer{

    private Random random;

    public RandomPlayer(ColorType playerColor) {
        super(playerColor);
        random = new Random();
    }


    private SimpleState3 extractState(Game game) {

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


        return simpleState;
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
//
//        if (simpleState1.getSurrounding()[0].getTileType() == SimpleState3Type.FREE) {
//            validMoves.add(Move.BOMB);
//        }
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
//        if (SHOW_LOG) {
//            System.out.println(simpleState1);
//            System.out.println(Arrays.toString(validMoves.toArray()));
//        }
        return validMoves;
    }

    @Override
    public Move determineMove(Game game) {
//        List<Move> moves = getPossibleActions(extractState(game));
        List<Move> moves = List.of(Move.LEFT, Move.RIGHT, Move.DOWN, Move.UP, Move.STAY);
        if (random.nextInt(100) + 1 < 1) {
            return Move.BOMB;
        } else {
            return moves.get(random.nextInt(moves.size()));
        }
    }
}
