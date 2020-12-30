package com.javarush.games.racer;

import com.javarush.engine.cell.*;
import com.javarush.games.racer.road.RoadManager;

public class RacerGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int CENTER_X = WIDTH / 2;
    public static final int ROADSIDE_WIDTH = 14;
    private static final int RACE_GOAL_CARS_COUNT = 40;
    private boolean isGameStopped;
    private int score;
    private RoadMarking roadMarking;
    private PlayerCar player;
    private RoadManager roadManager;
    private FinishLine finishLine;
    private ProgressBar progressBar;


    @Override
    public void initialize(){
        showGrid(false);
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    private void createGame(){
        setTurnTimer(40);
        score = 3500;
        roadMarking = new RoadMarking();
        player = new PlayerCar();
        roadManager = new RoadManager();
        finishLine = new FinishLine();
        progressBar = new ProgressBar(RACE_GOAL_CARS_COUNT);
        drawScene();
        isGameStopped = false;
    }

    private void gameOver(){
        isGameStopped = true;
        stopTurnTimer();
        player.stop();
        showMessageDialog(Color.NONE, "Game Over", Color.RED, 24 );
    }

    private void win(){
        isGameStopped = true;
        stopTurnTimer();
        showMessageDialog(Color.WHITE, "YOU WIN", Color.GREEN, 24);
    }

    private void drawScene(){
        drawField();
        roadMarking.draw(this);
        player.draw(this);
        roadManager.draw(this);
        finishLine.draw(this);
        progressBar.draw(this);
    }
    private void drawField(){
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (i == CENTER_X){
                    setCellColor(CENTER_X, j, Color.WHITE);
                } else if ((i >= ROADSIDE_WIDTH && i < WIDTH - ROADSIDE_WIDTH)){
                    setCellColor(i, j, Color.GREY);
                } else {
                    setCellColor(i, j, Color.GREEN);
                }
            }
        }
    }

    @Override
    public void setCellColor(int x, int y, Color color) {
        if ((x >= 0 && x < 64) && (y >= 0 && y < 64)) super.setCellColor(x, y, color);
    }

    private void moveAll(){
        roadMarking.move(player.speed);
        player.move();
        roadManager.move(player.speed);
        finishLine.move(player.speed);
        progressBar.move(roadManager.getPassedCarsCount());
    }

    @Override
    public void onTurn(int step) {
        if (finishLine.isCrossed(player)){
            win();
            drawScene();
        } else {
            if (roadManager.getPassedCarsCount() >= RACE_GOAL_CARS_COUNT) {
                finishLine.show();
            }

            if (roadManager.checkCrush(player)) {
                gameOver();
                drawScene();
            } else {
                moveAll();
                roadManager.generateNewRoadObjects(this);
                score -= 5;
                setScore(score);
                drawScene();
            }
        }

    }

    @Override
    public void onKeyPress(Key key) {
        switch (key){
            case RIGHT: player.setDirection(Direction.RIGHT); break;
            case LEFT: player.setDirection(Direction.LEFT); break;
            case SPACE: if (isGameStopped){ createGame();} break;
            case UP: player.speed = 2;
        }

    }

    @Override
    public void onKeyReleased(Key key) {
        switch (key){
            case RIGHT: if (player.getDirection().equals(Direction.RIGHT)) player.setDirection(Direction.NONE); break;
            case LEFT: if (player.getDirection().equals(Direction.LEFT)) player.setDirection(Direction.NONE); break;
            case UP: player.speed = 1;
        }
    }
}
