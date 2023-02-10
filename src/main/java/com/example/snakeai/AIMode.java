package com.example.snakeai;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
public class AIMode{

    static int SCREEN_WIDTH;
    static int SCREEN_HEIGHT;
    static int UNIT_SIZE = 25;
    static int GAME_UNITS;
    static final int DELAY = 45;
    static List<Corner> snake = new ArrayList<>();
//    static int x[];
//    static int y[];
    static int bodyParts = 6;
    static int applesEaten;
    static int appleX;
    static int appleY;
    static char direction = 'R';
    static boolean running = false;
    static Timeline timer = new Timeline();
    static Random random = new Random();
    static Stage primaryStage;
    static int xDistance;
    static int yDistance;
    static int hCost;
    static int numDirections = 0;
    static char[] directions;
    static int count = 0;
    static int gCost;

    public static class Corner {
        int x;
        int y;
        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void creatPanel (Stage stage, int w, int h) {
        SCREEN_WIDTH = w;
        SCREEN_HEIGHT = h;
        primaryStage = stage;
        random = new Random();
        try {
            newApple();
            VBox root = new VBox();
            Canvas c = new Canvas(SCREEN_WIDTH , SCREEN_HEIGHT);
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

                    if (now - lastTick > 1000000000 / 5) {
                        lastTick = now;
                        tick(gc);
                        return;
                    }
                }
            }.start();

            Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

            snake.add(new Corner(SCREEN_WIDTH/2, SCREEN_HEIGHT/2));
            snake.add(new Corner(SCREEN_WIDTH/2, SCREEN_HEIGHT/2));
            snake.add(new Corner(SCREEN_WIDTH/2, SCREEN_HEIGHT/2));



            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startGame();
    }

    public static void startGame(){
        running = true;
        timer.setDelay(Duration.INDEFINITE);
        timer.play();
    }

