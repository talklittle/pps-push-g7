package push.g3;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;

import push.sim.GameEngine;
import push.sim.Move;
import push.sim.Player.Direction;

public class PlayerBasicAbility {


    public static HashMap<Direction, Point[]> playerPoints;
    public static HashMap<Direction, Point[]> playerAdjacentPoints;
    public static HashMap<Point, Integer> scores;

    private static final int NUM_POINTS_WORTH_FOUR_X = 1;
    private static final int NUM_POINTS_WORTH_THREE_X = 1;
    private static final int NUM_POINTS_WORTH_TWO_X = 3;
    private static final int NUM_POINTS_WORTH_ONE_X = 3;

    public static int getScoreForPosition(Direction d, Point p) {
        Point[] playerArea = playerPoints.get(d);

        for(int i = 0; i < NUM_POINTS_WORTH_FOUR_X; i++) {
            if(playerArea[i].equals(p))
                return 4;
        }
        for(int i = NUM_POINTS_WORTH_FOUR_X; i < NUM_POINTS_WORTH_FOUR_X + NUM_POINTS_WORTH_THREE_X; i++) {
            if(playerArea[i].equals(p))
                return 3;
        }
        for(int i = NUM_POINTS_WORTH_FOUR_X + NUM_POINTS_WORTH_THREE_X; i < NUM_POINTS_WORTH_FOUR_X + NUM_POINTS_WORTH_THREE_X + NUM_POINTS_WORTH_TWO_X; i++) {
            if(playerArea[i].equals(p))
                return 2;
        }
        for(int i = NUM_POINTS_WORTH_FOUR_X + NUM_POINTS_WORTH_THREE_X + NUM_POINTS_WORTH_TWO_X; i < NUM_POINTS_WORTH_FOUR_X + NUM_POINTS_WORTH_THREE_X + NUM_POINTS_WORTH_TWO_X + NUM_POINTS_WORTH_ONE_X; i++) {
            if(playerArea[i].equals(p))
                return 1;
        }

        return 0;
    }

    public static void setUpPlayerPoints() {
        playerPoints = new HashMap<Direction, Point[]>();
        playerAdjacentPoints = new HashMap<Direction, Point[]>();

        Point[] NW = { new Point(4, 0), new Point(5, 1), new Point(6, 0), new Point(3, 1), new Point(6, 2), new Point(4, 2), new Point(7, 1), new Point(7, 3) };
        playerPoints.put(Direction.NW, NW);

        Point[] NE = { new Point(12, 0), new Point(11, 1), new Point(10, 0), new Point(13, 1), new Point(10, 2), new Point(9, 1), new Point(12, 2), new Point(9, 3) };
        playerPoints.put(Direction.NE, NE);

        Point[] W = { new Point(0, 4), new Point(2, 4), new Point(1, 3), new Point(1, 5), new Point(4, 4), new Point(3, 3), new Point(3, 5), new Point(6, 4) };
        playerPoints.put(Direction.W, W);

        Point[] SW = { new Point(4, 8), new Point(5, 7), new Point(3, 7), new Point(6, 8), new Point(6, 6), new Point(4, 6), new Point(7, 7), new Point(7, 5) };
        playerPoints.put(Direction.SW, SW);

        Point[] E = { new Point(16, 4), new Point(14, 4), new Point(15, 3), new Point(15, 5), new Point(12, 4), new Point(13, 3), new Point(13, 5), new Point(10, 4) };
        playerPoints.put(Direction.E, E);

        Point[] SE = { new Point(12, 8), new Point(11, 7), new Point(13, 7), new Point(10, 8), new Point(10, 6), new Point(12, 6), new Point(9, 7), new Point(9, 5) };
        playerPoints.put(Direction.SE, SE);

        Point[] NWA = { new Point(8, 0), new Point(2, 2), new Point(9, 1), new Point(3, 3), new Point(8, 2), new Point(5, 3), new Point(9, 3), new Point(6, 4), new Point(8, 4) };
        playerAdjacentPoints.put(Direction.NW, NWA);

        Point[] NEA = { new Point(8, 0), new Point(14, 2), new Point(7, 1), new Point(13, 3), new Point(8, 2), new Point(11, 3), new Point(7, 3), new Point(10, 4), new Point(8, 4) };
        playerAdjacentPoints.put(Direction.NE, NEA);

        Point[] WA = { new Point(2, 2), new Point(2, 6), new Point(4, 2), new Point(4, 6), new Point(5, 3), new Point(5, 5), new Point(7, 3), new Point(7, 5), new Point(8, 4) };
        playerAdjacentPoints.put(Direction.W, WA);

        Point[] SWA = { new Point(2, 6), new Point(8, 8), new Point(3, 5), new Point(9, 7), new Point(5, 5), new Point(8, 6), new Point(6, 4), new Point(9, 5), new Point(8, 4) };
        playerAdjacentPoints.put(Direction.SW, SWA);

        Point[] EA = { new Point(14, 2), new Point(14, 6), new Point(12, 2), new Point(12, 6), new Point(11, 3), new Point(11, 5), new Point(9, 3), new Point(9, 5), new Point(8, 4) };
        playerAdjacentPoints.put(Direction.E, EA);

        Point[] SEA = { new Point(14, 6), new Point(8, 8), new Point(13, 5), new Point(7, 7), new Point(11, 5), new Point(8, 6), new Point(10, 4), new Point(7, 5), new Point(8, 4) };
        playerAdjacentPoints.put(Direction.SE, SEA);

    }

