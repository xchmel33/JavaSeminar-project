package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MyTimer {
    public MyTimer(){
        timerLabel = new Label();
        timeSeconds = new SimpleDoubleProperty();
        time = Duration.ZERO;
        speedFactor = new SimpleDoubleProperty(1);
    }
    Timeline timeline;
    Label timerLabel;
    DoubleProperty timeSeconds;
    Duration time;
    public DoubleProperty speedFactor;

    public StackPane start() {
        // Configure the Label
        // Bind the timerLabel text property to the timeSeconds property
        timerLabel.textProperty().bind(timeSeconds.asString());
        timerLabel.setTextFill(Color.RED);
        timerLabel.setStyle("-fx-font-size: 4em;");

        // Create speed multiplier HBox
        HBox speedBox = new HBox();
        Button resetSlider = new Button("reset speed");
        Label speedInfo = new Label("1");
        Label speedInfoX = new Label("x");
        Button addSpeed_01 = new Button("+0.1");
        addSpeed_01.setOnAction(e -> speedFactor.set(speedFactor.doubleValue()+0.1));
        Button addSpeed_05 = new Button("+0.5");
        addSpeed_05.setOnAction(e -> speedFactor.set(speedFactor.doubleValue()+0.5));
        Button addSpeed_10 = new Button("+1.0");
        addSpeed_10.setOnAction(e -> speedFactor.set(speedFactor.doubleValue()+1.0));
        Button subSpeed_01 = new Button("-0.1");
        subSpeed_01.setOnAction(e -> speedFactor.set(speedFactor.doubleValue()-0.1));
        Button subSpeed_05 = new Button("-0.5");
        subSpeed_05.setOnAction(e -> speedFactor.set(speedFactor.doubleValue()-0.5));
        Button subSpeed_10 = new Button("-1.0");
        subSpeed_10.setOnAction(e -> speedFactor.set(speedFactor.doubleValue()-1.0));
        speedFactor.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                speedFactor.set(round(speedFactor.doubleValue(),1));
                if (speedFactor.doubleValue() <= 0){
                    speedFactor.set(0.1);
                }
                speedInfo.textProperty().set(String.valueOf(speedFactor.doubleValue()));
            }
        });
        resetSlider.setOnAction(e -> speedFactor.set(1));
        speedBox.getChildren().addAll(subSpeed_01,subSpeed_05,subSpeed_10,addSpeed_01,addSpeed_05,addSpeed_10);

        // Create and configure the Button
        Button start = new Button("start");
        Button reset = new Button("reset");
        start.setOnAction(e -> {
            if (start.getText()=="start"){
                start.setText("stop");
            }
            else {
                start.setText("start");
            }
            if (timeline != null) {
                if (start.getText()=="stop"){
                    timeline.play();
                }
                else {
                    timeline.pause();
                }
                ;
            } else {
                timeline = new Timeline(
                        new KeyFrame(Duration.millis(100),
                                new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent t) {
                                        Duration duration = ((KeyFrame)t.getSource()).getTime();
                                        time = time.add(duration.multiply(speedFactor.doubleValue()));
                                        timeSeconds.set(time.toSeconds());
                                    }
                                })
                );
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
            }
        });
        reset.setOnAction(e -> {
            time = Duration.ZERO;
            timeSeconds.set(time.toSeconds());
        });

        // Setup the Stage and the Scene (the scene graph)
        StackPane root = new StackPane();

        // Create and configure VBox
        // gap between components is 20
        VBox vb = new VBox(20);
        HBox hb = new HBox(10);
        HBox sb = new HBox();
        sb.setAlignment(Pos.CENTER);
        sb.getChildren().addAll(speedInfo,speedInfoX);
        hb.getChildren().addAll(start,reset,resetSlider);
        // center the components within VBox
        vb.setAlignment(Pos.CENTER);
        // Move the VBox down a bit
        vb.setLayoutY(30);
        // Add the button and timerLabel to the VBox
        vb.getChildren().addAll(timerLabel,sb,speedBox,hb);
        // Add the VBox to the root component
        root.getChildren().add(vb);

        return root;
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}