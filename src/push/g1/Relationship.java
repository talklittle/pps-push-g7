package push.g1;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import push.sim.*;

/*Models the relationship between our player and other players. 
 * It keeps track of aggressive and cooperative moves: moves 
 * that directly hurt or help our score. 
 * */ 
public class Relationship {
	
	private PlayerBoard ourBoard;
	private int otherPlayerID;
	private ArrayList<MoveResult> moves; 
	private double cooperationScore = 0;
	private Logger log;
	
	// How strongly our player holds grudges.
	// 1.0 is a perfect memory, 0.0 is short-term memory loss.
	public final static double MERCY = 0.9;

	
	public Relationship(int otherPlayerID, PlayerBoard ourBoard)
	{
		this.otherPlayerID = otherPlayerID;
		this.ourBoard = ourBoard;
		moves = new ArrayList<MoveResult>();
		log = Logger.getLogger(GameController.class);
	}
	
	public ArrayList<MoveResult> getMoves()
	{
		return moves;
	}
	
	public void setMoves(ArrayList<MoveResult> moves)
	{
		this.moves = moves;
	}
	
	public void addMove(MoveResult mr)
	{
		if (mr.isSuccess() && mr.getPlayerId() == otherPlayerID)
		{
			moves.add(mr);
			double cscore = findCooperationScore(mr);
			log.trace("New Cooperation Score: " + cscore + " with player " + otherPlayerID);
		}
		
	}
	
	public double getCooperationScore()
	{
		return cooperationScore;
	}
	
	public int getPlayerID()
	{
		return otherPlayerID;
	}
	
	public void setPlayerID(int otherPlayerID)
	{
		this.otherPlayerID = otherPlayerID;
	}
	
	/**
	 * Find the cooperation score of this relationship after a new move
	 * @return The new value already stored as the cooperation score.
	 */
	private double findCooperationScore(MoveResult mr)
	{
		Move m = mr.getMove();
		double oldWeighting = calculateCooperationWeighter(moves.size() - 1);
		// Undo the averaging
		cooperationScore *= oldWeighting;
		// Apply the mercy of forgetting
		cooperationScore *= MERCY;
		
		// Tack on the new cooperation score
		cooperationScore += findMoveCooperation(m);
		// Calculate the new weighting and use it to re-average
		cooperationScore /= 1 + (oldWeighting * MERCY);
		
		log.trace("Re-weighted cooperation score is "+cooperationScore);
		return cooperationScore;
	}
	
	/**
	 * Calculates the value of all the memory weightings added together 
	 */
	private static double calculateCooperationWeighter(int numberOfMoves) {
	    double result = 0.0;
	    for(int i = 0; i < numberOfMoves; i++) {
	        result += Math.pow(MERCY, i);
	    }
	    return result;
	}
	
	
	/*To Do: Take into account the size of the stack of coins that was moved*/
	public int findMoveCooperation(Move m)
	{
	    // TODO: take the amount moved into account too.
		/*If the move did not start or end on one of our hexagons, return 0*/
		
		Hexagon startHex = ourBoard.getHexAtPoint(m.getX(), m.getY());
		Hexagon endHex = ourBoard.getHexAtPoint(m.getNewX(), m.getNewY());
		
		if (!ourBoard.isHexagonOurs(startHex) && !ourBoard.isHexagonOurs(endHex))
		{
			log.trace("Neutral move.");
			return 0;
		}
		/*If this move moved a piece off of our hexagons, return -1*/ 
		else if (ourBoard.isHexagonOurs(startHex) && !ourBoard.isHexagonOurs(endHex))
		{
			log.trace("Hurtful move.");
			return -1;
		}
		/*If this move moved a piece to a lower valued hexagon from our collection of hexagons, return a negative score*/
		else if (startHex.getMultiplier() > endHex.getMultiplier() && ourBoard.isHexagonOurs(startHex) && ourBoard.isHexagonOurs(endHex))
		{	
			log.trace("Multiplier hurtful move.");
			int diff = endHex.getMultiplier() - startHex.getMultiplier();
			log.trace("diff: " + diff);
			return (endHex.getMultiplier() - startHex.getMultiplier());
		}
		/*If this move moved a piece to a higher valued hexagon from our collection of hexagons, return a positive*/
		else if (startHex.getMultiplier() <= endHex.getMultiplier() && ourBoard.isHexagonOurs(startHex) && ourBoard.isHexagonOurs(endHex))
		{
			log.trace("Multiplier cooperative move.");
			int diff = endHex.getMultiplier() - startHex.getMultiplier();
			log.trace("diff: " + diff);
			return (endHex.getMultiplier() - startHex.getMultiplier());
		}
		/*If this move moved a coin onto one of our hexagons return a positive score*/
		//TODO: determine if 1 is the right amount to return here.
		else if (ourBoard.isHexagonOurs(startHex) && !ourBoard.isHexagonOurs(endHex))
		{
			log.trace("Cooperative move.");
			return 1;
		}
		
		return 0;
	}
	
	public String toString()
	{
		return "Relationship between us and " + otherPlayerID + ": Cooperation Score - " + cooperationScore;
	}
}
