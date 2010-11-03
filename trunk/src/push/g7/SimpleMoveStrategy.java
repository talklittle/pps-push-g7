package push.g7;


import java.util.HashSet;
import org.apache.log4j.Logger;
import push.sim.Move;
import push.sim.Player.Direction;


public class SimpleMoveStrategy {

	private static final Logger logger = Logger.getLogger(SimpleMoveStrategy.class);
	Direction ally;
	Direction Enemy;
	HashSet<Direction> allys;


	public Move generateInitialMove(int[][]board, Direction myCorner,int round)
	{
		if (round <= StaticVariable.FirstStageRound-8) ally = myCorner.getOpposite();
		else if (round>StaticVariable.FirstStageRound-8 && round <= StaticVariable.FirstStageRound-6) ally = myCorner.getRelative(-1);
		else if (round>StaticVariable.FirstStageRound-6 && round <= StaticVariable.FirstStageRound-4) ally = myCorner.getRelative(1);
		else if (round>StaticVariable.FirstStageRound-4 && round <= StaticVariable.FirstStageRound-2) ally = myCorner.getRight();
		else if (round>StaticVariable.FirstStageRound-2 && round <= StaticVariable.FirstStageRound) ally = myCorner.getLeft();
		return generalMove(board, myCorner, ally);
	}

	public Move generateHelpfulMove(int[][]board, Direction myCorner,int round) 
	{
		return generalMove(board, myCorner, ally);
		
	}

	public Move generateBetrayalMove(int[][]board, Direction myCorner,int round) {
		
		return null;
	}
	
	public Move generalMove(int[][]board, Direction from, Direction to)
	{	
		int n1,n2;

		for (Direction i : Direction.values())
		{
			n1 = (int)to.getHome().getX()+i.getDx();
			n2 = (int)to.getHome().getY()+i.getDy();
			MovePointInDirection a = new MovePointInDirection(n1, n2, board, i.getOpposite(), from);
			if(a.validStatus == 1)
			{
				return new Move(n1, n2, i.getOpposite());
			}
		}

		for(int x=0; x<StaticVariable.MAX_X;x++)
			for (int y=0; y<StaticVariable.MAX_Y; y++)
				for (Direction d : Direction.values())
			{
					MovePointInDirection m = new MovePointInDirection(x,y, board, d,from);
					if(m.validStatus == 1 && m.benefitPlayer == to)
					{
						return new Move (x, y, d);
					}

			}
		
		// if any point is not 0, return this one with legal direction.
		for(int x=0; x<StaticVariable.MAX_X;x++)
			for (int y=0; y<StaticVariable.MAX_Y; y++)
				for (Direction d : Direction.values())
			{
					MovePointInDirection m = new MovePointInDirection(x,y, board, d,from);
					if(m.validStatus == 1)
					{
						return new Move (x, y, d);
					}

			}
		
		return null;

	}

}




