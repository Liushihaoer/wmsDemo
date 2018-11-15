package com.inca.saas.wms.stout;

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
 *	出库业务处理
 * 
 * @author Administrator wangdongdong
 *
 */
public class JmsStoutListener extends HttpServlet {

	private static final long serialVersionUID = 1L;
	Category log = Category.getInstance(getClass());
	
	/**
	 * 	
		出库业务：inca.saas.wms.jzt.stout
		出库反馈：inca.saas.wms.jzt.stout.feedback
	 */
//	private String DESTINATIONNAME_STOUT = "inca.saas.wms.jzt.stout";
	private PreparedStatement preparedStatement = null;
	@Override
	public void init() throws ServletException {
		try {
			sycJmsStout();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void sycJmsStout() throws Exception {
		JmsListener jms = new JmsListener();
		String domain = jms.getJmsBean().getDomain();
		log.info("sycJmsStout-domain:" + domain);
		jms.registJmsListener(domain + ":" + JmsConstant.STOUT, new MessageListener() {
			@Override
			public void onMessage(Message message) {
				if (message instanceof TextMessage) {
					TextMessage text = (TextMessage) message;
					log.info("sycJmsStout. ===============text ===="+text);
					java.sql.Connection con = null;
					try {
						String Message = text.getText();
						// 转json获取业务类型
						JSONObject json = JSONObject.parseObject(Message);
						String data = json.getString("data");
						log.info("sycJmsStout. =============== data ====" + data);
						if(data == null || "".equals(data)){
							return;
						}
						JSONArray jsonArray = JSONArray.parseArray(data);
						con = JmsDbConnection.getInstance().getCon();
						con.setAutoCommit(false);
						for (int i = 0; i < jsonArray.size(); i++) {
							JSONObject datajson = jsonArray.getJSONObject(i);
							log.info("sycJmsStout. =============== datajson ====" + datajson);
							String busiType = datajson.getString("busiType");
							// listen数据转入数据库
							if (busiType.equals("gpcsDelivery")) {
								log.info("九州通物流配送单同步至物流中间表，获取message===" + Message);
								insertWmsGpcsDelivery(datajson,con);
								log.info("九州通物流配送单同步至物流中间表完成==================");
							} else if (busiType.equals("stLs")) {
								log.info("九州通物流报损单同步至物流中间表开始==================");
								insertWmsStlsDoc(datajson,con);
								log.info("九州通物流报损单同步至物流中间表完成==================");
							} else if (busiType.equals("suPur")) {
								log.info("九州通物流采退单同步至物流中间表开始==================");
								insertWmsSuPur(datajson,con);
								log.info("九州通物流采退单同步至物流中间表完成==================");
							}
						}
						log.info("处理出库数据统一提交事务:start");
						con.commit();
						log.info("处理出库数据统一提交事务:end");
					} catch (JMSException e) {
						log.error(e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						log.error(e.getMessage());
						e.printStackTrace();
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
	
	private void insertWmsGpcsDelivery(JSONObject datajson,Connection con)  throws Exception{
		String  busiType = datajson.getString("busiType");
		String  docId = datajson.getString("docId");
//		String  busiDate = datajson.getString("busiDate");
		Date   busiDate =  datajson.getDate("busiDate");
		String  createUser = datajson.getString("createUser");
		String  docmemo = datajson.getString("docmemo");
		String  retailId = datajson.getString("retailId");
		String  dtlId = datajson.getString("dtlId");
		String  goodsId = datajson.getString("goodsId");
		String  goodsQty = datajson.getString("goodsQty");
		String  unitPrice = datajson.getString("unitPrice");
		String  amountMoney = datajson.getString("amountMoney");
		String  lot = datajson.getString("lot");
		String  wmsOwnerCode= datajson.getString("wmsOwnerCode");
		String  wmsOwnerId= datajson.getString("wmsOwnerId");
		String  hanghao = datajson.getString("hanghao");
		String  ecode = datajson.getString("ecode");
//		String  prodDate = datajson.getString("prodDate");
//		String  documentCode= datajson.getString("documentCode");
//		String  createTime = datajson.getString("createTime");
//		String  accountsetId = datajson.getString("accountsetId");
//		String  stopReason = datajson.getString("stopReason");
//		String  goodsCode = datajson.getString("goodsCode");
//		String  goodsName = datajson.getString("goodsName");
//		String  invalidDate = datajson.getString("invalidDate");
//		String  oldCode = datajson.getString("oldCode");
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		String djbh = wmsOwnerCode + "XSD"+String.format("%08d", Integer.parseInt(docId));//业务编号+业务简拼+8位ID
		String dwbh = wmsOwnerCode +"MD"+ retailId;//单位内码
		String busiDate1 = sdf.format(busiDate);
//		Date busiDate1=sdf.parse(busiDate);
//		java.sql.Date rq = new  java.sql.Date(busiDate.getTime());//日期
//		java.sql.Date ontime =new java.sql.Date(busiDate.getTime());//时间
		String ywy = createUser; //业务员
		String username = createUser; //操作员
		String sf_tbj = "N"; //是否退补价（默认N）
		String thfs = "4";  //提货方式  默认为3     1 自提 3 托运 4 市内配送
		String scf = "1";    //默认为1  0 业务系统下传 1 第三方下传 2 LMIS 3 京东下传
		String sf_ffzp = "N";  // 是否发放赠品（默认N）
		String zpfffs = "1";  // 赠品发放方式 1 随货同行2 不随货同行 3 无
		String khbz = docmemo; //客户备注（对应单据备注字段，可为空）
		String ddlx = "1";  // 订单类型（销售发货传1，采购退货传4）  配送 暂时先传1
		String danjlx = "6";  //三方则默认为6 其中：1 西药 2 中药 3 计生 4 器械 5 原料药 6 第三方 7 赠品
		String fkfs = "0";  // 付款方式（默认0）
		String cgy = createUser; //采购员 
		String yez = wmsOwnerId;  //业主内码
		String dj_sort = hanghao;  //行号 暂时传细单id
		String spid = wmsOwnerCode + goodsId; // 分公司商品内码
		String shl = goodsQty;  //数量
		String dj = unitPrice;  //单价 
		String hsje = amountMoney; //含税金额
		String ph = lot;
		String yew_type = "2"; //（2销售出库，3采购退货） 暂时这样传 
		String zt = "N";
		String sf_gxht = "N"; //是否打印购销合同（默认N）
		String dckhcbj = "0"; //调出考核成本价
		String zgb_ltjgm = "000000"; //流通监管码（与客户沟通不处理，可默认000000）
		if (ecode != null && !"".equals(ecode)) {
		    zgb_ltjgm = ecode;
		}
		String lshj = "0";

		String	sql = "insert into jzt_ckjxdj(djbh,dwbh,rq,ontime,ywy,username,sf_tbj,thfs,scf,sf_ffzp,"
				+ "zpfffs,khbz,ddlx,danjlx,fkfs,cgy,yez,dj_sort,spid,shl,dj,hsje,ph,yew_type,zt,sf_gxht,dckhcbj,zgb_ltjgm,lshj) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		preparedStatement = con.prepareStatement(sql);
		preparedStatement.setString(1, djbh);
		preparedStatement.setString(2,dwbh );
		preparedStatement.setString(3, busiDate1);
		preparedStatement.setString(4, busiDate1);
		preparedStatement.setString(5, ywy);
		preparedStatement.setString(6, username);
		preparedStatement.setString(7, sf_tbj);
		preparedStatement.setString(8,thfs);
		preparedStatement.setString(9,scf );
		preparedStatement.setString(10,sf_ffzp );
		preparedStatement.setString(11,zpfffs );
		preparedStatement.setString(12,khbz );
		preparedStatement.setString(13,ddlx );
		preparedStatement.setString(14,danjlx );
		preparedStatement.setString(15,fkfs );
		preparedStatement.setString(16,cgy );
		preparedStatement.setString(17,yez );
		preparedStatement.setString(18,dj_sort );
		preparedStatement.setString(19,spid );
		preparedStatement.setString(20,shl );
		preparedStatement.setString(21,dj );
		preparedStatement.setString(22,hsje );
		preparedStatement.setString(23,ph );
		preparedStatement.setString(24,yew_type);
		preparedStatement.setString(25,zt );
		preparedStatement.setString(26,sf_gxht );
		preparedStatement.setString(27,dckhcbj );
		preparedStatement.setString(28,zgb_ltjgm );
		preparedStatement.setString(29,lshj );
		log.info("九州通配送单同步至物流中间表准备执行sql="+sql);
		preparedStatement.executeUpdate();
		
		log.info("关闭preparedStatement...");
		if(preparedStatement != null){
			preparedStatement.close();
		}
	}

	
	public void insertWmsStlsDoc(JSONObject dataJson, Connection con) throws Exception {
		log.info("报损单插入到中间表的dataJson======================" + dataJson);
		Date riqi = dataJson.getDate("RIQI_DATE");
		java.sql.Date sqlDate = new java.sql.Date(riqi.getTime());
		String DANJ_NO = dataJson.getString("DANJ_NO");
		String YEZ_ID = dataJson.getString("YEZ_ID");
		String SHENQ_STAFF = dataJson.getString("SHENQ_STAFF");
		String SHANGP_ID = dataJson.getString("SHANGP_ID");
		String LOT = dataJson.getString("LOT");
		String BSBS_NUM = dataJson.getString("BSBS_NUM");
		String SUNY_FLG = dataJson.getString("SUNY_FLG");
		String ZHIL_QK = dataJson.getString("ZHIL_QK");
		String BEIZHU = dataJson.getString("BEIZHU");
		String ZT = dataJson.getString("ZT");
		String insertSql = "insert into jzt_sfbsbs(RIQI_DATE,DANJ_NO,YEZ_ID,SHENQ_STAFF,SHANGP_ID,LOT,BSBS_NUM,SUNY_FLG,ZHIL_QK,BEIZHU,ZT) "
				+ " values(?,?,?,?,?,?,?,?,?,?,?)";
		preparedStatement = con.prepareStatement(insertSql);
		preparedStatement.setDate(1, sqlDate);
		preparedStatement.setString(2, DANJ_NO);
		preparedStatement.setString(3, YEZ_ID);
		preparedStatement.setString(4, SHENQ_STAFF);
		preparedStatement.setString(5, SHANGP_ID);
		preparedStatement.setString(6, LOT);
		preparedStatement.setString(7, BSBS_NUM);
		preparedStatement.setString(8, SUNY_FLG);
		preparedStatement.setString(9, ZHIL_QK);
		preparedStatement.setString(10, BEIZHU);
		preparedStatement.setString(11, ZT);
		preparedStatement.executeUpdate();

	}
	
	
	private void insertWmsSuPur(JSONObject datajson,Connection con)  throws Exception{
//		String  busiType = datajson.getString("busiType");
		String  docId = datajson.getString("docId");
//		String hanghao = datajson.getString("hanghao");
//		String  busiDate = datajson.getString("busiDate");
		Date busiDate =  datajson.getDate("busiDate");
		String  createUser = datajson.getString("createUser");
		String  docmemo = datajson.getString("docmemo");
		String  supplyId = datajson.getString("supplyId");
		// 采购退货增加充红处理,行号改为细单ID liush 2018-06-06
		String  hanghao = datajson.getString("dtlId");
		String  goodsId = datajson.getString("goodsId");
		String  goodsQty = datajson.getString("goodsQty");
		String  unitPrice = datajson.getString("unitPrice");
		String  amountMoney = datajson.getString("amountMoney");
		String  lot = datajson.getString("lot");
		String  wmsOwnerCode= datajson.getString("wmsOwnerCode");
		String  wmsOwnerId= datajson.getString("wmsOwnerId");
		String  backReason = datajson.getString("backReason");
		String  wmsStatus = datajson.getString("wmsStatus");
//		String  prodDate = datajson.getString("prodDate");
//		String  documentCode= datajson.getString("documentCode");
//		String  createTime = datajson.getString("createTime");
//		String  accountsetId = datajson.getString("accountsetId");
//		String  goodsCode = datajson.getString("goodsCode");
//		String  goodsName = datajson.getString("goodsName");
//		String  invalidDate = datajson.getString("invalidDate");
//		String  oldCode = datajson.getString("oldCode");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String djbh = wmsOwnerCode + "GTD"+String.format("%08d", Integer.parseInt(docId));//业务编号+业务简拼+8位ID
		String dwbh = wmsOwnerCode +"GYS"+ supplyId;//单位内码
		String rq = sdf.format(busiDate);
		String ontime = sdf.format(busiDate);
//			java.sql.Date rq = new  java.sql.Date(busiDate.getTime());//日期
//			java.sql.Date ontime =new java.sql.Date(busiDate.getTime());//时间
		String ywy = createUser; //业务员
		String username = createUser; //操作员(采退不为空)
		String sf_tbj = "N"; //是否退补价（默认N）
		String thfs = "4";  //提货方式  默认为3     1 自提 3 托运 4 市内配送
		String scf = "1";    //默认为1  0 业务系统下传 1 第三方下传 2 LMIS 3 京东下传
		String sf_ffzp = "N";  // 是否发放赠品（默认N）
		String zpfffs = "1";  // 赠品发放方式 1 随货同行2 不随货同行 3 无
		String khbz = docmemo; //客户备注（对应单据备注字段，可为空）
		String ddlx = "4";  // 订单类型（销售发货传1，采购退货传4）
		String danjlx = "6";  //三方则默认为6 其中：1 西药 2 中药 3 计生 4 器械 5 原料药 6 第三方 7 赠品
		String fkfs = "0";  // 付款方式（默认0）
		String cgy = createUser; //采购员 
		String yez = wmsOwnerId;  //业主内码
		String dj_sort = hanghao; // 行号
		String spid = wmsOwnerCode + goodsId; // 分公司商品内码
		String shl = goodsQty;  //数量
		String dj = unitPrice;  //单价 
		String hsje = amountMoney; //含税金额
		String ph = lot;// 
		String yspd = wmsStatus; // 验收评定 采退单物流评定状态字段
		String yuany = backReason; // 用数字表示,采购退出时不能为空.
		String yew_type = "3"; //（2销售出库，3采购退货）
		String zt = "N";
		String sf_gxht = "N"; //是否打印购销合同（默认N）
		String dckhcbj = "0"; //调出考核成本价
		String zgb_ltjgm = "000000"; //流通监管码（与客户沟通不处理，可默认000000）
		String lshj = "0";

		String	sql = "insert into jzt_ckjxdj(djbh,dwbh,rq,ontime,ywy,username,sf_tbj,thfs,scf,sf_ffzp,"
				+ "zpfffs,khbz,ddlx,danjlx,fkfs,cgy,yez,dj_sort,spid,shl,dj,hsje,ph,yspd,yuany,yew_type,zt,sf_gxht,dckhcbj,zgb_ltjgm,lshj) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		preparedStatement = con.prepareStatement(sql);
		preparedStatement.setString(1, djbh);
		preparedStatement.setString(2,dwbh );
		preparedStatement.setString(3, rq);
		preparedStatement.setString(4,ontime);
		preparedStatement.setString(5, ywy);
		preparedStatement.setString(6, username);
		preparedStatement.setString(7, sf_tbj);
		preparedStatement.setString(8,thfs);
		preparedStatement.setString(9,scf );
		preparedStatement.setString(10,sf_ffzp );
		preparedStatement.setString(11,zpfffs );
		preparedStatement.setString(12,khbz );
		preparedStatement.setString(13,ddlx );
		preparedStatement.setString(14,danjlx );
		preparedStatement.setString(15,fkfs );
		preparedStatement.setString(16,cgy );
		preparedStatement.setString(17,yez );
		preparedStatement.setString(18,dj_sort );
		preparedStatement.setString(19,spid );
		preparedStatement.setString(20,shl );
		preparedStatement.setString(21,dj );
		preparedStatement.setString(22,hsje );
		preparedStatement.setString(23,ph );
		preparedStatement.setString(24,yspd );
		preparedStatement.setString(25,yuany );
		preparedStatement.setString(26,yew_type);
		preparedStatement.setString(27,zt );
		preparedStatement.setString(28,sf_gxht );
		preparedStatement.setString(29,dckhcbj );
		preparedStatement.setString(30,zgb_ltjgm );
		preparedStatement.setString(31,lshj );
		log.info("九州通采退单同步至物流中间表准备执行sql="+sql);
		preparedStatement.executeUpdate();
		log.info("关闭preparedStatement...");
		if(preparedStatement != null){
			preparedStatement.close();
		}
	}
	
}
