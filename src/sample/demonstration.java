package sample;


import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;

public class demonstration {

    public static void display(){

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Funkcie aplikácie");
        window.setWidth(500);
        window.setHeight(200);

        Label label = new Label();
        label.setText("Stlacenim tlacitka start sa nacita a zobrazi mapa skladu zo vstupneho súboru,\n " +
                "kde je mozne posielat poziadavky na prevoz tovaru zadanym vozikom, interaktivne\n" +
                "zablokovat ulicky, kliknutim na vozik zobrazit jeho trasu, kliknutim na regal\n " +
                "zobrazit jeho trasu, pomocou tlacitok urychliť/spomalit pohyb vozikov.\n" +
                "V hlavnom menu je este mozne zadat pocet vozikov ktore budu prepravovat\n" +
                "tovar a zaplnenost skladu");

        Button closeButton = new Button("Zavrieť");
        closeButton.setOnAction(e -> window.close());

        StackPane layout = new StackPane();
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(closeButton, Pos.BOTTOM_CENTER);
        layout.setAlignment(label, Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("file:data/MainTheme.css");
        window.setScene(scene);
        window.showAndWait();

    }
}
