package sample;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Shelf {
    public Shelf(String type, int amount, int id){
        shelfRect = new Rectangle(15,30);
        Goods = new ArrayList<GoodType>();
        ID = id;
        GoodType gd = new GoodType(type,amount);
        Goods.add(gd);
        getShelfInfo();

    }
    int ID;
    int finalTotalAmount;
    String finalShelfInfo;
    List<GoodType> Goods;
    Rectangle shelfRect;


    public void addShelf(String type, int amount){
        GoodType good = new GoodType(type,amount);
        Goods.add(good);
        getShelfInfo();
    }

    public void shelfHeat(int maxAmount){
        float oc = (float) finalTotalAmount/maxAmount;
        if (oc > 1){
            shelfRect.setFill(Color.PURPLE);
        }
        else if (oc > 0.8){
            shelfRect.setFill(Color.RED);
        }
        else if (oc > 0.5){
            shelfRect.setFill(Color.ORANGE);
        }
        else if (oc > 0.2){
            shelfRect.setFill(Color.YELLOW);
        }
        else {
            shelfRect.setFill(Color.TRANSPARENT);
        }
    }

    public void getShelfInfo(){
        String shelfInfo = "ID: " + Integer.toString(ID) + "\n" + "Goods:\n";
        int totalAmount = 0;
        for (GoodType shelfGood : Goods) {
            shelfInfo =  shelfInfo+Integer.toString(shelfGood.Amount)+"\t"+shelfGood.Good+"\n";
            totalAmount = totalAmount + shelfGood.Amount;
        }
        finalTotalAmount = totalAmount;
        finalShelfInfo = shelfInfo+"\n\n\nTotal: "+finalTotalAmount;
    }

    public Rectangle drawShelf(int ID, TextArea r){
        // rectangle + hover for showing goods
        shelfRect.setStroke(Color.BLACK);
        shelfRect.setFill(Color.TRANSPARENT);
        shelfRect.hoverProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean show) -> {
            if (show){
                r.textProperty().set(finalShelfInfo);
            }
            else {
                r.textProperty().set("");
            }
        });
        return shelfRect;
    }
}
