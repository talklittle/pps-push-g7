package push.g7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;
import push.sim.Player.Direction;

public class PushyPushelkins extends Player{
	int[][] board = new int[StaticVariable.MAX_Y][StaticVariable.MAX_X];
	int[][] previousBoard;

	ArrayList<Direction> playerPositions;
	HashMap<Direction, Integer> directionToID = new HashMap<Direction, Integer>();
	RecognizeEnemyAndAlly allyRecognizer;
	IllegalMoveChecker illegalMoveChecker;
	int round;
	int totalRounds;
	ScoreZones scoreZones;
	SimpleMoveStrategy strategy = new SimpleMoveStrategy();
	int Piles;
	
	
	private static final Logger logger = Logger.getLogger(PushyPushelkins.class);	

	
	@Override
	public void updateBoardState(int[][] board) {
		this.previousBoard = this.board;
		this.board= board;
		this.Piles=0;
		for (int x = 0; x < StaticVariable.MAX_X; x++)
			for (int y = 0; y < StaticVariable.MAX_Y; y++)
			{
					PointProperty p = new PointProperty(x, y,board);
					if (p.status == 1 && p.coins >= 2) 
					{
						this.Piles++;
					}

				}
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "g7.PushyPushelkins";
	}
	Direction myCorner;
	int id;
	@Override
	public void startNewGame(int id, int m,
			ArrayList<Direction> playerPositions) {
		this.round = 0;
		this.myCorner=playerPositions.get(id);
		this.id=id;
		this.playerPositions = playerPositions;
		this.totalRounds = m;
		this.scoreZones = new ScoreZones(playerPositions);
		this.illegalMoveChecker = new IllegalMoveChecker(playerPositions);
		for(int y=0;y<StaticVariable.MAX_Y;y++)
			for(int x=0; x<StaticVariable.MAX_X;x++)
			{
				this.board[y][x]=1;
			}
		this.previousBoard=this.board;
		this.Piles=61;
		
		for (int i = 0; i < playerPositions.size(); i++) {
			directionToID.put(playerPositions.get(i), i);
		}
		
		// From the beginning, everyone is your ally until demonstrated otherwise.
		this.allyRecognizer = new RecognizeEnemyAndAlly(myCorner, playerPositions, directionToID,
				scoreZones, board, playerPositions, null);
	}
	
	

	@Override
	public Move makeMove(List<MoveResult> previousMoves) {
		// (first round is Round 1)
		round++;
		
		allyRecognizer.addIllegalPlayers(illegalMoveChecker.getIllegalPlayerIds(previousBoard, previousMoves));
		allyRecognizer.updateScores(board, playerPositions, directionToID);
		allyRecognizer.updateAlliances(previousMoves);
		
		// If it is endgame
		if(round == totalRounds){
			return strategy.generateBetrayalMove(board,myCorner, round, allyRecognizer);
		}
		else
		{
		if (Piles > 6) {
			return strategy.generateHelpfulMove(board, myCorner, round, allyRecognizer);
		}
		// where piles lesser than 6, try to make allies with 3 players.
		else {
			return strategy.threeAllisMove(board, myCorner, round, allyRecognizer);
		}
		}
	}
}