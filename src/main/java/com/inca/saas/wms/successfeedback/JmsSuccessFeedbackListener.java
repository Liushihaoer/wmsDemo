package com.inca.saas.wms.successfeedback;

import java.sql.SQLException;
import java.sql.Statement;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Category;

import com.alibaba.fastjson.JSONObject;
import com.inca.saas.wms.common.JmsConstant;
import com.inca.saas.wms.jms.JmsDbConnection;
import com.inca.saas.wms.jms.JmsListener;

/**
* @author liushihao
* @version 创建时间：2017年12月27日 上午11:11:11
* erp业务处理完成后的回调
*/
public class JmsSuccessFeedbackListener extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	Category log = Category.getInstance(getClass());
	
	@Override
	public void init() throws ServletException {
		try {
			successFeedback();
		} catch (Exception e) {
			log.error("JmsSuccessFeedbackListener.init()",e);
			e.printStackTrace();
		}
	}
	
	public void successFeedback() throws Exception {
		JmsListener jms = new JmsListener();
		String domain = jms.getJmsBean().getDomain();
		log.info("successFeedback-domain:" + domain);
		jms.registJmsListener(domain + ":" + JmsConstant.SUCCESSFEEDBACK, new MessageListener() {
			@Override
			public void onMessage(Message message) {
				if (message instanceof TextMessage) {
					TextMessage text = (TextMessage) message;
					log.info("successFeedback ========  text  ====" + text);
					java.sql.Connection con = null;
					Statement statement = null;
					try {
						String stmessage = text.getText();
						JSONObject json = JSONObject.parseObject(stmessage);
						String data = json.getString("data");
						if(data == null || "".equals(data)){
							return;
						}
						con = JmsDbConnection.getInstance().getCon();
						con.setAutoCommit(false);
						JSONObject datajson = JSONObject.parseObject(data);
						log.info("successFeedback ========  datajson  ====" + datajson);
						String tableName = datajson.getString("tableName");
						String djbhList = datajson.getString("djbhList");
						if(djbhList.contains(",")){
							djbhList = djbhList.replace(",", "','");
						}
						
						String sql = "update " + tableName + " set zt= 'Y',process = '1' where djbh in ('" + djbhList + "')";
						if (tableName.equals("inf_erp_imp_rk_bill")) {
							sql = sql.replace("djbh", "cgddh");
						} else if ("inf_rk_shjscl".equals(tableName)) {
						    sql = sql.replace("djbh", "yewdj_no");
						}
						log.info("successFeedback ========  sql  ====" + sql);
						statement = con.createStatement();
						statement.executeUpdate(sql);
						log.info("successFeedback处理入库数据统一提交事务:start");
						con.commit();
						log.info("successFeedback处理入库数据统一提交事务:end");
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
					} finally {
						if (statement != null) {
							try {
								statement.close();
							} catch (SQLException e) {
								e.printStackTrace();
								log.error("successFeedback-finally-statement", e);
							}
						}
						if (con != null) {
							try {
								con.close();
							} catch (SQLException e) {
								e.printStackTrace();
								log.error("successFeedback-finally-connection", e);
							}
						}
					}
				}
			}
		});
		Thread.sleep(5000);
	}

}
