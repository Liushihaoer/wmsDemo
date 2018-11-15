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
 * 出库运输Gsp反馈业务处理
 *
 */
public class JmsCkysGspFeedbackListener extends BaseTimer {
	@SuppressWarnings("deprecation")
	Category log = Category.getInstance(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected long getScheduleTime(JmsBean jmsBean) {
		return jmsBean.getTime();
	}

	@Override
	protected String getLogPrefix() {
		return "处理出库运输gsp反馈";
	}

	@Override
	protected String getRequestName() {
		return JmsConstant.GSPFEEDBACK;
	}

	@Override
	protected String getQuerySql() {
		return "select * from INF_JL_CKYS where zt is null or zt = 'N'";
	}

	/**
	 * 从消息队列中提取数据存入相应集合
	 */
	protected List<Object> getResultList(ResultSet resultSet) throws Exception {
		List<Object> list = new ArrayList<>();
		while (resultSet.next()) {
			Map<Object, Object> map = new HashMap<>();
			// 发货时间
			String fac_time = resultSet.getString("fac_time");
			// 发货地址
			String deliveryAddress = resultSet.getString("fah_add");
			// 业主id
			String ownerId = resultSet.getString("yez_id");
			// 装车单号
			String truckCode = resultSet.getString("zhuangcd_no");
			// 单据编号
			String documentCode = resultSet.getString("yewdj_no");
			// 单位名称
			String Organization = resultSet.getString("danw_name");
			// 到货时间
			String arrivalTime = resultSet.getString("fanh_time");
			// 收货地址
			String receiptAddress = resultSet.getString("diz_phone");
			// 单位所在地
			String unitSite = resultSet.getString("danw_town");
			// 运输工具
			String transTool = resultSet.getString("cart_type");
			// 总件数
			String allNum = resultSet.getString("zjs");
			// 装箱数
			String wholeBox = resultSet.getString("zxs");
			// 拼箱数
			String joinBox = resultSet.getString("pxs");
			// 车牌号
			String licenseCode = resultSet.getString("chepai_no");
			// 经办人
			String operator = resultSet.getString("caoz_staff");
			// 托运名称
			String consign = resultSet.getString("tuoydw_name");
			// 状态
			String zt = resultSet.getString("zt");

			map.put("deliveryTime", fac_time);
			map.put("deliveryAddress", deliveryAddress);
			map.put("ownerId", ownerId);
			map.put("truckCode", truckCode);
			map.put("documentCode", documentCode);
			map.put("Organization", Organization);
			map.put("arrivalTime", arrivalTime);
			map.put("receiptAddress", receiptAddress);
			map.put("unitSite", unitSite);
			map.put("transTool", transTool);
			map.put("allNum", allNum.toString());
			map.put("wholeBox", wholeBox.toString());
			map.put("joinBox", joinBox.toString());
			map.put("licenseCode", licenseCode);
			map.put("operator", operator);
			map.put("consign", consign);
			map.put("zt", zt);
			// 业务类型
			map.put("busiType", "ckysjl");
			list.add(map);
		}
		return list;
	}

	/**
	 * 修改出库运输表中状态
	 */
	protected void updateData(Connection connection, ResultSet result) throws Exception {
		result.beforeFirst();// 回到结果集第一行
		PreparedStatement prepareStatement = null;
		while (result.next()) {
			String yewdj_no = result.getString("yewdj_no");
			String updateSql = "update INF_JL_CKYS set zt = 'Y' where yewdj_no = ?";
			log.info(" 出库运输更新,数据写入jms，更新中间表状态,执行sql：" + updateSql);
			prepareStatement = connection.prepareStatement(updateSql);
			prepareStatement.setString(1, yewdj_no);
			prepareStatement.executeUpdate();
			log.info("更新完成");
			if(prepareStatement != null){
				log.info("关闭statement");
				prepareStatement.close();
			}
		}
	}

}
