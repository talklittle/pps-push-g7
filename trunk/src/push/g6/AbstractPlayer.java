package push.g6;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public abstract class AbstractPlayer extends Player {

	protected static int[][] board;
	protected Direction myCorner;
	protected int currentRound = 0;
	protected int numRounds = -1;
	protected int myId;
	protected ArrayList<Point> whiteSpots = new ArrayList<Point>();
	protected HashMap<Integer, Integer> cumulativeBenefit = new HashMap<Integer, Integer>();
	protected Logger log = Logger.getLogger(this.getClass());
	protected int pileCnt = 0;
	protected ArrayList<Integer> invalidated = new ArrayList<Integer>();
	protected ArrayList<Integer> scores = null;
	protected TreeMap<Integer, Point> piles = null;
	protected TreeMap<Integer, Integer> currentRank = null;

	public ArrayList<Point> getWhiteSpots() {
		return whiteSpots;
	}

	public AbstractPlayer() {
	}

	public abstract String getName();

	@Override
	public void updateBoardState(int[][] board) {
		AbstractPlayer.board = board;
	}

	public int getID() {
		return this.myId;
	}

	public Logger getLogger() {
		return log;
	}

	public int getTotalRounds() {
		return this.numRounds;
	}

	public Direction getCorner() {
		return this.myCorner;
	}

	public int[][] getBoard() {
		return board;
	}

	public int getPileCnt() {
		return this.pileCnt;
	}

	public TreeMap<Integer, Point> getPiles() {
		return this.piles;
	}

	public ArrayList<Integer> getScores() {
		return this.scores;
	}

	public TreeMap<Integer, Integer> getRanks() {
		return this.currentRank;
	}

	@Override
	public void startNewGame(int id, int m, ArrayList<Direction> arrayList) {

		myCorner = arrayList.get(id);
		this.myId = id;
	}

	public void fillWhites() {
		if (myCorner.equals(Direction.E)) {
			whiteSpots.add(new Point(8, 0));
			whiteSpots.add(new Point(8, 2));
			whiteSpots.add(new Point(8, 4));
			whiteSpots.add(new Point(8, 6));
			whiteSpots.add(new Point(8, 8));
			whiteSpots.add(new Point(5, 3));
			whiteSpots.add(new Point(5, 5));
			whiteSpots.add(new Point(2, 2));
			whiteSpots.add(new Point(2, 6));
		}
		if (myCorner.equals(Direction.NE)) {
			whiteSpots.add(new Point(11, 5));
			whiteSpots.add(new Point(14, 6));
			whiteSpots.add(new Point(8, 4));
			whiteSpots.add(new Point(8, 6));
			whiteSpots.add(new Point(8, 8));
			whiteSpots.add(new Point(5, 3));
			whiteSpots.add(new Point(5, 5));
			whiteSpots.add(new Point(2, 2));
			whiteSpots.add(new Point(2, 6));
		}
		if (myCorner.equals(Direction.SE)) {
			whiteSpots.add(new Point(11, 3));
			whiteSpots.add(new Point(14, 2));
			whiteSpots.add(new Point(8, 4));
			whiteSpots.add(new Point(8, 6));
			whiteSpots.add(new Point(8, 0));
			whiteSpots.add(new Point(5, 3));
			whiteSpots.add(new Point(5, 5));
			whiteSpots.add(new Point(2, 2));
			whiteSpots.add(new Point(2, 6));
			whiteSpots.add(new Point(8, 2));
		}
		if (myCorner.equals(Direction.W)) {
			whiteSpots.add(new Point(8, 0));
			whiteSpots.add(new Point(8, 4));
			whiteSpots.add(new Point(8, 6));
			whiteSpots.add(new Point(8, 8));
			whiteSpots.add(new Point(8, 2));
			whiteSpots.add(new Point(11, 3));
			whiteSpots.add(new Point(11, 5));
			whiteSpots.add(new Point(14, 2));
			whiteSpots.add(new Point(14, 6));

		}
		if (myCorner.equals(Direction.SW)) {
			whiteSpots.add(new Point(11, 5));
			whiteSpots.add(new Point(14, 6));
			whiteSpots.add(new Point(8, 4));
			whiteSpots.add(new Point(14, 2));
			whiteSpots.add(new Point(8, 0));
			whiteSpots.add(new Point(5, 3));
			whiteSpots.add(new Point(11, 3));
			whiteSpots.add(new Point(2, 2));
			whiteSpots.add(new Point(8, 2));
		}
		if (myCorner.equals(Direction.NW)) {
			whiteSpots.add(new Point(11, 3));
			whiteSpots.add(new Point(14, 2));
			whiteSpots.add(new Point(8, 4));
			whiteSpots.add(new Point(8, 6));
			whiteSpots.add(new Point(14, 6));
			whiteSpots.add(new Point(11, 5));
			whiteSpots.add(new Point(5, 5));
			whiteSpots.add(new Point(8, 8));
			whiteSpots.add(new Point(2, 6));
		}

	}

	public static Direction getHomeofID(int id) {
		switch (id) {
		case 0:
			return Direction.NW;
		case 1:
			return Direction.NE;
		case 2:
			return Direction.E;
		case 3:
			return Direction.SE;
		case 4:
			return Direction.SW;
		case 5:
			return Direction.W;

		}
		return null;
	}

	/**
	 * Function returning to which player's territory a coin belongs
	 */
	public static int getPlayerBelongs(Point p) {

		Direction closest = null;
		int neighbour1 = 8;
		int neighbour2 = 8;
		for (Direction d : Direction.values()) {
			int s = GameEngine.getDistance(d.getHome(), p);
			if (s == 100)
				s = -1;
			if (s <= neighbour1) {
				neighbour2 = neighbour1;
				closest = d;
				neighbour1 = s;
			} else if (s <= neighbour2) {
				neighbour2 = s;
			}
		}
		if (neighbour1 != neighbour2) {

			return AbstractPlayer.getIdOfPoint(closest);
		}

		return -1;

	}

	public static boolean isWhite(Point p) {

		int neighbour1 = 8;
		int neighbour2 = 8;
		for (Direction d : Direction.values()) {
			int s = GameEngine.getDistance(d.getHome(), p);
			if (s == 100)
				s = -1;
			if (s <= neighbour1) {
				neighbour2 = neighbour1;
				neighbour1 = s;
			} else if (s <= neighbour2) {
				neighbour2 = s;
			}
		}
		if (neighbour1 != neighbour2) {

			return false;
		}

		return false;

	}

	public static int getIdOfPoint(Direction d) {
		switch (d) {
		case NW:
			return 0;
		case NE:
			return 1;
		case E:
			return 2;
		case SE:
			return 3;
		case SW:
			return 4;
		case W:
			return 5;
		}
		return -1;
	}

	/**
	 * Function evaluating last round's moves of opponents
	 */
	public void evaluateMoves(List<MoveResult> previousMoves) {

		if ((previousMoves == null) || previousMoves.isEmpty())
			return;

		int sum = 0;
		int min = 0, max = 0;
		for (MoveResult mr : previousMoves) {
			if (mr.getPlayerId() == this.myId)
				continue;

			// Evaluate whether the moves of the other players have helped or
			// not

			if (!GameEngine.isInBounds(mr.getMove().getNewY(), mr.getMove()
					.getNewX())
					|| !GameEngine.isValidDirectionForCellAndHome(
							AbstractPlayer.getHomeofID(mr.getPlayerId()), mr
									.getMove().getDirection())) {
				invalidated.add(mr.getPlayerId());
				continue;
			}
			// Compute the previous distance from our corner
			int previousDistance = GameEngine.getDistance(this.myCorner
					.getHome(), new Point(mr.getMove().getX(), mr.getMove()
					.getY()));

			int newDistance = GameEngine.getDistance(this.myCorner.getHome(),
					new Point(mr.getMove().getNewX(), mr.getMove().getNewY()));

			// Metric for how much beneficial was the last move
			Integer benefit = (previousDistance - newDistance)
					* board[mr.getMove().getNewY()][mr.getMove().getNewX()];

			if (this.cumulativeBenefit.containsKey((Integer) mr.getPlayerId())) {
				benefit += this.cumulativeBenefit.get((Integer) mr
						.getPlayerId());
			}
			this.cumulativeBenefit.put((Integer) mr.getPlayerId(), benefit);
			sum += benefit;

			if (benefit < min)
				min = benefit;
			if (benefit > max)
				max = benefit;

		}

		if (max <= 0) {
			return;
		}
		if (sum <= 0)
			sum = max;
		// Pick which player to benefit
		Random r = new Random();
		int probability = r.nextInt(sum);

		sum = 0;
		int sid = 1;
		for (Integer id : cumulativeBenefit.keySet()) {
			Integer i = cumulativeBenefit.get(id);
			if ((probability >= sum) && (probability < (sum + i))) {
				sid = id;
				break;
			}
		}
		log.debug("Decided to help " + sid);
		// Check whether there is an available move for sid

	}

	public static int getScoreOfCell(Point p) {

		int neighbour1 = 8;
		int neighbour2 = 8;
		for (Direction d : Direction.values()) {
			int s = GameEngine.getDistance(d.getHome(), p);
			if (s == 100)
				s = -1;
			if (s <= neighbour1) {
				neighbour2 = neighbour1;
				neighbour1 = s;
			} else if (s <= neighbour2) {
				neighbour2 = s;
			}
		}
		if (neighbour1 != neighbour2) {
			return (neighbour2 - neighbour1);

		}

		return 0;
	}

	/**
	 * Function updating last round's scores
	 */
	public ArrayList<Integer> updateScores() {
		scores = new ArrayList<Integer>();
		piles = new TreeMap<Integer, Point>();
		for (int i = 0; i < PushValues.NUM_PLAYERS; i++)
			scores.add(0);
		this.pileCnt = 0;
		for (int j = 0; j < 9; j++) {
			int curRowLen = j;
			if (curRowLen > 4)
				curRowLen = 8 - curRowLen;
			int offset = 4 - curRowLen;
			curRowLen += 5;
			for (int i = 0; i < curRowLen; i++) {
				int cointNum = this.board[j][i * 2 + offset];
				if (cointNum != 0) {
					this.pileCnt++;
					this.piles.put(cointNum, new Point(i * 2 + offset, j));
				}
				Direction closest = null;
				int neighbour1 = 8;
				int neighbour2 = 8;
				Point conv = new Point(i * 2 + offset, j);
				for (Direction d : Direction.values()) {
					int s = GameEngine.getDistance(d.getHome(), conv);
					if (s == 100)
						s = -1;
					if (s <= neighbour1) {
						neighbour2 = neighbour1;
						closest = d;
						neighbour1 = s;
					} else if (s <= neighbour2) {
						neighbour2 = s;
					}
				}
				if (neighbour1 != neighbour2) {
					// Add the score
					int score = cointNum;
					score = score * (neighbour2 - neighbour1);
					score = score
							+ scores.get(AbstractPlayer.getIdOfPoint(closest));
					scores.set(AbstractPlayer.getIdOfPoint(closest), score);
				}
			}
		}

		// Check invalidated
		for (int i = 0; i < 6; i++)
			if (this.invalidated.contains(i))
				scores.set(i, 0);

		

		ArrayList<Integer> oldScores = (ArrayList<Integer>) scores.clone();

		Collections.sort(scores);
		currentRank = new TreeMap<Integer, Integer>();

		int j = 0, previous = -1;
		for (int i = 0; i < oldScores.size(); i++) {
			if (previous == scores.get(i))
				j++;
			else
				j = 0;

			currentRank.put(oldScores.size() - i - j,
					oldScores.indexOf(scores.get(i)) + j);
			// log.debug("Player "+((oldScores.indexOf(scores.get(i)))+j)+" ranks "
			// +(oldScores.size()-i) );
			previous = scores.get(i);
		}

		return scores;
	}


	/**
	 * Function generating a random move
	 */
	public Move generateRandomMove(int depth)
	{
		if(depth > 300)
		{
			return new Move(0,0,Direction.NE);
		}
		int n2 = GameConfig.random.nextInt(9);
		int length = n2;
		if(length > 4)
		length=8-length;
		int offset = 4-length;
		length+=5;
		int n1 = GameConfig.random.nextInt(length);
		n1*=2;
		n1 += offset;
		if(!GameEngine.isInBounds(n1, n2))
			return generateRandomMove(depth+1);
		
		if(board != null&& board[n2][n1] == 0)
			return generateRandomMove(depth+1);
		Direction d = myCorner.getRelative(GameConfig.random.nextInt(3)-1);
		int tries = 0;
		while(!GameEngine.isValidDirectionForCellAndHome(d, myCorner) && tries < 10)
		{
			d = myCorner.getRelative(GameConfig.random.nextInt(2)-1);
			
			tries++;
		}
		if(!GameEngine.isValidDirectionForCellAndHome(d, myCorner))
			return generateRandomMove(depth+1);
		
		if(!GameEngine.isInBounds(n1+d.getDx(), n2+d.getDy()))
			return generateRandomMove(depth+1);
		
		Move m = new Move(n1, n2,d);
		return m;
	}
	

	public Move moveToWhite() {

		for (Point point : whiteSpots) {

			int x = (int) point.getX() - myCorner.getRelative(0).getDx();
			int y = (int) point.getY() - myCorner.getRelative(0).getDy();

			if ((x >= 0 && y >= 0) && (x < board[0].length && y < board.length)
					&& board[y][x] != 0)
				return new Move(x, y, myCorner.getOpposite());

			x = (int) point.getX() - myCorner.getRelative(1).getDx();
			y = (int) point.getY() - myCorner.getRelative(1).getDy();

			if ((x >= 0 && y >= 0) && (x < board[0].length && y < board.length)
					&& board[y][x] != 0)
				return new Move(x, y, myCorner.getOpposite().getRight());

			x = (int) point.getX() - myCorner.getRelative(-1).getDx();
			y = (int) point.getY() - myCorner.getRelative(-1).getDy();

			if ((x >= 0 && y >= 0) && (x < board[0].length && y < board.length)
					&& board[y][x] != 0)
				return new Move(x, y, myCorner.getOpposite().getLeft());

		}
		return this.generateRandomMove(0);

	}

	@Override
	public Move makeMove(List<MoveResult> previousMoves) {
		this.updateBoardState(board);

		return null;
	}
}
