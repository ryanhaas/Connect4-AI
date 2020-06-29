import java.awt.Color;
import java.awt.Graphics;


public class Cell {
	
	public static final int UNOCCUPIED = 0;
	public static final int PLAYER1 = 1;
	public static final int PLAYER2 = 2;
	public static final int VICTOR = 3;
	
	private int x,y,w,h;
	private int owner = 0;
	private boolean isWinningTile = false;
	
	public Cell(int x, int y, int w, int h){
		this.x=x;this.y=y;this.w=w;this.h=h;
		owner = 0;
	}
	public void setWinningTile(boolean b){
		isWinningTile = true;
	}
	public void draw(Graphics g){
		
		switch(owner){

			case PLAYER1:	g.setColor(Color.BLUE);
							g.fillOval(x, y, w, h);
							break;
						
			case PLAYER2: 	g.setColor(Color.RED);
							g.fillOval(x, y, w, h);
							break;
							

		
		}
		
		
		if(isWinningTile){
				g.setColor(Color.YELLOW);
				g.drawString("W", x + w/2-5, y + h/2 + 5);
				
		}
		g.setColor(Color.BLACK);
		g.drawRect(x,y,w,h);
		
	}
	public void setOwner(int x){
		owner = x;
	}
	public int getOwner(){
		return owner;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getW() {
		return w;
	}
	
	public int getH() {
		return h;
	}
}
