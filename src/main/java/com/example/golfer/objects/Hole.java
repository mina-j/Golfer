package com.example.golfer.objects;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;

public class Hole extends Circle {
	private int val;
	
	public Hole ( double radius, Translate position ,int v) {
		super(radius);
		Color p=Color.YELLOW;
		this.val=v;
		switch(val){
			case 5:
				p=Color.YELLOW;
				break;
			case 10:
				p=Color.FORESTGREEN;
				break;
			case 20:
				p=Color.DARKORCHID;
				break;
		}
		Stop[] tacke = new Stop[] { new Stop(0, Color.BLACK),
				new Stop(1, p)};
		RadialGradient rg2 = new RadialGradient(0, 0, 0.5, 0.5, 0.5,true, CycleMethod.NO_CYCLE, tacke);
		super.setFill(rg2);
		super.getTransforms ( ).addAll ( position );
	}
	
	public boolean handleCollision ( Ball ball ) {
		Bounds ballBounds = ball.getBoundsInParent ( );
		
		double ballX      = ballBounds.getCenterX ( );
		double ballY      = ballBounds.getCenterY ( );
		double ballRadius = ball.getRadius ( );
		
		Bounds holeBounds = super.getBoundsInParent ( );
		
		double holeX      = holeBounds.getCenterX ( );
		double holeY      = holeBounds.getCenterY ( );
		double holeRadius = super.getRadius ( );
		
		double distanceX = holeX - ballX;
		double distanceY = holeY - ballY;
		
		double distanceSquared = distanceX * distanceX + distanceY * distanceY;

		Point2D speed=ball.getSpeed();
		if(Math.sqrt(Math.pow(speed.getX(), 2) + Math.pow(speed.getY(),2)) <= 200){
			return (boolean) (distanceSquared < ( holeRadius * holeRadius ));
		}else {
			return false;
		}
	}

	public int getVal() {
		return val;
	}
}
