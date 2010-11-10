package push.g6.strategy;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import push.g6.AbstractPlayer;
import push.g6.NicePlayer;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

/**
 * A general strategy class
 *
 */
public abstract class Strategy {

    protected NicePlayer player = null;
	protected LinkedList<Point> neighbourhood =  new LinkedList<Point>();

    public abstract Move getMove(int round, List<MoveResult> previousMoves);
    
    public Move getBeneficialMove() {
		Move m = null;

		TreeMap<Integer, Move> moves = new TreeMap<Integer, Move>();
		for (Point po : neighbourhood) {
			if (player.getBoard()[(int) po.getY()][(int) po.getX()] > 0) {

				if (GameEngine.isInBounds((int) po.getX(), (int) po.getY())) {

					Direction d = AbstractPlayer.getHomeofID(this.player
							.getID());
					m = new Move((int) po.getX(), (int) po.getY(),
							d.getRelative(0));

					MoveResult mr = new MoveResult(m, this.player.getID());
					if (this.player.isValidMove(mr)
							&& (AbstractPlayer.getScoreOfCell(po) <= AbstractPlayer
									.getScoreOfCell(new Point(m.getNewX(), m
											.getNewY())))
							&& (AbstractPlayer.getPlayerBelongs(new Point(m
									.getNewX(), m.getNewY())) == this.player
									.getID())) {

						moves.put(
								AbstractPlayer.getScoreOfCell(new Point(m
										.getNewX(), m.getNewY()))
										* player.getBoard()[(int) po.getY()][(int) po
												.getX()], m);
						// return m;
					}

					m = new Move((int) po.getX(), (int) po.getY(),
							d.getRelative(-1));
					mr = new MoveResult(m, this.player.getID());
					if (this.player.isValidMove(mr)
							&& (AbstractPlayer.getScoreOfCell(po) <= AbstractPlayer
									.getScoreOfCell(new Point(m.getNewX(), m
											.getNewY())) && (AbstractPlayer
									.getPlayerBelongs(new Point(m.getNewX(), m
											.getNewY())) == this.player.getID()))) {

						moves.put(
								AbstractPlayer.getScoreOfCell(new Point(m
										.getNewX(), m.getNewY()))
										* player.getBoard()[(int) po.getY()][(int) po
												.getX()], m);
						// return m;
					}

					m = new Move((int) po.getX(), (int) po.getY(),
							d.getRelative(1));
					mr = new MoveResult(m, this.player.getID());
					if (!this.player.isValidMove(mr)
							|| !(AbstractPlayer.getScoreOfCell(po) <= AbstractPlayer
									.getScoreOfCell(new Point(m.getNewX(), m
											.getNewY())))
							&& (AbstractPlayer.getPlayerBelongs(new Point(m
									.getNewX(), m.getNewY())) == this.player
									.getID()))
						continue;
					else {
						moves.put(
								AbstractPlayer.getScoreOfCell(new Point(m
										.getNewX(), m.getNewY()))
										* player.getBoard()[(int) po.getY()][(int) po
												.getX()], m);
						// return m;
					}

				}

			}
		}

		if (moves.size() > 0) {
			for (Move mret : moves.descendingMap().values())
				return mret;
		}

		return null;
	}

}
