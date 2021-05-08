package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.*;

public class Main extends Application {

    Button demoButton, startButton, settingsButton, closeButton;

    @Override
    public void start(Stage primaryStage) throws Exception{

        String cwd = System.getProperty("user.dir");
        cwd = cwd.replace("\\","/");

        demoButton = new Button("Nápoveda");
        startButton = new Button("Štart");
        settingsButton = new Button("Nastavenia");
        closeButton = new Button("Koniec");
        Text title = new Text("IJA PROJEKT XCECHV03\n");

        demoButton.setOnAction(e -> demonstration.display());
        closeButton.setOnAction(e -> primaryStage.close());
        settingsButton.setOnAction(e -> settings.display());
        title.getStyleClass().add("title");

        GridPane root = new GridPane();
        root.setPadding(new Insets(10,10,10,10));
        root.setHgap(20);
        root.setVgap(5);
        root.add(title,0,0,5,1);
        root.add(startButton,3,1);
        root.add(demoButton, 3,2);
        root.add(settingsButton, 3,3);
        root.add(closeButton, 3,4);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 300, 275);
        startButton.setOnAction(e -> primaryStage.setScene(heatmap.init(primaryStage,scene)));
        scene.getStylesheets().add("file:data/MainTheme.css");
        primaryStage.setTitle("IJA Projekt xcechv03");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
