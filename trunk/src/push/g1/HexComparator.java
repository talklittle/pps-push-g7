package push.g1;

import java.util.Comparator;

public class HexComparator implements Comparator<Hexagon>
{

	public int compare(Hexagon firstHex, Hexagon secondHex) {
		if (firstHex.getMultiplier() < secondHex.getMultiplier())
			return 1;
		else if (firstHex.getMultiplier() > secondHex.getMultiplier())
			return -1;
		return 0;
	}

	
}