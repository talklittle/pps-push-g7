package push.g2;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

import org.apache.log4j.Logger;

public class Util {

	public static Logger log = Logger.getLogger("Util");
	
	public static Move hurtSelfLeast(int[][] board, Direction home)
	{
		//find all valid moves for this player
		ArrayList<Moves> moves = new ArrayList<Moves>();
		ArrayList<Direction> dirs = new ArrayList<Direction>();
		dirs.add(home.getLeft().getOpposite());
		dirs.add(home.getOpposite());
		dirs.add(home.getRight().getOpposite());
		Move m;
		
		//iterate through board
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[0].length; j++)
			{
				if(board[i][j] < 1)
					continue;
				
				//check if move is valid
				for(Direction d : dirs)
				{
					m = new Move(j,i,d);
					if(isValid(m,board,home))
					{
						moves.add(new Moves(m, affectsPlayerScore(home, m, board)));
					}
					else
					{
						//log.debug("INVALID: " + m.getX() + "," + m.getY() + ": " + m.getDirection());
					}
				}	
			}
		}
		return getLeastHurtful(moves);
	}
	
	private static Move getLeastHurtful(ArrayList<Moves> moves) {
		double least = -1000;
		Moves best = null;
		for(Moves m: moves)
		{
			if(m.getVal() > least)
			{
				least = m.getVal();
				best = m;
			}
		}
		if(best != null){
			log.debug("hurtful move: " + best.getM());
			return best.getM();
		}
		else
			return null;
	}

	public static Move getBestMove(int[][] board, Opponent op, Direction home, boolean ignoreSelfHurt)
	{
		//find all valid moves for this player
		ArrayList<Moves> moves = new ArrayList<Moves>();
		ArrayList<Direction> dirs = new ArrayList<Direction>();
		dirs.add(home.getLeft().getOpposite());
		dirs.add(home.getOpposite());
		dirs.add(home.getRight().getOpposite());
		Move m;
		
		//iterate through board
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[0].length; j++)
			{
				if(board[i][j] < 1)
					continue;
				
				//check if move is valid
				for(Direction d : dirs)
				{
					m = new Move(j,i,d);
					if(isValid(m,board,home))
					{
						// if we don't care about hurting ourselves, every move is valid
						if(ignoreSelfHurt)
							moves.add(new Moves(m,worthOfAMove(board,op.oppCorner,m)));
						// if we don't want to hurt ourselves, only add moves that don't hurt us
						else if(affectsPlayerScore(home, m, board) >= 0)
							moves.add(new Moves(m,worthOfAMove(board,op.oppCorner,m)));
						
						//log.debug("VALID: " + m.getX() + "," + m.getY() + ": " + m.getDirection());
						//moves.add(new Moves(m,worthOfAMove(board,op.oppCorner,m)));
					}
					else
					{
						log.debug("INVALID: " + m);
						//log.debug("INVALID: " + m.getX() + "," + m.getY() + ": " + m.getDirection());
					}
				}	
			}
		}
		
		//sort list
		log.debug("Play to go after = " + op.oppId);
		log.debug("Possible moves : " + moves.size());
		for(Moves mvs : moves)
		{
			log.debug(mvs.getM());
		}
		return getBest(moves, op.totalValue);
	}
	
	private static Move getBest(ArrayList<Moves> moves, double gold) 
	{
		Moves best = null;
		double val = -999999;
		for(Moves m: moves)
		{
			if(m.getVal() > val)
			{
				val = m.getVal();
				best = m;
			}
		}
		/*
		for(Moves m: moves)
		{
			if(m.getVal() >= 1)
			{
				if(m.getVal() > val)
				{
					best = m;
					val = m.getVal();
				}
			}
			else if(m.getVal() < 1 && m.getVal() > 0)
			{
				if(val >= 1){
					continue;
				}
				else if(val < 0)
				{
					val = m.getVal();
					best = m;
				}
				else if(m.getVal() < val){
					val = m.getVal();
					best = m;
				}
			}
			else if(m.getVal() < 0 && m.getVal() >= -1)
			{
				if(val >= 0){
					continue;
				}
				else if(val < -1)
				{
					val = m.getVal();
					best = m;
				}
				else if(m.getVal() < val){
					val = m.getVal();
					best = m;
				}
			}
			else if(m.getVal() <= -1)
			{
				if(val > -1 )
					continue;
				if(m.getVal() > val)
				{
					best = m;
					val = m.getVal();
				}
			}
		}
		*/
		if(best != null)
		{
			log.debug("Move: " +best.getM()+ " value: " + best.getVal());
			return best.getM();
		}
		return null;
		
		/*
		Moves best = null;
		double minDist = Integer.MAX_VALUE;
		for(Moves m : moves)
		{
			//if(m.getVal()*gold < 0)
			//	continue;
			if(Math.abs(m.getVal() - gold) < minDist)
			{
				minDist = Math.abs(m.getVal() - gold);
				best = m;
			}
		}
		
		
		if(best != null)
		{
		//	log.debug("value of best move " + best.getVal());
			return best.getM();
		}
		
		return null;*/
		/*
		Moves best = null;
		double minDist = 0;
		if(gold > 0)
			minDist = -1000;
		else
			minDist = 1000;
		for(Moves m : moves)
		{
			log.debug(m.getVal());
			if(gold > 0)
			{
				if(m.getVal() > minDist)
				{
					minDist = m.getVal();
					best = m;
				}
			}
			else
			{
				if(m.getVal() < minDist)
				{
					minDist = m.getVal();
					best = m;
				}
			}
		}
		
		
		if(best != null)
		{
			log.debug("value of best move " + best.getVal());
			return best.getM();
		}
		
		return null;
		*/
	}

	//calculates the worth of any move made in the previous round for G2Player
	public static double worthOfAMove(int[][]board, Direction g2Corner, Move m)
    {
		return affectsPlayerScore(g2Corner,m,board);
		
		//double points = affectsPlayerScore(g2Corner, m, board); 
		
			/*
			double worth=0.0;
	        double oldDistance=GameEngine.getDistance(g2Corner.getHome(), new Point(m.getX(),m.getY()));
	        double newDistance=GameEngine.getDistance(g2Corner.getHome(), new Point(m.getNewX(),m.getNewY()));
	        double coins= board[m.getNewY()][m.getNewX()];
	        if(newDistance == 0)
	        	newDistance = 0.1;
	        worth = 100*(coins)*((oldDistance-newDistance)/((newDistance+oldDistance)/2));
	        return 1.0/worth;
	        */
		
		/*
        double worth=0.0;
        double oldDistance=GameEngine.getDistance(g2Corner.getHome(), new Point(m.getX(),m.getY()));
        double newDistance=GameEngine.getDistance(g2Corner.getHome(), new Point(m.getNewX(),m.getNewY()));
        double coins= board[m.getNewY()][m.getNewX()];
        if(oldDistance==newDistance)
        {
        	
        }
        if(newDistance == 0)
        	newDistance = 0.1;
        worth = (coins)*((oldDistance/newDistance)-1.0);
        return worth;
        */
    }
	
	// returns how much a move affects the player's score
	public static int affectsPlayerScore(Direction playerCorner, Move m, int[][] board)
	{
		int[][] bCopy = cloneBoard(board);
		
		// get the current score of the player
		int oldScore = getCurrentScore(playerCorner, bCopy);
		
		// get the potential new score of the player
		int numCoins = bCopy[m.getY()][m.getX()];
		bCopy[m.getY()][m.getX()] = 0;
		bCopy[m.getNewY()][m.getNewX()] += numCoins;
		int newScore = getCurrentScore(playerCorner, bCopy);
		
		return newScore - oldScore;
	}
	
	public static int getCurrentScore(Direction corner, int[][] board)
	{
		int currentScore = 0;
		int homeCellX = corner.getHome().x;
		int homeCellY = corner.getHome().y;
		Direction out = corner.getOpposite();
		Direction right = corner.getLeft().getOpposite();
		Direction left = corner.getRight().getOpposite();
		
		int newY=0, newX=0;
		
		//4 point cells
		newY = homeCellY;
		newX = homeCellX;
		currentScore += 4*board[newY][newX];
		
		//3 point cells
		newY = homeCellY+out.getDy();
		newX = homeCellX+out.getDx();
		currentScore += 3*board[newY][newX];
		
		//2 point cells
		newY = homeCellY+right.getDy();
		newX = homeCellX+right.getDx();
		currentScore += 2*board[newY][newX];
		newY = homeCellY+left.getDy();
		newX = homeCellX+left.getDx();
		currentScore += 2*board[newY][newX];
		newY = homeCellY+out.getDy()*2;
		newX = homeCellX+out.getDx()*2;
		currentScore += 2*board[newY][newX];
		
		//1 point cells
		newY = homeCellY+out.getDy()+right.getDy();
		newX = homeCellX+out.getDx()+right.getDx();
		currentScore += 1*board[newY][newX];
		newY = homeCellY+out.getDy()+left.getDy();
		newX = homeCellX+out.getDx()+left.getDx();
		currentScore += 1*board[newY][newX];
		newY = homeCellY+out.getDy()*3;
		newX = homeCellX+out.getDx()*3;
		currentScore += 1*board[newY][newX];
		
		return currentScore;
	}
	
	public static int[][] cloneBoard(int[][] board)
	{
		if(board == null)
		{
			int[][] newBoard = new int[9][17];
			for(int x=0; x< newBoard.length; x++)
			{
				for(int y=0; y<newBoard.length; y++)
				{
					newBoard[x][y] = 0;
				}
			}
			return newBoard;
		}
		int[][] newBoard = new int[board.length][board[0].length];
		
		for(int x=0; x<board.length; x++)
		{
			for(int y=0; y<board.length; y++)
			{
				newBoard[x][y] = board[x][y];
			}
		}
		
		return newBoard;
	}
	
	private static boolean isValid(Move m, int[][] board, Direction myCorner)
	{
		return isSuccessByBoundsEtc(m, myCorner)
			&& isSuccessByCount(m, board)
			&& GameEngine.isValidDirectionForCellAndHome(m.getDirection(), myCorner);
	}
	
	private static boolean isSuccessByBoundsEtc(Move m, Direction home) {
		// Check that we are in bounds
		if (!GameEngine.isInBounds(m.getNewX(), m.getNewY()))
			return false;
		if (!GameEngine.isInBounds(m.getX(), m.getY()))
			return false;
		// Check that the direction is OK
		if (!m.getDirection().equals(home.getRelative(0))
				&& !m.getDirection()
						.equals(home.getRelative(-1))
				&& !m.getDirection()
						.equals(home.getRelative(1)))
			return false;
		return true;
	}
	
	private static boolean isSuccessByCount(Move m, int[][] board) {
		// Check that there are > 0 in this position
		if (board[m.getY()][m.getX()] == 0)
			return false;
		return true;
	}
	
	
	private static class Moves implements Comparable<Moves> {
		private Move m;
		private double val;
		
		public Move getM() {
			return m;
		}

		public double getVal() {
			return val;
		}

		
		public Moves(Move _m, double _val)
		{
			m = _m;
			val = _val;
		}

		@Override
		public int compareTo(Moves o) {
			if(Math.abs(val) > Math.abs(o.getVal()))
				return 1;
			if(Math.abs(o.getVal()) > Math.abs(val))
				return -1;
			return 0;
		}
	}
}








