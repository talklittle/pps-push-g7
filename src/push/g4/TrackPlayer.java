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
	
	
	public double scoreTo(TrackPlayer player, ArrayList<int[][]> oldBoards, boolean exceptLastMove)
	{
		log.debug("scoreTo: moveResults.size="+moveResults.size());
		double score=0;
		double weight = 1;
		int last = moveResults.size()-1;
		if(exceptLastMove){ //When computing how much we helped another player, we need to ignore the last move
			last--;
			weight = StaticPlayer.alpha;
		}
		for (int i= last; i>= 0; i--)
		{
			MoveResult moveResult=moveResults.get(i);
			Move move=moveResult.getMove();

			ArrayList<TrackMove> potentialMoves = StaticPlayer.benefitFriend(player.playerPosition, true, playerPosition, oldBoards.get(i));
			double percentGain = 1;
			if(potentialMoves.size() != 0 && potentialMoves.get(0).gain > 0){
				double gain=StaticPlayer.getGain(move, player.playerPosition, oldBoards.get(i));
				percentGain = gain / potentialMoves.get(0).gain; //actualGain / bestPossibleGain
			}
			score += weight * percentGain;
			weight *= StaticPlayer.alpha;//it will be 1 for the last move, alpha^1 second-last move, alpha^2 third-last move...)
			if(weight < 0.001) break;//just to speed-up and avoid useless computation
		}
		return score;
	}
	
	public void addMoveResult(MoveResult moveResult)
	{
		moveResults.add(moveResult);
	}
	
	public double scoreTo(TrackPlayer player, ArrayList<int[][]> oldBoards){
		return scoreTo( player, oldBoards, false);
	}
	
	public double scoreToExceptLast(TrackPlayer player, ArrayList<int[][]> oldBoards){
		return scoreTo( player, oldBoards, true);
	}

}
