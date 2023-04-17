package com.example.golfer.objects;

import com.example.golfer.Main;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.*;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.Random;

public class Token extends Group{
    private int val;
    private long created=0;

    public Token( ) {
        Color p=Color.GREEN;
        Random rand=new Random();
        val=0;
        val = rand.nextInt(3);

        int ranX = rand.nextInt((int)Main.WINDOW_WIDTH-90)+40; // random value from 0 to width
        int ranY = rand.nextInt((int)Main.WINDOW_HEIGHT-250)+50;
        Translate position=new Translate(ranX,ranY);
        switch(val){
            case 0:
                p=Color.GREEN;
                break;
            case 1:
                p=Color.HOTPINK;
                break;
            case 2:
                p=Color.STEELBLUE;
                break;
        }
        Stop[] tacke = new Stop[] { new Stop(0, Color.WHITE),
                new Stop(1, p)};
        RadialGradient rg2 = new RadialGradient(0, 0, 0.5, 0.5, 0.5,true, CycleMethod.NO_CYCLE, tacke);

        Path path = new Path();
        /*MoveTo moveTo = new MoveTo(68, 90);
        LineTo line1 = new LineTo(115, 100);
        LineTo line2 = new LineTo(65,50);
        LineTo line3 = new LineTo(98,110);
        LineTo line4 = new LineTo(103, 40);
        LineTo line5 = new LineTo(68, 90);*/
        MoveTo moveTo = new MoveTo(26, 30);
        LineTo line1 = new LineTo(61, 30);
        LineTo line2 = new LineTo(30,10);
        LineTo line3 = new LineTo(43,42);
        LineTo line4 = new LineTo(56, 10);
        LineTo line5 = new LineTo(26, 30);

        //Adding all the elements to the path
        path.getElements().add(moveTo);
        path.getElements().addAll(line1, line2, line3, line4, line5);
        path.setFill(rg2);
        path.setStroke(Color.TRANSPARENT);
        path.getTransforms().add(position);
        this.getChildren().add(path);

        RotateTransition rt = new RotateTransition(Duration.millis(1000), this);
        rt.setByAngle(360);
        rt.setCycleCount(Timeline.INDEFINITE);

        rt.play();
        created=System.currentTimeMillis();

    }
    public long getCreated(){
        return this.created;
    }

    public int getVal() {
        return val;
    }
}

