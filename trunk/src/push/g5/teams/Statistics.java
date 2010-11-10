package push.g5.teams;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import push.g5.analytics.PointMatrix;
import push.g5.Slot;
import push.g5.g5player;
import push.sim.GameEngine;
import push.g5.analytics.HelpRatio;

/** Statistics that are kept track of using this class :
 * 
 *  1) Total score for each player with the current board configuration. 
 *  2) Based on helpRatio, figure out which players are forming teams
 *  3) Also find out what the ranges of the scores for these players are. 
 *  
 *  Usage : 	
 *  
 *  Input : pass in a g5Player instance, since it uses some of the methods from g5.
 *  Output : scoreArray will contain the scores for each player, in the index corresponding to each player.
    Usage : Call the s.calculateScores after the updateBoardState method from g5Player has been called.  
   		Statistics s = new Statistics(<g5Player instance>);
		int[] scoreArray = s.calculateScores(<board that the g5player has>);
 */

public class Statistics {

	int[] totalScores;
	public g5player player;
	
	/** these two flags can be turned off to remove all stdout from this class. 
	 * 	debug gives the results expected from this class. detailedDebug is about how that was calculated.
	 */
	boolean debug = false , detailedDebug = false;
	
	private Logger log = Logger.getLogger(this.getClass());
	static final int NO_PLAYERS = 6;
	static final double HELP_THRESHOLD = 0.4;
	
	public Statistics() {
		totalScores = new int[6]; //for the 6 players
		for(int i = 0; i < NO_PLAYERS; i++) {
			totalScores[i] = 0;
		}
	}
	
	//take the current configuration of the game and return the totalScores for each player
	public int[] calculateScores(int[][] board) {
		
		int i;
		//reset current totalScores
		for(i = 0; i < NO_PLAYERS; i++) {
			totalScores[i] = 0;
		}
		ArrayList<Slot> allSlots = g5player.getAllSlots(); 
		int ownerIndex, coinStackSize, pointsWorth;
		for(i = 0; i < allSlots.size(); i++) {
			Slot currentSlot = allSlots.get(i);
			if(GameEngine.isInBounds(currentSlot.getX(), currentSlot.getY())) {
				ownerIndex = PointMatrix.getOwner(currentSlot);
				if(ownerIndex >= 0 && ownerIndex < 6 ) {
					if(currentSlot.getX() < 17 && currentSlot.getY() < 9) { //board sizes
						coinStackSize = board[currentSlot.getY()][currentSlot.getX()]; //should be 0 if it is invalid or if there are no coins.
						pointsWorth = g5player.getBonusFactor(ownerIndex,currentSlot);
						totalScores[ownerIndex] += pointsWorth * coinStackSize;
						if (detailedDebug)
							log.info("player : " + ownerIndex + " slot: ("
									+ currentSlot.getY() + ", "
									+ currentSlot.getX() + ") pointsWorth :"
									+ pointsWorth + " coinStackSize : "
									+ coinStackSize + " totalScore :"
									+ totalScores[ownerIndex]);
					}
				}
			}
		}
		if(debug) {
			for(i = 0; i < NO_PLAYERS; i++ ) {
				log.info("Statistics : player " + i + " scored " + totalScores[i]);
			}
		}
		return totalScores;
	}
	
	public ArrayList<Team> mergeTeams(ArrayList<Team> teams) {
		
		//merge teams that have overlapping members
		ArrayList<Team> mergedTeams = new ArrayList<Team>();
		for(int i=0; i < teams.size(); i++) {
			for(int j = i+1; j < teams.size(); j++) {
				Team team1 = teams.get(i);
				Team team2 = teams.get(j);
				if(team1.containsCommonPlayer(team2)) {
					Team mergedTeam = team1.merge(team2);
					//remove team i and team j, and add mergedTeam.
					for(int p = 0; p < teams.size(); p++) {
						if(p != i && p != j) {
							mergedTeams.add(teams.get(p));
						}
					}
					mergedTeams.add(mergedTeam);
					teams = mergedTeams; //replacing the list on which we are looping!
				}
			}
		}
		return teams;
	}
	
