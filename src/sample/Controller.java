package sample;

import bomberman.game.*;
import bomberman.player.AbstractPlayer;
import bomberman.player.HumanPlayer;
import bomberman.player.RandomPlayer;
import bomberman.player.SimplePlayer;
import bomberman.player.rlAgents.deepLearning.DeepLearningPlayer;
import bomberman.player.rlAgents.doubleQlearning.simple3.DoubleQSimple3;
import bomberman.player.rlAgents.qLearning.simple.SimpleQLearningPlayer;
import bomberman.player.rlAgents.qLearning.simple1.SimpleQLearningPlayer1;
import bomberman.player.rlAgents.qLearning.simple2.SimpleQLearningPlayer2;
import bomberman.player.rlAgents.qLearning.simple3.SimpleQLearningPlayer3;
import bomberman.player.rlAgents.sarsa.simple3.SarsaSimple3;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image ;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    public Button startButton;
    public TextArea logsTextField;
    public GridPane boardGridPane;
    private Timeline timeline;
    private String logs;
//    private final int BOARD_SIZE = 6;
//    private final int BOARD_SIZE = 9;
    private final int TILE_SIZE_BASE = 300;


    private final int GEN_COUNT = 100;
    private final int TRAIN_COUNT = 2000;
    private final int EVAL_COUNT = 100;
    private final boolean DISABLE_ANIMATION = true;
