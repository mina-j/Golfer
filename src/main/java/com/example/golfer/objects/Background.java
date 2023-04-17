package com.example.golfer.objects;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import com.example.golfer.Main;
public class Background {

    private int speedFactor;

    public Background ( int bg, Scene scene) {
        Main.setBG(bg);
        Image image;
        if(bg==0){
            image=new Image("grass.jpg");
        } else if(bg==1){
            image=new Image("cloud.jpg");
        }else {
            image=new Image("carpet.jpg");
        }
        ImagePattern bImage = new ImagePattern(image, 0, 0, scene.getWidth(), scene.getHeight(), false);
        scene.setFill(bImage);

    }




}
