package com.example.golfer.objects;

import com.example.golfer.Main;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Translate;

public class Teleporter extends Group {
    Circle c;


    public Teleporter(Translate position) {
        Line line1 = new Line(-20, 0, 20, 0);
        Line line2 = new Line(0, -20, 0, 20);
        Line line3 = new Line(0, -20, 0, 20);
        Line line4 = new Line(-20, 0, 20, 0);
        line1.setRotate(45);
        line2.setRotate(45);
        line3.setRotate(90);
        line4.setRotate(90);
        line1.setStyle(" -fx-stroke: black; -fx-stroke-width: 5;");
        line2.setStyle(" -fx-stroke: black; -fx-stroke-width: 5;");
        line3.setStyle(" -fx-stroke: black; -fx-stroke-width: 5;");
        line4.setStyle(" -fx-stroke: black; -fx-stroke-width: 5;");
        c=new Circle(0,0, 10, Color.WHITE);
        c.setStyle("    -fx-stroke: black; -fx-stroke-width: 1;");

        this.getTransforms().add(position);
        this.getChildren().addAll(line1,line2,line3,line4,c);

    }

    public void handle(Ball b,double x,double y,Teleporter pair){
        if(Main.isTeleporterUsed()){
            if(!b.getBoundsInParent().intersects(pair.getBoundsInParent()) && !b.getBoundsInParent().intersects(this.getBoundsInParent())){
                Main.setTeleporterUsed(false);
            }
            c.setFill(Color.WHITE);
           // c.setFill(Color.BLACK);
            return;
        }
        if(b.getBoundsInParent().intersects(this.getBoundsInParent())) {
            Main.setTeleporterUsed(true);
            b.setPosition(x, y);
           // c.setFill(Color.WHITE);
            c.setFill(Color.BLACK);
        }


    }
}
