package push.g6.strategy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import push.g6.AbstractPlayer;
import push.g6.NicePlayer;
import push.g6.PushValues;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

public class GeneralStrategy extends Strategy {

	//private LinkedList<Point> neighbourhood = null;
	private PriorityQueue<PlayerHelper> queue = new PriorityQueue<PlayerHelper>();
	private static ArrayList<Point> helpNorthEast;
	private static ArrayList<Point> helpEast;
	private static ArrayList<Point> helpSouthEast;
	private static ArrayList<Point> helpSouthWest;
	private static ArrayList<Point> helpWest;
	private static ArrayList<Point> helpNorthWest;

	static {
		helpWest = new ArrayList<Point>();
		helpWest.add(new Point(1, 3));
		helpWest.add(new Point(2, 4));
		helpWest.add(new Point(1, 5));
		helpWest.add(new Point(2, 2));
		helpWest.add(new Point(3, 3));
		helpWest.add(new Point(4, 4));
		helpWest.add(new Point(3, 5));
		helpWest.add(new Point(2, 6));
		helpWest.add(new Point(4, 2));
		helpWest.add(new Point(5, 3));
		helpWest.add(new Point(6, 4));
		helpWest.add(new Point(5, 5));
		helpWest.add(new Point(4, 6));
		helpWest.add(new Point(7, 3));
		helpWest.add(new Point(8, 4));
		helpWest.add(new Point(7, 5));

		helpSouthWest = new ArrayList<Point>();
		helpSouthWest.add(new Point(3, 7));
		helpSouthWest.add(new Point(5, 7));
		helpSouthWest.add(new Point(6, 8));
		helpSouthWest.add(new Point(2, 6));
		helpSouthWest.add(new Point(4, 6));
		helpSouthWest.add(new Point(6, 6));
		helpSouthWest.add(new Point(7, 7));
		helpSouthWest.add(new Point(8, 8));
		helpSouthWest.add(new Point(3, 5));
		helpSouthWest.add(new Point(5, 5));
		helpSouthWest.add(new Point(7, 5));
		helpSouthWest.add(new Point(8, 6));
		helpSouthWest.add(new Point(9, 7));
		helpSouthWest.add(new Point(6, 4));
		helpSouthWest.add(new Point(8, 4));
		helpSouthWest.add(new Point(9, 5));

		helpSouthEast = new ArrayList<Point>();
		helpSouthEast.add(new Point(10, 8));
		helpSouthEast.add(new Point(11, 7));
		helpSouthEast.add(new Point(13, 7));
		helpSouthEast.add(new Point(8, 8));
		helpSouthEast.add(new Point(9, 7));
		helpSouthEast.add(new Point(10, 6));
		helpSouthEast.add(new Point(12, 6));
		helpSouthEast.add(new Point(14, 6));
		helpSouthEast.add(new Point(7, 7));
		helpSouthEast.add(new Point(8, 6));
		helpSouthEast.add(new Point(9, 5));
		helpSouthEast.add(new Point(11, 5));
		helpSouthEast.add(new Point(13, 5));
		helpSouthEast.add(new Point(7, 5));
		helpSouthEast.add(new Point(8, 4));
		helpSouthEast.add(new Point(10, 4));

		helpEast = new ArrayList<Point>();
		helpEast.add(new Point(15, 5));
		helpEast.add(new Point(14, 4));
		helpEast.add(new Point(15, 3));
		helpEast.add(new Point(14, 6));
		helpEast.add(new Point(13, 5));
		helpEast.add(new Point(12, 4));
		helpEast.add(new Point(13, 3));
		helpEast.add(new Point(14, 2));
		helpEast.add(new Point(12, 6));
		helpEast.add(new Point(11, 5));
		helpEast.add(new Point(10, 4));
		helpEast.add(new Point(11, 3));
		helpEast.add(new Point(12, 2));
		helpEast.add(new Point(9, 5));
		helpEast.add(new Point(8, 4));
		helpEast.add(new Point(9, 3));

		helpNorthEast = new ArrayList<Point>();
		helpNorthEast.add(new Point(10, 0));
		helpNorthEast.add(new Point(11, 1));
		helpNorthEast.add(new Point(13, 1));
		helpNorthEast.add(new Point(8, 0));
		helpNorthEast.add(new Point(9, 1));
		helpNorthEast.add(new Point(10, 2));
		helpNorthEast.add(new Point(12, 2));
		helpNorthEast.add(new Point(14, 2));
		helpNorthEast.add(new Point(7, 1));
		helpNorthEast.add(new Point(8, 2));
		helpNorthEast.add(new Point(9, 3));
		helpNorthEast.add(new Point(11, 3));
		helpNorthEast.add(new Point(13, 3));
		helpNorthEast.add(new Point(7, 3));
		helpNorthEast.add(new Point(8, 4));
		helpNorthEast.add(new Point(10, 4));

		helpNorthWest = new ArrayList<Point>();
		helpNorthWest.add(new Point(3, 1));
		helpNorthWest.add(new Point(5, 1));
		helpNorthWest.add(new Point(6, 0));
		helpNorthWest.add(new Point(2, 2));
		helpNorthWest.add(new Point(4, 2));
		helpNorthWest.add(new Point(6, 2));
		helpNorthWest.add(new Point(7, 1));
		helpNorthWest.add(new Point(8, 0));
		helpNorthWest.add(new Point(3, 3));
		helpNorthWest.add(new Point(5, 3));
		helpNorthWest.add(new Point(7, 3));
		helpNorthWest.add(new Point(8, 2));
		helpNorthWest.add(new Point(9, 1));
		helpNorthWest.add(new Point(6, 4));
		helpNorthWest.add(new Point(8, 4));
		helpNorthWest.add(new Point(9, 3));
	}

