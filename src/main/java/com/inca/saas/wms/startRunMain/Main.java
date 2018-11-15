package com.inca.saas.wms.startRunMain;

import org.apache.log4j.Category;

import com.inca.saas.wms.casenumber.CaseNumberbackListener;
import com.inca.saas.wms.gspfeedback.JmsCkfhGspFeedbackListener;
import com.inca.saas.wms.gspfeedback.JmsCkysGspFeedbackListener;
import com.inca.saas.wms.gspfeedback.JmsRkjsGspFeedbackListener;
import com.inca.saas.wms.gspfeedback.JmsRklcGspFeedbackListener;
import com.inca.saas.wms.gspfeedback.JmsShysGspFeedbackListener;
import com.inca.saas.wms.gspfeedback.JmsZkyhGspFeedbackListener;
import com.inca.saas.wms.jms.JmsDbConnection;
import com.inca.saas.wms.jms.JmsListener;
import com.inca.saas.wms.pub.JmsPubListener;
import com.inca.saas.wms.simpleFlow.SimpleFlowTimer;
import com.inca.saas.wms.stcontrast.JmsStContrastListener;
import com.inca.saas.wms.stin.JmsStinListener;
import com.inca.saas.wms.stinfeedback.GpcsBackFeedbackListener;
import com.inca.saas.wms.stinfeedback.JmsStinFeedbackListener;
import com.inca.saas.wms.stout.JmsStoutListener;
import com.inca.saas.wms.stoutfeedback.JmsStoutFeedbackListener;
import com.inca.saas.wms.stoutfeedback.SuPurBackFeedbackListener;
import com.inca.saas.wms.successfeedback.JmsSuccessFeedbackListener;

public class Main {
    static Category log = Category.getInstance(Main.class);
    public static void main(String[] args) throws Exception {
        // 加载数据库
        try {
            JmsDbConnection jms = new JmsDbConnection();
            jms.initLmsBean();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsDbConnection error", ex);
        }
        // 加载JMS连接
        try {
            JmsListener jmsCon = new JmsListener();
            jmsCon.initLmsBean();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsListener error", ex);
        }
        // 加载基础数据
        try {
            JmsPubListener jmsPub = new JmsPubListener();
            jmsPub.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsPubListener error", ex);
        }
        // 加载stin
        try {
            JmsStinListener jmsStin = new JmsStinListener();
            jmsStin.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsStinListener error", ex);
        }
        // 加载入库反馈
        try {
            JmsStinFeedbackListener jmsBackListen = new JmsStinFeedbackListener();
            jmsBackListen.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsStinFeedbackListener error", ex);
        }
        // 加载配退反馈
        try {
            GpcsBackFeedbackListener gpcsBackListen = new GpcsBackFeedbackListener();
            gpcsBackListen.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("GpcsBackFeedbackListener error", ex);
        }
        // 加载出库正向流程信息
        try {
            JmsStoutListener jmsStout = new JmsStoutListener();
            jmsStout.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsStoutListener error", ex);
        }
        // 加载出库反馈
        try {
            JmsStoutFeedbackListener jmsStoutFeedbackListener = new JmsStoutFeedbackListener();
            jmsStoutFeedbackListener.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsStoutFeedbackListener error", ex);
        }
        // 加载采退反馈
        try {
            SuPurBackFeedbackListener suPurBackFeedbackListener = new SuPurBackFeedbackListener();
            suPurBackFeedbackListener.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("SuPurBackFeedbackListener error", ex);
        }
        // 简化流程反馈
        try {
            SimpleFlowTimer timer = new SimpleFlowTimer();
            timer.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("SimpleFlowTimer error", ex);
        }
        // 出库运输记录反馈
        try {
            JmsCkysGspFeedbackListener ckysTimer = new JmsCkysGspFeedbackListener();
            ckysTimer.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsCkysGspFeedbackListener error", ex);
        }
        // 出库复核记录反馈
        try {
            JmsCkfhGspFeedbackListener ckfhTimer = new JmsCkfhGspFeedbackListener();
            ckfhTimer.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsCkfhGspFeedbackListener error", ex);
        }
        // 入库拒收记录反馈
        try {
            JmsRkjsGspFeedbackListener rkjsTimer = new JmsRkjsGspFeedbackListener();
            rkjsTimer.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsRkjsGspFeedbackListener error", ex);
        }
        // 入库冷藏记录反馈
        try {
            JmsRklcGspFeedbackListener rklcTimer = new JmsRklcGspFeedbackListener();
            rklcTimer.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsRklcGspFeedbackListener error", ex);
        }
        // 在库养护记录反馈
        try {
            JmsZkyhGspFeedbackListener zkyhTimer = new JmsZkyhGspFeedbackListener();
            zkyhTimer.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsZkyhGspFeedbackListener error", ex);
        }
        // 入库收货验收记录反馈
        try {
            JmsShysGspFeedbackListener shysTimer = new JmsShysGspFeedbackListener();
            shysTimer.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsShysGspFeedbackListener error", ex);
        }
        // 库存对照
        try {
            JmsStContrastListener stContrastListener = new JmsStContrastListener();
            stContrastListener.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsStContrastListener error", ex);
        }
        // 箱号表反馈
        try {
            CaseNumberbackListener caseNumberbackListener = new CaseNumberbackListener();
            caseNumberbackListener.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("CaseNumberbackListener error", ex);
        }
        // erp处理成功后更新回传记录
        try {
            JmsSuccessFeedbackListener jmsSuccessFeedbackListener = new JmsSuccessFeedbackListener();
            jmsSuccessFeedbackListener.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("JmsSuccessFeedbackListener error", ex);
        }

        Thread.sleep(1000 * 60 * 60 * 24 * 360);

        System.out.println("hello");
    }

}
