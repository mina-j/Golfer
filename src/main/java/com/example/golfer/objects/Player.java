package com.example.golfer.objects;

import com.example.golfer.Main;
import com.example.golfer.Utilities;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Player extends Group {
	
	private double width;
	private double height;
	private Translate position;
	private Rotate rotate;
	
	public Player ( double width, double height, Translate position ) {
		this.width = width;
		this.height = height;
		this.position = position;
		Arc luk;

		Circle prozor;
		Path putanja;
		int shooter= Main.getSH();
		prozor = new Circle(width / 2, height, width, Color.DARKORCHID);
		if(shooter==0) {
			putanja = new Path(new MoveTo(0, 0), new LineTo(-width / 2, height), new LineTo(width * 3 / 2, height), new LineTo(width, 0), new ClosePath());
			putanja.setFill(Color.ALICEBLUE);
			super.getChildren ( ).addAll( putanja,prozor );
		}

		if(shooter==1){
			Circle p1=new Circle(width-5.5, height, width*2, Color.YELLOW); //3 je sirina lopte, 2 visina, 1 vrv pocetak ispisa kruznice
			Circle p2= new Circle(width*2/3-3, height*2/3+2, width*1.5, Color.YELLOW);
			Circle p3=new Circle(width/2-1.75, height/2-2, width, Color.YELLOW);

			super.getChildren ( ).addAll(p1, p2,p3,prozor);
		}

		if(shooter==2){
			putanja= new Path( new MoveTo(0,0), new LineTo(-width*3+2,height+width),new LineTo(width*3.8+2,height+width),new LineTo(width, 0), new ClosePath());
			putanja.setFill(Color.YELLOW);
			Rectangle r=new Rectangle(-width*4/3+4,-width/3+15,width*3,height+width/2);
			r.setFill(Color.DARKRED);
			Circle p3=new Circle(-width/2+11, width/2+4, width*1.5, Color.DARKRED);
			Circle kugla=new Circle(5, width*1.5, width/2, Color.ALICEBLUE);
			Circle kugla2=new Circle(5, width*4, width/2, Color.ALICEBLUE);
			Rectangle antena=new Rectangle(3.5,-width-10,4,20);
			Circle kugla3=new Circle(5.5, -width-12, width/3, Color.ALICEBLUE);
			antena.setFill(Color.DARKRED);
			super.getChildren ( ).addAll( putanja,p3,r,antena,kugla,kugla2,kugla3);
		}



		this.rotate = new Rotate ( );
		
		super.getTransforms ( ).addAll (
				position,
				new Translate ( width / 2, height ),
				rotate,
				new Translate ( -width / 2, -height )
		);
	}
	
	public void handleMouseMoved ( MouseEvent mouseEvent, double minAngleOffset, double maxAngleOffset ) {
		Bounds bounds = super.getBoundsInParent ( );
		
		double startX = bounds.getCenterX ( );
		double startY = bounds.getMaxY ( );
		
		double endX = mouseEvent.getX ( );
		double endY = mouseEvent.getY ( );
		
		Point2D direction     = new Point2D ( endX - startX, endY - startY ).normalize ( );
		Point2D startPosition = new Point2D ( 0, -1 );
		
		double angle = ( endX > startX ? 1 : -1 ) * direction.angle ( startPosition );
		
		this.rotate.setAngle ( Utilities.clamp ( angle, minAngleOffset, maxAngleOffset ) );
	}
	
	public Translate getBallPosition ( ) {
		double startX = this.position.getX ( ) + this.width / 2;
		double startY = this.position.getY ( ) + this.height;
		
		double x = startX + Math.sin ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		double y = startY - Math.cos ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		
		Translate result = new Translate ( x, y );
		
		return result;
	}
	
	public Point2D getSpeed ( ) {
		double startX = this.position.getX ( ) + this.width / 2;
		double startY = this.position.getY ( ) + this.height;
		
		double endX = startX + Math.sin ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		double endY = startY - Math.cos ( Math.toRadians ( this.rotate.getAngle ( ) ) ) * this.height;
		
		Point2D result = new Point2D ( endX - startX, endY - startY );
		
		return result.normalize ( );
	}
}
