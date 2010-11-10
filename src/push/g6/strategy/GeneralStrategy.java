package push.g6.strategy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import push.g6.AbstractPlayer;
import push.g6.NicePlayer;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

public class GeneralStrategy extends Strategy {

    private LinkedList<Point> neighbourhood = null;
    private Direction playerToHelp = null;
    private PriorityQueue<PlayerHelper> queue = new PriorityQueue<PlayerHelper>();
    private static ArrayList<Point> helpNorthEast;
	private static ArrayList<Point> helpEast;
	private static ArrayList<Point> helpSouthEast;
	private static ArrayList<Point> helpSouthWest;
	private static ArrayList<Point> helpWest;
	private static ArrayList<Point>	helpNorthWest;

	static{
		helpWest = new ArrayList<Point>();
		helpWest.add(new Point(1,3));
		helpWest.add(new Point(2,4));
		helpWest.add(new Point(1,5));
		helpWest.add(new Point(2,2));
		helpWest.add(new Point(3,3));
		helpWest.add(new Point(4,4));
		helpWest.add(new Point(3,5));
		helpWest.add(new Point(2,6));
		helpWest.add(new Point(4,2));
		helpWest.add(new Point(5,3));
		helpWest.add(new Point(6,4));
		helpWest.add(new Point(5,5));
		helpWest.add(new Point(4,6));
		helpWest.add(new Point(7,3));
		helpWest.add(new Point(8,4));
		helpWest.add(new Point(7,5));
		
		helpSouthWest = new ArrayList<Point>();
		helpSouthWest.add(new Point(3,7));
		helpSouthWest.add(new Point(5,7));
		helpSouthWest.add(new Point(6,8));
		helpSouthWest.add(new Point(2,6));
		helpSouthWest.add(new Point(4,6));
		helpSouthWest.add(new Point(6,6));
		helpSouthWest.add(new Point(7,7));
		helpSouthWest.add(new Point(8,8));
		helpSouthWest.add(new Point(3,5));
		helpSouthWest.add(new Point(5,5));
		helpSouthWest.add(new Point(7,5));
		helpSouthWest.add(new Point(8,6));
		helpSouthWest.add(new Point(9,7));
		helpSouthWest.add(new Point(6,4));
		helpSouthWest.add(new Point(8,4));
		helpSouthWest.add(new Point(9,5));
		
		helpSouthEast = new ArrayList<Point>();
		helpSouthEast.add(new Point(10,8));
		helpSouthEast.add(new Point(11,7));
		helpSouthEast.add(new Point(13,7));
		helpSouthEast.add(new Point(8,8));
		helpSouthEast.add(new Point(9,7));
		helpSouthEast.add(new Point(10,6));
		helpSouthEast.add(new Point(12,6));
		helpSouthEast.add(new Point(14,6));
		helpSouthEast.add(new Point(7,7));
		helpSouthEast.add(new Point(8,6));
		helpSouthEast.add(new Point(9,5));
		helpSouthEast.add(new Point(11,5));
		helpSouthEast.add(new Point(13,5));
		helpSouthEast.add(new Point(7,5));
		helpSouthEast.add(new Point(8,4));
		helpSouthEast.add(new Point(10,4));
		
		helpEast = new ArrayList<Point>();
		helpEast.add(new Point(15,5));
		helpEast.add(new Point(14,4));
		helpEast.add(new Point(15,3));
		helpEast.add(new Point(14,6));
		helpEast.add(new Point(13,5));
		helpEast.add(new Point(12,4));
		helpEast.add(new Point(13,3));
		helpEast.add(new Point(14,2));
		helpEast.add(new Point(12,6));
		helpEast.add(new Point(11,5));
		helpEast.add(new Point(10,4));
		helpEast.add(new Point(11,3));
		helpEast.add(new Point(12,2));
		helpEast.add(new Point(9,5));
		helpEast.add(new Point(8,4));
		helpEast.add(new Point(9,3));
		
		helpNorthEast = new ArrayList<Point>();
		helpNorthEast.add(new Point(10,0));
		helpNorthEast.add(new Point(11,1));
		helpNorthEast.add(new Point(13,1));
		helpNorthEast.add(new Point(8,0));
		helpNorthEast.add(new Point(9,1));
		helpNorthEast.add(new Point(10,2));
		helpNorthEast.add(new Point(12,2));
		helpNorthEast.add(new Point(14,2));
		helpNorthEast.add(new Point(7,1));
		helpNorthEast.add(new Point(8,2));
		helpNorthEast.add(new Point(9,3));
		helpNorthEast.add(new Point(11,3));
		helpNorthEast.add(new Point(13,3));
		helpNorthEast.add(new Point(7,3));
		helpNorthEast.add(new Point(8,4));
		helpNorthEast.add(new Point(10,4));
		
		helpNorthWest = new ArrayList<Point>();
		helpNorthWest.add(new Point(3,1));
		helpNorthWest.add(new Point(5,1));
		helpNorthWest.add(new Point(6,0));
		helpNorthWest.add(new Point(2,2));
		helpNorthWest.add(new Point(4,2));
		helpNorthWest.add(new Point(6,2));
		helpNorthWest.add(new Point(7,1));
		helpNorthWest.add(new Point(8,0));
		helpNorthWest.add(new Point(3,3));
		helpNorthWest.add(new Point(5,3));
		helpNorthWest.add(new Point(7,3));
		helpNorthWest.add(new Point(8,2));
		helpNorthWest.add(new Point(9,1));
		helpNorthWest.add(new Point(6,4));
		helpNorthWest.add(new Point(8,4));
		helpNorthWest.add(new Point(9,3));
	}
	
