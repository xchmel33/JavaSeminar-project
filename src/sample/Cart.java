package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Cart {
    Integer X,Y,ID;
    List<GoodType> content;
    Button cartIcon;
    String status;
    public DoubleProperty speed;
    Timeline timeline;

    HBox infoBox, moveBox;
    Label infoID, infoStatus, infoCords, mX,mY;
    Button moveCart;
    TextField moveParamX, moveParamY;
    Integer movesDone;

    public Cart(GridPane map, VBox tools, Integer id, Integer x, Integer y, MyTimer timer) {
        content = new ArrayList<>();
        ID = id;
        X = x;
        Y = y;
        movesDone = 0;
        speed = new SimpleDoubleProperty(1);
        cartIcon = new Button(ID.toString());
        cartIcon.setMaxSize(15, 30);
        cartIcon.setPrefSize(15,30);
        cartIcon.setStyle("-fx-color:green;-fx-font-size:8;");
        map.add(cartIcon, X, Y);
        status = "created";

        infoBox = new HBox();
        infoID = new Label("Cart "+ID.toString());
        infoStatus = new Label(status);
        infoStatus.setTextFill(Color.BLUE);
        infoCords = new Label("X = "+X.toString() + "\tY = " + Y.toString());
        infoBox.getChildren().addAll(infoID,infoStatus,infoCords);
        infoBox.setSpacing(10);
        moveBox = new HBox();
        mX = new Label("X:");
        mY = new Label("Y:");
        moveParamX = new TextField();
        moveParamY = new TextField();
        moveCart = new Button("MOVE CART");
        moveCart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Integer moveX = X;
                Integer moveY = Y;
                try {
                    moveX = Integer.valueOf(moveParamX.getText());
                }
                catch (Exception exception){
                    moveParamX.setText("invalid");
                }
                //if (moveX < 0) moveParamX.setText("invalid");
                try {
                    moveY = Integer.valueOf(moveParamY.getText());

                }
                catch (Exception exception){
                    moveParamY.setText("invalid");
                }
                //if (moveY < 0) moveParamY.setText("invalid");
                MoveCart(map,moveX,moveY,timer);
            }
        });
        moveParamX.setMaxSize(50,10);
        moveParamY.setMaxSize(50,10);
        moveBox.getChildren().addAll(mX,moveParamX,mY,moveParamY,moveCart);
        moveBox.setSpacing(5);
        cartIcon.setOnAction(e -> {
            if (cartIcon.getText().equals(ID.toString())){
                cartIcon.setText("X");
                cartIcon.setStyle("-fx-color:black;-fx-font-size:8;");
                tools.getChildren().addAll(infoBox,moveBox);
            }
            else{
                cartIcon.setText(ID.toString());
                cartIcon.setStyle("-fx-color:green;-fx-font-size:8;");
                tools.getChildren().removeAll(infoBox,moveBox);
            }
        });
    }

    public void MoveCart(GridPane map, Integer x, Integer y, MyTimer timer){

        if (Y != y && X != x) {

            // moving on both lines is forbidden
            System.out.println(Y+"!="+y+"&&"+X+"!="+x);
            return;
        }

        // cycles for timeline
        Integer Moves = 0;
        Boolean yDirection = false;
        Integer move = 0;

        if (x != X) {

            // move on line x
            if (X > x) {
                Moves = X - x;
                move = -1;
            } else {
                Moves = x - X;
                move = 1;
            }
            yDirection = false;
        }
        if (Y != y) {

            // move on line y
            if (Y > y) {
                Moves = Y - y;
                move = -1;
            } else {
                Moves = y - Y;
                move = 1;
            }
            yDirection = true;
        }
        Boolean finalYDirection = yDirection;
        Integer finalMove = move;

        EventHandler<ActionEvent> timelineActions = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                status = "moving";
                if (finalYDirection) {
                    Y = Y + finalMove;
                } else {
                    X = X + finalMove;
                }
                map.getChildren().remove(cartIcon);
                map.add(cartIcon, X, Y);
                movesDone++;
                DisplayInfo();
            }
        };
        speed.bind(timer.speedFactor);
        final Duration[] dSpeed = new Duration[1];
        dSpeed[0] = Duration.seconds(1/speed.doubleValue());
        KeyFrame k = new KeyFrame(dSpeed[0], timelineActions);
        timeline = new Timeline(k);
        timeline.setCycleCount(Moves);
        Integer finalMoves = Moves;
        speed.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                timeline.stop();
                Integer c = finalMoves - movesDone;
                dSpeed[0] = Duration.seconds(1/speed.doubleValue());
                timeline.getKeyFrames().remove(0);
                KeyFrame newK = new KeyFrame(dSpeed[0],timelineActions);
                timeline.getKeyFrames().add(newK);
                timeline.setCycleCount(c);
                timeline.play();
            }
        });
        timeline.play();
        timeline.setOnFinished(e -> {
            status = "finished moving";
            DisplayInfo();
        });
    }

    public void DisplayInfo(){
        infoStatus.textProperty().set(status);
        infoCords.textProperty().set("X = "+X.toString() + "\tY = " + Y.toString());
        if (status == "created"){
            infoStatus.setTextFill(Color.BLUE);
        }
        else if (status == "finished moving"){
            infoStatus.setTextFill(Color.GREEN);
        }
        else if (status == "moving"){
            infoStatus.setTextFill(Color.RED);
        }
    }

    public void loadCart(){

    }

    public void unloadCart(){

    }
}
