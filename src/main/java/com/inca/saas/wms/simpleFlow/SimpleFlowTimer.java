package com.inca.saas.wms.simpleFlow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inca.saas.wms.common.BaseTimer;
import com.inca.saas.wms.common.JmsConstant;
import com.inca.saas.wms.jms.JmsBean;

/**
 * 
 * @ClassName:SimpleFlowTimer
 * @Description:简化流程定时读取任务
 * @author:lzt
 * @date:2016年11月9日 下午4:55:30
 *
 */
public class SimpleFlowTimer extends BaseTimer {

	private static final long serialVersionUID = 1L;

	@Override
	protected long getScheduleTime(JmsBean jmsBean) {
		return jmsBean.getTime();
	}

	@Override
	protected String getLogPrefix() {
		return "处理简化流程反馈";
	}

	@Override
	protected String getQuerySql() {
		return "select * from inf_jzt_gjrk where isdone = 'N' order by danj_no, hanghao asc";
	}

	@Override
	protected String getRequestName() {
		return JmsConstant.SIMPLEFLOWFEEDBACK;
	}

	@Override
	protected void updateData(Connection con, ResultSet resultSet) throws Exception {
		resultSet.beforeFirst();// 回到结果集第一行
		while (resultSet.next()) {
			String danj_no = resultSet.getString("danj_no");
			String shangp_no = resultSet.getString("shangp_no");
			String updateSql = "update inf_jzt_gjrk set isdone = " + "'Y' where danj_no = ? and shangp_no = ?";
			PreparedStatement prepareStatement = con.prepareStatement(updateSql);
			prepareStatement.setString(1, danj_no);
			prepareStatement.setString(2, shangp_no);
			prepareStatement.executeUpdate();
			if (prepareStatement != null) {
			    prepareStatement.close();
			}
		}
	}

	@Override
	protected List<Object> getResultList(ResultSet resultSet) throws Exception {
		List<Object> list = new ArrayList<>();
		while (resultSet.next()) {
			Map<Object, Object> map = new HashMap<>();
			String danj_no = resultSet.getString("danj_no");// 单据编号
			String hanghao = resultSet.getString("hanghao");// 行号
			String shangp_no = resultSet.getString("shangp_no");// 商品编号
			String gys = resultSet.getString("gys");// 供应商
			String lot = resultSet.getString("lot");// 批号

			String shengchan_date = resultSet.getDate("shengchan_date").toString();// 生产日期
			String youx_date = resultSet.getDate("youx_date").toString();// 有效期至

			String num = resultSet.getString("num");// 数量
			String price = resultSet.getString("price");// 单价
			String danw_name = resultSet.getString("danw_name");// 需求组织（相应的单位名称）
			String zongd_flg = resultSet.getString("zongd_flg");// 总店标识：0 总店 1
																// 分店
			String lians_danw_id = resultSet.getString("lians_danw_id");
			String riqi_date = "";//添加中间表入库日期   
			if (null != resultSet.getDate("riqi_date")) {
				riqi_date = resultSet.getDate("riqi_date").toString();
			}
			String zgb_ltjgm = resultSet.getString("zgb_ltjgm");
			if (zgb_ltjgm == null) {
			    zgb_ltjgm = "";
			}
			
			map.put("danj_no", danj_no);
			map.put("hanghao", hanghao);
			map.put("shangp_no", shangp_no);
			map.put("gys", gys);
			map.put("lot", lot);
			map.put("shengchan_date", shengchan_date);
			map.put("youx_date", youx_date);
			map.put("num", num);
			map.put("price", price);
			map.put("danw_name", danw_name);
			map.put("zongd_flg", zongd_flg);
			map.put("lians_danw_id", lians_danw_id);
			map.put("rq", riqi_date);
			map.put("zgb_ltjgm", zgb_ltjgm);
			list.add(map);
		}
		return list;
	}

}
