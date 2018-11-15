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
* 入库拒收Gsp反馈业务处理
*
*/
public class JmsRkjsGspFeedbackListener extends BaseTimer{
	@SuppressWarnings("deprecation")
	Category log = Category.getInstance(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 7717206464402616208L;

	@Override
	protected long getScheduleTime(JmsBean jmsBean) {
		return jmsBean.getTime();
	}

	@Override
	protected String getLogPrefix() {
		return "入库拒收Gsp反馈";
	}

	@Override
	protected String getRequestName() {
		return JmsConstant.GSPFEEDBACK;
	}

	@Override
	protected String getQuerySql() {
		return "select * from INF_JL_RKJS where zt is null or zt = 'N'";
	}

	/**
	 * 从消息队列中提取数据存入相应集合
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<Object> getResultList(ResultSet resultSet) throws Exception {
		List<Object> list  = new ArrayList<Object>();
		while(resultSet.next()){
			Map<Object,Object> map = new HashMap();
			// 业主内码 owner_id
			String ownerId = resultSet.getString("yez_id");
			// 日期 date
			String date = resultSet.getString("riqi_date");
			// 单据编号 document_code
			String documentCode = resultSet.getString("DANJ_NO");
			// 行号 line_num
			String lineNum = resultSet.getString("hanghao");
			// 商品 goods_id Goods
			String goodsId = resultSet.getString("SHANGP_ID");
			// 拒收人 reject_user
			String rejectUser = resultSet.getString("CAOZ_STAFF");
			// 拒收环节 reject_step
			String rejectStep = resultSet.getString("JUS_SQ");
			// 拒收原因 reject_reason
			String rejectReason = resultSet.getString("JUSHOU_REASON");
			// 批号 lot_id Lot
			String lotId = resultSet.getString("LOT");
			// 数量 goods_qty BigDecimal
			String goodsQty = resultSet.getString("NUM");
			// 单位内码 owner_code
			String ownerCode = resultSet.getString("DANW_ID");
			// 业务单据编号 busi_code
			String busiCode = resultSet.getString("YEWDJ_NO");
			
			map.put("ownerId", ownerId);
			map.put("date", date);
			map.put("documentCode", documentCode);
			map.put("lineNum", lineNum);
			map.put("goodsId", goodsId);
			map.put("rejectUser", rejectUser);
			map.put("rejectStep", rejectStep);
			map.put("rejectReason", rejectReason);
			map.put("lotId", lotId);
			map.put("goodsQty", goodsQty);
			map.put("ownerCode", ownerCode);
			map.put("busiCode",busiCode);
			// 业务类型
			map.put("busiType", "rkjsjl");
			list.add(map);
		}
		return list;
	}

	/**
	 * 修改入库拒收表中状态
	 */
	protected void updateData(Connection connection, ResultSet result) throws Exception {
		result.beforeFirst();
		PreparedStatement prepareStatement = null;
		while (result.next()) {
			//获取在中间表的存储状态
			String documentCode = result.getString("DANJ_NO");
			String updateSql = "update INF_JL_RKJS set zt = 'Y' where DANJ_NO = ?";
			log.info(" 入库拒收更新,数据写入jms，更新中间表状态,执行sql："+updateSql);
			prepareStatement = connection.prepareStatement(updateSql);
			prepareStatement.setString(1, documentCode);
			prepareStatement.executeUpdate();
			log.info(" 更新完成");
			if(prepareStatement != null){
				log.info("关闭statement");
				prepareStatement.close();
			}
		}
	}
	
}
