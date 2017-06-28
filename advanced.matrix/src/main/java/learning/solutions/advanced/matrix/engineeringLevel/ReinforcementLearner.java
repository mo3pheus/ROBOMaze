package learning.solutions.advanced.matrix.engineeringLevel;

/**
 * Created by sanketkorgaonkar on 6/2/17.
 */

import learning.solutions.advanced.matrix.domain.RCell;
import learning.solutions.advanced.matrix.domain.Wall;
import learning.solutions.advanced.matrix.domain.WallBuilder;
import learning.solutions.advanced.matrix.utils.EnvironmentUtils;
import learning.solutions.advanced.matrix.utils.ReinforcementLearnerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by sanket on 5/23/17.
 */
public class ReinforcementLearner {
    public static final String RL_ENGINE_PREFIX = "mars.rover.rlEngine";

    private Logger logger = LoggerFactory.getLogger(ReinforcementLearner.class);

    private Properties mazeDefinition = null;
    private RCell[][]  navGrid        = null;

    private int lidarUsage       = 0;
    private int frameWidth       = 0;
    private int cellWidth        = 0;
    private int explorationSteps = 0;

    private WallBuilder wallBuilder = null;
    private RCell       source      = null;
    private RCell       destination = null;
    private RCell       altDest     = null;

    private double gamma         = 0.0d;
    private double alpha         = 0.0d;
    private int    minReward     = 0;
    private int    maxReward     = 0;
    private int    normalReward  = 0;
    private int    maxIterations = 0;

    public ReinforcementLearner(Properties mazeDefinition) {
        this.mazeDefinition = mazeDefinition;

        this.frameWidth = Integer.parseInt(mazeDefinition.getProperty(EnvironmentUtils.FRAME_WIDTH_PROPERTY));
        this.cellWidth = Integer.parseInt(mazeDefinition.getProperty(EnvironmentUtils.CELL_WIDTH_PROPERTY));
        this.wallBuilder = new WallBuilder(mazeDefinition);

        this.alpha = Double.parseDouble(mazeDefinition.getProperty(RL_ENGINE_PREFIX + ".discountRate"));
        this.gamma = Double.parseDouble(mazeDefinition.getProperty(RL_ENGINE_PREFIX + ".stepSize"));
        this.minReward = Integer.parseInt(mazeDefinition.getProperty(RL_ENGINE_PREFIX + ".minReward"));
        this.maxIterations = Integer.parseInt(mazeDefinition.getProperty(RL_ENGINE_PREFIX + ".maxIterations"));
        this.maxReward = Integer.parseInt(mazeDefinition.getProperty(RL_ENGINE_PREFIX + ".maxReward"));
        this.normalReward = Integer.parseInt(mazeDefinition.getProperty(RL_ENGINE_PREFIX + ".normalReward"));

        populateGrid();
        configureAdjacency();
    }

    public WallBuilder getWallBuilder() {
        return wallBuilder;
    }

    public void setWallBuilder(WallBuilder wallBuilder) {
        this.wallBuilder = wallBuilder;
    }

    public int getLidarUsage() {
        return lidarUsage;
    }

    public void train(Point source, Point dest) {
        logger.info(" rlEngine training triggered at SCET = " + System.currentTimeMillis() + " source = " + source
                .toString() + " destination = " + dest.toString());
        this.destination = ReinforcementLearnerUtil.findPoint(dest, navGrid);
        this.source = ReinforcementLearnerUtil.findPoint(source, navGrid);

        destination.setReward(maxReward);
        destination.setqValue(minReward);

        ReinforcementLearnerUtil.setDestinationReward(navGrid, destination, maxReward);
        int altDestX = Integer.parseInt(mazeDefinition.getProperty("maze.environment.alternate.destination").split("," +
                "")[0]);
        int altDestY = Integer.parseInt(mazeDefinition.getProperty("maze.environment.alternate.destination").split("," +
                "")[1]);
        altDest = ReinforcementLearnerUtil.findPoint(new Point(altDestX, altDestY), navGrid);
        ReinforcementLearnerUtil.setDestinationReward(navGrid, altDest, maxReward);

        for (int i = 0; i < maxIterations; i++) {
            RCell current = ReinforcementLearnerUtil.getRandomSource(navGrid);
            //episode
            //while (!current.equals(destination) || !current.equals(altDestRCell)) {
            while (!current.equals(destination) || !current.equals(altDest)) {
                RCell  temp      = current.getRandomAction();
                RCell  next      = ReinforcementLearnerUtil.findPoint(temp.getCenter(), navGrid);
                double maxQ      = next.getBestAction().getqValue();
                double newQValue = (1.0d - alpha) * current.getqValue() + alpha * (next.getReward() + gamma * maxQ);
                temp.setqValue(newQValue);
                current = next;
                explorationSteps++;
            }
            System.out.println("Reinforcement Learning progress = " + (i + 1) + "%");
        }
        logger.info(" rlEngine logging totalExplorationSteps = " + explorationSteps);
    }

