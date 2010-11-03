package push.g7;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import push.sim.GameEngine;
import push.sim.Player.Direction;

public class ScoreZones {

	private int[][] board;
	private ArrayList<Direction> positions;
	// Direction val to list of Points in that Direction's score zone
	private HashMap< Integer, ArrayList<Point> > valToScoreZones = new HashMap< Integer, ArrayList<Point> >();
	// Store the score multiplier for each Point
	private HashMap<Point, Integer> pointToMultiplier = new HashMap<Point, Integer>();
	
	public ScoreZones(ArrayList<Direction> positions) {
		
		this.board = board;
		this.positions = positions;
		
		for (int j = 0; j < 9; j++) {
			int length = j;
			if (length > 4)
				length = 8 - length;
			int offset = 4 - length;
			length += 5;
			for (int i = 0; i < length; i++) {
				// Cell is (i,j) but indexed (j,i)
//				int count = board.getCell(j, i * 2 + offset);
				Direction closest = null;
				Direction closest2 = null;
				int closestn = 8;
				int closestn2 = 8;
				Point conv = new Point(i * 2 + offset, j);
				for (Direction d : positions) {
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
					// Keep track of the score multiplier
					pointToMultiplier.put(conv, closestn2 - closestn);
					// Append to score zones list for the owner
					ArrayList<Point> scoreZonesList = valToScoreZones.get(closest.getVal());
					if (scoreZonesList == null) {
						scoreZonesList = new ArrayList<Point>();
						valToScoreZones.put(closest.getVal(), scoreZonesList);
					}
					scoreZonesList.add(conv);
				} else {
					pointToMultiplier.put(conv, 0);
				}
			}
		}
	}
	
	public HashMap<Integer, ArrayList<Point>> getValToScoreZones() {
		return valToScoreZones;
	}
	
	public boolean isPointBelongTo(Point point, Direction direction) {
		return valToScoreZones.get(direction.getVal()).contains(point);
	}
	
	public int getMultiplier(Point point) {
		return pointToMultiplier.get(point);
	}
}
