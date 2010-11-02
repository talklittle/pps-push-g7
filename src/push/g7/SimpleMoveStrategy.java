package push.g7;

import java.awt.Point;
import java.util.HashSet;

import org.apache.log4j.Logger;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;
import push.g7.PushyPushelkins;

public class SimpleMoveStrategy {
	
	private static final Logger logger = Logger.getLogger(SimpleMoveStrategy.class);
	Direction ally;
	HashSet<Direction> allys;

	
	public Move generateInitialMove(int[][]board, int id,Direction myCorner,int round, int depth){
		if (round <= 2){
			ally = myCorner.getOpposite();
			
		}
			
		return null;
	}
	
	public Move generateHelpfulMove(int[][]board, int id,Direction myCorner,int round, int depth) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Move generateBetrayalMove(int[][]board, int id,Direction myCorner,int round, int depth) {
		// TODO Auto-generated method stub
		return null;
	}
	public Move generalMove(int[][]board, int id,Direction from, Direction to, String pattern, int depth)
	{	
		int n1,n2;
		if(pattern.equals("strong"))
		{
			for (Direction i : Direction.values())
			{
				n1 = (int)to.getHome().getX()+i.getDx();
				n2 = (int)to.getHome().getY()+i.getDy();
				Move newMove = new Move(n1, n2, i.getOpposite());
				MoveResult m = new MoveResult(newMove, id);
				if(isSuccessByBoundsEtc(from,m) && isSuccessByCount(board, m) && GameEngine.isValidDirectionForCellAndHome(i.getOpposite(),from))
				return newMove;
				
			}
			
		}
		int n2 = GameConfig.random.nextInt(9);
		int length = n2;
		if(length > 4)
			length=8-length;
		int offset = 4-length;
		length+=5;
		int n1 = GameConfig.random.nextInt(length);
		n1*=2;
		n1 += offset;
		if(!GameEngine.isInBounds(n1, n2))
			return generalMove(board, id, from, to, pattern, depth+1);
		
		if(getDistance(new Point(n1,n2), myCorner.getHome())<=2)
			return generalMove(board, id, from, to, pattern, depth+1);
		
		if(board != null&& board[n2][n1] == 0)
			return generalMove(board, id, from, to, pattern, depth+1);
		Direction d = ally;
 		int tries = 0;
 		while(!GameEngine.isValidDirectionForCellAndHome(d, myCorner) && tries < 10)
 		{
 			d = myCorner.getRelative(-1);
 			
 		}
		if(!GameEngine.isValidDirectionForCellAndHome(d, myCorner))
			return generalMove(board, id, from, to, pattern, depth+1);
		
		if(!GameEngine.isInBounds(n1+d.getDx(), n2+d.getDy()))
			return generalMove(board, id, from, to, pattern, depth+1);
		
		Move m = new Move(n1, n2,d);
		return m;
	}

	private boolean isSuccessByCount(int[][]board, MoveResult m) {
		// Check that there are > 0 in this position
		if (board[m.getMove().getY()][m.getMove().getX()] == 0)
			return false;
		return true;
	}
	
	private boolean isSuccessByBoundsEtc(Direction myCorner, MoveResult m) {
		// Check that we are in bounds
		if (!GameEngine.isInBounds(m.getMove().getNewX(), m.getMove().getNewY()))
			return false;
		if (!GameEngine.isInBounds(m.getMove().getX(), m.getMove().getY()))
			return false;
		// Check that the direction is OK
		if (!m.getMove().getDirection()
				.equals(myCorner.getRelative(0))
				&& !m.getMove().getDirection()
						.equals(myCorner.getRelative(-1))
				&& !m.getMove().getDirection()
						.equals(myCorner.getRelative(1)))
			return false;
		return true;
	}
	
	
	}
