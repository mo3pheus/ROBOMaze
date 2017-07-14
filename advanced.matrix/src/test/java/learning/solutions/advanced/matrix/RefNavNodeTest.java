/**
 * 
 */
package learning.solutions.advanced.matrix;

import java.util.List;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import learning.solutions.advanced.matrix.domain.MatrixArchitect;
import learning.solutions.advanced.matrix.domain.NavCell;
import learning.solutions.advanced.matrix.driver.MatrixCreation;
import learning.solutions.advanced.matrix.engineeringLevel.NavigationEngine;
import learning.solutions.advanced.matrix.utils.NavUtil;

/**
 * @author sanketkorgaonkar
 *
 */
public class RefNavNodeTest {

	public static void mainTestNavRef(String[] args) {
		Map<Integer, NavCell> graph = new HashMap<Integer, NavCell>();
		int x = 0;
		int y = 0;
		int cellWidth = 25;
		for (int i = 0; i < 10; i++) {
			Point temp = new Point(i * cellWidth, i * cellWidth);
			NavCell nCell = new NavCell(temp, i);
			graph.put(i, nCell);
		}

		NavCell grandParent = new NavCell(new Point(0, 0), -1);
		for (int i : graph.keySet()) {
			NavCell nCell = graph.get(i);
			nCell.setParent(grandParent);
		}

		for (int i : graph.keySet()) {
			System.out.println(graph.get(i).toString());
		}

	}

	public static void main(String[] args) throws Exception {
		//MatrixArchitect creator = new MatrixArchitect(MatrixCreation.getMatrixConfig());

		NavigationEngine navEngine = new NavigationEngine(MatrixCreation.getMatrixConfig());
		int startId = NavUtil.findNavId(navEngine.getGridMap(), new Point(25, 25));
		int endId = NavUtil.findNavId(navEngine.getGridMap(), new Point(125, 175));

		System.out.println("Startid = " + startId + " endId = " + endId);

		NavCell start = navEngine.getGridMap().get(201);
		NavCell end = navEngine.getGridMap().get(199);

		List<Point> path = navEngine.navigate(start, end);
		int i = 0;
		for (Point nCell : path) {
			i++;
			System.out.println(" Index = " + i + " " + NavUtil.findNavId(navEngine.getGridMap(), nCell));
		}
	}

}
