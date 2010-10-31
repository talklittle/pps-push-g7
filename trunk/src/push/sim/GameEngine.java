/* 
 * 	$Id: GameEngine.java,v 1.6 2007/11/28 16:30:47 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package push.sim;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import push.g0.DumbPlayer0;
import push.g0.DumbPlayer1;
import push.sim.GameListener.GameUpdateType;
import push.sim.Player.Direction;


public final class GameEngine {
	private GameConfig config;
	private Board board;

	// private PlayerWrapper player;
	private int round;
	private ArrayList<GameListener> gameListeners;
	private Logger log;
	ArrayList<Direction> positions;
	HashSet<Integer> losers;
	static {
		PropertyConfigurator.configure("logger.properties");
		System.setOut(new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
				
			}
		}));
		System.setErr(new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
				
			}
		}));
	}
	public GameEngine(String configFile) {
		config = new GameConfig(configFile);
		gameListeners = new ArrayList<GameListener>();
		board = new Board();
		board.engine = this;
		
		log = Logger.getLogger(GameController.class);
	}

	public void addGameListener(GameListener l) {
		gameListeners.add(l);
	}

	public int getCurrentRound() {
		return round;
	}

	public GameConfig getConfig() {
		return config;
	}

	public Board getBoard() {
		return board;
	}

	ArrayList<MoveResult> lastRound = new ArrayList<MoveResult>();
	public static int getDistance(Point from, Point to)
	{
		int dx = from.x - to.x;
		int dy = from.y - to.y;
		int s = 100;
		if (Math.abs(dx) == Math.abs(dy))
			s = Math.abs(dx);
		else if (Math.abs(dy) == 0)
			s = Math.abs(dx) / 2;
		else if (Math.abs(dx) == 0)
			s = Math.abs(dy);
		else if (Math.abs(dx) == 1)
			s = Math.abs(dy);
		else if (Math.abs(dx) == 2)
			s = Math.abs(dy);
		else if (Math.abs(dx) >= 3 &&  Math.abs(dx) <= 6 && Math.abs(dy) >= 5)
			s = Math.abs(dy);
		else {
			s = (int) Math.ceil((double) (Math.abs(dx) + Math
					.abs(dy)) / 2.0d);
		}
		return s;
		
	}
	public ArrayList<Integer> getScores() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int i = 0; i < 6; i++)
			ret.add(0);
		for (int j = 0; j < 9; j++) {
			int length = j;
			if (length > 4)
				length = 8 - length;
			int offset = 4 - length;
			length += 5;
			for (int i = 0; i < length; i++) {
				// Cell is (i,j) but indexed (j,i)
				int count = board.getCell(j, i * 2 + offset);
				Direction closest = null;
				Direction closest2 = null;
				int closestn = 8;
				int closestn2 = 8;
				Point conv = new Point(i * 2 + offset, j);
				for (Direction d : positions) {
					int s = getDistance(d.getHome(),conv);
					if(s == 100)
						s = -1;
					if (s <= closestn) {
						closest2 = closest;
						closestn2 = closestn;
						closest = d;
						closestn = s;
					} else if (s <= closestn2) {
						closest2 = d;
						closestn2 = s;
					}
				}
				if (closestn != closestn2) {
					// Add the score
					int score = count;
					score = score * (closestn2 - closestn);
					score = score + ret.get(directionToID.get(closest));
					ret.set(directionToID.get(closest), score);
				}
			}
		}
		for (int i = 0; i < 6; i++)
			if(losers.contains(i))
				ret.set(i, 0);
		return ret;
	}

	public boolean step() {
		if (1 < 0
				|| (config.getMaxRounds() > 0 && getCurrentRound() >= config
						.getMaxRounds())) {
			// GAME OVER!
			notifyListeners(GameUpdateType.GAMEOVER);
			return false;
		}

		board.saveBoard();
		ArrayList<MoveResult> thisRound = new ArrayList<MoveResult>();
		for (int i = 0; i < players.size(); i++) {
			Move thisMove = null;
			try
			{
				players.get(i).updateBoardState(board.board);
				thisMove = players.get(i).makeMove(lastRound);
			}
			catch(Exception e)
			{
				log.error(e.toString() + " at player " + i + " (" + players.get(i) + ")");
				thisMove=new Move(0, 0, Direction.E);
			}
			MoveResult r = new MoveResult(thisMove, i);
			if (isValidMove(r)) {
				r.setSuccess(true);
				thisRound.add(r);
			} else{
				// Check to see if there weren't any possible moves
				for (int j = 0; j < board.board.length && !losers.contains(i); j++) {
					int[] row = board.getRow(j);
					for (int k = 0; k < row.length&& !losers.contains(i); k++) {
						if (row[k] > 0 // There are coins here... but can we
										// make a valid move here?
								&& ((isInBounds(positions.get(i).getRelative(0)
										.getDx()
										+ k, positions.get(i).getRelative(0)
										.getDy()
										+ j) )
										|| (isInBounds(positions.get(i).getRelative(1)
												.getDx()
												+ k, positions.get(i).getRelative(1)
												.getDy()
												+ j)) ||(isInBounds(positions.get(i).getRelative(-1)
														.getDx()
														+ k, positions.get(i).getRelative(-1)
														.getDy()
														+ j)))
						) {
							losers.add(i);
							log.error("Player " + i + " ("+playerAtDirection(positions.get(i)) + " made an invalid move");
						}
					}
				}
				r.setSuccess(false);
			}
		}
		HashMap<Point, ArrayList<MoveResult>> conflicts = new HashMap<Point, ArrayList<MoveResult>>();
		for (int i = 0; i < thisRound.size(); i++) {
			Point t = new Point(thisRound.get(i).getMove().getX(), thisRound
					.get(i).getMove().getY());
			if (!conflicts.containsKey(t))
				conflicts.put(t, new ArrayList<MoveResult>());
			conflicts.get(t).add(thisRound.get(i));
		}
		for (ArrayList<MoveResult> c : conflicts.values()) {
			if (c.size() > 1) {
				int selected = GameConfig.random.nextInt(c.size());
				for (int i = 0; i < c.size(); i++) {
					if (i != selected)
						c.get(i).setSuccess(false);
				}
			}
		}
		for (int i = 0; i < thisRound.size(); i++) {
			// Process all moves marked as valid
			if (thisRound.get(i).isSuccess()) {
				Move m = thisRound.get(i).getMove();
				int x = m.getX();
				int y = m.getY();
				int n_x = m.getX() + m.getDirection().getDx();
				int n_y = m.getY() + m.getDirection().getDy();
				board.queueMove(n_x, n_y, x, y);
			}
		}
		board.executeQueue();
		int sum = 0;
		for (int i = 0; i < board.board.length; i++) {
			for (int j = 0; j < board.board[0].length; j++) {
				sum += board.board[i][j];
			}
		}
		lastRound = thisRound;
		notifyListeners(GameUpdateType.MOVEPROCESSED);
		round++;
		return true;
	}

	private boolean isValidMove(MoveResult m) {
		return isSuccessByBoundsEtc(m) && isSuccessByCount(m) && isValidDirectionForCellAndHome(m.getMove().getDirection(),positions.get(m.getPlayerId()));
	}
	public static boolean isValidDirectionForCellAndHome(Direction d, Direction from)
	{
		if(d.equals(from.getRelative(-1))
				|| d.equals(from.getRelative(0))
				|| d.equals(from.getRelative(1)))
			return true;
		return false;
	}
	private boolean isSuccessByCount(MoveResult m) {
		// Check that there are > 0 in this position
		if (board.getCell(m.getMove().getY(), m.getMove().getX()) == 0)
			return false;
		return true;
	}

	public static boolean isInBounds(int x, int y) {
		int newRow = y;
		if (newRow < 0 || newRow > 8)
			return false;
		int length = newRow;
		if (length > 4)
			length = 8 - length;
		int offset = 4 - length;
		length += 5;
		int newCol = x;
		if (newCol < 0 || newCol >= offset + length * 2 || newCol < offset)
		{
			return false;
		}
		// See if they are in an "off" cell
		if (newCol % 2 != newRow % 2)
		{
			return false;
		}
			
		return true;
	}

	private boolean isSuccessByBoundsEtc(MoveResult m) {
		// Check that we are in bounds
		if (!isInBounds(m.getMove().getNewX(), m.getMove().getNewY()))
			return false;
		if (!isInBounds(m.getMove().getX(), m.getMove().getY()))
			return false;
		// Check that the direction is OK
		if (!m.getMove().getDirection()
				.equals(positions.get(m.getPlayerId()).getRelative(0))
				&& !m.getMove().getDirection()
						.equals(positions.get(m.getPlayerId()).getRelative(-1))
				&& !m.getMove().getDirection()
						.equals(positions.get(m.getPlayerId()).getRelative(1)))
			return false;
		return true;
	}

	private final static void printUsage() {
		System.err.println("Usage: GameEngine <config file> gui");
		System.err
				.println("Usage: GameEngine <config file> text <board> <playerclass> <num mosquitos> <num lights> <long|short> {max rounds}");
	}

	public void removeGameListener(GameListener l) {
		gameListeners.remove(l);
	}

	private void notifyListeners(GameUpdateType type) {
		Iterator<GameListener> it = gameListeners.iterator();
		while (it.hasNext()) {
			it.next().gameUpdated(type);
		}
	}

	public static final void main(String[] args) {

		if (args.length < 2 || args.length > 8) {
			printUsage();
			System.exit(-1);
		}
		GameEngine engine = new GameEngine(args[0]);
		if (args[1].equalsIgnoreCase("text")) {
			// TextInterface ti = new TextInterface();
			// ti.register(engine);
			// ti.playGame();
			if (args.length < 7) {
				printUsage();
				System.exit(-1);
			}
			Text t = new Text(engine);
			// try {
			// engine.getConfig().setPlayerClass((Class<Player>)
			// Class.forName(args[3]));
			// } catch (ClassNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			engine.getConfig().setMaxRounds(Integer.valueOf(args[1]));
			if (args[6].equals("long"))

				t.setLongMode(true);
			t.play();

		} else if (args[1].equalsIgnoreCase("gui")) {

			new GUI(engine);
		} else if (args[1].equalsIgnoreCase("tournament")) {
			// runTournament(args, engine);
		} else {
			printUsage();
			System.exit(-1);
		}
	}

	public ArrayList<Player> players;
	public HashMap<Direction, Integer> directionToID;

	public boolean setUpGame() {
		// try
		// {
		round = 0;
		losers = new HashSet<Integer>();
		// curPlayer = config.getPlayerClass().newInstance();
		// curPlayer.Register();

		// } catch (InstantiationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		players = new ArrayList<Player>();
		if(config.getSelectedPlayers() == null || config.getSelectedPlayers().size() != 6)
			return false;
		for (Class<Player> p : config.getSelectedPlayers()) {
			try {
				players.add(p.newInstance());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		ArrayList<Player> newList = new ArrayList<Player>();
		for (int i = 0; i < 6; i++) {
			int n = GameConfig.random.nextInt(players.size());
			newList.add(players.get(n));
			players.remove(n);
		}
		players = newList;
		
//		players.add(new DumbPlayer0());
//		players.add(new DumbPlayer1());
//		players.add(new DumbPlayer1());
//		players.add(new DumbPlayer1());
//		players.add(new DumbPlayer1());
//		players.add(new DumbPlayer1());
		
		positions = new ArrayList<Direction>();
		positions.add(Direction.NW);
		positions.add(Direction.NE);
		positions.add(Direction.E);
		positions.add(Direction.SE);
		positions.add(Direction.SW);
		positions.add(Direction.W);
		directionToID = new HashMap<Player.Direction, Integer>();
		for (int i = 0; i < positions.size(); i++) {
			directionToID.put(positions.get(i), i);
		}
		for (int i = 0; i < players.size(); i++) {
			players.get(i).startNewGame(i, config.getMaxRounds(),
					(ArrayList<Direction>) positions.clone());
		}
		round = 0;
		board.init();
		getScores();
		notifyListeners(GameUpdateType.STARTING);
		return true;
	}

	public Player playerAtDirection(Direction d) {
		// System.out.println(d.toString() + "->" + directionToID.get(d));
		return players.get(directionToID.get(d));
	}

	public void mouseChanged() {
		notifyListeners(GameUpdateType.MOUSEMOVED);
	}
}
