package sample;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


import java.util.ArrayList;
import java.util.List;

public class PathFinder {

    static List<Rectangle> pathPart;

    public static class Cords{
        int pX, pY;
        public Cords(int x, int y){
            pX = x;
            pY = y;
        }
    }

    public static class Point{
        Cords cords;
        List<Cords> path;
        public Point(int x, int y, List<Cords> oldPath){
            cords = new Cords(x,y);
            path = new ArrayList<>();
            path.addAll(oldPath);
            path.add(cords);
        }
    }

    
    public static List<Cords> BSPathFinder(GridPane map, List<Point> points) {
        Point beginPoint = points.get(0);
        Point endPoint = points.get(1);

        List<Point> OPEN1 = new ArrayList<>();
        List<Point> OPEN2 = new ArrayList<>();
        List<Point> CLOSED1 = new ArrayList<>();
        List<Point> CLOSED2 =  new ArrayList<>();

        // Bidirectional Search
        OPEN1.add(beginPoint);
        OPEN2.add(endPoint);
        System.out.println("begin point: "+beginPoint.cords.pY+" "+beginPoint.cords.pY);
        System.out.println("end point: "+endPoint.cords.pX + " "+endPoint.cords.pY);
        while (true){
            if (OPEN1.isEmpty()){
                return null;
            }
            Point chosenPoint = OPEN1.get(0);
            CLOSED1.add(chosenPoint);

            // expanding from begin point
            List<Point> followers = expandAll(chosenPoint,map);
            System.out.println("chosenPoint: "+chosenPoint.cords.pX+" "+chosenPoint.cords.pY);
            for (Point follower : followers){
                System.out.println("follower: "+follower.cords.pX + " " + follower.cords.pY);
                int i = searchFront(OPEN2,follower);
                if (i != -1){
                    Point x = OPEN2.get(i);
                    List<Cords> finalPath = follower.path;
                    for (int j = x.path.size()-1; j > 0; j--){
                        finalPath.add(x.path.get(j));
                    }
                    finalPath.add(endPoint.cords);
                    return finalPath;
                }
                if (searchFront(CLOSED1,follower) == -1){
                    OPEN1.add(follower);
                }
                OPEN1.remove(chosenPoint);
            }

            chosenPoint = OPEN2.get(0);
            CLOSED2.add(chosenPoint);

            //expanding from closed point
            System.out.println("chosenPoint: "+chosenPoint.cords.pX+" "+chosenPoint.cords.pY);
            for (Point follower : expandAll(chosenPoint,map)){
                int i = searchFront(OPEN1,follower);
                if (i != -1){
                    List<Cords> finalPath = OPEN1.get(i).path;
                    for (int j = follower.path.size()-1; j > 0; j--){
                        finalPath.add(follower.path.get(j));
                    }
                    finalPath.add(endPoint.cords);
                    return finalPath;
                }
                System.out.println("follower: "+follower.cords.pX + " " + follower.cords.pY);
                if (searchFront(CLOSED2,follower) == -1){
                    OPEN2.add(follower);
                }
                OPEN2.remove(chosenPoint);
            }
        }
    }

    public static int searchFront(List<Point> front, Point x){
        int i = 0;
        for (Point a : front){
            if ((a.cords.pX == x.cords.pX) && (a.cords.pY == x.cords.pY)){
                return i;
            }
            i++;
        }
        return -1;
    }

    public static List<Point> expandAll(Point chosenPoint, GridPane map) {
        List<Point> followers = new ArrayList<>();
        int x = chosenPoint.cords.pX;
        int y = chosenPoint.cords.pY;
        if ((x+1 <= map.getColumnCount()) && expand(map, x + 1, chosenPoint.cords.pY)) {
            followers.add(new Point(x + 1, y, chosenPoint.path));
        }
        if ((x-1 >= 0) && expand(map, x - 1, chosenPoint.cords.pY)) {
            followers.add(new Point(x - 1, y, chosenPoint.path));
        }
        if ((y+1 <= map.getRowCount()) && expand(map, x, y + 1)) {
            followers.add(new Point(x, y + 1, chosenPoint.path));
        }
        if ((y-1 >= 0) && expand(map, x, y - 1)) {
            followers.add(new Point(x, y - 1, chosenPoint.path));
        }
        return followers;
    }

    public static boolean expand(GridPane map, int x, int y){

        // get children from map
        final ObservableList<Node>[] children = new ObservableList[]{map.getChildren()};
        map.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                children[0] = map.getChildren();
            }
        });

        // look for any obstacles
        for (Node node : children[0]){
            if (map.getRowIndex(node).equals(y) && map.getColumnIndex(node).equals(x) && !(node instanceof Region)){
                return false;
            }
        }
        return true;
    }
}
