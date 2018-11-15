package com.inca.saas.wms.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 
 * @ClassName:SelectHelper
 * @Description:查询工具类
 * @author:lzt
 * @date:2016年11月9日 下午5:14:05
 *
 */
public class SelectHelper {

	/**
	 * 
	 * @Title:getResultSet
	 * @Description:根据sql获取查询结果
	 * @param con
	 * @param sql
	 * @throws Exception
	 * @return:ResultSet
	 * @date:2016年11月9日 下午6:36:40
	 */
	public static ResultSet getResultSet(Connection con, String sql) throws Exception {
		ResultSet resultSet = null;
		try {
			// 设置可滚动的ResultSet类型
			PreparedStatement preparedStatement = con.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE ,ResultSet.CONCUR_READ_ONLY);
			preparedStatement.executeQuery();
			resultSet = preparedStatement.getResultSet();
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (con != null) {
				con.rollback();
				con.close();
			}
		}
		return resultSet;

	}

}
