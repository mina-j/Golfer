package com.example.golfer.objects;

import com.example.golfer.Main;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.Random;

public class Ship extends Group {
    private boolean isFlying=false;
    private boolean finished=false;
    Translate position;
   private long created; // vreme zivota spaceship-a, ili predje samo jednom i nestane?

    public Ship() {
        position= new Translate (-20,-20);
        Ellipse e1= new Ellipse(10,10,20,7);
        Ellipse e2 = new Ellipse(10,0,7,4);
        e2.setStyle(" -fx-stroke: black; -fx-stroke-width: 2;");
        e2.setFill(Color.ALICEBLUE);
        e1.setFill(Color.DARKGREY);


        this.getTransforms().add(position);
        this.getChildren().addAll(e1,e2);
        this.created= System.currentTimeMillis();

    }


    public void fly(){
        Random rand=new Random();
       int x= rand.nextInt((int)Main.WINDOW_WIDTH-20)+10;
       KeyValue kv1;
       KeyValue kv2;
       if(rand.nextBoolean()){
           kv1=new KeyValue(this.translateYProperty(), Main.WINDOW_HEIGHT);
           kv2=new KeyValue(this.translateYProperty(),-20) ;
       }else{
           kv2=new KeyValue(this.translateYProperty(), Main.WINDOW_HEIGHT);
           kv1=new KeyValue(this.translateYProperty(),-20) ;
       }
        this.position.setX(x);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, kv1),
                new KeyFrame(Duration.seconds(5), kv2)
        );
        timeline.play();
    }

    public long getCreated(){
        return created;
    }

    public boolean isFlying(){
        return this.isFlying;
    }
}
