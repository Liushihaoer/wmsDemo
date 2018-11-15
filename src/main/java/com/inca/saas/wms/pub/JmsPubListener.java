package com.inca.saas.wms.pub;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.inca.saas.wms.common.JmsConstant;
import com.inca.saas.wms.jms.JmsBean;
import com.inca.saas.wms.jms.JmsDbConnection;
import com.inca.saas.wms.jms.JmsListener;

/**
 * tomcat加载启动
 * 
 * @author Administrator wangdongdong
 *
 */
public class JmsPubListener extends HttpServlet {

	private static final long serialVersionUID = 1L;
	final Log log = LogFactory.getLog(getClass());

//	private String DESTINATIONNAME_GOODS = "inca.saas.wms.jzt.goods";
//	private String DESTINATIONNAME_COMPANY = "inca.saas.wms.jzt.company";

	private PreparedStatement preparedStatement = null;

	@Override
	public void init() throws ServletException {
		try {
			sycJmsGoods();
			sycJmsCompany();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void sycJmsGoods() throws Exception {
		JmsListener jms = new JmsListener();
		String domain = jms.getJmsBean().getDomain();
		log.info("sycJmsGoods-domain:" + domain);
		jms.registJmsListener(domain + ":" + JmsConstant.GOODS, new MessageListener() {
			@Override
			public void onMessage(Message message) {
				if (message instanceof TextMessage) {
					TextMessage text = (TextMessage) message;
					try {
						String goodsMessage = text.getText();
						log.info("九州通物流同步商品，获取goodsMessage==="+goodsMessage);
						insertWmsGoods(goodsMessage);
						log.info("九州通物流同步商品完成===============================");
					} catch (JMSException e) {
						log.error(e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						log.error(e.getMessage());
						e.printStackTrace();
					}
				}
			}
		});
		Thread.sleep(5000);

	}

	public void sycJmsCompany() throws Exception {
		JmsListener jms = new JmsListener();
		String domain = jms.getJmsBean().getDomain();
		log.info("sycJmsCompany-domain:" + domain);
		jms.registJmsListener(domain + ":" + JmsConstant.COMPANY, new MessageListener() {
			@Override
			public void onMessage(Message message) {
				if (message instanceof TextMessage) {
					TextMessage text = (TextMessage) message;
					try {
						String companyMessage = text.getText();
						log.info("九州通物流同步企业，获取companyMessage==="+companyMessage);
						insertWmsCompany(companyMessage);
						log.info("九州通物流同步企业完成==================================");
					} catch (JMSException e) {
						log.error(e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						log.error(e.getMessage());
						e.printStackTrace();
					}
				}
			}
		});
		Thread.sleep(5000);
	}

	public void insertWmsCompany(String companyMessage) throws Exception {
		JSONObject json = JSONObject.parseObject(companyMessage);
		String data = json.getString("data");
		if(data == null || "".equals(data)){
			return;
		}
		JSONObject dataJson = JSONObject.parseObject(data);
		log.info("九州通物流接口，获得企业datajson=============="+dataJson);
		String dwbh = dataJson.getString("dwbh");
		String danwbh = dataJson.getString("danwbh");
		String dwmch = dataJson.getString("dwmch");
		String dwbh_sj = dataJson.getString("dwbh_sj");
		String zjm = dataJson.getString("zjm");
		String lxr = dataJson.getString("lxr");
		String lxrdh = dataJson.getString("lxrdh");
		String beactive = dataJson.getString("beactive");
		String quyufl = dataJson.getString("quyufl");
		String kemuhao = dataJson.getString("kemuhao");
		String dzdh = dataJson.getString("dzdh");
		String printfa = dataJson.getString("printfa");
		String yez = dataJson.getString("yez");
		String sf_yez = dataJson.getString("sf_yez");
		String zt = dataJson.getString("zt");
		String sf_yh = dataJson.getString("sf_yh");
		String lastModifyTime = dataJson.getString("last_modify_time");
		String danwjc = dataJson.getString("danwjc");
		java.sql.Connection con = null;
		try {
			log.info("九州通同步企业开始获取中间库链接================");
			con = JmsDbConnection.getInstance().getCon();
			log.info("九州通同步企业获取中间库链接完成，con================"+con);
			String querySql = "select * from jzt_mchk where dwbh = ? ";
			preparedStatement = con.prepareStatement(querySql);
			preparedStatement.setString(1, dwbh);
			ResultSet result = preparedStatement.executeQuery();
			if (result.next()) {
				int seqNo = result.getInt("seqno");
				String updateSql = "update jzt_mchk set dwbh = ? , danwbh = ? , dwmch = ? , dwbh_sj = ? "
						+ ", zjm = ? , lxr = ? , lxrdh = ? , beactive = ? , quyufl = ? , kemuhao = ? , dzdh =? "
						+ ", printfa = ? , yez = ? , sf_yez = ?  , zt = ? , sf_yh = ?, last_modify_time = ?, danwjc = ? where seqno = ?";
				log.info("九州通同步企业updateSql================"+updateSql);
				preparedStatement = con.prepareStatement(updateSql);
				preparedStatement.setInt(19, seqNo);
			} else {
				String insertSql = "insert into jzt_mchk(seqno,dwbh,danwbh,dwmch,dwbh_sj,zjm,lxr,lxrdh,beactive,quyufl,kemuhao,dzdh,printfa,yez,sf_yez,zt,sf_yh,last_modify_time,danwjc) "
						+ " values(jzt_mchk_seq.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				log.info("九州通同步企业insertSql================"+insertSql);
				preparedStatement = con.prepareStatement(insertSql);
			}
			preparedStatement.setString(1, dwbh);
			preparedStatement.setString(2, danwbh);
			preparedStatement.setString(3, dwmch);
			preparedStatement.setString(4, dwbh_sj);
			preparedStatement.setString(5, zjm);
			preparedStatement.setString(6, lxr);
			preparedStatement.setString(7, lxrdh);
			preparedStatement.setString(8, beactive);
			preparedStatement.setString(9, quyufl);
			preparedStatement.setString(10, kemuhao);
			preparedStatement.setString(11, dzdh);
			preparedStatement.setString(12, printfa);
			preparedStatement.setString(13, yez);
			preparedStatement.setString(14, sf_yez);
			preparedStatement.setString(15, zt);
			preparedStatement.setString(16, sf_yh);
			preparedStatement.setString(17, lastModifyTime);
			preparedStatement.setString(18, danwjc);
			preparedStatement.executeUpdate();
			log.info("九州通物流接口dwbh="+dwbh+"的企业同步至中间库，进行提交操作");
			con.commit();
			log.info("九州通物流接口dwbh="+dwbh+"的企业同步至中间库，提交完成");
		} catch (Exception e) {
			if (con != null) {
				con.rollback();
			}
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.close();
			}
		}

	}

	public void insertWmsGoods(String goodsMessage) throws Exception {
		JSONObject json = JSONObject.parseObject(goodsMessage);
		String data = json.getString("data");
		if(data == null || "".equals(data)){
			return;
		}
		JSONObject datajson = JSONObject.parseObject(data);
		log.info("九州通物流接口，获得商品datajson=============="+datajson);
		// 获取inca商品信息
		String goodsId = datajson.getString("goodsId");
		String goodscode = datajson.getString("goodsCode");
		String goodsname = datajson.getString("goodsname");
		String commodityName = datajson.getString("commodityName");
		String goodsOpcode = datajson.getString("goodsOpcode");
//		String goodsType = datajson.getString("goodsType");
		String goodsSpec = datajson.getString("goodsSpec");// 规格
		String prodArea = datajson.getString("prodArea");
		String factoryName = datajson.getString("factoryName");
		String approvalNo = datajson.getString("approvalNo");
		String goodsPackQty = datajson.getString("goodsPackQty");
		String goodsUnit = datajson.getString("goodsUnit");
		String medicineType = datajson.getString("medicineType");
		String goodsClass = datajson.getString("goodsClass");
		String defResaPrice = datajson.getString("defResaPrice");
		String storageCondition = datajson.getString("storageCondition");
		String validPeriod = datajson.getString("validPeriod");
		String superviseFlag = datajson.getString("superviseFlag");
		String caifLid = datajson.getString("caifLid");
		String giftFlag = datajson.getString("giftFlag");
		String importedFlag = datajson.getString("importedFlag");
		String oldCode = datajson.getString("oldCode");
		String wmsOwnerCode = datajson.getString("wmsOwnerCode");
		String wmsOwnerId = datajson.getString("wmsOwnerId");
		String zhuanhCoef = datajson.getString("zhuanhCoef");
		String lastModifyTime = datajson.getString("last_modify_time");
		String keyConserveFlag = datajson.getString("keyConserveFlag");
		String status = datajson.getString("status");
		// 生产厂家营业执照
		String makerYyzz = (String) datajson.get("makerYyzz");
		// 生产厂家营业执照有效期
		String makerYyzzyxq = (String) datajson.get("makerYyzzyxq");
		// 药品生产许可证号
		String makerLicense = (String) datajson.get("makerLicense");
		// 药品生产许可证有效期
		String makerLicenseYxq = (String) datajson.get("makerLicenseYxq");
		// 医疗器械生产企业许可证号
		String shcLicense = (String) datajson.get("shcLicense");
		// 医疗器械生产企业许可证有效期
		String shcLicenseYxq = (String) datajson.get("shcLicenseYxq");
		// 一类器械备案凭证号
		String YILQX_BAPZH = "";
		// 器械注册证有效期
		String xiwname = (String) datajson.get("xiwname");
		// 药品注册证效期至
		String ypzczYxqz = (String) datajson.get("ypzczYxqz");
		// 生产厂家gmp
		String makerGsp = (String) datajson.get("makerGsp");
		// gmp有效期至
		String gmpYxqz = (String) datajson.get("gmpYxqz");
		// 经营范围
		String shangpJyfw = (String) datajson.get("shangpJyfw");
		
		
		

		java.sql.Connection con = null;
		try {
			log.info("九州通同步商品开始获取中间库链接================");
			con = JmsDbConnection.getInstance().getCon();
			log.info("九州通同步商品获取中间库链接完成，con================"+con);
			Statement stm = null;
			stm = con.createStatement();
			// inca商品信息转化为wms中间表需要信息
			// ""代表业主编号 后续传
			String spid = wmsOwnerCode + goodsId;
			String spbh = wmsOwnerCode + goodscode.toUpperCase();
			// 存在某些商品名为空的问题,这里统一取通用名.
//			String spmch = commodityName;
			String tongym = goodsname;
			String zjm = goodsOpcode;
			String shpgg = goodsSpec;// 规格
			String shpchd = prodArea;
			String shengccj = factoryName;
			String pizhwh = approvalNo;
			String jlgg = goodsPackQty;
			String dw = goodsUnit;
			String jixing = "00";
			String jxsql = "select key from jzt_wms_options where optionname='medicinetype' and value='" + medicineType
					+ "'";
			ResultSet jxResultSet = stm.executeQuery(jxsql);
			if (jxResultSet.next()) {
				jixing = jxResultSet.getString("key");
				log.info("九州通同步商品执行剂型sql："+jxsql+"         .....,取到九州通对应值："+jixing);
			}else{
				log.info("九州通同步商品执行剂型sql："+jxsql+"          ..查询结果为空,取缺省值："+jixing);
			}
			String ypdl = "16";
			String ypdlsql = "select key from jzt_wms_options where optionname='goodsclass' and value='" + goodsClass
					+ "'";
			ResultSet ypdlResultSet = stm.executeQuery(ypdlsql);
			if (ypdlResultSet.next()) {
				ypdl = ypdlResultSet.getString("key");
				log.info("九州通同步商品执行商品大类sql："+ypdlsql+"        .....,取到九州通对应值："+ypdl);
			}else{
				log.info("九州通同步商品执行商品大类sql："+ypdlsql+"        ..查询结果为空,取缺省值："+ypdl);
			}
			String jyfw = "";
			String jyfwsql = "select key from jzt_wms_options where optionname='SHANGP_JYFW' and value='" + shangpJyfw
			        + "'";
			ResultSet jyfwResultSet = stm.executeQuery(jyfwsql);
			if (jyfwResultSet.next()) {
			    jyfw = jyfwResultSet.getString("key");
			    log.info("九州通同步商品执行商品经营范围sql："+jyfwsql+"        .....,取到九州通对应值："+jyfw);
			}else{
			    log.info("九州通同步商品执行商品经营范围sql："+jyfwsql+"        ..查询结果为空,取缺省值："+jyfw);
			}
			String lshj = defResaPrice;
			String cctj = "1";
			String cctjsql = "select key from jzt_wms_options where optionname='storagecondition' and value='"
					+ storageCondition + "'";
			ResultSet cctjResultSet = stm.executeQuery(cctjsql);
			if (cctjResultSet.next()) {
				cctj = cctjResultSet.getString("key");
				log.info("九州通同步商品执行储存条件sql："+cctjsql+"           .....,取到九州通对应值："+cctj);
			}else{
				log.info("九州通同步商品执行储存条件sql："+cctjsql+"           ..查询结果为空,取缺省值："+cctj);
			}
			String youxq = validPeriod;
			String sf_dzjg = superviseFlag;
			String caif_lid = caifLid;
			String sf_zp = giftFlag;
			String is_jkyp = importedFlag;
			String oldcode = oldCode;
			String yewlx = "2";
			String zt = "N";
			String yez = wmsOwnerId; // 业主内码 最后补充
			String kaipdw_min = "1";
			String zbz = "1";
			String beactive = "Y";// (商品活动状态默认传Y,表示活动状态.N表示被锁定) 活动状态根据erp商品状态赋值   liush 2018-05-14
			if (status != null && !"".equals(status)) {
			    beactive = status;
			}
			String zhuanh_coef = zhuanhCoef;
			String sf_zdkz = keyConserveFlag;

			String goodssql = "select * from jzt_spkfk where spid='" + spid + "'";
			ResultSet goodsResultSet = stm.executeQuery(goodssql);
			log.info("九州通同步商品查询中间表是否存在相同商品，执行sql："+goodssql);
			String sql = "";
			if (goodsResultSet.next()) {
				String seqno = goodsResultSet.getString("seqno");
				log.info("九州通同步商品查询中间表查到相同商品，主键seqno="+seqno+",执行更新语句！！！！！");
				sql = "update jzt_spkfk set spid= ? ,spbh = ? ,spmch = ? ,tongym = ? ,zjm = ? ,shpgg = ? ,"
						+ "shpchd = ? ,shengccj = ? ,pizhwh = ? ,jlgg = ? ,dw = ? ,jixing = ? ,ypdl = ? ,lshj = ? ,"
						+ "cctj  = ?  ,youxq = ? ,sf_dzjg = ? ,caif_lid = ? ,sf_zp = ? ,is_jkyp = ? ,oldcode = ? ,"
						+ "yewlx = ? ,zt = ? ,yez = ? ,kaipdw_min = ? ,zbz = ? ,beactive = ?, zhuanh_coef = ?,last_modify_time = ?,SF_ZDKZ = ? ,"
						+ "MAKER_YYZZ = ?, MAKER_YYZZYXQ = ?, MAKER_LICENSE = ?, MAKER_LICENSEYXQ = ?, SHC_LICENSE = ?, SHC_LICENSEYXQ = ?, "
						+ "YILQX_BAPZH = ?, XIWNAME = ?, YPZCZ_YXQZ = ?, MAKER_GSP = ?, GMP_YXQZ = ?, SHANGP_JYFW = ? where seqno = " + seqno + "";
			} else {
				log.info("九州通同步商品查询中间表没有查到相同商品，执行insert语句！！！！！");
				sql = "insert into jzt_spkfk(seqno,spid,spbh,spmch,tongym,zjm,shpgg,"
						+ "shpchd,shengccj,pizhwh,jlgg,dw,jixing,ypdl,lshj,"
						+ "cctj,youxq,sf_dzjg,caif_lid,sf_zp,is_jkyp,oldcode,yewlx,zt,yez,kaipdw_min,zbz,beactive,zhuanh_coef,last_modify_time,SF_ZDKZ,"
						+ "MAKER_YYZZ,MAKER_YYZZYXQ,MAKER_LICENSE,MAKER_LICENSEYXQ,SHC_LICENSE,SHC_LICENSEYXQ,YILQX_BAPZH,XIWNAME,YPZCZ_YXQZ,MAKER_GSP,GMP_YXQZ,SHANGP_JYFW) "
						+ "values(jzt_spkfk_seq.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			}

			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setString(1, spid);
			preparedStatement.setString(2, spbh);
			preparedStatement.setString(3, tongym);
			preparedStatement.setString(4, tongym);
			preparedStatement.setString(5, zjm);
			preparedStatement.setString(6, shpgg);
			preparedStatement.setString(7, shpchd);
			preparedStatement.setString(8, shengccj);
			preparedStatement.setString(9, pizhwh);
			preparedStatement.setString(10, jlgg);
			preparedStatement.setString(11, dw);
			preparedStatement.setString(12, jixing);
			preparedStatement.setString(13, ypdl);
			preparedStatement.setString(14, lshj);
			preparedStatement.setString(15, cctj);
			preparedStatement.setString(16, youxq);
			preparedStatement.setString(17, sf_dzjg);
			preparedStatement.setString(18, caif_lid);
			preparedStatement.setString(19, sf_zp);
			preparedStatement.setString(20, is_jkyp);
			preparedStatement.setString(21, oldcode);
			preparedStatement.setString(22, yewlx);
			preparedStatement.setString(23, zt);
			preparedStatement.setString(24, yez);
			preparedStatement.setString(25, kaipdw_min);
			preparedStatement.setString(26, zbz);
			preparedStatement.setString(27, beactive);
			preparedStatement.setString(28, zhuanh_coef);
			preparedStatement.setString(29, lastModifyTime);
			preparedStatement.setString(30, sf_zdkz);
			preparedStatement.setString(31, makerYyzz);
			preparedStatement.setString(32, makerYyzzyxq);
			preparedStatement.setString(33, makerLicense);
			preparedStatement.setString(34, makerLicenseYxq);
			preparedStatement.setString(35, shcLicense);
			preparedStatement.setString(36, shcLicenseYxq);
			preparedStatement.setString(37, YILQX_BAPZH);
			preparedStatement.setString(38, xiwname);
			preparedStatement.setString(39, ypzczYxqz);
			preparedStatement.setString(40, makerGsp);
			preparedStatement.setString(41, gmpYxqz);
			preparedStatement.setString(42, jyfw);
			preparedStatement.executeUpdate();
			log.info("九州通物流接口spid="+spid+"的商品同步至中间库，进行提交操作");
			con.commit();
			log.info("九州通物流接口spid="+spid+"的商品同步至中间库，提交完成");
		} catch (Exception e) {
			if (con != null) {
				con.rollback();
			}
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.close();
			}
		}

	}
	
}
