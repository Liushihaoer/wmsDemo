package com.inca.saas.wms.gspfeedback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inca.saas.wms.common.BaseTimer;
import com.inca.saas.wms.common.JmsConstant;
import com.inca.saas.wms.jms.JmsBean;

/**
 * 
 * @ClassName:JmsShysGspFeedbackListener
 * @Description:查询入库收货验收记录表
 * @author:liush
 * @date:2017年7月5日 下午14:35:35
 *
 */
public class JmsShysGspFeedbackListener extends BaseTimer {

	final Log log = LogFactory.getLog(getClass());

	private static final long serialVersionUID = 1L;

	@Override
	protected long getScheduleTime(JmsBean jmsBean) {
		return jmsBean.getTime();
	}

	@Override
	protected String getLogPrefix() {
		return "处理入库收货验收反馈";
	}

	@Override
	protected String getQuerySql() {
		return "select jl.*,dj.yuany from inf_jl_rkshys jl left join (select distinct djbh, spid, yuany from jzt_rkjxdj) dj on jl.yewdj_no = dj.djbh and jl.shangp_id = dj.spid where jl.zt is null or jl.zt = 'N'";
	}

	@Override
	protected String getRequestName() {
		return JmsConstant.GSPFEEDBACK;
	}

	@Override
	protected void updateData(Connection con, ResultSet resultSet) throws Exception {
		resultSet.beforeFirst();// 回到结果集第一行
		PreparedStatement prepareStatement = null;
		while (resultSet.next()) {
			String danj_no = resultSet.getString("danj_no");
			String hanghao = resultSet.getString("hanghao");
			String updateSql = "update inf_jl_rkshys set zt = 'Y' where danj_no = ? and hanghao = ?";
			prepareStatement = con.prepareStatement(updateSql);
			prepareStatement.setString(1, danj_no);
			prepareStatement.setString(2, hanghao);
			log.info("入库收货验收反馈,更新中间表状态,执行sql：" + updateSql + ",danj_no = " + danj_no + ",hanghao = " + hanghao);
			prepareStatement.executeUpdate();
			if (prepareStatement != null) {
				log.info("关闭statement");
				prepareStatement.close();
			}
		}
	}

