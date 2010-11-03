package push.g7;

import java.awt.Point;

import push.sim.GameEngine;
import push.sim.Player.Direction;

public class PointProperty {
	int x;
	int y;	
	int coins;
	int status;
	Direction home;

	int score;
	
	PointProperty(int x, int y,int[][] a)
	{
		this.x =x;
		this.y = y;
		setStatus();
		if(this.status == 1)
		{
		setCoins(a);
		setIdAndScore(a);
		}
	}
	private void setCoins(int[][] board)
	{
		coins = board[y][x];
	}
	private void setStatus()
	{
		if(GameEngine.isInBounds(x, y)) status=1;
		else status = 0;
	}

	public void setIdAndScore(int[][] board){
		
		// Cell is (i,j) but indexed (j,i)
		Direction closest = null;
		Direction closest2 = null;
		int closestn = 8;
		int closestn2 = 8;
		Point conv = new Point(x, y);
		for (Direction d : Direction.values()) {
			int s = GameEngine.getDistance(d.getHome(),conv);
			if(s == 100)
				s = -1;
			if (s <= closestn) {
				closest2 = closest;
				closestn2 = closestn;
				closest = d;
				closestn = s;
			} else if (s <= closestn2) {
				closest2 = d;
				closestn2 = s;
			}
		}
			score= (closestn2 - closestn);
			home = closest;
		
		
	}
	
	public int getCoins()
	{
		return	coins;
	}
	
	public int getStatus()
	{
		return	status;
	}

	public int getScore()
	{
		return	score;
	}
	
	public Direction getDirection()
	{
		return	home;
	}
	
}
