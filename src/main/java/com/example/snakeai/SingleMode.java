package com.example.snakeai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class SingleMode{
    static int SCREEN_WIDTH = 24;
    static int SCREEN_HEIGHT = 24;
    static final int UNIT_SIZE = 25;
    static int speed = 5;
    static int applesColor = 0;
    static int appleX = 0;
    static int appleX1 = 0;
    static int appleY = 0;
    static int appleY1 = 0;
    static List<Corner> snake = new ArrayList<>();
    static List<Corner> wall = new ArrayList<>();
    static Dir direction = Dir.left;

    private static final Timeline timeline = new Timeline();
    static boolean gameOver = false;
    static Random random = new Random();
    static boolean pause = false;
    static int scored = 0;

    public static class Corner{
        int x;
        int y;
        public Corner(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    public static void pauseGame(){
        pause = true;
        timeline.stop();
    }

    public static void continueGame(){
        pause = false;
        timeline.play();
    }

    public static void restartGame(){
        stopGame();
        startGame();
    }

    public static void stopGame(){
        gameOver = true;
        timeline.stop();
        scored = 0;
        snake.clear();
        wall.clear();
    }

    public static void startGame(){
        direction = Dir.left;
        snake.add(new Corner(SCREEN_WIDTH/2,SCREEN_HEIGHT/2));
        snake.add(new Corner(SCREEN_WIDTH/2,SCREEN_HEIGHT/2));
        snake.add(new Corner(SCREEN_WIDTH/2,SCREEN_HEIGHT/2));
        timeline.play();
        gameOver = false;
        speed = 6;
        addWall();
    }

    public static void addWall(){
        for(int i = 1; i <= 15; i++){
            wall.add(new Corner(random.nextInt(SCREEN_WIDTH), random.nextInt(SCREEN_HEIGHT)));
        }
    }
    public enum Dir{
        left, right, up, down
    }

    //food
    public static void newFood(){
        start: while (true){
            appleX = random.nextInt(SCREEN_WIDTH);
            appleY = random.nextInt(SCREEN_HEIGHT);

            for (Corner corner : snake){
                if(corner.x == appleX && corner.y == appleY){
                    continue start;
                }
            }

            applesColor = random.nextInt(5);
//            speed++;
            break;
        }
    }

    public static void newFood1(){
        start: while (true){
            appleX1 = random.nextInt(SCREEN_WIDTH);
            appleY1 = random.nextInt(SCREEN_HEIGHT);

            for (Corner corner : snake){
                if(corner.x == appleX1 && corner.y == appleY1){
                    continue start;
                }
            }
            break;
        }
    }

    public static void creatPanel(Stage primaryStage){
        try {
            newFood();
            newFood1();
            VBox root = new VBox();
            Canvas c = new Canvas(SCREEN_WIDTH*UNIT_SIZE, SCREEN_HEIGHT*UNIT_SIZE);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer(){
                long lastTick = 0;
                public void handle(long now){
                    if(lastTick==0){
                        lastTick = now;
                        tick(gc);

                        return;
                    }

                    if (now - lastTick > 1000000000/speed){
                        lastTick = now;
                        tick(gc);
                        return;
                    }
                }
            }.start();

            Scene scene = new Scene(root, SCREEN_WIDTH*UNIT_SIZE, SCREEN_HEIGHT*UNIT_SIZE);

            //control
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key ->{

                if(key.getCode() == KeyCode.W){
                    if(direction != Dir.down){
                        direction = Dir.up;
                    }
                }

                if(key.getCode() == KeyCode.S){
                    if(direction != Dir.up){
                        direction = Dir.down;
                    }
                }

                if(key.getCode() == KeyCode.A){
                    if(direction != Dir.right){
                        direction = Dir.left;
                    }
                }

                if(key.getCode() == KeyCode.D){
                    if(direction != Dir.left){
                        direction = Dir.right;
                    }
                }

                if (key.getCode() == KeyCode.R){
                    pause = false;
                    restartGame();
                }

                if(key.getCode() == KeyCode.P){
                    if(!pause){
                        pause = true;
                        pauseGame();
                    } else {
                        pause = false;
                        continueGame();
                    }
                }
            });

            //add snake parts
            snake.add(new Corner(SCREEN_WIDTH/2,SCREEN_HEIGHT/2));
            snake.add(new Corner(SCREEN_WIDTH/2,SCREEN_HEIGHT/2));
            snake.add(new Corner(SCREEN_WIDTH/2,SCREEN_HEIGHT/2));

            //add wall parts
            addWall();

            primaryStage.setScene(scene);
            primaryStage.show();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //tick
    public static void tick(GraphicsContext gc){
        if(gameOver){
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", 200, 300);
            return;
        }

        if (pause){
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("PAUSE", 250, 300);
            return;
        }

        for(int i = snake.size() - 1; i >= 1; i--){
            snake.get(i).x = snake.get(i-1).x;
            snake.get(i).y = snake.get(i-1).y;
        }

        switch (direction){
            case up:
                snake.get(0).y--;
                if(snake.get(0).y < 0){
                    gameOver = true;
                }
                break;
            case down:
                snake.get(0).y++;
                if(snake.get(0).y > SCREEN_HEIGHT){
                    gameOver = true;
                }
                break;
            case left:
                snake.get(0).x--;
                if(snake.get(0).x < 0){
                    gameOver = true;
                }
                break;
            case right:
                snake.get(0).x++;
                if(snake.get(0).x > SCREEN_WIDTH){
                    gameOver = true;
                }
                break;
        }

        //eat food
        if(appleX == snake.get(0).x && appleY == snake.get(0).y){
            snake.add(new Corner(-1,-1));
            scored+=1;
            newFood();
        }

        if(appleX1 == snake.get(0).x && appleY1 == snake.get(0).y){
            scored-=1;
            newFood1();
        }

        //self destroy
        for(int i = 1; i < snake.size(); i++){
            if(snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y){
                gameOver = true;
            }
        }

        //wall destroy
        for (int i = 0; i < wall.size(); i++) {
            if (snake.get(0).x == wall.get(i).x && snake.get(0).y == wall.get(i).y) {
                gameOver = true;
            }
        }

        //background color
        gc.setFill(Color.BLACK);
        gc.fillRect(0 ,0, SCREEN_WIDTH*UNIT_SIZE, SCREEN_HEIGHT*UNIT_SIZE);

        //scored
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("",25));
        gc.fillText("Score:" + scored, 10, 20 );

        //random food color
        Color color = Color.WHITE;
        switch (applesColor){
            case 0:
                color = Color.GOLD;
                break;
            case 1:
                color = Color.YELLOW;
                break;
            case 2:
                color = Color.BLUE;
                break;
            case 3:
                color = Color.PURPLE;
                break;
            case 4:
                color = Color.PINK;
                break;
        }
        gc.setFill(color);
        gc.fillOval(appleX*UNIT_SIZE, appleY*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

        gc.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        gc.fillOval(appleX1*UNIT_SIZE, appleY1*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

        //snake
        for (Corner corner : snake){
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(corner.x*UNIT_SIZE, corner.y*UNIT_SIZE, UNIT_SIZE-1, UNIT_SIZE-1);
            gc.setFill(Color.GREEN);
            gc.fillRect(corner.x*UNIT_SIZE, corner.y*UNIT_SIZE, UNIT_SIZE-2, UNIT_SIZE-2);
        }

        //wall
        for (Corner corner : wall){
            gc.setFill(Color.RED);
            gc.fillRect(corner.x*UNIT_SIZE, corner.y*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

        }
    }

}