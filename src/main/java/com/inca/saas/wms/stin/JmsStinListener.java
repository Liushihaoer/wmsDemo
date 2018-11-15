package com.inca.saas.wms.stin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Category;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inca.saas.wms.common.JmsConstant;
import com.inca.saas.wms.jms.JmsBean;
import com.inca.saas.wms.jms.JmsDbConnection;
import com.inca.saas.wms.jms.JmsListener;

/**
 * 入库业务处理
 * 
 * @author Administrator wangdongdong
 *
 */
public class JmsStinListener extends HttpServlet {

	/**
	 * 入库业务：inca.saas.wms.jzt.stin
	 */
	private static final long serialVersionUID = 1L;
	Category log = Category.getInstance(getClass());
	private PreparedStatement preparedStatement = null;

//	private String DESTINATIONNAME_STIN = "inca.saas.wms.jzt.stin";

	@Override
	public void init() throws ServletException {
		try {
			sycJmsStin();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void sycJmsStin() throws Exception {
		JmsListener jms = new JmsListener();
		String domain = jms.getJmsBean().getDomain();
		log.info("sycJmsStin-domain:" + domain);
		jms.registJmsListener(domain + ":" + JmsConstant.STIN, new MessageListener() {
			@Override
			public void onMessage(Message message) {
				if (message instanceof TextMessage) {
					TextMessage text = (TextMessage) message;
					log.info("sycJmsStin ========   text ====" + text);
					java.sql.Connection con = null;
					try {
						String Message = text.getText();
						// 转json获取业务类型
						JSONObject json = JSONObject.parseObject(Message);
						String data = json.getString("data");
						if(data == null || "".equals(data)){
							return;
						}
						JSONArray jsonArray = JSONArray.parseArray(data);
						log.info("sycJmsStin ========   jsonArray ====" + jsonArray);
						con = JmsDbConnection.getInstance().getCon();
						con.setAutoCommit(false);
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						for (int i = 0; i < jsonArray.size(); i++) {
							JSONObject datajson = jsonArray.getJSONObject(i);
							log.info("sycJmsStin ========   datajson ====" + datajson);
							String busiType = datajson.getString("busiType");
							// listen数据转入数据库
							if ("suOrder".equals(busiType)) {
								insertWmsSuOrder(datajson, con, dateFormat, timeFormat);
							}
							if ("gpcsBack".equals(busiType)) {
								insertWmsGpcsBack(datajson, con, dateFormat, timeFormat);
							}
						}
						log.info("处理入库数据统一提交事务:start");
						con.commit();
						log.info("处理入库数据统一提交事务:end");
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

	public void insertWmsSuOrder(JSONObject datajson, Connection con, SimpleDateFormat dateFormat,
            SimpleDateFormat timeFormat) throws Exception {
		// 采购订单信息
		log.info("sycJmsStin ======= insertWmsSuOrder    datajson ===" + datajson);
		String suOrder = datajson.getString("busiType");
		String docId = datajson.getString("docId");
		String dtlId = datajson.getString("dtlId");
		String hanghao = datajson.getString("hanghao");
		String documentCode = datajson.getString("documentCode");
		Date busiDate = datajson.getDate("busiDate");
		String createUser = datajson.getString("createUser");
		String createTime = datajson.getString("createTime");
		String accountsetId = datajson.getString("accountsetId");
		String arrivalDate = datajson.getString("arrivalDate");
		String contactPerson = datajson.getString("contactPerson");
		String contactTelNo = datajson.getString("contactTelNo");
		String stopReason = datajson.getString("stopReason");
		String goodsId = datajson.getString("goodsId");
		String goodsCode = datajson.getString("goodsCode");
		String goodsName = datajson.getString("goodsName");
		String goodsQty = datajson.getString("goodsQty");
		String unitPrice = datajson.getString("unitPrice");
		String amountMoney = datajson.getString("amountMoney");
		String lot = datajson.getString("lot");
		String prodDate = datajson.getString("prodDate");
		String invalidDate = datajson.getString("invalidDate");

		String oldCode = datajson.getString("oldCode");
		String wmsOwnerCode = datajson.getString("wmsOwnerCode");
		String wmsOwnerId = datajson.getString("wmsOwnerId");

		String dwbh = datajson.getString("dwbh");// 单位内码(规则见货主单位表）
		String lxr = datajson.getString("lxr");// 联系人字段 : 直采业务为购进入库表中的单据编号, 正常采购流程为制单人   liush 171019
		int length = docId.length();
		if (length < 8) {
			for (int i = 0; i < 8 - length; i++) {
				docId = "0" + docId;
			}
		}

		String djbh = wmsOwnerCode + "CGD" + docId;// 业务编号+业务简拼+8位ID
													// (varchar2(14))
		String date = dateFormat.format(busiDate);
        String time = timeFormat.format(busiDate);
		String rq = date;// 日期
		String ontime = time;// 时间
		String rktype = "1";// 入库类型（采购传1，销退传4）
		String scf = "1";// 上传方(默认传1)
		// String lxr = contactPerson;//客户联系人
		if(lxr == null || "".equals(lxr)){
			lxr = createUser;
		}
		String shouhy = createUser;// 收货员
		String username = createUser;// 操作员
		String ywy = createUser;// 业务员
		String lxrdh = contactTelNo;// 联系人电话
		String thlb = null;// 退货类型
		String yez = wmsOwnerId;// 业主内码（不能为空） 请向我们索取
		String dj_sort = hanghao;// 行号
		String spid = wmsOwnerCode + goodsId;
		;// 分公司商品内码
		String shl = goodsQty;// 数量
		String jiansh = null;// 件数
		String lingsshl = null;// 零散数量
		String dj = unitPrice;// 单价
		String je = amountMoney;// 金额
		String pihao = lot;// 批号
		String baozhiqi = prodDate;// 生产日期
		String sxrq = invalidDate;// 有效期至
		String quyubh = null;// 区域编号
		String yspd = "1";// 验收评定（销退必填，根据质量状态转换，合格传1，不合格传2）
		String yuany = null;// 退货原因
		String yew_type = "1";// 业务类型(默认传1)
		String zt = "N";// 积放短状态(默认传N)

		String sql = "insert into jzt_rkjxdj(djbh,dwbh,rq,ontime,rktype,scf,lxr,shouhy,username,ywy,lxrdh,thlb,yez,dj_sort,"
				+ "spid,shl,jiansh,lingsshl,dj,je,pihao,baozhiqi,sxrq,quyubh,yspd,yuany,yew_type,zt) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";// jzt_rkjxdj_seq.nextval,

		preparedStatement = con.prepareStatement(sql);
		preparedStatement.setString(1, djbh);
		preparedStatement.setString(2, dwbh);
		preparedStatement.setString(3, rq);
		preparedStatement.setString(4, ontime);
		preparedStatement.setString(5, rktype);
		preparedStatement.setString(6, scf);
		preparedStatement.setString(7, lxr);
		preparedStatement.setString(8, shouhy);
		preparedStatement.setString(9, username);
		preparedStatement.setString(10, ywy);
		preparedStatement.setString(11, lxrdh);
		preparedStatement.setString(12, thlb);
		preparedStatement.setString(13, yez);
		preparedStatement.setString(14, dj_sort);
		preparedStatement.setString(15, spid);
		preparedStatement.setString(16, shl);
		preparedStatement.setString(17, jiansh);
		preparedStatement.setString(18, lingsshl);
		preparedStatement.setString(19, dj);
		preparedStatement.setString(20, je);
		preparedStatement.setString(21, pihao);
		preparedStatement.setString(22, baozhiqi);
		preparedStatement.setString(23, sxrq);
		preparedStatement.setString(24, quyubh);
		preparedStatement.setString(25, yspd);
		preparedStatement.setString(26, yuany);
		preparedStatement.setString(27, yew_type);
		preparedStatement.setString(28, zt);
		log.info("执行采购单新增sql...........start");
		preparedStatement.executeUpdate();
		log.info("执行采购单新增sql...........end");
		log.info("关闭preparedStatement...");
		if(preparedStatement != null){
			preparedStatement.close();
		}

	}

	/**
	 * 
	 * @Title:insertWmsGpcsBack
	 * @Description:配退单信息插入中间表
	 * @param datajson
	 * @return:void
	 * @date:2016年11月15日 上午10:48:42
	 */
	private void insertWmsGpcsBack(JSONObject datajson, Connection con, SimpleDateFormat dateFormat,
            SimpleDateFormat timeFormat) throws Exception {
		// 采购订单信息
		log.info("sycJmsStin =======  insertWmsGpcsBack    datajson ===" + datajson);
		String busiType = datajson.getString("busiType");
		String docId = datajson.getString("docId");
		String hanghao = datajson.getString("dtlId");
//		String hanghao = datajson.getString("hanghao");
		String documentCode = datajson.getString("documentCode");
		Date busiDate = datajson.getDate("busiDate");
		String createUser = datajson.getString("createUser");
		String createTime = datajson.getString("createTime");
		String accountsetId = datajson.getString("accountsetId");
		// String arrivalDate = datajson.getString("arrivalDate");
		// String contactPerson = datajson.getString("contactPerson");
		// String contactTelNo = datajson.getString("contactTelNo");
		String stopReason = datajson.getString("stopReason");
		String goodsId = datajson.getString("goodsId");
		String goodsCode = datajson.getString("goodsCode");
		String goodsName = datajson.getString("goodsName");
		String goodsQty = datajson.getString("goodsQty");
		String unitPrice = datajson.getString("unitPrice");
		String amountMoney = datajson.getString("amountMoney");
		String lot = datajson.getString("lot");
		String prodDate = datajson.getString("prodDate");
		String invalidDate = datajson.getString("invalidDate");
		String backReason = datajson.getString("backReason");

		String oldCode = datajson.getString("oldCode");
		String wmsOwnerCode = datajson.getString("wmsOwnerCode");
		String wmsOwnerId = datajson.getString("wmsOwnerId");
		String wmsStatus = datajson.getString("wmsStatus");

		String dwbh = datajson.getString("dwbh");// 单位内码(规则见货主单位表）
		int length = docId.length();
		if (length < 8) {
			for (int i = 0; i < 8 - length; i++) {
				docId = "0" + docId;
			}
		}

		String djbh = wmsOwnerCode + "XTD" + docId;// 业务编号+业务简拼+8位ID
													// (varchar2(14))
		String date = dateFormat.format(busiDate);
        String time = timeFormat.format(busiDate);
		String rq = date;// 日期
		String ontime = time;// 时间
		String rktype = "4";// 入库类型（采购传1，销退传4）
		String scf = "1";// 上传方(默认传1)
		String lxr = createUser;// 客户联系人(客户联系人，收货员，业务员都取制单人)
		String shouhy = createUser;// 收货员
		String username = createUser;// 操作员
		String ywy = createUser;// 业务员
		String lxrdh = "";// 联系人电话
		String thlb = null;// 退货类型
		String yez = wmsOwnerId;// 业主内码（不能为空） 请向我们索取
		String dj_sort = hanghao;// 行号
		String spid = wmsOwnerCode + goodsId;
		;// 分公司商品内码
		String shl = goodsQty;// 数量
		String jiansh = null;// 件数
		String lingsshl = null;// 零散数量
		String dj = unitPrice;// 单价
		String je = amountMoney;// 金额
		String pihao = lot;// 批号
		String baozhiqi = prodDate;// 生产日期
		String sxrq = invalidDate;// 有效期至
		String quyubh = null;// 区域编号
		String yspd = wmsStatus;// 验收评定 根据配退审批物流评定状态回填
		String yuany = backReason;// 退货原因
		String yew_type = "1";// 业务类型(默认传1)
		String zt = "N";// 积放短状态(默认传N)

		String sql = "insert into jzt_rkjxdj(djbh,dwbh,rq,ontime,rktype,scf,lxr,shouhy,username,ywy,lxrdh,thlb,yez,dj_sort,"
				+ "spid,shl,jiansh,lingsshl,dj,je,pihao,baozhiqi,sxrq,quyubh,yspd,yuany,yew_type,zt) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";// jzt_rkjxdj_seq.nextval,
		preparedStatement = con.prepareStatement(sql);
		preparedStatement.setString(1, djbh);
		preparedStatement.setString(2, dwbh);
		preparedStatement.setString(3, rq);
		preparedStatement.setString(4, ontime);
		preparedStatement.setString(5, rktype);
		preparedStatement.setString(6, scf);
		preparedStatement.setString(7, lxr);
		preparedStatement.setString(8, shouhy);
		preparedStatement.setString(9, username);
		preparedStatement.setString(10, ywy);
		preparedStatement.setString(11, lxrdh);
		preparedStatement.setString(12, thlb);
		preparedStatement.setString(13, yez);
		preparedStatement.setString(14, dj_sort);
		preparedStatement.setString(15, spid);
		preparedStatement.setString(16, shl);
		preparedStatement.setString(17, jiansh);
		preparedStatement.setString(18, lingsshl);
		preparedStatement.setString(19, dj);
		preparedStatement.setString(20, je);
		preparedStatement.setString(21, pihao);
		preparedStatement.setString(22, baozhiqi);
		preparedStatement.setString(23, sxrq);
		preparedStatement.setString(24, quyubh);
		preparedStatement.setString(25, yspd);
		preparedStatement.setString(26, yuany);
		preparedStatement.setString(27, yew_type);
		preparedStatement.setString(28, zt);
		log.info("执行配退单新增sql...........start");
		preparedStatement.executeUpdate();
		log.info("执行配退单新增sql...........end");
		log.info("关闭preparedStatement...");
		if(preparedStatement != null){
			preparedStatement.close();
		}
	}

}
