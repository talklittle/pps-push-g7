package push.g5.teams;

import java.util.ArrayList;

public class Team {

	//keep track of the indexes of the players which are in this team
	ArrayList<Integer> teamMembers;

	//tentative : who are the best and the worst players in this team
	Integer bestScoringPlayer;
	Integer worstScoringPlayer;

	Integer averageScore;
	
	Team() {
		teamMembers = new ArrayList<Integer>();
	}
	
	public void addMember(int playerIndex) {
		if(!containsPlayer(playerIndex)) {
			teamMembers.add(playerIndex);
		}
	}
	
	public ArrayList<Integer> getMembers() {
		return teamMembers;
	}
	
	public boolean containsPlayer(Integer index) {
		boolean isPresent = false;
		for ( int i = 0; i < teamMembers.size(); i++) {
			if(teamMembers.get(i) == index) {
				isPresent = true;
			}
		}
		return isPresent;
	}
	
	public boolean containsCommonPlayer(Team otherTeam) {
		boolean containsCommon = false;
		for(int i = 0; i < otherTeam.teamMembers.size(); i++) {
			Integer index = otherTeam.teamMembers.get(i);
			if(this.containsPlayer(index)) {
				containsCommon = true;
			}
		}
		return containsCommon;
	}
	
	public Team merge(Team otherTeam) {
		Team newTeam = new Team();
		newTeam.teamMembers.addAll(this.teamMembers);
		for( int i = 0; i < otherTeam.teamMembers.size(); i++) {
			if(!newTeam.containsPlayer(otherTeam.teamMembers.get(i)))
				newTeam.teamMembers.add(otherTeam.teamMembers.get(i));
		}
		return newTeam;
	}
	
	public Integer getBestPlayer() {
		return bestScoringPlayer;
	}
	
	public Integer getWorstPlayer() {
		return worstScoringPlayer;
	}
	
	public void setBestPlayer(Integer player) {
		bestScoringPlayer = player;
	}
	
	public void setWorstPlayer(Integer player) {
		worstScoringPlayer = player;
	}
	
	public void setAverageScore(Integer score) {
		averageScore = score;
	}
}

















