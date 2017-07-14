package learning.solutions.advanced.matrix.domain;

import java.awt.Point;
import java.util.List;

public interface PerformsNavigation {
	List<Point> navigate(NavCell start, NavCell end);
}