//
//    private final int GEN_COUNT = 1;
//    private final int TRAIN_COUNT = 0;
//    private final int EVAL_COUNT = 10;
//    private final boolean DISABLE_ANIMATION = false;

    private Move enteredMove = null;

    private final String AGENT_RED = "File:src/resources/agent copy.png";
    private final String AGENT_BLUE = "File:src/resources/agent copy-3.png";
    private final String AGENT_YELLOW = "File:src/resources/agent copy-4.png";
    private final String AGENT_GREEN = "File:src/resources/agent copy-2.png";
    private final String BOMB_BLACK = "File:src/resources/bombBlack.png";
    private final String BOMB_RED = "File:src/resources/bombRed.png";
    private final String EXPLOSION = "File:src/resources/bombBlack-2.png";
    private final String BREAKABLE_TILE = "File:src/resources/breakableTile.png";
    private final String UNBREAKABLE_TILE = "File:src/resources/unBreakableTile.png";
    private final String FREE_PATH = "File:src/resources/free.png";

    private Game game;

    private boolean created;
    private Timer timer;

    public Controller() {
        logs = "";
        created = false;
    }

    private String getImagePath(String type){
//        System.out.println(type);
        return switch (type) {
            case "U" -> UNBREAKABLE_TILE;
            case "BB" -> BOMB_BLACK;
            case "B" -> BREAKABLE_TILE;
            case "BR" -> BOMB_RED;
            case "Red" -> AGENT_RED;
            case "Blue" -> AGENT_BLUE;
            case "Yellow" -> AGENT_YELLOW;
            case "Green" -> AGENT_GREEN;
            case "F" -> FREE_PATH;
            case "EX" -> EXPLOSION;
            default -> null;
        };
    }

    public void startButtonOnClick(ActionEvent actionEvent) throws InterruptedException {

        //Only Move.STAY vs Q-learning with encoded board as state
//        SimplePlayer simplePlayer2 = new SimplePlayer(ColorType.RED);
//        SimpleQLearningPlayer qLearningPlayer = new SimpleQLearningPlayer(ColorType.GREEN, "SimpleQ");
//        List<AbstractPlayer> playerList = List.of(simplePlayer2, qLearningPlayer);

        //Only Q-learning with encoded board as state vs Q-learning with encoded board as state
//        SimpleQLearningPlayer qLearningPlayer1 = new SimpleQLearningPlayer(ColorType.RED, "SimpleQ1-1");
//        SimpleQLearningPlayer qLearningPlayer = new SimpleQLearningPlayer(ColorType.GREEN, "SimpleQ1-2");
//        List<AbstractPlayer> playerList = List.of(qLearningPlayer1, qLearningPlayer);

//        //Only Q-learning with encoded board as state vs Q-learning with encoded board as state
//        SimpleQLearningPlayer qLearningPlayer1 = new SimpleQLearningPlayer(ColorType.RED, "SimpleQ1-1");
//        SimpleQLearningPlayer qLearningPlayer = new SimpleQLearningPlayer(ColorType.GREEN, "SimpleQ1-2");
//        List<AbstractPlayer> playerList = List.of(qLearningPlayer1, qLearningPlayer);

//         //Only Move.STAY vs Q-learning with 5-tile info as state
//        SimplePlayer simplePlayer2 = new SimplePlayer(ColorType.RED);
//        SimpleQLearningPlayer1 qLearningPlayer2 = new SimpleQLearningPlayer1(ColorType.GREEN, "SimpleQ2");
//        List<AbstractPlayer> playerList = List.of(simplePlayer2, qLearningPlayer2);

//        //Only Move.STAY vs Q-learning with 5-tile info as state
//        SimplePlayer simplePlayer2 = new SimplePlayer(ColorType.RED);
//        SimpleQLearningPlayer3 qLearningPlayer2 = new SimpleQLearningPlayer3(ColorType.GREEN, "SimpleQ4");
//        List<AbstractPlayer> playerList = List.of(simplePlayer2, qLearningPlayer2);

//          //Only Move.STAY vs Sarsa with 5-tile info as state
//          SimplePlayer simplePlayer2 = new SimplePlayer(ColorType.RED);
//          SarsaSimple3 qLearningPlayer2 = new SarsaSimple3(ColorType.GREEN, "SimpleQ3");
//          List<AbstractPlayer> playerList = List.of(simplePlayer2, qLearningPlayer2);
//
//          //Only Move.STAY vs Double Q Learning with 5-tile info as state
//          SimplePlayer simplePlayer2 = new SimplePlayer(ColorType.RED);
//          DoubleQSimple3 qLearningPlayer2 = new DoubleQSimple3(ColorType.GREEN, "SimpleQ3");
//          List<AbstractPlayer> playerList = List.of(simplePlayer2, qLearningPlayer2);

//          //Only Move.STAY vs Double Q Learning with 5-tile info as state -> enable random bombs
//          SarsaSimple3 qLearningPlayer1 = new SarsaSimple3(ColorType.RED, "SimpleQ13");
//          SimpleQLearningPlayer3 qLearningPlayer2 = new SimpleQLearningPlayer3(ColorType.GREEN, "SimpleQ13");
//          DoubleQSimple3 qLearningPlayer3 = new DoubleQSimple3(ColorType.BLUE, "SimpleQ13");
//          List<AbstractPlayer> playerList = List.of(qLearningPlayer1, qLearningPlayer2, qLearningPlayer3);

//                  //Only Move.STAY vs Double Q Learning with 5-tile info as state
//          SimplePlayer simplePlayer2 = new SimplePlayer(ColorType.RED);
//          DeepLearningPlayer qLearningPlayer2 = new DeepLearningPlayer(ColorType.GREEN, "SimpleQ7", 8080);
//          List<AbstractPlayer> playerList = List.of(simplePlayer2, qLearningPlayer2);
//                  //Only Move.STAY vs Double Q Learning with 5-tile info as state
//          RandomPlayer simplePlayer2 = new RandomPlayer(ColorType.RED);
//          DeepLearningPlayer qLearningPlayer2 = new DeepLearningPlayer(ColorType.GREEN, "SimpleQ7", 8080);
//          List<AbstractPlayer> playerList = List.of(simplePlayer2, qLearningPlayer2);

//          //Only Move.STAY vs Double Q Learning with 5-tile info as state -> enable random bombs, board5.txt
//          SarsaSimple3 qLearningPlayer1 = new SarsaSimple3(ColorType.RED, "SimpleQ11");
//          SimpleQLearningPlayer3 qLearningPlayer2 = new SimpleQLearningPlayer3(ColorType.GREEN, "SimpleQ11");
//          DoubleQSimple3 qLearningPlayer3 = new DoubleQSimple3(ColorType.BLUE, "SimpleQ11");
//          List<AbstractPlayer> playerList = List.of(qLearningPlayer1, qLearningPlayer2, qLearningPlayer3);

                  //Only Move.STAY vs Sarsa with 5-tile info as state
          DeepLearningPlayer simplePlayer2 = new DeepLearningPlayer(ColorType.RED, "SimpleQ9", 8080);
          SarsaSimple3 qLearningPlayer2 = new SarsaSimple3(ColorType.GREEN, "SimpleQ3");
          List<AbstractPlayer> playerList = List.of(simplePlayer2, qLearningPlayer2);

//          //Only Move.STAY vs Double Q Learning with 5-tile info as state -> disable random bombs
//          SarsaSimple3 qLearningPlayer1 = new SarsaSimple3(ColorType.RED, "SimpleQ9");
//          SimpleQLearningPlayer3 qLearningPlayer2 = new SimpleQLearningPlayer3(ColorType.GREEN, "SimpleQ9");
//          DoubleQSimple3 qLearningPlayer3 = new DoubleQSimple3(ColorType.BLUE, "SimpleQ9");
//          List<AbstractPlayer> playerList = List.of(qLearningPlayer1, qLearningPlayer2, qLearningPlayer3);

//        //Only Move.STAY vs Q-learning with 5-tile info as state
//        SimpleQLearningPlayer3 qLearningPlayer1 = new SimpleQLearningPlayer3(ColorType.RED, "SimpleQ1");
//        SimpleQLearningPlayer3 qLearningPlayer2 = new SimpleQLearningPlayer3(ColorType.GREEN, "SimpleQ2");
//        List<AbstractPlayer> playerList = List.of(qLearningPlayer1, qLearningPlayer2);

//        SimplePlayer simplePlayer1 = new SimplePlayer(ColorType.BLUE);
//        RandomPlayer randomPlayer = new RandomPlayer(ColorType.YELLOW);
//        HumanPlayer humanPlayer = new HumanPlayer(ColorType.GREEN, this);
//        SimpleQLearningPlayer1 qLearningPlayer2 = new SimpleQLearningPlayer1(ColorType.GREEN, "SimpleQ2");
//        SimpleQLearningPlayer1 qLearningPlayer3 = new SimpleQLearningPlayer1(ColorType.RED, "SimpleQ1");
//        SimpleQLearningPlayer2 qLearningPlayer2 = new SimpleQLearningPlayer2(ColorType.GREEN, "SimpleQ4");

//        List<AbstractPlayer> playerList = List.of(qLearningPlayer3, qLearningPlayer2);
//        List<AbstractPlayer> playerList = List.of(simplePlayer2, qLearningPlayer2);
//        List<AbstractPlayer> playerList = List.of(simplePlayer2, qLearningPlayer);

        game = new Game(playerList, 0, true, GEN_COUNT, TRAIN_COUNT, EVAL_COUNT, DISABLE_ANIMATION);
        game.setController(this);
        Thread thread = new Thread(game);
        thread.start();

        Thread.sleep(100);

//        game.setBombs(List.of(bomb1, bomb2, bomb3));

        String log ="";
        log += "\n----------START---------\n";
        log += game.boardToString();
        log += "\n----------START---------\n";
        logs += log;
        logsTextField.appendText(log);

//        new Thread(() -> {
//            while(!game.isEnded()) {
//                if (this.game.isNeedRender()) {
//                    renderBoard();
//                    this.game.setNeedRender(false);
//                }
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

        if (!DISABLE_ANIMATION) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (game.isNeedRender()) {
                            renderBoard();
                            game.setNeedRender(false);
                        }
                    });
                }
            }, 0, 10);
        }

    }

    private ImageView setImage(Image image, int size, int x, int y) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        GridPane.setRowIndex(imageView, x);
        GridPane.setColumnIndex(imageView, y);
        Image img = imageView.getImage();
        return imageView;
    }

    public void renderBoard() {
//        System.out.println("start rendering");

        boardGridPane.getChildren().clear();
        boardGridPane.setGridLinesVisible(true);
        for (int i = 0; i < game.getSize(); i++) {
            for (int j = 0; j < game.getSize(); j++) {
                Image image = new Image(getImagePath(game.getBoard()[i][j].getTileType().getName()));
                boardGridPane.getChildren().add(setImage(image, TILE_SIZE_BASE/game.getSize(), i, j));
            }
        }

        for(Bomb bomb: game.getBombs()) {
            Image image = new Image(getImagePath((bomb.getCountDown() > 1)? "BB": "BR"));
            boardGridPane.getChildren().add(setImage(image, TILE_SIZE_BASE/game.getSize(), bomb.getTile().getCoordinate().x, bomb.getTile().getCoordinate().y));
        }

        for(Tile explosion: game.getExplosions()) {
            Image image = new Image(getImagePath("EX"));
            boardGridPane.getChildren().add(setImage(image, TILE_SIZE_BASE/game.getSize(), explosion.getCoordinate().x, explosion.getCoordinate().y));
        }

        for(AbstractPlayer player: game.getPlayers()) {
            if (!player.isAlive()) continue;
            Image image = new Image(getImagePath(player.getPlayerColor().getName()));
            boardGridPane.getChildren().add(setImage(image, TILE_SIZE_BASE/game.getSize() - 6, player.getTile().getCoordinate().x, player.getTile().getCoordinate().y));
        }

//        System.out.println("Finished rendering");
    }

    public void handleOnKeyPressed(KeyEvent keyEvent) {
//        System.out.println("Pressed key text: " + keyEvent.getText());
        System.out.println("Pressed key code: " + keyEvent.getCode());
        switch (keyEvent.getCode()) {
            case A -> enteredMove = Move.LEFT;
            case W -> enteredMove = Move.UP;
            case S -> enteredMove = Move.DOWN;
            case D -> enteredMove = Move.RIGHT;
            case B -> enteredMove = Move.BOMB;
        }
    }

    public Move getEnteredMove() {
        return enteredMove;
    }

    public void setEnteredMove(Move enteredMove) {
        this.enteredMove = enteredMove;
    }

    public void stopButtonOnClick(ActionEvent actionEvent) {
        timer.cancel();
    }
}
