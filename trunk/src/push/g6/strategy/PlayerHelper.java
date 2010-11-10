package push.g6.strategy;

import push.sim.Player.Direction;

public class PlayerHelper implements Comparable<PlayerHelper>{
	private Direction direction;
	private int helpIndex;
	
	public PlayerHelper(Direction direction,int helpIndex){
		this.direction=direction;
		this.helpIndex=helpIndex;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public Direction getDirection() {
		return direction;
	}
	public void setHelpIndex(int helpIndex) {
		this.helpIndex = helpIndex;
	}
	public int getHelpIndex() {
		return helpIndex;
	}

	@Override
	public int compareTo(PlayerHelper other) {
		if(other.getHelpIndex() > this.helpIndex)
            return -1;
    else if (other.getHelpIndex() < this.helpIndex)
            return 1;
    else
            return 0;

	}
	
}