    //all direction...sorted from best to worse
    //use score of each cell to sort
    public static LinkedList<Move> getPossibleGoodMoves(Direction d,int[][] boards){
        Point[] points = playerPoints.get(d);
        int fromX;
        int fromY;
        LinkedList<Move> possibleMoves = new LinkedList<Move>();
        Direction direction;
        for(int i = 0;i<points.length;i++){
        	direction = d;
            for(int j = 0;j<playerPoints.size();j++){
                fromX = getFromX(direction,points[i].x);
                fromY = getFromY(direction,points[i].y);
                if(validCell(fromX,fromY)&&boards[fromY][fromX]>0&&getScore(fromX, fromY, points[i].x, points[i].y, d)>0){
                    possibleMoves.add(new Move(fromX, fromY, direction));
                }
                direction = direction.getLeft();
            }
        }
        //sort
        if(!possibleMoves.isEmpty()){
            int maxMoveIndex;
            Move maxMove;
            Move temp;
            for(int i = 0;i<possibleMoves.size();i++){
                maxMoveIndex = i;
                maxMove = possibleMoves.get(maxMoveIndex);
                for(int j = i+1;j<possibleMoves.size();j++){
                    if(getScore(maxMove.getX(), maxMove.getY(), maxMove.getNewX(),maxMove.getNewY(),d) <
                    		getScore(possibleMoves.get(j).getX(), possibleMoves.get(j).getY(), possibleMoves.get(j).getNewX(), possibleMoves.get(j).getNewY(),d)){
                        maxMoveIndex = j;
                        maxMove = possibleMoves.get(j);
                    }
                }

                temp = possibleMoves.get(maxMoveIndex);
                possibleMoves.set(maxMoveIndex, possibleMoves.get(i));
                possibleMoves.set(i, temp);
            }
        }

        return possibleMoves;
    }

    public static int getScore(int fromX, int fromY, int toX, int toY, Direction d){
        Point from = new Point(fromX, fromY);
        Point to = new Point(toX, toY);
        return getScoreForPosition(d, to) - getScoreForPosition(d, from);
    }

