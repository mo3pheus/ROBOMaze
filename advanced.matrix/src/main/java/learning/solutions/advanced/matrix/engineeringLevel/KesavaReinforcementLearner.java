/**
 * 
 */
package learning.solutions.advanced.matrix.engineeringLevel;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learning.solutions.advanced.matrix.domain.RCell;
import learning.solutions.advanced.matrix.domain.Wall;
import learning.solutions.advanced.matrix.domain.WallBuilder;
import learning.solutions.advanced.matrix.utils.EnvironmentUtils;
import learning.solutions.advanced.matrix.utils.ReinforcementLearnerUtil;

/**
 * @author ktalabattula
 * Reinforcement learner to help train a robo to navigate in a maze and find the destination.
 */
public class KesavaReinforcementLearner {
	private Properties mazeConfig;
	private int frameWidth;
	private int cellWidth;
	private WallBuilder wallBuilder;
	private double learningRate;
	private double decayRate;
	private int minReward;
	private int maxIterations;
	private int maxReward;
	private int normalReward;
	private RCell[][] navGrid;
	private RCell destination;
	private Logger logger;
	private Random random;

	public KesavaReinforcementLearner(Properties mazeConfig) {
        this.mazeConfig = mazeConfig;

        //to build the maze UI
        this.frameWidth = Integer.parseInt(mazeConfig.getProperty(EnvironmentUtils.FRAME_WIDTH_PROPERTY));
        this.cellWidth = Integer.parseInt(mazeConfig.getProperty(EnvironmentUtils.CELL_WIDTH_PROPERTY));
        this.wallBuilder = new WallBuilder(mazeConfig);

        //reinforcement learning parameters
        try {
        	this.learningRate = Double.parseDouble(mazeConfig.getProperty(ReinforcementLearner.RL_ENGINE_PREFIX + ".discountRate"));
            this.decayRate = Double.parseDouble(mazeConfig.getProperty(ReinforcementLearner.RL_ENGINE_PREFIX + ".stepSize"));
            this.minReward = Integer.parseInt(mazeConfig.getProperty(ReinforcementLearner.RL_ENGINE_PREFIX + ".minReward"));
            this.maxIterations = Integer.parseInt(mazeConfig.getProperty(ReinforcementLearner.RL_ENGINE_PREFIX + ".maxIterations"));
            this.maxReward = Integer.parseInt(mazeConfig.getProperty(ReinforcementLearner.RL_ENGINE_PREFIX + ".maxReward"));
            this.normalReward = Integer.parseInt(mazeConfig.getProperty(ReinforcementLearner.RL_ENGINE_PREFIX + ".normalReward"));
        } catch(Exception e) {
        	System.out.println(e.getMessage());
        }

        random = new Random();
        populateGrid();
        configureAdjacency();
        logger = LoggerFactory.getLogger(ReinforcementLearner.class);
    }

	//call this method to change the reinforcement learning values
	public void configureReinforcementLearner(double learningRate, double decayRate) {
		this.learningRate = learningRate;
		this.decayRate = decayRate;
	}

	private void populateGrid() {
        int numCells = frameWidth / cellWidth;
        navGrid = new RCell[numCells][numCells];

        int id = 0;
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                Point point = new Point(i * cellWidth, j * cellWidth);
                navGrid[i][j] = new RCell(point, id++, cellWidth);

                if (intersects(navGrid[i][j])) {
                    navGrid[i][j].setReward(minReward);
                } else {
                    navGrid[i][j].setReward(normalReward);
                }
            }
        }
    }

	private boolean intersects(RCell rCell) {
        boolean intersects = false;

        for (Wall w : wallBuilder.getWalls()) {
            if (w.intersects(rCell.getCenter())) {
                intersects = true;
                break;
            }
        }

        return intersects;
    }
	
	private void configureAdjacency() {
        for (int i = 0; i < navGrid[0].length; i++) {
            for (int j = 0; j < navGrid[0].length; j++) {
                RCell current = navGrid[i][j];
                AdjacencyCalculator adjacencyCalculator = new AdjacencyCalculator(current.getCenter(), mazeConfig);
                adjacencyCalculator.setrGridMap(navGrid);
                current.setAdjacentNodes(adjacencyCalculator.getAdjacentRNodes());
                current.initializeQValues(minReward);
            }
        }
    }
	
	public void train(Point finalPoint) {
		destination = ReinforcementLearnerUtil.findPoint(finalPoint, navGrid);
		ReinforcementLearnerUtil.setDestinationReward(navGrid, destination, maxReward);
        
		for(int i = 0; i < maxIterations; i++) {
			RCell current = ReinforcementLearnerUtil.getRandomSource(navGrid);
//            double explorationFactor = random.nextDouble();
			while (!current.equals(destination)) {
//                int id = random.nextDouble() < explorationFactor ? current.getRandomActionId() : current.getBestActionId();
                int id = current.getRandomActionId();
                RCell next = current.getState(id);
                double maxQ = current.getMaxQvalue();
                double reward = minReward;
                RCell nextCell = null;
                if (next != null) {
                	nextCell = ReinforcementLearnerUtil.findPoint(next.getCenter(), navGrid);
                	maxQ = nextCell.getMaxQvalue();
                	reward = nextCell.getReward();
                } else {
                	System.out.println("Something here");
                }
                double newQValue = (1.0d - learningRate) * current.getQvalueForAction(id) + learningRate * (reward + decayRate * maxQ - current.getQvalueForAction(id));
                current.setQvalueForAction(id, newQValue);
                if (next != null) {
                	current = nextCell;
                }
            }
            
            System.out.println("Reinforcement Learning progress = " + (i + 1) + "%");
		}
	}

	public java.util.List<Point> getShortestPath(Point start) {
        java.util.List<Point> shortestPath = new ArrayList<>();

        RCell startPos = ReinforcementLearnerUtil.findPoint(start, navGrid);
        RCell temp = startPos;
        int   i    = 0;
        while (true) {
            if (!shortestPath.contains(temp)) {
                shortestPath.add(temp.getCenter());
            }

            if (temp.equals(destination)) {
                return shortestPath;
            }

            RCell bestAction = temp.getState(temp.getBestActionId());
            temp = ReinforcementLearnerUtil.findPoint(bestAction.getCenter(), navGrid);
            i++;

            if (i == maxIterations * 10) {
                logger.error(" Could not find path between " + startPos.toString() + " and " + destination.toString());
                return shortestPath;
            }
        }
    }
}
