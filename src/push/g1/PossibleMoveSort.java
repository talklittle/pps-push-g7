package push.g1;

import java.util.Comparator;

public class PossibleMoveSort implements Comparator<PossibleMove> {


	public int compare(PossibleMove a, PossibleMove b) {
		
		//relationship
		if(a.getRelationship() < b.getRelationship() )
			return -1;
		
		else if(a.getRelationship() > b.getRelationship() == true)
			return 1;
		
		//cooperation
		else if(a.getCooperation() && !b.getCooperation())
			return -1;
		
		else if(!a.getCooperation() && b.getCooperation())
			return 1;
		
		//too many coins
		else if(a.tooManyCoins() && !b.tooManyCoins())
			return -1;
		
		else if(!a.tooManyCoins() && b.tooManyCoins())
			return 1;
		
		//wrong way
		else if(a.moveAway() && !b.moveAway())
			return -1;
		
		else if(!a.moveAway() && b.moveAway())
			return 1;
		
		// Not differentiable
		return 0;
	}

}