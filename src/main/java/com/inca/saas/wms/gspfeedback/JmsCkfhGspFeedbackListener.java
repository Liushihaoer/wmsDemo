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
 * 出库复核gsp反馈业务处理
 *
 */
public class JmsCkfhGspFeedbackListener extends BaseTimer {
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
		return "处理出库复核gsp反馈";
	}

	@Override
	protected String getRequestName() {
		return JmsConstant.GSPFEEDBACK;
	}

	@Override
	protected String getQuerySql() {
		return "select * from inf_jl_ckfh where zt is null or zt = 'N'";
	}

	/**
	 * 从消息队列中提取数据存入相应集合
	 */
	protected List<Object> getResultList(ResultSet resultSet) throws Exception {
		List<Object> list = new ArrayList<>();
		while (resultSet.next()) {
			Map<Object, Object> map = new HashMap<>();
			// 单据编号
			String documentCode = resultSet.getString("YEWDJ_NO");
			// 业主内码
			String ownerId = resultSet.getString("YEZ_ID");
			// 商品内码
			String goods = resultSet.getString("SHANGP_ID");
			// 复核人员
			String reviewUser = resultSet.getString("ZUOY_STAFF");
			// 验收评定
			String checkEvaluate = resultSet.getString("YANS_RLT");
			// 批号
			String lot = resultSet.getString("LOT");
			// 复核时间
			String reviewTime = resultSet.getString("ZUOYWC_DATE");
			// 数量
			String goodsQty = resultSet.getString("SL");

			map.put("documentCode", documentCode);
			map.put("ownerId", ownerId);
			map.put("goodsId", goods);
			map.put("reviewUser", reviewUser);
			map.put("checkEvaluate", checkEvaluate);
			map.put("lot", lot);
			map.put("reviewTime", reviewTime);
			map.put("goodsQty", goodsQty.toString());
			// 业务类型
			map.put("busiType", "ckfhjl");
			list.add(map);
		}
		return list;
	}

	/**
	 * 修改出库复核表中状态
	 */
	protected void updateData(Connection connection, ResultSet result) throws Exception {
		result.beforeFirst();// 回到结果集第一行
		PreparedStatement prepareStatement = null;
		while (result.next()) {
			String djbh = result.getString("YEWDJ_NO");
			String spid = result.getString("SHANGP_ID");
			String updateSql = "update inf_jl_ckfh set zt = 'Y' where YEWDJ_NO = ? and SHANGP_ID = ?";
			log.info(" 出库复核更新,数据写入jms，更新中间表状态,执行sql：" + updateSql);
			prepareStatement = connection.prepareStatement(updateSql);
			prepareStatement.setString(1, djbh);
			prepareStatement.setString(2, spid);
			prepareStatement.executeUpdate();
			log.info("更新完成");
			if(prepareStatement != null){
				log.info("关闭statement");
				prepareStatement.close();
			}
		}
	}

}
