package learning.solutions.advanced.matrix.engineeringLevel;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import learning.solutions.advanced.matrix.domain.Cell;
import learning.solutions.advanced.matrix.domain.Grid;
import learning.solutions.advanced.matrix.domain.MatrixElement;
import learning.solutions.advanced.matrix.domain.WallBuilder;
import learning.solutions.advanced.matrix.utils.EnvironmentUtils;

@Deprecated
public class AnimationEngine {
	public static void animateRobot(Graphics2D canvas, List<Point> positions, Properties matrixConfig)
			throws Exception {
		if (positions.isEmpty()) {
			throw new Exception("List of positions is empty!");
		}

		int cellWidth = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.CELL_WIDTH_PROPERTY));
		Point oldPosition = positions.get(0);
		for (int i = 0; i < positions.size(); i++) {
			if (i % 20 == 0) {
				System.out.println("roboLocationPing:: " + oldPosition.toString());
			}
			canvas.clearRect(oldPosition.x, oldPosition.y, cellWidth, cellWidth);

			Point position = positions.get(i);
			List<MatrixElement> matrixCitizens = new ArrayList<MatrixElement>();
			Cell startingCell = getStartLocation(matrixConfig);
			Cell destination = new Cell(matrixConfig);
			Grid grid = new Grid(matrixConfig);
			WallBuilder wallBuilder = new WallBuilder(matrixConfig);
			Cell robo = new Cell(matrixConfig);
			robo.setLocation(position);
			robo.setCellWidth(14);
			robo.setColor(EnvironmentUtils.findColor(matrixConfig.getProperty(EnvironmentUtils.ROBOT_COLOR)));

			matrixCitizens.add(grid);
			matrixCitizens.add(destination);
			matrixCitizens.add(startingCell);
			matrixCitizens.add(wallBuilder);
			matrixCitizens.add(robo);

			long delayMs = Long.parseLong(matrixConfig.getProperty(EnvironmentUtils.ANIMATION_PACE_DELAY));
			createMatrix(matrixCitizens, canvas, delayMs);
			oldPosition = position;
		}
		// send window closing event
	}

	private static Cell getStartLocation(Properties matrixConfig) {
		Cell start = new Cell(matrixConfig);
		int sourceX = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.ROBOT_START_LOCATION).split(",")[0]);
		int sourceY = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.ROBOT_START_LOCATION).split(",")[1]);
		start.setLocation(new Point(sourceX, sourceY));
		start.setColor(EnvironmentUtils.findColor(matrixConfig.getProperty(EnvironmentUtils.START_CELL_COLOR)));
		return start;
	}

	private static void createMatrix(List<MatrixElement> matrixCitizens, Graphics2D canvas, long delayMs) {
		for (MatrixElement mCitizen : matrixCitizens) {
			mCitizen.draw(canvas);
		}

		try {
			Thread.sleep(delayMs);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
