package push.g1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import push.sim.GameController;
import push.sim.Move;
import push.sim.MoveResult;

public class MoveFinder {
	private int ourID;
	private ArrayList<Relationship> relationshipList;
	private PlayerBoard ourBoard;
	private Logger log;

	public MoveFinder(PlayerBoard ourBoard) {
		this.ourBoard = ourBoard;
		this.ourID = ourBoard.getUs();

		relationshipList = new ArrayList<Relationship>();

		/* Initialize relationships with other players */
		for (int playerID = 0; playerID < 6; playerID++) {
			/* Add all the players except ourselves to the relationship list */
			if (playerID != ourID) {
				relationshipList.add(new Relationship(playerID, ourBoard));
			}
		}
		log = Logger.getLogger(GameController.class);

		log.trace("relationshipList.size(): " + relationshipList.size());
	}

	public Move findCooperativeMove() {
		log.trace("Entered findCooperativeMove");
		ArrayList<PossibleMove> allMoves = new ArrayList<PossibleMove>();
		allMoves = ourBoard.getPossibleMoves();

		/* Find the most cooperative player using historical data */
		Collections.sort(relationshipList, new RCmp());


		for (PossibleMove m : allMoves) {
			for (Relationship r : relationshipList) {
				if (r.getPlayerID() == m.getNewHexagon().getOwner())
					m.setRelationshipRank(r.getCooperationScore());
			}
		}

		if (allMoves.size() > 0) {
			Collections.sort(allMoves, new PossibleMoveSort());
			return allMoves.get(0);
		}

		// Return a default move
		log.error("returning default move, size of possible moves was: "
				+ allMoves.size());
		Move m = new Move(1, 1, ourBoard.getOurDirection().getOpposite());

		return m;
	}

	public void updateRelationships(List<MoveResult> previousMoves) {
		for (MoveResult mr : previousMoves) {
			// Update the relationship with:
			Relationship r = getRelationshipByID(mr.getPlayerId());

			if (mr.getPlayerId() != ourID) {
				r.addMove(mr);
				log.trace("Adding move: " + mr + " to " + r + "with player ID "
						+ mr.getPlayerId());
			}
		}
	}

	private Relationship getRelationshipByID(int playerID) {
		log.trace("looking for playerID " + playerID);

		for (Relationship r : relationshipList) {
			log.trace("r.getPlayerID(): " + r.getPlayerID());
			log.trace("playerID: " + playerID);
			if (r.getPlayerID() == playerID) {
				log.trace("found other player in rlist");
				return r;
			}

		}
		return null;
	}
}