	public ArrayList<Team> fillMissingMembers(ArrayList<Team> teams) {
		//finally, if there is a player thats not in any team, add the player as a team of his own.
		boolean[] players = new boolean[NO_PLAYERS];
		for(int i = 0; i < teams.size(); i++) {
			for(int j = 0; j < teams.get(i).teamMembers.size(); j++) {
				Integer playerIndex = teams.get(i).teamMembers.get(j);
				players[playerIndex] =  true;
			}
		}
		for(int i = 0; i < players.length; i++) {
			if(players[i] == false) {
				Team newTeam = new Team();
				newTeam.addMember(i);
				teams.add(newTeam);
			}
		}
		return teams;
	}

	public void printTeams(ArrayList<Team> teams) {
		if(debug) {
			for(int i = 0; i < teams.size(); i++) {
				ArrayList<Integer> members = teams.get(i).teamMembers;
				log.info("Stats : Team " + i + "contains members :");
				for(int j = 0; j < members.size(); j++) {
					log.info(members.get(j) + " ");
				}
			}
		}
	}

	//for each team, find best and worst players
	//NOTE : uses the scores that are stored in the player currently. 
	public void updateTeamStatistics(ArrayList<Team> teams) {
		int index = -1;
		for(int i = 0; i < teams.size(); i++) {
			Team currentTeam = teams.get(i);
		    int maxScore = 0; 
		    int maxScoringPlayer = 0;
		    int minScore = 500; //cant be more than this.
		    int minScoringPlayer = 0;
		    int sumScore = 0, average = 0;
		    for(int j = 0; j < currentTeam.teamMembers.size(); j++ ) {
		    	index = currentTeam.teamMembers.get(j);
		    	if( maxScore < totalScores[index] ) {
		    		maxScore = totalScores[index];
		    		maxScoringPlayer = index;
		    	}
		    	if( minScore > totalScores[index]) {
		    		minScore = totalScores[index];
		    		minScoringPlayer = index;
		    	}
		    	sumScore += totalScores[index];
		    }
		    average = sumScore/currentTeam.teamMembers.size();
		    currentTeam.setBestPlayer(maxScoringPlayer);
		    currentTeam.setWorstPlayer(minScoringPlayer);
		    currentTeam.setAverageScore(average);
		    if( debug ) {
		    	log.info("the best player " + maxScoringPlayer + " " + maxScore);
		    	log.info("worst player :" + minScoringPlayer + " " + minScore);
		    	log.info("team average :" + average);
		    }
		}
	}
	
	/** Any player who cooperates with at least one member of the team is in the team
	 *  Also, cooperation has to be > 0.4 */
	//NOTE : use this after calling calculateScores for updated player scores. 
	public ArrayList<Team> getCurrentTeams(HelpRatio[][] helpMatrix) {
		
		ArrayList<Team> teams = new ArrayList<Team>();
		for(int i = 0 ; i < helpMatrix.length; i++ ) {
			for(int j = 0; j < helpMatrix[0].length; j++) {
				boolean foundTeam = false;
				if(helpMatrix[i][j].getHelpRatio() > HELP_THRESHOLD) { //this means there is some cooperation
					//find the team which has either i or j and place both in i that team. If there is nothing, create new.
					for(int k=0; k < teams.size() && (foundTeam == false); k++) {
						if(teams.get(k).containsPlayer(i) || teams.get(k).containsPlayer(j)) {
							teams.get(k).addMember(i);
							teams.get(k).addMember(j);
							foundTeam = true; 
						} 
					}
					if(foundTeam == false) {
						Team newteam = new Team();
						newteam.addMember(i);
						newteam.addMember(j);
						teams.add(newteam);
					}
				}
			}
		}
		if(detailedDebug) printTeams(teams);
		teams = mergeTeams(teams);
		if(detailedDebug) printTeams(teams);
		teams = fillMissingMembers(teams);
		if(debug) printTeams(teams);
		updateTeamStatistics(teams);
		return teams;
	}
}
