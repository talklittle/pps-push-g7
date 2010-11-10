package push.g1;

import java.util.ArrayList;
import java.util.Comparator;

public class PossibleMoveSort implements Comparator {


	public int compare(Object a, Object b) {
		if(((PossibleMove) a).getPointsGained() < ((PossibleMove) b).getPointsGained())
			return 0;
		else
			return 1;
	}

}