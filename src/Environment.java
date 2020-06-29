import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

//REFERENCE https://www3.ntu.edu.sg/home/ehchua/programming/java/javagame_tictactoe_ai.html

public class Environment extends JPanel implements ActionListener, KeyListener {

	private JFrame frame = new JFrame("A WileD Connect Game");
	
	private Cell[][] board = new Cell[6][7];
	private boolean gameOver = false;
	private Timer timer = new Timer(1, this);
	private Timer delay = new Timer(1, this);
	private boolean player1Turn = true;
	private final int simulationGames = 1000000000;
	
	private int[] aiWins = new int[2];

	public Environment(){
		Container can = frame.getContentPane();
		can.add(this);
		
		setPreferredSize(new Dimension(300,300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(this);
		frame.pack();
		frame.setTitle("WileD Four");
		aiWins[0] = 0;
		aiWins[1] = 0;
		resetGame();
		frame.setVisible(true);
		
		timer.start();
	}
	
	public void runAI1(){
		haasAI2(Cell.PLAYER1);
	}
	public void runAI2(){
		superRandomAI(Cell.PLAYER2);
	}
	
	public void basicAI(int owner){
		//System.out.println("--------BASIC AI MAKING A MOVE--------");
		
		//find possible moves
		ArrayList<String> points = getPoints();
		
		//immediate win
		//detect Spots to Win REQUIRED FIRST 
		for(String p : points){
			int comma = p.indexOf(",");
			int r = Integer.parseInt(p.substring(0,comma));
			int c = Integer.parseInt(p.substring(comma + 1,p.length()));
			board[r][c].setOwner(owner);
			if(checkGameOver()){
				return;
			}
			else {
				board[r][c].setOwner(Cell.UNOCCUPIED);
			}
		}
		
		//immediate loss
		//detect Spots to Lose REQUIRED SECOND
		for(String p : points){
			int comma = p.indexOf(",");
			int r = Integer.parseInt(p.substring(0,comma));
			int c = Integer.parseInt(p.substring(comma + 1,p.length()));
			int other = Cell.UNOCCUPIED;
			if(owner == Cell.PLAYER1)other = Cell.PLAYER2;
			else other = Cell.PLAYER1;
			board[r][c].setOwner(other);
			if(checkGameOver()){
				board[r][c].setOwner(owner);
				return;
			}
			else {
				board[r][c].setOwner(Cell.UNOCCUPIED);
			}
		}
		
		//if no immediate block or win moves, go middle
		if(board[board.length - 1][board[0].length/2].getOwner() == Cell.UNOCCUPIED){
			board[board.length-1][board[0].length/2].setOwner(owner);
			return;
		}
		else if (board[board.length - 1][board[0].length/2 - 1].getOwner() == Cell.UNOCCUPIED){
			board[board.length - 1][board[0].length/2 - 1].setOwner(owner);
			return;
		}
		
	
		//System.out.println("Evaluating Space Value because no immediate action needed");
		int[] pointValue = new int[points.size()];
		int biggestVal = 0;
		int indexBiggestVal = -1;
		for(int i = 0; i < pointValue.length; i++){
			String p = points.get(i);
			int comma = p.indexOf(",");
			int r = Integer.parseInt(p.substring(0,comma));
			int c = Integer.parseInt(p.substring(comma + 1,p.length()));
			pointValue[i] = getSpaceValue(r, c, owner);
			if(pointValue[i] >= biggestVal){
				biggestVal = pointValue[i];
				indexBiggestVal = i;
			}
		}
		
		String p = points.get(indexBiggestVal);
		int comma = p.indexOf(",");
		int r = Integer.parseInt(p.substring(0,comma));
		int c = Integer.parseInt(p.substring(comma + 1,p.length()));
		//System.out.println("Setting owner at: " + r + "," + c + " with a value of " + biggestVal );
		board[r][c].setOwner(owner);
		
		
		//System.out.println("--------BASIC AI Finished A MOVE--------");
	}
	public int getSpaceValue(int r, int c, int owner){
		//System.out.println("Space value: (" + r+","+c+") = "+ horSpaceValue(r,c,owner) +", " +verSpaceValue(r, c, owner)+", " +diaLeftSpaceValue(r, c, owner)+", "+ diaRightSpaceValue(r,c,owner));
		return horSpaceValue(r,c,owner) + verSpaceValue(r, c, owner) + diaLeftSpaceValue(r, c, owner) + diaRightSpaceValue(r,c,owner);
	}

	public int diaLeftSpaceValue(int r, int c, int owner){
		int other = 1;
		if (owner == 1) other = 2;
		int value = 0;
		
		if(board[r][c].getOwner() != Cell.UNOCCUPIED)
			return 0;
		
		int dR = r;
		int dC = c;
		int cStop = 0;
		while(dR > 0 && dC > 0 && cStop != 4){
			dR--;
			dC--;
			cStop++;
		}
		
		while(dR + 3 < board.length && dC + 3 < board[0].length){
			if(board[dR][dC].getOwner() != other && board[dR + 1][dC + 1].getOwner() != other && board[dR+2][dC+2].getOwner() != other && board[dR+3][dC+3].getOwner() != other ){
				value++;
			}
			if (board[dR][dC].getOwner() == owner){
				value++;
			}
			if( board[dR + 1][dC + 1].getOwner()  == owner ){
				value+=2;
			}
			if(board[dR+2][dC+2].getOwner()== owner  ){
				value+=2;
			}
			if(board[dR+3][dC+3].getOwner()  == owner ){
				value++;
			}
			dR++;
			dC++;
		}
		
		
		return value;
	}
	public int diaRightSpaceValue(int r, int c, int owner){
		int other = 1;
		if (owner == 1) other = 2;
		int value = 0;
		
		if(board[r][c].getOwner() != Cell.UNOCCUPIED)
			return 0;
		
		int dR = r;
		int dC = c;
		int cStop = 0;
		
		while(dR > 0 && dC < board[0].length - 1 && cStop != 4){
			dR--;
			dC++;
			cStop++;
		}
		
		while(dR + 3 < board.length && dC - 3 >= 0){
			
			if(board[dR][dC].getOwner() != other && board[dR + 1][dC - 1].getOwner() != other && board[dR+2][dC-2].getOwner() != other && board[dR+3][dC-3].getOwner() != other ){
				value++;
			}
			if (board[dR][dC].getOwner() == owner){
				value++;
			}
			if( board[dR + 1][dC - 1].getOwner()  == owner ){
				value+=2;
			}
			if(board[dR+2][dC-2].getOwner()== owner  ){
				value+=2;
			}
			if(board[dR+3][dC-3].getOwner()  == owner ){
				value++;
			}
			dR++;
			dC--;
		}
		
		
		return value;
	}
	public int horSpaceValue(int r, int c, int owner){
		int other = 1;
		if (owner == 1) other = 2;
		int value = 0;
		
		if(board[r][c].getOwner() != Cell.UNOCCUPIED)
			return 0;

		//horizontal win
		int left = c - 3;
		if(left < 0) 
			left = 0;
		int right = c + 1;
		if(right > board[0].length)
			right = board[0].length;
		
		//System.out.println("LEFT: "+ left);
		for(int x = left; x < right; x++){
			
			if (board[r][left].getOwner() != other && board[r][left + 1].getOwner() != other && board[r][left + 2].getOwner() != other && board[r][left + 3].getOwner() != other ){
				value++;
			}
			if (board[r][left].getOwner() == owner){
				value++;
			}
			if( board[r][left + 1].getOwner()  == owner ){
				value+=2;
			}
			if(board[r][left + 2].getOwner()== owner  ){
				value+=2;
			}
			if(board[r][left + 3].getOwner()  == owner ){
				value++;
			}
		}
		
		return value;
	}
	public int verSpaceValue(int r, int c, int owner){
		int other = 1;
		if (owner == 1) other = 2;
		int value = 0;
		
		if(board[r][c].getOwner() != Cell.UNOCCUPIED)
			return 0;

		//vertical wins
		int top = c - 3;
		if (top < 0) top = 0;
		int bot = c + 1;
		if(bot > board.length)bot = board.length;
		
		
		for(int x = top; x < bot - 3; x++){
			
			if (board[top][c].getOwner() != other && board[top + 1][c].getOwner() != other && board[top + 2][c].getOwner() != other && board[top+3][c].getOwner() != other ){
				value++;
			}
			if (board[top][c].getOwner() == owner){
				value++;
			}
			if(board[top + 1][c].getOwner() == owner ){
				value+=2;
			}
			if(board[top + 2][c].getOwner() == owner  ){
				value+=2;
			}
			if(board[top+3][c].getOwner() == owner ){
				value++;
			}
		}
	
		
		
		
		return value;
	}
	
	
	
	public int[][] getBoardState(){
		int[][] oldCellState = new int[6][7];
		for(int r  = 0; r < oldCellState.length; r++){
			for(int c = 0; c < oldCellState[0].length; c++){
				oldCellState[r][c] = board[r][c].getOwner();
			}
		}
		return oldCellState;
	}
	public void resetBoardState(int[][] oldCellState){
		for(int r  = 0; r < oldCellState.length; r++){
			for(int c = 0; c < oldCellState[0].length; c++){
				board[r][c].setOwner(oldCellState[r][c]);
			}
		}
	}
	
	public ArrayList<String> getPoints(){
		ArrayList<String> points = new ArrayList<String>();
		for(int r = board.length - 1; r >= 0; r--){
			for(int c = board[0].length - 1; c >= 0; c--){
				if(canPlaceAtCell(r, c)){
					points.add(r+","+c);
				}
			}
		}
		return points;
	}

	
	public void humanPlayer(int owner) {
		System.out.print("Column Placement (1-" + board[0].length + "): ");
		Scanner sc = new Scanner(System.in);
		boolean keepTrying = true;
		while(keepTrying) {
			try {
				int colPlacement = Integer.parseInt(sc.nextLine()) - 1;
				while(colPlacement < 0 || colPlacement > board.length) {
					System.out.print("\nInvalid index, try again: ");
					colPlacement = Integer.parseInt(sc.nextLine()) - 1;
				}
				
				for(int row = board.length - 1; row >= 0; row--) {
					if(canPlaceAtCell(row, colPlacement)) {
						board[row][colPlacement].setOwner(owner);
						keepTrying = false;
						return;
					}
				}
				
				System.out.println("Could not place at col, try again: ");
				
			} catch(NumberFormatException nfe) {
				System.out.print("\nInvalid entry, try again: ");
			}
		}
	}
	
	// My AI
	public void haasAI(int owner) {
		int enemyOwner = (owner == Cell.PLAYER1) ? Cell.PLAYER2 : Cell.PLAYER1;
		if(firstMove()) {
			distributionFocus(owner);
			return;
		}
		
		//Looks for easy win
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[row].length; col++) {
				if(canPlaceAtCell(row, col)) {
					board[row][col].setOwner(owner);
					if(checkGameOver()) return;
					board[row][col].setOwner(Cell.UNOCCUPIED);
				}
			}
		}
		
		//Looks for block
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[row].length; col++) {
				if(canPlaceAtCell(row, col)) {
					board[row][col].setOwner(enemyOwner);
					if(checkGameOver()) {
						board[row][col].setOwner(owner);
						return;
					}
					board[row][col].setOwner(Cell.UNOCCUPIED);
				}
			}
		}
		
		ArrayList<int[]> open = new ArrayList<int[]>();
		for(int row = 0; row < board.length; row++)
			for(int col = 0; col < board[row].length; col++)
				if(board[row][col].getOwner() == Cell.UNOCCUPIED)
					open.add(new int[]{row, col});

		int winnableCol = -1;
		int distance = Integer.MAX_VALUE;
		
		for(int i = 0; i < open.size(); i++) {
			int row = open.get(i)[0];
			int col = open.get(i)[1];
			board[row][col].setOwner(owner);
			if(checkGameOver()) {
				int tmpWinnableCol = col;
				int tmpDistance;
				int tmp = board.length-1;
				for(int r = board.length - 1; r >= 0; r--)
					if(board[r][tmpWinnableCol].getOwner() == Cell.UNOCCUPIED) break;
					else tmp--;
				tmpDistance = tmp - row;
				if(tmpDistance < distance) {
					winnableCol = tmpWinnableCol;
					distance = tmpDistance;
				}
			}
			board[row][col].setOwner(Cell.UNOCCUPIED);
		}
		
		if(winnableCol != -1) {
			placeAt(winnableCol, owner);
			return;
		}
		
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[row].length; col++) {
				if(canPlaceAtCell(row, col)) {
					board[row][col].setOwner(owner);
					for(int r = board.length - 1; r >= 0; r--) {
						for(int c = 0; c < board[r].length; c++) {
							if(canPlaceAtCell(r, c)) {
								board[r][c].setOwner(enemyOwner);
								if(checkGameOver()) {
									board[row][col].setOwner(Cell.UNOCCUPIED);
									board[r][c].setOwner(Cell.UNOCCUPIED);
									int attempts = 0;
									int a, b;
									do {
										a = (int)(board.length * Math.random());
										b = (int)(board[a].length * Math.random());
										attempts++;
									} while((!canPlaceAtCell(a, b) || a == row || b == col) && attempts < 20);
									if(!canPlaceAtCell(a, b)) {
										do {
											a = (int)(board.length * Math.random());
											b = (int)(board[a].length * Math.random());
											attempts++;
										} while(!canPlaceAtCell(a, b));
									}
									board[a][b].setOwner(owner);
									return;
								}
								board[r][c].setOwner(Cell.UNOCCUPIED);
							}
						}
					}
					board[row][col].setOwner(Cell.UNOCCUPIED);
				}
			}
		}
		distributionFocus(owner);
	}
	
	public void haasAI2(int owner) {
		//Guarantee easy win
		for(int r = 0; r < board.length; r++)
			for(int c = 0; c < board[r].length; c++) {
				if(canPlaceAtCell(r, c)) {
					board[r][c].setOwner(owner);
					if(checkGameOver())
						return;
					board[r][c].setOwner(Cell.UNOCCUPIED);
				}
			}
		
		//Guarantee easy block
		for(int r = 0; r < board.length; r++)
			for(int c = 0; c < board[r].length; c++) {
				if(canPlaceAtCell(r, c)) {
					board[r][c].setOwner(owner == Cell.PLAYER1 ? Cell.PLAYER2 : Cell.PLAYER1);
					if(checkGameOver()) {
						board[r][c].setOwner(owner);
						return;
					}
					board[r][c].setOwner(Cell.UNOCCUPIED);
				}
			}
		
		//long init = System.currentTimeMillis();
		int[] result = minimaxAB(5, owner, owner, Integer.MIN_VALUE, Integer.MAX_VALUE);
		//System.out.println("Time to decide move: " + ((System.currentTimeMillis() - init)/1000.0) + " seconds");
		board[result[1]][result[2]].setOwner(owner);
	}
	
	public int[] minimaxAB(int depth, int owner, int player, int alpha, int beta) {
		int otherPlayer = player == Cell.PLAYER1 ? Cell.PLAYER2 : Cell.PLAYER1;
		//int best = owner == player ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		int current = 0;
		int bestRow = -1;
		int bestCol = -1;
		
		ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
		for(int row = 0; row < board.length; row++)
			for(int col = 0; col < board[row].length; col++)
				if(canPlaceAtCell(row, col))
					possibleMoves.add(new int[]{row,col});
		
		if(checkGameOver() || depth == 0) {
			return new int[]{evaluateBoard(player), bestRow, bestCol};
		}
		else {
			for(int[] moves : possibleMoves) {
				board[moves[0]][moves[1]].setOwner(owner);
				if(owner == player) {
					current = minimaxAB(depth-1, otherPlayer, player, alpha, beta)[0];
					if(current > alpha) {
						alpha = current;
						bestRow = moves[0];
						bestCol = moves[1];
					}
				}
				else {
					current = minimaxAB(depth-1, player, player, alpha, beta)[0];
					if(current < beta) {
						beta = current;
						bestRow = moves[0];
						bestCol = moves[1];
					}
				}
				
				board[moves[0]][moves[1]].setOwner(Cell.UNOCCUPIED);
				if(alpha >= beta) break;
			}
		}
		
		return new int[]{owner == player ? alpha : beta, bestRow, bestCol};
	}
	
	public int evaluateBoard(int owner) {
		int score = 0;
		//Horizontal
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[row].length - 3; col++) {
				Cell[] arr = new Cell[4];
				for(int i = 0; i < 4; i++)
					arr[i] = board[row][col+i];
				score += evalAtLine(arr, owner);
			}
		}
		
		//Vertical
		for(int col = 0; col < board[0].length; col++) {
			for(int row = 0; row < board.length - 3; row++) {
				Cell[] arr = new Cell[4];
				for(int i = 0; i < 4; i++) 
					arr[i] = board[row+i][col];
				score += evalAtLine(arr, owner);
			}
		}
		
		//Diag (top left to bottom right)
		for(int row = 0; row < board.length - 3; row++) {
			for(int col = 0; col < board[row].length - 3; col++) {
				Cell[] arr = new Cell[4];
				for(int i =0; i < 4; i++)
					arr[i] = board[row+i][col+i];
				score += evalAtLine(arr, owner);
			}
		}
		
		//Diag (top right to bottom left)
		for(int row = 0; row < board.length - 3; row++) {
			for(int col = board[row].length-1; col >= 3; col--) {
				Cell[] arr = new Cell[4];
				for(int i =0; i < 4; i++)
					arr[i] = board[row+i][col-i];
				score += evalAtLine(arr, owner);
			}
		}
		
		return score;
	}
	
	public int evalAtLine(Cell[] moves, int owner) {
		int other = owner == Cell.PLAYER1 ? Cell.PLAYER2 : Cell.PLAYER1;
		int score = -1;
	
		//First Cell
		if(moves[0].getOwner() == owner)
			score = 1;
		else if(moves[0].getOwner() == other)
			score = -1;
		
		//Second cell
		if(moves[1].getOwner() == owner) {
			if(score == 1)
				score = 10;
			else if(score == -1)
				return 0;
			else
				score = 1;
		}
		else if(moves[1].getOwner() == other) {
			if(score == -1)
				score = -10;
			else if(score == 1)
				return 0;
			else
				score = -1;
		}
		
		//Third cell
		if(moves[2].getOwner() == owner) {
			if(score > 0)
				score *= 10;
			else if(score < 0)
				return 0;
			else
				score = 1;
		}
		else if(moves[2].getOwner() == other) {
			if(score < 0)
				score *= 10;
			else if(score > 1)
				return 0;
			else
				score = -1;
		}
		
		//Fourth cell
		if(moves[3].getOwner() == owner) {
			if(score > 0)
				score *= 10;
			else if(score < 0)
				return 0;
			else
				score = 1;
		}
		else if(moves[3].getOwner() == other) {
			if(score < 0)
				score *= 10;
			else if(score > 1)
				return 0;
			else
				score = -1;
		}
		return score;
	}
	
	public int getWhoWon() {
		if(checkGameOver())
			if(player1Turn) return Cell.PLAYER1;
			else return Cell.PLAYER2;
		return -1;
	}
	
	public void placeAt(int col, int owner) {
		for(int row = board.length-1; row >= 0; row--) {
			if(canPlaceAtCell(row, col)) {
				board[row][col].setOwner(owner);
				return;
			}
		}
	}
	
	public boolean firstMove() {
		for(int row = 0; row < board.length; row++)
			for(int col = 0; col < board.length; col++) 
				if(board[row][col].getOwner() != Cell.UNOCCUPIED)
					return false;
		return true;
	}
	
	//
	//
	//
	//END OF MY CODE
	
	public void superRandomAI(int owner){
		double d = Math.random();
		if(d < .33)
			distributionFocus(owner);
		else if (d < .66)
			randomAI(owner);
		else
			centerFocus(owner);
	}
	public void distributionFocus(int owner){
		int d1, d2;
		do{
			d1 = (int)((3 - 0 + 1)*Math.random() + 0);
			d2 = (int)((3 - 0 + 1)*Math.random() + 0);
			int sum = d1 + d2;
			
			for(int r = board.length - 1; r >= 0; r--){
				if(canPlaceAtCell(r, sum)){
					board[r][sum].setOwner(owner);
					return;
				}
			}
		}
		while(true);
	}
	public void centerFocus(int owner){
		int mid = board.length / 2;
		
		for(int i = mid; i >= 0; i++){
			for(int r = board.length - 1; r >= 0; r--){
				if(canPlaceAtCell(r, i)){
					board[r][i].setOwner(owner);
					return;
				}
				if(canPlaceAtCell(r, board[r].length - 1 - i)){
					board[r][board[r].length - 1 - i].setOwner(owner);
					return;
				}
			}
		}
		
		int a, b;
		do{
			a = (int)((5 - 0 + 1)*Math.random() + 0);
			b = (int)((6 - 0 + 1)*Math.random() + 0);
		}
		while(!canPlaceAtCell(a,b));
		board[a][b].setOwner(owner);
	}
	
	public void randomAI(int owner){
		int a, b;
		do{
			a = (int)((5 - 0 + 1)*Math.random() + 0);
			b = (int)((6 - 0 + 1)*Math.random() + 0);
		}
		while(!canPlaceAtCell(a,b));
		board[a][b].setOwner(owner);
	}
	
	public boolean checkGameOver(){
		//all spaces are occupied
		boolean hasUnoccupied = false;
		for(Cell row[] : board)
			for(Cell c : row)
				if(c.getOwner() == Cell.UNOCCUPIED)
					hasUnoccupied = true;
			
		if(!hasUnoccupied)
			return true;
		
		//horizontal check
		for(int r = 0; r < board.length; r++){
			for(int c = 0; c < board[0].length - 3; c++){
				if(board[r][c].getOwner() != Cell.UNOCCUPIED)
					if(board[r][c].getOwner() == board[r][c+1].getOwner() && board[r][c].getOwner() == board[r][c+2].getOwner() && board[r][c].getOwner() == board[r][c + 3].getOwner())
						return true;
			}
		}
		
		//vertical check
		for(int r = 0; r < board.length-3; r++){
			for(int c = 0; c < board[0].length; c++){
				if(board[r][c].getOwner() != Cell.UNOCCUPIED)
					if(board[r][c].getOwner() == board[r+1][c].getOwner() && board[r][c].getOwner() == board[r+2][c].getOwner() && board[r][c].getOwner() == board[r+3][c].getOwner())
						return true;
			}
		}
		
		//diagonal check (top left to bottom right)
		for(int r = 0; r < board.length - 3; r++){
			for(int c = 0; c < board[0].length - 3; c++){
				if(board[r][c].getOwner() != Cell.UNOCCUPIED)
					if(board[r][c].getOwner() == board[r+1][c+1].getOwner() && board[r][c].getOwner() == board[r+2][c+2].getOwner() && board[r][c].getOwner() == board[r+3][c + 3].getOwner())
						return true;
			}
		}
		
		//diagonal check (top right to bottom left)
		for(int r = board.length - 1; r >= 3; r--){
			for(int c = 0; c < board[0].length - 3; c++){
				if(board[r][c].getOwner() != Cell.UNOCCUPIED)
					if(board[r][c].getOwner() == board[r-1][c+1].getOwner() && board[r][c].getOwner() == board[r-2][c+2].getOwner() && board[r][c].getOwner() == board[r-3][c + 3].getOwner())
						return true;
			}
		}
		
		return false;
	}
	public void resetGame(){
		gameOver = false;
		makeBoard();
	}
	public void makeBoard(){
		int locY = 0;
		int dX = getWidth()/board[0].length;
		int dY = (getHeight()-50)/board.length;
		for(int r = 0; r < board.length; r++){
			int locX = 0;
			for(int c = 0; c < board[r].length; c++){
				board[r][c] = new Cell(locX,locY,dX,dY);
				locX += dX;
			}
			locY += dY;
		}
		repaint();
	}
	public boolean canPlaceAtCell(int r, int c){
		if(board[r][c].getOwner() != Cell.UNOCCUPIED)	//if already owned
			return false;
		
		if(r == board.length - 1) //if first cell on bottom
			return true;
		
		if(board[r + 1][c].getOwner() == Cell.UNOCCUPIED){ //if the one below you is not occupied
			return false;
		}
		
		return true;
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for(Cell[] row : board)
			for(Cell c : row)
				c.draw(g2d);
		
		Font prev = g2d.getFont();
		for(int i = 0; i < board[0].length; i++) {
			String str = "" + (i+1);
			g2d.setFont(g2d.getFont().deriveFont(16f).deriveFont(Font.BOLD));
			FontMetrics fm = g2d.getFontMetrics();
			g2d.drawString(str, (board[0][i].getX() + board[0][i].getW()/2) - fm.stringWidth(str)/2, 
					(board[0][i].getY() + board[0][i].getH())/2 + fm.getHeight()/2);
		}
		g2d.setFont(prev);
		String s = "AI 2: " + aiWins[1];
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString("AI 1: " + aiWins[0], 10, getHeight() - fm.getHeight());
		g2d.drawString(s, getWidth() - fm.stringWidth(s) - 10, getHeight() - fm.getHeight());
		
		int totals = aiWins[0] + aiWins[1];
		if (totals == 0)totals = 1;
		double percent1 = ((int)((double)aiWins[0]/totals * 10000))/100.0;
		double percent2 = ((int)((double)aiWins[1]/totals * 10000))/100.0;
		
		s = "AI 2: " + percent2 + "%";
		g2d.drawString("AI 1: " + percent1 + "%", 10, getHeight() - 2*fm.getHeight());
		g2d.drawString(s, getWidth() - fm.stringWidth(s) - 10, getHeight() - 2*fm.getHeight());
	}
	
	public void setWinningCells(){
		//horizontal check
		for(int r = 0; r < board.length; r++){
			for(int c = 0; c < board[0].length - 3; c++){
				if(board[r][c].getOwner() != Cell.UNOCCUPIED)
					if(board[r][c].getOwner() == board[r][c+1].getOwner() && board[r][c].getOwner() == board[r][c+2].getOwner() && board[r][c].getOwner() == board[r][c + 3].getOwner()){
						board[r][c].setWinningTile(true);
						board[r][c+1].setWinningTile(true);
						board[r][c+2].setWinningTile(true);
						board[r][c+3].setWinningTile(true);
						return;
					}
			}
		}
		
		//vertical check
		for(int r = 0; r < board.length-3; r++){
			for(int c = 0; c < board[0].length; c++){
				if(board[r][c].getOwner() != Cell.UNOCCUPIED)
					if(board[r][c].getOwner() == board[r+1][c].getOwner() && board[r][c].getOwner() == board[r+2][c].getOwner() && board[r][c].getOwner() == board[r+3][c].getOwner()){
						board[r][c].setWinningTile(true);
						board[r+1][c].setWinningTile(true);
						board[r+2][c].setWinningTile(true);
						board[r+3][c].setWinningTile(true);
						return;
				}
			}
		}
		
		//diagonal check (top left to bottom right)
		for(int r = 0; r < board.length - 3; r++){
			for(int c = 0; c < board[0].length - 3; c++){
				if(board[r][c].getOwner() != Cell.UNOCCUPIED)
					if(board[r][c].getOwner() == board[r+1][c+1].getOwner() && board[r][c].getOwner() == board[r+2][c+2].getOwner() && board[r][c].getOwner() == board[r+3][c + 3].getOwner()){
						board[r][c].setWinningTile(true);
						board[r+1][c+1].setWinningTile(true);
						board[r+2][c+2].setWinningTile(true);
						board[r+3][c+3].setWinningTile(true);
						return;
					}
			}
		}
		
		//diagonal check (top right to bottom left)
		for(int r = board.length - 1; r >= 3; r--){
			for(int c = 0; c < board[0].length - 3; c++){
				if(board[r][c].getOwner() != Cell.UNOCCUPIED) {
					if(board[r][c].getOwner() == board[r-1][c+1].getOwner() && board[r][c].getOwner() == board[r-2][c+2].getOwner() && board[r][c].getOwner() == board[r-3][c + 3].getOwner()){
						board[r][c].setWinningTile(true);
						board[r-1][c+1].setWinningTile(true);
						board[r-2][c+2].setWinningTile(true);
						board[r-3][c+3].setWinningTile(true);
						return;
					}
				}
			}
		}
		
	}
	
	public void actionPerformed(ActionEvent a) {
		if(a.getSource() == timer){
			if(!gameOver)
				if(player1Turn)
					runAI1();
				else 
					runAI2();
				
			repaint();
			gameOver = checkGameOver();
			
			if(gameOver){
				if(player1Turn){
					aiWins[0]++;
				}
				else{
					aiWins[1]++;
				}
				timer.stop();
				delay.start();
				setWinningCells();
			}
			
			player1Turn = !player1Turn;
			
			repaint();
		}
		if(a.getSource() == delay){
			resetGame();
			delay.stop();
			timer.start();
		}
		
		if(aiWins[0] + aiWins[1] >= simulationGames)
			timer.stop();
	}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_P) {
			if(timer.isRunning())
				timer.stop();
			else
				timer.start();
		}
	}
	
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	public static void main(String[] args){
		new Environment();
	}
}