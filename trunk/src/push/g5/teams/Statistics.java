package push.g5.teams;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import push.g5.analytics.PointMatrix;
import push.g5.Slot;
import push.g5.g5player;
import push.sim.GameEngine;


/** Statistics that are kept track of using this class :
 * 
 *  1) Total score for each player with the current board configuration. 
 *  2) Based on helpRatio, figure out which players are forming teams
 *  3) Also find out what the ranges of the scores for these players are. 
 *  
 *  Usage : 	
 *  
 *  Input : pass in a g5Player instance, since it uses some of the methods from g5.
 *  Output : scoreArray will contain the scores for each player, in the index corresponding to each player.
    Usage : Call the s.calculateScores after the updateBoardState method from g5Player has been called.  
   		Statistics s = new Statistics(<g5Player instance>);
		int[] scoreArray = s.calculateScores(<board that the g5player has>);
 */

public class Statistics {

	int[] totalScores;
	public g5player player;
	
	/** these two flags can be turned off to remove all stdout from this class. 
	 * 	debug gives the results expected from this class. detailedDebug is about how that was calculated.
	 */
	boolean debug = true , detailedDebug = false;
	
	private Logger log = Logger.getLogger(this.getClass());

	Statistics(g5player player) {
		totalScores = new int[6]; //for the 6 players
		for(int i = 0; i < 6; i++) {
			totalScores[i] = 0;
		}
		this.player = player;
	}
	
	//take the current configuration of the game and return the totalScores for each player
	public int[] calculateScores(int[][] board) {
		
		int i;
		//reset current totalScores
		for(i = 0; i < 6; i++) {
			totalScores[i] = 0;
		}

		ArrayList<Slot> allSlots = player.getAllSlots(); 
		int ownerIndex, coinStackSize, pointsWorth;
		for(i = 0; i < allSlots.size(); i++) {
			Slot currentSlot = allSlots.get(i);
			if(GameEngine.isInBounds(currentSlot.getX(), currentSlot.getY())) {
				ownerIndex = PointMatrix.getOwner(currentSlot);
				if(ownerIndex >= 0 && ownerIndex < 6 ) {
					if(currentSlot.getX() < 17 && currentSlot.getY() < 9) { //board sizes
						coinStackSize = board[currentSlot.getY()][currentSlot.getX()]; //should be 0 if it is invalid or if there are no coins.
						pointsWorth = player.getBonusFactor(ownerIndex,currentSlot);
						totalScores[ownerIndex] += pointsWorth * coinStackSize;
						if (detailedDebug)
							log.info("player : " + ownerIndex + " slot: ("
									+ currentSlot.getY() + ", "
									+ currentSlot.getX() + ") pointsWorth :"
									+ pointsWorth + " coinStackSize : "
									+ coinStackSize + " totalScore :"
									+ totalScores[ownerIndex]);
					}
				}
			}
		}
		
		if(debug) {
			for(i = 0; i < 6; i++ ) {
				log.info("Statistics : player " + i + " scored " + totalScores[i]);
			}
		}
		
		return totalScores;
	}
}
