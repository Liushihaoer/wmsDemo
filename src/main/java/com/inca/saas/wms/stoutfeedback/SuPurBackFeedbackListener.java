package com.inca.saas.wms.stoutfeedback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;

import com.inca.saas.wms.common.BaseTimer;
import com.inca.saas.wms.common.JmsConstant;
import com.inca.saas.wms.jms.JmsBean;

/**
 * 采购退货反馈
 * 
 * @author liush
 * 
 */
public class SuPurBackFeedbackListener extends BaseTimer {
    Category log = Category.getInstance(getClass());

    private static final long serialVersionUID = 1L;

    @Override
    protected long getScheduleTime(JmsBean jmsBean) {
        return jmsBean.getTime();
    }

    @Override
    protected String getLogPrefix() {
        return "SuPurBackFeedbackListener:采购退货反馈：";
    }

    @Override
    protected String getRequestName() {
        return JmsConstant.SuPurBackFEEDBACK;
    }

    @Override
    protected String getQuerySql() {
        return "select * from inf_erp_imp_ck_bill where yew_type = '购进退出' and (nvl(zt,'N') = 'N' or (zt = 'Y' and process = '0'))";
    }

    @Override
    protected List<Object> getResultList(ResultSet resultSet) throws Exception {
        List<Object> list = new ArrayList<>();
        while (resultSet.next()) {
            String djbh = resultSet.getString("djbh");// 单据编号
            String dwbh = resultSet.getString("dwbh");// 单位内码
            Date rq = resultSet.getDate("rq");// 日期
            String ywy = resultSet.getString("ywy");// 业务员
            String scf = resultSet.getString("scf");// 上传方
            Integer Dj_sort = resultSet.getInt("Dj_sort");// 行号
            Integer Dj_sort_y = resultSet.getInt("Dj_sort_y");// 原细单ID
            String spid = resultSet.getString("spid");// 分公司商品内码
            String shl = resultSet.getString("shl");// 数量
            String dj = resultSet.getString("dj");// 单价
            String hsje = resultSet.getString("hsje");// 实际结算价
            String ph = resultSet.getString("ph");// 批号
            String baozhiqi = resultSet.getString("baozhiqi");// 生产日期
            String sxrq = resultSet.getString("sxrq");// 有效期至
            final String yew_type = resultSet.getString("yew_type");
            String Sf_zp = resultSet.getString("Sf_zp");//
            String thfs = resultSet.getString("thfs");
            String hesdj = resultSet.getString("hesdj");
            String phyq = resultSet.getString("phyq");
            String yuany = resultSet.getString("yuany");
            // 采购退货充红标识.为X代表整单充红. liush 2018-06-06
            String sf_ch = resultSet.getString("sf_ch");
            String dckhcbj = resultSet.getString("dckhcbj");
            String zcqqsh = resultSet.getString("zcqqsh");
            String zcqzzh = resultSet.getString("zcqzzh");

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("djbh", djbh);
            resultMap.put("dwbh", dwbh);
            resultMap.put("rq", rq);
            resultMap.put("ywy", ywy);
            resultMap.put("scf", scf);
            resultMap.put("Dj_sort", Dj_sort);
            resultMap.put("Dj_sort_y", Dj_sort_y);
            resultMap.put("spid", spid);
            resultMap.put("shl", shl);
            resultMap.put("dj", dj);
            resultMap.put("hsje", hsje);
            resultMap.put("ph", ph);
            resultMap.put("baozhiqi", baozhiqi);
            resultMap.put("sxrq", sxrq);
            resultMap.put("yew_type", yew_type);
            resultMap.put("Sf_zp", Sf_zp);
            resultMap.put("thfs", thfs);
            resultMap.put("hesdj", hesdj);
            resultMap.put("phyq", phyq);
            resultMap.put("yuany", yuany);
            resultMap.put("sf_ch", sf_ch);
            resultMap.put("dckhcbj", dckhcbj);
            resultMap.put("zcqqsh", zcqqsh);
            resultMap.put("zcqzzh", zcqzzh);
            list.add(resultMap);
        }

        return list;
    }

    @Override
    protected void updateData(Connection con, ResultSet resultSet) throws Exception {
        resultSet.beforeFirst();
        PreparedStatement preparedStatement = null;
        while (resultSet.next()) {
            String djbh = resultSet.getString("djbh");// 单据编号
            String dwbh = resultSet.getString("dwbh");// 单位内码
            Integer dj_sort = resultSet.getInt("Dj_sort");// 行号
            String spid = resultSet.getString("spid");// 分公司商品内码
            log.info("SuPurBackFeedbackListener.updateData,数据写入jms，更新中间表状态");
            String sql = "update inf_erp_imp_ck_bill set zt = 'Y',process = '0' where yew_type = '购进退出' and djbh = '"
                    + djbh + "' and Dj_sort = " + dj_sort + " and spid = '" + spid + "' and dwbh = '" + dwbh
                    + "' and nvl(process, '-1') != '1'";
            preparedStatement = con.prepareStatement(sql);
            log.info("SuPurBackFeedbackListener.updateData,数据写入jms，更新中间表状态,执行sql：" + sql);
            preparedStatement.executeUpdate();
            log.info("SuPurBackFeedbackListener.updateData,sql=" + sql + ",执行完成 =====================");
            if (preparedStatement != null) {
                log.info("关闭statement");
                preparedStatement.close();
            }
        }

    }

}
