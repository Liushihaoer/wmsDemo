package com.inca.saas.wms.casenumber;

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

public class CaseNumberbackListener extends BaseTimer{
	
	private static final long serialVersionUID = -9018073489594518045L;
	
	@SuppressWarnings("deprecation")
	Category log = Category.getInstance(getClass());

	@Override
	protected long getScheduleTime(JmsBean jmsBean) {
		return jmsBean.getTime();
	}

	@Override
	protected String getLogPrefix() {
		return "箱号表反馈";
	}

	@Override
	protected String getRequestName() {
		return JmsConstant.CASENUMBER;
	}

	@Override
	protected String getQuerySql() {
		return "select * from inf_jzt_ckpxmxb where zt is null or zt = 'N'";
	}

	@Override                                                  
	protected List<Object> getResultList(ResultSet resultSet) throws Exception {
		List<Object> list = new ArrayList<>();
		while (resultSet.next()) {
			Map<Object, Object> map = new HashMap<>();
			String yezId = resultSet.getString("yez_id");
			String riqiDate = resultSet.getString("riqi_date");
			String yewdjNo = resultSet.getString("yewdj_no");
			String hangHao = resultSet.getString("hanghao");
			String shangpId = resultSet.getString("shangp_id");
			String lot = resultSet.getString("lot");
			String num = resultSet.getString("num");
			String liushBarcode = resultSet.getString("liush_barcode");
			String pingxNo = resultSet.getString("pingx_no");
			 
			map.put("yezId", yezId);
			map.put("riqiDate", riqiDate);
			map.put("yewdjNo", yewdjNo);
			map.put("hangHao", hangHao);
			map.put("shangpId", shangpId);
			map.put("lot", lot);
			map.put("num", num);
			map.put("liushBarcode", liushBarcode);
			map.put("pingxNo", pingxNo);
			list.add(map);
		}
		return list;
	}

	@Override
	protected void updateData(Connection connection, ResultSet result) throws Exception {
		result.beforeFirst();// 回到结果集第一行
		PreparedStatement prepareStatement = null;
		while (result.next()) {
			String yezId = result.getString("yez_id");
			String updateSql = "update inf_jzt_ckpxmxb set zt = 'Y' where yez_id = ?";
			log.info(" 箱号表更新,数据写入jms，更新中间表状态,执行sql："+updateSql);
			prepareStatement = connection.prepareStatement(updateSql);
			prepareStatement.setString(1, yezId);
			prepareStatement.executeUpdate();
			log.info("更新完成");
			if(prepareStatement != null){
				log.info("关闭statement");
				prepareStatement.close();
			}
		}
	}
}
