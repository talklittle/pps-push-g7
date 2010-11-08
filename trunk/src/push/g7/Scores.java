package push.g7;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import push.sim.GameEngine;
import push.sim.Player.Direction;

public class Scores {
	ArrayList<Integer> scores;
	boolean[] validPlayers;
	
	public Scores(boolean[] validPlayers) {
		this.validPlayers = validPlayers;
	}
	
	public void updateScores(int[][] board, List<Direction> playerPositions, Map<Direction, Integer> directionToID) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int i = 0; i < 6; i++)
			ret.add(0);
		for (int j = 0; j < 9; j++) {
			int length = j;
			if (length > 4)
				length = 8 - length;
			int offset = 4 - length;
			length += 5;
			for (int i = 0; i < length; i++) {
				// Cell is (i,j) but indexed (j,i)
				int count = board[j][i * 2 + offset];
				Direction closest = null;
				Direction closest2 = null;
				int closestn = 8;
				int closestn2 = 8;
				Point conv = new Point(i * 2 + offset, j);
				for (Direction d : playerPositions) {
					int s = GameEngine.getDistance(d.getHome(),conv);
					if(s == 100)
						s = -1;
					if (s <= closestn) {
						closest2 = closest;
						closestn2 = closestn;
						closest = d;
						closestn = s;
					} else if (s <= closestn2) {
						closest2 = d;
						closestn2 = s;
					}
				}
				if (closestn != closestn2) {
					// Add the score
					int score = count;
					score = score * (closestn2 - closestn);
					score = score + ret.get(directionToID.get(closest));
					ret.set(directionToID.get(closest), score);
				}
			}
		}
		for (int i = 0; i < 6; i++)
			if(!validPlayers[i])
				ret.set(i, 0);
		scores = ret;
	}
	
	public List<Integer> getScores() {
		return scores;
	}

}
