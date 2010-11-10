package push.g3;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
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
    private ScoreChart scoreChart;

    private Direction currentAttemptedFriend;

    private final int MAX_DISTANCE_FROM_HOME = 3;
    private final int UP_ANTE = 1;

    private int numberMovesThusFar;
    private int numberMovesPossible;

    private ArrayList<Direction> players;

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
        PlayerBasicAbility.setUpPlayerPoints();
        this.ourID = id;
        this.myCorner = arrayList.get(id);
        scoreChart = new ScoreChart();
        numberMovesPossible = m;
        numberMovesThusFar = 0;
        players = arrayList;

        currentAttemptedFriend = myCorner.getOpposite();
    }

    @Override
    public void updateBoardState(int[][] board) {
        this.board= board;
    }

    private final double PERCENT_OPENING_STRATEGY = .05;
    private final double PERCENT_ENDING_STRATEGY = .02;

    @Override
    public Move makeMove(List<MoveResult> previousMoves)
    {
        LinkedList<Move> possibleMovesForPlayerWeWantToHelp;

        Move nextMove;
        Direction playerWeWantToHelp;
        int topOfList = 0;
        int nextRank = 1;
        
        playerWeWantToHelp = players.get(0);

        for(MoveResult mr : previousMoves)
            scoreChart.updateScore(playerIDToDirection(mr.getPlayerId()), ScoreChart.getScoreGivenMove(myCorner, mr.getMove()));

        if(numberMovesThusFar < Math.ceil(numberMovesPossible*PERCENT_OPENING_STRATEGY)) { // opening strategy
            possibleMovesForPlayerWeWantToHelp = PlayerBasicAbility.doGood(myCorner.getOpposite(), myCorner, board);
            nextMove = possibleMovesForPlayerWeWantToHelp.get(topOfList);
        } else if((numberMovesPossible - numberMovesThusFar) < Math.ceil(numberMovesPossible*PERCENT_ENDING_STRATEGY)) { // ending strategy
            LinkedList<Direction> players = PlayerBasicAbility.getPlayerRanking(board);
            
            nextMove = null;
            int player = -1;
            while(nextMove == null && player < Direction.values().length -1) { //TODO: try to hurt second player (not first!)
                player++;
                if(players.get(player).equals(myCorner)) // don't hurt ourselves!!
                    continue;
                nextMove = PlayerBasicAbility.doEvil(players.get(player), myCorner, board);               
            }
        } else { // middle strategy
            nextMove = null;
            while (nextMove == null && nextRank < Direction.values().length) {
            	playerWeWantToHelp = scoreChart.playerToHelpGivenRank(myCorner, nextRank);
            	int max2Score = scoreChart.getScoreGivenDirection(playerWeWantToHelp);
            	
            	possibleMovesForPlayerWeWantToHelp = PlayerBasicAbility.doGood(playerWeWantToHelp, myCorner, board);
            	
            	int BestScoreSoFar = 0;
                
                for(Move m : possibleMovesForPlayerWeWantToHelp) {
                    int moveScore = ScoreChart.getScoreGivenMove(playerWeWantToHelp, m);
                    if(moveScore > BestScoreSoFar && moveScore <= max2Score + UP_ANTE) {
                        BestScoreSoFar = moveScore;
                        nextMove = m;
                        
                    }
                }
                
                // if we couldn't find a move within our UP_ANTE parameter, then we should find ANY move which can help
                if(nextMove == null && possibleMovesForPlayerWeWantToHelp.size() > 0) {
                    for(Move m : possibleMovesForPlayerWeWantToHelp) {
                        int moveScore = ScoreChart.getScoreGivenMove(playerWeWantToHelp, m);
                        if(moveScore > BestScoreSoFar) {
                            BestScoreSoFar = moveScore;
                            nextMove = m;
                            
                        }
                    }                    
                }
                
            	nextRank++;
            }
        }

        if(nextMove == null) {
            logger.error(myCorner + " is defaulting to generateRandomMove! Bad!");
            nextMove = generateRandomMove(0);
        }
        
        numberMovesThusFar++;
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
        Point destinationPoint = new Point(move.getNewX(), move.getNewY());
        Point fromPoint = new Point(move.getX(),move.getY());
        int distanceDestination = GameEngine.getDistance(myCorner.getHome(), destinationPoint);
        int distanceFrom = GameEngine.getDistance(myCorner.getHome(), fromPoint);
        if(distanceDestination>MAX_DISTANCE_FROM_HOME){
            return false;
        }

        int leftDistance = GameEngine.getDistance(myCorner.getLeft().getHome(), destinationPoint);
        int rightDistance = GameEngine.getDistance(myCorner.getRight().getHome(), destinationPoint);
        int score = 0;
        if(leftDistance<=rightDistance){
            score = leftDistance-distanceFrom;
        }else{
        	score = rightDistance-distanceFrom;
        }
        if(score<0){
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


    public void translateMoveToScore(Move otherPlayersMove) {
        //TODO: Code function
    }


}
