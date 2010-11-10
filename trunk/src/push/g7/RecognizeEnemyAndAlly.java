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
	
	// for each player 0 thru 5, keep a list of their assistance moves
	ArrayList< ArrayList<Integer> > strongAssistanceScores = new ArrayList< ArrayList<Integer> >();
	ArrayList< ArrayList<Integer> > weakAssistanceScores = new ArrayList< ArrayList<Integer> >();
	
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
		
		// init empty assistance history for 6 players
		for (int i = 0; i < 6; i++) {
			strongAssistanceScores.add(new ArrayList<Integer>());
			weakAssistanceScores.add(new ArrayList<Integer>());
		}
		
		scores = new Scores(validPlayers);
		scores.updateScores(initialBoard, playerPositions, directionToID);
	}
	
	public void updateScores(int[][] board, List<Direction> playerPositions, Map<Direction, Integer> directionToID) {
		scores.updateScores(board, playerPositions, directionToID);
	}
	
	public void updateAlliances(List<MoveResult> previousMoves, int[][] previousBoard, int[][] currentBoard) {
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
			
			// First update the strongAssistanceScores and weakAssistanceScores
			if (result.isSuccess()) {
				Move move = result.getMove();
				Point oldPoint = new Point(move.getX(), move.getY());
				Point newPoint = new Point(move.getNewX(), move.getNewY());
				int oldDistance = GameEngine.getDistance(oldPoint, myCorner.getHome());
				int newDistance = GameEngine.getDistance(newPoint, myCorner.getHome());
				
				// If the distance decreases, then it's a good move (weak assistance)
				weakAssistanceScores.get(playerId).add(-(newDistance - oldDistance));
				
	//			logger.info("weakAssistanceScore["+playerId+"]="+weakAssistanceScore[playerId]+" -- "
	//					+ move.toString());
				
				// Take the number of coins from old space, compare the multiplier from new and old locations.
				// Ignore the new number of coins, since another player could have simultaneously pushed onto the new.
				PointProperty oldPointProperty = new PointProperty(move.getX(), move.getY(), previousBoard);
				PointProperty newPointProperty = new PointProperty(move.getNewX(), move.getNewY(), previousBoard);
				
				// If the move changed our score, it is a strong assistance (or opposite) indicator
				if (scoreZones.isPointBelongTo(newPoint, myCorner)) {
					// If the move shifted stack between 2 of our own points, use multiplierDelta.
					if (scoreZones.isPointBelongTo(oldPoint, myCorner)) {
						strongAssistanceScores.get(playerId).add(
								oldPointProperty.getCoins() * (newPointProperty.getScore() - oldPointProperty.getScore()));
					}
					// If the move shifted stack from external point onto our point, add new multiplier.
					else {
						strongAssistanceScores.get(playerId).add(
								oldPointProperty.getCoins() * scoreZones.getMultiplier(newPoint));
					}
				}
				// But if the old point was on our score, then we lost points. Attacking us!
				else if (scoreZones.isPointBelongTo(oldPoint, myCorner)) {
					strongAssistanceScores.get(playerId).add(
							-oldPointProperty.getCoins() * scoreZones.getMultiplier(oldPoint));
				}
//				logger.info("strongAssistanceScore["+playerId+"]="+strongAssistanceScore[playerId]+" -- "
//						+ move.toString());
			} else {
				// Unsuccessful move. Don't care!
				strongAssistanceScores.get(playerId).add(0);
				weakAssistanceScores.get(playerId).add(0);
			}
			
			double strongWeightedAssistanceScore = calculateWeightedScore(strongAssistanceScores.get(playerId));
			
			// Add to appropriate list: ally or enemy
			// Neutral players are taken care of in getNeutralPlayers()
			// XXX For now, only uses strongAssistanceScore
			if (strongWeightedAssistanceScore > 0) {
				ally.add(playerPositions.get(playerId));
			} else if (strongWeightedAssistanceScore < 0) {
				enemy.add(playerPositions.get(playerId));
			}
		}
	}
	
	public double calculateWeightedScore(ArrayList<Integer> scoresList) {
		int length = scoresList.size();
		double score = 0.0;
		for (int i = 0; i < length; i++) {
			score += (double) scoresList.get(i) * (double) (i+1) / (double) length;
		}
		return score;
	}
	
	/**
	 * Get the list of allies, sorted by strongest helper to weakest. (Enemies not included.)
	 * @return
	 */
	public ArrayList<Direction> getAlliesStrongestToWeakest() {
		ArrayList<Direction> sortedAllies = new ArrayList<Direction>();
		
		// Insert allies, from weakest to strongest.
		double[] scores = new double[strongAssistanceScores.size()];
		double[] scoresSorted  = new double[strongAssistanceScores.size()];
		for (int i = 0; i < scores.length; i++) {
			scores[i] = calculateWeightedScore(strongAssistanceScores.get(i));
			scoresSorted[i] = scores[i];
		}
		Arrays.sort(scoresSorted);
		
		for (int scoreRank = 0; scoreRank < scoresSorted.length; scoreRank++) {
			for (int id = 0; id < scores.length; id++) {
				// allyif strong assistance > 0
				if (scores[id] > 0 && scores[id] == scoresSorted[scoreRank]) {
					sortedAllies.add(playerPositions.get(id));
				}
			}
		}
		// Reverse to get the strongest to weakest allies (positive numbers descending).
		Collections.reverse(sortedAllies);
		return sortedAllies;
	}
	
	/**
	 * Get the list of enemies, sorted by strongest harmer to weakest.
	 * @return
	 */
	public ArrayList<Direction> getEnemiesStrongestToWeakest() {
		ArrayList<Direction> sortedEnemies = new ArrayList<Direction>();
		
		// Insert opponents from greatest harmers, weak harmers, weak helpers, strong helpers
		double[] scores = new double[strongAssistanceScores.size()];
		double[] scoresSorted  = new double[strongAssistanceScores.size()];
		for (int i = 0; i < scores.length; i++) {
			scores[i] = calculateWeightedScore(strongAssistanceScores.get(i));
			scoresSorted[i] = scores[i];
		}
		Arrays.sort(scoresSorted);
		
		for (int scoreRank = 0; scoreRank < scoresSorted.length; scoreRank++) {
			for (int id = 0; id < scores.length; id++) {
				// allyif strong assistance > 0
				if (scores[id] < 0 && scores[id] == scoresSorted[scoreRank]) {
					sortedEnemies.add(playerPositions.get(id));
				}
			}
		}
		// Already sorted strongest to weakest (most negative to least negative)
		return sortedEnemies;
	}
	
	public ArrayList<Direction> getNeutralPlayers() {
		ArrayList<Direction> neutralPlayers = new ArrayList<Direction>();
		for (int id = 0; id < strongAssistanceScores.size(); id++) {
			if (calculateWeightedScore(strongAssistanceScores.get(id)) == 0) {
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
