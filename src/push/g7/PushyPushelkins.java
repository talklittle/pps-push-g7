package push.g7;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class PushyPushelkins extends Player{
	int[][] board;
	ArrayList<Direction> playerPositions;
	RecognizeEnemyAndAlly allyRecognizer;
	int round;
	int totalRounds;
	ScoreZones scoreZones;
	
	// Number of rounds at the end to consider endgame
	private static final int ENDGAME_ROUNDS = 10;
	
	
	private static final Logger logger = Logger.getLogger(PushyPushelkins.class);
	
	@Override
	public void updateBoardState(int[][] board) {
		this.board= board;
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
		
		this.myCorner=playerPositions.get(id);
		this.id=id;
		this.playerPositions = playerPositions;
		
		this.round = 0;
		this.totalRounds = m;
		this.scoreZones = new ScoreZones(playerPositions);
		
		// From the beginning, everyone is your ally until demonstrated otherwise.
		this.allyRecognizer = new RecognizeEnemyAndAlly(myCorner, playerPositions, scoreZones, playerPositions, null);
	}
	
	

	@Override
	public Move makeMove(List<MoveResult> previousMoves) {
		// (first round is Round 1)
		round++;
		
		allyRecognizer.updateAlliances(previousMoves);
//		return generateSimpleMove(0);
		
		// If it is not yet endgame
		if (round <= totalRounds - ENDGAME_ROUNDS) {
			return generateHelpfulMove();
		}
		// If it is endgame
		else {
			return generateBetrayalMove();
		}
	}
	
	public int getDepth(Point point, Direction playerHome)
	{
		int a =((point.x-playerHome.getHome().x)+(point.y-playerHome.getHome().y));
		int depth;
		if(a%2 == 0)
		{
			depth = a / 2;
		}else {
			depth = StaticVariable.MAX_DEPTH+1;
	
		}
		return depth;
	}
	
	
	private Move generateHelpfulMove() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Move generateBetrayalMove() {
		// TODO Auto-generated method stub
		return null;
	}
	
//	public Move generateSimpleMove(int depth)
//	{
//		RecognizeEnemyAndAlly a = new RecognizeEnemyAndAlly(myCorner, scoreZones);
//		Direction enemy = a.getEnemy(myCorner).get(0);
//		Direction ally = a.getAlly(myCorner).get(0);
//		if(depth > 300)
//		{
//			return new Move(0,0,Direction.NE);
//		}
//		int n2 = GameConfig.random.nextInt(9);
//		int length = n2;
//		if(length > 4)
//			length=8-length;
//		int offset = 4-length;
//		length+=5;
//		int n1 = GameConfig.random.nextInt(length);
//		n1*=2;
//		n1 += offset;
//		if(!GameEngine.isInBounds(n1, n2))
//			return generateSimpleMove(depth+1);
//		
//		if(getDistance(new Point(n1,n2), myCorner.getHome())<=2)
//			return generateSimpleMove(depth+1);
//		
//		if(board != null&& board[n2][n1] == 0)
//			return generateSimpleMove(depth+1);
//		Direction d = ally;
////		int tries = 0;
////		while(!GameEngine.isValidDirectionForCellAndHome(d, myCorner) && tries < 10)
////		{
////			d = myCorner.getRelative(-1);
////			
////			tries++;
////		}
//		if(!GameEngine.isValidDirectionForCellAndHome(d, myCorner))
//			return generateSimpleMove(depth+1);
//		
//		if(!GameEngine.isInBounds(n1+d.getDx(), n2+d.getDy()))
//			return generateSimpleMove(depth+1);
//		
//		Move m = new Move(n1, n2,d);
//		return m;
//	}
//	public Point getPushPoint(Direction myCorner, Direction enemy, Direction ally)//delete ally one day;
//	{
//		int depth = 0;
//		while(depth<StaticVariable.MAX_DEPTH)
//		{
//			for (int x = 0; x < StaticVariable.MAX_X; x++)
//					for(int y = 0; y < StaticVariable.MAX_Y; y++)
//				{
//					Point point= new Point(x,y); 
//					System.out.println(depth + point.toString());
//					if(getDepth(point, enemy) == depth && board[x][y] != 0 && GameEngine.isInBounds(x, y))
//					{   System.out.println(depth + point.toString());
//						return point;
//					}
//				}
//			depth ++;
//		}
//		return new Point(8,4);
//	}
//	public Move generateSimpleMove()
//	{
//		RecognizeEnemyAndAlly a = new RecognizeEnemyAndAlly();
//		Direction enemy = a.getEnemy(myCorner).get(0);
//		Direction ally = a.getAlly(myCorner).get(0);
//		Point PushPoint=  getPushPoint(myCorner, enemy, ally);
//		Move m = new Move(PushPoint.x, PushPoint.y, ally);
//		return m;
//	}
	
	public int connected(Point point1, Point point2)
	{
		logger.debug((point1.y - point2.y)/(point1.x-point2.x));
		switch((point1.y - point2.y)/(point1.x-point2.x))
		{
		case 0: return 0;
		case 1: return 1;
		case -1: return -1;
		default: return 2;
		
		}
	}

	private static int getDistance(Point from, Point to)
	{
		int dx = from.x - to.x;
		int dy = from.y - to.y;
		int s = 100;
		if (Math.abs(dx) == Math.abs(dy))
			s = Math.abs(dx);
		else if (Math.abs(dy) == 0)
			s = Math.abs(dx) / 2;
		else if (Math.abs(dx) == 0)
			s = Math.abs(dy);
		else if (Math.abs(dx) == 1)
			s = Math.abs(dy);
		else if (Math.abs(dx) == 2)
			s = Math.abs(dy);
		else if (Math.abs(dx) >= 3 &&  Math.abs(dx) <= 6 && Math.abs(dy) >= 5)
			s = Math.abs(dy);
		else {
			s = (int) Math.ceil((double) (Math.abs(dx) + Math
					.abs(dy)) / 2.0d);
		}
		return s;
		
	}
	
}
