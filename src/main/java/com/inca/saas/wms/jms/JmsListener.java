package com.inca.saas.wms.jms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.inca.saas.wms.common.JmsConstant;
import com.inca.saas.wms.pub.JmsPubListener;

/**
 * jms监听处理
 * 
 * @author Administrator wangdongdong
 *
 */
public class JmsListener {

	public static JmsListener instance;

	public static JmsListener getInstance() throws Exception {
		if (instance == null) {
			instance = new JmsListener();
		}
		return instance;
	}

	public JmsListener() {
		try {
			initLmsBean();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public Connection getJmsCon() throws Exception {
//		String user = env("ACTIVEMQ_USER", "admin");
//		String passWord = env("ACTIVEMQ_PASSWORD", "password");
//		String host = env("ACTIVEMQ_HOST", "localhost");
//		int port = Integer.parseInt(env("ACTIVEMQ_PORT", "5672"));
		String user = jmsBean.getJmsUser();
		String passWord = jmsBean.getJmsPassWord();
		String host = jmsBean.getJmsIp();
		String port = jmsBean.getJmsPort();

		String connectionURI = "amqp://" + host + ":" + port;
		connectionURI = host;

		//JmsConnectionFactory factory = new JmsConnectionFactory(connectionURI);
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, passWord, host);

		Connection connection = factory.createConnection(user, passWord);
		return connection;
	}

	public void registJmsListener(String destinationName, MessageListener listener) throws Exception {
		Connection connection = getJmsCon();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue(destinationName);
		MessageConsumer consumer = session.createConsumer(destination);
		consumer.setMessageListener(listener);
	}

	// 调用示例
	public static void main(String[] args) throws Exception {
		// 项目启动
		// 调用
		JmsListener jms = new JmsListener();
		JmsPubListener jmspub=new JmsPubListener();
		jmspub.init();
		// "a" 表示队列的唯一编码。不能重复。
		jms.registJmsListener("inca.saas.wms.jzt.company", new MessageListener() {

			@Override
			public void onMessage(Message message) {
				if (message instanceof TextMessage) {
					TextMessage text = (TextMessage) message;
					String text2;
					try {
						text2 = text.getText();
						
						System.out.println(text2);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			}
		});
		Thread.sleep(5000);
	}

	public void initLmsBean() throws Exception {
		// 读取文件
		InputStream in = null;
		// 数据库内容
		String customId = "";
		String setCode = "";
		String dbIp = "";
		String dbUser = "";
		String dbPassWord = "";
		String dbSid = "";
		// jms内容
		String jmsIp = "";
		String jmsPort = "";
		String jmsUser = "";
		String jmsPassWord = "";
		long time;
		// 域名
		String domain = "";
		// 读取参数
		File configfile = new File("jztwms.properties");
		Properties props = null;
		if (configfile.exists()) {
			try {
				in = new FileInputStream(configfile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			props = new Properties();
			try {
				props.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			setCode = props.getProperty("setCode");
			customId = props.getProperty("customId");
			dbIp = props.getProperty("dbIp");
			dbUser = props.getProperty("dbUser");
			dbPassWord = props.getProperty("dbPassWord");
			dbSid = props.getProperty("dbSid");
			jmsIp = props.getProperty("jmsIp");
			jmsPort = props.getProperty("jmsPort");
			jmsUser = props.getProperty("jmsUser");
			jmsPassWord = props.getProperty("jmsPassWord");
			time = Integer.parseInt(props.getProperty("time"));
			jmsBean = new JmsBean();
			jmsBean.setSetCode(setCode);
			jmsBean.setCustomId(customId);
			jmsBean.setJmsIp(jmsIp);
			jmsBean.setJmsPort(jmsPort);
			jmsBean.setJmsUser(jmsUser);
			jmsBean.setJmsPassWord(jmsPassWord);
			jmsBean.setDbIp(dbIp);
			jmsBean.setDbSid(dbSid);
			jmsBean.setDbUser(dbUser);
			jmsBean.setDbPassWord(dbPassWord);
			jmsBean.setTime(time * 60000);
			
			// 设置队列名为 域名 + : + 原有队列名 与 ibs 保持一致   liush 20170508
			domain = props.getProperty("domain");
			jmsBean.setDomain(domain);
//			jmsConstant = new JmsConstant();
			/*Class<? extends JmsConstant> jmsConstantClass = jmsConstant.getClass();
			Field[] declaredFields = jmsConstantClass.getDeclaredFields();
			Field ibsField = jmsConstantClass.getDeclaredField("SENDTOIBSDEST");// 发送到ibs的队列名-不处理
			for (Field field : declaredFields) {
				String fieldName = field.getName();
				String fieldVaule = (String) field.get(fieldName);
				if(fieldName.equals(ibsField.getName()) || fieldVaule.startsWith(domain)){
					continue;
				}
				field.set(fieldName, domain + ":" + fieldVaule);
			}*/
			// 设置队列名为 域名 + : + 原有队列名 与 ibs 保持一致   liush 20170508
		
		} else {

		}
		return;
	}

	/**
	 * 物流接口属性bean
	 */
	private static JmsBean jmsBean = null;
	private static JmsConstant jmsConstant = null;

	public JmsBean getJmsBean() {
		return jmsBean;
	}

	public void setJmsBean(JmsBean jmsBean) {
		JmsListener.jmsBean = jmsBean;
	}

	private static String env(String key, String defaultValue) {
		String rc = System.getenv(key);
		if (rc == null)
			return defaultValue;
		return rc;
	}

	// public static void main(String[] args) throws JMSException, Exception {
	//
	// final String TOPIC_PREFIX = "queue://";
	//
	// String user = env("ACTIVEMQ_USER", "admin");
	// String password = env("ACTIVEMQ_PASSWORD", "password");
	// String host = env("ACTIVEMQ_HOST", "localhost");
	// int port = Integer.parseInt(env("ACTIVEMQ_PORT", "5672"));
	//
	// String connectionURI = "amqp://" + host + ":" + port;
	// String destinationName = arg(args, 0, "queue://ceshi");
	//
	// JmsConnectionFactory factory = new JmsConnectionFactory(connectionURI);
	//
	// Connection connection = factory.createConnection(user, password);
	// connection.start();
	// Session session = connection.createSession(false,
	// Session.AUTO_ACKNOWLEDGE);
	//
	// Destination destination = null;
	//// if (destinationName.startsWith(TOPIC_PREFIX)) {
	//// destination =
	// session.createTopic(destinationName.substring(TOPIC_PREFIX.length()));
	//// } else {
	// destination = session.createQueue(destinationName);
	//// }
	//
	// MessageConsumer consumer = session.createConsumer(destination);
	// long start = System.currentTimeMillis();
	// long count = 1;
	// System.out.println("Waiting for messages...");
	// while (true) {
	// Message msg = consumer.receive();
	// if (msg instanceof TextMessage) {
	// String body = ((TextMessage) msg).getText();
	// if ("SHUTDOWN".equals(body)) {
	// long diff = System.currentTimeMillis() - start;
	// System.out.println(String.format("Received %d in %.2f seconds", count,
	// (1.0 * diff / 1000.0)));
	// connection.close();
	// try {
	// Thread.sleep(10);
	// } catch (Exception e) {}
	// System.exit(1);
	// } else {
	// try {
	// if (count != msg.getIntProperty("id")) {
	// System.out.println("mismatch: " + count + "!=" +
	// msg.getIntProperty("id"));
	// }
	// } catch (NumberFormatException ignore) {
	// }
	//
	// if (count == 1) {
	// start = System.currentTimeMillis();
	// } else if (count % 1000 == 0) {
	// System.out.println(String.format("Received %d messages.", count));
	// }
	// count++;
	// }
	//
	// } else {
	// System.out.println("Unexpected message type: " + msg.getClass());
	// }
	// }
	// }
}