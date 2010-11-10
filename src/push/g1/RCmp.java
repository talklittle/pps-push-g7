package push.g1;

import java.util.Comparator;

public class RCmp implements Comparator<Relationship>
{
	public int compare(Relationship r1, Relationship r2) {
		if (r1.getCooperationScore() < r2.getCooperationScore())
			return 1;
		else if (r1.getCooperationScore() > r2.getCooperationScore())
			return -1;
		else 
			return 0;
	}
	
}