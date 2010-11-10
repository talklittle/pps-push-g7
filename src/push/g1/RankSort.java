package push.g1;

import java.util.Comparator;

public class RankSort implements Comparator<PossibleMove> {


	public int compare(PossibleMove a, PossibleMove b) {
		
		if(a.getOurPoints() < b.getOurPoints() )
			return -1;
		
		else if(a.getOurPoints() > b.getOurPoints() )
			return 1;
		
		else if(a.getChangeInPoints() < b.getChangeInPoints() )
			return -1;
		
		else if(a.getChangeInPoints() > b.getChangeInPoints() )
			return 1;
		
		
		return 0;
	}

}