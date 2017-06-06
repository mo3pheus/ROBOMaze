/**
 * 
 */
package learning.solutions.advanced.matrix.driver;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;

import learning.solutions.advanced.matrix.domain.MatrixArchitect;
import learning.solutions.advanced.matrix.engineeringLevel.KesavaReinforcementLearner;
import learning.solutions.advanced.matrix.engineeringLevel.NavigationEngine;
import learning.solutions.advanced.matrix.utils.EnvironmentUtils;

/**
 * @author ktalabattula
 *
 */
public class KesavaMazeControl {
	private static Properties matrixConfig = null;

    public static void main(String[] args) throws IOException {
        configureLogging();
        System.out.println("Hello to the Robo-Maze_World");
        matrixConfig = getMatrixConfig();
        NavigationEngine navEngine = new NavigationEngine(matrixConfig);
        new MatrixArchitect(matrixConfig, navEngine.getAnimationCalibratedRobotPath());
        KesavaReinforcementLearner learner = new KesavaReinforcementLearner(matrixConfig);
        learner.configureReinforcementLearner(0.4d, 0.8d);
        int sourceX = Integer
                .parseInt(matrixConfig.getProperty(EnvironmentUtils.ROBOT_START_LOCATION).split(",")[0]);
        int sourceY = Integer
                .parseInt(matrixConfig.getProperty(EnvironmentUtils.ROBOT_START_LOCATION).split(",")[1]);
        int destX = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.DESTN_POSN_PROPERTY).split(",")[0]);
        int destY = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.DESTN_POSN_PROPERTY).split(",")[1]);
        learner.train(new Point(destX, destY));
        new MatrixArchitect(matrixConfig, NavigationEngine.getAnimationCalibratedRobotPath(learner.getShortestPath(new Point(sourceX, sourceY)), 5));
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
		URL url = MatrixCreation.class.getResource("/mazeDefinition.properties");
        FileInputStream propFile = new FileInputStream(url.getPath());
        matrixConfig = new Properties();
        matrixConfig.load(propFile);
        return matrixConfig;
    }
}
