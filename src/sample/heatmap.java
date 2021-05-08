package sample;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class heatmap {
    public static Scene init(Stage main, Scene mainScene) {

        String cwd = System.getProperty("user.dir");
        cwd = cwd.replace("\\","/");

        HBox root = new HBox();
        root.setSpacing(20);

        HBox buttons = new HBox();
        Button play = new Button("play");
        Button pause = new Button("pause");
        Button menu = new Button("menu");
        menu.setOnAction(e -> main.setScene(mainScene));
        buttons.getChildren().addAll(play, pause, menu);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(20);

        HBox buttons2 = new HBox();
        Text heatmapOpt = new Text("Heatmap:     ");
        Button on = new Button("on");
        Button off = new Button("off");
        buttons2.getChildren().addAll(heatmapOpt,on,off);

        VBox tools = new VBox();
        GridPane maxAmountInput = new GridPane();
        tools.getChildren().addAll(buttons,buttons2,maxAmountInput);
        tools.setSpacing(15);

        GridPane map = new GridPane();
        map.setAlignment(Pos.CENTER_RIGHT);

        VBox vInfo = new VBox();

        JSONParser inputParser = new JSONParser();
        Object obj = new Object();
        try {
            obj = inputParser.parse(new FileReader("data/in.json"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray jsonShelves = (JSONArray) jsonObject.get("shelves");
        JSONArray jsonRequests = (JSONArray) jsonObject.get("carts");
        List<Integer> shelfIDs = new ArrayList<Integer>();
        List<Integer> shelfIDx = new ArrayList<Integer>();
        ArrayList<Shelf> shelves = new ArrayList<Shelf>();
        shelfIDs.add(0);
        shelfIDx.add(0);

        int nSpacers = 0;
        Integer shelfStorageIndex = 0;
        boolean vSpacer = false;

        // set capacity of shelf
        AtomicInteger maxAmount = new AtomicInteger(100);
        play.setOnAction(e -> maxAmount.set(50));
        for (Object shelf : jsonShelves){
            JSONObject jsonShelf = (JSONObject) shelf;
            Long shelfLongID = (Long) jsonShelf.get("shelf");
            int shelfID = shelfLongID.intValue();
            String shelfType = (String) jsonShelf.get("type");
            Long shelfLongAmount = (Long) jsonShelf.get("amount");
            int shelfAmount = shelfLongAmount.intValue();

            List<Integer> cords;
            if (!vSpacer){
                 cords = getShelfStorage(shelfID, vSpacer);
            }
            else {
                shelfID++;
                cords = getShelfStorage(shelfID, vSpacer);
                shelfID--;
                if (shelfID % 20 == 0){
                    vSpacer = false;
                }
            }
            Integer y = cords.get(0);
            Integer x = cords.get(1);
            if (cords.get(3) == 1){
                vSpacer = true;
            }

            if (shelfIDs.contains(shelfID)){
                Shelf appendS = shelves.get(shelfID-1);
                appendS.addShelf(shelfType,shelfAmount);
                shelves.set(shelfID-1,appendS);
                map.getChildren().removeAll(appendS.shelfRect);
                if (shelfID % 20 == 0){
                    map.add(appendS.shelfRect, x, y+1);
                }
                else{
                    map.add(appendS.shelfRect, x, y);
                }
            }
            else {

                Shelf s = new Shelf(shelfType, shelfAmount, shelfID);
                shelfIDs.add(shelfID);
                shelves.add(s);
                map.add(s.drawShelf(shelfID, vInfo), x, y);
            }
            nSpacers = cords.get(2);
        }

        AtomicInteger in = new AtomicInteger();
        in.set(100);
        AtomicBoolean isOn = new AtomicBoolean();
        isOn.set(false);
        AtomicBoolean er = new AtomicBoolean();
        isOn.set(false);

        Text maxAmountInputT = new Text("Maximum amount of stock in shelf:");
        TextField maxAmountInputTF = new TextField();
        Button maxAmountInputB = new Button("set");
        maxAmountInputB.setStyle("-fx-width: 100px;");
        maxAmountInputB.setOnAction(e ->{
            try {
                er.set(false);
                in.set(Integer.parseInt(maxAmountInputTF.getText()));
            }
            catch (Exception exception){
                er.set(true);
            }
            if (!er.get()){
                if (isOn.get()){
                    for (Shelf sh : shelves) {
                        sh.shelfHeat(in.get());
                    }
                }
            }
            else {
                maxAmountInputTF.setText("Error - integer only!");
            }
        });
        maxAmountInput.add(maxAmountInputT,0,0);
        maxAmountInput.add(maxAmountInputTF,0,1);
        maxAmountInput.add(maxAmountInputB,1,1);
        maxAmountInput.setMaxSize(100,100);
        maxAmountInput.setAlignment(Pos.TOP_LEFT);

        on.setStyle("-fx-color:white");
        off.setStyle("-fx-color:red");
        on.setOnAction(e -> {
            isOn.set(true);
            on.setStyle("-fx-color:green");
            off.setStyle("-fx-color:white");
            if (!er.get()){
                for (Shelf sh : shelves) {
                    sh.shelfHeat(in.get());
                }
            }
            else {
                maxAmountInputTF.setText("Error - integer only!");
            }
        });
        off.setOnAction(e ->{
            isOn.set(false);
            on.setStyle("-fx-color:white");
            off.setStyle("-fx-color:red");
            for (Shelf sh : shelves) {
                sh.shelfRect.setFill(Color.TRANSPARENT);
            }
        });


        int z = 0;
        for (int i = 0; i <= nSpacers; i++){
            Region cSpacer = new Region();
            cSpacer.setPrefSize(30,600);
            map.add(cSpacer,1+z,0,2,20);
            z = z + 4;
        }
        Region rSpacer = new Region();
        rSpacer.setPrefSize(180,30);
        map.add(rSpacer,0,10,16,1);
        int mapMaxW = map.getRowCount()*15;
        map.setPrefSize(mapMaxW,810);

        StackPane dock = new StackPane();
        Rectangle dockR =  new Rectangle(120,30);
        Text dockT = new Text("VYDAJ");
        dockR.setStroke(Color.BLACK);
        dockR.setFill(Color.TRANSPARENT);
        dock.getChildren().addAll(dockT,dockR);
        Region dockSpacer = new Region();
        dockSpacer.setPrefSize(180,60);
        map.add(dockSpacer,0,22,16,2);
        map.add(dock,4,25,8,1);

        HBox Zoom = new HBox();
        Text ZoomT = new Text("Zoom: ");
        Slider slider = new Slider(0.5,2,1);
        Button resetZoom = new Button("reset");
        resetZoom.setOnAction(e -> {
            slider.setValue(1);
        });
        ScrollPane mapScroll = new ScrollPane();
        mapScroll.pannableProperty().set(true);
        mapScroll.setContent(map);
        mapScroll.setPrefSize(mapMaxW,810);
        ZoomingPane zoomingPane = new ZoomingPane(mapScroll);
        zoomingPane.zoomFactorProperty().bind(slider.valueProperty());
        zoomingPane.setMaxSize(mapMaxW+5,820);
        zoomingPane.setPrefSize(mapMaxW+5,820);
        zoomingPane.setStyle("-fx-border-color: black;");
        Zoom.getChildren().addAll(ZoomT,slider,resetZoom);

        tools.getChildren().add(Zoom);
        tools.setPrefWidth(250);
        root.getChildren().addAll(tools,zoomingPane,vInfo);
        Scene scene = new Scene(root, 960, 960);
        return scene;
    }

    public static List<Integer> getShelfStorage(int id, boolean spc){
        List<Integer> cords = new ArrayList<Integer>();
        int newID = 0;
        int spacer = 0;
        int s = 0;
        if (spc){
            s = 1;
        }
        if (id % 10 == 0 && id % 20 != 0) {
            newID = id+1;
            spacer = 1;
        }
        else{
            newID = id;
        }
        if (id <= 20+s){
            cords.add(newID);
            cords.add(0);
            cords.add(0);
            cords.add(spacer);
        }
        else if (id <= 40+s){
            cords.add(newID-20);
            cords.add(3);
            cords.add(1);
            cords.add(spacer);
        }
        else if (id <= 60+s){
            cords.add(newID-40);
            cords.add(4);
            cords.add(1);
            cords.add(spacer);
        }
        else if (id <= 80+s){
            cords.add(newID-60);
            cords.add(7);
            cords.add(2);
            cords.add(spacer);
        }
        else if (id <= 100+s){
            cords.add(newID-80);
            cords.add(8);
            cords.add(2);
            cords.add(spacer);
        }
        else if (id <= 120+s){
            cords.add(newID-100);
            cords.add(11);
            cords.add(3);
            cords.add(spacer);
        }
        else if (id <= 140+s){
            cords.add(newID-120);
            cords.add(12);
            cords.add(3);
            cords.add(spacer);
        }
        else if (id <= 160+s){
            cords.add(newID-140);
            cords.add(15);
            cords.add(4);
            cords.add(spacer);
        }
        else if (id <= 180+s){
            cords.add(newID-160);
            cords.add(16);
            cords.add(4);
            cords.add(spacer);
        }
        else if (id <= 200+s){
            cords.add(newID-180);
            cords.add(19);
            cords.add(5);
            cords.add(spacer);
        }
        return cords;

    }
}