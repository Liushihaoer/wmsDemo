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
* 在库养护Gsp反馈业务处理
*
*/
public class JmsZkyhGspFeedbackListener extends BaseTimer{
	@SuppressWarnings("deprecation")
	Category log = Category.getInstance(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 20160025363704277L;

	@Override
	protected long getScheduleTime(JmsBean jmsBean) {
		return jmsBean.getTime();
	}

	@Override
	protected String getLogPrefix() {
		return "在库养护Gsp反馈";
	}

	@Override
	protected String getRequestName() {
		return JmsConstant.GSPFEEDBACK;
	}

	@Override
	protected String getQuerySql() {
		return "select * from INF_JL_ZKYH where zt is null or zt = 'N'";
	}

	/**
	 * 从消息队列中提取数据存入相应集合
	 */
	protected List<Object> getResultList(ResultSet resultSet) throws Exception {
		List<Object> list = new ArrayList<>();
		while (resultSet.next()) {
			Map<Object, Object> map = new HashMap<>();
			// 日期 date
			String date = (String) resultSet.getString("RIQI_DATE");
			// 单据编号 document_code
			String documentCode = (String) resultSet.getString("DANJ_NO");
			// 行号 line_num
			String lineNum = (String) resultSet.getString("HANGHAO");
			// 业主内码 owner_id
			String ownerId = (String) resultSet.getString("YEZ_ID");
			// 商品 goods_id Goods
			String goodsId = (String) resultSet.getString("SHANGP_ID");
			// 批号 lot_id Lot
			String lotId = (String) resultSet.getString("LOT");
			// 数量 goods_qty
			String goodsQty = (String) resultSet.getString("NUM");
			// 显示货位 pos_name
			String posName = (String) resultSet.getString("HUOW_ID");
			// 质量状况 goods_status
			String goodsStatus = (String) resultSet.getString("ZHIL_ZK");
			// 处理措施 treatment_method
			String treatmentMethod = (String) resultSet.getString("CLYJ");
			// 抽检数量 spot_check_num BigDecimal
			String spotCheckNum = (String) resultSet.getString("CHOUJ_NUM");
			// 玻屑数量 glass_crumbs_num
			String glassCrumbsNum = (String) resultSet.getString("KJYW_NUM_BX");
			// 白块数量 white_num
			String whiteNum = (String) resultSet.getString("KJYW_NUM_BK");
			// 纤维数量 fiber_num
			String fiberNum = (String) resultSet.getString("KJYW_NUM_XW");
			// 白点数量 flake_num
			String flakeNum = (String) resultSet.getString("KJYW_NUM_BD");
			// 其他数量 other_num
			String otherNum = (String) resultSet.getString("KJYW_NUM_QT");
			// 不合格率 failure_rate BigDecimal
			String failureRate = (String) resultSet.getString("CHOUJ_BUHGL");
			// 购进日期 in_date
			String inDate = (String) resultSet.getString("GJRQ");
			// 养护员 maintain_user
			String maintainUser = (String) resultSet.getString("SHENH_STAFF");
			// 养护类型 maintain_type
			String maintainType = (String) resultSet.getString("TYPE");
			// 序号 seq_no
			String seqNo = (String) resultSet.getString("SEQNO");
			// 备注  实体类中没有  
			String remark = (String) resultSet.getString("REMARK");
			
			map.put("date", date);
			map.put("documentCode", documentCode);
			map.put("lineNum", lineNum);
			map.put("ownerId", ownerId);
			map.put("goodsId", goodsId);
			map.put("lotId", lotId);
			map.put("goodsQty", goodsQty);
			map.put("posName", posName);
			map.put("goodsStatus", goodsStatus);
			map.put("treatmentMethod", treatmentMethod);
			map.put("spotCheckNum",spotCheckNum);
			map.put("glassCrumbsNum", glassCrumbsNum);
			map.put("whiteNum", whiteNum);
			map.put("fiberNum", fiberNum);
			map.put("flakeNum", flakeNum);
			map.put("otherNum", otherNum);
			map.put("failureRate", failureRate);
			map.put("inDate", inDate);
			map.put("maintainUser", maintainUser);
			map.put("maintainType", maintainType);
			map.put("seqNo", seqNo);
			map.put("remark", remark);
			// 业务类型
			map.put("busiType", "zkyhjl");
			list.add(map);
		}
		return list;
	}

	/**
	 * 修改在库养护表中状态
	 */
	protected void updateData(Connection connection, ResultSet result) throws Exception {
		result.beforeFirst();// 回到结果集第一行
		PreparedStatement prepareStatement = null;
		while (result.next()) {
			String documentCode = result.getString("DANJ_NO");
			String updateSql = "update INF_JL_ZKYH set zt = 'Y' where DANJ_NO = ?";
			log.info(" 在库养护更新,数据写入jms，更新中间表状态,执行sql："+updateSql);
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
