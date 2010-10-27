package push.g7;

import java.util.*;

import push.sim.Move;
import push.sim.Player.Direction;

public class RecognizeEnemyAndAlly {
	HashMap<Direction, ArrayList<Move>> players;
	ArrayList<Direction> ally = new ArrayList<Direction>();
	ArrayList<Direction> enemy = new ArrayList<Direction>();
	
	public ArrayList<Direction> getAlly(Direction myCorner) {
		setAlly(myCorner);
		return ally;
	}
	
	public void setAlly(Direction myCorner)
	{
		Direction FirstAlly=myCorner.getRelative(1);
		ally.add(FirstAlly);
	}
	
	public ArrayList<Direction> getEnemy(Direction myCorner) {
		setEnemy(myCorner);
		return enemy;
	}
	
	public void setEnemy(Direction myCorner)
	{
		Direction FirstEnemy=myCorner.getRight();
		enemy.add(FirstEnemy);
	}
}
