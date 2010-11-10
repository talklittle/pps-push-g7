package push.g6.strategy;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import push.g6.AbstractPlayer;
import push.g6.NicePlayer;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

/**
 * Strategy used for our last move
 */

public class EndMoveStrategy extends Strategy {

    private LinkedList<Point> neighbourhood = null;

    public EndMoveStrategy(NicePlayer p) {

        this.player = p;

        this.player = p;

        neighbourhood = new LinkedList<Point>();
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

//        for (Point po : neighbourhood) {
//            this.player.getLogger().debug(
//                    "Player " + player.getID() + " is close too " + po.getX()
//                            + " " + po.getY());
//
//        }

    }

    public Move getBeneficialMove() {
        Move m = null;
        int min = 62, maxDiffDist = 0;
        for (Point po : neighbourhood) {
            if (player.getBoard()[(int) po.getY()][(int) po.getX()] > 0) {

                for (int i = -1; i <= 1; i++) {
                    if (GameEngine.isInBounds((int) po.getX(), (int) po.getY())
                    // && GameEngine.isInBounds((int) po.getX()
                    // + this.player.getCorner().getRelative(i)
                    // .getDx(), (int) po.getY()
                    // + this.player.getCorner().getRelative(i)
                    // .getDy())
                    ) {

                        int id1 = (player.getID() - 1) % 6;
                        int id2 = (player.getID() + 1) % 6;

                        if (id1 < 0)
                            id1 += 6;

                        int distance = GameEngine
                                .getDistance(
                                        AbstractPlayer.getHomeofID(
                                                player.getID()).getHome(),
                                        new Point(
                                                (int) po.getX()
                                                        + this.player
                                                                .getCorner()
                                                                .getRelative(i)
                                                                .getDx(),
                                                (int) po.getY()
                                                        + this.player
                                                                .getCorner()
                                                                .getRelative(i)
                                                                .getDy()));

                        int distance1 = GameEngine
                                .getDistance(AbstractPlayer.getHomeofID(id1)
                                        .getHome(),
                                        new Point(
                                                (int) po.getX()
                                                        + this.player
                                                                .getCorner()
                                                                .getRelative(i)
                                                                .getDx(),
                                                (int) po.getY()
                                                        + this.player
                                                                .getCorner()
                                                                .getRelative(i)
                                                                .getDy()));

                        int distance2 = GameEngine
                                .getDistance(AbstractPlayer.getHomeofID(id2)
                                        .getHome(),
                                        new Point(
                                                (int) po.getX()
                                                        + this.player
                                                                .getCorner()
                                                                .getRelative(i)
                                                                .getDx(),
                                                (int) po.getY()
                                                        + this.player
                                                                .getCorner()
                                                                .getRelative(i)
                                                                .getDy()));

                        int old2 = GameEngine.getDistance(AbstractPlayer
                                .getHomeofID(id2).getHome(),
                                new Point((int) po.getX(), (int) po.getY()));
                        int old1 = GameEngine.getDistance(AbstractPlayer
                                .getHomeofID(id1).getHome(),
                                new Point((int) po.getX(), (int) po.getY()));
                        int old = GameEngine.getDistance(AbstractPlayer
                                .getHomeofID(player.getID()).getHome(),
                                new Point((int) po.getX(), (int) po.getY()));

                        int disto = Math.min(distance1, distance2);
                        if ((m == null)
                                || ((old - Math.min(old1, old2) == 0) && disto > 0)
                                || (((disto - distance) > maxDiffDist) && (disto - distance) > 0)
                                || ((disto - distance) == maxDiffDist)
                                && (player.getBoard()[(int) po.getY()][(int) po
                                        .getX()] < min)) {

                            min = player.getBoard()[(int) po.getY()][(int) po
                                    .getX()];
                            maxDiffDist = disto - distance;

                            m = new Move((int) po.getX(), (int) po.getY(),
                                    this.player.getCorner().getRelative(i));
                        }
                    }
                }
            }
        }

        if (min != 62)
            return m;
        else
            return null;
    }

    @Override
    public Move getMove(int round, List<MoveResult> previousMoves) {

        // Decide whether to harm the stronger or to help the weakest
        // or help ourselves if we can

        Move m = null;

        if ((m = this.getBeneficialMove()) != null)
            return m;
        

        int firstId = this.player.getHighestScorer();
        int lastId = this.player.getLowestScorer();

        // Check if we can move away of an enemy
        // Check if we can move towards a weaker player
        int i=0;
        for (Point cp : this.player.getPiles().keySet()) {
            i++;
            if (!this.neighbourhood.contains(cp) || (i==this.player.getPileCnt()))
            {
                int player=AbstractPlayer.getPlayerBelongs(cp);
                if(player==-1)
                    continue;
                Direction d=AbstractPlayer.getHomeofID(player);
                m = new Move((int) cp.getX(), (int) cp.getY(),
                        d.getOpposite());
       
                MoveResult mr = new MoveResult(m, this.player.getID());
                if (this.player.isValidMove(mr))
                    return m;
                m = new Move((int) cp.getX(), (int) cp.getY(),  d.getOpposite().getLeft());
                mr = new MoveResult(m, this.player.getID());
                if (this.player.isValidMove(mr))
                    return m;
                
                m = new Move((int) cp.getX(), (int) cp.getY(), d.getOpposite().getRight());
                mr = new MoveResult(m, this.player.getID());
                if (!this.player.isValidMove(mr))
                    continue;
                else
                    return m;
            }
        }
        
        i=0;
        for (Point cp : this.player.getPiles().keySet()) {
            i++;
            //if (!this.neighbourhood.contains(cp) || (i==this.player.getPileCnt()))
            {
                int player=AbstractPlayer.getPlayerBelongs(cp);
                if(player==-1)
                    continue;
                
                Direction d=AbstractPlayer.getHomeofID(player);
                if(player==this.player.getID()
                        && !AbstractPlayer.isWhite(cp))
                    d=d.getOpposite();
                m = new Move((int) cp.getX(), (int) cp.getY(),
                        d);
       
                MoveResult mr = new MoveResult(m, this.player.getID());
                if (this.player.isValidMove(mr))
                    return m;
                m = new Move((int) cp.getX(), (int) cp.getY(),  d.getLeft());
                mr = new MoveResult(m, this.player.getID());
                if (this.player.isValidMove(mr))
                    return m;
                
                m = new Move((int) cp.getX(), (int) cp.getY(), d.getRight());
                mr = new MoveResult(m, this.player.getID());
                if (!this.player.isValidMove(mr))
                    continue;
                else
                    return m;
            }
        }
        

        // No valid move available
        return this.player.makeNeutralMove();
    }

}
