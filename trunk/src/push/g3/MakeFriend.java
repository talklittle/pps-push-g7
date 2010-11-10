package push.g3;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;
import push.sim.Player.Direction;

import org.apache.log4j.Logger;

public class MakeFriend /* extends Player */ {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	ArrayList<Direction> friends;
	Direction possibleBestFriend;
	Direction possibleSecondFriend;
	Direction possibleThirdFriend;
	Direction backupFriend;
	
	int possibleBestFriendId;
	int possibleSecondFriendId;
	int possibleThirdFriendId;
	int backupFriendId;
	boolean firstFriendAccept;
	boolean possibleSecondFriendAccept;
	boolean possibleThirdFriendAccept;
	Direction myCorner;
	ArrayList<Direction> requestFriend;
	int ourID;
	int currentRound = 0;
	final int FIRST_ROUND = 1;
	final int THIRD_ROUND = 3;
	
	Direction finalBestFriend;
	Direction finalSecondFriend;
	
	int finalBestFriendID;
	int finalSecondFriendID;
	
	int towardsBestFriendX;
	int towardsBestFriendY;
	int towardsSecondFriendX;
	int towardsSecondFriendY;
	
		
//	@Override
    public String getName()
    {
        return "MakeFriend";
    }
	
	
//	@Override
    public void startNewGame(int id, int m, ArrayList<Direction> arrayList)
    {
        this.ourID = id;
        this.myCorner = arrayList.get(id);
        friends = new ArrayList<Direction>();
        possibleBestFriend = myCorner.getOpposite();
        log.error("my val " + myCorner.getVal());
        log.error("MakeFriend " + myCorner.getVal() + " possible best friend " + possibleBestFriend.getVal());
        log.error("MakeFriend " + myCorner.getVal() + " our id " + id);
        
		possibleSecondFriend = possibleBestFriend.getLeft();
		log.error("MakeFriend " + myCorner.getVal() + " possible second friend " + possibleSecondFriend.getVal());
		
		possibleThirdFriend = possibleBestFriend.getRight();
		log.error("MakeFriend " + myCorner.getVal() + " possible third friend " + possibleThirdFriend.getVal());
		
		possibleBestFriendId = (ourID + 3)%6;
		possibleSecondFriendId = (ourID+4)%6;
		possibleThirdFriendId = (ourID+2)%6;
		
		
		firstFriendAccept = false;
		possibleSecondFriendAccept = false;
		possibleThirdFriendAccept = false;
		requestFriend = new ArrayList<Direction>();
        
    }
	
    
    /*
     // The following constructor will not be invoked. So, I am commenting it out. 
	public MakeFriend(Direction myC, int ourID){
		this.myCorner = myC;
		possibleBestFriend = myCorner.getOpposite();
		possibleSecondFriend = possibleBestFriend.getLeft();
		possibleThirdFriend = possibleBestFriend.getRight();
		firstFriendAccept = false;
		possibleSecondFriendAccept = false;
		possibleThirdFriendAccept = false;
		requestFriend = new ArrayList<Direction>();
		this.ourID = ourID;
	}
	*/
	
	/*
	public Move initiateFriend(List<MoveResult> previousMoves, Direction friend){

		
		
		
		return doGoodTo(friend);
		
		
		
		
		if (roundNumber == FIRST_ROUND || roundNumber == THIRD_ROUND) return doGoodTo(possibleBestFriend);
		
		if((roundNumber%3)==2){ //Every second round, do good to possibleSecondFriend
			return doGoodTo(possibleSecondFriend);
		}
		if((roundNumber%3)==0){
			return doGoodTo(possibleThirdFriend);
		}else{
			return doGoodTo(possibleBestFriend);
		}
		
		
		
		
		
	} 
	 */
	
