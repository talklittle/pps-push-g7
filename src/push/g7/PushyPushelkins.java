package push.g7;

import java.awt.Point;
import java.util.*;
import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class PushyPushelkins extends Player{
	int[][] board;
	
	
	private static final Random random = new Random();
	
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
		myCorner=playerPositions.get(id);
		
		this.id=id;
	}
	
	

	@Override
	public Move makeMove(List<MoveResult> previousMoves) {
		return generateSimpleMove();
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
	
	public Point getPushPoint(Direction myCorner, Direction Enemy)
	{
		int depth = 0;
		
		while(depth<StaticVariable.MAX_DEPTH)
		{
			for (int x = 0; x < StaticVariable.MAX_X; x++)
					for(int y = 0; y < StaticVariable.MAX_Y; y++)
				{
					Point point= new Point(x,y); 
					if(getDepth(point, Enemy) == depth && board[x][y] != 0 && GameEngine.isInBounds(x, y))
					{
						return point;
					}else
					{
						depth++;
					}
				}
		}
		return new Point(8,4);
	}
	public Move generateSimpleMove()
	{
		RecognizeEnemyAndAlly a = new RecognizeEnemyAndAlly();
		Direction enemy = a.getEnemy(myCorner).get(0);
		Direction ally = a.getAlly(myCorner).get(0);
		Point PushPoint=  getPushPoint(myCorner, enemy);
		Move m = new Move(PushPoint.x, PushPoint.y, ally);
		return m;
	}

	
}
