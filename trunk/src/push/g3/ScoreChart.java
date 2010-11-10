package push.g3;

import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;


public class ScoreChart {

    private final static int MEMORY_SIZE = 3;
    private final static int LAST_MOVE_WEIGHT = 3;

    private HashMap<Direction, Queue<Integer>> playerScores;

    public static int getScoreGivenMove(Direction d, Move m) {
        Point[] playerArea = PlayerBasicAbility.playerPoints.get(d);
        Point endPoint = new Point(m.getNewX(), m.getNewY());

        for(int i = 0; i < playerArea.length; i++) {
            if(endPoint.equals(playerArea[i]))
                return PlayerBasicAbility.getScoreForPosition(d, endPoint);
        }

        return 0;
    }

    public ScoreChart() {
        playerScores = new HashMap<Direction, Queue<Integer>>();
        playerScores.put(Direction.NW, new LinkedList<Integer>());
        playerScores.put(Direction.NE, new LinkedList<Integer>());
        playerScores.put(Direction.W, new LinkedList<Integer>());
        playerScores.put(Direction.SW, new LinkedList<Integer>());
        playerScores.put(Direction.E, new LinkedList<Integer>());
        playerScores.put(Direction.SE, new LinkedList<Integer>());
    }

    // an average which heavily weights the last move
    private double getAverage(Queue<Integer> q) {
        double average = 0;

        Iterator<Integer> it = q.iterator();
        while(it.hasNext()) {
            int current = it.next();
            average += current;
            if(!it.hasNext()) // last element
                average += current * LAST_MOVE_WEIGHT;
        }

        average /= MEMORY_SIZE + LAST_MOVE_WEIGHT;

        return average;
    }

    private int getSum(Queue<Integer> q) {
        int sum = 0;

        Iterator<Integer> it = q.iterator();
        while(it.hasNext()) {
            int current = it.next();
            sum += current;
            if(!it.hasNext()) // last element
                sum += current * LAST_MOVE_WEIGHT;
        }

        return sum;
    }

    //rank is a value between 1 and 5 (inclusive)
    public Direction playerToHelpGivenRank(Direction ourCorner, int rank) {
        PriorityQueue<DirectionScoreBind> scores = new PriorityQueue<DirectionScoreBind>();
        for (Direction player : Direction.values()) {
            if(player.equals(ourCorner)) // don't look at ourselves
                continue;

            scores.add(new DirectionScoreBind(player, getAverage(playerScores.get(player)), getSum(playerScores.get(player))));
        }

        for(int i = 1; i < rank; i++) {
            scores.poll();
        }

        return scores.peek().getDirection();
    }
    
    public int getScoreGivenDirection(Direction d) {
        return (int) Math.ceil(getAverage(playerScores.get(d)));
    }

    class DirectionScoreBind implements Comparable<DirectionScoreBind> {
        private Direction d;
        private double average;
        private int sum;

        public DirectionScoreBind(Direction d, double score, int sum) {
            this.d = d;
            this.average = score;
        }

        public int compareTo(DirectionScoreBind arg0)
        {
            int comparedAverage = (new Double(average)).compareTo(new Double(arg0.average)) * -1; // *-1 to compensate for PriorityQueue insanity
            if(comparedAverage == 0)
                return (new Integer(sum)).compareTo(new Integer(arg0.sum)) * -1; // *-1 to compensate for PriorityQueue insanity
            else
                return comparedAverage;
        }

        public double getAverage() {
            return average;
        }

        public double getSum() {
            return sum;
        }

        public Direction getDirection() {
            return d;
        }
    }


    public void updateScore(Direction player, int score) {
        Queue<Integer> scoresForThisPlayer = null;

        if (playerScores.get(player)!=null)
            scoresForThisPlayer = playerScores.get(player);

        if (scoresForThisPlayer.size() >= MEMORY_SIZE)
            scoresForThisPlayer.remove();
        scoresForThisPlayer.add(score);

        playerScores.put(player, scoresForThisPlayer);
    }


}
