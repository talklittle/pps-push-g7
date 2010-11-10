package push.g5;

import java.awt.AWTException;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import push.g5.analytics.HelpRatio;
import push.g5.analytics.HelpRatioComponents;
import push.g5.analytics.NetMove;
import push.g5.analytics.PointMatrix;
import push.g5.strategy.TeamBuilder;
import push.g5.teams.Statistics;
import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;


public class g5player extends Player{
	public int[][] board;
	int[][] boardCopy;
	private Logger log = Logger.getLogger(this.getClass());
	public int round = 0;
	private PointMatrix pointMatrixWholeGame;
	private ScreenGrabber grabber = new ScreenGrabber();
	
	public int NUMRECENTMOVES = 3;
	private static boolean IS_FIRST = true;
	private int currentRound = -2;
	
	private static int CURRENT_GAME_NUMBER;
	private static boolean TAKE_SCREEN_SHOTS = false;
	private static String gameNumberFile = "screen_shots/current_game.txt";
	
	Statistics s = new Statistics();
	
	static
	{
		if(TAKE_SCREEN_SHOTS)
		{
			File f = new File(gameNumberFile);
			Scanner fileReader = null;
			try {
				fileReader = new Scanner(f);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			CURRENT_GAME_NUMBER = fileReader.nextInt();
			
			try{
		    // Create file 
		    FileWriter fstream = new FileWriter(gameNumberFile);
		        BufferedWriter out = new BufferedWriter(fstream);
		    out.write(""+(CURRENT_GAME_NUMBER+1));
		    //Close the output stream
		    out.close();
		    }catch (Exception e){//Catch exception if any
		      System.err.println("Error: " + e.getMessage());
		    }
		}
	}

	@Override
	public void updateBoardState(int[][] board) {
		this.board= board;
		
		if( ( IS_FIRST || currentRound != -2 ) && TAKE_SCREEN_SHOTS )
		{
			IS_FIRST = false;
			try {
				grabber.grabScreen(CURRENT_GAME_NUMBER + "_" + ++currentRound);
			} catch (AWTException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		s.calculateScores(board);
	}
	@Override
	public String getName() {
		return "Prisoner";
	}
	
	public int[][] copyBoard(int[][] myBoard)
	{
		int[][] myNewBoard = new int[9][17];
		for(int y = 0; y < 9; y++)
		{
			for(int x = 0; x < 17; x++)
			{
				myNewBoard[y][x] = myBoard[y][x];
			}
		}
		return myNewBoard;
	}
	
	public Direction myCorner = Direction.E;
	public Cooperator firstCooperator = new Cooperator();
	public Cooperator secondCooperator = new Cooperator();
	public Cooperator firstNonCooperator = new Cooperator();
	public Cooperator secondNonCooperator = new Cooperator();
	public Cooperator thirdNonCooperator = new Cooperator();
	public Cooperator potentialCooperator = new Cooperator();
	public Cooperator nextPlayer = new Cooperator();
	public boolean firstCooperationEstablished = false;
	public boolean secondCooperationEstablished = false;
	public boolean exploringFirstCooperation = false;
	public boolean exploringSecondCooperation = false;
	public boolean inEvenCooperation = false;
	public boolean leftPlayerGoodIndirectCoop = true;
	public boolean rightPlayerGoodIndirectCoop = true;
	public boolean straightPlayerGoodIndirectCoop = true;
	int remainingTriesToInitiateCooperation = 0;
	int cooperatorRelative = -1;
	Direction playerToHelp;
	Direction secondPlayerToHelp;
	TeamBuilder builder;
	public double highestRatio;
	
	ArrayList<List<MoveResult>> allMoveLists = new ArrayList<List<MoveResult>>(); 
	ArrayList<int[][]> allBoards = new ArrayList<int[][]>();
	HelpRatio[][] recentHelpMatrix;
	int leftDirection;
	int rightDirection;
	int straightDirection;
	
	final double COOPERATION_ESTABLISHED_THRESHOLD = 0.70;
	final double SWITCH_TO_ONE_THRESHOLD = 0.65;
	final double EXPLORE_NEW_PARTNER_THRESHOLD = 0.35;
	final double TRY_FOR_SECOND_PARTNER_THRESHOLD = 0.50;
	final double SECOND_COOPERATION_ESTABLISHED_THRESHOLD = 0.50;
	final double UNSOLICITED_COOPERATION_ESTABLISHED_THRESHOLD = 0.80;
	final double UNSOLICITED_SECOND_COOPERATION_ESTABLISHED_THRESHOLD = 0.60;
	final double DROP_LESSER_COOPERATOR_THRESHOLD = 0;
	final double DROP_PRIMARY_COOPERATOR_THRESHOLD = 0;
	final double LEAVE_HERMIT_MODE_THRESHOLD = 0.30;
	final double LEAVE_PRIMARY_COOPERATOR_THRESHOLD = 0.20;
	
	public void printStatus(int i)
	{
		log.info(id + "    Best possible help amount of player " + i + " to me (player " + id + ") is: " + recentHelpMatrix[i][id].getPositivePointsPossible());
		ArrayList<HelpRatioComponents> myHRC = recentHelpMatrix[i][id].getMoves();
		int theSize = myHRC.size();
		String myString = id + "               ";
		for(int m = 0; m < theSize; m++)
		{
			myString += " " + myHRC.get(m).max;
		}
		log.info(myString);
		
		
		log.info(id + "    Worst possible help amount of player " + i + " to me (player " + id + ") is: " + recentHelpMatrix[i][id].getNegativePointsPossible());				
		myString = id + "               ";
		for(int m = 0; m < theSize; m++)
		{
			myString += " " + myHRC.get(m).min;
		}
		log.info(myString);
		
		
		log.info(id + "    Actual Change in points of player " + i + " to me (player " + id + ") is: " + recentHelpMatrix[i][id].getPointChange());
		myString = id + "               ";
		for(int m = 0; m < theSize; m++)
		{
			myString += " " + myHRC.get(m).change;
		}
		log.info(myString);
		
		log.info(id + "    Help Ratio of player " + i + " to me (player " + id + ") is: " + recentHelpMatrix[i][id].getHelpRatio());
		log.info(id);
	}
	
	public int id;
	@Override
	public void startNewGame(int id, int m,
			ArrayList<Direction> playerPositions) {
		//initialize game variables
		myCorner=playerPositions.get(id);
		this.id=id;
		pointMatrixWholeGame = new PointMatrix(this);
		leftDirection = getPlayerIndex(myCorner.getRelative(1));
		rightDirection = getPlayerIndex(myCorner.getRelative(-1));
		straightDirection = getPlayerIndex(myCorner.getRelative(0));
		builder = new TeamBuilder( this );
		
		//initialize partner variables
		firstCooperationEstablished = false;
		secondCooperationEstablished = false;
		exploringFirstCooperation = true;
		exploringSecondCooperation = false;
		leftPlayerGoodIndirectCoop = true;
		rightPlayerGoodIndirectCoop = true;
		straightPlayerGoodIndirectCoop = true;
		remainingTriesToInitiateCooperation = NUMRECENTMOVES * 2;
		potentialCooperator = new Cooperator(straightDirection, 0, rightPlayerGoodIndirectCoop);
		inEvenCooperation = true;
	} 

	public PointMatrix recentPointMatrix;
	public Move makeMove(List<MoveResult> previousMoves) {
		
		log.info("");
		log.info("--------------------------------------------------");
		log.info(id + "ROUND: " + round);
		
		
		allMoveLists.add(previousMoves);
		allBoards.add(copyBoard(board));
		
		pointMatrixWholeGame.addRound(previousMoves, board);
		
		recentPointMatrix = new PointMatrix(this);
		
		int lastIndex = allMoveLists.size() - 1;
		if(lastIndex >= NUMRECENTMOVES)
		{
			recentPointMatrix.setPreviousBoard(allBoards.get(lastIndex - NUMRECENTMOVES));
			for(int i = NUMRECENTMOVES-1; i >= 0; i--)
			{
				recentPointMatrix.addRound(allMoveLists.get(lastIndex - i), allBoards.get(lastIndex - i));
			}
		}
		else //if at beginning of game, then the recentPointMatrix is just the matrix from the whole game. 
		{
			recentPointMatrix = pointMatrixWholeGame;
		}
		
		log.info(null);
		log.info(id + "Help ratio matrix from the last " + NUMRECENTMOVES + " rounds:");
		recentHelpMatrix = recentPointMatrix.getHelpMatrix();
		printMatrix(recentHelpMatrix);
		//printStatus(leftDirection);
		//printStatus(rightDirection);
		//printStatus(straightDirection);
		
		determineNextMove(previousMoves);
		
		Move moveToMake;
		if( nextPlayer.index == id )
		{
			if( firstCooperationEstablished )
			{
				log.info("We have only one weak cooperator, we helped them last time so make neutral move this time.");
				moveToMake = makeNeutralMove( firstCooperator.index, -1 );	
			}
			else
				{
					log.info("We have no cooperators, make a neutral move");
					moveToMake = makeNeutralMove( -1, -1 );
				}
		}
		else
			moveToMake = findMostHelpfulMove(myCorner, getDirection(nextPlayer.index), board, nextPlayer.goodIndirectCoop, true);
		round++;
		return moveToMake;
	}
	
	public void printMatrix(HelpRatio[][] matrix)
	{
		if( matrix == null || matrix.length == 0 )
			log.info("tried to print an empty matrix");
		
		String output = "\n";
		for(int i=0; i<matrix.length; i++)
		{
			for(int j=0; j<matrix[0].length; j++)
			{
				output += String.format("%9.3f ", matrix[i][j].getHelpRatio());
			}
			output += "\n";
		}
		log.info(output + "\n");
		s.getCurrentTeams(matrix); //to be placed where needed.
	}
	
	/* Returns the most helpful move that player "helper" can do for player "receiverOfHelp".
	 * The most helpful move is the one that increases receiverOfHelp's score by the most points
	 * (or decreases it by the least points if no helpful moves are possible). 
	 */
	public Move findMostHelpfulMove(Direction helper, Direction receiverOfHelp, int[][] myBoard)
	{
		return findMostHelpfulMove(helper, receiverOfHelp, myBoard, false, false);
	}
	
	public Move findMostHelpfulMove(Direction helper, Direction receiverOfHelp, int[][] myBoard, boolean helpFullIndirectly)
	{
		return findMostHelpfulMove(helper, receiverOfHelp, myBoard, helpFullIndirectly, false);
	}
	
	public Move findMostHelpfulMove(Direction helper, Direction receiverOfHelp, int[][] myBoard, boolean helpFullIndirectly, boolean callNeutralIfCantMakeMove)
	{
		int maxPointChange = -122;
		Move moveToMake = new Move(8,4,myCorner.getOpposite()); //initialize moveToMake to a random move in case it finds that no moves are possible.
		ArrayList<Slot> potentialSlots = getAllSlots();
		int minHurtHelperAmount = 122;
		for(int i = 0; i < potentialSlots.size(); i++)
		{
			Slot mySlot = potentialSlots.get(i);
			if(myBoard[mySlot.getY()][mySlot.getX()] != 0)
			{
				for(int j = -1; j <= 1; j++)
				{
					Move m = new Move(mySlot.getX(), mySlot.getY(), helper.getRelative(j));	
					if(GameEngine.isInBounds(m.getNewX(), m.getNewY()))
					{
						boolean moveHurtsHelper = false;
						int hurtHelperAmount = 0;
						NetMove myNetMove = pointMatrixWholeGame.calcNetMove(m, myBoard);
						int pointChange = 0;
						if(myNetMove.playerIncreaseIndex == getPlayerIndex(receiverOfHelp))
						{
							pointChange = myNetMove.pointIncrease;
							minHurtHelperAmount = 0;
						}
						else if(myNetMove.playerDecreaseIndex != getPlayerIndex(receiverOfHelp))
						{
							if(myNetMove.playerDecreaseIndex != getPlayerIndex(helper))
							{
								pointChange = 0;
								minHurtHelperAmount = 0;
							}
							else
							{
								pointChange = 0;
								moveHurtsHelper = true;
								hurtHelperAmount = -1*myNetMove.pointDecrease;
							}
						}
						else if(myNetMove.playerDecreaseIndex == getPlayerIndex(receiverOfHelp))
						{
							pointChange = myNetMove.pointDecrease;
						}
						
						if(pointChange >= maxPointChange)
						{
							if(moveHurtsHelper == false)
							{
								maxPointChange = pointChange;
								moveToMake = m;
							}
							else if(hurtHelperAmount < minHurtHelperAmount)
							{
								minHurtHelperAmount = hurtHelperAmount;
								maxPointChange = pointChange;
								moveToMake = m;
							}
						}
					}
				}
			}
		}
		
		if(maxPointChange == 0 && helpFullIndirectly)
		{
			if(findBestIndirectMove(helper, receiverOfHelp, myBoard) != null)
				moveToMake = findBestIndirectMove(helper, receiverOfHelp, myBoard);
			else
				{
					log.info("FINDBESTINDIRECTMOVE RETURNED null! Couldn't find a good indirect move. ");
					if(callNeutralIfCantMakeMove)
					{
						return makeNeutralMove(-1, getPlayerIndex(receiverOfHelp));
					}
				}
		}
		
		//log.info("Most helpful move that player " + getPlayerIndex(helper) + " can do for player " + getPlayerIndex(receiverOfHelp) + " is move " + moveToMake.getX() + ", " + moveToMake.getY() + "to " + moveToMake.getNewX() + ", " + moveToMake.getNewY() + " which has a point change of " + maxPointChange);
		return moveToMake;
	}
	
	public Move makeNeutralMove(int weakCoopId, int strongCoopId)
	{
		log.info(getPlayerIndex(myCorner) + "MAKING NEUTRAL MOVE");
		Statistics myStats = new Statistics();
		int[] myScores = myStats.calculateScores(board);
		
		Move moveToMake = null;
		//TRY TO HELP SELF
		ArrayList<Slot> potentialSlots = getAllSlots();
		int helpAmount = 0;
		for(int i = 0; i < potentialSlots.size(); i++)
		{
			Slot mySlot = potentialSlots.get(i);
			if(board[mySlot.getY()][mySlot.getX()] != 0)
			{
				for(int j = -1; j <= 1; j++)
				{
					Move m = new Move(mySlot.getX(), mySlot.getY(), myCorner.getRelative(j));	
					if(GameEngine.isInBounds(m.getNewX(), m.getNewY()))
					{
						NetMove myNetMove = pointMatrixWholeGame.calcNetMove(m, board);
						if(myNetMove.playerIncreaseIndex == getPlayerIndex(myCorner))
						{
							if(myNetMove.pointIncrease > helpAmount)
							{
								helpAmount = myNetMove.pointIncrease;
								moveToMake = m;
							}
						}
					}
				}
			}
		}
		if(helpAmount > 0)
		{
			if(moveToMake != null)
			{
				log.info(getPlayerIndex(myCorner) + "   Helped ourselves!");
				return moveToMake;
			}
			else
				log.error("Something weird happened, shouldn't have reached here b/c if helpAmount > 0, then moveToMake shouldn't be null");
		}
		
		
		
		
		
		//------------------------------------------------------------------
		//Couldn't help ourselves, so let's try to hurt whoever we can who has the most points.
		log.info(getPlayerIndex(myCorner) + "   Couldn't help ourselves, so let's try to hurt whoever we can who has the most points.");
		class PlayerScore
		{	
			private int score;
			private int index;
			public PlayerScore(int index, int score)
			{
				this.index = index;
				this.score = score;
			}
			public int getPlayerScore()
			{
				return score;
			}
			public int getPlayerIndex()
			{
				return index;
			}
		}
		
		ArrayList<PlayerScore> myPlayerScore = new ArrayList<PlayerScore>();
		
		//add to array list players who we want to hurt, along with their scores. 
		for(int i = 0; i < 6; i++)
		{
			if(i != strongCoopId && i != weakCoopId && i != getPlayerIndex(myCorner))
			{
				myPlayerScore.add(new PlayerScore(i, myScores[i]));
			}
		}
		
		//Sort the players using bubble sort
		
		boolean swap = true;
		while(swap)
		{
			swap = false;
			for(int i = 0; i < myPlayerScore.size()-1; i++)
			{
				if(myPlayerScore.get(i).getPlayerScore() < myPlayerScore.get(i+1).getPlayerScore())
				{
					Collections.swap(myPlayerScore, i, i+1);
					swap = true;
				}
			}
		}
		
		//List is now sorted. If we find a move that hurts someone, make it. 
		for(int i = 0; i < myPlayerScore.size(); i++)
		{
			Move m = findMostHurtfulMove(myCorner, getDirection(myPlayerScore.get(i).getPlayerIndex()), board);
			NetMove myNetMove = pointMatrixWholeGame.calcNetMove(m, board);
			if(myNetMove.playerDecreaseIndex == myPlayerScore.get(i).getPlayerIndex())
			{
				log.info(getPlayerIndex(myCorner) + "   Found a move that hurts player " + myPlayerScore.get(i).getPlayerIndex() + " by " + myNetMove.pointDecrease + "points");
				return m;
			}
		}
		
		
		log.info(getPlayerIndex(myCorner) + "   Couldn't hurt any noncooperators, let's try helping them. ");
		//-----------------------------------------------------------
		//If we've reached here, then we couldn't hurt any noncooperators, so let's try helping them.
		log.info(getPlayerIndex(myCorner) + "   Couldn't hurt any noncooperators, so let's try helping them.");
		for(int i = myPlayerScore.size()-1; i >=0 ; i--)
		{
			Move m = findMostHelpfulMove(myCorner, getDirection(myPlayerScore.get(i).getPlayerIndex()), board);
			NetMove myNetMove = pointMatrixWholeGame.calcNetMove(m, board);
			if(myNetMove.playerIncreaseIndex == myPlayerScore.get(i).getPlayerIndex())
			{
				log.info(getPlayerIndex(myCorner) + "   Found a move that helps player " + myPlayerScore.get(i).getPlayerIndex() + " by " + myNetMove.pointIncrease + "points");
				return m;
			}
		}
		
		
		//---------------------------------------------------
		//Couldn't help any noncooperators, let's try helping weak cooperator
		if(weakCoopId != -1)
		{
			log.info(getPlayerIndex(myCorner) + "   Couldn't help any noncooperators, let's try helping weak cooperator");
			
			Move m = findMostHelpfulMove(myCorner, getDirection(weakCoopId), board);
			NetMove myNetMove = pointMatrixWholeGame.calcNetMove(m, board);
			if(myNetMove.playerIncreaseIndex == weakCoopId)
			{
				log.info(getPlayerIndex(myCorner) + "   Found a move that helps our weak cooperator, player " + weakCoopId + ", by " + myNetMove.pointIncrease + "points");
				return m;
			}
		}
		
		
		//---------------------------------------------------
		//Couldn't help weak cooperator, let's try hurting him
		if(weakCoopId != -1)
		{
			log.info(getPlayerIndex(myCorner) + "   Couldn't help weak cooeprator, let's try hurting him");
			
			Move m = findMostHurtfulMove(myCorner, getDirection(weakCoopId), board);
			NetMove myNetMove = pointMatrixWholeGame.calcNetMove(m, board);
			if(myNetMove.playerDecreaseIndex == weakCoopId)
			{
				log.info(getPlayerIndex(myCorner) + "   Found a move that hurts our weak cooperator, player " + weakCoopId + ", by " + myNetMove.pointDecrease + "points");
				return m;
			}
		}
		
		//-------------------------------------------
		//Last resort, do whatever move it can that hurts our strong cooperator the least, if we have one. 
		moveToMake = null;
		if(strongCoopId != -1)
		{
			log.info("Last resort.  Making whatever move it can that hurts our strong cooperator the least");
			return findMostHelpfulMove(myCorner, getDirection(strongCoopId), board); //If we've reached here, it is actually not possible to help our strong cooperator, so this will actually return the move that hurts him the least. 
		}
		else
		{
			log.info("Last resort. Making whatever move it can");
			return findMostHelpfulMove(myCorner, getDirection(0), board); //Basically, this means return the best move possible.  This is our last resort. 
		}
	}
	
	
	
	public boolean didCooperatorMakeBestIndirectMoveForUs(Move moveToUs, Direction helper, Direction receiverOfHelp, int[][] myBoard)
	{
		//We want their move to be in our direction (to get the pile closer to us), and also to be a pile worth moving - one that is a large number of coins, and also close to us.
		
		//First check that they moved in our direction. 
		int distanceOfPileBeforeMove = GameEngine.getDistance(new Point(moveToUs.getX(), moveToUs.getY()), getHomePoint(getPlayerIndex(receiverOfHelp)));
		int distanceOfPileAfterMove = GameEngine.getDistance(new Point(moveToUs.getNewX(), moveToUs.getNewY()), getHomePoint(getPlayerIndex(receiverOfHelp)));
		if(distanceOfPileAfterMove > distanceOfPileBeforeMove)
		{
			return false;
		}
		
		log.info("Finding the best indirect move that player " + getPlayerIndex(helper) + "  could have done for us (player " + getPlayerIndex(receiverOfHelp) + ")");
		double maxValue = 0.0;
		ArrayList<Slot> potentialSlots = getAllSlots();
		for(int i = 0; i < potentialSlots.size(); i++)
		{
			Slot mySlot = potentialSlots.get(i);
			if(myBoard[mySlot.getY()][mySlot.getX()] != 0)
			{
				if(GameEngine.getDistance(new Point(mySlot.getX(), mySlot.getY()), getHomePoint(getPlayerIndex(receiverOfHelp))) < GameEngine.getDistance(new Point(mySlot.getX(), mySlot.getY()), getHomePoint(getPlayerIndex(helper)))) //If tile is closer to the receiverOfHelp than it is to helper (we don't want to move a pile of coins that is closer to us towards them)
				{
					for(int j = -1; j <= 1; j++)
					{
						Move m = new Move(mySlot.getX(), mySlot.getY(), helper.getRelative(j));
						if(GameEngine.isInBounds(m.getNewX(), m.getNewY()))
						{
							Slot myNewSlot = new Slot(m.getNewX(), m.getNewY());
							int initialDistance = GameEngine.getDistance(new Point(mySlot.getX(), mySlot.getY()), getHomePoint(getPlayerIndex(receiverOfHelp)));
							int distanceAfterMove = GameEngine.getDistance(new Point(myNewSlot.getX(), myNewSlot.getY()), getHomePoint(getPlayerIndex(receiverOfHelp)));
							if(distanceAfterMove < initialDistance) //if it's actually moving the coins closer to them. 
							{
								int distanceFromPerimeter = findDistanceFromPerimeter(myNewSlot, getPlayerIndex(receiverOfHelp));
								int distanceFromHome = GameEngine.getDistance(new Point(m.getNewX(), m.getNewY()), getHomePoint(getPlayerIndex(receiverOfHelp)));
								double value = 1.0*myBoard[mySlot.getY()][mySlot.getX()]/distanceFromPerimeter + 1.0/distanceFromHome;
								if(value > maxValue)
								{
									maxValue = value;
								}
							}
						}
					}
				}
			}
		}
		
		  
		
		log.info("The most helpful indirect move that should have been made to us was of value: " + maxValue);
		
		Slot newSlot = new Slot(moveToUs.getNewX(), moveToUs.getNewY());
		int distanceFromPerimeter = findDistanceFromPerimeter(newSlot, getPlayerIndex(receiverOfHelp));
		int distanceFromHome = GameEngine.getDistance(new Point(moveToUs.getNewX(), moveToUs.getNewY()), getHomePoint(getPlayerIndex(receiverOfHelp)));
		double value = 1.0*myBoard[moveToUs.getY()][moveToUs.getX()]/distanceFromPerimeter + 1.0/distanceFromHome;
		
		log.info("The move they actually made to us was of value: " + value);
		
		//If the move they made was of value 80% or better than the value of what we deem the best indirect move towards us, then return true. 
		if(value >= .8*maxValue)
		{
			log.info("This is a good indirect move!");
			return true;
		}
		else
			{
				log.info("Not a good indirect move");
				return false;
			}
	}
	
	public Move findBestIndirectMove(Direction helper, Direction receiverOfHelp, int[][] myBoard)
	{
		log.info("Player " + getPlayerIndex(helper) + "  is finding best indirect move for player " + getPlayerIndex(receiverOfHelp));
		double maxValue = 0.0;
		Move moveToMake = null; 
		ArrayList<Slot> potentialSlots = getAllSlots();
		for(int i = 0; i < potentialSlots.size(); i++)
		{
			Slot mySlot = potentialSlots.get(i);
			if(myBoard[mySlot.getY()][mySlot.getX()] != 0)
			{
				if(GameEngine.getDistance(new Point(mySlot.getX(), mySlot.getY()), getHomePoint(getPlayerIndex(receiverOfHelp))) < GameEngine.getDistance(new Point(mySlot.getX(), mySlot.getY()), getHomePoint(getPlayerIndex(helper)))) //If tile is closer to the receiverOfHelp than it is to helper (we don't want to move a pile of coins that is closer to us towards them)
				{
					for(int j = -1; j <= 1; j++)
					{
						Move m = new Move(mySlot.getX(), mySlot.getY(), helper.getRelative(j));
						if(GameEngine.isInBounds(m.getNewX(), m.getNewY()))
						{
							Slot myNewSlot = new Slot(m.getNewX(), m.getNewY());
							int initialDistance = GameEngine.getDistance(new Point(mySlot.getX(), mySlot.getY()), getHomePoint(getPlayerIndex(receiverOfHelp)));
							int distanceAfterMove = GameEngine.getDistance(new Point(myNewSlot.getX(), myNewSlot.getY()), getHomePoint(getPlayerIndex(receiverOfHelp)));
							if(distanceAfterMove < initialDistance) //if it's actually moving the coins closer to them. 
							{
								int distanceFromPerimeter = findDistanceFromPerimeter(myNewSlot, getPlayerIndex(receiverOfHelp));
								int distanceFromHome = GameEngine.getDistance(new Point(m.getNewX(), m.getNewY()), getHomePoint(getPlayerIndex(receiverOfHelp)));
								double value = 1.0*myBoard[mySlot.getY()][mySlot.getX()]/distanceFromPerimeter + 1.0/distanceFromHome;
								if(value > maxValue)
								{
									maxValue = value;
									moveToMake = m;
								}
							}
						}
					}
				}
			}
		}
		
		log.info("The most helpful indirect move was of value: " + maxValue);
		
		return moveToMake;
	}
	
	
	public int findDistanceFromPerimeter(Slot mySlot, int playerIndex)
	{
		ArrayList<Slot> slotList = getPerimeterSlots(playerIndex);
		int minDistance = 20;
		for(int i = 0; i < slotList.size(); i++)
		{
			Slot perimeterSlot = slotList.get(i);
			Point myPerimeterPoint = new Point(perimeterSlot.getX(), perimeterSlot.getY());
			int thisDistance = GameEngine.getDistance(myPerimeterPoint, new Point(mySlot.getX(), mySlot.getY()));
			if(thisDistance < minDistance)
			{
				minDistance = thisDistance;
			}
		}
		return minDistance;
	}
	
	/* Returns the most hurtful move that player "hurter" can do for player "receiverOfHurtfulMove".
	 * The most hurtful move is the one that decreases receiverOfHelp's score by the most points
	 * (or increases it by the least points if no hurtful moves are possible). 
	 */
	public Move findMostHurtfulMove(Direction hurter, Direction receiverOfHurtfulMove, int[][] myBoard)
	{
		int minPointChange = 122;
		Move moveToMake = new Move(8,4,myCorner.getOpposite()); //initialize moveToMake to a random move in case it finds that no moves are possible.
		
		ArrayList<Slot> potentialSlots = getAllSlots();
		for(int i = 0; i < potentialSlots.size(); i++)
		{
			Slot mySlot = potentialSlots.get(i);
			if(myBoard[mySlot.getY()][mySlot.getX()] != 0)
			{
				for(int j = -1; j <= 1; j++)
				{
					Move m = new Move(mySlot.getX(), mySlot.getY(), hurter.getRelative(j));
					if(GameEngine.isInBounds(m.getNewX(), m.getNewY()))
					{
						NetMove myNetMove = pointMatrixWholeGame.calcNetMove(m, myBoard);
						int pointChange = 0;
						if(myNetMove.playerDecreaseIndex == getPlayerIndex(receiverOfHurtfulMove))
						{
							pointChange = myNetMove.pointDecrease;
						}
						else if(myNetMove.playerIncreaseIndex != getPlayerIndex(receiverOfHurtfulMove))
						{
							pointChange = 0;
						}
						else if(myNetMove.playerIncreaseIndex == getPlayerIndex(receiverOfHurtfulMove))
						{
							pointChange = myNetMove.pointIncrease;
						}
						
						if(pointChange <= minPointChange)
						{
							minPointChange = pointChange;
							moveToMake = m;
						}
					}
				}
			}
		}
		return moveToMake;
	}
	
	
	public Move generateRandomDirectionMove(Direction dir, int depth)
	{
		if(depth > 300)
		{
			log.info("couldn't make a move");
			return new Move(0,0,Direction.NE);
		}
		log.info("    direction to return favor: " + dir.name());
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
			return generateRandomDirectionMove(dir, depth+1);
		
		if(board != null&& board[n2][n1] == 0)
			return generateRandomDirectionMove(dir, depth+1);
		if(!GameEngine.isValidDirectionForCellAndHome(dir, myCorner))
			return generateRandomDirectionMove(dir, depth+1);
		
		if(!GameEngine.isInBounds(n1+dir.getDx(), n2+dir.getDy()))
			return generateRandomDirectionMove(dir, depth+1);
		
		Move m = new Move(n1, n2,dir);
		return m;
	}
	
	
	
	public Move generateRandomMove(int depth)
	{
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
	
	/*Returns an ArrayList of all 61 slots on the board */
	public static ArrayList<Slot> getAllSlots()
	{
		ArrayList<Slot> slotListAll = new ArrayList<Slot>();
		for(int y = 0; y < 9; y++)
		{
			for(int x = 0; x < 17; x++)
			{
				if(GameEngine.isInBounds(x, y))
				{
					slotListAll.add(new Slot(x,y));
				}
			}
		}
		
		return slotListAll;
	}
	
	public int getPlayerIndex(Direction d)
	{
		if(d.name().equals("NW"))
			return 0;
		else if(d.name().equals("NE"))
			return 1;
		else if(d.name().equals("E"))
			return 2;
		else if(d.name().equals("SE"))
			return 3;
		else if(d.name().equals("SW"))
			return 4;
		return 5; //W
	}
	
	public Direction getDirection(int d)
	{
		if( d == 0 )
			return Direction.NW;
		if( d == 1 )
			return Direction.NE;
		if( d == 2 )
			return Direction.E;
		if( d == 3 )
			return Direction.SE;
		if( d == 4 )
			return Direction.SW;
		
		return Direction.W;
	}
	
	/*Takes a player's index and returns the coordinates of his home slot */
	public static Point getHomePoint(int playerIndex)
	{
		Point homePoint = new Point(4,0);
		if(playerIndex == 0)
			homePoint = new Point(4,0); //NW
		else if(playerIndex == 1)
			homePoint = new Point(12,0); //NE
		else if(playerIndex == 2)
			homePoint = new Point(16,4);//E
		else if(playerIndex == 3)
			homePoint = new Point(12,8);//SE
		else if(playerIndex == 4)
			homePoint = new Point(4,8);//SW
		else if(playerIndex == 5)
			homePoint = new Point(0,4);//W
		
		return homePoint;
	}
	
	/*Takes a player location and a slot and returns how many points that slot is worth for that player.
	 */ 
	public static int getBonusFactor(int playerIndex, Slot mySlot)
	{	
		Point homePoint = getHomePoint(playerIndex);
		Point myPoint = new Point(mySlot.getX(), mySlot.getY());
		int distance = GameEngine.getDistance(homePoint, myPoint); //this is "d"
		
		int closestNeighborDistance = 20;
		for(int i = 0; i < 6; i++)
		{
			if(i != playerIndex)
			{
				Point neighbor = getHomePoint(i);
				int neighborDistance = GameEngine.getDistance(neighbor, myPoint);
				if(neighborDistance < closestNeighborDistance)
				{
					closestNeighborDistance = neighborDistance; //this is "e"
				}
			}
		}
		return Math.max(0, closestNeighborDistance - distance); // returns e - d
	}
	
	public ArrayList<Slot> getPerimeterSlots(int playerIndex)
	{
		ArrayList<Slot> mySlotList = new ArrayList<Slot>();
		if(playerIndex == 0)
		{
			mySlotList.add(new Slot(3,1));
			mySlotList.add(new Slot(4,2));
			mySlotList.add(new Slot(7,3));
			mySlotList.add(new Slot(7,1));
			mySlotList.add(new Slot(6,0));
		}
		else if(playerIndex == 1)
		{
			mySlotList.add(new Slot(10,0));
			mySlotList.add(new Slot(9,1));
			mySlotList.add(new Slot(9,3));
			mySlotList.add(new Slot(12,2));
			mySlotList.add(new Slot(13,1));
		}
		else if(playerIndex == 2)
		{
			mySlotList.add(new Slot(15,3));
			mySlotList.add(new Slot(13,3));
			mySlotList.add(new Slot(10,4));
			mySlotList.add(new Slot(13,5));
			mySlotList.add(new Slot(15,5));
		}
		else if(playerIndex == 3)
		{
			mySlotList.add(new Slot(13,7));
			mySlotList.add(new Slot(12,6));
			mySlotList.add(new Slot(9,5));
			mySlotList.add(new Slot(9,7));
			mySlotList.add(new Slot(10,8));
		}
		else if(playerIndex == 4)
		{
			mySlotList.add(new Slot(6,8));
			mySlotList.add(new Slot(7,7));
			mySlotList.add(new Slot(7,5));
			mySlotList.add(new Slot(4,6));
			mySlotList.add(new Slot(3,7));
		}
		else if(playerIndex == 5)
		{
			mySlotList.add(new Slot(1,5));
			mySlotList.add(new Slot(3,5));
			mySlotList.add(new Slot(6,4));
			mySlotList.add(new Slot(3,3));
			mySlotList.add(new Slot(1,3));
		}
		return mySlotList;
	}
	
	public void determineNextMove(List<MoveResult> previousMoves)
	{
		
		//set up cooperator and non-cooperator variables
		if(firstCooperationEstablished)
		{
			firstCooperator.ratio = recentHelpMatrix[firstCooperator.index][id].getHelpRatio();
		
			if(secondCooperationEstablished)
			{
				secondCooperator.ratio = recentHelpMatrix[secondCooperator.index][id].getHelpRatio();
				int otherIndex = leftDirection + rightDirection + straightDirection - firstCooperator.index - secondCooperator.index;
				if( otherIndex == leftDirection )
					firstNonCooperator = new Cooperator(otherIndex, recentHelpMatrix[otherIndex][id].getHelpRatio(), leftPlayerGoodIndirectCoop);
				else if( otherIndex == rightDirection )
					firstNonCooperator = new Cooperator(otherIndex, recentHelpMatrix[otherIndex][id].getHelpRatio(), rightPlayerGoodIndirectCoop);
				else
					firstNonCooperator = new Cooperator(otherIndex, recentHelpMatrix[otherIndex][id].getHelpRatio(), straightPlayerGoodIndirectCoop);
			}
			else
			{
				if( firstCooperator.index == leftDirection )
				{
					firstNonCooperator = new Cooperator(rightDirection, recentHelpMatrix[rightDirection][id].getHelpRatio(), rightPlayerGoodIndirectCoop);
					secondNonCooperator = new Cooperator(straightDirection, recentHelpMatrix[straightDirection][id].getHelpRatio(), straightPlayerGoodIndirectCoop);
				}
				else if( firstCooperator.index == rightDirection )
				{
					firstNonCooperator = new Cooperator(leftDirection, recentHelpMatrix[leftDirection][id].getHelpRatio(), leftPlayerGoodIndirectCoop);
					secondNonCooperator = new Cooperator(straightDirection, recentHelpMatrix[straightDirection][id].getHelpRatio(), straightPlayerGoodIndirectCoop);
				}
				else
				{
					firstNonCooperator = new Cooperator(rightDirection, recentHelpMatrix[rightDirection][id].getHelpRatio(), rightPlayerGoodIndirectCoop);
					secondNonCooperator = new Cooperator(leftDirection, recentHelpMatrix[leftDirection][id].getHelpRatio(), leftPlayerGoodIndirectCoop);
				}
			}
		}
		else
		{
			firstNonCooperator = new Cooperator(leftDirection, recentHelpMatrix[leftDirection][id].getHelpRatio(), leftPlayerGoodIndirectCoop);
			secondNonCooperator = new Cooperator(straightDirection, recentHelpMatrix[straightDirection][id].getHelpRatio(), straightPlayerGoodIndirectCoop);
			thirdNonCooperator = new Cooperator(rightDirection, recentHelpMatrix[rightDirection][id].getHelpRatio(), rightPlayerGoodIndirectCoop);
		}
		
		if( exploringFirstCooperation || exploringSecondCooperation )
			potentialCooperator.ratio = recentHelpMatrix[potentialCooperator.index][id].getHelpRatio();
		
		highestRatio = -1.0;
		double leftRatio = recentHelpMatrix[leftDirection][id].getHelpRatio();
		double straightRatio = recentHelpMatrix[rightDirection][id].getHelpRatio();
		double rightRatio = recentHelpMatrix[straightDirection][id].getHelpRatio();
		highestRatio = Math.max( Math.max( leftRatio, rightRatio ), straightRatio );
		nextPlayer = null;
		
		//*********************************************************************************************************
		//**************************************currently in a team of three***************************************
		//*********************************************************************************************************
		if( firstCooperationEstablished && secondCooperationEstablished )
		{
			boolean chooseDefaultNext = false;
			
			//being helped more than we are helping, we're cool with that
			if( firstCooperator.ratio >= SWITCH_TO_ONE_THRESHOLD && secondCooperator.ratio >= SWITCH_TO_ONE_THRESHOLD )
			{
				log.info("players "+firstCooperator.index+" and "+secondCooperator.index+" are helping us more than we are them, making no adjustment");
				chooseDefaultNext = true;
			}
			
			//check if first cooperator should become only cooperator
			else if( firstCooperator.ratio >=  SWITCH_TO_ONE_THRESHOLD )
			{
				log.info("the help ratio of player "+firstCooperator.index+" is now above "+SWITCH_TO_ONE_THRESHOLD+", moving to full cooperation");
				nextPlayer = firstCooperator;
				secondCooperationEstablished = false;
				inEvenCooperation = true;
			}
			
			//check if second cooperator should become only cooperator
			else if( secondCooperator.ratio >=  SWITCH_TO_ONE_THRESHOLD )
			{
				log.info("the help ratio of player "+secondCooperator.index+" is now above "+SWITCH_TO_ONE_THRESHOLD+", moving to full cooperation");
				nextPlayer = secondCooperator;
				firstCooperator = secondCooperator;
				secondCooperationEstablished = false;
				inEvenCooperation = true;
			}
			
			//check if other potential cooperator should join the team
			else if( firstNonCooperator.ratio >= SWITCH_TO_ONE_THRESHOLD )
			{
				log.info("the help ratio of player "+firstNonCooperator.index+" is above "+SWITCH_TO_ONE_THRESHOLD+", adding to 3-way coop");
				
				if( firstCooperator.ratio > secondCooperator.ratio )
				{
					secondCooperator = firstCooperator;
				}	
				
				firstCooperator = firstNonCooperator;
				chooseDefaultNext = true;
				inEvenCooperation = true;
			}
			
			//currently helping partners unevenly
			else if( !inEvenCooperation )
			{
				//check if lesser cooperator became enemy
				if( secondCooperator.ratio <= DROP_LESSER_COOPERATOR_THRESHOLD )
				{
					int nextCandidate = builder.getNextBestCooperator(this);
					secondCooperationEstablished = false;
					inEvenCooperation = false;
					log.info("the help ratio of player "+secondCooperator.index+" fell below "+DROP_LESSER_COOPERATOR_THRESHOLD+", reverting to single coop");
					
					//if theres another candidate, attempt to recruit them
					if( nextCandidate != id )
					{
						
						exploringSecondCooperation = true;
						remainingTriesToInitiateCooperation = NUMRECENTMOVES * 2 + 1;
						potentialCooperator = firstNonCooperator;
						inEvenCooperation = true;
						log.info("exploring second coop candidate: "+nextCandidate);
					}
					
					secondCooperator = null;
					nextPlayer = firstCooperator;
				}
				
				//chek if better cooperator became enemy
				else if( firstCooperator.ratio <= DROP_LESSER_COOPERATOR_THRESHOLD )
				{
					int nextCandidate = builder.getNextBestCooperator(this);
					secondCooperationEstablished = false;
					inEvenCooperation = false;
					log.info("the help ratio of player "+firstCooperator.index+" fell below "+DROP_LESSER_COOPERATOR_THRESHOLD+", reverting to single coop");
					
					//if theres another candidate, attempt to recruit them
					if( nextCandidate != id )
					{
						exploringSecondCooperation = true;
						remainingTriesToInitiateCooperation = NUMRECENTMOVES * 2 + 1;
						potentialCooperator = firstNonCooperator;
						inEvenCooperation = true;
						log.info("exploring second coop candidate: "+nextCandidate);
					}
					
					firstCooperator = secondCooperator;
					secondCooperator = null;
					nextPlayer = firstCooperator;
				}
				
				//check if lesser cooperator got his act together
				else if( secondCooperator.ratio >= SECOND_COOPERATION_ESTABLISHED_THRESHOLD )
				{
					inEvenCooperation = true;
					chooseDefaultNext = true;
					log.info("the help ratio of player "+secondCooperator.index+" went back above "+SECOND_COOPERATION_ESTABLISHED_THRESHOLD+", reverting to even coop");
				}
				
				//continue uneven team
				else
				{
					log.info("continuing with uneven coop");
					chooseDefaultNext = true;
				}
			}
			
			//check if first cooperator should be dumped or relative help should be adjusted
			else if ( firstCooperator.ratio <= EXPLORE_NEW_PARTNER_THRESHOLD )
			{
				log.info("the help ratio of player "+firstCooperator.index+" dropped below "+EXPLORE_NEW_PARTNER_THRESHOLD+", making adjustment");
				int nextCandidate = builder.getNextBestCooperator(this);
				
				//adjust relative help
				if( nextCandidate == id )
				{
					Cooperator worseCoop = firstCooperator;
					firstCooperator = secondCooperator;
					secondCooperator = worseCoop;
					inEvenCooperation = false;
					log.info("entering uneven cooperation");
				}
				
				//explore new partner
				else
				{
					secondCooperationEstablished = false;
					exploringSecondCooperation = true;
					remainingTriesToInitiateCooperation = NUMRECENTMOVES * 2 + 1;
					potentialCooperator = firstNonCooperator;
					firstCooperator = secondCooperator;
					secondCooperator = null;
					inEvenCooperation = true;
					log.info("exploring player "+nextCandidate+" as a coop");
				}
				
				nextPlayer = firstCooperator;
			}
			
			//check if second cooperator should be dumped
			else if ( secondCooperator.ratio <= EXPLORE_NEW_PARTNER_THRESHOLD )
			{
				log.info("the help ratio of player "+secondCooperator.index+" dropped below "+EXPLORE_NEW_PARTNER_THRESHOLD+", making adjustment");
				int nextCandidate = builder.getNextBestCooperator(this);
				
				//adjust relative help
				if( nextCandidate == id )
				{
					inEvenCooperation = false;
					log.info("entering uneven cooperation");
				}
				
				//explore new partner
				else
				{
					secondCooperationEstablished = false;
					exploringSecondCooperation = true;
					remainingTriesToInitiateCooperation = NUMRECENTMOVES * 2 + 1;
					secondCooperator = null;
					inEvenCooperation = true;
					potentialCooperator = firstNonCooperator;
					log.info("exploring player "+nextCandidate+" as a coop");
				}
				
				nextPlayer = firstCooperator;
			}
			
			//continue with 3-way cooperation
			else
			{
				log.info("continuing 3 way coop");
				chooseDefaultNext = true;
			}
			
			if( chooseDefaultNext )
			{
				//50% cooperation with each
				if( inEvenCooperation )
				{
					nextPlayer = firstCooperator;
					if( currentRound % 2 == 0 )
						nextPlayer = secondCooperator;
						
				}
				
				//66% cooperation with 1, 33% with the other
				else
				{
					nextPlayer = firstCooperator;
					if( currentRound % 3 == 0 )
						nextPlayer = secondCooperator;
				}
			}
		}
		
		//*********************************************************************************************************
		//**************************************currently in a team of two*****************************************
		//*********************************************************************************************************
		else if( firstCooperationEstablished )
		{
			//search for second cooperator
			if( exploringSecondCooperation )
			{
				log.info("exploring secong cooperation");
				remainingTriesToInitiateCooperation--;
				nextPlayer = firstCooperator;
				if( currentRound % 2 == 0 )
					nextPlayer = potentialCooperator;
				
				//second cooperation established, move to two cooperators mode
				if( potentialCooperator.ratio >= SECOND_COOPERATION_ESTABLISHED_THRESHOLD )
				{
					log.info("player "+potentialCooperator.index+"'s ratio raised above "+SECOND_COOPERATION_ESTABLISHED_THRESHOLD+", entering two coop mode");
					exploringSecondCooperation = false;
					secondCooperationEstablished = true;
					remainingTriesToInitiateCooperation = 0;
					secondCooperator = potentialCooperator;
					potentialCooperator = null;
					inEvenCooperation = true;
				}
				
				//original cooperator got his act together
				else if( firstCooperator.ratio >= SWITCH_TO_ONE_THRESHOLD )
				{
					log.info("player "+firstCooperator.index+" raised his ratio above "+SWITCH_TO_ONE_THRESHOLD+", returning to full coop");
					exploringSecondCooperation = false;
					remainingTriesToInitiateCooperation = 0;
					potentialCooperator = null;
					inEvenCooperation = true;
					
					nextPlayer = firstCooperator;
				}
				
				//someone else is sending us signals
				else if( firstNonCooperator.ratio >=  UNSOLICITED_SECOND_COOPERATION_ESTABLISHED_THRESHOLD || secondNonCooperator.ratio >=  UNSOLICITED_SECOND_COOPERATION_ESTABLISHED_THRESHOLD )
				{
					log.info("player "+firstNonCooperator.index+"'s ratio is above "+UNSOLICITED_SECOND_COOPERATION_ESTABLISHED_THRESHOLD+", picked up signal and making second coop");
					secondCooperator = ( firstNonCooperator.ratio >=  UNSOLICITED_SECOND_COOPERATION_ESTABLISHED_THRESHOLD ) ? firstNonCooperator : secondNonCooperator;
					exploringSecondCooperation = false;
					secondCooperationEstablished = true;
					remainingTriesToInitiateCooperation = 0;
					potentialCooperator = null;
					inEvenCooperation = true;
					
					if( nextPlayer != firstCooperator )
						nextPlayer = secondCooperator;
				}
				
				//potential cooperator did not take, try second if available
				else if( remainingTriesToInitiateCooperation == 0 )
				{
					log.info("player "+potentialCooperator.index+" did not respond to overtures, abandoning");
					nextPlayer = firstCooperator;
					int nextCandidate = builder.getNextBestCooperator(this);
					
					//if another candidate, attempt to recruit him
					if( nextCandidate != id )
					{
						log.info("exploring player "+nextCandidate+" as a second coop");
						potentialCooperator = ( firstNonCooperator.index == nextCandidate ) ? firstNonCooperator : secondNonCooperator;
						remainingTriesToInitiateCooperation = NUMRECENTMOVES * 2 + 1;
					}
					
					//no candidates, stick with the original
					else
					{
						log.info("entering uneven single coop");
						remainingTriesToInitiateCooperation = 0;
						exploringSecondCooperation = false;
						inEvenCooperation = false;
					}
				}
			}
			
			//first cooperator is not very good
			else if( firstCooperator.ratio <= TRY_FOR_SECOND_PARTNER_THRESHOLD && firstCooperator.ratio > DROP_PRIMARY_COOPERATOR_THRESHOLD )
			{
				int nextCandidate = builder.getNextBestCooperator(this);
				log.info("player "+firstCooperator.index+"'s ratio fell below "+TRY_FOR_SECOND_PARTNER_THRESHOLD+", adjusting coop");
				
				//there is a candidate for a second cooperator, attempt to recruit him
				if( nextCandidate != id )
				{
					log.info("exploring "+nextCandidate+" as a coop");
					potentialCooperator = ( firstNonCooperator.index == nextCandidate ) ? firstNonCooperator : secondNonCooperator;
					exploringSecondCooperation = true;
					remainingTriesToInitiateCooperation = NUMRECENTMOVES * 2;
					inEvenCooperation = true;
					nextPlayer = potentialCooperator;
				}
				
				//no other candidates, reduce help to current
				else
				{
					log.info("entering uneven coop");
					inEvenCooperation = false;
					nextPlayer = new Cooperator( id, 0, true );
				}
			}
			
			//check if partner should be dropped entirely
			else if( firstCooperator.ratio < DROP_PRIMARY_COOPERATOR_THRESHOLD )
			{
				log.info("player "+firstCooperator.index+"'s ratio fell below "+DROP_PRIMARY_COOPERATOR_THRESHOLD+". dropping as a partner");
				int nextCandidate = builder.getNextBestCooperator(this);
				
				
				//there is a candidate for a second cooperator, attempt to recruit him
				if( nextCandidate != id )
				{
					log.info("exploring "+nextCandidate+" as a partner");
					
					if( firstNonCooperator.index == nextCandidate )
						potentialCooperator = firstNonCooperator;
					else if( secondNonCooperator.index == nextCandidate )
						potentialCooperator = secondNonCooperator;
					else
						potentialCooperator = thirdNonCooperator;
					exploringFirstCooperation = true;
					remainingTriesToInitiateCooperation = NUMRECENTMOVES * 2;
					nextPlayer = potentialCooperator;
				}
				
				//no candidates and no partners, become a hermit
				else
				{
					log.info("entering hermit mode");
					nextPlayer = new Cooperator( id, 0, true );
				}
				
				firstCooperationEstablished = false;
				inEvenCooperation = true;
			}
			
			//really strong signals being sent by non-cooperator 1
			else if( ( firstNonCooperator.ratio - firstCooperator.ratio ) > LEAVE_PRIMARY_COOPERATOR_THRESHOLD )
			{
				log.info("player "+firstNonCooperator.index+"'s ratio is at least "+LEAVE_PRIMARY_COOPERATOR_THRESHOLD+
						 " greater than "+firstCooperator.index+". adding him and entering two partner mode");
				secondCooperationEstablished = true;
				secondCooperator = firstCooperator;
				firstCooperator = firstNonCooperator;
				inEvenCooperation = true;
				nextPlayer = firstCooperator;
			}
			
			//really strong signals being sent by non-cooperator 2
			else if( ( secondNonCooperator.ratio - firstCooperator.ratio ) > LEAVE_PRIMARY_COOPERATOR_THRESHOLD )
			{
				log.info("player "+secondNonCooperator.index+"'s ratio is at least "+LEAVE_PRIMARY_COOPERATOR_THRESHOLD+
						 " greater than "+firstCooperator.index+". adding him and entering two partner mode");
				secondCooperationEstablished = true;
				secondCooperator = firstCooperator;
				firstCooperator = secondNonCooperator;
				inEvenCooperation = true;
				nextPlayer = firstCooperator;
			}
			
			//continue with cooperation
			else
			{
				log.info("continuing 2-way cooperation");
				ArrayList<HelpRatioComponents> recentMoves = recentHelpMatrix[firstCooperator.index][id].getMoves();
				if( recentMoves.get( recentMoves.size() - 1 ).max == 0 )
				{
					MoveResult hisMove = previousMoves.get(0);
					for( MoveResult m : previousMoves ) 
					{
						if( m.getPlayerId() == firstCooperator.index )
							hisMove = m;
					}
					firstCooperator.goodIndirectCoop = didCooperatorMakeBestIndirectMoveForUs( hisMove.getMove(), getDirection(firstCooperator.index), myCorner, allBoards.get( allBoards.size() - 2 ));
				}
				
				if( inEvenCooperation )
				{
					nextPlayer = firstCooperator;
				}
				else
				{
					if( currentRound % 2 == 0 )
						nextPlayer = firstCooperator;
					//on an off-move with a half cooperator, set self as next player to help
					else
						nextPlayer = new Cooperator( id, 0, true );
				}
			}
		}
		
		//*********************************************************************************************************
		//**************************************currently not in a team********************************************
		//*********************************************************************************************************
		else if( exploringFirstCooperation )
		{
			log.info("exploring first cooperation");
			remainingTriesToInitiateCooperation--;
			nextPlayer = potentialCooperator;
			
			//targeted cooperator responded
			if( potentialCooperator.ratio >= COOPERATION_ESTABLISHED_THRESHOLD )
			{
				log.info("player "+potentialCooperator.index+"'s ratio went above "+COOPERATION_ESTABLISHED_THRESHOLD+". accepting as a coop");
				exploringFirstCooperation = false;
				inEvenCooperation = true;
				firstCooperationEstablished = true;
				firstCooperator = potentialCooperator;
				potentialCooperator = null;
				remainingTriesToInitiateCooperation = 0;
			}
			
			//someone is sending us signals
			else if( highestRatio >= UNSOLICITED_COOPERATION_ESTABLISHED_THRESHOLD )
			{
				if( firstNonCooperator.ratio == highestRatio )
					firstCooperator = firstNonCooperator;
				else if( secondNonCooperator.ratio == highestRatio )
					firstCooperator = secondNonCooperator;
				else
					firstCooperator = thirdNonCooperator;
				
				log.info("player "+firstCooperator.index+"'s ratio went above "+UNSOLICITED_COOPERATION_ESTABLISHED_THRESHOLD+". accepting as a coop");
				
				firstCooperationEstablished = true;
				inEvenCooperation = true;
				exploringFirstCooperation = false;
				remainingTriesToInitiateCooperation = 0;
				nextPlayer = firstCooperator;
			}
			
			//potential cooperator did not respond
			else if( remainingTriesToInitiateCooperation == 0 )
			{
				log.info("player "+potentialCooperator.index+" did not respond. giving up");
				int nextCandidate = builder.getNextBestCooperator(this);
				
				//explore new candidate
				if( id != nextCandidate )
				{
					log.info("exploring "+nextCandidate+" as a coop");
					
					remainingTriesToInitiateCooperation = NUMRECENTMOVES * 2;
					if( nextCandidate == firstNonCooperator.index )
						potentialCooperator = firstNonCooperator;
					else if( nextCandidate == secondNonCooperator.index )
						potentialCooperator = secondNonCooperator;
					else
						potentialCooperator = thirdNonCooperator;
					
					nextPlayer = potentialCooperator;
				}
				
				//enter hermit mode
				else
				{
					log.info("entering hermit mode");
					exploringFirstCooperation = false;
					nextPlayer = new Cooperator(id, 0, true);
				}
			}
		}
		
		//*********************************************************************************************************
		//**************************************currently in hermit mode*******************************************
		//*********************************************************************************************************
		//someone is sending us signals
		//TODO add check for player not dumb
		else if( highestRatio >= LEAVE_HERMIT_MODE_THRESHOLD )
		{
			firstCooperationEstablished = true;
			if( firstNonCooperator.ratio == highestRatio )
				firstCooperator = firstNonCooperator;
			else if( secondNonCooperator.ratio == highestRatio )
				firstCooperator = secondNonCooperator;
			else
				firstCooperator = thirdNonCooperator;
			
			inEvenCooperation = true;
			nextPlayer = firstCooperator;
			log.info("player "+firstCooperator.index+"'s ratio went above "+LEAVE_HERMIT_MODE_THRESHOLD+". leaving hermit mode and accepting as a coop");
		}
		
		//in hermit mode
		else
		{
			log.info("in hermit mode");
			int nextCandidate = builder.getNextBestCooperator(this);
			nextPlayer = new Cooperator(id, 0, true);
			
			//explore new candidate
			if( id != nextCandidate )
			{
				log.info("leaving hermit mode and exploring "+nextCandidate+" as a coop");
				
				remainingTriesToInitiateCooperation = NUMRECENTMOVES * 2;
				if( nextCandidate == firstNonCooperator.index )
					potentialCooperator = firstNonCooperator;
				else if( nextCandidate == secondNonCooperator.index )
					potentialCooperator = secondNonCooperator;
				else
					potentialCooperator = thirdNonCooperator;
				
				nextPlayer = potentialCooperator;
			}
		}
	}
	
}
