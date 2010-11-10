package push.g7;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import push.sim.Move;
import push.sim.Player.Direction;

public class SimpleMoveStrategy {

	private static final Logger logger = Logger
			.getLogger(SimpleMoveStrategy.class);
	ArrayList<Direction> allys = new ArrayList<Direction>();

	public Move generateHelpfulMove(int[][] board, Direction myCorner,
			int round, RecognizeEnemyAndAlly recognize) {
		logger.info("helpful move. round: " + round + "\n");
		// Pick the ally to help
		Direction allyToHelp;

		ArrayList<Direction> alliesStrongToWeak = recognize
				.getAlliesStrongestToWeakest();
		ArrayList<Direction> neutralPlayers = recognize.getNeutralPlayers();
		ArrayList<Direction> enemiesStrongToWeak = recognize
				.getEnemiesStrongestToWeakest();

		ArrayList<Direction> priority = new ArrayList<Direction>();
		priority.addAll(alliesStrongToWeak);
		priority.addAll(neutralPlayers);
		Collections.reverse(enemiesStrongToWeak);
		priority.addAll(enemiesStrongToWeak);

		return generalMove(0,round, board, myCorner, priority);
	}

	public Move generateBetrayalMove(int[][] board, Direction myCorner,
			int round, RecognizeEnemyAndAlly recognize) {
		// find a betrayal move, i.e., large stacks that we can move to white
		// spots
		logger.info("betrayal move. round: " + round + "\n");

		// See who we want to hurt most.
		// First are highest scoring players who are NOT our allies.
		// Next are highest scoring players who ARE our allies.
		// Finally, any invalid players (have 0 score anyway).
		List<Integer> scores = recognize.scores.getScores();
		ArrayList<Integer> scoresCopy = new ArrayList<Integer>(scores);
		Collections.sort(scoresCopy);
		Collections.reverse(scoresCopy);
		List<Direction> allies = recognize.getAlliesStrongestToWeakest();

		ArrayList<Integer> harmNotAllies = new ArrayList<Integer>();
		ArrayList<Integer> harmYesAllies = new ArrayList<Integer>();
		ArrayList<Integer> harmInvalid = new ArrayList<Integer>();

		for (int id = 0; id < recognize.validPlayers.length; id++) {
			if (!recognize.validPlayers[id])
				harmInvalid.add(id);
		}

		// scores highest to lowest
		for (int i = 0; i < scoresCopy.size(); i++) {
			for (int id = 0; id < scores.size(); id++) {
				if (scores.get(id) == scoresCopy.get(i)) {
					if (!harmInvalid.contains(id)) {
						if (allies.contains(recognize.playerPositions.get(id))) {
							harmYesAllies.add(id);
						} else {
							harmNotAllies.add(id);
						}
					}
				}
			}
		}
		ArrayList<Integer> harmPriority = new ArrayList<Integer>();
		harmPriority.addAll(harmNotAllies);
		harmPriority.addAll(harmYesAllies);
		harmPriority.addAll(harmInvalid);
		ArrayList<Direction> harmPriorityDirections = new ArrayList<Direction>();
		for (Integer harmPrio : harmPriority) {
			harmPriorityDirections.add(recognize.playerPositions.get(harmPrio));
		}

		// supply a list of "to" Directions
		return generalMove(1,round, board, myCorner, harmPriorityDirections);
	}
	
	public Move threeAllisMove(int[][]board, Direction myCorner, int round, RecognizeEnemyAndAlly recognize)
	{
		return null;
	}

	public Move generalMove(int status, int round, int[][] board,
			Direction myCorner, ArrayList<Direction> allysOrEnemy) {
		logger.info("ally : " + allys.iterator());
		if (round <= 2 * StaticVariable.TrySteps) {
			allysOrEnemy.clear();
			allysOrEnemy.add(myCorner.getRelative(0));
			allysOrEnemy.add(myCorner.getRelative(1));
			GetMostEfficientMove getMove = new GetMostEfficientMove(status,
					myCorner, allysOrEnemy.get(round % 2), board);
			if (getMove.NoValidHurtForThisEnemy == 0) {
				return getMove.hurtestMove;
			}
		} else {
			// ally;
			if (status == 0) {
				// try to help the first ally then the next.
				for (Direction i : allysOrEnemy) {
					GetMostEfficientMove getMove = new GetMostEfficientMove(
							status, myCorner, i, board);
					if (getMove.NoValidHelpForThisAlly == 0) {
						return getMove.mostHelpfulMove;
					}
				}
			}
			// enemy
			else {
				for (Direction i : allysOrEnemy) {
					GetMostEfficientMove getMove = new GetMostEfficientMove(
							status, myCorner, i, board);
					if (getMove.NoValidHurtForThisEnemy == 0) {
						return getMove.hurtestMove;
					}
				}
			}
		}

		// If all the allies is not valid for help or enemies to hurt,any valid
		// move but not hurt myself.
		for (int x = 0; x < StaticVariable.MAX_X; x++)
			for (int y = 0; y < StaticVariable.MAX_Y; y++)
				for (Direction d : Direction.values()) {
					MovePointInDirection m = new MovePointInDirection(x, y,
							board, d, myCorner);
					if (m.validStatus == 1 && m.hurtPlayer != myCorner) {
						// logger.info("no move for ally.benefitPlayer is :" +
						// m.benefitPlayer+"\n");
						return new Move(x, y, d);
					}

				}
		// any move is valid, return this one
		for (int x = 0; x < StaticVariable.MAX_X; x++)
			for (int y = 0; y < StaticVariable.MAX_Y; y++)
				for (Direction d : Direction.values()) {
					MovePointInDirection m = new MovePointInDirection(x, y,
							board, d, myCorner);
					if (m.validStatus == 1) {
						// logger.info("no move for ally. benefitPlayer is :" +
						// m.benefitPlayer+"\n");
						return new Move(x, y, d);
					}

				}

		// No moves; return a dummy Move
		return new Move(0, 0, Direction.E);

	}

}