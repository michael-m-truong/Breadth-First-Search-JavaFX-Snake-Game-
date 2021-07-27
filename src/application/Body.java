package application;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.lang.Math;

public class Body {
	
	private double row;
	private double column;
	private Rectangle snake;

	public Body(Rectangle snake, double row, double column) {
		this.snake = snake;
		this.row = row;
		this.column = column;
		snake.setX(50);
		snake.setY(50);
		snake.setWidth(Math.round(500/row*100.0)/100.0);         //used Math.round to round to nearest hundredth place, because some numbers have repeating decimals
		snake.setHeight(Math.round(500/column*100.0)/100.0);
		snake.setFill(Color.WHITE);
		snake.setStroke(Color.BLACK);
		snake.setStrokeWidth(3);
	}
	
	public double velocity() {
		double w = snake.getWidth(); 
		return w/8; //moves a distance of 1/8th of a tile
	}
	
	public double[] rowgrid() {    //essentially creates a grid and the coordinates for each line 
		double h = snake.getHeight();
		double rowLines[] = new double[(int) row];
		for (int i = 0; i < row; i++) {
			rowLines[i] = Math.round(h*(i)*100.0)/100.0;
		}
		return rowLines;
		
	}
	
	
	
	
	
}
