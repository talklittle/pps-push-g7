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
	public static final int HISTORY_MEMORY = 10;
	
	public int oppId = 0;
	public int score = 16; //every player starts with 16 points
	
	public int[][] board;
	public int[][] prevBoard;
	public Direction g2Corner;
	public Direction oppCorner;
	
	public LinkedList<Double> valHistory;
	public LinkedList<Double> potentialHistory;
	public LinkedList<Integer> helpedHistory;
	
	// negative value = defect opponent
	// 0 = neutral opponent
	// positive value = cooperative opponent
	public double totalValue = 0.0;
	
	// amount that we need to "repay" the opponent
	// positive means we should pay them back
	public double owedDebt = 0.0;
	
	// potential that they could have helped us, averaged over previous rounds
	// vs. the amount that they actually helped us
	public double totalPotentialHelped = 0.0;
	
	// this is the amount helped over time
	public double totalAmountHelped = 0.0;
	
	public Opponent(int idNum, Direction myCorner, Direction opposingCorner)
	{
		g2Corner = myCorner;
		oppId = idNum;
		oppCorner = opposingCorner;
		
		valHistory = new LinkedList<Double>();
		potentialHistory = new LinkedList<Double>();
		helpedHistory = new LinkedList<Integer>();
	}
	
	public void addToAmountHelpedHistory(int amountHelped)
	{
		if(helpedHistory.size() == HISTORY_MEMORY)
		{
			helpedHistory.remove();
		}
		
		helpedHistory.addLast(amountHelped);
		
		totalAmountHelped = updateHistoricalHelpedAverage();
	}
	
	// adds the player's most recent move to the historical stack
	public void addToValueHistory(double val)
	{
		if(valHistory.size() == HISTORY_MEMORY)
		{
			valHistory.remove();
		}
		
		valHistory.addLast(val);
		
		totalValue = updateHistoricalValAverage();
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
		int possibleAffected = 0;
		if(amountAffectedG2 > 0)
			possibleAffected = Integer.MIN_VALUE;
		else
			possibleAffected = Integer.MAX_VALUE;
		
		//iterate through all the possible moves
		for(int i = 0; i < prevBoard.length; i++)
		{
			for(int j = 0; j < prevBoard[0].length; j++)
			{
				if(prevBoard[i][j] < 1)
					continue;
				
				for(Direction d : dirs)
				{
					//if the player had the ability to help/hurt us more than he actually did, keep track of it
					int tempAffected = Util.affectsPlayerScore(g2Corner, m, prevBoard);
					
					if(amountAffectedG2 > 0 && tempAffected > amountAffectedG2)
						possibleAffected = tempAffected;
					else if(amountAffectedG2 < 0 && tempAffected < amountAffectedG2)
						possibleAffected = tempAffected;
				}
			}
		}
		
		//calculate the actual divided by possible to get the potential affected
		double potentialAffected = amountAffectedG2 / (double)possibleAffected; 
		
		if(potentialHistory.size() == HISTORY_MEMORY)
		{
			potentialHistory.remove();
		}
		
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
			avgVal += count * val;
			denominator += count;
			count += 1.0;
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
		if(this.totalAmountHelped > opp2.totalAmountHelped)
			return 1;
		if(totalAmountHelped >this.totalAmountHelped)
			return -1;
		return 0;
	}
}







