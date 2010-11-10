package push.g4;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class G4Player extends Player
{
	int[][] board;
	int[][] oldBoard;
	ArrayList<int[][]> oldBoards;
	TrackPlayer trackPlayer[];
	Direction myCorner;
	int id;
	private ArrayList<Direction> playerPositions;
	private static final Logger log=Logger.getLogger(G4Player.class); 
	
	@Override
	public void updateBoardState(int[][] board)
	{
		if (this.board!=null)
		{
			this.oldBoard=new int[board.length][board[0].length];
			for (int i=0; i<board.length; i++)
				for (int j=0; j<board[0].length; j++)
					oldBoard[i][j]=this.board[i][j];
			oldBoards.add(oldBoard);
		}
		
		this.board=new int[board.length][board[0].length];
		for (int i=0; i<board.length; i++)
			for (int j=0; j<board[0].length; j++)
				this.board[i][j]=board[i][j];
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return "Gambler Player";
	}

	@Override
	public void startNewGame(int id, int m, ArrayList<Direction> playerPositions)
	{
		myCorner=playerPositions.get(id);
		this.id=id;
		this.playerPositions=playerPositions;
		board=null;
		oldBoard=null;
		oldBoards=new ArrayList<int[][]>();
		
		trackPlayer=new TrackPlayer[6];
		
		
		for (int i=0; i<trackPlayer.length; i++)
		{
			int positionPoint;
			if (i==(id+1)%6 || i==(id+5)%6)
				positionPoint=0;
			else if ((i==(id+2)%6 || i==(id+4)%6))
				positionPoint=1;
			else
				positionPoint=2;
			trackPlayer[i]=new TrackPlayer(i, playerPositions.get(i), positionPoint);
		}
	}

	@Override
	public Move makeMove(List<MoveResult> previousMoves)
	{
		log.debug("makeMove(): previousMoves.size="+previousMoves.size());
		for (int i=0; i<previousMoves.size(); i++)
			log.debug(previousMoves.get(i).getMove());
		updateStatus(previousMoves);
		
		Move move=null;
		//if (previousMoves.isEmpty())
		if (oldBoard==null)
		{
			log.debug("first move");
			move=helpOpposite();
		}
		else
		{
			move =generateFriendlyMove(previousMoves);
			if (move==null) // if can't help friends, help self
			{
				log.debug(id+" benefit self");
				ArrayList<TrackMove> tempMoves = benefitFriend(myCorner);
				for (int i=0; i<tempMoves.size(); i++)
					log.debug(id+" benefit self "+tempMoves.get(i).gain+" "+tempMoves.get(i).move);
				if (tempMoves.size()>0)
					move=tempMoves.get(0).move;
			}
			if (move==null) // if can't help self, hurt others.
			{
				log.debug(id+" hurt others");
				move=hurtPlayer();
			}		
		}
		
		Direction d = move.getDirection();
		if (move == null || !GameEngine.isValidDirectionForCellAndHome(d, myCorner) || !GameEngine.isInBounds(move.getX()+d.getDx(), move.getY()+d.getDy())){
			//THIS RETURNS ALL POSSIBLE MOVES
			log.debug("IVALID MOVE EVEN AFTER RANDOM MOVE");
			ArrayList<TrackMove> trackMoves= benefitFriend(myCorner, false); 
			if(trackMoves.size() > 0){
				double rand = Math.random()* ((double) trackMoves.size()-1);
				int index = (int) rand;
				move = trackMoves.get(index).move;	
			}
		}
		return move;
	}
	
	public void updateStatus(List<MoveResult> previousMoves)
	{
		for (int i=0; i<previousMoves.size(); i++)
		{
			MoveResult moveResult=previousMoves.get(i);
			trackPlayer[i].addMoveResult(moveResult);
			log.debug("updateStatus: trackPlayer["+i+"].moveResults.size="+trackPlayer[i].moveResults.size());
//			log.debug("updateStatus: "+trackPlayer[i].id+"["+i+"]"+" scoreTo "+id+" is "+
//				trackPlayer[i].scoreTo(trackPlayer[id], oldBoards));
		}
	}

	public Move helpDiagonal()
	{
		Move move=null;		
		int random2=1-2*GameConfig.random.nextInt(2); //-1 or 1
		Direction friend=myCorner.getRelative(random2);
		Point2D point=new Point2D.Double(friend.getHome().getX()-friend.getDx(), 
			friend.getHome().getY()-friend.getDy());
		move=new Move((int)point.getX(), (int)point.getY(), friend);
		return move;
	}
	
	public Move helpOpposite()
	{
		Direction friend=myCorner.getRelative(0);
		log.debug(friend.getHome());
		Move move=benefitFriend(friend).get(0).move;
		return move;
	}
	
	public Move generateFriendlyMove(List<MoveResult> previousMoves)
	{
		TrackPlayer us=trackPlayer[id];
		ArrayList<TrackPlayer> rankPlayers=new ArrayList<TrackPlayer>();
		
		// sort the players based on diff and position
		for (int i=0; i<trackPlayer.length; i++)
		{
			if (i==id || isNeighbor(i))
				continue;
			TrackPlayer player=trackPlayer[i];
			double scoreToUs=player.scoreTo(us, oldBoards);
			double scoreToPlayer=us.scoreToExceptLast(player, oldBoards);//Our last move, didn't affect the player last move because he didn't have our move yet so it should be considered
			
			//double lastScoreToUs=player.lastScoreTo(us, oldBoard);
			player.diff=(scoreToUs-scoreToPlayer);
			rankPlayers.add(player);
		}
		Collections.sort(rankPlayers, new TrackPlayerComparator());
		
		Move bestMove=null;
		for (int i=0; i<rankPlayers.size(); i++)
		{
			TrackPlayer player=rankPlayers.get(i);
			ArrayList<TrackMove> trackMoves=benefitFriend(player.playerPosition);
			/*
			double gain=player.lastScoreTo(us, oldBoard);
			if (gain<=0)
			break;
			for (int j=0; j<trackMoves.size(); j++)
			{
				log.debug("\t"+id+" can benefit "+player.id+" with "+trackMoves.get(j).gain+" in return to "+gain);
				if (gain>=trackMoves.get(j).gain)
				{
					int randomIndex=j;
					if (trackMoves.get(j).gain<=0)
					{
						if (j==0)
							break;
						else
							bestMove=trackMoves.get(j-1).move;
					}
					else
					{
						if (j!=0)
							randomIndex=j-GameConfig.random.nextInt(2); //50% higher return
						bestMove=trackMoves.get(randomIndex).move;
					}
					
					log.debug(id+" benefit "+player.id+" with "+trackMoves.get(j).gain+" in return to "+gain);
					break;
				}
			}
			
			if (bestMove!=null)
				break;
			*/
			if(trackMoves.size() > 0){
				bestMove = trackMoves.get(0).move;
				log.debug(id+" benefit "+player.id+" with "+trackMoves.get(0).gain);
				break;
			}
		}
				
		return bestMove;
	}
	
	public Move hurtPlayer()
	{
		int leftId=(id+1) % 6;
		int rightId=(id+6) % 6;
		Direction leftNeighbor=playerPositions.get(leftId);
		Direction rightNeighbor=playerPositions.get(rightId);
		// TODO Auto-generated method stub
		Move move=hurtPlayer(leftNeighbor);
		if (move==null)
			move=hurtPlayer(rightNeighbor);
		if (move==null)
			move=generateRandomMove(0);
		return move;
	}
	
	public Move hurtPlayer(Direction friend)
	{
		ArrayList<TrackMove> trackMoves=benefitFriend(friend, false);
		if (trackMoves.size()==0)
			return null;
		return trackMoves.get(trackMoves.size()-1).move;
	}
	
	public double getGain(Move move, Direction direction)
	{
		return StaticPlayer.getGain(move, direction, oldBoard);
	}
	
	public double getFutureGain(Move move, Direction direction)
	{
		return StaticPlayer.getGain(move, direction, board);
	}
	
	public ArrayList<TrackMove> benefitFriend(Direction friend)
	{
		return benefitFriend(friend, true);
	}
	
	public ArrayList<TrackMove> benefitFriend(Direction friend, boolean onlyBenefit)
	{
		return StaticPlayer.benefitFriend(friend, onlyBenefit, myCorner, board);
	}
	
	public boolean isNeighbor(int playerId)
	{
		return (playerId==(id+1)%6 || playerId==(id+5)%6);
	}
	
	public Move generateRandomMove(int depth)
	{
		if (depth>300)
		{
			return new Move(0, 0, Direction.NE);
		}
		int n2=GameConfig.random.nextInt(9);
		int length=n2;
		if (length>4)
			length=8-length;
		int offset=4-length;
		length+=5;
		int n1=GameConfig.random.nextInt(length);
		n1*=2;
		n1+=offset;
		if (!GameEngine.isInBounds(n1, n2))
			return generateRandomMove(depth+1);

		if (board!=null&&board[n2][n1]==0)
			return generateRandomMove(depth+1);
		Direction d=myCorner.getRelative(GameConfig.random.nextInt(3)-1);
		int tries=0;
		while (!GameEngine.isValidDirectionForCellAndHome(d, myCorner)
			&&tries<10)
		{
			d=myCorner.getRelative(GameConfig.random.nextInt(2)-1);

			tries++;
		}
		if (!GameEngine.isValidDirectionForCellAndHome(d, myCorner))
			return generateRandomMove(depth+1);

		if (!GameEngine.isInBounds(n1+d.getDx(), n2+d.getDy()))
			return generateRandomMove(depth+1);

		Move m=new Move(n1, n2, d);
		return m;
	}
}
