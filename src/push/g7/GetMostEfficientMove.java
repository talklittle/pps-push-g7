package push.g7;
import push.sim.*;
import push.sim.Player.Direction;

public class GetMostEfficientMove {
	//by default, we have valid move to help this ally.
	int NoValidHelpForThisAlly=0;
	int NoValidHurtForThisEnemy=0;
	Move mostHelpfulMove = null;
	Move hurtestMove = null;
	
	GetMostEfficientMove(int status, Direction myCorner, Direction to, int[][] board)
	{
		//status = 0 represents it's our ally. status =1 represents it's our enemy.
		if(status==0) {mostHelpfulMove=getBenefitMove(myCorner, to, board);}
		else if(status ==1){hurtestMove = getHurtMove(myCorner, to, board);}
	}
	//always using the most efficient way to help or hurt player.
	public Move getBenefitMove(Direction myCorner, Direction to, int[][] board)
	{
		int benefitScore1 = 0;
		int benefitScore2 = 0;
		Move helpWithoutHurting = null;
		Move helpWithHurting = null;
		for(int x=0; x<StaticVariable.MAX_X;x++)
			for (int y=0; y<StaticVariable.MAX_Y; y++)
				for (Direction d : Direction.values())
				{ 
					MovePointInDirection m = new MovePointInDirection(x,y, board, d,myCorner);
					//try to help ally without hurting others 
					if(m.validStatus == 1 && m.benefitPlayer == to && m.hurtPlayer == null && m.benefitScore> benefitScore1 )
					{
						benefitScore1 = m.benefitScore;
						helpWithoutHurting = new Move(x,y,d);
					}
					//if can't help ally without hurting others, hurt others and protect myself.
					if(m.validStatus == 1 && m.benefitPlayer == to && m.hurtPlayer != myCorner && m.benefitScore> benefitScore2 )
					{
						benefitScore2 = m.benefitScore;
						helpWithHurting = new Move(x,y,d);
					}					
				}
		if (helpWithoutHurting != null){return helpWithoutHurting;}
		else if(helpWithHurting != null){return helpWithHurting;}
		else 
		{	//not valid move for this ally.
			NoValidHelpForThisAlly =1;
			return new Move(0, 0, Direction.E);
		}

	}
	
	public Move getHurtMove(Direction myCorner, Direction to, int[][] board)
	{
		int hurtScore1 = 0;
		int hurtScore2 = 0;
		Move HurtWithoutHelping = null;
		Move HurtWithHelping = null;
		for(int x=0; x<StaticVariable.MAX_X;x++)
			for (int y=0; y<StaticVariable.MAX_Y; y++)
				for (Direction d : Direction.values())
				{ 
					MovePointInDirection m = new MovePointInDirection(x,y, board, d,myCorner);
					//try to hurt enemy without helping others 
					if(m.validStatus == 1 && m.hurtPlayer == to && m.benefitPlayer == null && m.hurtScore> hurtScore1 )
					{
						hurtScore1 = m.hurtScore;
						HurtWithoutHelping = new Move(x,y,d);
					}
					//if can't hurt enemy without helping others, help others.
					if(m.validStatus == 1 && m.hurtPlayer == to && m.hurtScore> hurtScore2 )
					{
						hurtScore2 = m.hurtScore;
						HurtWithHelping = new Move(x,y,d);
					}					
				}
		if (HurtWithoutHelping != null){return HurtWithoutHelping;}
		else if(HurtWithHelping != null){return HurtWithHelping;}
		else 
		{
			NoValidHurtForThisEnemy =1;
			return new Move(0, 0, Direction.E);
		}

	}
}
