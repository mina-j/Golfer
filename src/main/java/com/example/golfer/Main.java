package com.example.golfer;

import com.example.golfer.objects.*;
import com.example.golfer.objects.Background;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main extends Application implements EventHandler<MouseEvent> {
	public static final double WINDOW_WIDTH  = 600; //600
	public static final double WINDOW_HEIGHT = 800; //800
	
	private static final double PLAYER_WIDTH            = 10;
	private static final double PLAYER_HEIGHT           = 80;
	private static final double PLAYER_MAX_ANGLE_OFFSET = 60;
	private static final double PLAYER_MIN_ANGLE_OFFSET = -60;
	
	private static final double MS_IN_S            = 1e3;
	private static final double NS_IN_S            = 1e9;
	private static final double MAXIMUM_HOLD_IN_S  = 3;
	private static final double MAXIMUM_BALL_SPEED = 1500;
	private static final double BALL_RADIUS        = Main.PLAYER_WIDTH / 2;
	private static final double BALL_DAMP_FACTOR   = 0.995;
	private static final double MIN_BALL_SPEED     = 5;
	
	private static final double HOLE_RADIUS = 3 * BALL_RADIUS;
	private static final int MAX_LIVES = 5;
	private static final Integer STARTTIME = 100;
	private static final long TOKEN_RENEW_TIME= 15000;
	private static final long TOKEN_LIFE_TIME = 5000;
	private static final long SHIP_RENEW_TIME = 10000;
	
	private Group root;
	private Timer timer;
	private Stage stage;
	private Scene scene;
	private Player player;
	private Ball ball;
	private Teleporter teleptr1,teleptr2;
	private Ship space_ship;
	private long time;
	private static int bg; //background ID
	private static int sh; //shooter ID
	private ScaleTransition st; // for speed statistic
	private ScaleTransition holeAnim;
	private Timeline task; //za time bar
	private Text loptetext=new Text(),poenitext=new Text(); //ispis poena i zivota
	private static int ballsLeft=MAX_LIVES;
	private static ArrayList<Hole> holes = new ArrayList<>(); //spisak rupa
	private static ArrayList<Wall> walls= new ArrayList<>();//za prepreke o koje se odbija
	private static ArrayList<Wall> speedBumps= new ArrayList<>(); //za nitro i usporavanje
	private ArrayList<Circle> ballsLeftList=new ArrayList<>(); //prikaz preostalih zivota
	private static CopyOnWriteArrayList<Token> tokens= new CopyOnWriteArrayList<>();
	private static boolean isInHole=false;
	private static boolean tUsed=false; //da li je teleporter iskoriscen
	private int score=0;
	IntegerProperty timeSeconds =new SimpleIntegerProperty((STARTTIME)*100 ); //za time bar
	Label timerLabel=new Label(); //za time bar
	private long startTime=0, proteklo=0;
	private ProgressBar bar;
	private BorderPane border = new BorderPane();
	private int max_shoot_speed=1500;


	private void addHoles ( Translate hole0Position, Translate hole1Position,Translate hole2Position,Translate hole3Position) {

		Hole hole0 = new Hole ( Main.HOLE_RADIUS, hole0Position,20 );
		this.root.getChildren ( ).addAll ( hole0 );

		if(hole1Position!=null) {
			Hole hole1 = new Hole(Main.HOLE_RADIUS, hole1Position,5);
			this.root.getChildren().addAll(hole1);
			this.holes.add(hole1);
		}

		if(hole2Position!=null) {
			Hole hole2 = new Hole(Main.HOLE_RADIUS, hole2Position,10);
			this.root.getChildren().addAll(hole2);
			this.holes.add(hole2);
		}

		if(hole3Position!=null) {
			Hole hole3 = new Hole(Main.HOLE_RADIUS, hole3Position,10);
			this.root.getChildren().addAll(hole3);
			this.holes.add(hole3);
		}
		
		this.holes.add(hole0);

	}

	public void addWalls(){

		Wall leftwall = new Wall(new Translate(0,0), 18,WINDOW_HEIGHT, bg );
		Wall rightwall = new Wall(new Translate(WINDOW_WIDTH-18,0), 18, WINDOW_HEIGHT,bg);
		Wall topwall = new Wall(new Translate(18,0), WINDOW_WIDTH-18, 18,bg);

		walls.add(leftwall);
		walls.add(rightwall);
		walls.add(topwall);
		this.root.getChildren().addAll(leftwall,rightwall,topwall);

	}
	public void addObstacle(Translate t, double w, double h){

		Wall bar = new Wall(t, w,h, Color.DARKBLUE, 0 );

		walls.add(bar);
		this.root.getChildren().add(bar);

	}
	public void addSpeedBumps(Translate t1, Translate t2, Translate t3, Translate t4){
		Wall bump1=new Wall(t1,20,20,null,0.85);
		Wall bump2=new Wall(t2,20,20,null,0.85);
		Wall nitro1=new Wall(t3,20,20,null,1.11);
		Wall nitro2=new Wall(t4,20,20,null,1.21);
		speedBumps.add(bump1);
		speedBumps.add(bump2);
		speedBumps.add(nitro1);
		speedBumps.add(nitro2);
		this.root.getChildren().addAll(bump1,bump2,nitro1,nitro2);

	}
	
	@Override
	public void start ( Stage stage ) throws IOException {
		this.root  = new Group ( );
		this.stage=stage;
		scene = new Scene ( this.root, Main.WINDOW_WIDTH, WINDOW_HEIGHT, Color.BLACK );
		//treba da imam negde pravljenje player-a player=new Player();


		scene.addEventHandler (
				MouseEvent.MOUSE_MOVED,
				mouseEvent -> this.player.handleMouseMoved (
						mouseEvent,
						Main.PLAYER_MIN_ANGLE_OFFSET,
						Main.PLAYER_MAX_ANGLE_OFFSET
				)
		);
		
		scene.addEventHandler ( MouseEvent.ANY, this );
		scene.addEventHandler(
				KeyEvent.KEY_RELEASED,
				keyEvent -> {
					if(ball != null && keyEvent.getCode() == KeyCode.SPACE) {
						this.root.getChildren ( ).remove ( this.ball );
						this.ball = null;

					}

		});

		holeAnim = new ScaleTransition(Duration.millis(2000));
		holeAnim.setOnFinished(evt -> {
			Main.setIsInHole(false);
			this.root.getChildren ( ).remove ( this );

		});
		
		timer = new Timer (
				deltaNanoseconds -> {

					if(startTime!=0 && System.currentTimeMillis()-startTime>TOKEN_RENEW_TIME){
						Token token=new Token();
						boolean overlay=true;
							while(overlay){
							token= new Token();
								boolean o1=false;
								if(this.ball!=null){ o1=ball.getBoundsInParent().intersects(token.getBoundsInParent());}
								boolean ot=(teleptr1.getBoundsInParent().intersects(token.getBoundsInParent())) || (teleptr2.getBoundsInParent().intersects(token.getBoundsInParent()));

								Token finalToken = token;
							boolean o2=holes.stream().anyMatch (hole ->
							{
								boolean goal=hole.getBoundsInParent().intersects(finalToken.getBoundsInParent());
								return goal;
							});
							boolean o3=speedBumps.stream().anyMatch ( bump ->
							{
								boolean goal=bump.getBoundsInParent().intersects(finalToken.getBoundsInParent());
								return goal;
							});

							boolean o4=walls.stream().anyMatch ( bump ->
							{
								boolean goal=bump.getBoundsInParent().intersects(finalToken.getBoundsInParent());
								return goal;
							});
							overlay=o1 || o2 || o3 || o4;
						}
						tokens.add(token);
						this.root.getChildren().add(token);
						startTime=System.currentTimeMillis();
					}
					if(!tokens.isEmpty()){
						for(Token t: tokens){
							if(System.currentTimeMillis()-t.getCreated()>TOKEN_LIFE_TIME) {
								this.root.getChildren().remove(t);
								tokens.remove(t);
							}

						}
					}
					//za spaceship
					/*if(space_ship!=null && space_ship.getFinished()){
						this.root.getChildren().remove(space_ship);
						this.space_ship= new Ship();
					}*/
					if(proteklo!=0 && System.currentTimeMillis()-proteklo>SHIP_RENEW_TIME){
						space_ship=new Ship();
						//ships.add(ship);
						this.root.getChildren().add(space_ship);
						proteklo=System.currentTimeMillis();
						space_ship.fly();
					}
					if(space_ship!=null){
							if(System.currentTimeMillis()-space_ship.getCreated()>5000) {
								this.root.getChildren().remove(space_ship);
								space_ship=null;
							}
					}
					/*if( this.space_ship!=null && space_ship.getCreated()+ SHIP_RENEW_TIME >= System.currentTimeMillis() && !this.space_ship.isFlying()){
						this.root.getChildren().add(space_ship);
						space_ship.fly();
					}*/


					double deltaSeconds = ( double ) deltaNanoseconds / Main.NS_IN_S;
					if(this.ball!= null && isInHole){
						if(!ball.shrink()){
							this.root.getChildren ( ).remove ( this.ball );
							this.ball = null;
							isInHole=false;
						}
					}
					if ( this.ball != null && !isInHole ) {
						isInHole =holes.stream().anyMatch ( hole ->
						 {
						 boolean goal=hole.handleCollision ( this.ball );
						 if(goal){
							 score+= hole.getVal();
							 poenitext.setText("Broj poena: "+score);
							 ball.resetSpeed();
						 }
						 return goal;
						 });

						double factor=0;
						for(Wall b:speedBumps){
							if (b.getBoundsInParent().intersects(ball.getBoundsInParent())) {
								factor=b.getFactor();
								break;
							}
						}

						//sudar teleporter
							teleptr1.handle(ball,teleptr2.getBoundsInParent().getCenterX(),teleptr2.getBoundsInParent().getCenterY(),teleptr2);
							teleptr2.handle(ball,teleptr1.getBoundsInParent().getCenterX(),teleptr1.getBoundsInParent().getCenterY(),teleptr1);

						boolean stopped = this.ball.update (
									deltaSeconds,
									0,
									Main.WINDOW_WIDTH,
									0,
									Main.WINDOW_HEIGHT,
									factor==0 ? Main.BALL_DAMP_FACTOR : factor,
									Main.MIN_BALL_SPEED,
									this.walls
							);
						for(Token b:tokens){
							if (b.getBoundsInParent().intersects(ball.getBoundsInParent())) {
								gift(b.getVal());
								this.root.getChildren().remove(b);
								tokens.remove(b);
								break;
							}
						}

						if (stopped && !isInHole) {
							this.root.getChildren ( ).remove ( this.ball );
							this.ball = null;
						}
						if( space_ship!=null && space_ship.getBoundsInParent().intersects(this.ball.getBoundsInParent())){
							this.root.getChildren().remove(this.ball);
							this.ball=null;
						}


					}
					if((ballsLeft==0 && this.ball==null) || timeSeconds.get()==0){
						gameOver();

					}

				}
		);
		timer.start ( );

		GridPane gridL = new GridPane();

		gridL.setAlignment(Pos.CENTER);
		//gridL.setPrefHeight(20);
		gridL.setHgap(20);
		gridL.setVgap(50);
		border.setCenter(gridL);

		HBox hboxlevel = new HBox();
		Label lbLvl= new Label("Choose a surface! :)");
		lbLvl.setFont(new Font("Times New Roman", 40));
		lbLvl.setAlignment(Pos.CENTER);
		hboxlevel.setPrefHeight(40);
		hboxlevel.setPadding(new Insets(20, 20, 20, 130));
		hboxlevel.setStyle("-fx-background-color: #33AFFF;");
		border.setTop(hboxlevel);
		hboxlevel.getChildren().addAll( lbLvl);

		Button buttonCurrent = new Button("Grass");
		buttonCurrent.setPrefSize(300, 90);
		buttonCurrent.setFont(new Font("Arial Black",25));
		buttonCurrent.setStyle(" -fx-text-fill: #FFFFFF; ");
		//pozadina dugmeta
		Image image = new Image("grass.jpg");
		BackgroundImage bImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, new BackgroundSize(buttonCurrent.getWidth(), buttonCurrent.getHeight(), true, true, true, false));
		javafx.scene.layout.Background backGround = new javafx.scene.layout.Background(bImage);
		buttonCurrent.setBackground(backGround);
		//akcija dugmeta
		buttonCurrent.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				chooseShooterSet(0);
			}
		});

		Button buttonProjected = new Button("Cloud");
		buttonProjected.setPrefSize(300, 90);
		buttonProjected.setFont(new Font("Arial Black",25));
		buttonProjected.setStyle(" -fx-text-fill: #FFFFFF; "); //-fx-background-color: #ff0000;
		//pozadina dugmeta
		Image image1 = new Image("cloud.jpg");
		BackgroundImage bImage1 = new BackgroundImage(image1, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, new BackgroundSize(buttonCurrent.getWidth(), buttonCurrent.getHeight(), true, true, true, false));
		javafx.scene.layout.Background backGround1 = new javafx.scene.layout.Background(bImage1);
		buttonProjected.setBackground(backGround1);
		//akcija dugmeta
		buttonProjected.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				chooseShooterSet(1);
			}
		});

		Button buttonHouse = new Button("House");
		buttonHouse.setPrefSize(300, 90);
		buttonHouse.setFont(new Font("Arial Black",25));
		buttonHouse.setStyle(" -fx-text-fill: #FFFFFF; "); //-fx-background-color: #ff0000;
		//pozadina dugmeta
		Image image2 = new Image("carpet.jpg");
		BackgroundImage bImage2 = new BackgroundImage(image2, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, new BackgroundSize(buttonCurrent.getWidth(), buttonCurrent.getHeight(), true, true, true, false));
		javafx.scene.layout.Background backGround2 = new javafx.scene.layout.Background(bImage2);
		buttonHouse.setBackground(backGround2);
		//akcija dugmeta
		buttonHouse.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				chooseShooterSet(2);
			}
		});

		gridL.add(buttonCurrent,1,0);
		gridL.add(buttonProjected,1,1);
		gridL.add(buttonHouse,1,2);

		border.setCenter(gridL);

		Scene newScene=new Scene(border,Main.WINDOW_WIDTH,Main.WINDOW_HEIGHT);
		scene.setCursor ( Cursor.NONE );
		
		stage.setTitle ( "Golfer" );
		stage.setResizable ( false );
		stage.setScene ( newScene );
		stage.show ( );
	}
	
	public static void main ( String[] args ) {
		launch ( );
	}
	
	@Override public void handle ( MouseEvent mouseEvent ) {
		if (ball==null && ballsLeft>0 && timeSeconds.get()!=0 && mouseEvent.getEventType ( ).equals ( MouseEvent.MOUSE_PRESSED ) && mouseEvent.isPrimaryButtonDown ( )  ) {
			this.time = System.currentTimeMillis ( );
			st.playFromStart();
			if(ballsLeft==5){
				task.playFromStart();
				startTime=System.currentTimeMillis();
				proteklo=System.currentTimeMillis();
			}
		} else if ( mouseEvent.getEventType ( ).equals ( MouseEvent.MOUSE_RELEASED ) ) {
			if ( this.time != - 1 && this.ball == null ) {
				st.playFromStart();
				st.jumpTo(Duration.ZERO);
				st.stop();
				double value        = ( System.currentTimeMillis ( ) - this.time ) / Main.MS_IN_S;
				double deltaSeconds = Utilities.clamp ( value, 0, Main.MAXIMUM_HOLD_IN_S );
				
				double ballSpeedFactor = deltaSeconds / Main.MAXIMUM_HOLD_IN_S * max_shoot_speed;
				
				Translate ballPosition = this.player.getBallPosition ( );
				Point2D   ballSpeed    = this.player.getSpeed ( ).multiply ( ballSpeedFactor );
				this.root.getChildren().remove(ballsLeftList.get(ballsLeft-1));
				ballsLeftList.remove(ballsLeft-1);
				ballsLeft--;
				this.ball = new Ball ( Main.BALL_RADIUS, ballPosition, ballSpeed );
				this.root.getChildren ( ).addAll ( this.ball );
			}
			this.time = -1;
		}
	}

	public void chooseShooterSet(int bg){
		new Background(bg,scene);
		border.setTop(null);
		border.setCenter(null);
		GridPane gridS = new GridPane();
		gridS.setAlignment(Pos.CENTER);
		//gridS.setPrefHeight(20);
		gridS.setHgap(20);
		gridS.setVgap(50);
		border.setCenter(gridS);

		HBox hboxshooter = new HBox();
		Label lblSh= new Label("Choose a shooter! :)");
		lblSh.setFont(new Font("Times New Roman", 40));
		lblSh.setAlignment(Pos.CENTER);
		hboxshooter.setPrefHeight(40);
		hboxshooter.setPadding(new Insets(20, 20, 20, 130));
		hboxshooter.setStyle("-fx-background-color: #33AFFF;");
		border.setTop(hboxshooter);
		hboxshooter.getChildren().addAll( lblSh);
		//DUGMAD
		Button btnSh1 = new Button("MIN SPEED");
		btnSh1.setPrefSize(300, 90);
		btnSh1.setFont(new Font("Arial Black",25));
		btnSh1.setStyle(" -fx-text-fill: #FFFFFF; -fx-background-color: #FF9882;");
		//akcija dugmeta
		btnSh1.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				//max_shoot_speed= 2000;
				stage.setScene ( scene );
				stage.show ( );
				playGolf(0);
			}
		});

		Button btnSh2 = new Button("MEDIUM SPEED");
		btnSh2.setPrefSize(300, 90);
		btnSh2.setFont(new Font("Arial Black",25));
		btnSh2.setStyle(" -fx-text-fill: #FFFFFF; -fx-background-color: #FF5733;"); //-fx-background-color: #ff0000;
		//akcija dugmeta
		btnSh2.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				max_shoot_speed= 2200;
				stage.setScene ( scene );
				stage.show ( );
				playGolf(1);

			}
		});

		Button btnSh3 = new Button("MAX SPEED");
		btnSh3.setPrefSize(300, 90);
		btnSh3.setFont(new Font("Arial Black",25));
		btnSh3.setStyle(" -fx-text-fill: #FFFFFF; -fx-background-color: #E32800;"); //-fx-background-color: #ff0000;
		//akcija dugmeta
		btnSh3.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent t){
				max_shoot_speed= 3000;
				stage.setScene ( scene );
				stage.show ( );
				playGolf(2);
			}
		});

		gridS.add(btnSh1,1,0);
		gridS.add(btnSh2,1,1);
		gridS.add(btnSh3,1,2);
		border.setCenter(gridS);


	}
	public void setPlayGround(){
		switch(bg){
			case 0:
				//dodavanje blata i leda
				addSpeedBumps(new Translate(200,250 ), new Translate(Main.WINDOW_WIDTH - 60,Main.WINDOW_HEIGHT-200 ), new Translate(Main.WINDOW_WIDTH/2,Main.WINDOW_HEIGHT/2 + 20 ),new Translate(70,Main.WINDOW_HEIGHT-100 ));
				//dodavanje prepreka
				addObstacle(new Translate(WINDOW_WIDTH/2-10,WINDOW_HEIGHT/4- 80 ), 12,150);
				addObstacle(new Translate(100,WINDOW_HEIGHT/2 ), 150,12);
				addObstacle(new Translate(WINDOW_WIDTH-250,WINDOW_HEIGHT/2 ), 150,12);
				break;
			case 1:
				addSpeedBumps(new Translate(120,200 ), new Translate(Main.WINDOW_WIDTH - 100,Main.WINDOW_HEIGHT/3 ), new Translate(Main.WINDOW_WIDTH/2-100,Main.WINDOW_HEIGHT/2 + 80 ),new Translate(WINDOW_WIDTH-100,Main.WINDOW_HEIGHT-100 ));
				//prepreke
				addObstacle(new Translate(WINDOW_WIDTH-100,WINDOW_HEIGHT/2-60 ), 12,100);
				addObstacle(new Translate(WINDOW_WIDTH*2/3 ,WINDOW_HEIGHT/2-70 ), 150,12);
				addObstacle(new Translate(20,WINDOW_HEIGHT-180 ), 170,12);
				break;
			case 2:
				addSpeedBumps(new Translate(WINDOW_WIDTH-160,200 ), new Translate(Main.WINDOW_WIDTH/2,Main.WINDOW_HEIGHT/2+ 80 ), new Translate(Main.WINDOW_WIDTH/2-100,Main.WINDOW_HEIGHT/2 +180  ),new Translate(WINDOW_WIDTH-150,Main.WINDOW_HEIGHT/3+100 ));
				//prepreke
				addObstacle(new Translate(WINDOW_WIDTH/3,WINDOW_HEIGHT*2/3-70 ), 190,12);
				addObstacle(new Translate(WINDOW_WIDTH/3 ,WINDOW_HEIGHT/2-60 ), 190,12);
				addObstacle(new Translate(WINDOW_WIDTH/3 ,200 ), 190,12);
				break;
		}
		//dodaj igraca
		player=new Player(PLAYER_WIDTH,PLAYER_HEIGHT,new Translate(WINDOW_WIDTH/2-PLAYER_WIDTH/2, WINDOW_HEIGHT-PLAYER_HEIGHT-10));
		//dodaj indikator brzine
		Rectangle stats =new Rectangle(4, WINDOW_HEIGHT-100, 7, 200);
		stats.setFill(Color.PURPLE);

		st = new ScaleTransition(Duration.millis(3000), stats);
		st.setFromY(0);
		st.setToY(1);

		//dodaj br poena text
		poenitext.setText("Broj poena: "+score);
		poenitext.setX(10);
		poenitext.setY(15);
		poenitext.setFont(Font.font ("Times New Roman", FontWeight.BOLD, 20));
		poenitext.setFill(Color.PALEGREEN);
		poenitext.setStyle(" -fx-stroke: black; -fx-stroke-width: 1;");
		poenitext.toFront();
		//dodaj zivote
		loptetext.setText("Pokusaji: ");
		loptetext.setX(WINDOW_WIDTH-170);
		loptetext.setY(15);
		loptetext.setFont(Font.font ("Times New Roman", FontWeight.BOLD, 20));
		loptetext.setFill(Color.ORANGERED);
		loptetext.setStyle("  -fx-stroke: black; -fx-stroke-width: 1;");
		loptetext.toFront();
		for(int i = 0; i < 5; i++){
			Circle c=new Circle(WINDOW_WIDTH-80 + i*15, 10, BALL_RADIUS,Color.RED);
			c.setStyle("    -fx-stroke: black; -fx-stroke-width: 1;");
			this.root.getChildren().add(c);
			ballsLeftList.add(c);
		}

		//dodaj rupe
		if(this.bg==0) {
			addHoles(new Translate(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT * 0.1), new Translate(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT * 0.4), new Translate(Main.WINDOW_WIDTH / 3, Main.WINDOW_HEIGHT * 0.25), new Translate(Main.WINDOW_WIDTH * 2 / 3, Main.WINDOW_HEIGHT * 0.25));
		}else if(this.bg==1){
			addHoles(new Translate(Main.WINDOW_WIDTH*3/4, 150), new Translate(Main.WINDOW_WIDTH -100, Main.WINDOW_HEIGHT *0.75-50), new Translate(80, Main.WINDOW_HEIGHT * 2/3+20), new Translate(Main.WINDOW_WIDTH -130, Main.WINDOW_HEIGHT * 0.5));
		}else{
			addHoles(new Translate(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT * 0.25 -60),new Translate(70, Main.WINDOW_HEIGHT * 0.75), new Translate(Main.WINDOW_WIDTH / 2, Main.WINDOW_HEIGHT /2), new Translate(Main.WINDOW_WIDTH -100, Main.WINDOW_HEIGHT/3 + 50) );
		}

		//dodaj time bar
		timerLabel.setText(timeSeconds.toString());
		timerLabel.setTextFill(Color.BLACK);
		timerLabel.getTransforms().setAll(new Translate(WINDOW_WIDTH/2-10, 1));
		timerLabel.setStyle(" -fx-stroke: black; -fx-stroke-width: 1; -fx-font-weight: bold; -fx-font-size: 14; -fx-font-family: Time New Roman");
		timerLabel.textProperty().bind(Bindings.createStringBinding(() -> String.format("%02d:%02d",timeSeconds.divide(100).get() / 60,timeSeconds.divide(100).get() % 60)));

		bar = new ProgressBar();
		bar.setPrefSize(220, 15);
		bar.setStyle(" -fx-accent: grey; -fx-control-inner-background: red;");
		bar.getTransforms().setAll(new Translate(400, 16),new Rotate(-180, 0, 0));
		//bar.progressProperty().bind(timeSeconds.divide(STARTTIME*100.0).subtract(1).multiply(-1)); //matematika zato sto odbrojava unazad
		bar.setProgress(0);
		task = new Timeline(new KeyFrame(Duration.seconds(STARTTIME+1), new KeyValue(timeSeconds, 0)));
		//task.setCycleCount(Timeline.INDEFINITE);
		timeSeconds.addListener((ov, statusOld, statusNewNumber) -> {
			int statusNew = statusNewNumber.intValue();
			bar.progressProperty().bind(timeSeconds.divide(STARTTIME*100.0).subtract(1).multiply(-1)); //matematika zato sto odbrojava unazad
			timerLabel.textProperty().bind(Bindings.createStringBinding(() -> String.format("%02d:%02d",timeSeconds.divide(100).get() / 60,timeSeconds.divide(100).get() % 60)));

		});

		//dodaj teleportere
		if(this.bg==0) {
			teleptr1=new Teleporter(new Translate(WINDOW_WIDTH/3-30,WINDOW_HEIGHT/2+100));
			teleptr2=new Teleporter(new Translate(WINDOW_WIDTH-80,WINDOW_HEIGHT/2-80));
		}else if(this.bg==1){
			teleptr1=new Teleporter(new Translate(WINDOW_WIDTH/2,WINDOW_HEIGHT/3));
			teleptr2=new Teleporter(new Translate(WINDOW_WIDTH/2+150,WINDOW_HEIGHT*2/3));
		}else{
			teleptr1=new Teleporter(new Translate(150,WINDOW_HEIGHT/4));
			teleptr2=new Teleporter(new Translate(WINDOW_WIDTH-100,150));
		}
		this.root.getChildren().addAll(player,stats,loptetext,poenitext,bar,timerLabel,teleptr1,teleptr2);

	}

	public void playGolf(int sh){
		addWalls();
		setSH(sh);
		setPlayGround();

	}

	public static void setBG(int i){
		bg=i;
	}

	public static int getBG(){
		return bg;
	}

	public static void setSH(int i){
		sh=i;
	}

	public static int getSH(){
		return sh;
	}

	public static void setIsInHole(boolean b){
		isInHole=b;
	}

	public void gift(int v){
		if(v==0){ //zeleni
			task.stop();
			timeSeconds.set(timeSeconds.get()+1000); //10 sekundi povecava
			task.playFrom(Duration.seconds(timeSeconds.get()*(-1)));
		}
		if(v==1){//plavi
			if(ballsLeft==5)return; //ne moze da ima vise od 5 zivota
			Circle c=new Circle(WINDOW_WIDTH-80 + ballsLeftList.size()*15, 10, BALL_RADIUS,Color.RED);
			c.setStyle("    -fx-stroke: black; -fx-stroke-width: 1;");
			this.root.getChildren().add(c);
			ballsLeftList.add(c);
			ballsLeft++;
		}
		if(v==2){//roze
			score+= 20;
			poenitext.setText("Broj poena: "+score);
		}
	}

	public static boolean isTeleporterUsed(){
		return tUsed;
	}
	public static void setTeleporterUsed(boolean b){
		tUsed=b;
	}

	public void gameOver(){
		//GAME OVER
		BorderPane over = new BorderPane();
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		//gridL.setPrefHeight(20);
		grid.setHgap(20);
		grid.setVgap(50);
		over.setCenter(grid);

		HBox hboxlevel = new HBox();
		Label lblScore= new Label("Your score : "+ score);
		lblScore.setFont(new Font("Times New Roman", 30));
		lblScore.setAlignment(Pos.CENTER);

		hboxlevel.setPrefHeight(40);
		hboxlevel.setPadding(new Insets(20, 20, 20, 130));
		hboxlevel.setStyle("-fx-background-color: #33AFFF;");
		over.setTop(hboxlevel);
		hboxlevel.getChildren().addAll( lblScore);

		Label lbl= new Label("GAME OVER");
		lbl.setFont(new Font("Arial Black", 50));
		lbl.setAlignment(Pos.CENTER);

		Image image = new Image("gameover.jpg");
		BackgroundImage bImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, new BackgroundSize(grid.getWidth(), grid.getHeight(), true, true, true, false));
		javafx.scene.layout.Background backGround = new javafx.scene.layout.Background(bImage);
		grid.setBackground(backGround);
		grid.add(lbl,1,1);

		over.setCenter(grid);
		Scene gameOverScene=new Scene(over,Main.WINDOW_WIDTH,Main.WINDOW_HEIGHT);
		scene.setCursor ( Cursor.NONE );

		stage.setTitle ( "Golfer" );
		stage.setResizable ( false );
		stage.setScene ( gameOverScene );
		stage.show ( );
		timer.stop();
	}



	/*
	//za brod da se random generise vreme kada ce proci
	long t=random.nextInt(max - min + 1) + min
	//ako je proslo vreme t se ponovo generise i novi ship opet

	 */

}