package learning.solutions.advanced.matrix.domain;

/**
 * Created by sanketkorgaonkar on 6/2/17.
 */
import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by sanket on 5/23/17.
 */
public class RCell {

    public enum Direction {
        NORTH(0), SOUTH(1), WEST(2), EAST(3);
        private final int value;

        private Direction(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private static final double REALLY_LOW_VALUE = -99999.0d;
    private static final int    MAX_TRIES        = 100;

    private Point   center        = null;
    private int     cellWidth     = 0;
    private int     id            = -1;
    private double  qValue        = 0.0d;
    private double  reward        = 0.0d;
    private RCell[] adjacentNodes = new RCell[Direction.values().length];
    private double[] adjacentNodesQValues = new double[Direction.values().length];

    public RCell(Point center, int id, int cellWidth) {
        this.center = center;
        this.id = id;
        this.cellWidth = cellWidth;
    }

    public double getqValue() {
        return qValue;
    }

    public void setqValue(double qValue) {
        this.qValue = qValue;
    }

    public RCell[] getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(RCell[] adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    public Point getCenter() {
        return center;
    }

    public int getId() {
        return id;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public String toString() {
        String cellString = center.toString();
        cellString += " Id = " + id + " qValue = " + qValue + " Reward = " + reward;
        return cellString;
    }

    public RCell getBestAction() {
        RCell best = null;

        double maxQ = RCell.REALLY_LOW_VALUE;
        for (RCell temp : adjacentNodes) {
            if (temp != null && temp.getqValue() > maxQ) {
                best = temp;
                maxQ = best.getqValue();
            }
        }

        return best;
    }
    
    public int getBestActionId() {
        int best = 0;

        double maxQ = RCell.REALLY_LOW_VALUE;
        for (int i = 0; i < adjacentNodes.length; i++) {
            if (adjacentNodesQValues[i] > maxQ) {
                best = i;
                maxQ = adjacentNodesQValues[i];
            }
        }

        return best;
    }

    public RCell getRandomAction() {
        int id    = ThreadLocalRandom.current().nextInt(0, Direction.values().length);
        int tries = 0;
        while (adjacentNodes[id] == null && tries < MAX_TRIES) {
            id = ThreadLocalRandom.current().nextInt(0, Direction.values().length);
            tries++;
        }
        return adjacentNodes[id];
    }
    
    public int getRandomActionId() {
        int id    = ThreadLocalRandom.current().nextInt(0, Direction.values().length);
        int tries = 0;
        while (adjacentNodes[id] == null && tries < MAX_TRIES) {
            id = ThreadLocalRandom.current().nextInt(0, Direction.values().length);
            tries++;
        }
        return id;
    }
    
    public RCell getState(int id) {
    	return adjacentNodes[id];
    }

	public void initializeQValues(double qValue) {
		for(int i = 0; i < adjacentNodes.length; i++) {
			if (adjacentNodes[i] == null) {
				adjacentNodesQValues[i] = RCell.REALLY_LOW_VALUE;
			} else {
				adjacentNodesQValues[i] = qValue;
			}
			
		}
	}
	
	public void setQvalueForAction(int id, double qValue) {
		adjacentNodesQValues[id] = qValue;
	}
	
	public double getQvalueForAction(int id) {
		return adjacentNodesQValues[id];
	}
	
	public double getMaxQvalue() {
        double maxQ = RCell.REALLY_LOW_VALUE;
        for (int i = 0; i < adjacentNodes.length; i++) {
            if (adjacentNodesQValues[i] > maxQ) {
            	maxQ = adjacentNodesQValues[i];
            }
        }

        return maxQ;
	}
}

