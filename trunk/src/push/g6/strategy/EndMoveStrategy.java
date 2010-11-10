package push.g6.strategy;

import java.awt.Point;
import java.util.List;
import java.util.TreeMap;

import push.g6.AbstractPlayer;
import push.g6.NicePlayer;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

/**
 * Strategy used for our last move
 */

public class EndMoveStrategy extends Strategy {


	public EndMoveStrategy(NicePlayer p) {

		this.player = p;

		System.out.println("homex " + this.player.getCorner().getHome().getX()
				+ " homey " + this.player.getCorner().getHome().getY());
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

	}

	
	@Override
	public Move getMove(int round, List<MoveResult> previousMoves) {

		Move bm = null;

		// Check if we can move away of an enemy
		// Check if we can move towards a weaker player

		if ((bm = this.getBeneficialMove()) != null)
			return bm;

		for (Integer rank : this.player.getRanks().keySet()) {

			TreeMap<Integer, Move> moves = new TreeMap<Integer, Move>();
			int id = this.player.getRanks().get(rank);
			for (Point cp : this.player.getPiles().descendingMap().values()) {

				if (!this.neighbourhood.contains(cp)) {
					int player = AbstractPlayer.getPlayerBelongs(cp);
					if (player != id || (player == -1)
							|| (player == this.player.getID()))
						continue;
					Direction d = AbstractPlayer.getHomeofID(player);
					Move m = new Move((int) cp.getX(), (int) cp.getY(),
							d.getOpposite());

					MoveResult mr = new MoveResult(m, this.player.getID());
					if (this.player.isValidMove(mr)
							&& (AbstractPlayer.getScoreOfCell(cp) > AbstractPlayer
									.getScoreOfCell(new Point(m.getNewX(), m
											.getNewY())))) {
						moves.put(AbstractPlayer.getScoreOfCell(new Point(m
								.getNewX(), m.getNewY())), m);
					}
					m = new Move((int) cp.getX(), (int) cp.getY(), d
							.getOpposite().getLeft());
					mr = new MoveResult(m, this.player.getID());
					if (this.player.isValidMove(mr)
							&& (AbstractPlayer.getScoreOfCell(cp) > AbstractPlayer
									.getScoreOfCell(new Point(m.getNewX(), m
											.getNewY())))) {
						moves.put(AbstractPlayer.getScoreOfCell(new Point(m
								.getNewX(), m.getNewY())), m);
						// return m;
					}

					m = new Move((int) cp.getX(), (int) cp.getY(), d
							.getOpposite().getRight());
					mr = new MoveResult(m, this.player.getID());
					if (!this.player.isValidMove(mr)
							&& (AbstractPlayer.getScoreOfCell(cp) > AbstractPlayer
									.getScoreOfCell(new Point(m.getNewX(), m
											.getNewY()))))
						continue;
					else {
						moves.put(AbstractPlayer.getScoreOfCell(new Point(m
								.getNewX(), m.getNewY())), m);
					}
				}
			}
			if (moves.size() > 0) {
				for (Move mret : moves.values()) {
					return mret;
				}
			}

		}

		for (Integer rank : this.player.getRanks().descendingKeySet()) {

			TreeMap<Integer, Move> moves = new TreeMap<Integer, Move>();
			int id = this.player.getRanks().get(rank);
			for (Point cp : this.player.getPiles().descendingMap().values()) {

				if (!this.neighbourhood.contains(cp)) {
					int player = AbstractPlayer.getPlayerBelongs(cp);
					if (player != id || (player == -1))
						continue;
					Direction d = AbstractPlayer.getHomeofID(player);
					Move m = new Move((int) cp.getX(), (int) cp.getY(), d);

					MoveResult mr = new MoveResult(m, this.player.getID());
					if (this.player.isValidMove(mr)) {
						moves.put(AbstractPlayer.getScoreOfCell(new Point(m
								.getNewX(), m.getNewY())), m);
						// return m;
					}
					m = new Move((int) cp.getX(), (int) cp.getY(), d.getLeft());
					mr = new MoveResult(m, this.player.getID());
					if (this.player.isValidMove(mr)) {
						moves.put(AbstractPlayer.getScoreOfCell(new Point(m
								.getNewX(), m.getNewY())), m);
						// return m;
					}

					m = new Move((int) cp.getX(), (int) cp.getY(), d.getRight());
					mr = new MoveResult(m, this.player.getID());
					if (!this.player.isValidMove(mr))
						continue;
					else {
						moves.put(AbstractPlayer.getScoreOfCell(new Point(m
								.getNewX(), m.getNewY())), m);
						// return m;
					}
				}
			}
			if (moves.size() > 0) {
				for (Move mret : moves.values()){
					return mret;
				}

			}

		}

		// No valid move available
		return this.player.makeNeutralMove();
	}

}
