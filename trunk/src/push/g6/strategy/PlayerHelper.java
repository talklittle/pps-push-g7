package push.g6.strategy;

import push.sim.Player.Direction;

public class PlayerHelper implements Comparable<PlayerHelper>{
	private Direction direction;
	private int helpIndex;
	private int id;
	
	public PlayerHelper(Direction direction,int helpIndex,int id){
		this.direction=direction;
		this.helpIndex=helpIndex;
		this.id=id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + helpIndex;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerHelper other = (PlayerHelper) obj;
		if (direction != other.direction)
			return false;
		if (helpIndex != other.helpIndex)
			return false;
		if (id != other.id)
			return false;
		return true;
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
            return 1;
    else if (other.getHelpIndex() < this.helpIndex)
            return -1;
    else
            return 0;

	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
}
