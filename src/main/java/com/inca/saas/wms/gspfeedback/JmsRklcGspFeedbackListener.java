package com.inca.saas.wms.gspfeedback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;

import com.inca.saas.wms.common.BaseTimer;
import com.inca.saas.wms.common.JmsConstant;
import com.inca.saas.wms.jms.JmsBean;

/**
* 入库冷藏Gsp反馈业务处理
*
*/
public class JmsRklcGspFeedbackListener extends BaseTimer{
	@SuppressWarnings("deprecation")
	Category log = Category.getInstance(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = -5842937458328055007L;

	@Override
	protected long getScheduleTime(JmsBean jmsBean) {
		return jmsBean.getTime();
	}

	@Override
	protected String getLogPrefix() {
		return "入库冷藏Gsp反馈";
	}

	@Override
	protected String getRequestName() {
		return JmsConstant.GSPFEEDBACK;
	}

	@Override
	protected String getQuerySql() {
		return "select * from INF_JL_RKLCJL where zt is null or zt = 'N'";
	}

	/**
	 * 从消息队列中提取数据存入相应集合
	 */
	protected List<Object> getResultList(ResultSet resultSet) throws Exception {
		List<Object> list = new ArrayList<>();
		while (resultSet.next()) {
			Map<Object, Object> map = new HashMap<>();
			// 日期 date
			String date = resultSet.getString("RIQI_DATE");
			// 业主内码 owner_id
			String ownerId = resultSet.getString("yez_id");
			// 业务单据编号 busi_code
			String busiCode = resultSet.getString("YEWDJ_NO");
			// 单据编号 document_code
			String documentCode = resultSet.getString("DANJ_NO");
			// 行号 line_num
			String lineNum = resultSet.getString("HANGHAO");
			// 商品 goods_id Goods
			String goodsId = resultSet.getString("SHANGP_ID");
			// 批号 lot_id Lot
			String lotId = resultSet.getString("LOT");
			// 冷藏品运输工具 transport_tool
			String transportTool = resultSet.getString("TRANSPORT_TOOL");
			// 发送地点 send_address
			String sendAddress = resultSet.getString("FAY_ADDRESS");
			// 启运日期 delivery_date
			String deliveryDate = resultSet.getString("QIY_DATE");
			// 运输方式 trans_mode
			String transMode = resultSet.getString("YUNS_WAY");
			// 温控方式 temp_mode
			String tempMode = resultSet.getString("WENK_WAY");
			// 到货时间 arrival_date
			String arrivalDate = resultSet.getString("DAOH_DATE");
			// 温控状况 temp_status
			String tempStatus = resultSet.getString("WENK_ZK");
			// 运输过程温度记录 temp_records
			String tempRecords = resultSet.getString("WEND_RECORDS");
			// 运输单位 trans_unit
			String transUnit = resultSet.getString("YUNS_DANW");
			// 冷藏品温度 cold_storage_temp
			String coleStorageTemp = resultSet.getString("LCP_TPRT");

			map.put("date", date);
			map.put("ownerId", ownerId);
			map.put("busiCode", busiCode);
			map.put("documentCode", documentCode);
			map.put("lineNum", lineNum);
			map.put("goodsId", goodsId);
			map.put("lotId", lotId);
			map.put("transportTool", transportTool);
			map.put("sendAddress", sendAddress);
			map.put("deliveryDate", deliveryDate);
			map.put("transMode",transMode);
			map.put("tempMode", tempMode);
			map.put("arrivalDate", arrivalDate);
			map.put("tempStatus", tempStatus);
			map.put("tempRecords", tempRecords);
			map.put("transUnit", transUnit);
			map.put("coleStorageTemp", coleStorageTemp);
			// 业务类型
			map.put("busiType", "rklcjl");
			list.add(map);
		}
		return list;
	}

	/**
	 * 修改入库冷藏表中状态
	 */
	protected void updateData(Connection connection, ResultSet result) throws Exception {
		result.beforeFirst();// 回到结果集第一行
		PreparedStatement prepareStatement = null;
		while (result.next()) {
			String documentCode = result.getString("DANJ_NO");
			String updateSql = "update INF_JL_RKLCJL set zt = 'Y' where DANJ_NO = ?";
			log.info(" 入库冷藏更新,数据写入jms，更新中间表状态,执行sql："+updateSql);
			prepareStatement = connection.prepareStatement(updateSql);
			prepareStatement.setString(1, documentCode);
			prepareStatement.executeUpdate();
			log.info("更新完成");
			if(prepareStatement != null){
				log.info("关闭statement");
				prepareStatement.close();
			}
		}
	}

}