    public static LinkedList<Direction> getPlayerRanking(int[][] board){
        //NE > E > SE > SW > W > NW
        LinkedList<Direction> rank = new LinkedList<Direction>();
        rank.add(Direction.NE);
        rank.add(Direction.E);
        rank.add(Direction.SE);
        rank.add(Direction.SW);
        rank.add(Direction.W);
        rank.add(Direction.NW);
        int[] scores = new int[6];
        Direction direction = Direction.NE;
        Point[] points;
        //calculate scores for each corner
        for(int i = 0;i<scores.length;i++){
            scores[i] = 0;
            points = playerPoints.get(direction);
            for(int j = 0;j<points.length;j++){
                scores[i] += getScoreForPosition(direction, points[j])*board[points[j].y][points[j].x];
            }
            direction = direction.getRight();
        }
        //sort
        int maxIndex, temp;
        Direction temp2;
        for(int i = 0;i<scores.length;i++){
            maxIndex = i;
            for(int j = i+1;j<scores.length;j++){
                if(scores[maxIndex]<scores[j]){
                    maxIndex = j;
                }
            }
            temp = scores[maxIndex];
            scores[maxIndex] = scores[i];
            scores[i] = temp;
            
            temp2 = rank.get(maxIndex);
            rank.set(maxIndex, rank.get(i));
            rank.set(i, temp2);
        }
        return rank;
    }

    //return move that do good to specified friend. sorted from best to worse
    //if there is no move to do good, return empty LinkedList.
    public static LinkedList<Move> doGood(Direction friend, Direction me, int[][] boards){
        LinkedList<Move> possibleMoves = new LinkedList<Move>();
        LinkedList<Move> allPossibleMove;
        allPossibleMove = getPossibleGoodMoves(friend,boards);
        if(allPossibleMove.isEmpty()){
            return allPossibleMove;
        }
        Move temp;
        for(int i = 0;i<allPossibleMove.size();i++){
            temp = allPossibleMove.get(i);
            if(temp.getDirection().equals(me.getRelative(-1))||
            		temp.getDirection().equals(me.getRelative(0))||
            		temp.getDirection().equals(me.getRelative(1))){
            	possibleMoves.add(temp);
            }
        }
        return possibleMoves;
    }

    //get every possible bad moves for that enemy and from most harmful
    public static LinkedList<Move> getPossibleBadMoves(Direction enemy, int[][] boards){
        Point[] points = playerPoints.get(enemy);
        int toX;
        int toY;
        LinkedList<Move> possibleMoves = new LinkedList<Move>();
        Direction direction = enemy;
        for(int i = 0;i<points.length;i++){
            for(int j = 0;j<playerPoints.size();j++){
                toX = points[i].x+ direction.getDx();
                toY = points[i].y + direction.getDy();
                if(validCell(toX,toY)&&boards[points[i].y][points[i].x]>0&&getScore(points[i].x, points[i].y,toX,toY, enemy)<0){
                    possibleMoves.add(new Move(points[i].x, points[i].y, direction));
                }
                direction = direction.getLeft();
            }
        }
        //sort
        if(!possibleMoves.isEmpty()){
            int minMoveIndex;
            Move minMove;
            Move temp;
            for(int i = 0;i<possibleMoves.size();i++){
                minMoveIndex = i;
                minMove = possibleMoves.get(i);
                for(int j = i+1;j<possibleMoves.size();j++){
                    if(getScore(minMove.getX(), minMove.getY(), minMove.getNewX(),minMove.getNewY(),enemy) >
                    getScore(possibleMoves.get(j).getX(), possibleMoves.get(j).getY(), possibleMoves.get(j).getNewX(), possibleMoves.get(j).getNewY(),enemy)){
                        minMoveIndex = j;
                        minMove = possibleMoves.get(i);
                    }
                }
                temp = possibleMoves.get(minMoveIndex);
                possibleMoves.set(minMoveIndex, possibleMoves.get(i));
                possibleMoves.set(i, temp);	
            }
        }
        return possibleMoves;
    }

    //return the most harmful move to that person
    // return NULL if cannot harm that player
    public static Move doEvil(Direction enemy, Direction me, int[][] boards){
        LinkedList<Move> allPossibleMove;
        allPossibleMove = getPossibleBadMoves(enemy,boards);
        if(allPossibleMove.isEmpty()){
            return null;
        }
        Move temp;
        for(int i = 0;i<allPossibleMove.size();i++){
            temp = allPossibleMove.get(i);
            if(temp.getDirection().equals(me.getRelative(-1))||
            		temp.getDirection().equals(me.getRelative(0))||
            		temp.getDirection().equals(me.getRelative(1))){
            	return temp;
            }
        }
        return null;
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
