package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


import java.util.ArrayList;
import java.util.List;

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

    public Cart(GridPane map, VBox tools, Integer id, Integer x, Integer y, MyTimer timer) {
        content = new ArrayList<>();
        ID = id;
        X = x;
        Y = y;
        speed = new SimpleDoubleProperty(1);
        cartIcon = new Button("C");
        cartIcon.setMaxSize(15, 30);
        cartIcon.setPrefSize(15,30);
        cartIcon.setStyle("-fx-color:green;-fx-font-size:7;");
        cartIcon.setOnAction(e -> {
            if (cartIcon.getText() == "C"){
                cartIcon.setText("");
                cartIcon.setStyle("-fx-color:black");
            }
            else{
                cartIcon.setText("C");
                cartIcon.setStyle("-fx-color:green");
            }
        });
        map.add(cartIcon, X, Y);
        status = "created";

        infoBox = new HBox();
        infoID = new Label("Cart"+ID.toString());
        infoStatus = new Label(status);
        infoCords = new Label(X.toString()+" | "+Y.toString());
        infoBox.getChildren().addAll(infoID,infoStatus,infoCords);
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


                if (Y != moveY && X != moveX) {

                    // moving on both lines is forbidden
                    System.out.println(Y+"!="+moveY+"&&"+X+"!="+moveX);
                    return;
                }

                // cycles for timeline
                Integer Moves = 0;
                Boolean yDirection = false;
                Integer move = 0;

                if (moveX != X) {

                    // move on line x
                    if (X > moveX) {
                        Moves = X - moveX;
                        move = -1;
                    } else {
                        Moves = moveX - X;
                        move = 1;
                    }
                    yDirection = false;
                }
                if (Y != moveY) {

                    // move on line y
                    if (Y > moveY) {
                        Moves = Y - moveY;
                        move = -1;
                    } else {
                        Moves = moveY - Y;
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
                        System.out.println("move cart, speed: "+speed.doubleValue());
                    }
                };

                // base timeline
                final Duration[] dSpeed = new Duration[1];
                dSpeed[0] = Duration.seconds(1/speed.doubleValue());
                KeyFrame k = new KeyFrame(dSpeed[0], timelineActions);
                timeline = new Timeline(k);
                timeline.setCycleCount(Moves);
                timeline.play();
                timeline.setOnFinished(e -> {
                    status = "finshed moving";
                });

                // timeline dynamic interruptions
                Integer finalMoves = Moves;
                speed.bind(timer.speedFactor);
                speed.addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        timeline.stop();
                        Integer c = finalMoves - timeline.getCycleCount();
                        dSpeed[0] = Duration.seconds(1/speed.doubleValue());
                        timeline.getKeyFrames().remove(0);
                        KeyFrame newK = new KeyFrame(dSpeed[0],timelineActions);
                        timeline.getKeyFrames().add(newK);
                        timeline.setCycleCount(c);
                        timeline.play();
                    }
                });

            }
        });
        moveBox.getChildren().addAll(mX,moveParamX,mY,moveParamY,moveCart);
        tools.getChildren().addAll(infoBox,moveBox);
    }

    public void loadCart(){

    }

    public void unloadCart(){

    }
}
