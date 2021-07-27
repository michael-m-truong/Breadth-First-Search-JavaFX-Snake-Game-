package application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Graph {

	public static LinkedList<Integer> rowQueue = new LinkedList<Integer>();
	public static LinkedList<Integer> colQueue = new LinkedList<Integer>();
	private int[][] board;
	private static int boardRowCol;
	public static boolean[][] visited = new boolean[boardRowCol+2][boardRowCol+2];
	public static int[][][] previous = new int[boardRowCol+2][boardRowCol+2][2];
	private LinkedList<String> shortestPath = new LinkedList<String>();
	public static int moveCount = 0;
	private boolean impossible;
	
	public Graph(int[][] board, int boardRowCol) {
		this.board = board;
		Graph.boardRowCol = boardRowCol;
	}
	
	public void updateGraph(int[][] board) {
		board = this.board;
	}
	
	public int[][] getBoard() {
		return board;
	}
	
	public boolean getImpossible() {
		return impossible;
	}
	
	public LinkedList<String> getShortestPath() {
		return shortestPath;
	}
	
	public boolean directlyAdjacent() {   //returns the direction of the apple when 1 tile away
		int startRow = findStart(board)[0];
		int startCol = findStart(board)[1];
		if (board[startRow+1][startCol] == 3) {
			shortestPath.add("DOWN");
			return true;
		}
		else if (board[startRow-1][startCol] == 3) {
			shortestPath.add("UP");
			return true;
		}
		else if (board[startRow][startCol+1] == 3) {
			shortestPath.add("RIGHT");
			return true;
		}
		else if (board[startRow][startCol-1] == 3) {
			shortestPath.add("LEFT");
			return true;
		}
		return false;
		
	}
	
	public boolean isImpossible() {  //if distance to nearest obstacle is 1 tile for all 4 directions, snake is dead and thus impossible to move 
		int startRow = findStart(board)[0];
		int startCol = findStart(board)[1];
		if (shortestPath.getFirst().equals("DOWN")) {
			if (board[startRow+1][startCol] == 1) {
				System.out.println(shortestPath.getFirst());
				return true;
			}
		}
		else if (shortestPath.getFirst().equals("UP")) {
			if (board[startRow-1][startCol] == 1) {
				System.out.println(shortestPath.getFirst());
				return true;
			}
		}
		else if (shortestPath.getFirst().equals("RIGHT")) {
			if (board[startRow][startCol+1] == 1) {
				System.out.println(shortestPath.getFirst());
				return true;
			}
		}
		else if (shortestPath.getFirst().equals("LEFT"))
			if (board[startRow][startCol-1] == 1) {
				System.out.println(shortestPath.getFirst());
				return true;
			}
		return false;
	}
	
	public void optimalMove() {   //when the apple is unreachable, the snake will move in the direction with the most open space
		int distanceToRightWall = 0;
		int distanceToLeftWall = 0;
		int distanceToUpWall = 0;
		int distanceToDownWall = 0;
		int startRow = findApple(board)[0];
		int startCol = findApple(board)[1];
		if (isApple()) {
			startRow = findStart(board)[0];
			startCol = findStart(board)[1];
		}
		while (board[startRow+distanceToDownWall][startCol] != 1 && board[startRow+distanceToDownWall][startCol] != 2) {
			distanceToDownWall +=1;
		}
		while (board[startRow-distanceToUpWall][startCol] != 1 && board[startRow-distanceToUpWall][startCol] != 2) {
			distanceToUpWall +=1;
		}
		while (board[startRow][startCol+distanceToRightWall] != 1 && board[startRow][startCol+distanceToRightWall] != 2) {
			distanceToRightWall +=1;
		}
		while (board[startRow][startCol-distanceToLeftWall] != 1 && board[startRow][startCol-distanceToLeftWall] != 2) {
			distanceToLeftWall +=1;
		}
		int maxDistanceDU = Math.max(distanceToDownWall, distanceToUpWall);
		int maxDistanceRL = Math.max(distanceToRightWall, distanceToLeftWall);
		int maxDistance = Math.max(maxDistanceDU, maxDistanceRL);
		int[] distances = {distanceToRightWall, distanceToLeftWall, distanceToUpWall, distanceToDownWall};
		ArrayList<String> max_distances = new ArrayList<String>();
		for (int i = 0; i < 4; i++) {
			if (distances[i] == maxDistance) {
				if (i == 0) {
					max_distances.add("RIGHT");
				}
				else if (i == 1) {
					max_distances.add("LEFT");
				}
				else if (i == 2) {
					max_distances.add("UP");
				}
				else {
					max_distances.add("DOWN");
				}
			}
		}
		if (maxDistance == 1) {    //if max distance equals 1, it means snake has no where to move
			max_distances.clear();
		} 
		else {
		Random random = new Random();    //randomize for directions with equal max distance
		int num = random.nextInt(max_distances.size());
		shortestPath.add(max_distances.get(num));
		}
	}
	
	public void BFSearch() {   //starts Breadth First Search
		impossible = false;
		shortestPath.clear();
		if (!isApple()) {    //if apple is eaten, move in the most optimal direction
			optimalMove();
			
		}
		else if (directlyAdjacent()) {   //if apple is directly adjacent to snake head
	
		}
		else {     
			moveCount = 0;
			visited = new boolean[boardRowCol+2][boardRowCol+2];
			previous = new int[boardRowCol+2][boardRowCol+2][2];
			rowQueue.clear();
			colQueue.clear();
			boolean[][] visited = new boolean[boardRowCol+2][boardRowCol+2];
			int startRow = findStart(board)[0];
			int startCol = findStart(board)[1];
			visited[startRow][startCol] = true;
			rowQueue.add(startRow);
			colQueue.add(startCol);
			while (rowQueue.size() > 0) {
				int r = rowQueue.getFirst();
				int c = colQueue.getFirst();
				rowQueue.removeFirst();
				colQueue.removeFirst();
				if (board[r][c] == 3) {
					shortestPath = reconstructPath(startRow, startCol, r, c);
					break;
				}
				moveCount++;
				exploreNeighbors(board, r, c);
			}
			impossible = true;
			if (shortestPath.isEmpty()) {
				optimalMove();
			}
		}
	}
	
	public static int[] findStart(int[][] board) {   //find snake head coordinates
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == 4) {
					int[] headPos = {i,j};
					return headPos; 
				}
			}
		}
		int[] headPos = {};
		return headPos; 
	}
	
	public static int[] findApple(int[][] board) {  //finds apple coordinates
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == 3) {
					int[] applePos = {i,j};
					return applePos; 
				}
			}
		}
		int[] applePos = {};
		return applePos; 
	}
	
	public boolean isApple() {  //returns boolean on whether apple is eaten or not
		boolean apple = false;
		boolean head = false;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == 4) {
					head = true;
				}
				if (board[i][j] == 3) {
					apple = true;
				}
			}
		}
		if (apple && head) {  //apple is eaten when the number 4 (which represents snake head) is not on the board map and apple is present
			return true;
		}
		return false;
	}
	
	public static void exploreNeighbors(int[][] board, int r, int c) {
		int[] dr = {-1, 1, 0, 0}; //north south east west
		int[] dc = {0, 0, 1, -1};
		for (int i = 0; i < 4; i++) {
			int rr = r + dr[i];
			int cc = c + dc[i];
			if (rr == 0 || cc == 0) {
				continue;
			}
			if (rr == boardRowCol+2 || cc == boardRowCol+2) {
				continue;
			}
			if (visited[rr][cc]) {
				continue;
			}
			if (board[rr][cc] == 1 || board[rr][cc] == 2) {
				continue;
			}
			rowQueue.add(rr);
			colQueue.add(cc);
			visited[rr][cc] = true;
			int[] prevCoords = {r,c};
			previous[rr][cc] = prevCoords;
		}
		
	}
	
	public static LinkedList<String> reconstructPath(int startRow, int startCol, int endRow, int endCol) {  
		int[][][] path = new int[1][64][2];
		int r = endRow;
		int c = endCol;
		int[] start = {startRow, startCol};
		int[] end = {endRow, endCol};
		int moves = 1;
		path[0][0] = end;
		System.out.println();
		int[] current = previous[r][c];
		while (current[0] != startRow || current[1] != startCol) {
			current = previous[r][c];
			path[0][moves] = current; 
			moves+=1;
			r = current[0];
			c = current[1];			
		}
		
		moves-=1;
		if (moves == 0) {
			moves+=1;
		}
		LinkedList<String> shortestPathDirections = new LinkedList<String>();
		int[] dr = {-1, 1, 0, 0}; //north south east west
		int[] dc = {0, 0, 1, -1};
		for (int i = 0; i < moves; i++) {
			if (path[0][i][1]+1 == path[0][i+1][1]) {
				shortestPathDirections.addFirst("LEFT");
			}
			else if (path[0][i][1]-1 == path[0][i+1][1]) {
				shortestPathDirections.addFirst("RIGHT");
			}
			else if (path[0][i][0]+1 == path[0][i+1][0]) {
				shortestPathDirections.addFirst("UP");
			}
			else if (path[0][i][0]-1 == path[0][i+1][0]) {
				shortestPathDirections.addFirst("DOWN");
			}
		}
		
		for (int i = 0; i < shortestPathDirections.size(); i++) {
			System.out.println(shortestPathDirections.get(i));
		}
		return shortestPathDirections;
	}
	
}
