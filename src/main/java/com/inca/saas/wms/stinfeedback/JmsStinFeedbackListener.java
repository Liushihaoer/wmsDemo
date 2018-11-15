package com.inca.saas.wms.stinfeedback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;

import com.inca.saas.wms.common.BaseTimer;
import com.inca.saas.wms.common.JmsConstant;
import com.inca.saas.wms.jms.JmsBean;

/**
 * 入库反馈业务处理
 * 
 * @author wangdongdong
 *
 */
public class JmsStinFeedbackListener extends BaseTimer {
	Category log = Category.getInstance(getClass());
	private static final long serialVersionUID = 1L;
	
	@Override
	protected long getScheduleTime(JmsBean jmsBean) {
		return jmsBean.getTime();
	}

	@Override
	protected String getLogPrefix() {
		return "处理入库反馈";
	}

	@Override
	protected String getRequestName() {
		return JmsConstant.STINFEEDBACK;
	}

	@Override
    protected String getQuerySql() {
        return "select rkBill.* from inf_rk_shjscl jscl left join inf_erp_imp_rk_bill rkBill on jscl.yewdj_no = rkBill.cgddh where jscl.ruk_type = '1' and (nvl(jscl.zt,'N') = 'N' or (jscl.zt = 'Y' and jscl.process = '0'))"
                + " and rkBill.rktype = '1' and (nvl(rkBill.zt,'N') = 'N' or (rkBill.zt = 'Y' and rkBill.process = '0'))";
    }

	@Override
    protected void updateData(Connection con, ResultSet resultSet) throws Exception {
        resultSet.beforeFirst();// 回到结果集第一行
        Statement statement = null;
        while (resultSet.next()) {
            String cgddh = resultSet.getString("cgddh");
            String updateSql = "update inf_erp_imp_rk_bill set zt = 'Y',process = '1' where rktype = '1' and cgddh = '" + cgddh + "'";
            statement = con.createStatement();
            log.info("JmsStinFeedbackListener.updateData,updateSql : " + updateSql);
            statement.executeUpdate(updateSql);
            updateSql = "update inf_rk_shjscl set zt = 'Y', process = '1' where ruk_type = '1' and yewdj_no = '" + cgddh + "'";
            log.info("JmsStinFeedbackListener.updateData,updateSql : " + updateSql);
            statement.executeUpdate(updateSql);
            if (statement != null) {
                log.info("关闭statement");
                statement.close();
            }
        }
    }

	@Override
	protected List<Object> getResultList(ResultSet resultSet) throws Exception {
		List<Object> list = new ArrayList<>();
		while (resultSet.next()) {
			Map<Object, Object> map = new HashMap<>();
			String djbh = resultSet.getString("djbh");// 单据编号 rkd03000796888
			String dwbh = resultSet.getString("dwbh");// 单位内码 a53zb0
			String rq = resultSet.getString("rq");// 日期 2016-11-04
			String shy = resultSet.getString("shy");// 剩余空间 系统管理员
			String ywy = resultSet.getString("ywy");// 业务员 admin
			String zhijren = resultSet.getString("zhijren");// 质检员 系统管理员
			String scf = resultSet.getString("scf");// 上传方 第三方下传
			String cgddh = resultSet.getString("cgddh");// 入库通知单号 A53CGD00000822
			String rktype = resultSet.getString("rktype");// 入库类型 1
			String Dj_sort = resultSet.getString("Dj_sort");// 行号 1
			String dj_sort_y = resultSet.getString("dj_sort_y");// 1 ?
			String spid = resultSet.getString("spid");// 分公司商品内码 A534004
			String shl = resultSet.getString("shl");// 数量 10
			String dj = resultSet.getString("dj");// 单价 10
			String je = resultSet.getString("je");// 金额 100
			String ph = resultSet.getString("ph");// 批号 201611041700
			String sxrq = resultSet.getString("sxrq");// 生产日期 2016-11-04
			String baozhiqi = resultSet.getString("baozhiqi");// 有效期至 2018-10-31
			String yew_type = resultSet.getString("yew_type");// 业务类型 购进入库
			String Sf_zp = resultSet.getString("Sf_zp");// ""
			String yans_rlt = resultSet.getString("yans_rlt");// 验收评定 ：1 (1 合格 2
																// 不合格 3 入库待验 4
																// 拒收 5 待验确定)
			String zt = resultSet.getString("zt");// 状态 ""
			String kehdj_no = resultSet.getString("kehdj_no");//随货同行单号
			map.put("djbh", djbh);
			map.put("dwbh", dwbh);
			map.put("rq", rq);
			map.put("shy", shy);
			map.put("ywy", ywy);
			map.put("zhijren", zhijren);
			map.put("scf", scf);
			map.put("cgddh", cgddh);
			map.put("rktype", rktype);
			map.put("Dj_sort", Dj_sort);
			map.put("dj_sort_y", dj_sort_y);
			map.put("spid", spid);
			map.put("shl", shl);
			map.put("dj", dj);
			map.put("je", je);
			map.put("ph", ph);
			map.put("sxrq", sxrq);
			map.put("baozhiqi", baozhiqi);
			map.put("yew_type", yew_type);
			map.put("Sf_zp", Sf_zp);
			map.put("yans_rlt", yans_rlt);
			map.put("zt", zt);
			map.put("kehdj_no", kehdj_no);
			list.add(map);
		}
		return list;
	}
	
}
