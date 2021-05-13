package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
    Timeline timeline, moveTimeline;
    boolean timelineFinished, showPath;

    HBox infoBox, moveBox;
    Label infoID, infoStatus, infoCords, mX,mY;
    Button moveCart;
    TextField moveParamX, moveParamY;
    Integer movesDone;
    List<Rectangle> pathPart;
    String sContent;

    public Cart(GridPane map, VBox tools, Integer id, Integer x, Integer y, MyTimer timer, TextArea t) {
        content = new ArrayList<>();
        sContent = "";
        ID = id;
        X = x;
        Y = y;
        movesDone = 0;
        showPath = false;
        speed = new SimpleDoubleProperty(1);
        cartIcon = new Button(ID.toString());
        cartIcon.setMaxSize(15, 30);
        cartIcon.setPrefSize(15,30);
        cartIcon.setStyle("-fx-color:green;-fx-font-size:8;");
        map.add(cartIcon, X, Y);
        status = "created";
        timelineFinished = true;

        // info
        infoBox = new HBox();
        infoID = new Label("Cart "+ID.toString());
        infoStatus = new Label(status);
        infoStatus.setTextFill(Color.BLUE);
        infoCords = new Label("X = "+X.toString() + "\tY = " + Y.toString());
        infoBox.getChildren().addAll(infoID,infoStatus,infoCords);
        infoBox.setSpacing(10);

        // move cart box
        moveBox = new HBox();
        mX = new Label("X:");
        mY = new Label("Y:");
        moveParamX = new TextField();
        moveParamY = new TextField();
        moveCart = new Button("MOVE");
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
                Integer finalMoveX = moveX;
                Integer finalMoveY = moveY;

                MoveCart(map,finalMoveX,finalMoveY,timer);
            }
        });

        // add moving option to tools
        moveParamX.setMaxSize(50,10);
        moveParamY.setMaxSize(50,10);
        moveBox.getChildren().addAll(mX,moveParamX,mY,moveParamY,moveCart);
        moveBox.setSpacing(5);
        cartIcon.setOnAction(e -> {
            if (cartIcon.getText().equals(ID.toString())){
                cartIcon.setText("X");
                cartIcon.setStyle("-fx-color:black;-fx-font-size:8;");
                showPath = true;
                tools.getChildren().addAll(infoBox,moveBox);
                getCartContent();
                t.textProperty().set(sContent);
            }
            else{
                cartIcon.setText(ID.toString());
                cartIcon.setStyle("-fx-color:green;-fx-font-size:8;");
                showPath = false;
                if(pathPart != null) {
                    for (Rectangle RP : pathPart) {
                        map.getChildren().remove(RP);
                    }
                }
                tools.getChildren().removeAll(infoBox,moveBox);
                t.textProperty().set("");
            }
        });
    }

    public void MoveCart(GridPane map, Integer x, Integer y, MyTimer timer){
        // find path
        List<PathFinder.Point> p = new ArrayList<>();
        p.add(new PathFinder.Point(X,Y,new ArrayList<>()));
        p.add(new PathFinder.Point(x,y,new ArrayList<>()));
        List<PathFinder.Cords> path = PathFinder.BSPathFinder(map,p);

        if (path == null){
            System.out.println("Path not found!");
            return;
        }

        // show path
        pathPart = new ArrayList<>();
        if (showPath){
            for (PathFinder.Cords c : path){
                Rectangle pPart = new Rectangle(15,30);
                pPart.setFill(Color.ORANGE);
                map.add(pPart,c.pX,c.pY);
                pathPart.add(pPart);
            }
        }

        // timeline actions
        movesDone = 0;
        EventHandler<ActionEvent> timelineActions = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                status = "moving";
                X = path.get(movesDone).pX;
                Y = path.get(movesDone).pY;
                map.getChildren().remove(cartIcon);
                map.add(cartIcon, X, Y);
                DisplayInfo();
                if (showPath){
                    map.getChildren().remove(pathPart.get(movesDone));
                }
                movesDone++;
            }
        };

        // set movement speed based on global timer
        speed.bind(timer.speedFactor);
        final Duration[] dSpeed = new Duration[1];
        dSpeed[0] = Duration.seconds(1/speed.doubleValue());
        KeyFrame k = new KeyFrame(dSpeed[0], timelineActions);
        timeline = new Timeline(k);
        Integer finalMoves = path.size();
        timeline.setCycleCount(finalMoves);


        // dynamically change speed of carts
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

        // move cart
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

    public void loadCart(String type, int amount){
        content.add(new GoodType(type,amount));
    }

    public void unloadCart(){

    }

    public void getCartContent(){
        sContent = "ID: " + Integer.toString(ID) + "\n" + "Goods:\n";
        int totalAmount = 0;
        for (GoodType cartGood : content) {
            sContent =  sContent+Integer.toString(cartGood.Amount)+"\t"+cartGood.Good+"\n";
            totalAmount = totalAmount + cartGood.Amount;
        }
        sContent = sContent+"\n\n\nTotal: "+totalAmount;
    }
}
