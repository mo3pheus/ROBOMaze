package learning.solutions.advanced.matrix.domain;

import java.awt.Graphics2D;

public abstract class VirtualElement extends MatrixElement {
	public abstract void draw(Graphics2D g2);

	public abstract void build();
}