package com.example.snakeai;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultiplayersMode {
    static int SCREEN_WIDTH = 24;
    static int SCREEN_HEIGHT = 24;
    static final int UNIT_SIZE = 25;
    static int speed = 5;
    static int speed1 = 6;
    static int speed2 = 6;
    static int score1, score2;
    static int applesColor = 0;
    static int appleX = 0;
    static int appleY = 0;
    static List<Corner> snake1 = new ArrayList<>();
    static List<Corner> snake2 = new ArrayList<>();
    static Dir direction1 = Dir.left;
    static Dir direction2 = Dir.right;
    static boolean gameOver = false;
    static boolean pause = false;
    static String winner;
    static Random random = new Random();
    private static final Timeline timeline = new Timeline();

    public static class Corner {
        int x;
        int y;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public enum Dir {
        left, right, up, down;
    }

    public static void pauseGame() {
        pause = true;
        timeline.stop();
    }

    public static void continueGame() {
        pause = false;
        timeline.play();
    }

    public static void restartGame() {
        speed = 5;
        stopGame();
        startGame();
    }

    public static void stopGame() {
        gameOver = true;
        timeline.stop();
        snake1.clear();
        snake2.clear();
    }

    public static void startGame() {
        direction1 = Dir.left;
        direction2 = Dir.left;
        snake1.add(new Corner(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2));
        snake1.add(new Corner(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2));
        snake1.add(new Corner(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2));
        snake2.add(new Corner(SCREEN_WIDTH / 3, SCREEN_HEIGHT / 3));
        snake2.add(new Corner(SCREEN_WIDTH / 3, SCREEN_HEIGHT / 3));
        snake2.add(new Corner(SCREEN_WIDTH / 3, SCREEN_HEIGHT / 3));
        timeline.play();
        gameOver = false;
        speed1 = 6;
        speed2 = 6;
    }

    //food
    public static void newFood() {
        start:
        while (true) {
            appleX = random.nextInt(SCREEN_WIDTH);
            appleY = random.nextInt(SCREEN_HEIGHT);

            for (Corner corner : snake1) {
                if (corner.x == appleX && corner.y == appleY) {
                    continue start;
                }
            }

            for (Corner corner : snake2) {
                if (corner.x == appleX && corner.y == appleY) {
                    continue start;
                }
            }

            applesColor = random.nextInt(5);
            speed++;
            break;
        }
    }

    public static void creatPanel(Stage primaryStage) {
        try {
            newFood();
            VBox root = new VBox();
            Canvas c = new Canvas(SCREEN_WIDTH * UNIT_SIZE, SCREEN_HEIGHT * UNIT_SIZE);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer() {
                long lastTick = 0;

                public void handle(long now) {
                    if (lastTick == 0) {
                        lastTick = now;
                        tick(gc);
                        return;
                    }

                    if (now - lastTick > 1000000000 / speed) {
                        lastTick = now;
                        tick(gc);
                        return;
                    }
                }
            }.start();

            Scene scene = new Scene(root, SCREEN_WIDTH * UNIT_SIZE, SCREEN_HEIGHT * UNIT_SIZE);

            //control
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {

                if (key.getCode() == KeyCode.W) {
                    if (direction1 != Dir.down) {
                        direction1 = Dir.up;
                    }
                }

                if (key.getCode() == KeyCode.UP) {
                    if (direction2 != Dir.down) {
                        direction2 = Dir.up;
                    }
                }

                if (key.getCode() == KeyCode.S) {
                    if (direction1 != Dir.up)
                        direction1 = Dir.down;
                }

                if (key.getCode() == KeyCode.DOWN) {
                    if (direction2 != Dir.up)
                        direction2 = Dir.down;
                }

                if (key.getCode() == KeyCode.A) {
                    if (direction1 != Dir.right)
                        direction1 = Dir.left;
                }

                if (key.getCode() == KeyCode.LEFT) {
                    if (direction2 != Dir.right)
                        direction2 = Dir.left;
                }

                if (key.getCode() == KeyCode.D) {
                    if (direction1 != Dir.left)
                        direction1 = Dir.right;
                }

                if (key.getCode() == KeyCode.RIGHT) {
                    if (direction2 != Dir.left)
                        direction2 = Dir.right;
                }

                if (key.getCode() == KeyCode.R) {
                    restartGame();
                }

                if (key.getCode() == KeyCode.P) {
                    if (!pause) {
                        pause = false;
                        pauseGame();
                    } else {
                        pause = true;
                        continueGame();
                    }
                }
            });

            //add snake1 parts
            snake1.add(new Corner(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2));
            snake1.add(new Corner(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2));
            snake1.add(new Corner(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2));

            //add snake2 parts
            snake2.add(new Corner(SCREEN_WIDTH / 3, SCREEN_HEIGHT / 3));
            snake2.add(new Corner(SCREEN_WIDTH / 3, SCREEN_HEIGHT / 3));
            snake2.add(new Corner(SCREEN_WIDTH / 3, SCREEN_HEIGHT / 3));

            primaryStage.setScene(scene);
            primaryStage.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //tick
    public static void tick(GraphicsContext gc) {
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", 200, 300);

            if (winner == "P1") {
                gc.setFill(Color.RED);
                gc.setFont(new Font("", 50));
                gc.fillText("Winner: " + winner, 200, 350);
            }else if (winner == "P2") {
                gc.setFill(Color.RED);
                gc.setFont(new Font("", 50));
                gc.fillText("Winner: " + winner, 200, 350);
            }else if (snake1.get(0).x < 0 || snake1.get(0).y < 0 || snake1.get(0).x > SCREEN_WIDTH
                || snake1.get(0).y > SCREEN_HEIGHT) {
                    if (score1 > score2) {
                        gc.setFill(Color.RED);
                        gc.setFont(new Font("", 50));
                        gc.fillText("Winner: P1", 200, 350);
                    } else if (score2 > score1) {
                        gc.setFill(Color.RED);
                        gc.setFont(new Font("", 50));
                        gc.fillText("Winner: P2", 200, 350);
                    } else {
                        gc.setFill(Color.RED);
                        gc.setFont(new Font("", 50));
                        gc.fillText("Draw", 250, 350);
                    }
                } else if (snake2.get(0).x < 0 || snake2.get(0).y < 0 || snake2.get(0).x > SCREEN_WIDTH
                    || snake2.get(0).y > SCREEN_HEIGHT) {
                if (score1 > score2) {
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("", 50));
                    gc.fillText("Winner: P1", 200, 350);
                } else if (score2 > score1) {
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("", 50));
                    gc.fillText("Winner: P2", 200, 350);
                } else {
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("", 50));
                    gc.fillText("Draw", 250, 350);
                }
            }


                return;
            }

            if (pause) {
                gc.setFill(Color.RED);
                gc.setFont(new Font("", 50));
                gc.fillText("PAUSE", 250, 300);
                return;
            }

            for (int i = snake1.size() - 1; i >= 1; i--) {
                snake1.get(i).x = snake1.get(i - 1).x;
                snake1.get(i).y = snake1.get(i - 1).y;
            }

            switch (direction1) {
                case up:
                    snake1.get(0).y--;
                    if (snake1.get(0).y < 0) {
                        gameOver = true;
                    }
                    break;
                case down:
                    snake1.get(0).y++;
                    if (snake1.get(0).y > SCREEN_HEIGHT) {
                        gameOver = true;
                    }
                    break;
                case left:
                    snake1.get(0).x--;
                    if (snake1.get(0).x < 0) {
                        gameOver = true;
                    }
                    break;
                case right:
                    snake1.get(0).x++;
                    if (snake1.get(0).x > SCREEN_WIDTH) {
                        gameOver = true;
                    }
                    break;
            }

            for (int i = snake2.size() - 1; i >= 1; i--) {
                snake2.get(i).x = snake2.get(i - 1).x;
                snake2.get(i).y = snake2.get(i - 1).y;
            }

            switch (direction2) {
                case up:
                    snake2.get(0).y--;
                    if (snake2.get(0).y < 0) {
                        gameOver = true;
                    }
                    break;
                case down:
                    snake2.get(0).y++;
                    if (snake2.get(0).y > SCREEN_HEIGHT) {
                        gameOver = true;
                    }
                    break;
                case left:
                    snake2.get(0).x--;
                    if (snake2.get(0).x < 0) {
                        gameOver = true;
                    }
                    break;
                case right:
                    snake2.get(0).x++;
                    if (snake2.get(0).x > SCREEN_WIDTH) {
                        gameOver = true;
                    }
                    break;
            }

            //eat food
            if (appleX == snake1.get(0).x && appleY == snake1.get(0).y) {
                snake1.add(new Corner(-1, -1));
                speed1++;
                score1 = speed1 - 6;
                newFood();
            } else if (appleX == snake2.get(0).x && appleY == snake2.get(0).y) {
                snake2.add(new Corner(-1, -1));
                speed2++;
                score2 = speed2 - 6;
                newFood();
            }

            //self destroy
            for (int i = 1; i < snake1.size(); i++) {
                if (snake1.get(0).x == snake1.get(i).x && snake1.get(0).y == snake1.get(i).y) {
                    gameOver = true;
                }
            }

            for (int i = 1; i < snake2.size(); i++) {
                if (snake2.get(0).x == snake2.get(i).x && snake2.get(0).y == snake2.get(i).y) {
                    gameOver = true;
                }
            }

            // check collision
            for (int i = 0; i < snake1.size(); i++) {
                if (snake1.get(i).x == snake2.get(0).x && snake1.get(i).y == snake2.get(0).y) {
                    gameOver = true;
                    winner = "P1";
                }
            }

            for (int i = 0; i < snake2.size(); i++) {
                if (snake2.get(i).x == snake1.get(0).x && snake2.get(i).y == snake1.get(0).y) {
                    gameOver = true;
                    winner = "P2";
                }
            }

            //background color
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, SCREEN_WIDTH * UNIT_SIZE, SCREEN_HEIGHT * UNIT_SIZE);

            //scored P1
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("", 25));
            gc.fillText("Score P1:" + (speed1 - 6), 15, 30);

            //scored P2
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("", 25));
            gc.fillText("Score P2:" + (speed2 - 6), 480, 30);


            //random food color
            Color color = Color.WHITE;
            switch (applesColor) {
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
            gc.fillOval(appleX * UNIT_SIZE, appleY * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

            //snake
            for (Corner corner : snake1) {
                gc.setFill(Color.LIGHTGREEN);
                gc.fillRect(corner.x * UNIT_SIZE, corner.y * UNIT_SIZE, UNIT_SIZE - 1, UNIT_SIZE - 1);
                gc.setFill(Color.GREEN);
                gc.fillRect(corner.x * UNIT_SIZE, corner.y * UNIT_SIZE, UNIT_SIZE - 2, UNIT_SIZE - 2);
            }

            for (Corner corner : snake2) {
                gc.setFill(Color.RED);
                gc.fillRect(corner.x * UNIT_SIZE, corner.y * UNIT_SIZE, UNIT_SIZE - 1, UNIT_SIZE - 1);
                gc.setFill(Color.LIGHTPINK);
                gc.fillRect(corner.x * UNIT_SIZE, corner.y * UNIT_SIZE, UNIT_SIZE - 2, UNIT_SIZE - 2);
            }
        }
    }
