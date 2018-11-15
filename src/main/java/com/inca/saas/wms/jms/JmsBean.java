package com.inca.saas.wms.jms;

/**
 * 物流接口参数bean
 * 
 * @author Administrator wangdongdong
 *
 */
public class JmsBean {

	/**
	 * set1
	 */
	private String setCode = null;

	/**
	 * customId-685
	 */
	private String customId = null;
	/**
	 * 定时
	 */
	private long time = 60000;
	/**
	 * 物流接口中间库IP String ip = "218.247.157.237";
	 */
	private String dbIp = null;
	/**
	 * 物流接口中间库用户 String user= "jztwms";
	 */
	private String dbUser = null;
	/**
	 * 物流接口中间库密码 String pass= "jztwms";
	 */
	private String dbPassWord = null;
	/**
	 * 物流接口中间库 String sid = "orcl";oracle数据库
	 */
	private String dbSid = null;
	/**
	 * localhost
	 */
	private String JmsIp = null;
	/**
	 * 5672
	 */
	private String JmsPort = null;
	/**
	 * admin
	 */
	private String JmsUser = null;
	/**
	 * password
	 */
	private String JmsPassWord = null;
	
	/**
	 * domain
	 */
	public String domain;
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getSetCode() {
		return setCode;
	}

	public void setSetCode(String setCode) {
		this.setCode = setCode;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getDbIp() {
		return dbIp;
	}

	public void setDbIp(String dbIp) {
		this.dbIp = dbIp;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassWord() {
		return dbPassWord;
	}

	public void setDbPassWord(String dbPassWord) {
		this.dbPassWord = dbPassWord;
	}

	public String getDbSid() {
		return dbSid;
	}

	public void setDbSid(String dbSid) {
		this.dbSid = dbSid;
	}

	public String getJmsIp() {
		return JmsIp;
	}

	public void setJmsIp(String jmsIp) {
		JmsIp = jmsIp;
	}

	public String getJmsPort() {
		return JmsPort;
	}

	public void setJmsPort(String jmsPort) {
		JmsPort = jmsPort;
	}

	public String getJmsUser() {
		return JmsUser;
	}

	public void setJmsUser(String jmsUser) {
		JmsUser = jmsUser;
	}

	public String getJmsPassWord() {
		return JmsPassWord;
	}

	public void setJmsPassWord(String jmsPassWord) {
		JmsPassWord = jmsPassWord;
	}

}
