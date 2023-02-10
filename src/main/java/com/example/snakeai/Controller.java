package com.example.snakeai;

import com.example.snakeai.SingleMode;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXButton;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static com.example.snakeai.AI.numDirections;


public class Controller{
    @FXML
    private JFXButton aiButton;

    @FXML
    private JFXButton multiButton;

    @FXML
    private JFXButton scoreButton;

    @FXML
    private JFXButton singleButton;
    @FXML
    private AnchorPane anchorPane;

    int SCREEN_WIDTH;
    int SCREEN_HEIGHT;
    static final int UNIT_SIZE = 25;
    int GAME_UNIT;
    static  final int DELAY = 75;
    int x[];
    int y[];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = true;
    public Pane frame;

    @FXML
    void listBoard(MouseEvent event) {

    }

    @FXML
    void playAIMode(MouseEvent event) {
        Stage stage = (Stage) aiButton.getScene().getWindow();
        AI.creatPanel(stage, 600, 600);
    }

    @FXML
    void playMultiMode(MouseEvent event) {
        Stage stage = (Stage) multiButton.getScene().getWindow();
        MultiplayersMode.creatPanel(stage);
    }

    @FXML
    void playSingleMode(MouseEvent event) {
        Stage stage = (Stage) singleButton.getScene().getWindow();
        SingleMode.creatPanel(stage);
    }


}
