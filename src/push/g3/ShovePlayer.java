package push.g3;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class ShovePlayer extends Player {

    private int[][] board;
    private int ourID;
    private Direction myCorner;

    private Direction[] bestPotentialFriends;
    private int currentAttemptedFriend;
    private int numCurrentAttmptedRoundsToMakeThisFriend;
    private final int MAX_ATTEMPTED_ROUNDS_TO_MAKE_FRIENDS = 5;
    private boolean hasCurrentAttmptedFriendBecomeOurFriend;

    private final int LEFT = 0;
    private final int BEST = 1;
    private final int RIGHT = 2;
    private final int MAX_POTENTIAL_FRIENDS = 3;

    private final int MAX_DISTANCE_FROM_HOME = 3;

    public enum MoveClassification {
        HELPS_MOST (0), 
        HELPS_A_LOT (1), 
        KIND_OF_HELPS (2), 
        SORT_OF_HELPS (3), 
        NEUTRAL (4), 
        SORT_OF_HURTS (5), 
        KIND_OF_HURTS (6), 
        HURTS_A_LOT (7), 
        HURTS_MOST (8);

        int ranking;

        MoveClassification(int rank) {
            ranking = rank;
        }

        boolean doesHelpUs(MoveClassification mc) {
            return mc.ranking < MoveClassification.NEUTRAL.ranking;
        }

        boolean doesNotHelpUs(MoveClassification mc) {
            return mc.ranking >= MoveClassification.NEUTRAL.ranking;
        }
    }

    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public String getName()
    {
        return "ShovePlayer";
    }

    @Override
    public void startNewGame(int id, int m, ArrayList<Direction> arrayList)
    {
        this.ourID = id;
        this.myCorner = arrayList.get(id);
        bestPotentialFriends = new Direction[MAX_POTENTIAL_FRIENDS];
        bestPotentialFriends[BEST] = myCorner.getOpposite();
        bestPotentialFriends[LEFT] = bestPotentialFriends[BEST].getLeft();
        bestPotentialFriends[RIGHT] = bestPotentialFriends[BEST].getRight();

        currentAttemptedFriend = BEST;

        numCurrentAttmptedRoundsToMakeThisFriend = 0;
        hasCurrentAttmptedFriendBecomeOurFriend = false;
    }

    @Override
    public void updateBoardState(int[][] board) {
        this.board= board;
    }

    @Override
    public Move makeMove(List<MoveResult> previousMoves)
    {
        for(MoveResult mr : previousMoves)
            logger.debug("Player" + mr.getPlayerId() + " moved " + mr.getMove() + ". Success is " + mr.isSuccess());
        logger.debug("");

        //TODO: check to see if our friend has helped us
        //TODO: if all of our potential friends shun us... let's be evil.
        //TODO: if someone helps us who we didn't try to reach out to... reciprocate and make them our friend

        if(playerHasTriedToHelpUsThisTurn(directionToPlayerID(bestPotentialFriends[currentAttemptedFriend]), previousMoves))
            hasCurrentAttmptedFriendBecomeOurFriend = true; //TODO: what if the friend starts backstabbing us, we need to notice this


        if(numCurrentAttmptedRoundsToMakeThisFriend > MAX_ATTEMPTED_ROUNDS_TO_MAKE_FRIENDS && !hasCurrentAttmptedFriendBecomeOurFriend) {
            currentAttemptedFriend = (currentAttemptedFriend + 1) % MAX_POTENTIAL_FRIENDS;
            numCurrentAttmptedRoundsToMakeThisFriend = 0; // reset to start
        }

        Move nextMove = PlayerBasicAbility.doGood(bestPotentialFriends[currentAttemptedFriend], myCorner, board);
        
        //if we couldn't find a valid move... try someone else. TODO: kill magic numbers
        if(nextMove == null)
            nextMove = PlayerBasicAbility.doGood(bestPotentialFriends[(currentAttemptedFriend + 1) % MAX_POTENTIAL_FRIENDS], myCorner, board);
        if(nextMove == null)
            nextMove = PlayerBasicAbility.doGood(bestPotentialFriends[(currentAttemptedFriend + 2) % MAX_POTENTIAL_FRIENDS], myCorner, board);
        if(nextMove == null) {
            logger.error("defaulting to generateRandomMove! Bad!");
            nextMove = generateRandomMove(0);
        }
        
        numCurrentAttmptedRoundsToMakeThisFriend++;
        return nextMove;
    }

    public boolean playerHasTriedToHelpUsThisTurn(int friendID, List<MoveResult> previousMoves) {
        for(MoveResult mr : previousMoves) {
            if(friendID == mr.getPlayerId()) {
                return (didMoveHelpUs(mr.getMove()).ranking < MoveClassification.NEUTRAL.ranking);
            }
        }
        return false;
    }

    //Checks to see if other players have initiated co-operation (or moved coins towards our home slot)
    public MoveClassification didMoveHelpUs(Move move){
        Point moveOrigin = new Point(move.getX(), move.getY());
        Point moveDestination = new Point(move.getNewX(), move.getNewY());
        int originDistance = GameEngine.getDistance(myCorner.getHome(), moveOrigin);
        int destinationDistance = GameEngine.getDistance(myCorner.getHome(), moveDestination);
        
        if(isInOurArea(move)) {
            if(destinationDistance <= MAX_DISTANCE_FROM_HOME || originDistance <= MAX_DISTANCE_FROM_HOME) {
                if(originDistance < destinationDistance) {
                    switch(originDistance) {
                    case 0:
                        return MoveClassification.HURTS_MOST;
                    case 1:
                        return MoveClassification.HURTS_A_LOT;
                    case 2:
                        return MoveClassification.KIND_OF_HURTS;
                    case 3:
                        return MoveClassification.SORT_OF_HURTS;
                    }
                } else if (originDistance > destinationDistance) {
                    switch(destinationDistance) {
                    case 0:
                        return MoveClassification.HELPS_MOST;
                    case 1:
                        return MoveClassification.HELPS_A_LOT;
                    case 2:
                        return MoveClassification.KIND_OF_HELPS;
                    case 3:
                        return MoveClassification.SORT_OF_HELPS;
                    }
                } else { // neutral
                    return MoveClassification.NEUTRAL;
                }
            } 
        } else {
            return MoveClassification.NEUTRAL;
        }
        
        logger.error("Could not figure out if move " + move + " helped us. Returning neutral.");
        return MoveClassification.NEUTRAL;
    }

    //Checks to see if other players have initiated co-operation (or moved coins towards our home slot)
    public boolean isInOurArea(Move move){
        //TODO: check origin point as well
        Point newMovePoint = new Point(move.getNewX(), move.getNewY());
        int me = GameEngine.getDistance(myCorner.getHome(), newMovePoint);
        
        if(me>3){
            return false;
        }
        
        int left = GameEngine.getDistance(myCorner.getLeft().getHome(), newMovePoint);
        int right = GameEngine.getDistance(myCorner.getRight().getHome(), newMovePoint);
        int result = 0;
        if(left<=right){
            result = left-me;
        }else{
            result = right-me;
        }
        if(result<0){
            return false;
        }else{
            return true;
        }
    }
    
    //Returns the home slot of the player whose ID is playerID
    public Direction playerIDToDirection(int playerID){
        Direction result = myCorner;
        int tempID = ourID;
        while(tempID!=playerID){
            result = result.getRight();
            tempID++;
            if(tempID>5){
                tempID=0;
            }
        }
        return result;
    }

    public int directionToPlayerID(Direction d){
        Direction result = myCorner;
        int tempID = ourID;
        while(result.equals(d) == false){
            result = result.getRight();
            tempID++;
            if(tempID>5){
                tempID=0;
            }
        }
        return tempID;
    }

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


}
