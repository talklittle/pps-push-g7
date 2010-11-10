package push.g2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.Point;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;
import push.sim.Player.Direction;

public class Opponent implements Comparable<Opponent>
{
	public static final int HISTORY_MEMORY = 7;
	public static final int HISTORY_MEM_MIN = 4;
	public static final int HISTORY_MEM_MAX = 10;

	public static final int END_GAME_ROUND_START = 10;
	
	public static final double WORTH_PERCENTAGE = 0.3;
	public static final double POTENTIAL_HELPED_PERCENTAGE = 0.3;
	public static final double AMOUNT_HELPED_PERCENTAGE = 0.4;
	
	public int oppId = 0;
	public int score = 16; //every player starts with 16 points
	
	public int[][] board;
	public int[][] prevBoard;
	public Direction g2Corner;
	public Direction oppCorner;
	
	public int numRounds = 0;
	public int curRound = 0;
	
	public LinkedList<Double> valHistory;
	public LinkedList<Double> potentialHistory;
	public LinkedList<Integer> helpedHistory;
	
	public int historicalMemory = 7; //how far to look back?
	
	// the players OVERALL RANKING (a weighted avg)
	public double ranking = 0.0;
	
	// amount that we need to "repay" the opponent
	// positive means we should pay them back
	public double owedDebt = 0.0;
	
	// negative value = defect opponent
	// 0 = neutral opponent
	// positive value = cooperative opponent
	public double totalWorthValue = 0.0;
	
	// potential that they could have helped us, averaged over previous rounds
	// vs. the amount that they actually helped us
	public double totalPotentialHelped = 0.0;
	
	// this is the amount helped over time
	public double totalAmountHelped = 0.0;
	
	public Opponent(int idNum, Direction g2Corner, Direction opposingCorner, int numRds)
	{
		this.g2Corner = g2Corner;
		oppId = idNum;
		oppCorner = opposingCorner;
		prevBoard = Util.makeNewBoard();
		board = Util.makeNewBoard();
		numRounds = numRds;
		historicalMemory = 7;
		
		valHistory = new LinkedList<Double>();
		potentialHistory = new LinkedList<Double>();
		helpedHistory = new LinkedList<Integer>();
	}
	
	/**
	 * Updates the total ranking of the player based on weighted averages
	 * of the worth, potential helped, and amount helped
	 */
	public void updateRanking()
	{
		ranking = WORTH_PERCENTAGE * totalWorthValue + 
			POTENTIAL_HELPED_PERCENTAGE * totalPotentialHelped +
			AMOUNT_HELPED_PERCENTAGE * totalAmountHelped;
		
		//if the player is net positive or negative, change the ranking to positive or negative
		if(totalAmountHelped < 0 && ranking > 0)
			ranking = ranking * -1.0;
		else if(totalAmountHelped > 0 && ranking < 0)
			ranking = ranking * -1.0;
		
		score = Util.getCurrentScore(oppCorner, board);
	}
	
	public void updateMemoryLookback()
	{
		curRound++;
		
		if(ranking < 0.01 && historicalMemory-1 > HISTORY_MEM_MIN)
			historicalMemory--;
		else if(ranking > .01 && historicalMemory+1 < HISTORY_MEM_MAX)
			historicalMemory++;
		
		//if it's almost the end of the game, execute end-game strategy
		if(numRounds - curRound <= END_GAME_ROUND_START)
			historicalMemory = 3;
	}
	
	public void addToAmountHelpedHistory(int amountHelped)
	{
		while(helpedHistory.size() >= historicalMemory)
		{
			helpedHistory.remove();
		}
		
		helpedHistory.addLast(amountHelped);
		
		totalAmountHelped = updateHistoricalHelpedAverage();
	}
	
	// adds the player's most recent move to the historical stack
	public void addToWorthHistory(double val)
	{
		while(valHistory.size() >= historicalMemory)
		{
			valHistory.remove();
		}
		
		valHistory.addLast(val);
		
		totalWorthValue = updateHistoricalValAverage();
	}
	
