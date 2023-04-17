package com.example.golfer.objects;

import com.example.golfer.Main;
import com.example.golfer.Utilities;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.ArrayList;

public class Ball extends Circle {
	private Translate position;
	private Point2D speed;
	private Scale scale=new Scale();
	private int  yes=200000000;
	
	public Ball ( double radius, Translate position, Point2D speed ) {
		super ( radius, Color.RED );
		
		this.position = position;
		this.speed = speed;
		scale.setX(1);
		scale.setY(1);
		scale.setPivotX(getTranslateX());
		scale.setPivotY(getTranslateY());
		
		super.getTransforms ( ).addAll ( this.position,scale );
	}
	private double sc = -0.01;
	public boolean shrink(){
		if(scale.getX()>0.1) {
			scale.setX(scale.getX() + sc);
			scale.setY(scale.getY() + sc);
			//if(scale.getX()<0.2 || scale.getX()>1){sc=sc*-1; return false;}
			return true;
		}else{return false;}

	}
	
	public boolean update (double ds, double left, double right, double top, double bottom, double dampFactor, double minBallSpeed, ArrayList<Wall> walls) {
		boolean result = false;
		
		double newX= this.position.getX ( ) + this.speed.getX ( ) * ds;
		double newY = this.position.getY ( ) + this.speed.getY ( ) * ds;
		
		double radius = super.getRadius ( );
		
		double minX = left + radius;
		double maxX = right - radius;
		double minY = top + radius;
		double maxY = bottom - radius;

		for(Wall w: walls){

			if( w.getBoundsInParent().getMaxY()>ds*this.speed.getY()+this.getBoundsInParent().getMinY() && w.getBoundsInParent().getMinY()<this.speed.getY()*ds+this.getBoundsInParent().getMaxY() ){
				if(w.getBoundsInParent().getMinX()<this.getBoundsInParent().getMaxX() && w.getBoundsInParent().getMaxX()>this.getBoundsInParent().getMinX()){
					this.speed = new Point2D ( this.speed.getX ( ), -this.speed.getY ( ) );
					newY = this.position.getY();
				}
			}
			if( w.getBoundsInParent().getMaxX()>this.speed.getX()*ds+this.getBoundsInParent().getMinX() && w.getBoundsInParent().getMinX()<ds*this.speed.getX()+ this.getBoundsInParent().getMaxX()){
				if(w.getBoundsInParent().getMinY()<this.getBoundsInParent().getMaxY() && w.getBoundsInParent().getMaxY()>this.getBoundsInParent().getMinY()){
					this.speed = new Point2D ( -this.speed.getX ( ), this.speed.getY ( ) );
					newX = this.position.getX();
				}
			}

		}
		
		this.position.setX ( Utilities.clamp ( newX, minX, maxX ) );
		this.position.setY ( Utilities.clamp ( newY, minY, maxY ) );
	
		if ( newX < minX || newX > maxX ) {
			this.speed = new Point2D ( -this.speed.getX ( ), this.speed.getY ( ) );
		}
		
		if ( newY < minY || newY > maxY ) {
			this.speed = new Point2D ( this.speed.getX ( ), -this.speed.getY ( ) );
		}
		
		this.speed = this.speed.multiply ( dampFactor );
		
		double ballSpeed = this.speed.magnitude ( );
		
		if ( ballSpeed < minBallSpeed ) {
			result = true;
		}
		
		return result;
	}

	public Point2D getSpeed() {
		return speed;
	}

	public  void resetSpeed() {
		speed =new Point2D ( 0, 0 );
	}
	public void setPosition(double x, double y){
		this.position.setX(x);
		this.position.setY(y);
	}
}
