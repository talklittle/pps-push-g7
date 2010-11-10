package push.g2;

import java.awt.Point;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Queue;

import org.apache.log4j.Logger;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class G2Player extends Player{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	//start the end game strategy with this many rounds remaining:
	public static int END_GAME_START = 8;
	
	ArrayList<Opponent> opponents;
	
	int curRound = -1;
	int[][] board; //first set of arrays=9, second set of arrays=17
	int[][] prevBoard;
	Direction myCorner;
	int id;
	int numRounds = 0;
	
	boolean isEndGame = false;
	
	public String getName()
	{
		return "Push-a-Maniac";
	}
	
	// changes the current board state for G2 and the opponent objects
	public void updateBoardState(int[][] _board)
	{
		prevBoard = Util.cloneBoard(board);
		board = Util.cloneBoard(_board);
		//board = _board;
		
		for(Opponent o : opponents)
		{
			o.prevBoard = Util.cloneBoard(prevBoard);
			o.board = Util.cloneBoard(board);
			o.score = Util.getCurrentScore(o.oppCorner, board);
		}
	}
	
	// initializes the new board game settings
	public void startNewGame(int id, int m, ArrayList<Direction> playerPositions) 
	{
		//set up our own values
		this.id=id;
		myCorner=playerPositions.get(id);
		curRound = -1;
		prevBoard = Util.makeNewBoard();
		board = Util.makeNewBoard();
		numRounds = m;
		
		//create the list of opponents
		opponents = new ArrayList<Opponent>();
		for(int oppCount=0; oppCount<6; oppCount++)
		{
			if(oppCount != id)
			{
				opponents.add(new Opponent(oppCount, 
						myCorner, 
						playerPositions.get(oppCount),
						numRounds));
			}
		}
	}

	public Move makeMove(List<MoveResult> previousMoves)
	{
		curRound++;
		
		//determine if it's time to start the end game strategy
		if(numRounds - curRound <= END_GAME_START)
			isEndGame = true;
		
		int i = 0;
		for(MoveResult mr : previousMoves)
		{
			//log.debug(mr.getPlayerId()+" : " + mr.getMove() +" : "+mr.isSuccess());
			i++;
		}
		try
		{
			// add every opponent's move to their respective history
			if(previousMoves.isEmpty())
			{
				return new Move(8, 4, myCorner.getOpposite());
			}
			
			// it's not the first move
			for(MoveResult mr : previousMoves)
			{
				for(int x=0; x<opponents.size(); x++)
				{
					if(mr.getPlayerId() == opponents.get(x).oppId)
					{
						Move m = mr.getMove();
						Opponent op = opponents.get(x);
						op.addToWorthHistory(Util.worthOfAMove(prevBoard, op.oppCorner, m));
						op.addToAmountHelpedHistory(Util.affectsPlayerScore(myCorner, m, prevBoard));
						op.addToPotentialHistory(mr.getMove());
						op.updateOwedDebt(m);
						op.updateRanking();
						op.updateMemoryLookback();
						break;
					}
				}
			}
			
			Collections.sort(opponents); //sorts in ascending order, but we want the most useful one
			Collections.reverse(opponents);
//			log.error("\n-----------------------------------------------------ROUND " + curRound);
			
			//SWAP to affect other people
			boolean swapped = false;
			if(opponents.get(0).ranking>0 && opponents.get(1).ranking>0)
			{
				double difference = opponents.get(0).ranking - opponents.get(1).ranking;
				double percentOf = (difference / opponents.get(0).ranking) * 100.0;
				
				//determine which of the two players to help based on a weighted average of who's helped more
//				log.error("percentage 1 vs 2: " + percentOf);
				//if((Math.random()*100) < percentOf)
				if(Math.random()*10 < 2)
				{
					Collections.swap(opponents, 0, 1);
					swapped = true;
//					log.error("SWAPPING 1 and 2");
				}
			}
			
			//10% of the time, hurt somebody instead
			if(Math.random()*100 < 10 && !swapped)
			{
				//randomly hurt the player that hurt us the most
				if(Math.random()*2 == 0)
				{
					Collections.swap(opponents, 0, 4);
//					log.error("SWAPPING 1 and 5");
				}
				//randomly hurt the player with the highest score
				else
				{
					int maxScore = 0;
					int maxScoreId = 0;
					int count = -1;
					for(Opponent o : opponents)
					{
						count++;
						if(o.score > maxScore)
						{
							maxScore = o.score;
							maxScoreId = count;
						}
					}
					Collections.swap(opponents, 0, maxScoreId);
//					log.error("SWAPPING 1 and COUNT");
				}
			}
			
			
//			for(Opponent o : opponents)
//			{
//				log.error(o.oppId + " worth: " + o.totalWorthValue);
//				log.error(o.oppId + " helped: " + o.totalAmountHelped);
//				log.error(o.oppId + " potential: " + o.totalPotentialHelped);
//			}
			
//			for(Opponent o : opponents)
//			{
//				log.error(o.oppId + " ranking: " + o.ranking + " (mem " + o.historicalMemory + ")");
//			}
			
			//return the best move
			for(Opponent o : opponents)
			{	
				//tries to return a move that doesn't hurt us
				Move ourMove = null;
				if(o.ranking>=0)
					ourMove = Util.getBestMove(board, o, myCorner, false, 1);
				else
					ourMove = Util.getBestMove(board, o, myCorner, false, -1);
				
				if(ourMove != null)
				{
					//log.error("MOVE for " + o.oppId + ": " + 
							//ourMove.getX() + "," + ourMove.getY() + ": " + ourMove.getDirection());
					return ourMove;
				}
			}
			
			//if there are no moves that help people and DON'T hurt us, then we must hurt ourselves
			//log.error("HURTFUL MOVE");
			Move hurtSelf = Util.hurtSelfLeast(board, myCorner);
			if(hurtSelf != null)
			{
				//log.error("HURTFULMOVE: " +  hurtSelf);
				return hurtSelf;
			}
			
//			for(Opponent o : opponents)
//			{
//				Move ourMove = Util.getBestMove(board, o, myCorner, true);
//				if(ourMove != null)
//				{
//					//log.error("BAD MOVE: " + ourMove.getX() + "," + ourMove.getY() + ": " + ourMove.getDirection());
//					return ourMove;
//				}
//			}
			
			//to help certain opponents, but we can only hurt them
			
			//this is called only if the only moves we can make will hurt us
			// THIS IS A LAST RESORT
			//log.error("Last RESORT");
			return new Move(0,0,Direction.NE);
		}
		catch(Exception e)
		{
			java.io.StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			e.printStackTrace(pw);
			pw.flush();
			sw.flush();
			log.error(sw.toString());
		}
		
		log.debug("Printing default move");
		//no move is possible (or it's the first turn)
		return new Move(0,0, myCorner.getOpposite());
	}
	
	public Move generateRandomMove(int depth)
	{
		log.debug("generating a rando");
		if(depth > 300)
		{
			return new Move(0,0,Direction.NE);
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
			return generateRandomMove(depth+1);
		
		if(board != null&& board[n2][n1] == 0)
			return generateRandomMove(depth+1);
		Direction d = myCorner.getRelative(GameConfig.random.nextInt(3)-1);
		int tries = 0;
		while(!GameEngine.isValidDirectionForCellAndHome(d, myCorner) && tries < 10)
		{
			d = myCorner.getRelative(GameConfig.random.nextInt(2)-1);
			
			tries++;
		}
		if(!GameEngine.isValidDirectionForCellAndHome(d, myCorner))
			return generateRandomMove(depth+1);
		
		if(!GameEngine.isInBounds(n1+d.getDx(), n2+d.getDy()))
			return generateRandomMove(depth+1);
		
		Move m = new Move(n1, n2,d);
		return m;
	}
}