    @Deprecated
    public void train(Point source, Point dest, java.util.List<Point> captureExploration) {
        logger.info(" rlEngine training triggered at SCET = " + System.currentTimeMillis() + " source = " + source
                .toString() + " destination = " + dest.toString());
        this.destination = ReinforcementLearnerUtil.findPoint(dest, navGrid);
        this.source = ReinforcementLearnerUtil.findPoint(source, navGrid);

        destination.setReward(maxReward);
        destination.setqValue(maxReward);

        ReinforcementLearnerUtil.setDestinationReward(navGrid, destination, maxReward);
        int altDestX = Integer.parseInt(mazeDefinition.getProperty("maze.environment.alternate.destination").split("," +
                "")[0]);
        int altDestY = Integer.parseInt(mazeDefinition.getProperty("maze.environment.alternate.destination").split("," +
                "")[1]);
        altDest = ReinforcementLearnerUtil.findPoint(new Point(altDestX, altDestY), navGrid);
        ReinforcementLearnerUtil.setDestinationReward(navGrid, altDest, maxReward);

        for (int i = 0; i < maxIterations; i++) {
            RCell current = ReinforcementLearnerUtil.getRandomSource(navGrid);
            //episode
            while (!current.equals(destination) && !current.equals(altDest)) {
                RCell  temp = current.getRandomAction();
                RCell  next = ReinforcementLearnerUtil.findPoint(temp.getCenter(), navGrid);
                double maxQ = next.getBestAction().getqValue();
                double newQValue = (1.0d - alpha) * current.getqValue() + alpha * (next.getReward() + gamma * maxQ -
                        current.getqValue());
                temp.setqValue(newQValue);

                captureExploration.add(next.getCenter());
                explorationSteps++;
                current = next;
            }
            System.out.println("Reinforcement Learning progress = " + (i + 1) + "%");
        }
        logger.info(" rlEngine logging totalExplorationSteps = " + explorationSteps);
    }

    public java.util.List<Point> getShortestPath() {
        java.util.List<Point> shortestPath = new ArrayList<>();

        RCell temp = source;
        int   i    = 0;
        while (true) {
            if (!shortestPath.contains(temp)) {
                shortestPath.add(temp.getCenter());
            }

            if (temp.equals(destination) || temp.equals(altDest)) {
                return shortestPath;
            }

            RCell bestAction = temp.getBestAction();
            temp = ReinforcementLearnerUtil.findPoint(bestAction.getCenter(), navGrid);
            i++;

            if (i == maxIterations * 10) {
                logger.error(" Could not find path between " + source.toString() + " and " + destination.toString());
                return shortestPath;
            }
            System.out.println("i = " + i);
        }
    }

    public void configureLearner(double discount, double stepSize) {
        this.gamma = discount;
        this.alpha = stepSize;
    }

    public java.util.List<Point> getPath() {
        return null;
    }

    public RCell[][] getNavGrid() {
        return navGrid;
    }

    public double getGamma() {
        return gamma;
    }

    public double getAlpha() {
        return alpha;
    }

    public int getMinReward() {
        return minReward;
    }

    public int getMaxReward() {
        return maxReward;
    }

    public int getNormalReward() {
        return normalReward;
    }

    public int getMaxIterations() {
        return maxIterations;
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

    private void configureAdjacency() {
        for (int i = 0; i < navGrid[0].length; i++) {
            for (int j = 0; j < navGrid[0].length; j++) {
                RCell               current             = navGrid[i][j];
                AdjacencyCalculator adjacencyCalculator = new AdjacencyCalculator(current.getCenter(), mazeDefinition);
                adjacencyCalculator.setrGridMap(navGrid);
                current.setAdjacentNodes(adjacencyCalculator.getAdjacentRNodes());
            }
        }
    }

    private boolean intersects(RCell rCell) {
        boolean intersects = false;

        for (Wall w : wallBuilder.getWalls()) {
            lidarUsage++;
            if (w.intersects(rCell.getCenter())) {
                intersects = true;
                break;
            }
        }

        return intersects;
    }

    private RCell getRandomNextNode(final RCell current) {
        RCell next = null;
        while (true) {
            int randomChoice = ThreadLocalRandom.current().nextInt(0, 4);
            switch (randomChoice) {
                case 0: {
                    next = ReinforcementLearnerUtil.getLeft(current, navGrid, cellWidth);
                }
                break;
                case 1: {
                    next = ReinforcementLearnerUtil.getRight(current, navGrid, cellWidth, frameWidth);
                }
                break;
                case 2: {
                    next = ReinforcementLearnerUtil.getTop(current, navGrid, cellWidth);
                }
                break;
                case 3: {
                    next = ReinforcementLearnerUtil.getBottom(current, navGrid, cellWidth, frameWidth);
                }
            }

            if (next != null) {
                return next;
            }
        }
    }
}