	public Move doGoodTo(Direction f){
		
		if (currentRound <= 4) { //doing good for initiating co-operation
			Point p = getMove(f);
			log.error("MakeFriend " + myCorner.getVal() + " doing good to " + f.getVal());
			log.error("MakeFriend " + myCorner.getVal() + " Moving from " + p.x + " , " + p.y);
			
			return new Move(p.x, p.y, f);
		}
		
		else { //doing good to the list of confirmed friends
		
		
			if (f.equals(finalBestFriend)) {
				log.error("MakeFriend " + myCorner.getVal() + " doing good to (final bf)" + finalBestFriend.getVal());
				log.error("MakeFriend " + myCorner.getVal() + " Moving from " + towardsBestFriendX + " , " + towardsBestFriendY);
				return new Move(towardsBestFriendX, towardsBestFriendY, f);
			}
			else {
				log.error("MakeFriend " + myCorner.getVal() + " doing good to (second bf) " + finalSecondFriend.getVal());
				log.error("MakeFriend " + myCorner.getVal() + " Moving from " + towardsSecondFriendX + " , " + towardsSecondFriendY);
				return new Move(towardsSecondFriendX, towardsSecondFriendY, f);
			}
		
		}
	}
	
	//Returns the home slot of the player whose ID is playerID
    public Direction playerIDToDirection(int playerID){
        Direction result = myCorner;
        int tempID = ourID;
        while(tempID!=playerID){
            result = result.getRight();
            tempID++;
            if(tempID>5){
                tempID=0;
            }
        }
        return result;
    }
	
	
	public int directionToPlayerID(Direction d){
		Direction result = myCorner;
		int tempID = ourID;
		while(result.equals(d) == false){
			result = result.getRight();
			tempID++;
			if(tempID>5){
				tempID=0;
			}
		}
		return tempID;
	}
	
	
	
	
	//Checks to see if other players have initiated co-operation (or moved coins towards our home slot)
	public int isInOurArea(Move move){
		Point newMovePoint = new Point(move.getNewX(), move.getNewY());
		int me = GameEngine.getDistance(myCorner.getHome(), newMovePoint);
		if(me>3){
			return 0;
		}
		int left = GameEngine.getDistance(myCorner.getLeft().getHome(), newMovePoint);
		int right = GameEngine.getDistance(myCorner.getRight().getHome(), newMovePoint);
		int result = 0;
		if(left<=right){
			result = left-me;
		}else{
			result = right-me;
		}
		if(result<0){
			return 0;
		}else{
			return result;
		}
	}
	
	
	
	
	
//	@Override
	public Move makeMove(List<MoveResult> previousMoves) {
		currentRound++;
		log.error("MakeFriend " + myCorner.getVal() + " round " + currentRound);
		
			
			
		if (currentRound == 1) return doGoodTo(possibleBestFriend);
		
		else if (currentRound == 2) 	{
			ArrayList<Direction> temp = new ArrayList<Direction>();
			
			for(MoveResult pm: previousMoves){
				
				if(pm.isSuccess() && isInOurArea(pm.getMove())>0 && pm.getPlayerId() != ourID){
					int pmPlayerId = pm.getPlayerId();
					if (pmPlayerId == possibleBestFriendId) { 
						
						for (Direction friend : friends) {
							temp.add(friend);
						}
						friends.clear();
						
						//adding best friend to the top of the list
						friends.add(possibleBestFriend);
						
						for (Direction friend : temp)
							friends.add(friend);
						
						
						
						
					}
					//check if second possible friend or third possible friend has initiated co-op. If so, add them as friends.
					else if (pmPlayerId == possibleSecondFriendId || pmPlayerId == possibleThirdFriendId)
						friends.add(playerIDToDirection(pmPlayerId));
					else { 
						//backup friend is required when we don't have at least two players to co-operate with
						backupFriend = playerIDToDirection(pmPlayerId);
						backupFriendId = pmPlayerId;
					}
				}
			}
			
			//do Good to friends
			if (friends.contains(possibleSecondFriend))
				return doGoodTo(possibleSecondFriend);
			else if (friends.contains(possibleThirdFriend))
				return doGoodTo(possibleThirdFriend);
			else {
				log.error("MakeFriend " + myCorner.getVal() + " doing good to second friend " + possibleSecondFriend.getVal());
				return doGoodTo(possibleSecondFriend);
			}
			
			
			
		}
		
		
		else if (currentRound == 3) {
			
			ArrayList<Direction> temp = new ArrayList<Direction>();
			for(MoveResult pm: previousMoves){
				
				if(pm.isSuccess() && isInOurArea(pm.getMove())>0 && pm.getPlayerId() != ourID){
					int pmPlayerId = pm.getPlayerId();
					if (pmPlayerId == possibleBestFriendId && !friends.contains(possibleBestFriend)) 
					{
							
							for (Direction friend : friends) {
								temp.add(friend);
							}
							friends.clear();
							//add best friend to the top of the list
							friends.add(possibleBestFriend);
							
							for (Direction friend : temp)
								friends.add(friend);
								
								
					}
					else if ((pmPlayerId == possibleSecondFriendId && !friends.contains(possibleSecondFriend)) || (pmPlayerId == possibleThirdFriendId && !friends.contains(possibleThirdFriend)))
						if (friends.size() < 2)
							friends.add(playerIDToDirection(pmPlayerId));
					else { 
						if (friends.size() < 2) {
							backupFriend = playerIDToDirection(pmPlayerId);
						}
						
						
					}
				}
			}
			
			
				if (friends.size() > 0) { //keep co-operating with existing friends to sustain relationship
					log.error("MakeFriend " + myCorner.getVal() + " check: doing good to " + friends.get(0));
					return doGoodTo(friends.get(0));
				}
				else { //initiate co-op with third possible friend
					log.error("MakeFriend " + myCorner.getVal() + " check: doing good to " + possibleThirdFriend);
					return doGoodTo(possibleThirdFriend);
				}
			
		}
		
		
		
		else if (currentRound == 4) {
			
			for(MoveResult pm: previousMoves){
				
				//Give possible best friend another chance - wait for best friend to co-operate
				if(pm.isSuccess() && isInOurArea(pm.getMove())>0 && pm.getPlayerId() != ourID){
					log.error("MakeFriend " + myCorner.getVal() + " " + pm.getPlayerId() + " initiated co-operation");
					int pmPlayerId = pm.getPlayerId();
					if (pmPlayerId == possibleBestFriendId && !friends.contains(possibleBestFriend)) friends.add(possibleBestFriend);
					if (friends.size() < 2 && backupFriend != null) friends.add(backupFriend);
						
						
						
					}
				}
			
			
			if (friends.size() < 1)
				log.error("MakeFriend " + myCorner.getVal() + " No one's cooperating. Adding friends anyway. ");
			
			//if we have no friends yet, add possible best friend and possible second friend
			if (friends.size() < 1) friends.add(possibleBestFriend);
			if (friends.size() < 2) friends.add(possibleSecondFriend);
			
			finalizeFriends(); //finalize two friends; set 'from' slots for moves.
			
			log.error("MakeFriend " + myCorner.getVal() + " check: doing good to " + friends.get(1).getVal());
			
			return doGoodTo(friends.get(1));
			
			
		}
		
		
		
		else /* if (currentRound > 4) */ {
			int indexFriend = currentRound % 2;
			
			//do good to friends alternately
			if (indexFriend == 1) {
				log.error("MakeFriend " + myCorner.getVal() + " check: doing good to bf " + friends.get(0).getVal());
				return doGoodTo(friends.get(0));
			}
			else {
				log.error("MakeFriend " + myCorner.getVal() + " check: doing good to second bf " + friends.get(0).getVal());
				return doGoodTo(friends.get(1));
			}
			
		}
		
		
		
		
		
	}
	
	
	public Point getMove(Direction friend) { //Set 'from' slots for every move
		
		Direction tempFriend = friend;
		int tempFriendVal = tempFriend.getVal();
		
		
		int tempFriendX = 0;
		int tempFriendY = 0;
		
		if (tempFriendVal == 1) {
			if (ourID == 1) {
				tempFriendX = 8;
				tempFriendY = 2;
			}
			
			else if (ourID == 2) {
				tempFriendX = 5;
				tempFriendY = 3;
			
			}
			
			else if (ourID == 3) {
				tempFriendX = 5;
				tempFriendY = 3;
			}
			
			else if (ourID == 4) {
				tempFriendX = 5;
				tempFriendY = 3;
			}
			
			else if (ourID == 5) {
				tempFriendX = 5;
				tempFriendY = 3;
			}
		
			
			
		}
		
		
		else if (tempFriendVal == 0) {
			if (ourID == 0) {
				tempFriendX = 8;
				tempFriendY = 2;
			}
			
			else if (ourID == 2) {
				tempFriendX = 11;
				tempFriendY = 3;
			
			}
			
			else if (ourID == 3) {
				tempFriendX = 11;
				tempFriendY = 3;
			}
			
			else if (ourID == 4) {
				tempFriendX = 8;
				tempFriendY = 2;
			}
			
			else if (ourID == 5) {
				tempFriendX = 8;
				tempFriendY = 2;
			}
		
			
			
		}
		

		else if (tempFriendVal == 5) {
			if (ourID == 0) {
				tempFriendX = 11;
				tempFriendY = 3;
			}
			
			else if (ourID == 1) {
				tempFriendX = 11;
				tempFriendY = 3;
			
			}
			
			else if (ourID == 3) {
				tempFriendX = 11;
				tempFriendY = 5;
			}
			
			else if (ourID == 4) {
				tempFriendX = 11;
				tempFriendY = 5;
			}
			
			else if (ourID == 5) {
				tempFriendX = 11;
				tempFriendY = 3;
			}
		
			
			
		}
		
		
		
		
		
		else if (tempFriendVal == 4) {
			if (ourID == 0) {
				tempFriendX = 11;
				tempFriendY = 5;
			}
			
			else if (ourID == 1) {
				tempFriendX = 11;
				tempFriendY = 5;
			
			}
			
			else if (ourID == 2) {
				tempFriendX = 11;
				tempFriendY = 5;
			}
			
			else if (ourID == 4) {
				tempFriendX = 8;
				tempFriendY = 6;
			}
			
			else if (ourID == 5) {
				tempFriendX = 8;
				tempFriendY = 6;
			}
		
			
			
		}
		
		
		
		
		
		else if (tempFriendVal == 3) {
			if (ourID == 0) {
				tempFriendX = 5;
				tempFriendY = 5;
			}
			
			else if (ourID == 1) {
				tempFriendX = 5;
				tempFriendY = 5;
			
			}
			
			else if (ourID == 2) {
				tempFriendX = 5;
				tempFriendY = 5;
			}
			
			else if (ourID == 3) {
				tempFriendX = 8;
				tempFriendY = 6;
			}
			
			else if (ourID == 5) {
				tempFriendX = 5;
				tempFriendY = 5;
			}
		
			
			
		}
		
		
		else /* if (tempFriendID == 5) */ {
			if (ourID == 0) {
				tempFriendX = 5;
				tempFriendY = 3;
			}
			
			else if (ourID == 1) {
				tempFriendX = 5;
				tempFriendY = 3;
			
			}
			
			else if (ourID == 2) {
				tempFriendX = 5;
				tempFriendY = 3;
			}
			
			else if (ourID == 3) {
				tempFriendX = 5;
				tempFriendY = 5;
			}
			
			else if (ourID == 4) {
				tempFriendX = 5;
				tempFriendY = 5;
			}
		
			
		
		
	}	
		log.error("move will be executed from " + tempFriendX + " , " + tempFriendY);
		return new Point(tempFriendX, tempFriendY);
		
	}
	
	
	
	
	
	public void finalizeFriends()
	{

		for (int i = 0; i < 2; i++) {
			
				log.error ("MakeFriend " + myCorner.getVal() + " friends list size " + friends.size());
				if (friends.get(i) == null) log.error("item in friend list equal to null ");
				Direction friend = friends.get(i);
				Point towardsPoint = getMove(friend);
								
				if (i == 0) {
					finalBestFriend = friends.get(i);
					log.error("MakeFriend " + myCorner.getVal() + " final best friend " + friends.get(0).getVal());
					towardsBestFriendX = towardsPoint.x;
					towardsBestFriendY = towardsPoint.y;
				}
				else {
					finalSecondFriend = friends.get(i);
					log.error("MakeFriend " + myCorner.getVal() + " final second friend " + friends.get(1).getVal());
					towardsSecondFriendX = towardsPoint.x;
					towardsSecondFriendY = towardsPoint.y;
				}
					
				
				
				
				
		}
	}
	
		
	
}
