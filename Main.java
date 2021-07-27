package application;
	
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
//import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
//import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;


public class Main extends Application {
	int boardRowCol = 8;  //dimensions of the intial board 8x8 
	public void start(Stage stage) {
		Group root = new Group();
	    Scene scene = new Scene(root, 1000,600, Color.GREEN);
	    Button button = new Button("Restart"); 
	    Button button2 = new Button("Breadth First Search"); 
	    Button button3 = new Button("Submit");
	    TextField boardDimension = new TextField();
	    button3.setLayoutX(800);
	    button3.setLayoutY(450);
	    button3.setPrefHeight(50);
	    button3.setPrefWidth(100);
	    button3.setFocusTraversable(false);
	    boardDimension.setText("Enter # of rows/col for board");
	    boardDimension.setLayoutX(625);
	    boardDimension.setLayoutY(450);
	    boardDimension.setPrefWidth(200);
	    boardDimension.setPrefHeight(50);
	    button.setLayoutX(775);
	    button.setLayoutY(100);
	    button.setPrefHeight(50);
	    button.setPrefWidth(100);
	    button2.setLayoutX(700);
	    button2.setLayoutY(300);
	    button2.setPrefHeight(50);
	    button2.setPrefWidth(200);
	    ArrayList<String> input = new ArrayList<String>();      //logs key press
	    ArrayList<Integer> frames = new ArrayList<Integer>();  //snake moves 1/8th of a tile each time Animation timer runs 
	    
	    Rectangle area = new Rectangle();     
		Rectangle border1 = new Rectangle();  //setting up the square boundary for the snake
		Rectangle snake = new Rectangle();      
		Body head = new Body(snake, boardRowCol, boardRowCol);   //snake body dimensions scale off dimensions of board to make board re-sizable
		area.setX(0);
		area.setY(0);
		area.setHeight(600);
		area.setWidth(600);
		area.setFill(Color.SADDLEBROWN);
		border1.setX(50);
		border1.setY(50);
		border1.setHeight(500);
		border1.setWidth(500);
		border1.setFill(Color.GREEN);
		root.getChildren().add(area);
	    root.getChildren().add(border1);
	    frames.add(0);  //snake starts at 0 frames, once frames reaches 8, it means snake has traveled a distance of 1 tile 
	    double tileArrays[][][] = squarePositions(head.rowgrid(), boardRowCol);  // maps out the x & y coordinates for the tiles, like a checker board
	    Rectangle apple = new Rectangle();
	    apple.setHeight(snake.getHeight());
	    apple.setWidth(snake.getWidth());
	    apple.setX(50);
	    apple.setY(50);
	    apple.setLayoutX(head.rowgrid()[boardRowCol/2]);
	    apple.setLayoutY(head.rowgrid()[boardRowCol/2]);
	    apple.setFill(Color.RED);
	    apple.setStroke(Color.BLACK);
		apple.setStrokeWidth(3);
	    root.getChildren().add(apple);
	    root.getChildren().add(button);
	    root.getChildren().add(button2);
	    root.getChildren().add(boardDimension);
	    root.getChildren().add(button3);
	    
	    
	    ArrayList<Integer> applesEaten = new ArrayList<Integer>();  
	    LinkedList<String> moveSet = new LinkedList<String>();      //move set for the tail
	    LinkedList<Rectangle> currentHead = new LinkedList<Rectangle>();   // a new body is added to head each time apple is eaten
	    ArrayList<Boolean> Alive = new ArrayList<Boolean>();    //snake is either alive or dead
	    ArrayList<Boolean> BFS_ON = new ArrayList<Boolean>();     //tells whether the snake is on autopilot
	    BFS_ON.add(false);
	    Alive.add(true);
	    int[][] boardMap = new int[boardRowCol+2][boardRowCol+2];  //creates board array, +2 for the boundaries
	    moveSet.addFirst("");    //initializing moveSet
	    applesEaten.add(0);
	    
	    
	    scene.setOnKeyPressed(new EventHandler<KeyEvent>()    //translates key presses to snake movement
	            {
	                public void handle(KeyEvent e)
	                {
	                    String code = e.getCode().toString();
	                    System.out.println("test");
	                    if ( !input.contains(code) ) {
	                    	if (input.contains("RIGHT") && code.equals("LEFT"));      //making sure snake doesn't go backwards
	                    	else if(input.contains("LEFT") && code.equals("RIGHT"));
	                    	else if(input.contains("DOWN") && code.equals("UP"));
	                    	else if(input.contains("UP") && code.equals("DOWN"));
	                    	else {
	                    		input.add(code);   
	                    	}  
	                    }
	                }
	            });
	 
	    EventHandler<ActionEvent> buttonClick = new EventHandler<ActionEvent>() {  //restarts the game
            public void handle(ActionEvent e)
            {
            	BFS_ON.set(0, false);
        		Body head = new Body(snake, boardRowCol, boardRowCol);
            	apple.setLayoutX(head.rowgrid()[boardRowCol/2]);
        	    apple.setLayoutY(head.rowgrid()[boardRowCol/2]);
        	    snake.setLayoutX(0);
        		snake.setLayoutY(0);
        		input.clear();
        		currentHead.clear();
        		currentHead.add(snake);
        		while (root.getChildren().size() > 8) {
        			root.getChildren().remove(8);
        		} 
        		Alive.set(0, true);
        		frames.set(0, 0);
        		start(stage);
        		
        		
            }
        };
        
        EventHandler<ActionEvent> button3Click = new EventHandler<ActionEvent>() {  //takes in userinput and changes board dimension
            public void handle(ActionEvent e)
            {
            	System.out.println(boardDimension.getText());
            	snake.requestFocus();
        		boardRowCol = Integer.parseInt(boardDimension.getText());
        		apple.setHeight(snake.getHeight());
        		apple.setWidth(snake.getWidth());
            }
        };
        
        EventHandler<ActionEvent> button2Click = new EventHandler<ActionEvent>() {   //starts the breadth first search
            public void handle(ActionEvent e)
            {	
            	BFS_ON.set(0, true);
        	    LinkedList<String> shortestPath = new LinkedList<String>();
            	Graph graph = new Graph(tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol), boardRowCol); 
        	    graph.BFSearch();
        	    shortestPath = graph.getShortestPath();
        	    input.clear();
        	    if (!graph.isApple()) {
        	    	input.clear();
        	    	input.add(shortestPath.getFirst());
        	    }
        	    	
        	    else if (shortestPath.size() != 0) {
        	    	if (input.size() != 0) { 
        	    		if (!input.get(input.size()-1).equals(shortestPath.getFirst())) {
        	    			input.add(shortestPath.getFirst());
        	    		}
        	    		else {
        	    			
        	    		}
        	    	}
        	    	else {
            	    input.add(shortestPath.getFirst());
        	    	shortestPath.removeFirst();
        	    	}
        	    }
        	    if (input.isEmpty()) {
        	    	graph.BFSearch();
        	    }
        	    
        		
        		
            }
        };  
        button.setOnAction(buttonClick);
        button.setFocusTraversable(false);
        button2.setOnAction(button2Click);
        button2.setFocusTraversable(false);
        button3.setOnAction(button3Click);
        boardDimension.setFocusTraversable(false);
	    currentHead.addFirst(snake);
	    Graph graph = new Graph(tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol), boardRowCol); 
	    graph.BFSearch();
	    new AnimationTimer() {
	    	public void stop() {   //allows game to be restartable with a button click
	    		Graph graph = new Graph(tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol), boardRowCol); 
	    	    graph.BFSearch();
	    		if (!BFS_ON.get(0)) {
	    		super.stop();
	    		Timer restart = new Timer();
	    		TimerTask tt = new TimerTask() {  
				    @Override  
				    public void run() {  
				    	if (Alive.get(0)) {
				    		start();
				    		restart.cancel();
				    	}
				    };  
				};  
				restart.schedule(tt, 0, 1); 
	    		}
	    		else if (graph.getImpossible()) {
	    			super.stop();
	    		}
	    		else {
	    			Alive.set(0, true);
	    			button2.fire();
	    		} 
	    	}
	    	public void handle(long currentNanoTime) {
	    		int [][] boardPos;
	    		if (frames.get(0) == 8 && Alive.get(0)) {
	    			boardPos = tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol);  //tailcollision2 returns matrix board map
		    		if (BFS_ON.get(0)) {   //if BFS button is on, start the search
		    			button2.fire();  
		    		}
	    		} 
	    		
	    		// if apple is eaten then randomize apple location and add on a new snake body
	    		if (objPosition(currentHead.getFirst())[0] == objPosition(apple)[0] && objPosition(currentHead.getFirst())[1] == objPosition(apple)[1]) {
	    			moveSet.addLast(moveSet.get(moveSet.size()-1));
	    			int upperbound = boardRowCol;
	    			boolean newApple = false;
	    			int c = 0;
	    			if (input.get(input.size()-1) == "UP") {
	    				Rectangle addedHead = new Rectangle();
	    				Body addedBody = new Body(addedHead, boardRowCol, boardRowCol);
	    				newApple = false;
	    				if (objPosition(currentHead.getFirst())[1]-addedHead.getHeight() + addedHead.getHeight()<= 0) {
	    					Alive.set(0, false);
	    					stop();
	    				}
	    				else {
		    				addedHead.setLayoutX(objPosition(currentHead.getFirst())[0]);  //adding new body based on most recent key press 
		    				addedHead.setLayoutY(objPosition(currentHead.getFirst())[1]-addedHead.getHeight());
		    				currentHead.addFirst(addedHead);
		    				root.getChildren().add(addedHead);
		    				moveSet.addFirst("UP");
	    					moveSet.removeLast();
	    					while (!newApple) {
			    				Random rand = new Random();
			    				int num1 = rand.nextInt(upperbound);
			    				int num2 =rand.nextInt(upperbound);
			    				c = 0;
			    				if (tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 1 && tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 3 && tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 4) {
			    					boardMap[num1+1][num2+1] = 3;
			    					apple.setLayoutX(tileArrays[num1][num2][0]);
			    					apple.setLayoutY(tileArrays[num1][num2][1]);
			    					newApple = true;
			    					
			    				}
		    				}
	    					Graph graph = new Graph(tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol), boardRowCol); 
	    				    graph.BFSearch();
	    				    if (BFS_ON.get(0)) {
	    				    	button2.fire();
	    				    }
	    				}
	    				if (objPosition(currentHead.getFirst())[1] <= 0) {  //for cases where the snake eats the apple at 1 tile away from the boundary, it gives the user 250 ms to react and avoid wall collision
	    					
	    					Timer timer = new Timer();
	    					TimerTask tt = new TimerTask() {  
	    					    @Override  
	    					    public void run() {  
	    					    	if (input.get(input.size()-1) == "UP") {
	    		    					Alive.set(0, false);
	    		    					stop();
	    		    					timer.cancel();
	    	    					}
	    					    };  
	    					};  
	    					timer.schedule(tt, 250); 
	    					
	    				} 
	    			}
	    			else if (input.get(input.size()-1) == "DOWN") {
	    				Rectangle addedHead = new Rectangle();
	    				Body addedBody = new Body(addedHead, boardRowCol, boardRowCol);
	    				newApple = false;
	    				if (objPosition(currentHead.getFirst())[1]+addedHead.getHeight() >= 500.0-currentHead.getFirst().getHeight() /*- 6.0*/ + addedHead.getHeight()) {
	    					Alive.set(0, false);
	    					stop();
	    				}
	    				else {
		    				addedHead.setLayoutX(objPosition(currentHead.getFirst())[0]);
		    				addedHead.setLayoutY(objPosition(currentHead.getFirst())[1]+addedHead.getHeight());
		    				currentHead.addFirst(addedHead);
		    				root.getChildren().add(addedHead);
		    				moveSet.addFirst("DOWN");
	    					moveSet.removeLast();
	    					while (!newApple) {
			    				Random rand = new Random();
			    				int num1 = rand.nextInt(upperbound);
			    				int num2 =rand.nextInt(upperbound);
			    				c = 0;
			    				if (tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 1 && tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 3 && tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 4) {
			    					apple.setLayoutX(tileArrays[num1][num2][0]);
			    					apple.setLayoutY(tileArrays[num1][num2][1]);
			    					newApple = true;
			    					
			    				}
		    				}
	    					Graph graph = new Graph(tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol), boardRowCol); 
	    				    graph.BFSearch();
	    				    if (BFS_ON.get(0)) {
	    				    	button2.fire();
	    				    }
	    				}
	    				if (objPosition(currentHead.getFirst())[1] >= 500.0-currentHead.getFirst().getHeight() /*- 6.0*/) {
	    					
	    					Timer timer = new Timer();
	    					TimerTask tt = new TimerTask() {  
	    					    @Override  
	    					    public void run() {  
	    					    	if (input.get(input.size()-1) == "DOWN") {
	    		    					Alive.set(0, false);
	    		    					stop();
	    		    					timer.cancel();
	    	    					}
	    					    };  
	    					};  
	    					timer.schedule(tt, 250); 
	    					
	    				} 
	    			}
	    			else if (input.get(input.size()-1) == "LEFT") {
	    				Rectangle addedHead = new Rectangle();
	    				Body addedBody = new Body(addedHead, boardRowCol, boardRowCol);
	    				newApple = false;
	    				if (objPosition(currentHead.getFirst())[0]-addedHead.getHeight() + addedHead.getHeight() <= 0) {
	    					Alive.set(0, false);
	    					stop();
	    				}
	    				else {
		    				addedHead.setLayoutX(objPosition(currentHead.getFirst())[0]-addedHead.getHeight());
		    				addedHead.setLayoutY(objPosition(currentHead.getFirst())[1]);
		    				currentHead.addFirst(addedHead);
		    				root.getChildren().add(addedHead);
		    				moveSet.addFirst("LEFT");
	    					moveSet.removeLast();
	    					while (!newApple) {
			    				Random rand = new Random();
			    				int num1 = rand.nextInt(upperbound);
			    				int num2 =rand.nextInt(upperbound);
			    				c = 0;
			    				if (tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 1 && tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 3 && tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 4) {
			    					apple.setLayoutX(tileArrays[num1][num2][0]);
			    					apple.setLayoutY(tileArrays[num1][num2][1]);
			    					newApple = true;
			    					
			    				}
		    				}
	    					Graph graph = new Graph(tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol), boardRowCol); 
	    				    graph.BFSearch();
	    				    if (BFS_ON.get(0)) {
	    				    	button2.fire();
	    				    }
	    				}
	    				if (objPosition(currentHead.getFirst())[0] <= 0) {
	    					
	    					Timer timer = new Timer();
	    					TimerTask tt = new TimerTask() {  
	    					    @Override  
	    					    public void run() {  
	    					    	if (input.get(input.size()-1) == "LEFT") {
	    		    					Alive.set(0, false);
	    		    					stop();
	    		    					timer.cancel();
	    	    					}
	    					    };  
	    					};  
	    					timer.schedule(tt, 250); 
	    						
	    				} 
	    			}
	    			else if (input.get(input.size()-1) == "RIGHT") {
	    				Rectangle addedHead = new Rectangle();
	    				Body addedBody = new Body(addedHead, boardRowCol, boardRowCol);
	    				newApple = false;
	    				if (objPosition(currentHead.getFirst())[0]+addedHead.getHeight() >= 500.0-currentHead.getFirst().getWidth() /*- 6.0*/ + addedHead.getHeight()) {
	    					Alive.set(0, false);
	    					stop();
	    				}
	    				else {
		    				addedHead.setLayoutX(objPosition(currentHead.getFirst())[0]+addedHead.getHeight());
		    				addedHead.setLayoutY(objPosition(currentHead.getFirst())[1]);
		    				currentHead.addFirst(addedHead);
		    				root.getChildren().add(addedHead);
		    				moveSet.addFirst("RIGHT");
	    					moveSet.removeLast();
	    					while (!newApple) {
			    				Random rand = new Random();
			    				int num1 = rand.nextInt(upperbound);
			    				int num2 =rand.nextInt(upperbound);
			    				c = 0;
			    				if (tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 1 && tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 3 && tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol)[num1+1][num2+1] != 4) {
			    					apple.setLayoutX(tileArrays[num1][num2][0]);
			    					apple.setLayoutY(tileArrays[num1][num2][1]);
			    					newApple = true;
			    					
			    				}
		    				}
	    					Graph graph = new Graph(tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol), boardRowCol); 
	    				    graph.BFSearch();
	    				    if (BFS_ON.get(0)) {
	    				    	button2.fire();
	    				    }
	    				}
	    				if (objPosition(currentHead.getFirst())[0] >= 500.0-currentHead.getFirst().getWidth() /*- 6.0*/) {
	    					
	    					Timer timer = new Timer();
	    					TimerTask tt = new TimerTask() {  
	    					    @Override  
	    					    public void run() {  
	    					    	if (input.get(input.size()-1) == "RIGHT") {
	    		    					Alive.set(0, false);
	    		    					stop();
	    		    					timer.cancel();
	    	    					}
	    					    };  
	    					};  
	    					timer.schedule(tt, 250); 
	    					
	    				} 
	    			}
	    			
	    		}
	  
	    		// when input size equals 2, it means that the user is trying to change direction
	    		if (input.size() == 2) { 
	    			if (frames.get(0) == 8 && Alive.get(0)) {
	    				if (input.get(1) == "DOWN" && currentHead.getFirst().getLayoutY() < 500.0-currentHead.getFirst().getHeight() /*- 6.0*/) {
	    					boardPos = tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol);
	    					moveSet.addFirst("DOWN");
	    					moveSet.removeLast();
	    					if (tailCollision1(moveSet, currentHead, boardPos, tileArrays) && !BFS_ON.get(0)) {
	    						Alive.set(0, false);
	    						stop();
	    		    		}
	    					else {
	    					currentHead.getFirst().setLayoutY(currentHead.getFirst().getLayoutY() + head.velocity());
	    					input.clear();
	    					input.add("DOWN");
	    					frames.set(0, 1);
	    					moveBody(moveSet, currentHead, head.velocity());  
	    					}
	    					
	    					
	    	    		}
	    				else if (input.get(1).equals("RIGHT") && currentHead.getFirst().getLayoutX() < 500.0-currentHead.getFirst().getWidth() /*- 6.0*/) {				
	    					boardPos = tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol);
	    					moveSet.addFirst("RIGHT");
	    					moveSet.removeLast();
	    					if (tailCollision1(moveSet, currentHead, boardPos, tileArrays) && !BFS_ON.get(0)) {
	    						Alive.set(0, false);
	    						stop();
	    		    		}
	    					else {
	    					currentHead.getFirst().setLayoutX(currentHead.getFirst().getLayoutX() + head.velocity());
	    					input.clear();
	    					input.add("RIGHT");
	    					frames.set(0, 1);
	    					moveBody(moveSet, currentHead, head.velocity());
	    					}
	    					
	    					
	    				}
	    				else if (input.get(1).equals("UP") && currentHead.getFirst().getLayoutY() > 0) {
	    					boardPos = tailCollision2(tileArrays, boardMap, currentHead,apple, boardRowCol);
	    					moveSet.addFirst("UP");
	    					moveSet.removeLast();
	    					if (tailCollision1(moveSet, currentHead, boardPos, tileArrays) && !BFS_ON.get(0)) {
	    						Alive.set(0, false);
	    						stop();
	    		    		}
	    					else {
	    					currentHead.getFirst().setLayoutY(currentHead.getFirst().getLayoutY() - head.velocity());
	    					input.clear();
	    					input.add("UP");
	    					frames.set(0, 1);
	    					moveBody(moveSet, currentHead, head.velocity());
	    					}
	    					
	    					
	    	    		}
	    				else if (input.get(1).equals("LEFT") && currentHead.getFirst().getLayoutX() > 0) {
	    					boardPos = tailCollision2(tileArrays, boardMap, currentHead,apple, boardRowCol);
	    					moveSet.addFirst("LEFT");
	    					moveSet.removeLast();
	    					if (tailCollision1(moveSet, currentHead, boardPos, tileArrays) && !BFS_ON.get(0)) {
	    						Alive.set(0, false);
	    						stop();
	    		    		}
	    					else {
		    					currentHead.getFirst().setLayoutX(currentHead.getFirst().getLayoutX() - head.velocity());
		    					input.clear();
		    					input.add("LEFT");
		    					frames.set(0, 1);
		    					moveBody(moveSet, currentHead, head.velocity());
	    					}
	    					
	    	    		}
	    				
	    			}
	    			// the snake only changes directions when frames equals 8, or else it will not be aligned with correctly
	    			else {

	    				if (input.get(0).equals("RIGHT") && currentHead.getFirst().getLayoutX() < 500.0-currentHead.getFirst().getWidth() /*- 6.0*/) {
	    					currentHead.getFirst().setLayoutX(currentHead.getFirst().getLayoutX() + head.velocity());   					
	    	    			frames.set(0, frames.get(0) + 1);
	    					if (frames.get(0) == 9) {
	    						frames.set(0, 1);
	    						moveSet.addFirst("RIGHT");
	    						moveSet.removeLast();
	    					}
	    					moveBody(moveSet, currentHead, head.velocity());

	    				}
	    				else if (input.get(0).equals("UP") && currentHead.getFirst().getLayoutY() > 0) {	    	    			
	    					currentHead.getFirst().setLayoutY(currentHead.getFirst().getLayoutY() - head.velocity());
	    	    		    frames.set(0, frames.get(0) + 1);
	    	    		    if (frames.get(0) == 9) {
	    						frames.set(0, 1);
	    						moveSet.addFirst("UP");
	    						moveSet.removeLast();
	    					}
	    					moveBody(moveSet, currentHead, head.velocity());
	    	    		}
	    				else if (input.get(0).equals("DOWN") && currentHead.getFirst().getLayoutY() < 500.0-currentHead.getFirst().getHeight() /*- 6.0*/) {
	    					currentHead.getFirst().setLayoutY(currentHead.getFirst().getLayoutY() + head.velocity());	
	    	    			frames.set(0, frames.get(0) + 1);
	    					if (frames.get(0) == 9) {
	    						frames.set(0, 1);
	    						moveSet.addFirst("DOWN");
	    						moveSet.removeLast();
	    					}
	    					moveBody(moveSet, currentHead, head.velocity());
	    	    		}
	    				else if (input.get(0).equals("LEFT") && currentHead.getFirst().getLayoutX() > 0) {
	    					currentHead.getFirst().setLayoutX(currentHead.getFirst().getLayoutX() - head.velocity());
	    	    			frames.set(0, frames.get(0) + 1);
	    	    			if (frames.get(0) == 9) {
	    						frames.set(0, 1);
	    						moveSet.addFirst("LEFT");
	    						moveSet.removeLast();
	    					}
	    					moveBody(moveSet, currentHead, head.velocity());
	    	    		}
	    			}
	    		} 
	    		
	    		//if user does not want to change directions, continue moving in same direction
	    		else if (input.contains("UP") && currentHead.getFirst().getLayoutY() > 0) {
	    			currentHead.getFirst().setLayoutY(currentHead.getFirst().getLayoutY() - head.velocity());
	    		    frames.set(0, frames.get(0) + 1);
	    		    if (frames.get(0) == 9) {
						frames.set(0, 1);
						moveSet.addFirst("UP");
						moveSet.removeLast();						
					}
					moveBody(moveSet, currentHead, head.velocity());
	    		    if (frames.get(0) == 8) {
						String tempLast= moveSet.getLast();
						moveSet.addFirst("UP");
						moveSet.removeLast();
		    			boardPos = tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol);
			    		if (tailCollision1(moveSet, currentHead, boardPos, tileArrays) && !BFS_ON.get(0)) {
			    			Alive.set(0, false);
			    			stop();
			    		} 
			    		else {
			    			moveSet.addLast(tempLast);
			    			moveSet.removeFirst();
			    		}
		    		} 

	    		}
	    		
	    		//if user does not want to change directions, continue moving in same direction
	    		else if (input.contains("DOWN") && currentHead.getFirst().getLayoutY() < 500.0-currentHead.getFirst().getHeight() /*- 6.0*/) {
	    			currentHead.getFirst().setLayoutY(currentHead.getFirst().getLayoutY() + head.velocity());
					frames.set(0, frames.get(0) + 1);
					if (frames.get(0) == 9) {
						frames.set(0, 1);
						moveSet.addFirst("DOWN");
						moveSet.removeLast();
					}
					moveBody(moveSet, currentHead, head.velocity());
					if (frames.get(0) == 8) {
						String tempLast= moveSet.getLast();
						moveSet.addFirst("DOWN");
						moveSet.removeLast();
		    			boardPos = tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol);
			    		if (tailCollision1(moveSet, currentHead, boardPos, tileArrays) && !BFS_ON.get(0)) {
			    			Alive.set(0, false);
			    			stop();
			    		} 
			    		else {
			    			moveSet.addLast(tempLast);
			    			moveSet.removeFirst();
			    		}
		    		} 


	    		}
	    		
	    		//if user does not want to change directions, continue moving in same direction
	    		else if (input.contains("LEFT") && currentHead.getFirst().getLayoutX() > 0) {
	    			currentHead.getFirst().setLayoutX(currentHead.getFirst().getLayoutX() - head.velocity());
	    			frames.set(0, frames.get(0) + 1);
	    		    if (frames.get(0) == 9) {
						frames.set(0, 1);
						moveSet.addFirst("LEFT");
						moveSet.removeLast();
					}
					moveBody(moveSet, currentHead, head.velocity());
	    		    if (frames.get(0) == 8) {
						String tempLast= moveSet.getLast();
						moveSet.addFirst("LEFT");
						moveSet.removeLast();
		    			boardPos = tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol);
			    		if (tailCollision1(moveSet, currentHead, boardPos, tileArrays) && !BFS_ON.get(0)) {
			    			Alive.set(0, false);
			    			stop();
			    		} 
			    		else {
			    			moveSet.addLast(tempLast);
			    			moveSet.removeFirst();
			    		}
		    		} 

	    		}
	    		
	    		//if user does not want to change directions, continue moving in same direction
	    		else if (input.contains("RIGHT") && currentHead.getFirst().getLayoutX() < 500.0-currentHead.getFirst().getWidth() /*- 6.0*/) {
	    			currentHead.getFirst().setLayoutX(currentHead.getFirst().getLayoutX() + head.velocity());
	    		    frames.set(0, frames.get(0) + 1);
					if (frames.get(0) == 9) {
						frames.set(0, 1);
						moveSet.addFirst("RIGHT");
						moveSet.removeLast();
					}
					moveBody(moveSet, currentHead, head.velocity());
					if (frames.get(0) == 8) {
						String tempLast= moveSet.getLast();
						moveSet.addFirst("RIGHT");
						moveSet.removeLast();
		    			boardPos = tailCollision2(tileArrays, boardMap, currentHead, apple, boardRowCol);
			    		if (tailCollision1(moveSet, currentHead, boardPos, tileArrays) && !BFS_ON.get(0)) {
			    			Alive.set(0, false);
			    			stop();
			    		} 
			    		else {
			    			moveSet.addLast(tempLast);
			    			moveSet.removeFirst();
			    		}
		    		} 

	    		}
	    		
	    		
	    	}
	    }.start();
	    root.getChildren().add(snake);
	    stage.setScene(scene);
	    stage.show();
	    
	    
	    
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	public static double[][][] squarePositions(double[] array, int boardRowCol) {    //returns a 3D matrix of all the xy positions in the board
		double tileArrays[][][] = new double[boardRowCol][boardRowCol][boardRowCol];
		double pos[] = new double[2];
		int x = 0;
		int y = 0;
		for (double i: array) {
			for (double j: array) {
				pos[0] = i;
				pos[1] = j;
				double[] temp = {j,i};
				System.out.println(i);
				System.out.println(j);
				System.out.println(y);
				System.out.println(x);
				tileArrays[y][x] = temp;
				x +=1;
				
			}
			y +=1;
			x=0;
		}
		for (double[][] i: tileArrays) {
			for (double[] j: i) {
				System.out.println(Arrays.toString(j));
			}
		}

		return tileArrays;
		
	}
	
	public static double[] objPosition(Rectangle snake) {  //returns an array of the xy coordinates of the snake
		double[] snakePos = new double[2];
		snakePos[0] = Math.round(snake.getLayoutX()*100.0)/100.0;
		snakePos[1] = Math.round(snake.getLayoutY()*100.0)/100.0;
		return snakePos;
	}
	
	
	public static void moveBody(LinkedList<String> moveSet, LinkedList<Rectangle> currentHead, double velocity) {  //moves the tail
		for (int i = 0; i < moveSet.size(); i++) { 
			if (i != 0) {
				if (moveSet.get(i).equals("RIGHT")) {
					currentHead.get(i).setLayoutX(currentHead.get(i).getLayoutX() + velocity);

				}
				if (moveSet.get(i).equals("LEFT")) {
					currentHead.get(i).setLayoutX(currentHead.get(i).getLayoutX() - velocity);

				}
				if (moveSet.get(i).equals("UP")) {
					currentHead.get(i).setLayoutY(currentHead.get(i).getLayoutY() - velocity);

				}
				if (moveSet.get(i).equals("DOWN")) {
					currentHead.get(i).setLayoutY(currentHead.get(i).getLayoutY() + velocity);

				}
			}
	    } 
	}
	
	public static boolean tailCollision1(LinkedList<String> moveSet, LinkedList<Rectangle> currentHead, int boardMap[][], double[][][] tileArrays) {  //returns boolean of whether or not snake has hit its own body or the boundary
		if (moveSet.getFirst().equals("RIGHT")) {
			for (int i = 0; i < tileArrays.length; i++) {
				for (int j=0; j<tileArrays[i].length; j++) {
					if (Math.round(currentHead.get(0).getLayoutX()*100.0)/100.0 == tileArrays[i][j][0] && Math.round(currentHead.get(0).getLayoutY()*100.0)/100.0 == tileArrays[i][j][1]) {
						if (boardMap[i+1][j+2] == 1 || boardMap[i+1][j+2] == 2) {
							return true;
						} 
					}
				}
			}
		}
		else if (moveSet.getFirst().equals("LEFT")) {
			for (int i = 0; i < tileArrays.length; i++) {
				for (int j=0; j<tileArrays[i].length; j++) {
					if (Math.round(currentHead.get(0).getLayoutX()*100.0)/100.0 == tileArrays[i][j][0] && Math.round(currentHead.get(0).getLayoutY()*100.0)/100.0 == tileArrays[i][j][1]) {
						if (boardMap[i+1][j] == 1 || boardMap[i+1][j] == 2) {
							return true;
						} 
					}
				}
			}
		}
		else if (moveSet.getFirst().equals("UP")) {
			for (int i = 0; i < tileArrays.length; i++) {
				for (int j=0; j<tileArrays[i].length; j++) {
					if (Math.round(currentHead.get(0).getLayoutX()*100.0)/100.0 == tileArrays[i][j][0] && Math.round(currentHead.get(0).getLayoutY()*100.0)/100.0 == tileArrays[i][j][1]) {
						if (boardMap[i][j+1] == 1 || boardMap[i][j+1] == 2) {
							return true;
						} 
					}
				}
			}
		}
		else if (moveSet.getFirst().equals("DOWN")) {
			for (int i = 0; i < tileArrays.length; i++) {
				for (int j=0; j<tileArrays[i].length; j++) {
					if (Math.round(currentHead.get(0).getLayoutX()*100.0)/100.0 == tileArrays[i][j][0] && Math.round(currentHead.get(0).getLayoutY()*100.0)/100.0 == tileArrays[i][j][1]) {
						if (boardMap[i+2][j+1] == 1 || boardMap[i+2][j+1] == 2) {
							return true;
						} 
					}
				}
			}
		}
		return false;
	}
	
	public static int[][] tailCollision2(double[][][] tileArrays, int[][] boardMap, LinkedList<Rectangle> currentHead, Rectangle apple, int boardRowCol) {  //returns a 2D matrix of the current board map
		for (int i = 0; i < boardRowCol+2; i++) {   //2 represents boundaries   4 represents snake head   1 represents tail     3 represents apple 
	    	for (int j = 0; j < boardRowCol+2; j++) {
		    	if (i == 0 || i== boardRowCol+1|| j==0 || j==boardRowCol+1) {
		    		boardMap[i][j] = 2;
		    	}
		    	else {
		    		boardMap[i][j] = 0;
		    	}
		    } 
	    } 
		for (int i = 0; i < tileArrays.length; i++) {
			for (int j=0; j<tileArrays[i].length; j++) {
				for (int k = 0; k < currentHead.size(); k++) {
					if (Math.round(currentHead.get(k).getLayoutX()*100.0)/100.0 == tileArrays[i][j][0] && Math.round(currentHead.get(k).getLayoutY()*100.0)/100.0 == tileArrays[i][j][1]) {
						boardMap[i+1][j+1] = 1;
						if (k == 0) {
							boardMap[i+1][j+1] = 4;
						} 
					}
					if (Math.round(apple.getLayoutX()*100.0)/100.0 == tileArrays[i][j][0] && Math.round(apple.getLayoutY()*100.0)/100.0 == tileArrays[i][j][1]) {
						boardMap[i+1][j+1] = 3;
					}
				}
			}
		}
		for (int i = 0; i < boardMap.length; i++) {
	    	for (int j = 0; j < boardMap[i].length; j++) {
		    	System.out.print(boardMap[i][j]);
		    } 
	    	System.out.println();
	    }
    	System.out.println();
		return boardMap;
	}
	
	
	
}
