package push.sim;

import push.sim.Player.Direction;

public class Move {
	private int x;
	private int y;
	private Direction direction;
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return x+y+direction.hashCode();
	}
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		if(!(arg0 instanceof Move))
			return false;
		else
			return x == ((Move) arg0).x && y == ((Move) arg0).y && ((Move) arg0).direction == direction;
	}
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
