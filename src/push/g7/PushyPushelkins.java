package push.g7;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;
import push.sim.Player.Direction;

public class PushyPushelkins extends Player {
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
	int haveCoins;
	int formerScore;
	int afterScore;

	private static final Logger logger = Logger
			.getLogger(PushyPushelkins.class);

	@Override
	public void updateBoardState(int[][] board) {
		this.previousBoard = this.board;
		this.board = board;
		this.Piles = 0;
		this.haveCoins = 0;
		this.formerScore = this.afterScore;
		for (int x = 0; x < StaticVariable.MAX_X; x++)
			for (int y = 0; y < StaticVariable.MAX_Y; y++) {
				if (scoreZones.isPointBelongTo(new Point(x, y), myCorner))
					haveCoins++;
				PointProperty p = new PointProperty(x, y, board);
				if (p.status == 1&&p.coins>=1) {
					this.Piles++;
				}
				if (p.status == 1 && p.home == myCorner) {
					afterScore = p.score * p.coins;
				}

			}

	}

	@Override
	public String getName() {
		return "PushyPushelkins";
	}

	Direction myCorner;
	int id;

	@Override
	public void startNewGame(int id, int m, ArrayList<Direction> playerPositions) {
		this.round = -1;
		this.myCorner = playerPositions.get(id);
		this.id = id;
		this.playerPositions = playerPositions;
		this.totalRounds = m;
		this.scoreZones = new ScoreZones(playerPositions);
		this.illegalMoveChecker = new IllegalMoveChecker(playerPositions);
		for (int y = 0; y < StaticVariable.MAX_Y; y++)
			for (int x = 0; x < StaticVariable.MAX_X; x++) {
				this.board[y][x] = 1;
			}
		this.previousBoard = this.board;
		this.Piles = 61;
		this.formerScore = this.afterScore = 16;

		for (int i = 0; i < playerPositions.size(); i++) {
			directionToID.put(playerPositions.get(i), i);
		}

		// From the beginning, everyone is your ally until demonstrated
		// otherwise.
		this.allyRecognizer = new RecognizeEnemyAndAlly(myCorner,
				playerPositions, directionToID, scoreZones, board,
				playerPositions, null);
	}

	@Override
	public Move makeMove(List<MoveResult> previousMoves) {
		// (first round is Round 1)
		round++;
		logger.info("pile="+Piles);
		allyRecognizer.addIllegalPlayers(illegalMoveChecker
				.getIllegalPlayerIds(previousBoard, previousMoves));
		allyRecognizer.updateScores(board, playerPositions, directionToID);
		allyRecognizer.updateAlliances(previousMoves, previousBoard, board);

		// If it is endgame
		if (round == totalRounds - 1) {
			logger.info("round:"+round);
			logger.info("totalRounds:"+totalRounds);
			Move a = strategy.helpOurselfMove(board, myCorner, round,
					totalRounds, formerScore, afterScore,allyRecognizer);
			if (a != null) {
				return a;
			} else {
				return strategy.generateBetrayalMove(board, myCorner, round,
						allyRecognizer);
			}
		} else {
			if (Piles > 6) {
				return strategy.generateHelpfulMove(board, myCorner, round,
						allyRecognizer);
			}
			// where piles lesser than 6, try to make allies with 3 players.
			else {
				Move m = strategy.helpOurselfMove(board, myCorner, round,
						totalRounds, formerScore, afterScore, allyRecognizer);
				if (m != null) {
					return m;
				} else {
					return strategy.threeAlliesMove(board, myCorner, round);
				}

			}
		}
	}
}