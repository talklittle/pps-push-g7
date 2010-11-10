package push.g4;

import java.util.ArrayList;
import java.util.Collections;

import push.sim.GameEngine;
import push.sim.Move;
import push.sim.Player.Direction;

public class StaticPlayer {
	public static double alpha=0.70; //current gain weight
	
	public static int[][] validRows = {{4,6,8,10,12},
									{3,5,7,9,11,13}, 
									{2,4,6,8,10,12,14},
									{1,3,5,7,9,11,13,15},
									{0,2,4,8,10,12,14,16},
									{1,3,5,7,9,11,13,15},
									{2,4,6,8,10,12,14},
									{3,5,7,9,11,13},
									{4,6,8,10,12}};

	public static double[][] score0 = 
		//	 0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6
		{	{0,0,0,0,0,0,0,0,0,0,2,0,4,0,0,0,0},	//0
			{0,0,0,0,0,0,0,0,0,1,0,3,0,2,0,0,0},	//1
			{0,0,0,0,0,0,0,0,0,0,2,0,1,0,0,0,0},	//2
			{0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0},	//3
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//4
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//5
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//6
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//7
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};	//8
	public static double[][] score1 = 
		//	 0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6
		{	{0,0,0,0,4,0,2,0,0,0,0,0,0,0,0,0,0},	//0
			{0,0,0,2,0,3,0,1,0,0,0,0,0,0,0,0,0},	//1
			{0,0,0,0,1,0,2,0,0,0,0,0,0,0,0,0,0},	//2
			{0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0},	//3
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//4
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//5
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//6
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//7
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};	//8
	
	public static double[][] score2 = 
		//	 0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6
		{	{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//0
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//1
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//2
			{0,2,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0},	//3
			{4,0,3,0,2,0,1,0,0,0,0,0,0,0,0,0,0},	//4
			{0,2,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0},	//5
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//6
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//7
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};	//8
	
	public static double[][] score3 = 
		//	 0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6
		{	{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//0
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//1
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//2
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//3
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//4
			{0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0},	//5
			{0,0,0,0,1,0,2,0,0,0,0,0,0,0,0,0,0},	//6
			{0,0,0,2,0,3,0,1,0,0,0,0,0,0,0,0,0},	//7
			{0,0,0,0,4,0,2,0,0,0,0,0,0,0,0,0,0}};	//8
	public static double[][] score4 = 
		//	 0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6
		{	{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//0
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//1
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//2
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//3
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//4
			{0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0},	//5
			{0,0,0,0,0,0,0,0,0,0,2,0,1,0,0,0,0},	//6
			{0,0,0,0,0,0,0,0,0,1,0,3,0,2,0,0,0},	//7
			{0,0,0,0,0,0,0,0,0,0,2,0,4,0,0,0,0}};	//8
	public static double[][] score5 = 
		//	 0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6
		{	{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//0
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//1
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//2
			{0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,2,0},	//3
			{0,0,0,0,0,0,0,0,0,0,1,0,2,0,3,0,4},	//4
			{0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,2,0},	//5
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//6
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	//7
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};	//8
		
	public static double getScore(Direction player, int row, int col){
		//TODO FIX IDS, they don't correspond to the same numbers in the board (e.g., id 0 should be in (4,0))
		//player id 0
		double score = 0;
		if(player.getHome().x == 12 && player.getHome().y ==0)
			score = score0[row][col];
		//player id 1
		if(player.getHome().x == 4 && player.getHome().y ==0)
			score = score1[row][col];
		//player id 2
		if(player.getHome().x == 0 && player.getHome().y ==4)
			score = score2[row][col];
		//player id 3
		if(player.getHome().x == 4 && player.getHome().y ==8)
			score = score3[row][col];
		//player id 4
		if(player.getHome().x == 12 && player.getHome().y ==8)
			score = score4[row][col];
		//player id 5
		if(player.getHome().x == 16 && player.getHome().y ==4)
			score = score5[row][col];
		
		if(score == 0) //TO BE REMOVED WHEN THE TABLES ARE SET WITH THE ACTUAL FRACTIONS
			score = 1/2;
		
		return score;
	}
	
	public static double getGain(Move move, Direction direction, int [][] board)
	{
		int srcColumn=move.getX();
		int srcRow=move.getY();
		int dstColumn=move.getNewX();
		int dstRow=move.getNewY();
		
		double srcScore=StaticPlayer.getScore(direction, srcRow, srcColumn);
		double dstScore=StaticPlayer.getScore(direction, dstRow, dstColumn);
		
		return (dstScore-srcScore)*board[srcRow][srcColumn];
	}
	
	public static ArrayList<TrackMove> benefitFriend(Direction friend, boolean onlyBenefit, Direction myCorner, int[][] board){
		ArrayList<TrackMove> trackMoves=new ArrayList<TrackMove>();
		for (int row=0; row<StaticPlayer.validRows.length; row++)
		{
			for (int k=0; k<StaticPlayer.validRows[row].length; k++)
			{
				int column=StaticPlayer.validRows[row][k];
				if (board[row][column]==0)
					continue;
				for (int d=-1; d<=1; d++)
				{
					Direction direction=myCorner.getRelative(d);
					if (GameEngine.isInBounds(column+direction.getDx(), row+direction.getDy()))
					{
						Move move=new Move(column, row, direction);
						double gain=getGain(move, friend, board);
						if ((onlyBenefit && gain>0) || !onlyBenefit)
							trackMoves.add(new TrackMove(move, gain));
					}
						
				}
			}
		}
		Collections.sort(trackMoves, new TrackMoveComparator());
		return trackMoves;
	}
}

