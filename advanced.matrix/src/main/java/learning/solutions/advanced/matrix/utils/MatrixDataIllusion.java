package learning.solutions.advanced.matrix.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MatrixDataIllusion {
	private static List<Point> generateRobotPositions(Point start, Point end, int stepSize) {
		List<Point> positions = new ArrayList<Point>();

		int numXIterations = Math.abs(start.x - end.x) / stepSize;
		int lastStepSize = Math.abs(start.x - end.x) % stepSize;
		int startX = start.x;
		int startY = start.y;
		for (int i = 0; i < numXIterations; i++) {
			startX = (start.x > end.x) ? startX - stepSize : startX + stepSize;
			Point temp = new Point(startX, startY);
			positions.add(temp);
		}
		if (lastStepSize > 0) {
			startX = (start.x > end.x) ? startX - lastStepSize : startX + lastStepSize;
			Point temp = new Point(startX, startY);
			positions.add(temp);
		}

		int numYIterations = Math.abs(start.y - end.y)/ stepSize;
		lastStepSize = Math.abs(start.y - end.y) % stepSize;
		for (int i = 0; i < numYIterations; i++) {
			startY = (start.y > end.y) ? startY - stepSize : startY + stepSize;
			Point temp = new Point(startX, startY);
			positions.add(temp);
		}
		if (lastStepSize > 0) {
			startY = (start.y > end.y) ? startY - lastStepSize : startY + lastStepSize;
			Point temp = new Point(startX, startY);
			positions.add(temp);
		}

		return positions;
	}

	public static List<Point> generateAnimationProfile(Properties matrixConfig) {
		List<Point> robotPositions = new ArrayList<Point>();

		int numPositions = Integer
				.parseInt(matrixConfig.getProperty(EnvironmentUtils.ANIMATION_PROFILE_NUMBER_POSITIONS));
		if (numPositions > 0) {
			int stepSize = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.ANIMATION_STEP_SIZE));

			for (int i = 0; i < numPositions - 1; i++) {
				String startPosnProp = EnvironmentUtils.ANIMATION_PROFILE_POSITION_HEADER + Integer.toString(i);
				String endPosnProp = EnvironmentUtils.ANIMATION_PROFILE_POSITION_HEADER + Integer.toString(i + 1);
				String[] startPosnString = matrixConfig.getProperty(startPosnProp).split(",");
				String[] endPosnString = matrixConfig.getProperty(endPosnProp).split(",");
				Point start = new Point(Integer.parseInt(startPosnString[0]), Integer.parseInt(startPosnString[1]));
				Point end = new Point(Integer.parseInt(endPosnString[0]), Integer.parseInt(endPosnString[1]));

				List<Point> tempList = MatrixDataIllusion.generateRobotPositions(start, end, stepSize);
				System.out.println(" Start = " + start.toString() + " End = " + end.toString());
				robotPositions.addAll(tempList);
			}
		}

		return robotPositions;
	}
}
