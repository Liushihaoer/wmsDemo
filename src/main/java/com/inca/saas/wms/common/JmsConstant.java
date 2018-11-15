package com.inca.saas.wms.common;

/**
 * 
 * @ClassName:JmsConstant
 * @Description:JMS消息队列、内部请求名常量
 * @author:lzt
 * @date:2016年11月9日 下午5:01:10
 *
 */
public class JmsConstant {
	/**
	 * 发送至IBS消息队列前缀
	 */
	public static String SENDTOIBSDEST = "inca.saas.set.";
	
	/**
	 * 商品接收消息队列名
	 */
	public static String GOODS = "inca.saas.wms.jzt.goods";

	/**
	 * 往来单位接收消息队列名
	 */
	public static String COMPANY = "inca.saas.wms.jzt.company";

	/**
	 * 入库接收消息队列名
	 */
	public static String STIN = "inca.saas.wms.jzt.stin";

	/**
	 * 出库接收消息队列名
	 */
	public static String STOUT = "inca.saas.wms.jzt.stout";

	/**
	 * 入库反馈消息内部请求名
	 */
	public static String STINFEEDBACK = "inca.saas.wms.jzt.stin.feedback";
	/**
	 * 配退反馈消息内部请求名
	 */
	public static String GPCSBACKFEEDBACK = "inca.saas.wms.jzt.gpcsBack.feedback";

	/**
	 * 出库反馈消息内部请求名
	 */
	public static String STOUTFEEDBACK = "inca.saas.wms.jzt.stout.feedback";

	/**
	 * 采退反馈消息内部请求名
	 */
	public static String SuPurBackFEEDBACK = "inca.saas.wms.jzt.suPurBack.feedback";

	/**
	 * 简化流程消息内部请求名
	 */
	public static String SIMPLEFLOWFEEDBACK = "inca.saas.wms.jzt.simpleflow.feedback";
	
	/**
	 * 质量反馈内部请求名
	 */
	public static String GSPFEEDBACK = "inca.saas.wms.jzt.gsp.feedback";
	
	/**
	 * 物流库存对照接收消息队列名
	 */
	public static String  STCONTRAST = "inca.saas.wms.jzt.st.stcontrast";
	
	/**
	 * 物流库存对照内部请求名
	 */
	public static String STCONTRASTBACK = "inca.saas.wms.jzt.st.stcontrast.feedback";
	
	/**
	 * 箱号表请求名
	 */
	public static String CASENUMBER = "inca.saas.wms.jzt.gsp.casenumber";

	/**
	 * erp回调请求名
	 */
	public static String SUCCESSFEEDBACK = "inca.saas.wms.jzt.success.feedback";

}
