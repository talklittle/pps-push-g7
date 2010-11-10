package push.g4;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

public class TrackPlayer
{
	public ArrayList<MoveResult> moveResults;
	public int id;
	public double diff;
	public Direction playerPosition;
	public int positionScore;
	private static final Logger log=Logger.getLogger(TrackPlayer.class);
	
	public TrackPlayer(int id, Direction playerDirection, int positionScore)
	{
		this.id=id;
		this.playerPosition=playerDirection;
		this.positionScore=positionScore;
		this.moveResults=new ArrayList<MoveResult>();
	}
	
	public double scoreTo(TrackPlayer player, ArrayList<int[][]> oldBoards)
	{
		log.debug("scoreTo: moveResults.size="+moveResults.size());
		double score=0;
		for (int i=0; i<moveResults.size(); i++)
		{
			MoveResult moveResult=moveResults.get(i);
			Move move=moveResult.getMove();
			int gain=StaticPlayer.getGain(move, player.playerPosition, oldBoards.get(i));
			score+=gain;			
		}
		return score/moveResults.size();
	}
	
	public double lastScoreTo(TrackPlayer player, int[][] oldBoard)
	{
		Move move=moveResults.get(moveResults.size()-1).getMove();
		return StaticPlayer.getGain(move, player.playerPosition, oldBoard);
	}
	
	public void addMoveResult(MoveResult moveResult)
	{
		moveResults.add(moveResult);
	}
}
