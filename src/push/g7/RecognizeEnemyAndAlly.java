package push.g7;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
	
	private ScoreZones scoreZones;
	
	private static final Logger logger = Logger.getLogger(RecognizeEnemyAndAlly.class);
	
	public RecognizeEnemyAndAlly(Direction myCorner, List<Direction> playerPositions, ScoreZones scoreZones) {
		this(myCorner, playerPositions, scoreZones, null, null);
	}
	
	public RecognizeEnemyAndAlly(Direction myCorner, List<Direction> playerPositions, ScoreZones scoreZones,
			Collection<Direction> initialAllies, Collection<Direction> initialEnemies) {
		this.myCorner = myCorner;
		this.playerPositions = playerPositions;
		this.scoreZones = scoreZones;
		if (initialAllies != null)
			ally.addAll(initialAllies);
		if (initialEnemies != null)
			enemy.addAll(initialEnemies);
	}
	
	public void updateAlliances(List<MoveResult> previousMoves) {
		if (previousMoves == null || previousMoves.size() == 0)
			return;
		
		ally.clear();
		enemy.clear();
		
		for (MoveResult result : previousMoves) {
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
	
	public boolean isAlly(Direction direction) {
		return ally.contains(direction);
	}
	
}
