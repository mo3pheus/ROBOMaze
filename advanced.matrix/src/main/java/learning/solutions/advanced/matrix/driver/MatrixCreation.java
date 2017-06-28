package learning.solutions.advanced.matrix.driver;

import learning.solutions.advanced.matrix.domain.Cell;
import learning.solutions.advanced.matrix.domain.MatrixArchitect;
import learning.solutions.advanced.matrix.domain.RCell;
import learning.solutions.advanced.matrix.engineeringLevel.NavigationEngine;
import learning.solutions.advanced.matrix.engineeringLevel.ReinforcementLearner;
import learning.solutions.advanced.matrix.utils.EnvironmentUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MatrixCreation {

    private static Properties matrixConfig = null;

    public static void main(String[] args) throws IOException {
        configureLogging();
        System.out.println("Hello to the Robo-Maze_World");
        matrixConfig = getMatrixConfig();
//        NavigationEngine navEngine = new NavigationEngine(getMatrixConfig());
//        new MatrixArchitect(getMatrixConfig(), navEngine.getAnimationCalibratedRobotPath());

        java.util.List<Point> captureExploration = new ArrayList<>();
        ReinforcementLearner  learner            = new ReinforcementLearner(getMatrixConfig());
        learner.configureLearner(0.4d, 0.99d);
        int sourceX = Integer
                .parseInt(matrixConfig.getProperty(EnvironmentUtils.ROBOT_START_LOCATION).split(",")[0]);
        int sourceY = Integer
                .parseInt(matrixConfig.getProperty(EnvironmentUtils.ROBOT_START_LOCATION).split(",")[1]);
        int destX = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.DESTN_POSN_PROPERTY).split(",")[0]);
        int destY = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.DESTN_POSN_PROPERTY).split(",")[1]);

        learner.train(new Point(sourceX, sourceY), new Point(destX, destY), captureExploration);

        MatrixArchitect matrixArchitect = new MatrixArchitect(getMatrixConfig(), NavigationEngine
                .getAnimationCalibratedRobotPath(learner.getShortestPath(), 1));

        Map<Point, Double> heatMap = new HashMap<>();
        RCell[][]          navGrid = learner.getNavGrid();
        for (int i = 0; i < navGrid[0].length; i++) {
            for (int j = 0; j < navGrid[0].length; j++) {
                RCell best = navGrid[i][j].getBestAction();
                if (heatMap.get(best.getCenter()) != null) {
                    Double score = heatMap.get(best.getCenter());
                    heatMap.put(best.getCenter(), score + best.getqValue());
                } else {
                    heatMap.put(best.getCenter(), best.getqValue());
                }
            }
        }

        double   maxScore = 0.0d;
        Point    maxPoint = null;
        double[] scores   = new double[heatMap.keySet().size()];
        int      i        = 0;
        for (Point current : heatMap.keySet()) {
            scores[i] = heatMap.get(current).doubleValue();
            if (heatMap.get(current) > maxScore) {
                maxScore = heatMap.get(current).doubleValue();
                maxPoint = current;
            }
        }
        System.out.println(" Max Score is = " + maxScore + " for point = " + maxPoint.toString());

        JLayeredPane      contentPane  = matrixArchitect.getSurface().getLayeredPane();
        Map<Point, Color> heatColorMap = new HashMap<>();
        for (Point current : heatMap.keySet()) {
            heatColorMap.put(current, getHeatMapColor(heatMap.get(current).intValue(), maxScore));
            Cell cell = new Cell(matrixConfig);
            cell.setLocation(current);
            cell.setColor(heatColorMap.get(current));
            cell.setCellWidth(25);
            contentPane.add(cell, 200);
            System.out.println(" Current = " + current.toString() + " Count = " + heatMap.get(current) + " Color " +
                    "returned = " + heatColorMap.get(current).toString());
        }
        matrixArchitect.getSurface().repaint();
    }

    private static Color getHeatMapColor(double score, double maxScore) {
        if (score < 0.0d) {
            return Color.black;
        } else if (score > 0.0d && score < 2.5d) {
            return Color.red;
        } else if (score > 2.5d && score < 10.0d) {
            return Color.orange;
        } else if (score > 10.0d && score < 20.0d) {
            return Color.yellow;
        } else {
            return Color.white;
        }
    }

    public static void configureLogging() {
        FileAppender fa = new FileAppender();
        fa.setFile("navEngineOutput/navEnginePath_" + Long.toString(System.currentTimeMillis()) + ".txt");
        fa.setLayout(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"));
        fa.setThreshold(Level.toLevel(Priority.INFO_INT));
        fa.activateOptions();
        org.apache.log4j.Logger.getRootLogger().addAppender(fa);
    }

    public static Properties getMatrixConfig() throws IOException {
        URL             url      = MatrixCreation.class.getResource("/mazeDefinition.properties");
        FileInputStream propFile = new FileInputStream(url.getPath());
        matrixConfig = new Properties();
        matrixConfig.load(propFile);
        return matrixConfig;
    }
}
