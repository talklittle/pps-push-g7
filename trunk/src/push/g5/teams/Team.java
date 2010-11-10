package push.g5.teams;

import java.util.ArrayList;

public class Team {

	//keep track of the indexes of the players which are in this team
	ArrayList<Integer> teamMembers;
	
	//tentative : who are the best and the worst players in this team
	//int bestPlayerIndex, worstPlayerIndex;
	
	Team() {
		teamMembers = new ArrayList<Integer>();
	}
	
	public void addMember(int playerIndex) {
		if(!teamMembers.contains(playerIndex)) {
			teamMembers.add(playerIndex);
		}
	}
	
	public ArrayList<Integer> getMembers() {
		return teamMembers;
	}
}
