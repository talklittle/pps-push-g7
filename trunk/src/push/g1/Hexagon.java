package push.g1;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import push.sim.GameController;

public class Hexagon {
	
	public Hexagon(int m, int c, int o, int x, int y){
		multiplier = m;
		numCoins = c;
		owner = o;
		this.x = x;
		this.y = y;

		adjacentHexagons = new ArrayList<Hexagon>();
        log = Logger.getLogger(GameController.class);
	}

	public int getValue(){
		return multiplier * numCoins;
	}
	
	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}

	public int getNumCoins() {
		return numCoins;
	}

	public void setNumCoins(int numCoins) {
		this.numCoins = numCoins;
	}

	public int getOwner() {
		return owner;
	}

	public String toString(){
		return multiplier + ", " + numCoins + ", " + owner + ", x: " + x + ", y: " + y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void addAdjHex(Hexagon adjHex)
	{
		adjacentHexagons.add(adjHex);
	}
	
	public ArrayList<Hexagon> getAdjHexagons()
	{
		return adjacentHexagons;
	}

	public int multiplier;
	public int numCoins;
	public int owner;
	private Logger log;
	private int x;
	private int y;
	
	private ArrayList<Hexagon> adjacentHexagons;


}
