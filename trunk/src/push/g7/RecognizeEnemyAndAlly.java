package push.g7;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

public class RecognizeEnemyAndAlly {
	Direction myCorner;
	HashMap<Direction, ArrayList<Move>> players;
	ArrayList<Direction> ally = new ArrayList<Direction>();
	ArrayList<Direction> enemy = new ArrayList<Direction>();
	int[] strongAssistanceScore = new int[6];
	int[] weakAssistanceScore = new int[6];
	List<Direction> playerPositions;
	Map<Direction, Integer> directionToID;
	
	// Keep track of scores
	Scores scores;
	
	// Detect illegal moves
	boolean[] validPlayers = {true, true, true, true, true, true};
	ArrayList< ArrayList<Move> > allValidMoves = new ArrayList< ArrayList<Move> >();
	
	private ScoreZones scoreZones;
	
	private static final Logger logger = Logger.getLogger(RecognizeEnemyAndAlly.class);
	
	public RecognizeEnemyAndAlly(Direction myCorner, List<Direction> playerPositions,
			Map<Direction, Integer> directionToID,
			ScoreZones scoreZones, int[][] initialBoard) {
		this(myCorner, playerPositions, directionToID, scoreZones, initialBoard, null, null);
	}
	
	public RecognizeEnemyAndAlly(Direction myCorner, List<Direction> playerPositions,
			Map<Direction, Integer> directionToID,
			ScoreZones scoreZones, int[][] initialBoard,
			Collection<Direction> initialAllies, Collection<Direction> initialEnemies) {
		this.myCorner = myCorner;
		this.playerPositions = playerPositions;
		this.directionToID = directionToID;
		this.scoreZones = scoreZones;
		if (initialAllies != null)
			ally.addAll(initialAllies);
		if (initialEnemies != null)
			enemy.addAll(initialEnemies);
		
		scores = new Scores(validPlayers);
		scores.updateScores(initialBoard, playerPositions, directionToID);
	}
	
	public void updateScores(int[][] board, List<Direction> playerPositions, Map<Direction, Integer> directionToID) {
		scores.updateScores(board, playerPositions, directionToID);
	}
	
	public void updateAlliances(List<MoveResult> previousMoves) {
		if (previousMoves == null || previousMoves.size() == 0)
			return;
		
		ally.clear();
		enemy.clear();
		
		for (MoveResult result : previousMoves) {
			// Skip myself
			if (myCorner.equals(playerPositions.get(result.getPlayerId()))) {
				logger.debug("updateAlliances: skip myself id="+result.getPlayerId());
				continue;
			}
			
			int playerId = result.getPlayerId();
			Move move = result.getMove();
			Point oldPoint = new Point(move.getX(), move.getY());
			Point newPoint = new Point(move.getNewX(), move.getNewY());
			int oldDistance = GameEngine.getDistance(oldPoint, myCorner.getHome());
			int newDistance = GameEngine.getDistance(newPoint, myCorner.getHome());
			
			// If the distance decreases, then it's a good move (weak assistance)
			if (newDistance < oldDistance) {
				weakAssistanceScore[playerId]++;
			} else if (newDistance > oldDistance) {
				weakAssistanceScore[playerId]--;
			}
			
//			// If the move is INVALID FOR US, then assume it's a good move (weak assistance)
//			if (!GameEngine.isValidDirectionForCellAndHome(move.getDirection(), myCorner)) {
//				weakAssistanceScore[playerId]++;
//			} else {
//				weakAssistanceScore[playerId]--;
//			}
			logger.info("weakAssistanceScore["+playerId+"]="+weakAssistanceScore[playerId]+" -- "
					+ move.toString());
			
			int multiplierDelta = scoreZones.getMultiplier(newPoint) - scoreZones.getMultiplier(oldPoint);
			
			// If the move changed our score, it is a strong assistance (or opposite) indicator
			if (scoreZones.isPointBelongTo(newPoint, myCorner)) {
				// If the move shifted stack between 2 of our own points, use multiplierDelta.
				if (scoreZones.isPointBelongTo(oldPoint, myCorner)) {
					strongAssistanceScore[playerId] += multiplierDelta;
				}
				// If the move shifted stack from external point onto our point, add new multiplier.
				else {
					strongAssistanceScore[playerId] += scoreZones.getMultiplier(newPoint);
				}
			}
			// But if the old point was on our score, then we lost points. Attacking us!
			else if (scoreZones.isPointBelongTo(oldPoint, myCorner)) {
				strongAssistanceScore[playerId] -= scoreZones.getMultiplier(oldPoint);

			}
			logger.info("strongAssistanceScore["+playerId+"]="+strongAssistanceScore[playerId]+" -- "
					+ move.toString());

			// Add to appropriate list: ally or enemy
			// XXX For now, only uses strongAssistanceScore
			if (strongAssistanceScore[playerId] > 0) {
				ally.add(playerPositions.get(playerId));
			} else if (strongAssistanceScore[playerId] < 0) {
				enemy.add(playerPositions.get(playerId));
			}
			
		}
	}
	
