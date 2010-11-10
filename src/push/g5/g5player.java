package push.g5;

import java.awt.AWTException;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import push.g5.analytics.HelpRatio;
import push.g5.analytics.HelpRatioComponents;
import push.g5.analytics.NetMove;
import push.g5.analytics.PointMatrix;
import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class g5player extends Player{
	int[][] board;
	int[][] boardCopy;
	private Logger log = Logger.getLogger(this.getClass());
	private ArrayList<Direction> playerPositions;
	private int round = 0;
	private PointMatrix pointMatrixWholeGame;
	private ScreenGrabber grabber = new ScreenGrabber();
	
	private int NUMRECENTMOVES = 5;
	private static boolean IS_FIRST = true;
	private int currentRound = -2;
	private int lastCooperator = -1;
	
	private static int CURRENT_GAME_NUMBER;
	private static boolean TAKE_SCREEN_SHOTS = false;
	private static String gameNumberFile = "screen_shots/current_game.txt";
	
//	static
//	{
//		File f = new File(gameNumberFile);
//		Scanner fileReader = null;
//		try {
//			fileReader = new Scanner(f);
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		CURRENT_GAME_NUMBER = fileReader.nextInt();
//		
//		try{
//	    // Create file 
//	    FileWriter fstream = new FileWriter(gameNumberFile);
//	        BufferedWriter out = new BufferedWriter(fstream);
//	    out.write(""+(CURRENT_GAME_NUMBER+1));
//	    //Close the output stream
//	    out.close();
//	    }catch (Exception e){//Catch exception if any
//	      System.err.println("Error: " + e.getMessage());
//	    }
//	}

	@Override
	public void updateBoardState(int[][] board) {
		this.board= board;
		
		if( ( IS_FIRST || currentRound != -2 ) && TAKE_SCREEN_SHOTS )
		{
			IS_FIRST = false;
			try {
				grabber.grabScreen(CURRENT_GAME_NUMBER + "_" + ++currentRound);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Prisoner 0";
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
	
	Direction myCorner;
	boolean cooperationEstablished;
	int cooperatorRelative = -1;
	int numTriesToInitiateCooperation = 0;
	Direction playerToHelp;
	ArrayList<List<MoveResult>> allMoveLists = new ArrayList<List<MoveResult>>(); 
	ArrayList<int[][]> allBoards = new ArrayList<int[][]>();
	
	
	int id;
	@Override
	public void startNewGame(int id, int m,
			ArrayList<Direction> playerPositions) {
		myCorner=playerPositions.get(id);
		this.playerPositions = playerPositions;
		this.id=id;
		cooperationEstablished = false;
		pointMatrixWholeGame = new PointMatrix(this);
	} 

	@Override
	public Move makeMove(List<MoveResult> previousMoves) {
		
		log.info("");
		log.info("--------------------------------------------------");
		log.info(getPlayerIndex(myCorner) + "ROUND: " + round);
		
		allMoveLists.add(previousMoves);
		allBoards.add(copyBoard(board));
		
		pointMatrixWholeGame.addRound(previousMoves, board);
		
		PointMatrix recentPointMatrix = new PointMatrix(this);
		
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
		log.info(getPlayerIndex(myCorner) + "Help ratio matrix from the last " + NUMRECENTMOVES + " rounds:");
		HelpRatio[][] recentHelpMatrix = recentPointMatrix.getHelpMatrix();
		printMatrix(recentHelpMatrix);
		
		double highestRatio = -1.0;
		int cooperatingPlayer = 0;
		for(int i = 0; i < 6; i++)
		{
			if(i != getPlayerIndex(myCorner))
			{
				double thisRatio = recentHelpMatrix[i][getPlayerIndex(myCorner)].getHelpRatio();
				log.info(getPlayerIndex(myCorner) + "    Best possible help amount of player " + i + " to me (player " + getPlayerIndex(myCorner) + ") is: " + recentHelpMatrix[i][getPlayerIndex(myCorner)].getPositivePointsPossible());
				ArrayList<HelpRatioComponents> myHRC = recentHelpMatrix[i][getPlayerIndex(myCorner)].getMoves();
				int theSize = myHRC.size();
				String myString = getPlayerIndex(myCorner) + "               ";
				for(int m = 0; m < theSize; m++)
				{
					myString += " " + myHRC.get(m).max;
				}
				log.info(myString);
				
				
				log.info(getPlayerIndex(myCorner) + "    Worst possible help amount of player " + i + " to me (player " + getPlayerIndex(myCorner) + ") is: " + recentHelpMatrix[i][getPlayerIndex(myCorner)].getNegativePointsPossible());				
				myString = getPlayerIndex(myCorner) + "               ";
				for(int m = 0; m < theSize; m++)
				{
					myString += " " + myHRC.get(m).min;
				}
				log.info(myString);
				
				
				log.info(getPlayerIndex(myCorner) + "    Actual Change in points of player " + i + " to me (player " + getPlayerIndex(myCorner) + ") is: " + recentHelpMatrix[i][getPlayerIndex(myCorner)].getPointChange());
				myString = getPlayerIndex(myCorner) + "               ";
				for(int m = 0; m < theSize; m++)
				{
					myString += " " + myHRC.get(m).change;
				}
				log.info(myString);
				
				log.info(getPlayerIndex(myCorner) + "    Help Ratio of player " + i + " to me (player " + getPlayerIndex(myCorner) + ") is: " + recentHelpMatrix[i][getPlayerIndex(myCorner)].getHelpRatio());
				log.info(getPlayerIndex(myCorner));
				if(thisRatio >= highestRatio)
				{
					highestRatio = thisRatio;
					cooperatingPlayer = i;
				}
			}
		}
		
		
		if(cooperatingPlayer != lastCooperator)
		{
			if(lastCooperator != -1)
			{
				double ratioOfLastCooperator = recentHelpMatrix[lastCooperator][getPlayerIndex(myCorner)].getHelpRatio();
				if(ratioOfLastCooperator >= .9*highestRatio)
				{
					cooperatingPlayer = lastCooperator;
					log.info(getPlayerIndex(myCorner) + " ***Even though our last cooperator didn't have the highest ratio this time, he still is within 10% of the best, so we are sticking with him");
				}
			}
		}

		
		//if there's nothing our last cooperator could have done to help us, let's still cooperate with him.
		if(lastCooperator != -1)
		{
			if(recentHelpMatrix[lastCooperator][getPlayerIndex(myCorner)].getPositivePointsPossible() == 0)  
			{
				cooperatingPlayer = lastCooperator;
			}
		}
		
		log.info(getPlayerIndex(myCorner) + "Found that the player most trying to help me is player " + cooperatingPlayer + " with a ratio of " + highestRatio); 
		
		if(highestRatio <= .7)
		{
			playerToHelp = myCorner.getRelative(cooperatorRelative);
			log.info(getPlayerIndex(myCorner) + "Help ratio from affector player " + getPlayerIndex(playerToHelp) + " to affected player " + getPlayerIndex(myCorner) + " is: " + recentHelpMatrix[getPlayerIndex(playerToHelp)][getPlayerIndex(myCorner)].getHelpRatio());
			
			if(numTriesToInitiateCooperation >= 4 && recentHelpMatrix[getPlayerIndex(playerToHelp)][getPlayerIndex(myCorner)].getHelpRatio() <= .4) 
			{
				if(cooperatorRelative <= 0)
				{
					log.info("He's not cooperating with us. Let's try someone else!");
					cooperatorRelative++;
					playerToHelp = myCorner.getRelative(cooperatorRelative);
					numTriesToInitiateCooperation = 0;
				}
				else
				{
					
					log.info("Couldn't find any cooperators, so make a random move");
					return generateRandomMove(0);
				}
			}
		}
		else
		{
			log.info(getPlayerIndex(myCorner) + "This ratio is > .7, so found someone who might be cooperating with us: Player " + cooperatingPlayer);
			playerToHelp = getDirection(cooperatingPlayer);
			numTriesToInitiateCooperation = 0;
		}
		
		

		
		log.info(getPlayerIndex(myCorner) + "direction we are trying to cooperate with is: " + playerToHelp.name());
		log.info(getPlayerIndex(myCorner) + "index of this direction is: " + getPlayerIndex(playerToHelp));
		lastCooperator = getPlayerIndex(playerToHelp);
		
		Move moveToMake = findMostHelpfulMove(myCorner, playerToHelp, board);
		round++;
		numTriesToInitiateCooperation++;
		log.info(getPlayerIndex(myCorner) + "Move is: " + moveToMake.getX() + ", " + moveToMake.getY() + " to " + moveToMake.getNewX() + ", " + moveToMake.getNewY());
		log.info(getPlayerIndex(myCorner) + "");
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
	}
	
	/* Returns the most helpful move that player "helper" can do for player "receiverOfHelp".
	 * The most helpful move is the one that increases receiverOfHelp's score by the most points
	 * (or decreases it by the least points if no helpful moves are possible). 
	 */
	public Move findMostHelpfulMove(Direction helper, Direction receiverOfHelp, int[][] myBoard)
	{
		return findMostHelpfulMove(helper, receiverOfHelp, myBoard, false);
	}
	
	public Move findMostHelpfulMove(Direction helper, Direction receiverOfHelp, int[][] myBoard, boolean helpFullIndirectly)
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
								hurtHelperAmount = myNetMove.pointDecrease;
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
				log.error("FINDBESTINDIRECTMOVE RETURNED null! Something wrong is happening. ");
		}
		
		//log.info("Most helpful move that player " + getPlayerIndex(helper) + " can do for player " + getPlayerIndex(receiverOfHelp) + " is move " + moveToMake.getX() + ", " + moveToMake.getY() + "to " + moveToMake.getNewX() + ", " + moveToMake.getNewY() + " which has a point change of " + maxPointChange);
		return moveToMake;
	}
	
	//Change this so that it does not hurt itself.  
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
				for(int j = -1; j <= 1; j++)
				{
					Move m = new Move(mySlot.getX(), mySlot.getY(), helper.getRelative(j));	
					if(GameEngine.isInBounds(m.getNewX(), m.getNewY()))
					{
						int distanceFromPerimeter = findDistanceFromPerimeter(new Slot(m.getNewX(), m.getNewY()), getPlayerIndex(receiverOfHelp));
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
	
	/*Takes a player location and returns a list of the slots that pushing coins from could increase that person's score. 
	 *So this would be all of the slots of that persons' color minus the 4 pt one, plus all slots ONE away from a slot of their color. 
	 */  
	
	public ArrayList<Slot> getAllSlots()
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
	public Point getHomePoint(int playerIndex)
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
	public int getBonusFactor(int playerIndex, Slot mySlot)
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
	
}
