package push.g5.analytics;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import push.sim.GameController;

public class HelpRatio {
	
	private int bestPointChangePossible = 0;
	private int worstPointChangePossible = 0;
	private int pointChange = 0;
	private int netHelpfulMoves = 0;
	private double helpRatio=0;
	private ArrayList<HelpRatioComponents> moves = new ArrayList<HelpRatioComponents>();

	public void addRound(int currentPointChange, int currentBestPointChangePossible, int currentWorstPointChangePossible)
	{
		moves.add(new HelpRatioComponents(currentPointChange, currentBestPointChangePossible, currentWorstPointChangePossible));
		
		pointChange += currentPointChange;
		bestPointChangePossible += currentBestPointChangePossible;
		worstPointChangePossible += currentWorstPointChangePossible;
		
		if( bestPointChangePossible == worstPointChangePossible )
			helpRatio = 0;
		else
			helpRatio = 2.0 * ( pointChange - worstPointChangePossible ) / ( bestPointChangePossible - worstPointChangePossible ) - 1;
		
		if( currentPointChange > 0 )
			netHelpfulMoves++;
		else if( currentPointChange < 0 )
			netHelpfulMoves--;
			
	}
	
	public int getPositivePointsPossible() {
		return bestPointChangePossible;
	}

	public int getNegativePointsPossible() {
		return worstPointChangePossible;
	}

	public int getPointChange() {
		return pointChange;
	}

	public int getNetHelpfulMoves() {
		return netHelpfulMoves;
	}

	public double getHelpRatio()
	{
		return helpRatio;
	}
	
	public ArrayList<HelpRatioComponents> getMoves()
	{
		return moves;
	}
	
	public String toString()
	{
		return "" + helpRatio;
	}
}
