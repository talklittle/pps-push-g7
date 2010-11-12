package push.g7;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

public class SimpleMoveStrategy {

	private static final Logger logger = Logger
			.getLogger(SimpleMoveStrategy.class);
	ArrayList<Direction> allys = new ArrayList<Direction>();
	ArrayList<Direction> formerAllys = allys;

	public Move generateHelpfulMove(int[][] board, Direction myCorner,
			int round, RecognizeEnemyAndAlly recognize, boolean isShortGame) {
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

		return generalMove(0, round, board, myCorner, priority, isShortGame);
	}

	public Move generateBetrayalMove(int[][] board, Direction myCorner,
			int round, RecognizeEnemyAndAlly recognize, boolean isShortGame) {
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
					if (!harmInvalid.contains(id) && !harmYesAllies.contains(id) && !harmNotAllies.contains(id)) {
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
		return generalMove(1, round, board, myCorner, harmPriorityDirections, isShortGame);
	}
	
	/**
	 * short games:
	 * 1st round help opposite
	 * remaining rounds help someone if they helped previous round (truncated history),
	 * or else if no allies then help ourselves.
	 * if we run out of moves to help ourselves (e.g. we had an ally helping for a while
	 * but not previous round) then do a harmful move.
	 * @param board
	 * @param myCorner
	 * @param round
	 * @param previousMoves use only the previous round's MoveResults (only a 1-round memory)
	 * @return
	 */
	public Move generateShortGameMove(int[][] board, Direction myCorner,
			int round, int totalRounds, RecognizeEnemyAndAlly recognize, List<MoveResult> previousMoves,
			int formerScore, int afterScore) {
		if (round == 0) {
			// help the Opposite
			ArrayList<Direction> oppositeOnly = new ArrayList<Direction>();
			oppositeOnly.add(myCorner.getOpposite());
			return generalMove(0, round, board, myCorner, oppositeOnly, true);
		} else {
			ArrayList<Direction> allies = recognize.getAlliesStrongestToWeakest();
			if (allies.isEmpty()) {
				// No allies; Try to help ourselves (force it)
				Move tryHelpOurself = helpOurselfMove(board, myCorner, round, totalRounds,
						formerScore, afterScore, recognize, true);
				if (tryHelpOurself != null) {
					return tryHelpOurself;
				}
				// can't help ourselves so do a harmful move
				return generateBetrayalMove(board, myCorner, round, recognize, true);
			} else {
				// we have ally from previous round, help them
				return generateHelpfulMove(board, myCorner, round, recognize, true);
			}
		}
	}
	
	public Move helpOurselfMove(int[][]board,Direction myCorner,int round,int totalRounds,
			int formerScore, int afterScore,RecognizeEnemyAndAlly recognize, boolean force)
	{
		//last 3 round try the best to help ourself
		if(round>=totalRounds-3)
		{
			ArrayList<Direction> allys=new ArrayList<Direction>();
			allys.add(myCorner);
			allys.addAll(recognize.getAlliesStrongestToWeakest());
			allys.addAll(recognize.getNeutralPlayers());
			ArrayList<Direction> a =recognize.getEnemiesStrongestToWeakest();
			Collections.reverse(a);
			allys.addAll(a);

			return singleAllyMove(board,myCorner,allys);
		}
		else if(formerScore>afterScore || force)
		{
			for(int y=0;y<StaticVariable.MAX_Y;y++)
				for(int x=0;x<StaticVariable.MAX_X;x++)
					for (Direction d : Direction.values()) 
					{
						MovePointInDirection m = new MovePointInDirection(x, y,
								board, d, myCorner);
						PointProperty p = new PointProperty(x,y,board);
						if(m.validStatus==1 && m.benefitPlayer==myCorner&&p.home!=myCorner)
						{
							return new Move(x,y,d);
						}
					
					}
		}
		return null;
	}


	public Move generalMove(int status, int round, int[][] board,
			Direction myCorner, ArrayList<Direction> allysOrEnemy, boolean isShortGame) {
		logger.info("ally : " + allys.iterator());
		// in first 6 rounds, try to set up two allies with help them
		// alternatively.
		if (round < 2 * StaticVariable.TrySteps && !isShortGame) {
			allysOrEnemy.clear();
			allysOrEnemy.add(myCorner.getRelative(0));
			allysOrEnemy.add(myCorner.getRelative(1));
			GetMostEfficientMove getMove = new GetMostEfficientMove(status,
					myCorner, allysOrEnemy.get(round % 2), board);
			if (getMove.NoValidHelpForThisAlly == 0) {
				return getMove.mostHelpfulMove;
			} else {
				GetMostEfficientMove getMove2 = new GetMostEfficientMove(
						status, myCorner, allysOrEnemy.get(round % 2), board);
				if (getMove2.NoValidHelpForThisAlly == 0) {
					return getMove2.mostHelpfulMove;
				}
			}
			formerAllys = allysOrEnemy;
		}
		// in the following rounds, check if the two has set up the relation.
		else {

			// ally;
			if (status == 0) {
				// if set up keep allies with the two player.
				if ((formerAllys.size() >= 1 && formerAllys.contains(allysOrEnemy.get(0)))
						|| (formerAllys.size() >= 2 && formerAllys.contains(allysOrEnemy.get(1)))) {
				    Move m = twoAlliesMove(board,myCorner,round, allysOrEnemy);
					if (m != null) return m;
				}
				Move m = singleAllyMove(board,myCorner,allysOrEnemy);
				if(m!=null){return m;}
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

	public Move singleAllyMove(int[][] board,Direction myCorner, ArrayList<Direction> allysOrEnemy) 
	{
		if (allysOrEnemy.size() < 3)
			return null;
		
		// try to help the first ally then the next. randomly help other allies.
		Random rand = new Random();
		ArrayList<Direction> allys = new ArrayList<Direction>();
			
		allys.add(allysOrEnemy.get(0));
		allys.add(allysOrEnemy.get(1));
		allys.add(allysOrEnemy.get(2));
		for (Direction i : allys) {
			GetMostEfficientMove getMove = new GetMostEfficientMove(0,myCorner, i, board);
			if (getMove.NoValidHelpForThisAlly == 0 && rand.nextInt(4)!=3) {
				return getMove.mostHelpfulMove;
			}
		}
		//if no move for those allies, get rid of random thing to see if it works.
		for (Direction i : allys) {
			GetMostEfficientMove getMove = new GetMostEfficientMove(0,myCorner, i, board);
			if (getMove.NoValidHelpForThisAlly == 0) {
				return getMove.mostHelpfulMove;
			}
		}
		return null;
	}
	
	public Move twoAlliesMove(int[][] board, Direction myCorner,int round, ArrayList<Direction> allysOrEnemy) 
	{
		if (allysOrEnemy.size() < 2)
			return null;
		
		ArrayList<Direction> allys = new ArrayList<Direction>();
		allys.add(allysOrEnemy.get(0));
		allys.add(allysOrEnemy.get(1));
		GetMostEfficientMove getMove = new GetMostEfficientMove(0,myCorner, allys.get(round % 2), board);
		if (getMove.NoValidHelpForThisAlly == 0) {
			return getMove.mostHelpfulMove;
		} else {
			GetMostEfficientMove getMove2 = new GetMostEfficientMove(
					0, myCorner, allysOrEnemy.get(round % 2), board);
			if (getMove2.NoValidHelpForThisAlly == 0) {
				return getMove2.mostHelpfulMove;
			}
		}
		return null;
		
	}
	
	public Move threeAlliesMove(int[][] board, Direction myCorner, int round) 
	{
		ArrayList<Direction> allys = new ArrayList<Direction>();
		allys.add(myCorner.getOpposite());
		allys.add(myCorner.getRelative(1));
		allys.add(myCorner.getRelative(-1));
		GetMostEfficientMove getMove = new GetMostEfficientMove(0,myCorner, allys.get(round%3), board);
		if(getMove.NoValidHelpForThisAlly==0)return getMove.mostHelpfulMove;
		//if no such move, and we should help our allies whatever any points it already gets.
		for (Direction i : allys) {
			GetMostEfficientMove getMove2 = new GetMostEfficientMove(0,myCorner, i, board);
			if (getMove2.NoValidHelpForThisAlly == 0) {
				return getMove2.mostHelpfulMove;
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
