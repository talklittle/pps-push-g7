
package push.sim;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Jon Bell
 */
public abstract class Player {

	/**
	 * Defines the various enum directions for a player to push OR the location of players.
	 */
	public enum Direction{NE(0,-1,1,new Point(12,0)), NW(1,-1,-1,new Point(4,0)), W(2,0,-2,new Point(0,4)), SW(3,1,-1, new Point(4,8)),
		SE(4,1,1,new Point(12,8)), E(5,0,2,new Point(16,4));
		
	private int val, d_y, d_x;
	private Point home;
	public Point getHome() {
		return (Point) home.clone();
	}
	Direction(int v, int d_y,int d_x,Point home)
	{
		this.val = v;
		this.d_y = d_y;
		this.d_x = d_x;
		this.home=home;
	}
	Direction(int v, int d_y, int d_x)
	{
		this(v,d_y,d_x,null);
	}
	public int getDx() {
		return d_x;
	}
	public int getDy() {
		return d_y;
	}
	public int getVal() {
		return val;
	}
	public Direction getLeft()
	{
		int o = val+1;
		if(o == 6)
			o=0;
		for(Direction d : Direction.values())
		{
			if(d.val==o)
				return d;
		}
		return null;
	}
	public Direction getRight()
	{
		int o = val-1;
		if(o == -1)
			o=5;
		for(Direction d : Direction.values())
		{
			if(d.val==o)
				return d;
		}
		return null;
	}
	public Direction getOpposite()
	{
		int o = val-3;
		if(o < 0)
			o = 6+o;
		for(Direction d : Direction.values())
		{
			if(d.val == o)
				return d;
		}
		return null;
	}
	public Direction getRelative(int n)
	{
		if(n ==1)
			return getOpposite().getLeft();
		else if(n==0)
			return getOpposite();
		else if(n==-1)
			return getOpposite().getRight();
		return null;
	}
	};
	
    /**
     * Returns the name for this player
     */
    public abstract String getName();
    
    public String getNameWithTeam()
    {
    	String r = this.getClass().getName().replace("push.", "");
    	return r.substring(0, r.indexOf(".")) + " - " + getName();
    }
    /**
     * Called on the player when it is instantiated
     */
	public void Register()
	{
		//Do nothing is OK!
	}
	
	/**
	 * Called on the player when a new game starts. Passes the ID of the player, to be used 
	 * as an index into the move result array. Also passes the number of rounds (m), and the
	 * entire set of players and positions
	 */
	public abstract void startNewGame(int id, int m, ArrayList<Direction> arrayList);

	/**
	 * Called every round for you to return your move. You are passed a list of 
	 * MoveResult, indexed by player ID.
	 * 
	 * Note that you can only return 1 of 3 directions - they must be either to the top-left, top or top-right
	 * relative to your starting position
	 * @return
	 */
	public abstract Move makeMove(List<MoveResult> previousMoves);
	
	public void updateBoardState(int[][] board){
		//do nothing is ok.
	}
}

