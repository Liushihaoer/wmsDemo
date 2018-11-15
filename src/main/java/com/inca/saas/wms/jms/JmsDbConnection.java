package com.inca.saas.wms.jms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.log4j.Category;

/**
 * 物流中间库连接
 * 
 * @author Administrator wangdongdong
 *
 */
public class JmsDbConnection {
	
	Category logger = Category.getInstance(JmsDbConnection.class);

	public static JmsDbConnection instance;

	public static JmsDbConnection getInstance() throws Exception {
		if (instance == null) {
			instance = new JmsDbConnection();
		}
		return instance;
	}

	public JmsDbConnection() {
		try {
			initLmsBean();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public Connection getCon() throws Exception {
		String ip = jmsBean.getDbIp();
		String user = jmsBean.getDbUser();
		String pass = jmsBean.getDbPassWord();
		String sid = jmsBean.getDbSid();
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@" + ip + ":1521:" + sid;
		logger.info("获取中间库链接url="+url+",user="+user+",pass="+pass);
		Connection sqlcon = DriverManager.getConnection(url, user, pass);
		if (sqlcon == null) {
			logger.info("获取url="+url+"的中间库链接失败");
			throw new Exception("---");
		}
		logger.info("获取url="+url+"的中间库链接成功");
		sqlcon.setAutoCommit(false);

		return sqlcon;
	}

	/**
	 * 物流接口属性bean
	 */
	private static JmsBean jmsBean = null;

	public JmsBean getJmsBean() {
		return jmsBean;
	}

	public void setJmsBean(JmsBean jmsBean) {
		JmsDbConnection.jmsBean = jmsBean;
	}

	public void initLmsBean() {
		// 读取文件
		InputStream in = null;
		// 数据库内容
		String setCode = "";
		String customId = "";
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
		} else {
			logger.info("九州通：没有找到--jztwms.properties--配置文件");
		}
		return;
	}
	
	public static void main(String[] args) {
		JmsDbConnection jms = new JmsDbConnection();
		jms.initLmsBean();
	}

}
