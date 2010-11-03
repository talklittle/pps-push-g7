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
		logger.info("round: "+ round+"\n");
		if (round <= StaticVariable.FirstStageRound-8) ally = myCorner.getOpposite();
		else if (round>StaticVariable.FirstStageRound-8 && round <= StaticVariable.FirstStageRound-6) ally = myCorner.getRelative(-1);
		else if (round>StaticVariable.FirstStageRound-6 && round <= StaticVariable.FirstStageRound-4) ally = myCorner.getRelative(1);
		else if (round>StaticVariable.FirstStageRound-4 && round <= StaticVariable.FirstStageRound-2) ally = myCorner.getRight();
		else if (round>StaticVariable.FirstStageRound-2 && round <= StaticVariable.FirstStageRound) ally = myCorner.getLeft();
		return generalMove(board, myCorner, ally);
	}

	public Move generateHelpfulMove(int[][]board, Direction myCorner,int round) 
	{
		logger.info("round: "+ round+"\n");
		ally = myCorner.getOpposite();
		return generalMove(board, myCorner, ally);
		
	}

	public Move generateBetrayalMove(int[][]board, Direction myCorner,int round) {
		// FIXME find a betrayal move, i.e., large stacks that we can move to white spots
		logger.info("round: "+ round+"\n");
		return generateHelpfulMove(board, myCorner, round);
	}
	
	public Move generalMove(int[][]board, Direction from, Direction to)
	{	
		int n1,n2;
		logger.info("ally : " + to);
		//strong ally. use strong signal.
		//TODO:only implement part of strong signal 
		for (Direction i : Direction.values())
		{
			n1 = (int)to.getHome().getX()+i.getDx();
			n2 = (int)to.getHome().getY()+i.getDy();
			MovePointInDirection a = new MovePointInDirection(n1, n2, board, i.getOpposite(), from);
			if(a.validStatus == 1 && a.benefitPlayer == to && a.hurtPlayer != from)
			{
				logger.info("stong : benefitPlayer is :" + a.benefitPlayer+"\n");
				return new Move(n1, n2, i.getOpposite());
			}
		}
		//try to help ally without hurting others
		for(int x=0; x<StaticVariable.MAX_X;x++)
			for (int y=0; y<StaticVariable.MAX_Y; y++)
				for (Direction d : Direction.values())
			{
					MovePointInDirection m = new MovePointInDirection(x,y, board, d,from);
					if(m.validStatus == 1 && m.benefitPlayer == to && m.hurtPlayer == null)
					{
						logger.info("general : benefitPlayer is :" + m.benefitPlayer+"\n");
						return new Move (x, y, d);
					}

			}
		//if can't help ally without hurting others, hurt others and protect myself.
		for(int x=0; x<StaticVariable.MAX_X;x++)
			for (int y=0; y<StaticVariable.MAX_Y; y++)
				for (Direction d : Direction.values())
			{
					MovePointInDirection m = new MovePointInDirection(x,y, board, d,from);
					if(m.validStatus == 1 && m.benefitPlayer == to && m.hurtPlayer != from)
					{
						logger.info("general : benefitPlayer is :" + m.benefitPlayer+"\n");
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
						logger.info("no move for ally. benefitPlayer is :" + m.benefitPlayer+"\n");
						return new Move (x, y, d);
					}

			}
		
		// No moves; return a dummy Move
		return new Move(0, 0, Direction.E);

	}

}




