package com.example.golfer.objects;

import com.example.golfer.Main;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Translate;

public class Wall extends Rectangle {
    private double speedFactor;
    public Wall(Translate coordinate, double w, double h, int img){
        super(w, h);
        this.getTransforms().add(coordinate);
        Image image;
        if(img<3) {
            if(img==1){
                image = new Image("wire.png");
            }else{
                image = new Image("fence.jpg");
            }
            //Main.WINDOW_WIDTH
            ImagePattern pImage = new ImagePattern(image, 0, 0, 23, 70, false);
            this.setFill(pImage);
        }else{
            this.setFill(Color.BLACK);
        }
    }

    public Wall(Translate coordinate, double w, double h, Paint c,double factor){
        super(w, h);
        speedFactor=factor;
        this.getTransforms().add(coordinate);
        int bg=Main.getBG();
        Paint color;
        if(factor==0){
            this.setFill(c);
            return;
        }

        if(bg==0){
            if(factor<1){
                color=Color.BROWN;
            }else{
                color=Color.ALICEBLUE;
            }
        } else if(bg==1){
            if(factor<1){
                color=Color.DARKGREY;
            }else{
                color=Color.YELLOW;
            }
        }else {
            if(factor<1){
                color=Color.BLACK;
            }else{
                color=Color.KHAKI;
            }
        }
        this.setFill(color);
    }

    public double getFactor(){
            return this.speedFactor;
    }

}