	private static ArrayList<Point> getHelpPointsForplayer(PlayerHelper player) {
		if (player.getDirection().equals(Direction.E)) {
			return helpEast;
		}
		if (player.getDirection().equals(Direction.NE)) {
			return helpNorthEast;
		}
		if (player.getDirection().equals(Direction.W)) {
			return helpWest;
		}
		if (player.getDirection().equals(Direction.NW)) {
			return helpNorthWest;
		}
		if (player.getDirection().equals(Direction.SE)) {
			return helpSouthEast;
		}
		if (player.getDirection().equals(Direction.SW)) {
			return helpSouthWest;
		}
		// Code never gets here
		return null;
	}

	public GeneralStrategy(NicePlayer p) {
		this.player = p;

		neighbourhood = new LinkedList<Point>();
		neighbourhood.add(new Point((int) this.player.getCorner().getHome()
				.getX(), (int) this.player.getCorner().getHome().getY()));

		for (int i = 0; i < 3; i++) {

			int x = (int) (this.player.getCorner().getRelative(1 - i).getDx() + this.player
					.getCorner().getHome().getX());
			int y = (int) (this.player.getCorner().getRelative(1 - i).getDy() + this.player
					.getCorner().getHome().getY());

			neighbourhood.add(new Point(x, y));

			for (int j = 0; j < 3; j++) {
				neighbourhood.add(new Point(x
						+ this.player.getCorner().getRelative(1 - j).getDx(), y
						+ this.player.getCorner().getRelative(1 - j).getDy()));
			}
		}

		
		addPlayersToQueue();
	}

	private void addPlayersToQueue() {
		for (int i = 0; i <= 5; i++) {
			if (i == this.player.getID())
				continue;
			int helpIndex=0;
			if(AbstractPlayer.getHomeofID(i).equals(this.player.getCorner().getOpposite())){
				this.player.getLogger().debug("increasing inde");
				helpIndex=1;
			}
			queue.add(new PlayerHelper(AbstractPlayer.getHomeofID(i), helpIndex, i));
		}
	}
		
		private void addPlayersToQueueNoPreference() {
			for (int i = 0; i <= 5; i++) {
				if (i == this.player.getID())
					continue;
				int helpIndex=0;
				queue.add(new PlayerHelper(AbstractPlayer.getHomeofID(i), helpIndex, i));
			}

	}

	public void evaluateMoves1(List<MoveResult> previousMoves) {
		if ((previousMoves == null) || previousMoves.isEmpty()
				|| queue.isEmpty())
			return;
		// This is our move, so we ignore it.
		for (MoveResult mr : previousMoves) {
			if (mr.getPlayerId() == this.player.getID())
				continue;
			// Evaluate whether the moves of the other players have helped or
			// not. Check if its a valid move.
			if (!GameEngine.isInBounds(mr.getMove().getNewY(), mr.getMove()
					.getNewX())
					|| !GameEngine.isValidDirectionForCellAndHome(
							AbstractPlayer.getHomeofID(mr.getPlayerId()), mr
									.getMove().getDirection())) {
				continue;
			}
			Point pointFrom = new Point(mr.getMove().getX(), mr.getMove()
					.getY());
			Point pointTo = new Point(mr.getMove().getNewX(), mr.getMove()
					.getNewY());
			// A move was made into our region. Someone helped us.
			if (AbstractPlayer.getPlayerBelongs(pointTo) == this.player.getID()) {
				changePriority(mr, PushValues.BIG_HELP);
			}
			// Someone moves in our direction.
			else if ((mr.getMove().getDirection().equals(this.player
					.getCorner()))) {
				changePriority(mr, PushValues.SMALL_HELP);
			}
			// Someone Hurts Us.
			else if (AbstractPlayer.getPlayerBelongs(pointFrom) == this.player
					.getID()
					&& AbstractPlayer.getPlayerBelongs(pointTo) != this.player
							.getID()) {
				changePriority(mr, PushValues.LOWER_PRIORITY);
			} else {
				changePriority(mr,PushValues.NEUTRUAL);
				// do nothing
			}

		}

	}

