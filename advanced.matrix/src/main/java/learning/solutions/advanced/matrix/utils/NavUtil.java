package learning.solutions.advanced.matrix.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import learning.solutions.advanced.matrix.domain.NavCell;

public class NavUtil {
	public static Map<Integer, NavCell> populateGridMap(Properties matrixConfig) {
		int totalHeight = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.FRAME_HEIGHT_PROPERTY));
		int cellWidth = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.CELL_WIDTH_PROPERTY));

		int pointCount = totalHeight / cellWidth;
		int id = 0;
		Map<Integer, NavCell> gridMap = new HashMap<Integer, NavCell>();
		for (int i = 0; i < pointCount; i++) {
			for (int j = 0; j < pointCount; j++) {
				Point tempPoint = new Point(j * cellWidth, i * cellWidth);
				NavCell nCell = new NavCell(tempPoint, id);
				gridMap.put(id, nCell);
				id++;
			}
		}
		return gridMap;
	}

	public static int findNavId(Map<Integer, NavCell> gridMap, Point center) {
		int id = -1;

		for (int i : gridMap.keySet()) {
			NavCell temp = gridMap.get(i);
			if (temp.getCenter().equals(center)) {
				id = i;
				break;
			}
		}

		return id;
	}

	public static void addUniqueToOpen(List<NavCell> source, List<NavCell> open, List<NavCell> closed) {
		for (NavCell nCell : source) {
			if (!open.contains(nCell) && !closed.contains(nCell)) {
				open.add(nCell);
			}
		}
	}

	public static final double getGCost(NavCell current, NavCell start, int cellWidth) {
		if (current == null || current.equals(start)) {
			return 0.0d;
		} else
			return cellWidth + getGCost(current.getParent(), start, cellWidth);
	}

	public static final List<NavCell> getAdjNodesFromGrid(Map<Integer, NavCell> gridMap, NavCell[] adjNodes) {
		List<NavCell> adjacentNodes = new ArrayList<NavCell>();

		for (NavCell nCell : adjNodes) {
			int id = NavUtil.findNavId(gridMap, nCell.getCenter());
			adjacentNodes.add(gridMap.get(id));
		}
		return adjacentNodes;
	}

	/**
	 * Returns the index of the cell with the lowest fScore.
	 * 
	 * @param listCells
	 * @param start
	 * @param end
	 * @param cellWidth
	 * @return
	 */
	public static final int getMinFCell(List<NavCell> listCells, NavCell start, NavCell end, int cellWidth) {
		double minFScore = Double.MAX_VALUE;
		int minFId = 0;

		for (int i = 0; i < listCells.size(); i++) {
			NavCell nCell = listCells.get(i);
			double currentFScore = nCell.getfCost();
			if (currentFScore < minFScore) {
				minFId = i;
				minFScore = currentFScore;
			}
		}

		return minFId;
	}

	/**
	 * Returns the directions of point2 with respect to point 1
	 * @param lat1 : Latitude of point1 in degrees
	 * @param long1 : Longitude of point1 in degrees
	 * @param lat2 : Latitude of point2 in degrees
	 * @param long2 : Longitude of point2 in degrees
     * @return String (EAST, WEST, NORTH, SOUTH)
	 *
	 * φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6,371km);
	 * note that angles need to be in radians to pass to trig functions!
	 * Δφ = (lat2-lat1).toRadians();
	 * Δλ = (lon2-lon1).toRadians();
	 * θ = atan2( sin Δλ ⋅ cos φ2 , cos φ1 ⋅ sin φ2 − sin φ1 ⋅ cos φ2 ⋅ cos Δλ )
     */
	public static final String getDirection(double lat1, double long1, double lat2, double long2) {
		Double[] startCoordsInRads = new Double[]{Math.toRadians(lat1), Math.toRadians(long1)};
		Double[] destCoordsInRads = new Double[]{Math.toRadians(lat2), Math.toRadians(long2)};

		double bearing = Math.atan2(
				Math.sin(destCoordsInRads[1] - startCoordsInRads[1]) * Math.cos(destCoordsInRads[0]),
				Math.cos(startCoordsInRads[0]) * Math.sin(destCoordsInRads[0]) - Math.sin(startCoordsInRads[0]) * Math.cos(destCoordsInRads[0]) * Math.cos(destCoordsInRads[1] - startCoordsInRads[1])
		);

		double val = Math.toDegrees(bearing);

		if (val < 45 && val >= -45) {
			return "North";
		} else if (val >= 45 && val < 135) {
			return "East";
		} else if (val >= 135 || val < -135) {
			return "South";
		}

		return "West";
	}
}
