package push.g1;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;
import push.sim.Player.Direction;

public class PlayerBoard {

    private Hexagon[][] b;
    private Logger log;
    private final int startingCoins = 1;
    private int us;
    private int[][] board;
    private Direction myCorner;
    private final int unitinitalized = 1000;
    private final int noone = -1;
    
	public PlayerBoard(ArrayList<Direction> p, int u, Direction d) {
		log = Logger.getLogger(this.getClass());
		log.trace("initializing player board\n");
		us = u;
		myCorner = d;

		// Initialize board
		b = new Hexagon[17][9];
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 9; j++) {
				b[i][j] = new Hexagon(0, startingCoins, unitinitalized, i, j);
			}
		}

		// nw
		int n = 0;
		for (int i = 0; i < p.size(); i++)
			if (p.get(i) == Player.Direction.NW)
				n = i;
		b[4][0] = new Hexagon(4, startingCoins, n, 4, 0);
		b[6][0] = new Hexagon(2, startingCoins, n, 6, 0);
		b[3][1] = new Hexagon(2, startingCoins, n, 3, 1);
		b[5][1] = new Hexagon(3, startingCoins, n, 5, 1);
		b[4][2] = new Hexagon(1, startingCoins, n, 4, 2);
		b[6][2] = new Hexagon(1, startingCoins, n, 6, 2);
		b[7][2] = new Hexagon(1, startingCoins, n, 7, 2);
		b[7][3] = new Hexagon(1, startingCoins, n, 7, 3);

		// ne
		n = 0;
		for (int i = 0; i < p.size(); i++)
			if (p.get(i) == Player.Direction.NE)
				n = i;
		b[12][0] = new Hexagon(4, startingCoins, n, 12, 0);
		b[10][0] = new Hexagon(2, startingCoins, n, 10, 0);
		b[13][1] = new Hexagon(2, startingCoins, n, 13, 1);
		b[11][1] = new Hexagon(3, startingCoins, n, 11, 1);
		b[9][1] = new Hexagon(1, startingCoins, n, 9, 1);
		b[10][2] = new Hexagon(1, startingCoins, n, 10, 2);
		b[12][2] = new Hexagon(1, startingCoins, n, 12, 2);
		b[9][3] = new Hexagon(1, startingCoins, n, 9, 3);

		// e
		n = 0;
		for (int i = 0; i < p.size(); i++) {
			if (p.get(i) == Player.Direction.E)
				n = i;
		}

		b[16][4] = new Hexagon(4, startingCoins, n, 16, 4);
		b[15][3] = new Hexagon(2, startingCoins, n, 15, 3);
		b[15][5] = new Hexagon(2, startingCoins, n, 15, 5);
		b[14][4] = new Hexagon(3, startingCoins, n, 14, 4);
		b[13][3] = new Hexagon(1, startingCoins, n, 13, 3);
		b[14][4] = new Hexagon(1, startingCoins, n, 14, 4);
		b[13][5] = new Hexagon(1, startingCoins, n, 13, 5);
		b[10][4] = new Hexagon(1, startingCoins, n, 10, 4);

		// se
		n = 0;
		for (int i = 0; i < p.size(); i++)
			if (p.get(i) == Player.Direction.SE)
				n = i;
		b[12][8] = new Hexagon(4, startingCoins, n, 12, 8);
		b[13][7] = new Hexagon(2, startingCoins, n, 13, 7);
		b[10][8] = new Hexagon(2, startingCoins, n, 10, 8);
		b[11][7] = new Hexagon(3, startingCoins, n, 11, 7);
		b[12][6] = new Hexagon(1, startingCoins, n, 12, 6);
		b[10][6] = new Hexagon(1, startingCoins, n, 10, 6);
		b[9][7] = new Hexagon(1, startingCoins, n, 9, 7);
		b[9][5] = new Hexagon(1, startingCoins, n, 9, 5);

		// sw
		n = 0;
		for (int i = 0; i < p.size(); i++)
			if (p.get(i) == Player.Direction.SW)
				n = i;
		b[4][8] = new Hexagon(4, startingCoins, n, 4, 8);
		b[3][7] = new Hexagon(2, startingCoins, n, 3, 7);
		b[6][8] = new Hexagon(2, startingCoins, n, 6, 8);
		b[5][7] = new Hexagon(3, startingCoins, n, 5, 7);
		b[4][6] = new Hexagon(1, startingCoins, n, 4, 6);
		b[7][7] = new Hexagon(1, startingCoins, n, 7, 7);
		b[6][6] = new Hexagon(1, startingCoins, n, 6, 6);
		b[7][5] = new Hexagon(1, startingCoins, n, 7, 5);

		// w
		n = 0;
		for (int i = 0; i < p.size(); i++)
			if (p.get(i) == Player.Direction.W)
				n = i;
		b[0][4] = new Hexagon(4, startingCoins, n, 0, 4);
		b[1][3] = new Hexagon(2, startingCoins, n, 1, 3);
		b[1][5] = new Hexagon(2, startingCoins, n, 1, 5);
		b[2][4] = new Hexagon(3, startingCoins, n, 2, 4);
		b[3][3] = new Hexagon(1, startingCoins, n, 3, 3);
		b[4][4] = new Hexagon(1, startingCoins, n, 4, 4);
		b[3][5] = new Hexagon(1, startingCoins, n, 3, 5);
		b[6][4] = new Hexagon(1, startingCoins, n, 6, 4);

		// Blanks
		b[8][0] = new Hexagon(0, startingCoins, noone, 8, 0);
		b[2][2] = new Hexagon(0, startingCoins, noone, 2, 2);
		b[14][2] = new Hexagon(0, startingCoins, noone, 14, 2);
		b[11][3] = new Hexagon(0, startingCoins, noone, 11, 3);
		b[8][4] = new Hexagon(0, startingCoins, noone, 8, 4);
		b[5][5] = new Hexagon(0, startingCoins, noone, 5, 5);
		b[11][5] = new Hexagon(0, startingCoins, noone, 11, 5);
		b[2][6] = new Hexagon(0, startingCoins, noone, 2, 6);
		b[8][6] = new Hexagon(0, startingCoins, noone, 8, 6);
		b[14][6] = new Hexagon(0, startingCoins, noone, 14, 6);
		b[8][8] = new Hexagon(0, startingCoins, noone, 8, 8);

		fillAdjacentHexagonLists();
	}

	public ArrayList<Hexagon> getOurHexagons() {

		ArrayList<Hexagon> h = new ArrayList<Hexagon>();

		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				if (b[i][j].getOwner() == us)
					h.add(b[i][j]);
			}
		}

		return h;
	}

	public String printBoard() {
		String str = "";

		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				str += b[i][j] + "     ";
			}
			str += "\n";
		}

		return str;
	}

	public void updateMove(MoveResult mr) {

		if (mr.isSuccess()) {
			int coins = b[mr.getMove().getX()][mr.getMove().getY()]
					.getNumCoins();

			b[mr.getMove().getX()][mr.getMove().getY()].setNumCoins(0);

			b[mr.getMove().getNewX()][mr.getMove().getNewY()].setNumCoins(coins
					+ b[mr.getMove().getNewX()][mr.getMove().getNewY()]
							.getNumCoins());
		}
	}

	/** Returns the point change for our player given a move */
	public int getOurPoints(Move p) {

		int newspace = 0, oldspace = 0, wasonnewspace = 0, points = 0;
		
		log.trace("in get our points");

		if (b[p.getNewX()][p.getNewY()].getOwner() != us)
			newspace = b[p.getNewX()][p.getNewY()].getMultiplier()
					* (b[p.getNewX()][p.getNewY()].getNumCoins() + b[p.getX()][p
							.getY()].getNumCoins());

		if (b[p.getX()][p.getY()].getOwner() != us)
			oldspace = b[p.getX()][p.getY()].getMultiplier()
					* b[p.getX()][p.getY()].getNumCoins();

		if (b[p.getNewX()][p.getNewY()].getOwner() != us)
			wasonnewspace = b[p.getNewX()][p.getNewY()].getMultiplier()
					* (b[p.getNewX()][p.getNewY()].getNumCoins());

		log.trace("move: " + p.toString() + " get us: " + points + "( " +
		newspace + " -" + oldspace + "-" + wasonnewspace + ")");

		points = (-1) * (newspace - oldspace - wasonnewspace);

		return points;
	}

	public int getChangeInPoints(Move p) {

		int newspace = 0, oldspace = 0, wasonnewspace = 0, points = 0;

		if (b[p.getNewX()][p.getNewY()].getOwner() == us)
			newspace = b[p.getNewX()][p.getNewY()].getMultiplier()
					* (b[p.getNewX()][p.getNewY()].getNumCoins() + b[p.getX()][p
							.getY()].getNumCoins());

		if (b[p.getX()][p.getY()].getOwner() == us)
			oldspace = b[p.getX()][p.getY()].getMultiplier()
					* b[p.getX()][p.getY()].getNumCoins();

		if (b[p.getNewX()][p.getNewY()].getOwner() == us)
			wasonnewspace = b[p.getNewX()][p.getNewY()].getMultiplier()
					* (b[p.getNewX()][p.getNewY()].getNumCoins());

		// log.trace("move: " + p.toString() + " get us: " + points + "( " +
		// newspace + " -" + oldspace + "-" + wasonnewspace + ")");

		points = newspace - oldspace - wasonnewspace;

		return points;
	}

	/** Traces a path given a direction and a starting hexagon */
	public Hexagon traceOnDirection(Hexagon start, Direction d) {

		return b[start.getX() + d.getDx()][start.getY() + d.getDy()];
	}

	public Hexagon getHexagon(int x, int y) {
		return b[x][y];
	}

	public boolean isHexagonOurs(Hexagon h) {
		ArrayList<Hexagon> ourHexagons = getOurHexagons();
		for (Hexagon hex : ourHexagons) {
			if (hex.getX() == h.getX() && hex.getY() == h.getY())
				return true;
		}

		return true;
	}

	public Hexagon getHexAtPoint(int x, int y) {
		ArrayList<Hexagon> ourHexagons = getOurHexagons();
		for (Hexagon hex : ourHexagons) {
			if (hex.getX() == x && hex.getY() == y)
				return hex;
		}
		return getOurHexagons().get(0);
	}

	public ArrayList<Hexagon> getPlayerHexagons(int playerID) {

		ArrayList<Hexagon> h = new ArrayList<Hexagon>();

		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				if (b[i][j].getOwner() == playerID)
					h.add(b[i][j]);
			}
		}
		return h;
	}

	/* Tell each hexagon on the board who their neighbors are */
	private void fillAdjacentHexagonLists() {
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				Hexagon h = b[i][j];

				if (i + 1 < b.length && j + 1 < b[0].length)
					h.addAdjHex(b[i + 1][j + 1]);

				if (i - 1 >= 0 && j + 1 < b[0].length)
					h.addAdjHex(b[i - 1][j + 1]);

				if (i - 2 >= 0)
					h.addAdjHex(b[i - 2][j]);

				if (i - 1 >= 0 && j - 1 >= 0)
					h.addAdjHex(b[i - 1][j - 1]);

				if (i + 1 < b.length && j - 1 >= 0)
					h.addAdjHex(b[i + 1][j - 1]);

				if (i + 2 < b.length)
					h.addAdjHex(b[i + 2][j]);

			}
		}
	}

	public boolean validMove(Move nextMove) {

		// log.trace("looking for valid move");
		// log.trace("next move is: " + nextMove);

		/*
		 * if (checkStartPosition(nextMove) && checkDirection(nextMove) &&
		 * checkEndPosition(nextMove)){
		 */
		if (inBounds(nextMove)) {
			// log.trace("valid\n");
			return true;
		}

		// log.trace("not valid\n");
		return false;
	}

	private boolean checkStartPosition(Move nextMove) {
		log.trace("Number of pieces at move origin: "
				+ board[nextMove.getX()][nextMove.getY()]);

		/* if the start position is in bounds */
		/* and if the start position has a positive number of coins */

		/* Do not move from a spot that does not have any pieces. */
		if (board == null || board[nextMove.getX()][nextMove.getY()] == 0) {
			log.trace("Okay, I guess that doesn't work.");
			return false;
		}

		/* Make sure starting spot of the move exists on the board. */
		if (!inBounds(nextMove)) {
			return false;
		}

		log.trace("That works.");
		return true;
	}

	private boolean inBounds(Move m) {
		if (m.getNewX() < board.length && m.getNewY() < board[0].length) {
			log.trace("in bounds!");
			return true;
		}

		else {
			log.trace("oob\n");
			return false;
		}
	}

	private boolean checkDirection(Move nextMove) {
		log.trace("checking direction\n");

		/* if this direction is not towards this player */
		/* Make sure we're allowed to move in a given direction */
		if (!GameEngine.isValidDirectionForCellAndHome(nextMove.getDirection(),
				myCorner)) {
			return false;
		}
		return true;
	}

	private boolean checkEndPosition(Move nextMove) {
		log.trace("checking end position\n");
		/* make sure this position is on the board */
		/* Make sure both start and end spots of the move exist on the board. */
		if (!inBounds(nextMove)) {
			return false;
		}
		return true;
	}

	public ArrayList<PossibleMove> getPossibleMoves() {

		//log.trace("in get possible moves");

		ArrayList<PossibleMove> posMoves = new ArrayList<PossibleMove>();

		//log.trace("initialized arraylist");

		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				
				if (new PossibleMove(i, j, myCorner.getRelative(-1), this).isValid()) {
					posMoves.add(new PossibleMove(i,
							j,myCorner.getRelative(-1), this));
				}
				
				if (new PossibleMove(i, j, myCorner.getRelative(0), this).isValid()) {
					posMoves.add(new PossibleMove(i,
							j, myCorner.getRelative(0), this));
				}
				
				if (new PossibleMove(i, j, myCorner.getRelative(1), this).isValid()) {
					posMoves.add(new PossibleMove(i,
							j, myCorner.getRelative(1), this));
				}
			}
		}

		//log.trace("found all possible moves\n");
		return posMoves;
	}

	public int getPlayerScore(int playerID) {
		ArrayList<Hexagon> spaces = getPlayerHexagons(playerID);
		int score = 0;
		for (Hexagon h : spaces) {
			score += h.getValue();
		}

		return score;
	}

	public Direction getOurDirection() {
		return myCorner;
	}

	/** Returns our player id */
	public int getUs() {
		return us;
	}
}
