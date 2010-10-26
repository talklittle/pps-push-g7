package push.sim;

import push.sim.Player.Direction;

public class Move {
	private int x;
	private int y;
	private Direction direction;
	public int getNewX()
	{
		return x + direction.getDx();
	}
	public int getNewY()
	{
		return y + direction.getDy();
	}
	public Move(int x, int y, Direction direction) {
		super();
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Direction getDirection() {
		return direction;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[Move from ("+x+","+y+") to ("+getNewX()+","+getNewY()+") via "+direction.toString()+"]";
	}
}
