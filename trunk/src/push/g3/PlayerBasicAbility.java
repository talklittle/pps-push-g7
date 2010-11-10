package push.g3;

import push.sim.Move;
import push.sim.Player.Direction;

public class PlayerBasicAbility {

	//return move that do good to specified friend.
	//if there is no move to do good, return null.
	public static Move doGood(Direction friend, Direction me, int[][] boards){
		int toX=-1;
		int toY=-1;
		int fromX = -1;
		int fromY = -1;
		int counter = 0;
		Move bestMove = null;
		if(friend.equals(Direction.NE)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					toX = 12;
					toY = 0;
				}else if(counter==1){
					toX = 11;
					toY = 1;
				}else if(counter==2){
					toX = 10;
					toY = 0;
				}else if(counter==3){
					toX = 13;
					toY = 1;
				}else if(counter==4){
					toX = 10;
					toY = 2;
				}else if(counter==5){
					toX = 9;
					toY = 1;
				}else if(counter==6){
					toX = 12;
					toY = 2;
				}else if(counter==7){
					toX = 9;
					toY = 3;
				}
				//check if any move to that spot has coins to move.
				for(int i = -1;i<1;i++){
					fromX = getFromX(me.getRelative(i),toX);
					fromY = getFromY(me.getRelative(i),toY);
					if((validCell(fromX,fromY))&&(validCell(toX, toY))){
					    if(boards[fromY][fromX]>0){
					        bestMove = new Move(fromX, fromY, me.getRelative(i));
					        return bestMove;
					    }						
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}else if(friend.equals(Direction.E)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					toX = 16;
					toY = 4;
				}else if(counter==1){
					toX = 14;
					toY = 4;
				}else if(counter==2){
					toX = 15;
					toY = 3;
				}else if(counter==3){
					toX = 15;
					toY = 5;
				}else if(counter==4){
					toX = 12;
					toY = 4;
				}else if(counter==5){
					toX = 13;
					toY = 3;
				}else if(counter==6){
					toX = 13;
					toY = 5;
				}else if(counter==7){
					toX = 10;
					toY = 4;
				}
				//check if any move to that spot has coins to move.
				for(int i = -1;i<1;i++){
					fromX = getFromX(me.getRelative(i),toX);
					fromY = getFromY(me.getRelative(i),toY);
					if((validCell(fromX,fromY))&&(validCell(toX, toY))){
					    if(boards[fromY][fromX]>0){
					        bestMove = new Move(fromX, fromY, me.getRelative(i));
					        return bestMove;
					    }						
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}else if(friend.equals(Direction.SE)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					toX = 12;
					toY = 8;
				}else if(counter==1){
					toX = 11;
					toY = 7;
				}else if(counter==2){
					toX = 13;
					toY = 7;
				}else if(counter==3){
					toX = 10;
					toY = 8;
				}else if(counter==4){
					toX = 10;
					toY = 6;
				}else if(counter==5){
					toX = 12;
					toY = 6;
				}else if(counter==6){
					toX = 9;
					toY = 7;
				}else if(counter==7){
					toX = 9;
					toY = 5;
				}
				//check if any move to that spot has coins to move.
				for(int i = -1;i<1;i++){
					fromX = getFromX(me.getRelative(i),toX);
					fromY = getFromY(me.getRelative(i),toY);
					if(validCell(fromX,fromY) && validCell(toX, toY)){
					    if(boards[fromY][fromX]>0){
					        bestMove = new Move(fromX, fromY, me.getRelative(i));
					        return bestMove;
					    }						
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}else if(friend.equals(Direction.SW)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					toX = 4;
					toY = 8;
				}else if(counter==1){
					toX = 5;
					toY = 7;
				}else if(counter==2){
					toX = 3;
					toY = 7;
				}else if(counter==3){
					toX = 6;
					toY = 8;
				}else if(counter==4){
					toX = 6;
					toY = 6;
				}else if(counter==5){
					toX = 4;
					toY = 6;
				}else if(counter==6){
					toX = 7;
					toY = 7;
				}else if(counter==7){
					toX = 7;
					toY = 5;
				}
				//check if any move to that spot has coins to move.
				for(int i = -1;i<1;i++){
					fromX = getFromX(me.getRelative(i),toX);
					fromY = getFromY(me.getRelative(i),toY);
					if(validCell(fromX,fromY) && validCell(toX, toY)){
					    if(boards[fromY][fromX]>0){
					        bestMove = new Move(fromX, fromY, me.getRelative(i));
					        return bestMove;
					    }						
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}else if(friend.equals(Direction.W)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					toX = 0;
					toY = 4;
				}else if(counter==1){
					toX = 2;
					toY = 4;
				}else if(counter==2){
					toX = 1;
					toY = 3;
				}else if(counter==3){
					toX = 1;
					toY = 5;
				}else if(counter==4){
					toX = 4;
					toY = 4;
				}else if(counter==5){
					toX = 3;
					toY = 3;
				}else if(counter==6){
					toX = 3;
					toY = 5;
				}else if(counter==7){
					toX = 6;
					toY = 4;
				}
				//check if any move to that spot has coins to move.
				for(int i = -1;i<1;i++){
					fromX = getFromX(me.getRelative(i),toX);
					fromY = getFromY(me.getRelative(i),toY);
					if(validCell(fromX,fromY) && validCell(toX, toY)){
					    if(boards[fromY][fromX]>0){
					        bestMove = new Move(fromX, fromY, me.getRelative(i));
					        return bestMove;
					    }						
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}else if(friend.equals(Direction.NW)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					toX = 4;
					toY = 0;
				}else if(counter==1){
					toX = 5;
					toY = 1;
				}else if(counter==2){
					toX = 3;
					toY = 1;
				}else if(counter==3){
					toX = 6;
					toY = 0;
				}else if(counter==4){
					toX = 6;
					toY = 2;
				}else if(counter==5){
					toX = 4;
					toY = 2;
				}else if(counter==6){
					toX = 7;
					toY = 1;
				}else if(counter==7){
					toX = 7;
					toY = 3;
				}
				//check if any move to that spot has coins to move.
				for(int i = -1;i<1;i++){
					fromX = getFromX(me.getRelative(i),toX);
					fromY = getFromY(me.getRelative(i),toY);
					if(validCell(fromX,fromY) && validCell(toX, toY)){
					    if(boards[fromY][fromX]>0){
					        bestMove = new Move(fromX, fromY, me.getRelative(i));
					        return bestMove;
					    }						
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}
		return bestMove;
	}
	
	//similar to DoGood but with opposite direction.
	// return NULL if cannot harm that player
	public static Move harm(Direction enemy, Direction me, int[][] boards){
		int toX=-1;
		int toY=-1;
		int fromX = -1;
		int fromY = -1;
		int counter = 0;
		Move result = null;
		if(enemy.equals(Direction.NE)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					fromX = 12;
					fromY = 0;
				}else if(counter==1){
					fromX = 11;
					fromY = 1;
				}else if(counter==2){
					fromX = 10;
					fromY = 0;
				}else if(counter==3){
					fromX = 13;
					fromY = 1;
				}else if(counter==4){
					fromX = 10;
					fromY = 2;
				}else if(counter==5){
					fromX = 9;
					fromY = 1;
				}else if(counter==6){
					fromX = 12;
					fromY = 2;
				}else if(counter==7){
					fromX = 9;
					fromY = 3;
				}
				//check if any move from that spot has coins to move.
				for(int i = -1;i<1;i++){
					toX = fromX + me.getRelative(i).getDx();
					toY = fromY + me.getRelative(i).getDy();
					if(validCell(fromX,fromY) && validCell(toX, toY)){
					    if(boards[fromY][fromX]>0){
					        result = new Move(fromX, fromY, me.getRelative(i));
					        return result;
					    }						
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}else if(enemy.equals(Direction.E)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					fromX = 16;
					fromY = 4;
				}else if(counter==1){
					fromX = 14;
					fromY = 4;
				}else if(counter==2){
					fromX = 15;
					fromY = 3;
				}else if(counter==3){
					fromX = 15;
					fromY = 5;
				}else if(counter==4){
					fromX = 12;
					fromY = 4;
				}else if(counter==5){
					fromX = 13;
					fromY = 3;
				}else if(counter==6){
					fromX = 13;
					fromY = 5;
				}else if(counter==7){
					fromX = 10;
					fromY = 4;
				}
				//check if any move to that spot has coins to move.
				for(int i = -1;i<1;i++){
					toX = fromX + me.getRelative(i).getDx();
					toY = fromY + me.getRelative(i).getDy();
					if(validCell(fromX,fromY) && validCell(toX, toY)){
					    if(boards[fromY][fromX]>0){
					        result = new Move(fromX, fromY, me.getRelative(i));
					        return result;
					    }
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}else if(enemy.equals(Direction.SE)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					fromX = 12;
					fromY = 8;
				}else if(counter==1){
					fromX = 11;
					fromY = 7;
				}else if(counter==2){
					fromX = 13;
					fromY = 7;
				}else if(counter==3){
					fromX = 10;
					fromY = 8;
				}else if(counter==4){
					fromX = 10;
					fromY = 6;
				}else if(counter==5){
					fromX = 12;
					fromY = 6;
				}else if(counter==6){
					fromX = 9;
					fromY = 7;
				}else if(counter==7){
					fromX = 9;
					fromY = 5;
				}
				//check if any move to that spot has coins to move.
				for(int i = -1;i<1;i++){
					toX = fromX + me.getRelative(i).getDx();
					toY = fromY + me.getRelative(i).getDy();
					if(validCell(fromX,fromY) && validCell(toX, toY)){
					    if(boards[fromY][fromX]>0){
					        result = new Move(fromX, fromY, me.getRelative(i));
					        return result;
					    }
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}else if(enemy.equals(Direction.SW)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					fromX = 4;
					fromY = 8;
				}else if(counter==1){
					fromX = 5;
					fromY = 7;
				}else if(counter==2){
					fromX = 3;
					fromY = 7;
				}else if(counter==3){
					fromX = 6;
					fromY = 8;
				}else if(counter==4){
					fromX = 6;
					fromY = 6;
				}else if(counter==5){
					fromX = 4;
					fromY = 6;
				}else if(counter==6){
					fromX = 7;
					fromY = 7;
				}else if(counter==7){
					fromX = 7;
					fromY = 5;
				}
				//check if any move to that spot has coins to move.
				for(int i = -1;i<1;i++){
					toX = fromX + me.getRelative(i).getDx();
					toY = fromY + me.getRelative(i).getDy();
					if(validCell(fromX,fromY) && validCell(toX, toY)){
					    if(boards[fromY][fromX]>0){
					        result = new Move(fromX, fromY, me.getRelative(i));
					        return result;
					    }
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}else if(enemy.equals(Direction.W)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					fromX = 0;
					fromY = 4;
				}else if(counter==1){
					fromX = 2;
					fromY = 4;
				}else if(counter==2){
					fromX = 1;
					fromY = 3;
				}else if(counter==3){
					fromX = 1;
					fromY = 5;
				}else if(counter==4){
					fromX = 4;
					fromY = 4;
				}else if(counter==5){
					fromX = 3;
					fromY = 3;
				}else if(counter==6){
					fromX = 3;
					fromY = 5;
				}else if(counter==7){
					fromX = 6;
					fromY = 4;
				}
				//check if any move to that spot has coins to move.
				for(int i = -1;i<1;i++){
					toX = fromX + me.getRelative(i).getDx();
					toY = fromY + me.getRelative(i).getDy();
					if(validCell(fromX,fromY) && validCell(toX, toY)){
					    if(boards[fromY][fromX]>0){
					        result = new Move(fromX, fromY, me.getRelative(i));
					        return result;
					    }						
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}else if(enemy.equals(Direction.NW)){
			while(counter<8){
				//order from best spot to okay spot
				if(counter==0){
					fromX = 4;
					fromY = 0;
				}else if(counter==1){
					fromX = 5;
					fromY = 1;
				}else if(counter==2){
					fromX = 3;
					fromY = 1;
				}else if(counter==3){
					fromX = 6;
					fromY = 0;
				}else if(counter==4){
					fromX = 6;
					fromY = 2;
				}else if(counter==5){
					fromX = 4;
					fromY = 2;
				}else if(counter==6){
					fromX = 7;
					fromY = 1;
				}else if(counter==7){
					fromX = 7;
					fromY = 3;
				}
				//check if any move to that spot has coins to move.
				for(int i = -1;i<1;i++){
					toX = fromX + me.getRelative(i).getDx();
					toY = fromY + me.getRelative(i).getDy();
					if(validCell(fromX,fromY) && validCell(toX, toY)){
					    if(boards[fromY][fromX]>0){
					        result = new Move(fromX, fromY, me.getRelative(i));
					        return result;
					    }						
					}
				}
				//fail to find valid move, try another spot.
				counter++;
			}
		}
		return result;
	}
	
	//given point to go and direction, find from point.
	public static int getFromX(Direction d, int toX){
		int fromX=-1;
		if(d.equals(Direction.E)){
			 fromX = toX-2;
		}else if(d.equals(Direction.NE)){
			fromX = toX-1;
		}else if(d.equals(Direction.NW)){
			fromX = toX+1;
		}else if(d.equals(Direction.SE)){
			fromX = toX-1;
		}else if(d.equals(Direction.SW)){
			fromX = toX+1;
		}else if(d.equals(Direction.W)){
			fromX = toX+2;
		}
		return fromX;
	}
	//given point to go and direction, find from point.
	public static int getFromY(Direction d, int toY){
		int fromY=-1;
		if(d.equals(Direction.E)){
			 fromY = toY;
		}else if(d.equals(Direction.NE)){
			fromY = toY+1;
		}else if(d.equals(Direction.NW)){
			fromY = toY+1;
		}else if(d.equals(Direction.SE)){
			fromY = toY-1;
		}else if(d.equals(Direction.SW)){
			fromY = toY-1;
		}else if(d.equals(Direction.W)){
			fromY = toY;
		}
		return fromY;
	}
	//hard coding to check valid cell
	public static boolean validCell(int x, int y){
		if(x==0){
			if(y==4){
				return true;
			}
		}else if(x==1){
			if(y==3){
				return true;
			}else if(y==5){
				return true;
			}
		}else if(x==2){
			if(y==2){
				return true;
			}else if(y==4){
				return true;
			}else if(y==6){
				return true;
			}
		}else if(x==3){
			if(y==1){
				return true;
			}else if(y==3){
				return true;
			}else if(y==5){
				return true;
			}else if(y==7){
				return true;
			}
		}else if(x==4){
			if(y==0){
				return true;
			}else if(y==2){
				return true;
			}else if(y==4){
				return true;
			}else if(y==6){
				return true;
			}else if(y==8){
				return true;
			}
		}else if(x==5){
			if(y==1){
				return true;
			}else if(y==3){
				return true;
			}else if(y==5){
				return true;
			}else if(y==7){
				return true;
			}
		}else if(x==6){
			if(y==0){
				return true;
			}else if(y==2){
				return true;
			}else if(y==4){
				return true;
			}else if(y==6){
				return true;
			}else if(y==8){
				return true;
			}
		}else if(x==7){
			if(y==1){
				return true;
			}else if(y==3){
				return true;
			}else if(y==5){
				return true;
			}else if(y==7){
				return true;
			}
		}else if(x==8){
			if(y==0){
				return true;
			}else if(y==2){
				return true;
			}else if(y==4){
				return true;
			}else if(y==6){
				return true;
			}else if(y==8){
				return true;
			}
		}else if(x==9){
			if(y==1){
				return true;
			}else if(y==3){
				return true;
			}else if(y==5){
				return true;
			}else if(y==7){
				return true;
			}
		}else if(x==10){
			if(y==0){
				return true;
			}else if(y==2){
				return true;
			}else if(y==4){
				return true;
			}else if(y==6){
				return true;
			}else if(y==8){
				return true;
			}
		}else if(x==11){
			if(y==1){
				return true;
			}else if(y==3){
				return true;
			}else if(y==5){
				return true;
			}else if(y==7){
				return true;
			}
		}else if(x==12){
			if(y==0){
				return true;
			}else if(y==2){
				return true;
			}else if(y==4){
				return true;
			}else if(y==6){
				return true;
			}else if(y==8){
				return true;
			}
		}else if(x==13){
			if(y==1){
				return true;
			}else if(y==3){
				return true;
			}else if(y==5){
				return true;
			}else if(y==7){
				return true;
			}
		}else if(x==14){
			if(y==2){
				return true;
			}else if(y==4){
				return true;
			}else if(y==6){
				return true;
			}
		}else if(x==15){
			if(y==3){
				return true;
			}else if(y==5){
				return true;
			}
		}else if(x==16){
			if(y==4){
				return true;
			}
		}
		return false;
	}
}