    public static void tick(GraphicsContext gc){
        if(running){
            //background color
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, SCREEN_WIDTH * UNIT_SIZE, SCREEN_HEIGHT * UNIT_SIZE);
            timer.setDelay(Duration.millis(DELAY));

            //snake color
            for(Corner corner : snake){
                gc.setFill(Color.WHITE);
                gc.fillRect(corner.x, corner.y, UNIT_SIZE, UNIT_SIZE);
            }

            //food color
            gc.setFill(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            gc.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            //scored
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("", 30));
            gc.fillText("Score: " + applesEaten, 15 ,30);

            //eat food


            if(numDirections == -1){
                pathFinder();
            }
            move();
            checkApple();
            checkCollisions();
        }
    }

    public static void newApple(){
        start:while (true) {
            appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE);
            appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE);

            for (Corner corner : snake) {
                if (corner.x == appleX && corner.y == appleY) {
                    continue start;
                }
            }

            List<Node> path = aStar();
            if (path == null) {
                numDirections = -1;
                return;
            }
            numDirections = path.size();
            directions = new char[numDirections];
            for (int i = 0; i < numDirections; i++) {
                directions[i] = path.get(i).getDirection();
            }
        }
    }

    public static void move() {
        if (numDirections != -1) {
            direction = directions[numDirections - 1];
            numDirections--;
        }

        for(int i = snake.size(); i > 0; i--) {
            snake.get(i).x = snake.get(i-1).x;
            snake.get(i).y = snake.get(i-1).y;
        }

        switch(direction) {
            case 'U':
                snake.get(0).y--;
                break;
            case 'D':
                snake.get(0).y++;
                break;
            case 'L':
                snake.get(0).x--;
                break;
            case 'R':
                snake.get(0).x++;
                break;
        }
    }
    public static void checkApple() {
        if((snake.get(0).x == appleX) && (snake.get(0).y == appleY)) {
            snake.add(new Corner(-1,-1));
            applesEaten++;
            newApple();
        }
    }

    public static void checkCollisions() {
        for(int i = snake.size(); i>0; i--) {
            if((snake.get(0).x == snake.get(i).x) && (snake.get(0).y == snake.get(i).y)) {
                running = false;
            }
        }

        if(snake.get(0).x < 0) {
            running = false;
        }
        if(snake.get(0).x >= SCREEN_WIDTH) {
            running = false;
        }
        if (snake.get(0).y < 0) {
            running = false;
        }
        if (snake.get(0).y >= SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }
    public static boolean isBlocked(char d, int x1, int y1) {
        if (d == 'R' ) {
            if (x1 >= SCREEN_WIDTH) {
                return true;
            }
            for (int i = snake.size(); i > 0; i--) {
                if ((x1 == snake.get(i).x) && (y1 == snake.get(i).y)) {
                    return true;
                }
            }
        } else if (d == 'L') {
            if (x1 < 0) {
                return true;
            }
            for (int i = snake.size(); i > 0; i--) {
                if ((x1 == snake.get(i).x) && (y1 == snake.get(i).y)) {
                    return true;
                }
            }
        } else if (d == 'D') {
            if (y1 >= SCREEN_WIDTH) {
                return true;
            }
            for (int i = snake.size(); i > 0; i--) {
                if ((x1 == snake.get(i).x) && (y1 == snake.get(i).y)) {
                    return true;
                }
            }
        } else {
            if (y1 < 0) {
                return true;
            }
            for (int i = snake.size(); i > 0; i--) {
                if ((x1 == snake.get(i).x) && (y1 == snake.get(i).y)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Node> aStar(){
        List<Node> parents = new ArrayList<Node>();
        PriorityQueue<Node> open = new PriorityQueue<Node>();
        List<Node> closed = new ArrayList<Node>();

        count = 0;
        gCost = 0;
        Node startNode = new Node(snake.get(0).x, snake.get(0).y, gCost, findHCost(snake.get(0).x, snake.get(0).y));
        startNode.setDirection(direction);
        Node goalNode = new Node(appleX, appleY, findHCost(snake.get(0).x, snake.get(0).y), 0);

        open.add(startNode);

        while (!open.isEmpty()) {

            count++;

            Node current = open.poll();
            current.close();
            closed.add(current);

            if (count > (SCREEN_WIDTH / UNIT_SIZE) * (SCREEN_HEIGHT / UNIT_SIZE) * 10) {
                System.out.println("Couldnt find path");
                return null;
            }

            if (current.same(goalNode)) {
                //backtrack and create parents list
                boolean finished = false;
                Node n = current;
                while (!finished) {
                    parents.add(n);
                    n = n.getParent();
                    if (n.getParent() == null) {
                        finished = true;
                    }
                }
                return parents;
            }

            // check neighbours
            for (int i = 0; i < 3; i++) {

                if (i == 0) {
                    gCost = 10; // if current direction
                } else {
                    gCost = 14; // if change direction, costs more
                }

                boolean exists = false;
                Node n;
                if (i == 0) {
                    if (current.getDirection() == 'R') { // Continue Right
                        // CHECK IF BLOCKED
                        if (!isBlocked(current.getDirection(), current.getxAxis() + UNIT_SIZE, current.getyAxis())) {
                            n = new Node(current.getxAxis() + UNIT_SIZE, current.getyAxis(), gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    } else if (current.getDirection() == 'L') { // Continue Left
                        if (!isBlocked(current.getDirection(), current.getxAxis() - UNIT_SIZE, current.getyAxis())) {
                            n = new Node(current.getxAxis() - UNIT_SIZE, current.getyAxis(), gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    } else if (current.getDirection() == 'D') { // Continue Down
                        if (!isBlocked(current.getDirection(), current.getxAxis(), current.getyAxis() + UNIT_SIZE)) {
                            n = new Node(current.getxAxis(), current.getyAxis() + UNIT_SIZE, gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    } else { // Continue Up
                        if(!isBlocked(current.getDirection(), current.getxAxis(), current.getyAxis() - UNIT_SIZE)) {
                            n = new Node(current.getxAxis(), current.getyAxis() - UNIT_SIZE, gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    }
                } else if (i == 1) {
                    if (current.getDirection() == 'R') { // Turn Down
                        if(!isBlocked('D', current.getxAxis(), current.getyAxis() + UNIT_SIZE)) {
                            n = new Node(current.getxAxis(), current.getyAxis() + UNIT_SIZE, gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    } else if (current.getDirection() == 'L') { // Turn Up
                        if(!isBlocked('U', current.getxAxis(), current.getyAxis() - UNIT_SIZE)) {
                            n = new Node(current.getxAxis(), current.getyAxis() - UNIT_SIZE, gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    } else if (current.getDirection() == 'D') { // Turn Left
                        if(!isBlocked('L', current.getxAxis() - UNIT_SIZE, current.getyAxis())) {
                            n = new Node(current.getxAxis() - UNIT_SIZE, current.getyAxis(), gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    } else { // Turn Right
                        if(!isBlocked('R', current.getxAxis() + UNIT_SIZE, current.getyAxis())) {
                            n = new Node(current.getxAxis() + UNIT_SIZE, current.getyAxis(), gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    }
                } else {
                    if (current.getDirection() == 'R') { // Turn Up
                        if(!isBlocked('U', current.getxAxis(), current.getyAxis() - UNIT_SIZE)) {
                            n = new Node(current.getxAxis(), current.getyAxis() - UNIT_SIZE, gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    } else if (current.getDirection() == 'L') { // Turn Down
                        if(!isBlocked('D', current.getxAxis(), current.getyAxis() + UNIT_SIZE)) {
                            n = new Node(current.getxAxis(), current.getyAxis() + UNIT_SIZE, gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    } else if (current.getDirection() == 'D') { // Turn Right
                        if(!isBlocked('R', current.getxAxis() + UNIT_SIZE, current.getyAxis())) {
                            n = new Node(current.getxAxis() + UNIT_SIZE, current.getyAxis(), gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    } else { // Turn Left
                        if(!isBlocked('L', current.getxAxis() - UNIT_SIZE, current.getyAxis())) {
                            n = new Node(current.getxAxis() - UNIT_SIZE, current.getyAxis(), gCost, findHCost(current.getxAxis(), current.getyAxis()));
                            if (open.contains(n) || closed.contains(n)) {
                                exists = true;
                            }
                        } else {
                            continue;
                        }
                    }
                }

                if (exists && n.isClosed()) {
                    continue;
                }

                if (n.getFCost() <= current.getFCost() || !open.contains(n)) {
                    n.setParent(current);
                    if (!open.contains(n)) {
                        n.setgCost(n.getParent().getgCost() + gCost);
                        open.add(n);
                    }
                }
            }
        }
        return null;
    }

    public static int findHCost(int xAxis, int yAxis) {
        hCost = 0;
        xDistance = Math.abs((appleX - xAxis) / UNIT_SIZE);
        yDistance = Math.abs((appleY - yAxis) / UNIT_SIZE);
        if (yDistance != 0) {
            hCost = 4;
        }
        hCost += (xDistance * 10) + (yDistance * 10);
        return hCost;
    }

    public static void pathFinder() {
        int hCostA = 0;
        int hCostB = 0;
        int hCostC = 0;
        boolean blocked = false;
        int fCostA = 999999999;
        int fCostB = 999999999;
        int fCostC = 999999999;

        switch(direction) {
            case 'U':
                hCostA = 0;
                hCostB = 0;
                hCostC = 0;

                // If space to go up
                if (snake.get(0).y - UNIT_SIZE >= 0) {
                    // If no body parts blocking
                    for(int i = snake.size(); i>0; i--) {
                        if((snake.get(0).x == snake.get(i).x) && (snake.get(0).y - UNIT_SIZE == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going up
                        xDistance = Math.abs((appleX - snake.get(0).x) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - (snake.get(0).y - UNIT_SIZE)) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostA = 4;
                        }
                        hCostA+= (xDistance * 10) + (yDistance * 10);
                        fCostA = hCostA + 10;
                    }
                    blocked = false;
                }

                // If space to go left
                if(snake.get(0).x - UNIT_SIZE >= 0) {
                    // If no body parts blocking
                    for(int i = snake.size(); i>0; i--) {
                        if((snake.get(0).x - UNIT_SIZE == snake.get(i).x) && (snake.get(0).y == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going left
                        xDistance = Math.abs((appleX - (snake.get(0).x - UNIT_SIZE)) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - snake.get(0).y) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostB = 4;
                        }
                        hCostB+= (xDistance * 10) + (yDistance * 10);
                        fCostB = hCostB + 14;
                    }
                    blocked = false;
                }

                // If space to go right
                if(snake.get(0).x + UNIT_SIZE < SCREEN_WIDTH) {
                    // If no body parts blocking
                    for(int i = snake.size(); i>0; i--) {
                        if((snake.get(0).x + UNIT_SIZE == snake.get(i).x) && (snake.get(0).y == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going right
                        xDistance = Math.abs((appleX - (snake.get(0).x + UNIT_SIZE)) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - snake.get(0).y) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostC = 4;
                        }
                        hCostC+= (xDistance * 10) + (yDistance * 10);
                        fCostC = hCostC + 14;
                    }
                    blocked = false;
                }

                if(fCostA <= fCostB && fCostA <= fCostC) {
                    direction = 'U';
                } else if (fCostB < fCostA && fCostB <= fCostC) {
                    direction = 'L';
                } else if(fCostC < fCostB && fCostC < fCostA) {
                    direction = 'R';
                }
                fCostA = 999999999;
                fCostB = 999999999;
                fCostC = 999999999;

                break;

            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            case 'D':
                hCostA = 0;
                hCostB = 0;
                hCostC = 0;

                // If space to go down
                if (snake.get(0).y + UNIT_SIZE < SCREEN_HEIGHT) {
                    // If no body parts blocking
                    for (int i = snake.size(); i > 0; i--) {
                        if ((snake.get(0).x == snake.get(i).x) && (snake.get(0).y + UNIT_SIZE == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going down
                        xDistance = Math.abs((appleX - snake.get(0).x) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - (snake.get(0).y + UNIT_SIZE)) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostA = 4;
                        }
                        hCostA += (xDistance * 10) + (yDistance * 10);
                        fCostA = hCostA + 10;
                    }
                    blocked = false;
                }

                // If space to go left
                if (snake.get(0).x - UNIT_SIZE >= 0) {
                    // If no body parts blocking
                    for (int i = snake.size(); i > 0; i--) {
                        if ((snake.get(0).x - UNIT_SIZE == snake.get(i).x) && (snake.get(0).y == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going left
                        xDistance = Math.abs((appleX - (snake.get(0).x - UNIT_SIZE)) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - snake.get(0).y) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostB = 4;
                        }
                        hCostB += (xDistance * 10) + (yDistance * 10);
                        fCostB = hCostB + 14;
                    }
                    blocked = false;
                }

                // If space to go right
                if (snake.get(0).x + UNIT_SIZE < SCREEN_WIDTH) {
                    // If no body parts blocking
                    for (int i = snake.size(); i > 0; i--) {
                        if ((snake.get(0).x + UNIT_SIZE == snake.get(i).x) && (snake.get(0).y == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going right
                        xDistance = Math.abs((appleX - (snake.get(0).x + UNIT_SIZE)) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - snake.get(0).y) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostC = 4;
                        }
                        hCostC += (xDistance * 10) + (yDistance * 10);
                        fCostC = hCostC + 14;
                    }
                    blocked = false;
                }

                if (fCostA <= fCostB && fCostA <= fCostC) {
                    direction = 'D';
                } else if (fCostB < fCostA && fCostB <= fCostC) {
                    direction = 'L';
                } else if (fCostC < fCostB && fCostC < fCostA) {
                    direction = 'R';
                }
                fCostA = 999999999;
                fCostB = 999999999;
                fCostC = 999999999;

                break;

            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            case 'L':
                hCostA = 0;
                hCostB = 0;
                hCostC = 0;

                // If space to go left
                if (snake.get(0).x - UNIT_SIZE >= 0) {
                    // If no body parts blocking
                    for (int i = snake.size(); i > 0; i--) {
                        if ((snake.get(0).x - UNIT_SIZE == snake.get(i).x) && (snake.get(0).y == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going left
                        xDistance = Math.abs((appleX - (snake.get(0).x - UNIT_SIZE)) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - snake.get(0).y) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostA = 4;
                        }
                        hCostA += (xDistance * 10) + (yDistance * 10);
                        fCostA = hCostA + 10;
                    }
                    blocked = false;
                }

                // If space to go down
                if (snake.get(0).y + UNIT_SIZE < SCREEN_HEIGHT) {
                    // If no body parts blocking
                    for (int i = snake.size(); i > 0; i--) {
                        if ((snake.get(0).x == snake.get(i).x) && (snake.get(0).y + UNIT_SIZE == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going down
                        xDistance = Math.abs((appleX - snake.get(0).x) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - (snake.get(0).y + UNIT_SIZE)) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostB = 4;
                        }
                        hCostB += (xDistance * 10) + (yDistance * 10);
                        fCostB = hCostB + 14;
                    }
                    blocked = false;
                }

                // If space to go up
                if (snake.get(0).y - UNIT_SIZE >= 0) {
                    // If no body parts blocking
                    for (int i = snake.size(); i > 0; i--) {
                        if ((snake.get(0).x == snake.get(i).x) && (snake.get(0).y - UNIT_SIZE == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going up
                        xDistance = Math.abs((appleX - snake.get(0).x) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - (snake.get(0).y - UNIT_SIZE)) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostC = 4;
                        }
                        hCostC += (xDistance * 10) + (yDistance * 10);
                        fCostC = hCostC + 14;
                    }
                    blocked = false;
                }

                if (fCostA <= fCostB && fCostA <= fCostC) {
                    direction = 'L';
                } else if (fCostB < fCostA && fCostB <= fCostC) {
                    direction = 'D';
                } else if (fCostC < fCostB && fCostC < fCostA) {
                    direction = 'U';
                }

                fCostA = 999999999;
                fCostB = 999999999;
                fCostC = 999999999;

                break;
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            case 'R':
                hCostA = 0;
                hCostB = 0;
                hCostC = 0;

                // If space to go right
                if (snake.get(0).x + UNIT_SIZE < SCREEN_WIDTH) {
                    // If no body parts blocking
                    for (int i = snake.size(); i > 0; i--) {
                        if ((snake.get(0).x + UNIT_SIZE == snake.get(i).x) && (snake.get(0).y == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going right
                        xDistance = Math.abs((appleX - (snake.get(0).x + UNIT_SIZE)) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - snake.get(0).y) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostA = 4;
                        }
                        hCostA += (xDistance * 10) + (yDistance * 10);
                        fCostA = hCostA + 10;
                    }
                    blocked = false;
                }

                // If space to go down
                if (snake.get(0).y + UNIT_SIZE < SCREEN_HEIGHT) {
                    // If no body parts blocking
                    for (int i = snake.size(); i > 0; i--) {
                        if ((snake.get(0).x == snake.get(i).x) && (snake.get(0).y + UNIT_SIZE == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going down
                        xDistance = Math.abs((appleX - snake.get(0).x) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - (snake.get(0).y + UNIT_SIZE)) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostB = 4;
                        }
                        hCostB += (xDistance * 10) + (yDistance * 10);
                        fCostB = hCostB + 14;
                    }
                    blocked = false;
                }

                // If space to go up
                if (snake.get(0).y - UNIT_SIZE >= 0) {
                    // If no body parts blocking
                    for (int i = snake.size(); i > 0; i--) {
                        if ((snake.get(0).x == snake.get(i).x) && (snake.get(0).y - UNIT_SIZE == snake.get(i).y)) {
                            blocked = true;
                            break;
                        }
                    }
                    if (blocked != true) {
                        // Going up
                        xDistance = Math.abs((appleX - snake.get(0).x) / UNIT_SIZE);
                        yDistance = Math.abs((appleY - (snake.get(0).y - UNIT_SIZE)) / UNIT_SIZE);
                        if (yDistance != 0) {
                            hCostC = 4;
                        }
                        hCostC += (xDistance * 10) + (yDistance * 10);
                        fCostC = hCostC + 14;
                    }
                    blocked = false;
                }

                if (fCostA <= fCostB && fCostA <= fCostC) {
                    direction = 'R';
                } else if (fCostB < fCostA && fCostB <= fCostC) {
                    direction = 'D';
                } else if (fCostC < fCostB && fCostC < fCostA) {
                    direction = 'U';
                }

                fCostA = 999999999;
                fCostB = 999999999;
                fCostC = 999999999;

                break;
        }
    }
}
