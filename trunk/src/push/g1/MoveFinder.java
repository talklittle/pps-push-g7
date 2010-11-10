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
import push.sim.Player.Direction;

public class MoveFinder {
	private int numPlayers;
	private Direction myCorner;
	private int id;
	private ArrayList<Relationship> relationshipList;
	private GameEngine gameEngine;
	private PlayerBoard ourBoard;
	private Logger log;
	ArrayList<PossibleMove> possibleMoves;

	public MoveFinder(PlayerBoard ourBoard) {
		this.ourBoard = ourBoard;

		relationshipList = new ArrayList<Relationship>();
		numPlayers = 6;

		/* Initialize relationships with other players */
		for (int playerID = 0; playerID < 6; playerID++) {
			/* Add all the players except ourselves to the relationship list */
			if (playerID != id) {
				relationshipList.add(new Relationship(playerID, ourBoard));
			}
		}
		log = Logger.getLogger(GameController.class);

		log.trace("num players: " + 6);
		log.trace("relationshipList.size(): " + relationshipList.size());

		gameEngine = new GameEngine("push.xml");
		possibleMoves = ourBoard.getPossibleMoves();
	}

	public Move findCooperativeMove() {
		log.trace("In find cooperative move");
		ArrayList<PossibleMove> badMoves = new ArrayList<PossibleMove>();
		ArrayList<PossibleMove> reallyBadMoves = new ArrayList<PossibleMove>();
		/* Find the most cooperative player using historical data */
		Collections.sort(relationshipList, new RCmp());

		for (Relationship mostCooperative : relationshipList) {
			log.trace(mostCooperative);
		}

		for (Relationship mostCooperative : relationshipList) {
			/*
			 * Get that player's hexagons, so we can figure out which hexagons
			 * we want to move towards to cooperate more with this player.
			 */
			ArrayList<Hexagon> playerHexagons = ourBoard
					.getPlayerHexagons(mostCooperative.getPlayerID());

			/* Sort the other player's hexagons in order of their multipliers */
			Collections.sort(playerHexagons, new HexComparator());
			
			for (Hexagon endHex : playerHexagons)
			{
				for (Direction dir : Player.Direction.values())
				{	
					PossibleMove m = new PossibleMove (
							endHex.getX() + dir.getDx(), 
							endHex.getY() + dir.getDy(), dir.getOpposite(), this.ourBoard);
					//log.trace("move: " + m + "\n");
					
					//also need to test that the move helps the other player
					
					if (moveInPossibleMoveList(m) && ourBoard.getHexAtPoint(m.getX(), m.getY()).getMultiplier() <=  ourBoard.getHexAtPoint(m.getNewX(), m.getNewY()).getMultiplier() && m.moveAway())
					{
						
						/**
						 * it is a "bad move" if it is valid but makes the next
						 * square end up with too many coins
						 */
						if (!m.tooManyCoins() && m.moveAway())
							return m;
						else if(m.moveAway())
							badMoves.add(m);
						else
							reallyBadMoves.add(m);
						//log.trace("that move was considered valid");
						return m;
					}
					else
					{

					}
				}
			}
		}

		if (badMoves.size() > 0){
			//log.error("returning bad move");
			return badMoves.get(0);
		}
		
		if(reallyBadMoves.size() > 0)
			return reallyBadMoves.get(0);

		// Return a default move
		log.error("returning default move");
		Move m = new Move (1,1,ourBoard.getOurDirection().getOpposite());

		return m;

	}

	private boolean moveInPossibleMoveList(Move m) {
		possibleMoves = ourBoard.getPossibleMoves();
		for (PossibleMove possMove : possibleMoves) {
			if (possMove.getX() == m.getX() && possMove.getY() == m.getY()
					&& possMove.getDirection() == m.getDirection())
				return true;
		}
		return false;
	}

	public void updateRelationships(List<MoveResult> previousMoves) {

		for (MoveResult mr : previousMoves) {
			// Update the relationship with:
			Relationship r = getRelationshipByID(mr.getPlayerId());

			if (mr.getPlayerId() != id) {
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
