package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
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

        // root pane
        HBox root = new HBox();
        root.setSpacing(20);

        // heatmap optional buttons
        HBox buttons = new HBox();
        Text heatmapOpt = new Text("Heatmap:     ");
        Button on = new Button("on");
        Button off = new Button("off");
        buttons.getChildren().addAll(heatmapOpt,on,off);

        // tools
        VBox tools = new VBox();
        VBox maxAmountInput = new VBox();
        tools.getChildren().addAll(buttons,maxAmountInput);
        tools.setSpacing(20);

        // warehouse map
        GridPane map = new GridPane();
        map.setAlignment(Pos.CENTER_RIGHT);

        // shelf and cart information
        VBox info = new VBox(20);
        TextArea vInfo = new TextArea();
        vInfo.setMaxSize(250,300);
        vInfo.setPrefSize(250,300);
        TextArea cInfo = new TextArea();
        cInfo.setMaxSize(250,300);
        cInfo.setPrefSize(250,300);
        TextArea reqInfo = new TextArea();
        reqInfo.setMaxSize(250,200);
        reqInfo.setPrefSize(250,200);
        info.getChildren().addAll(vInfo,cInfo,reqInfo);

        // parse json input
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

        // shelf lists
        List<Integer> shelfIDs = new ArrayList<Integer>();
        List<Integer> shelfIDx = new ArrayList<Integer>();
        ArrayList<Shelf> shelves = new ArrayList<Shelf>();
        shelfIDs.add(0);
        shelfIDx.add(0);

        // space betweem columns
        int nSpacers = 0;
        boolean vSpacer = false;

        // draw shelves
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

        // space between columns
        int z = 0;
        for (int i = 0; i <= nSpacers; i++){
            Region cSpacer = new Region();
            Region ccSpacer = new Region();
            cSpacer.setPrefSize(15,600);
            ccSpacer.setPrefSize(15,600);
            map.add(cSpacer,1+z,0,1,20);
            map.add(ccSpacer,2+z,0,1,20);
            z = z + 4;
        }

        // space between rows
        Region rSpacer = new Region();
        rSpacer.setPrefSize(180,30);
        map.add(rSpacer,0,10,16,1);
        int mapMaxW = map.getRowCount()*15;
        map.setPrefSize(mapMaxW,810);


        // max stock in shelf variables
        AtomicInteger in = new AtomicInteger();
        in.set(100);
        AtomicBoolean isOn = new AtomicBoolean();
        isOn.set(false);
        AtomicBoolean er = new AtomicBoolean();
        isOn.set(false);

        // max stock in shelf
        Text maxAmountInputT = new Text("Maximum amount of stock in shelf:");
        TextField maxAmountInputTF = new TextField();
        maxAmountInputTF.setMaxWidth(100);
        Button maxAmountInputB = new Button("set");
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
        HBox maxInH = new HBox();
        maxInH.getChildren().addAll(maxAmountInputTF,maxAmountInputB);
        maxAmountInput.getChildren().addAll(maxAmountInputT,maxInH);
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

        // timer from custom class
        MyTimer timer = new MyTimer();
        tools.getChildren().add(timer.start());
        tools.setPrefWidth(250);



        // carts
        JSONArray jsonCarts = (JSONArray) jsonObject.get("carts");
        AtomicInteger cartID = new AtomicInteger(0);
        List<Cart> carts = new ArrayList<>();
        for (Object cart: jsonCarts){
            JSONObject jsonCart = (JSONObject) cart;
            Long jsonCartID = (Long) jsonCart.get("cart");
            JSONArray jsonCartContent = (JSONArray) jsonCart.get("order");
            cartID.set(jsonCartID.intValue());
            Cart xCart = new Cart(map, tools, cartID.get(), cartID.get()-1, 24, timer,cInfo);
            for (Object Content: jsonCartContent){
                JSONObject jsonContent = (JSONObject) Content;
                String contentType = (String) jsonContent.get("type");
                Long jsonContentAmount = (Long) jsonContent.get("amount");
                int contentAmount = jsonContentAmount.intValue();
                xCart.loadCart(contentType,contentAmount);
            }
            carts.add(xCart);
        }

        // carts adding
        Button addCart =  new Button("ADD CART");
        addCart.setOnAction(e ->{
            Cart cart = new Cart(map,tools, cartID.incrementAndGet(),cartID.get()-1,24,timer,cInfo);
            carts.add(cart);
        });

        // requests box
        VBox reqBox = new VBox(10);
        HBox reqBox1 = new HBox(5);
        HBox reqBox2 = new HBox(5);
        HBox reqBox3 = new HBox(5);
        Label reqL = new Label("Requests");
        Label reqCartL = new Label("Cart ID:");
        Label reqShelfL = new Label("Shelf ID:");
        Label stockTypeL = new Label("Stock type:");
        Label stockAmountL = new Label("Stock Amount:");
        TextField reqCart = new TextField();
        TextField reqShelf = new TextField();
        TextField stockType = new TextField();
        TextField stockAmount = new TextField();
        reqCart.setMaxWidth(30);
        reqShelf.setMaxWidth(30);
        stockType.setMaxWidth(50);
        stockAmount.setMaxWidth(30);
        Button prijem = new Button("PRIJEM");
        Button vydaj = new Button("VYDAJ");
        reqBox1.getChildren().addAll(reqCartL,reqCart,reqShelfL,reqShelf);
        reqBox2.getChildren().addAll(stockTypeL,stockType,stockAmountL,stockAmount);
        reqBox3.getChildren().addAll(prijem,vydaj);
        reqBox.getChildren().addAll(reqL,reqBox1,reqBox2,reqBox3);
        tools.getChildren().addAll(reqBox,addCart);


        // request
        AtomicInteger reqID = new AtomicInteger();
        prijem.setOnAction(e ->{
            reqID.getAndIncrement();
            reqInfo.appendText("REQUEST "+reqID.get()+":\n PRIJEM "+"CART "+reqCart.getText()+" SHELF "+reqShelf.getText()+" STOCK "+stockType.getText()+" "+stockType.getText()+"\n");
        });
        vydaj.setOnAction(e ->{
            reqID.getAndIncrement();
            reqInfo.appendText("REQUEST "+reqID.get()+":\n VYDAJ "+"CART "+reqCart.getText()+" SHELF "+reqShelf.getText()+" STOCK "+stockType.getText()+" "+stockType.getText()+"\n");
        });

        // issue place & parking
        StackPane dock = new StackPane();
        StackPane park = new StackPane();
        Rectangle dockR = new Rectangle(120,30);
        Rectangle parkR = new Rectangle(120,30);
        Text dockT = new Text("VYDAJ");
        Text parking = new Text("PARKING");
        dockR.setStroke(Color.BLACK);
        dockR.setFill(Color.TRANSPARENT);
        parkR.setStroke(Color.BLACK);
        parkR.setFill(Color.TRANSPARENT);
        dock.getChildren().addAll(dockT,dockR);
        park.getChildren().addAll(parking,parkR);
        Region dockSpacer = new Region();
        dockSpacer.setPrefSize(180,60);
        map.add(dockSpacer,0,22,16,2);
        map.add(dock,8,25,8,1);
        map.add(park,0,25,8,1);

        // warehouse map zoom
        VBox mapZoom =  new VBox(20);
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
        Zoom.setAlignment(Pos.CENTER);
        mapZoom.getChildren().addAll(zoomingPane,Zoom);

        // button for switching stage back to menu
        Button menu = new Button("menu");
        menu.setOnAction(e -> main.setScene(mainScene));
        menu.setAlignment(Pos.TOP_RIGHT);

        // add all elements to root
        root.getChildren().addAll(tools,mapZoom,info,menu);

        // return scene to be displayed
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
