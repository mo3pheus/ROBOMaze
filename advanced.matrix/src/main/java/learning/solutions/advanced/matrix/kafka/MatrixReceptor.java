/**
 * 
 */
package learning.solutions.advanced.matrix.kafka;

import java.awt.Event;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.protobuf.InvalidProtocolBufferException;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import learning.solutions.advanced.matrix.MatrixCommunication.MatrixCall;
import learning.solutions.advanced.matrix.domain.MatrixArchitect;
import learning.solutions.advanced.matrix.domain.NavigationPath;
import learning.solutions.advanced.matrix.domain.PortableMatrixConfig;
import learning.solutions.advanced.matrix.driver.MatrixCreation;
import learning.solutions.advanced.matrix.engineeringLevel.NavigationEngine;
import learning.solutions.advanced.matrix.utils.SerializationUtil;

/**
 * @author sanketkorgaonkar
 *
 */
public class MatrixReceptor extends Thread {
	final static String	clientId			= "ControlRoomZion";
	final static String	TOPIC				= "nebuchadnezzar.main.deck.com.2";
	ConsumerConnector	consumerConnector	= null;
	MatrixArchitect		architect			= null;

	public MatrixReceptor() throws Exception {
		Properties properties = new Properties();
		properties.put("zookeeper.connect", "localhost:2181");
		properties.put("group.id", "test-coms-zion-controlRoom");
		ConsumerConfig consumerConfig = new ConsumerConfig(properties);
		consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
		MatrixCreation.configureLogging();
		Properties matrixConfig = new Properties();
		FileInputStream propFile = new FileInputStream(
				"/Users/sanketkorgaonkar/Documents/CodeRepos/AdvancedMatrix/advanced.matrix/src/main/resources/mazeDefinition.properties");
		matrixConfig.load(propFile);

		if (matrixConfig != null) {
			this.architect = new MatrixArchitect(matrixConfig);
		}
	}

	@Override
	public void run() {
		System.out.println("Listening ...");
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(TOPIC, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector
				.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(TOPIC).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext())
			try {
				MatrixCall received = (MatrixCall.parseFrom(it.next().message()));
				System.out.println(received);

				if (received.getMessageType() == 2) {
					NavigationPath navPath = (NavigationPath) SerializationUtil
							.deserialize(received.getPath().toByteArray());
					architect.updateRobotPositions(navPath.getPath());
				} else if (received.getMessageType() == 1) {
					PortableMatrixConfig matrixConfig = (PortableMatrixConfig) SerializationUtil
							.deserialize(received.getMatrixConfig().toByteArray());

					NavigationEngine navEngine = new NavigationEngine(matrixConfig.getMatrixConfig());
					architect = new MatrixArchitect(matrixConfig.getMatrixConfig(),
							navEngine.getAnimationCalibratedRobotPath());
				}

			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
}
