package push.g2;
import java.awt.Point;
import java.util.*;
import org.apache.log4j.Logger;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;
import push.sim.Player.Direction;


import org.apache.log4j.Logger;

@SuppressWarnings("unused")
public class Util_endGame {

	public static Logger log = Logger.getLogger("Util_endgame");
	public static HashMap<Integer, Direction>IDtoDirection=new HashMap<Integer, Direction>(); 
	boolean situation_flag;			// Situation_flag = false when neighbors are bankrupt, true when they are wealthy
	public ArrayList <Direction> positions = new ArrayList<Direction>();
	
	public Util_endGame()
	{
		
		
		positions.add(Direction.NW);
		positions.add(Direction.NE);
		positions.add(Direction.E);
		positions.add(Direction.SE);
		positions.add(Direction.SW);
		positions.add(Direction.W);
		IDtoDirection = new HashMap<Integer,Player.Direction>();
		for (int i = 0; i < positions.size(); i++) {
			IDtoDirection.put(i, positions.get(i));
			}
		
	}

	public static Direction getDirectionbyID( int id)
	{
		return IDtoDirection.get(id);
	}
	public void assess_Situation(int board[][], int myID){
		
		
				
		//******** FIND NEIGHBORS****************
		int neighbor_id1= myID-1;
		int neighbor_id2= myID+1;
		
		if(neighbor_id1==-1)		neighbor_id1=5;
		if(neighbor_id1==6)			neighbor_id1=0;
			
		if(neighbor_id2==-1)		neighbor_id2=5;
		if(neighbor_id2==6)			neighbor_id2=0;
		
		//********** CHECK WHAT DO THEY HAVE**************
		/*
		 * If neighbors have > 0 (editable) coins, set situation_flag= true, else false.
		 */
		
		Direction neighbor1dir= getDirectionbyID(neighbor_id1) ;
		Direction neighbor2dir= getDirectionbyID(neighbor_id2);
		
		if(Util.getCurrentScore(neighbor1dir, board)>0 || Util.getCurrentScore(neighbor2dir, board)>0 )
		{
			situation_flag=true;
		}
		else
		{
			situation_flag=false;
		}
	}
	
	public void endGameAction(int board[][], int myID)
	{
		/*
		 * If situation_flag=true, neighbors are wealthy, make alliances with
		 * neighbors of neighbors
		 */
		
		/*
		 * If situation_flag=false, neighbors are bankrupt, sabotage wealthy people
		 */
		HashMap<Direction, Integer> wealth=new HashMap<Direction, Integer>();
		for(Direction d:	positions)
		{
			if(d== getDirectionbyID(myID)){
				//*********** DO NOTHING********
				continue;
			}
			
			wealth.put(d, Util.getCurrentScore(d, board));
		}
		
		// **************** sorting neighbors based on wealth **********
		
		/*
		HashMap<Direction, Integer> map = new LinkedHashMap<Direction, Integer>();
		ArrayList<Direction> wealthMapKeys = new ArrayList<Direction>(wealth.keySet());
		ArrayList<Integer> wealthMapValues = new ArrayList<Integer>(wealth.values());
		TreeSet<Integer> sortedSet = new TreeSet<Integer>(wealthMapValues);
		Object[] sortedArray = sortedSet.toArray();
		int size = sortedArray.length;

		for (int i=0; i<size; i++) {
		   map.put
		      (wealthMapKeys.get(wealthMapValues.indexOf(sortedArray[i])),
		       (Integer)sortedArray[i]);
		}
		*/
		int max=0;
		Direction d_temp;
		ArrayList<Direction> wealthMapKeys = new ArrayList<Direction>(wealth.keySet());
		for(Direction t:	wealthMapKeys)
		{
			if(max<wealth.get(t))
			{
				max=wealth.get(t);
				d_temp=t;
			}
		}
		
		// Player with max wealth (not neighbor) = d_temp (his direction)
		
		
	}
	
	
}
