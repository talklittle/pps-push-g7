package push.g1;

import org.apache.log4j.Logger;

import push.sim.GameController;
import push.sim.Move;
import push.sim.Player;
import push.sim.Player.Direction;

public class PossibleMove extends Move {

	public PossibleMove(int x, int y, Direction direction, PlayerBoard b) {
		super(x, y, direction);
		board = b;
		log = Logger.getLogger(GameController.class);
	}

	/** Initialize a possible move by giving it two hexagons */
	public PossibleMove(Hexagon a, Hexagon b, PlayerBoard bd) {

		super(a.getX(), a.getY(), findDirection(a, b));
		board = bd;
	}

	public PossibleMove(Move p, PlayerBoard b) {

		super(p.getX(), p.getY(), p.getDirection());
		board = b;
	}

	/** Find the move direction for two hexagons */
	public static Direction findDirection(Hexagon a, Hexagon b) {

		// nw
		if (a.getX() - 1 == b.getX() && a.getY() - 1 == b.getY())
			return Player.Direction.NW;

		// e
		if (a.getX() + 2 == b.getX() && a.getY() == b.getY())
			return Player.Direction.E;

		// se
		if (a.getX() + 1 == b.getX() && a.getY() + 1 == b.getY())
			return Player.Direction.SE;

		// sw
		if (a.getX() - 1 == b.getX() && a.getY() + 1 == b.getY())
			return Player.Direction.SW;

		// w
		if (a.getX() - 2 == b.getX() && a.getY() == b.getY())
			return Player.Direction.W;

		// ne
		if (a.getX() + 1 == b.getX() && a.getY() - 1 == b.getY())
			return Player.Direction.NE;

		return null;
	}

	/** Gives the points gained in the move **/
	public int getPointsGained() {
		if (!isValid()) {
			// log.trace("invalid: cannot return points");
			return -1000;
		}

		return board.getOurPoints(this) + board.getChangeInPoints(this);
	}

	public String toString() {
		return "Possible move from: " + this.getX() + ", " + this.getY()
				+ " to: " + this.getNewX() + ", " + this.getNewY() + " in: "
				+ this.getDirection();
	}

	public Boolean isValid() {

		Boolean valid = true;
		String err = "Error: ";

		if (this.getNewX() > 16 || this.getNewX() < 0 || this.getNewY() < 0
				|| this.getNewY() > 8) // OOB
		{
			err += "ood ";
			valid = false;
		}

		else if (this.getX() > 16 || this.getX() < 0 || this.getY() < 0
				|| this.getY() > 8) // OOB
		{
			err += "oob2 ";
			valid = false;
		}

		// no coins
		else if (board.getHexagon(this.getX(), this.getY()).getNumCoins() == 0) {
			err += "no coins ";
			valid = false;
		}

		// Going onto an uninitialized hexagon
		else if (board.getHexagon(this.getNewX(), this.getNewY()).owner == 1000) {
			err += "new space not initialized";
			valid = false;
		}

		// False square
		else if (board.getHexagon(this.getX(), this.getY()).owner == 1000) {
			err += "old space not initialized";
			valid = false;
		}

		else if (this.getDirection() != board.getOurDirection().getRelative(-1)
				&& this.getDirection() != board.getOurDirection()
						.getRelative(0)
				&& this.getDirection() != board.getOurDirection()
						.getRelative(1)) {
			err += "invalid direction ";
			valid = false;
		}

		/*
		 * if(!valid) log.trace("rejecting move: " + this.toString() +
		 * " because: " + err);
		 */
		return valid;
	}

	/** Does this move hurt someone who is ahead of us */
	public Boolean sabatoge() {

		if (board.getChangeInPoints(this) <= 0)
			return false;

		if (board.getPlayerScore(board.getHexagon(this.getNewX(),
				this.getNewY()).getOwner()) < board.getPlayerScore(board
				.getUs()))
			return false;

		return true;
	}

	/** Does this move help us */
	public Boolean helpUs() {
		log.trace("in help us with move: " + this);
		log.trace("get our points is: " + board.getOurPoints(this));
		if (board.getOurPoints(this) > 0)
			return true;
		return false;
	}
	
	/**Check to see if this is a move away from us*/
	public Boolean moveAway(){
		
		if(getPointsGained() < 0)
			return true;
		
		else
			return false;
	}
	
	public boolean tooManyCoins(){
		int newcoins = 0;
		newcoins += board.getHexagon(this.getNewX(), this.getNewY()).getNumCoins();
		newcoins += board.getHexagon(this.getX(), this.getY()).getNumCoins();
		
		if(newcoins > cointhreshold)
			return true;
		return false;
	}

	private PlayerBoard board;
	private Logger log;
	private final int cointhreshold = 10;

}
