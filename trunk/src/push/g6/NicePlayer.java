package push.g6;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import push.g6.strategy.EndMoveStrategy;
import push.g6.strategy.GeneralStrategy;
import push.g6.strategy.ShortTermStrategy;
import push.g6.strategy.Strategy;
import push.sim.Move;
import push.sim.MoveResult;

public class NicePlayer extends AbstractPlayer {

    private Strategy strategy = null;
    

    @Override
    public String getName() {
        return "NicePlayer";
    }

    @Override
    public void startNewGame(int id, int m, ArrayList<Direction> arrayList) {

  //      log.debug("NicePlayer starting...");
        this.myId = id;
        myCorner = arrayList.get(id);
        this.numRounds = m;
        this.fillWhites();

        if (m <= 10) {
            strategy = new ShortTermStrategy(this);
        } else {
            strategy = new GeneralStrategy(this);
        }
  //      log.debug("Initialized NicePlayer");

    }

    public boolean isValidMove(MoveResult m) {
        return isSuccessByBoundsEtc(m)
                && isSuccessByCount(m)
                && isValidDirectionForCellAndHome(m.getMove().getDirection(),
                        AbstractPlayer.getHomeofID(m.getPlayerId()));
    }

    public static boolean isValidDirectionForCellAndHome(Direction d,
            Direction from) {
        if (d.equals(from.getRelative(-1)) || d.equals(from.getRelative(0))
                || d.equals(from.getRelative(1)))
            return true;
        return false;
    }

    private boolean isSuccessByCount(MoveResult m) {
        // Check that there are > 0 in this position
        if (board[m.getMove().getY()][m.getMove().getX()] == 0)
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
        if (newCol < 0 || newCol >= offset + length * 2 || newCol < offset) {
            return false;
        }
        // See if they are in an "off" cell
        if (newCol % 2 != newRow % 2) {
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
        if (!m.getMove()
                .getDirection()
                .equals(AbstractPlayer.getHomeofID(m.getPlayerId())
                        .getRelative(0))
                && !m.getMove()
                        .getDirection()
                        .equals(AbstractPlayer.getHomeofID(m.getPlayerId())
                                .getRelative(-1))
                && !m.getMove()
                        .getDirection()
                        .equals(AbstractPlayer.getHomeofID(m.getPlayerId())
                                .getRelative(1)))
            return false;
        return true;
    }
    
   
    public Move makeNeutralMove() {
        for (Point2D point : whiteSpots) {

            int x = (int) point.getX() - myCorner.getRelative(0).getDx();
            int y = (int) point.getY() - myCorner.getRelative(0).getDy();

            if ((x >= 0 && y >= 0) && (x < board[0].length && y < board.length)
                    && board[y][x] != 0) {
                Move m = new Move(x, y, myCorner.getOpposite());
                MoveResult mr = new MoveResult(m, this.myId);
                if (this.isValidMove(mr))
                    return m;
            }

            x = (int) point.getX() - myCorner.getRelative(1).getDx();
            y = (int) point.getY() - myCorner.getRelative(1).getDy();

            if ((x >= 0 && y >= 0) && (x < board[0].length && y < board.length)
                    && board[y][x] != 0) {
                Move m = new Move(x, y, myCorner.getOpposite().getRight());
                MoveResult mr = new MoveResult(m, this.myId);
                if (this.isValidMove(mr))
                    return m;
            }

            x = (int) point.getX() - myCorner.getRelative(-1).getDx();
            y = (int) point.getY() - myCorner.getRelative(-1).getDy();

            if ((x >= 0 && y >= 0) && (x < board[0].length && y < board.length)
                    && board[y][x] != 0) {

                Move m = new Move(x, y, myCorner.getOpposite().getLeft());
                MoveResult mr = new MoveResult(m, this.myId);
                if (this.isValidMove(mr))
                    return m;

            }

        }
        log.debug("ID: " + this.myId + " returning a random move");
        return generateRandomMove(0);

    }

    @Override
    public Move makeMove(List<MoveResult> previousMoves) {

        // We should not change this order
        this.updateBoardState(board);
        this.updateScores();
        currentRound++;
        if(currentRound==this.numRounds)
            strategy=new EndMoveStrategy(this);
        return this.strategy.getMove(currentRound, previousMoves);

    }

}
