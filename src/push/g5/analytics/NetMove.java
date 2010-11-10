package push.g5.analytics;

public class NetMove {
	public int playerIncreaseIndex;
	public int pointIncrease;
	public int playerDecreaseIndex;
	public int pointDecrease;
	
	NetMove(int playerIncreaseIndex, int pointIncrease, int playerDecreaseIndex, int pointDecrease)
	{
		if(playerIncreaseIndex == -1 && playerDecreaseIndex == 1)
			System.err.println("Impossible NetMove instantiated (0 people affected)");
		this.playerIncreaseIndex = playerIncreaseIndex;
		this.pointIncrease = pointIncrease;
		this.playerDecreaseIndex = playerDecreaseIndex;
		this.pointDecrease = pointDecrease;
	}
	
	NetMove()
	{
		playerDecreaseIndex = -1;
		pointDecrease = 0;
		playerIncreaseIndex = -1;
		pointIncrease = 0;
	}
}