	@Override
	protected List<Object> getResultList(ResultSet resultSet) throws Exception {
		List<Object> list = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while (resultSet.next()) {
			Map<Object, Object> map = new HashMap<>();
			String riqi_date = resultSet.getDate("riqi_date") == null ? "" : resultSet.getDate("riqi_date").toString();// 日期
			String kaip_time = "";
			Timestamp kaipTime = resultSet.getTimestamp("kaip_time");
			
			if (kaipTime != null) {
				kaip_time = format.format(kaipTime);
			}
//			String kaip_time = resultSet.getDate("kaip_time") == null ? "" : resultSet.getDate("kaip_time").toString();// 收货时间
			String yewdj_no = resultSet.getString("yewdj_no");// 业务单据编号
			String danj_no = resultSet.getString("danj_no");// 单据编号
			String hanghao = resultSet.getString("hanghao");// 行号
			String danw_id = resultSet.getString("danw_id");// 单位内码
			String shangp_id = resultSet.getString("shangp_id");// 商品内码
			String shij_num = resultSet.getString("shij_num");// 实际数量
			String price = resultSet.getString("price");// 单价
			String amount = resultSet.getString("amount");// 金额
			String pihao = resultSet.getString("lot");// 批号
			String shengchan_date = resultSet.getDate("shengchan_date") == null ? ""
					: resultSet.getDate("shengchan_date").toString();// 生产日期
			String youx_date = resultSet.getDate("youx_date") == null ? "" : resultSet.getDate("youx_date").toString();// 有效期
			String caigou_staff = resultSet.getString("caigou_staff");// 采购员
			String shouh_staff = resultSet.getString("shouh_staff");// 收货员
			String shouh_result = resultSet.getString("shouh_result");// 收货结论
			String zhij_staff = resultSet.getString("zhij_staff");// 验收员
			String zhij_date = "";
			Timestamp zhijDate = resultSet.getTimestamp("zhij_date");
			if (zhijDate != null) {
				zhij_date = format.format(zhijDate);
			}
//			String zhij_date = resultSet.getDate("zhij_date") == null ? "" : resultSet.getDate("zhij_date").toString();// 验收日期
			String yans_rlt = resultSet.getString("yans_rlt");// 验收评定
			String yans_result = resultSet.getString("yans_result");// 验收结论
			String chul_fanga = resultSet.getString("chul_fanga");// 处理措施
			String ruk_type = resultSet.getString("ruk_type");// 入库类型
			String jushou_reason = resultSet.getString("jushou_reason");// 拒收(待验)原因
			String zuoy_staff_1 = resultSet.getString("zuoy_staff_1");// 验收员1（特药）
			String zuoy_staff_2 = resultSet.getString("zuoy_staff_2");// 验收员2（特药）
			String yuany = resultSet.getString("yuany");// 退货原因

			map.put("riqi_date", riqi_date);
			map.put("kaip_time", kaip_time);
			map.put("yewdj_no", yewdj_no);
			map.put("danj_no", danj_no);
			map.put("hanghao", hanghao);
			map.put("danw_id", danw_id);
			map.put("shangp_id", shangp_id);
			map.put("shij_num", shij_num);
			map.put("price", price);
			map.put("amount", amount);
			map.put("pihao", pihao);
			map.put("shengchan_date", shengchan_date);
			map.put("youx_date", youx_date);
			map.put("caigou_staff", caigou_staff);
			map.put("shouh_staff", shouh_staff);
			map.put("jushou_reason", jushou_reason);
			map.put("zuoy_staff_1", zuoy_staff_1);
			map.put("zuoy_staff_2", zuoy_staff_2);
			if (ruk_type != null && !"".equals(ruk_type)) {
				if (ruk_type.equals("1")) {
					ruk_type = "购进入库";
				} else if (ruk_type.equals("4")) {
					ruk_type = "销退入库";
				}
			}
			map.put("ruk_type", ruk_type);
			if (shouh_result != null && !"".equals(shouh_result)) {
				if (shouh_result.equals("1")) {
					shouh_result = "收货建议入合格库";
				} else if (shouh_result.equals("2")) {
					shouh_result = "拒收";
				} else if (shouh_result.equals("3")) {
					shouh_result = "待处理";
				} else if (shouh_result.equals("4")) {
					shouh_result = "收货建议入不合格库";
				}
			}
			map.put("shouh_result", shouh_result);

			map.put("zhij_staff", zhij_staff);
			map.put("zhij_date", zhij_date);

			if (yans_rlt != null && !"".equals(yans_rlt)) {
				if (yans_rlt.equals("1")) {
					yans_rlt = "合格";
				} else if (yans_rlt.equals("2")) {
					yans_rlt = "不合格";
				} else if (yans_rlt.equals("3")) {
					yans_rlt = "入库待验";
				} else if (yans_rlt.equals("4")) {
					yans_rlt = "拒收";
				}
			}
			map.put("yans_rlt", yans_rlt);

			if (yans_result != null && !"".equals(yans_result)) {
				if (yans_result.equals("1")) {
					yans_result = "合格";
				} else if (yans_result.equals("2")) {
					yans_result = "不合格";
				} else if (yans_result.equals("3")) {
					yans_result = "待复验";
				}
			}
			map.put("yans_result", yans_result);

			if (chul_fanga != null && !"".equals(chul_fanga)) {
				if (chul_fanga.equals("1")) {
					chul_fanga = "入合格品库";
				} else if (chul_fanga.equals("2")) {
					chul_fanga = "入退货库";
				} else if (chul_fanga.equals("3")) {
					chul_fanga = "拒收";
				} else if (chul_fanga.equals("4")) {
					chul_fanga = "待复验";
				} else if (chul_fanga.equals("5")) {
					chul_fanga = "入不合格品库";
				}
			}
			map.put("chul_fanga", chul_fanga);

			// 业务类型
			map.put("busiType", "rkshys");
			// 退货原因
			map.put("yuany", yuany);
			list.add(map);
		}
		return list;
	}

}