	public void addToPotentialHistory(Move m)
	{
		//find all valid moves for this player
		ArrayList<Direction> dirs = new ArrayList<Direction>();
		dirs.add(oppCorner.getLeft().getOpposite());
		dirs.add(oppCorner.getOpposite());
		dirs.add(oppCorner.getRight().getOpposite());
		
		//determine how much the opponent affected us
		double amountAffectedG2 = (double)Util.affectsPlayerScore(g2Corner, m, prevBoard);
		double possibleAffected = amountAffectedG2;
		
		boolean canBeHelped = false;
		
		//iterate through all the possible moves
		for(int i = 0; i < prevBoard.length; i++)
		{
			for(int j = 0; j < prevBoard[0].length; j++)
			{
				if(prevBoard[i][j] < 1)
					continue;
				
				for(Direction d : dirs)
				{
					Move tempMove = new Move(j, i, d);
					
					//check that the move is valid
					if(Util.isValid(tempMove, prevBoard, oppCorner))
					{
						//if the player had the ability to help/hurt us more than he actually did, keep track of it
						int tempAffected = Util.affectsPlayerScore(g2Corner, tempMove, prevBoard);
						
						if(amountAffectedG2 >= 0 && tempAffected >= possibleAffected)
							possibleAffected = (double)tempAffected;
						else if(amountAffectedG2 < 0 && tempAffected <= possibleAffected)
							possibleAffected = (double)tempAffected;
						
//						if(amountAffectedG2 == 0 && tempAffected > possibleAffected)
//							possibleAffected = (double)tempAffected;
						
						//flag to check that g2 player could have even been helped in this round
						if(tempAffected > 0)
							canBeHelped = true;
					}
				}
			}
		}
		
		//calculate the actual divided by possible to get the potential affected
		double potentialAffected = amountAffectedG2 / (double)possibleAffected; 
		
		//check that the opponent helped us, potential is positive (and vice versa)
		if(amountAffectedG2>0 && potentialAffected<0)
			potentialAffected *= -1.0;
		else if(amountAffectedG2<0 && potentialAffected>0)
			potentialAffected *= -1.0;
		
		//check that amountAffected is 0, but opponent could have helped us
		if(potentialAffected==0 && possibleAffected<=0)
			potentialAffected = .01;
		else if(potentialAffected==0 && possibleAffected>0)
			potentialAffected = -.01;
			
		while(potentialHistory.size() >= historicalMemory)
		{
			potentialHistory.remove();
		}
		
		//if they hurt us, we still want to know that they hurt us (by making it negative)
		potentialAffected = (amountAffectedG2 > 0) ? potentialAffected : potentialAffected*-1.0;
		
		potentialHistory.addLast(potentialAffected);
		totalPotentialHelped = updateHistoricalPotentialAverage();
	}
	
	// determines how much an opponent affected g2
	public void updateOwedDebt(Move m)
	{
		int amountAffected = Util.affectsPlayerScore(g2Corner, m, board);
		owedDebt += amountAffected; 
	}
	
	// gets the opponent's total average "value"
	private double updateHistoricalValAverage()
	{
		double avgVal = 0.0;
		double count = 0.0; 
		
		for(Double val : valHistory)
		{
			avgVal += val;
			count += 1.0;
		}
		
		return avgVal/count;
	}

	private double updateHistoricalPotentialAverage()
	{
		double avgVal = 0.0;
		double denominator = 0.0;
		double count = 0.0; 
		
		for(Double val : potentialHistory)
		{
			count += 1.0;
			avgVal += count * val;
			denominator += count;
		}
		
		double avg = avgVal/denominator;
		if(Double.isNaN(avg) || Double.isInfinite(avg))
		{
			return 0;
		}
		
		return avgVal/denominator;
	}
	
	private double updateHistoricalHelpedAverage()
	{
		double curAvgHelped = 0.0;
		double denominator = 0.0;
		double count = 1.0;
		
		//weighted average with 10 being the most recent, and 1 being the least recent
		for(Integer amtHelped : helpedHistory)
		{
			curAvgHelped += count * (double)amtHelped;
			denominator += count;
			count += 1.0;
		}
		
		return curAvgHelped/denominator;
	}
	
	@Override
	public int compareTo(Opponent opp2) {
		if(this.ranking > opp2.ranking)
			return 1;
		if(ranking >this.ranking)
			return -1;
		return 0;
	}
}







