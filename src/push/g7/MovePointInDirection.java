package push.g7;
import push.sim.Player.Direction;

public class MovePointInDirection {

	int x;
	int y;
	Direction benefitPlayer;
	Direction hurtPlayer;
	int benefitScore;
	int hurtScore;
	int validStatus = 1;
	
	MovePointInDirection(int x,int y, int[][] board, Direction d,Direction myCorner)
	{
		PointProperty beforeMove = new PointProperty (x,y,board);
		PointProperty afterMove = new PointProperty (x+d.getDx(), y+d.getDy(), board);
		if(afterMove.status==0 || beforeMove.status==0 || board[y][x] ==0 || !checkMove(d,myCorner)) validStatus = 0;
		else{
			if(afterMove.home != beforeMove.home)
			{
				if(afterMove.home !=null)
				{
					benefitPlayer= afterMove.home;
					benefitScore = beforeMove.coins *afterMove.score;
				}
				if(beforeMove.home != null)
				{
					hurtPlayer = beforeMove.home;
					hurtScore = beforeMove.coins * beforeMove.score;
				}
			}else 
			{
				int a = beforeMove.coins * afterMove.score - beforeMove.coins * beforeMove.score;
				if( a <=0)
				{
					hurtPlayer = beforeMove.home;
					hurtScore = Math.abs(a);
				}else 
				{
					benefitPlayer = beforeMove.home;
					benefitScore = a;
				}
			}
		}
	}
	
	private boolean checkMove(Direction d, Direction myCorner)
	{
		if (!d.equals(myCorner.getRelative(0)) && !d.equals(myCorner.getRelative(-1)) && !d.equals(myCorner.getRelative(1)))
			return false;
		return true;
	}
}
