package com.inca.saas.wms.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Category;

import com.inca.saas.wms.jms.JmsBean;
import com.inca.saas.wms.jms.JmsDbConnection;
import com.inca.saas.wms.jms.JmsListener;
import com.inca.saas.wms.jms.TransData;
import com.inca.saas.wms.utils.SelectHelper;

/**
 * 
 * @ClassName:BaseTimer
 * @Description:定时任务抽象类，主要处理物流接口反馈业务
 * @author:lzt
 * @date:2016年11月9日 下午6:10:02
 *
 */
public abstract class BaseTimer extends HttpServlet {
	Category log = Category.getInstance(getClass());

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @Title:getScheduleTime
	 * @Description:获取配置文件定时任务时间
	 * @param jmsBean
	 * @return:long
	 * @date:2016年11月9日 下午6:07:29
	 */
	protected abstract long getScheduleTime(JmsBean jmsBean);

	/**
	 * 
	 * @Title:getErrorPrefix
	 * @Description:获取log日志前缀
	 * @return:String
	 * @date:2016年11月9日 下午6:08:00
	 */
	protected abstract String getLogPrefix();

	/**
	 * 
	 * @Title:getDestination
	 * @Description:获取内部请求名
	 * @return:String
	 * @date:2016年11月9日 下午6:21:25
	 */
	protected abstract String getRequestName();

	/**
	 * 
	 * @Title:getQuerySql
	 * @Description:获取查询sql
	 * @return:String
	 * @date:2016年11月9日 下午6:08:47
	 */
	protected abstract String getQuerySql();

	/**
	 * 
	 * @Title:getResultList
	 * @Description:将查询结果转换成list
	 * @return:List<Object>
	 * @date:2016年11月9日 下午6:08:47
	 */
	protected abstract List<Object> getResultList(ResultSet resultSet) throws Exception;
	
	/**
	 * 
	 * @Title:updateData
	 * @Description:更新中间表数据状态(将已读过的反馈数据zt字段改为Y)
	 * @param connection
	 * @param result	
	 * @return:void
	 * @date:2016年11月11日 下午11:11:11
	 */
	protected abstract void updateData(Connection connection,ResultSet result) throws Exception;
	
	/**
	 * 
	 * @Title:sendToIbs
	 * @Description:将反馈数据推送到IBS
	 * @param list
	 * @return:void
	 * @date:2016年11月9日 下午6:09:11
	 */
	protected void sendToIbs(List<Object> list) throws Exception {
		JmsBean jmsBean = JmsDbConnection.getInstance().getJmsBean();

		String user = jmsBean.getJmsUser();
		String passWord = jmsBean.getJmsPassWord();
		String host = jmsBean.getJmsIp();
		String setCode = jmsBean.getSetCode();
		String customId = jmsBean.getCustomId();

		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, passWord, host);
		javax.jms.Connection jmsCon = factory.createConnection(user, passWord);
		jmsCon.start();
		Session session = jmsCon.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue(JmsConstant.SENDTOIBSDEST + setCode);

		MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		TransData td = new TransData();
		td.setData(list);
		td.getCustomerIdList().add(customId);
		td.setIntent(getRequestName());
		td.setSetId(setCode);

		TextMessage msg = session.createTextMessage(td.toJson());
		producer.send(msg);
		Thread.sleep(1000 * 3);
		jmsCon.close();
	}

	@Override
	public void init() throws ServletException {
		try {
			JmsBean jmsBean = JmsListener.getInstance().getJmsBean();
			Timer servlettimer = new Timer(getLogPrefix(), true);
			long lper = getScheduleTime(jmsBean);
			Date bdate = new Date();
			servlettimer.schedule(new FeedBackProc(), bdate, lper);
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(getLogPrefix() + " error : " + e.getMessage());
		}
	}

	/**
	 * 
	 * @Title:getResultSet
	 * @Description:获取中间表数据集合
	 * @return:ResultSet
	 * @date:2016年11月9日 下午6:11:21
	 */
	protected ResultSet getResultSet(Connection con) throws Exception {
		ResultSet resultSet = null;

		try {
			resultSet = SelectHelper.getResultSet(con, getQuerySql());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(getLogPrefix() + " error : " + e.getMessage());
		}
		return resultSet;
	}
	
	/**
	 * 
	 * @ClassName:FeedBackProc
	 * @Description:反馈处理定时任务
	 * @author:lzt
	 * @date:2016年11月9日 下午6:11:00
	 *
	 */
	class FeedBackProc extends TimerTask {

		public FeedBackProc() {
		}

		public void run() {
			Connection con = null;
			try {
				log.info(getLogPrefix() + "开始！");
				con = JmsDbConnection.getInstance().getCon();
				log.info(getLogPrefix() + "-getConnection()");
				// 读取中间表数据
				ResultSet resultSet = getResultSet(con);
				log.info(getLogPrefix() + "-getResultSet()");
				// 将数据转换成list
				List<Object> list = getResultList(resultSet);
				log.info(getLogPrefix() + "-getResultList()");
				if (list != null && list.size() > 0) {
					// 发送数据到IBS
					sendToIbs(list);
					log.info(getLogPrefix() + " sendToIbs.list : " + list.toString());
					log.info(getLogPrefix() +"-updateData_start");
					updateData(con,resultSet);
					log.info(getLogPrefix() +"-updateData_end");
					list.clear();
				}
				log.info(getLogPrefix() + "完成！");
				con.commit();
				if (resultSet != null) {
				    resultSet.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(getLogPrefix() + " error : " + e.getMessage());
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
						log.error(getLogPrefix() + " error : " + e.getMessage());
					}
				}
			}
		}
	}
}
