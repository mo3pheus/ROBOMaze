package learning.solutions.advanced.matrix.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;

import learning.solutions.advanced.matrix.driver.MatrixCreation;

public class PortableMatrixConfig implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2277832505186440104L;
	private Properties			matrixConfig		= null;

	public PortableMatrixConfig() {
		try {
			matrixConfig = MatrixCreation.getMatrixConfig();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Properties getMatrixConfig() {
		return matrixConfig;
	}

	public void setMatrixConfig(Properties matrixConfig) {
		this.matrixConfig = matrixConfig;
	}
}
