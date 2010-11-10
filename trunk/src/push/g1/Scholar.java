package push.g1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import push.sim.GameController;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class Scholar extends Player {
	private int id = -1;
	private int totalRounds = -1;
	private int currentRound = 0;
	private ArrayList<Direction> playerPositions;
	public static int[][] currentBoard;
	private Logger log;
	private PlayerBoard board;
	public static final double TIT_TAT_MULTIPLIER = 1.0;
	public static final double TIT_TAT_FAIL_MULTIPLIER = 0.2;
	private Direction ourDirection;
	private static final int smallGameThreshold = 5;
	private MoveFinder moveFinder;
	private GameEngine game;

	@Override
	public String getName() {
		return "Scholar";
	}

	@Override
	public void startNewGame(int id, int totalRounds,
			ArrayList<Direction> playerPositions) {
		this.id = id;
		this.totalRounds = totalRounds;
		this.playerPositions = playerPositions;
		log = Logger.getLogger(GameController.class);

		ourDirection = playerPositions.get(id);

		board = new PlayerBoard(playerPositions, id, ourDirection);
		game = new GameEngine("push.xml");
		moveFinder = new MoveFinder(board);
	}

	@Override
	public Move makeMove(List<MoveResult> previousMoves) {
		currentRound++;

		// Update moves and relationships
		for (MoveResult mr : previousMoves) {
			board.updateMove(mr);
		}
		
		if (previousMoves.size() - 6 >= 0){
			moveFinder.updateRelationships(previousMoves.subList(previousMoves.size()-6 , previousMoves.size()));
		}

		if (currentRound == (totalRounds -1)
				|| totalRounds <= smallGameThreshold) {
			return endOfRoundMove();
		}

		//else
			return moveFinder.findCooperativeMove();

	}

	@Override
	public void updateBoardState(int[][] board) {
		Scholar.currentBoard = board;
	}

	private void processPreviousMoves(List<MoveResult> previousMoves) {
		for (MoveResult mr : previousMoves) {
			log.trace("move result id: " + mr.getPlayerId());
			board.updateMove(mr);
			moveFinder.updateRelationships(previousMoves);
			// update relationships
		}
	}

	/**
	 * Aggressive player for the end of the round. Will test every move that
	 * could help us and perform the best move. Both 2's can move to the three
	 * Four whites can move to green
	 * 
	 * TODO Add in the other good moves Add in things that really hurt other
	 * players?
	 * */
	private Move endOfRoundMove() {

		log.trace("in end of round move");
		ArrayList<PossibleMove> pos = new ArrayList<PossibleMove>();
		pos = board.getPossibleMoves();
		log.trace("our direction is:" + ourDirection);

		log.trace("we have: " + pos.size() + " possible moves");
		/*
		 * for(int i = 0; i < pos.size(); i++) log.trace(pos.get(i));
		 */

		Collections.sort(pos, new PossibleMoveSort());
		int posnum = 0;
		boolean found = false;
		int i = 0;
		
		/*Find the best move after based upon it either helping us 
		or sabbatoging someone else*/
		while (i < pos.size()) {

			if (pos.get(posnum).helpUs() || pos.get(posnum).sabatoge()) {
				found = true;
				posnum = i;
				i = pos.size();
			}
			i++;
		}
		
		if(found == false && pos.size() > 0){
			posnum = 0;
			found = true;
		}

		if (found == true) {
			log.trace("we think our move is: " + pos.get(posnum).isValid());
			log.trace("posnum is:" + posnum);
			log.trace("our move is:");
			log.trace(pos.get(posnum));
			log.trace("we chose this move because there is a change of: "
					+ pos.get(posnum).getPointsGained());
			return pos.get(posnum);
		} else {
			log.error("returning null in end of round move");
			return new Move(0, 0, ourDirection);
		}
	}
}
