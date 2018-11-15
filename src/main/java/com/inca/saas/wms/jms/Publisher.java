/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.inca.saas.wms.jms;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

class Publisher {

	public static void main(String[] args) throws Exception {

		final String TOPIC_PREFIX = "queue://";

		String user = env("ACTIVEMQ_USER", "admin");
		String passWord = env("ACTIVEMQ_PASSWORD", "password");
		String host = env("ACTIVEMQ_HOST", "localhost");
		int port = Integer.parseInt(env("ACTIVEMQ_PORT", "5672"));

		String connectionURI = "amqp://" + host + ":" + port;
		String destinationName = arg(args, 0, "queue://com.inca.saas.goods");

		int messages = 5;
		int size = 5;

		String DATA = "abcdefghijklmnopqrstuvwxyz";
		String body = "";
		for (int i = 0; i < size; i++) {
			body += DATA.charAt(i % DATA.length());
		}

		// JmsConnectionFactory factory = new
		// JmsConnectionFactory(connectionURI);
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, passWord, host);

		Connection connection = factory.createConnection(user, passWord);
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destination = null;
		// if (destinationName.startsWith(TOPIC_PREFIX)) {
		// destination =
		// session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
		// } else {
		destination = session.createQueue(destinationName);
		// }

		MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		for (int i = 1; i <= messages; i++) {
			TextMessage msg = session.createTextMessage("#:接收的数据--" + body);
			msg.setIntProperty("id", i);
			producer.send(msg);
			if ((i % 1000) == 0) {
				System.out.println(String.format("Sent %d messages", i));
			}
		}

		// producer.send(session.createTextMessage("SHUTDOWN"));
		Thread.sleep(1000 * 3);
		connection.close();
		System.exit(0);
	}

	public void Publisher(String body) throws Exception {

		final String TOPIC_PREFIX = "queue://";

		String user = env("ACTIVEMQ_USER", "admin");
		String passWord = env("ACTIVEMQ_PASSWORD", "password");
		String host = env("ACTIVEMQ_HOST", "localhost");
		String[] args = { "0" };
		int port = Integer.parseInt(env("ACTIVEMQ_PORT", "5672"));

		String connectionURI = "amqp://" + host + ":" + port;
		String destinationName = arg(args, 0, "queue://ceshi");

		int messages = 10;
		int size = 10;

		// String DATA = "abcdefghijklmnopqrstuvwxyz";
		// String body = "";
		// for (int i = 0; i < size; i++) {
		// body += DATA.charAt(i % DATA.length());
		// }

		// JmsConnectionFactory factory = new
		// JmsConnectionFactory(connectionURI);
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, passWord, host);
		Connection connection = factory.createConnection(user, passWord);
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Destination destination = null;
		// if (destinationName.startsWith(TOPIC_PREFIX)) {
		// destination =
		// session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
		// } else {
		destination = session.createQueue(destinationName);
		// }

		MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		// for (int i = 1; i <= messages; i++) {
		TextMessage msg = session.createTextMessage("message--" + body);
		msg.setIntProperty("id", 1);
		producer.send(msg);
		// if ((i % 1000) == 0) {
		// System.out.println(String.format("Sent %d messages", i));
		// }
		// }

		producer.send(session.createTextMessage("SHUTDOWN"));
		Thread.sleep(1000 * 3);
		connection.close();
		System.exit(0);
	}

	private static String env(String key, String defaultValue) {
		String rc = System.getenv(key);
		if (rc == null)
			return defaultValue;
		return rc;
	}

	private static String arg(String[] args, int index, String defaultValue) {
		if (index < args.length)
			return args[index];
		else
			return defaultValue;
	}

}