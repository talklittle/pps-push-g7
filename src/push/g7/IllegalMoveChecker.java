package push.g7;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

public class IllegalMoveChecker {
	
	private List<Direction> playerPositions;
	
	private static final Logger logger = Logger.getLogger(IllegalMoveChecker.class);
	
	public IllegalMoveChecker(List<Direction> playerPositions) {
		this.playerPositions = playerPositions;
	}
	
	public ArrayList<Integer> getIllegalPlayerIds(int[][] prevTurnBoard, List<MoveResult> moveResults) {
		ArrayList<Integer> illegalPlayerIds = new ArrayList<Integer>();
		
		for (MoveResult moveResult : moveResults) {
			Move move = moveResult.getMove();
			
			if (!GameEngine.isValidDirectionForCellAndHome(move.getDirection(), playerPositions.get(moveResult.getPlayerId()))) {
				logger.info("found an illegal move:" + move.toString());
				illegalPlayerIds.add(moveResult.getPlayerId());
			}
		}
		
		return illegalPlayerIds;
	}
}
