package com.inca.saas.wms.stcontrast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Category;

import com.alibaba.fastjson.JSONObject;
import com.inca.saas.wms.common.JmsConstant;
import com.inca.saas.wms.jms.JmsBean;
import com.inca.saas.wms.jms.JmsDbConnection;
import com.inca.saas.wms.jms.JmsListener;
import com.inca.saas.wms.jms.TransData;
import com.inca.saas.wms.utils.SelectHelper;

/**
* @author sunweichao
* @version 创建时间：2016年11月23日 下午3:51:22
* 库存对照业务 接收IBS发送过来的查询库存对照请求  查询中间表数据同时反馈给jms
*/
public class JmsStContrastListener  extends HttpServlet {
	
	/**
	 * 库存对照业务：inca.saas.wms.jzt.stin
	 */
	private static final long serialVersionUID = 1L;
	Category log = Category.getInstance(getClass());
	
	@Override
	public void init() throws ServletException {
		try {
			synStContrast();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void synStContrast() throws Exception {
		JmsListener jms = new JmsListener();
		String domain = jms.getJmsBean().getDomain();
		log.info("synStContrast-domain:" + domain);
		jms.registJmsListener(domain + ":" + JmsConstant.STCONTRAST, new MessageListener() {
			@Override
			public void onMessage(Message message) {
				if (message instanceof TextMessage) {
					TextMessage text = (TextMessage) message;
					log.info("synStContrast ========   text ====" + text);
					java.sql.Connection con = null;
					try {
						String stmessage = text.getText();
						JSONObject json = JSONObject.parseObject(stmessage);
						String data = json.getString("data");
						JSONObject datajson = JSONObject.parseObject(data);
						// 获取inca商品信息
						String yez_id = datajson.getString("wmsOwnerId");
						con = JmsDbConnection.getInstance().getCon();
						String  sql = "select a.shangp_id, a.lot, a.kuc_state, sum(a.num) num, a.shengchan_date, a.youx_date "
								+ " from KC_SPPHHW_SC a where a.yez_id =   '"+yez_id
								+ "' and a.xuhao = (select max(to_number(b.xuhao)) num from KC_SPPHHW_SC b where b.yez_id = '"+yez_id+"'"
								+ " and b.shangp_id = a.shangp_id)"
								+ " group by a.shangp_id, a.lot, a.kuc_state, a.shengchan_date, a.youx_date";
						ResultSet resultSet = SelectHelper.getResultSet(con, sql);
						
						List<Object> list = getResultList(resultSet);
						if(list!=null && list.size()>0){
							sendToIbs(list);
						}
						con.commit();
					} catch (JMSException e) {
						e.printStackTrace();
						log.error(e.getMessage());
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
					} finally {
						if (con != null) {
							try {
								con.close();
							} catch (SQLException e) {
								e.printStackTrace();
								log.info(e.getMessage());
							}
						}
					}
				}
			}

		});
		Thread.sleep(5000);

	}
	

	protected List<Object> getResultList(ResultSet resultSet) throws Exception {
		List<Object> list = new ArrayList<>();
		while (resultSet.next()) {
			Map<Object, Object> map = new HashMap<>();
			String shangp_id = resultSet.getString("shangp_id"); 
			String lot = resultSet.getString("lot"); 
			String kuc_state =  resultSet.getString("kuc_state"); 
			String num =  resultSet.getString("num"); 
			Date shengchan_date =  resultSet.getDate("shengchan_date"); 
			Date youx_date =  resultSet.getDate("youx_date"); 
			map.put("shangp_id", shangp_id);
			map.put("lot", lot);
			map.put("kuc_state", kuc_state);
			map.put("num", num);
			map.put("shengchan_date", shengchan_date == null ? "" : shengchan_date.toString());
			map.put("youx_date", youx_date == null ? "" : youx_date.toString());
			
			list.add(map);
		}
		return list;
	}
	
	
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
		td.setIntent(JmsConstant.STCONTRASTBACK);
		td.setSetId(setCode);

		TextMessage msg = session.createTextMessage(td.toJson());
		producer.send(msg);
		Thread.sleep(1000 * 3);
		jmsCon.close();
	}
	
	
	
	

	

}