	private void changePriority(MoveResult mr, int changeValue) {
		int helpingPlayer = mr.getPlayerId();
		ArrayList<PlayerHelper> temp=new ArrayList<PlayerHelper>();
		for (PlayerHelper player : queue) {
			// Find the player who helped us and increase his priority and add
			// him to the Q.
			if (player.getId() == helpingPlayer) {
				//queue.remove(player);
				player.setHelpIndex(player.getHelpIndex()+changeValue);
				//temp.add(player);
			}
		}
	}


	@Override
	public Move getMove(int round, List<MoveResult> previousMoves) {

		if((round%PushValues.CYCLE_DURATION)==0)
		{
			this.queue.clear();
			this.addPlayersToQueueNoPreference();
			
		}
		
		if(round>(PushValues.LAST_QUARTER*this.player.getTotalRounds()))
		{
			
			for(Integer p:this.player.getPiles().keySet()) {
				Point ppoint=this.player.getPiles().get(p);
				
				int id=AbstractPlayer.getPlayerBelongs(ppoint); 
				
				//TODO
			}
		}
		
		this.evaluateMoves1(previousMoves);
		return makeAHelpingMove();

	}

	public Move makeAHelpingMove() {
		if(queue.isEmpty()){
			this.player.getLogger().debug("Empty Q!");
			return this.player.makeNeutralMove();

		}
		for(PlayerHelper p:queue){
			this.player.getLogger().debug("Player: "+p.getId()+" help index: "+p.getHelpIndex());

		}
		PlayerHelper friend = queue.peek();
		
		this.player.getLogger().debug("The player to help is: Player"+friend.getId()+" helppoints "+friend.getHelpIndex());
		Move moveToReturn=null;
		ArrayList<Point> helpPoints = getHelpPointsForplayer(friend);
		for (Point p : helpPoints) {
			for (int i = -1; i < 2; i++) {
				moveToReturn = new Move((int) p.getX(), (int) p.getY(),
						this.player.getCorner().getRelative(i));
				// We have a valid move that helps a player
				MoveResult mr = new MoveResult(moveToReturn,
						this.player.getID());
				if (this.player.isValidMove(mr)) {
					if (!this.player.getWhiteSpots().contains(
							new Point(mr.getMove().getNewX(), mr.getMove()
									.getNewY()))) {
					///	this.player.getLogger().debug("Point zone"+ p.toString()+" "+AbstractPlayer.getScoreOfCell(p));
						return moveToReturn;
					}
				}
				// the move we found was not valid. Check for some other moves.
				else {
					moveToReturn = null;
				}
			}
		}
		
		if(moveToReturn==null) {
			
			PlayerHelper friend1 = queue.remove();
			
			friend = queue.peek();
			
			queue.add(friend1);
			
			this.player.getLogger().debug("The player to help is: Player"+friend.getId()+" helppoints "+friend.getHelpIndex());
			
			helpPoints = getHelpPointsForplayer(friend);
			for (Point p : helpPoints) {
				for (int i = -1; i < 2; i++) {
					moveToReturn = new Move((int) p.getX(), (int) p.getY(),
							this.player.getCorner().getRelative(i));
					// We have a valid move that helps a player
					MoveResult mr = new MoveResult(moveToReturn,
							this.player.getID());
					if (this.player.isValidMove(mr)) {
						if (!this.player.getWhiteSpots().contains(
								new Point(mr.getMove().getNewX(), mr.getMove()
										.getNewY()))) {
						///	this.player.getLogger().debug("Point zone"+ p.toString()+" "+AbstractPlayer.getScoreOfCell(p));
							return moveToReturn;
						}
					}
					// the move we found was not valid. Check for some other moves.
					else {
						moveToReturn = null;
					}
				}
			}
			
		}
		
		return this.player.makeNeutralMove();
	}

	/* A generateRandomMove that does not allow you to return a move
	 * that takes away coins from your neighborhood. 
	 */
	public Move generateNotSoRandomMove()
	{
		/* For when we need to do a not so random move */
		int countIter = 0; 
		Move temp;
		temp = player.generateRandomMove(0);
		Point tempp = new Point(temp.getX(), temp.getY());
		while(AbstractPlayer.getPlayerBelongs(tempp) == this.player.getID()
				&& countIter < 10000)
		{
			temp = player.generateRandomMove(0);
			countIter++;
		}
		/* if by 10000 iterations, we couldn't find an allowable move,
		 * it's most likely that the only legal moves available are to move 
		 * from our area, so we are forced to concede, and return a move 
		 * that hurts us. 
		 */
		if(countIter == 10000)
			return player.generateRandomMove(0);
		
		/* if countIter is not 10000, well, we found a winner! */
		return temp;
	}
}
