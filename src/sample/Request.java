package sample;

import javafx.scene.layout.GridPane;

import java.util.List;

public class Request {
    String Type, stockType;
    Integer stockAmount, cartID, shelfID;

    public Request(String type, String stocktype,int stockamount, int shelfid, int cartid){
        Type = type;
        stockType = stocktype;
        stockAmount = stockamount;
        cartID = cartid;
        shelfID = shelfid;
    }
    public boolean processRequest(Cart cart, Shelf shelf, GridPane map, MyTimer timer){
        if (Type == "prijem"){

            //move cart to issue place
            cart.MoveCart(map, 24,10, timer);

            //load cart from issue place
            cart.loadCart(stockType,stockAmount);

            //move cart to shelf
            if ((shelf.ID <= 20) || (shelf.ID > 40 && shelf.ID <= 60) || (shelf.ID > 80 && shelf.ID <= 100) || (shelf.ID > 120 && shelf.ID <= 140) || (shelf.ID > 160 && shelf.ID <= 180)){
                List<Integer> cords = heatmap.getShelfStorage(shelf.ID,(shelf.ID % 10 == 0 && shelf.ID % 20 != 0));
                cart.MoveCart(map,cords.get(0),cords.get(1), timer);
            }

            //unload cart
            shelf.addShelf(stockType,stockAmount);
            cart.unloadCart();

            //move cart to parking
            cart.MoveCart(map,cart.ID,24,timer);
            return true;
        }
        else{
            //move cart to shelf
            if ((shelf.ID <= 20) || (shelf.ID > 40 && shelf.ID <= 60) || (shelf.ID > 80 && shelf.ID <= 100) || (shelf.ID > 120 && shelf.ID <= 140) || (shelf.ID > 160 && shelf.ID <= 180)){
                List<Integer> cords = heatmap.getShelfStorage(shelf.ID,(shelf.ID % 10 == 0 && shelf.ID % 20 != 0));
                cart.MoveCart(map,cords.get(0),cords.get(1), timer);
            }

            //load cart from shelf
            cart.loadCart(stockType,stockAmount);
            shelf.Goods.clear();

            //move cart to issue place
            cart.MoveCart(map, 24,10, timer);

            //unload cart
            cart.unloadCart();

            //park cart
            cart.MoveCart(map,cart.ID,24,timer);
            return true;
        }
    }
}