	private static ArrayList<Point> getHelpPointsForplayer(PlayerHelper player){
		if(player.getDirection().equals(Direction.E)){
			return helpEast;
		}
		if(player.getDirection().equals(Direction.NE)){
			return helpNorthEast;
		}
		if(player.getDirection().equals(Direction.W)){
			return helpWest;
		}
		if(player.getDirection().equals(Direction.NW)){
			return helpNorthWest;
		}
		if(player.getDirection().equals(Direction.SE)){
			return helpSouthEast;
		}
		if(player.getDirection().equals(Direction.SW)){
			return helpSouthWest;
		}
		//Code never gets here
		return null;
	}

    
    
    public GeneralStrategy(NicePlayer p) {
    	this.player = p;

        neighbourhood = new LinkedList<Point>();
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
        
        for (Point po : neighbourhood) {
           // this.player.getLogger().debug(
           //         "Player " + player.getID() + " is close too " + po.getX()
            //                + " " + po.getY());

        }
    }

    public void evaluateMoves(List<MoveResult> previousMoves) {

        
    	if ((previousMoves == null) || previousMoves.isEmpty())
            return;
        LinkedList<Integer> players = new LinkedList<Integer>();
        
        for (MoveResult mr : previousMoves) {
            if (mr.getPlayerId() == this.player.getID())
                continue;

            // Evaluate whether the moves of the other players have helped or
            // not

            if (!GameEngine.isInBounds(mr.getMove().getNewY(), mr.getMove()
                    .getNewX())
                    || !GameEngine.isValidDirectionForCellAndHome(
                            AbstractPlayer.getHomeofID(mr.getPlayerId()), mr
                                    .getMove().getDirection())) {
                continue;
            }

            if ((mr.getMove().getDirection().equals(this.player.getCorner())))
                players.add(mr.getPlayerId());

        }

        if (players.size() == 0)
            this.playerToHelp = null;
        else {
            Random r = new Random();
            int pId = r.nextInt(players.size());
            playerToHelp = this.player.getHomeofID(pId);
        }

    }

    @Override
    public Move getMove(int round, List<MoveResult> previousMoves) {

        if (previousMoves == null || previousMoves.isEmpty()) {
            return this.player.makeNeutralMove();
        }
        this.evaluateMoves(previousMoves);

        if (playerToHelp == null) {
            return this.player.makeNeutralMove();

        } else {

            for (int i = 0; i < this.player.getBoard().length; i++) {
                for (int j = 0; j < this.player.getBoard()[i].length; j++) {
                    Point p = new Point(i, j);
                    if (this.neighbourhood.contains(p)){
                    //    this.player.getLogger().debug("x: "+p.getX()+" y: "+p.getY());
                        continue;
                    }
                    if (!GameEngine.isInBounds(i, j)
                            || !GameEngine.isValidDirectionForCellAndHome(
                                    this.player.getCorner(), this.playerToHelp))
                        continue;
                    if (this.player.getBoard()[j][i] != 0) {

                        Move m = new Move(i, j, playerToHelp);
//                        this.player.getLogger().debug(
//                                "player id:" + this.player.getID() + " x: " + m.getX()
//                                        + " y:" + m.getY() + " newX"+ m.getNewX()+
//                                        "newY "+ m.getNewY()
//                                        +"DirectionTo "+m.getDirection());
                        if(GameEngine.isInBounds(m.getNewX(), m.getNewY()))
                            return m;
                    }

                }
            }
           // this.player.getLogger().debug("Player "+this.player.getID()+" neutral move");

            return this.player.makeNeutralMove();
        }
    //	return makeAHelpingMove();

    }
    
    public Move makeAHelpingMove(){
    	queue.add(new PlayerHelper(this.player.getCorner().getOpposite(),1));
    	PlayerHelper friend=queue.peek();
    	Move moveToReturn;
    	ArrayList<Point> helpPoints=getHelpPointsForplayer(friend);
    	for(Point p:helpPoints){
    		for(int i=-1;i<2;i++){
    			moveToReturn=new Move((int)p.getX(),(int)p.getY(),this.player.getCorner().getRelative(i));
    			//We have a valid move that helps a player
    			MoveResult mr=new MoveResult(moveToReturn, this.player.getID());
    			if(this.player.isValidMove(mr)){
    				if(!this.player.getWhiteSpots().contains(new Point(mr.getMove().getNewX(),mr.getMove().getNewY()))){
    					return moveToReturn; 
    				}
    			}
    			//the move we found was not valid. Check for some other moves.
    			else{
    				moveToReturn=null;
    			}
    		}
    	}
    	return this.player.makeNeutralMove();
    }

	
}
