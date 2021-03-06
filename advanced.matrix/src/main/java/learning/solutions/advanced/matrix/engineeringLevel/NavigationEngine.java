package learning.solutions.advanced.matrix.engineeringLevel;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learning.solutions.advanced.matrix.domain.NavCell;
import learning.solutions.advanced.matrix.domain.PerformsNavigation;
import learning.solutions.advanced.matrix.utils.AnimationUtil;
import learning.solutions.advanced.matrix.utils.EnvironmentUtils;
import learning.solutions.advanced.matrix.utils.NavUtil;

public class NavigationEngine implements PerformsNavigation {

	private Map<Integer, NavCell>	gridMap				= null;
	private Properties				matrixConfig		= null;
	private int						cellWidth			= 0;
	private int						animationStepSize	= 5;
	private NavCell					source				= null;
	private NavCell					destination			= null;
	private List<Point>				robotPath			= null;
	private Logger					logger				= LoggerFactory.getLogger(NavigationEngine.class);

	public NavigationEngine(Properties matrixConfig) {
		this.matrixConfig = matrixConfig;
		this.gridMap = new HashMap<Integer, NavCell>();
		gridMap = NavUtil.populateGridMap(matrixConfig);
		this.cellWidth = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.CELL_WIDTH_PROPERTY));
		this.animationStepSize = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.ANIMATION_STEP_SIZE));

		configureAdjacency();

		try {
			int sourceX = Integer
					.parseInt(matrixConfig.getProperty(EnvironmentUtils.ROBOT_START_LOCATION).split(",")[0]);
			int sourceY = Integer
					.parseInt(matrixConfig.getProperty(EnvironmentUtils.ROBOT_START_LOCATION).split(",")[1]);
			int destX = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.DESTN_POSN_PROPERTY).split(",")[0]);
			int destY = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.DESTN_POSN_PROPERTY).split(",")[1]);
			int sourceId = NavUtil.findNavId(gridMap, new Point(sourceX, sourceY));
			int destId = NavUtil.findNavId(gridMap, new Point(destX, destY));
			source = gridMap.get(sourceId);
			destination = gridMap.get(destId);
			robotPath = navigate(source, destination);
			logPath();
		} catch (Exception e) {
			System.out.println("Robot navigation guidance not provided");
		}
	}

	public List<Point> getRobotPath() {
		return robotPath;
	}

	public List<Point> getAnimationCalibratedRobotPath() {
		List<Point> calibratedPath = new ArrayList<Point>();
		if (robotPath != null) {
			for (int i = 0; i < robotPath.size() - 1; i++) {
				Point start = robotPath.get(i);
				Point end = robotPath.get(i + 1);
				List<Point> tempPoints = AnimationUtil.generateRobotPositions(start, end, animationStepSize);
				calibratedPath.addAll(tempPoints);
			}
		}
		return calibratedPath;
	}

    public static List<Point> getAnimationCalibratedRobotPath(List<Point> robotPath, int animationStepSize) {
        List<Point> calibratedPath = new ArrayList<Point>();
        if (robotPath != null) {
            for (int i = 0; i < robotPath.size() - 1; i++) {
                Point start = robotPath.get(i);
                Point end = robotPath.get(i + 1);
                List<Point> tempPoints = AnimationUtil.generateRobotPositions(start, end, animationStepSize);
                calibratedPath.addAll(tempPoints);
            }
        }
        return calibratedPath;
    }

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Integer i : gridMap.keySet()) {
			NavCell nCell = gridMap.get(i);
			sb.append("\n ============================================ ");
			sb.append("\n Id = " + i + " Center = " + nCell.toString());
		}

		return sb.toString();
	}

	/**
	 * This method provides the navigation functionality to get from point start
	 * to point end. The following code implements the A* algorithm.
	 */
	public List<Point> navigate(NavCell start, NavCell end) {
		List<NavCell> open = new ArrayList<NavCell>();
		List<NavCell> closed = new ArrayList<NavCell>();
		open.add(start);

		boolean done = false;
		while (!done) {
			int minFIndex = NavUtil.getMinFCell(open, start, end, cellWidth);
			NavCell current = open.get(minFIndex);
			open.remove(minFIndex);
			closed.add(current);

			if (current.equals(end)) {
				System.out.println("Path found!");
				return calcPath(start, current);
			}

			List<NavCell> adjacentNodes = NavUtil.getAdjNodesFromGrid(gridMap, current.getAdjacentNodes());
			for (int i = 0; i < adjacentNodes.size(); i++) {
				NavCell cAdjNode = adjacentNodes.get(i);
				if (closed.contains(cAdjNode)) {
					continue;
				}

				if (!open.contains(cAdjNode)) {
					cAdjNode.setParent(current);
					double gCost = NavUtil.getGCost(cAdjNode, start, cellWidth);
					cAdjNode.setgCost(gCost);
					int hCost = Math.abs(cAdjNode.getCenter().x - end.getCenter().x)
							+ Math.abs(cAdjNode.getCenter().y - end.getCenter().y);
					cAdjNode.sethCost(hCost);
					cAdjNode.setfCost(hCost + cAdjNode.getgCost());
					open.add(cAdjNode);
				} else {
					double newGCost = NavUtil.getGCost(current, start, cellWidth) + cellWidth;
					if (cAdjNode.getgCost() > newGCost) {
						cAdjNode.setgCost(newGCost);
						cAdjNode.setfCost(cAdjNode.gethCost() + cAdjNode.getgCost());
						cAdjNode.setParent(current);
					}
				}
			}
			done = open.isEmpty();
		}

		if (open.isEmpty()) {
			System.out.println(
					"No path was found between start => " + start.toString() + " and end => " + end.toString());
			return null;
		}
		return null;
	}

	/**
	 * @return the gridMap
	 */
	public Map<Integer, NavCell> getGridMap() {
		return gridMap;
	}

	/**
	 * @param gridMap
	 *            the gridMap to set
	 */
	public void setGridMap(Map<Integer, NavCell> gridMap) {
		this.gridMap = gridMap;
	}

	private void logPath() {
		for (Point p : this.robotPath) {
			logger.info("navEnginePosn :: " + p.toString());
		}
	}

	private void configureAdjacency() {
		for (int id : gridMap.keySet()) {
			NavCell nCell = gridMap.get(id);
			AdjacencyCalculator adSensor = new AdjacencyCalculator(nCell.getCenter(), matrixConfig);
			nCell.setAdjacentNodes(adSensor.getAdjacentNodes());
		}
	}

	private List<Point> calcPath(NavCell start, NavCell current) {
		List<Point> path = new ArrayList<Point>();

		boolean done = true;
		while (done) {
			if (current.equals(start)) {
				done = false;
			}
			path.add(current.getCenter());
			current = current.getParent();
		}
		Collections.reverse(path);
		return path;
	}
}
