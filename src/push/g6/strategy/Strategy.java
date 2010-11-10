package push.g6.strategy;

import java.util.List;

import push.g6.AbstractPlayer;
import push.g6.NicePlayer;
import push.sim.Move;
import push.sim.MoveResult;

/**
 * A general strategy class
 *
 */
public abstract class Strategy {

    protected NicePlayer player = null;
    public abstract Move getMove(int round, List<MoveResult> previousMoves);
}