	/**
	 * Get the list of allies, sorted by strongest helper to weakest. (Enemies not included.)
	 * @return
	 */
	public ArrayList<Direction> getAlliesStrongestToWeakest() {
		ArrayList<Direction> sortedAllies = new ArrayList<Direction>();
		int[] strongScoreSorted = strongAssistanceScore.clone();
		
		// Insert allies, from weakest to strongest. then Reverse.
		Arrays.sort(strongScoreSorted);
		for (int scoreRank = 0; scoreRank < strongScoreSorted.length; scoreRank++) {
			for (int id = 0; id < strongAssistanceScore.length; id++) {
				// allyif strong assistance > 0
				if (strongAssistanceScore[id] > 0 && strongAssistanceScore[id] == strongScoreSorted[scoreRank]) {
					sortedAllies.add(playerPositions.get(id));
				}
			}
		}
		// Reverse to get the strongest to weakest ordering.
		Collections.reverse(sortedAllies);
		return sortedAllies;
	}
	
	/**
	 * Get the list of enemies, sorted by strongest harmer to weakest.
	 * @return
	 */
	public ArrayList<Direction> getEnemiesStrongestToWeakest() {
		ArrayList<Direction> sortedEnemies = new ArrayList<Direction>();
		int[] strongScoreSorted = strongAssistanceScore.clone();
		
		// Insert enemies, from weakest to strongest. then Reverse.
		Arrays.sort(strongScoreSorted);
		for (int scoreRank = 0; scoreRank < strongScoreSorted.length; scoreRank++) {
			for (int id = 0; id < strongAssistanceScore.length; id++) {
				// enemy if strong assistance < 0
				if (strongAssistanceScore[id] < 0 && strongAssistanceScore[id] == strongScoreSorted[scoreRank]) {
					sortedEnemies.add(playerPositions.get(id));
				}
			}
		}
		// Reverse to get the strongest to weakest ordering.
		Collections.reverse(sortedEnemies);
		return sortedEnemies;
	}
	
	public ArrayList<Direction> getNeutralPlayers() {
		ArrayList<Direction> neutralPlayers = new ArrayList<Direction>();
		for (int id = 0; id < strongAssistanceScore.length; id++) {
			if (strongAssistanceScore[id] == 0) {
				neutralPlayers.add(playerPositions.get(id));
			}
		}
		// put the Opposite player first, Opposite's neighbors next, finally neighbors.
		ArrayList<Direction> neutralPlayersSorted = new ArrayList<Direction>();
		if (neutralPlayers.contains(myCorner.getRelative(0)))
			neutralPlayersSorted.add(myCorner.getRelative(0));
		if (neutralPlayers.contains(myCorner.getRelative(-1)))
			neutralPlayersSorted.add(myCorner.getRelative(-1));
		if (neutralPlayers.contains(myCorner.getRelative(1)))
			neutralPlayersSorted.add(myCorner.getRelative(1));
		if (neutralPlayers.contains(myCorner.getLeft()))
			neutralPlayersSorted.add(myCorner.getLeft());
		if (neutralPlayers.contains(myCorner.getRight()))
			neutralPlayersSorted.add(myCorner.getRight());
		return neutralPlayersSorted;
	}
	
	
	public void addIllegalPlayers(Collection<Integer> illegalPlayerIds) {
		for (Integer id : illegalPlayerIds) {
			validPlayers[id] = false;
		}
	}
	
	public boolean[] getValidPlayers() {
		return validPlayers;
	}
	
}
