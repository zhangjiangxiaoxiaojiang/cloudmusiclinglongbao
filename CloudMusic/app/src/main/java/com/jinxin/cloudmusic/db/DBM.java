package com.jinxin.cloudmusic.db;

import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.constant.LocalConstant;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * Created by XTER on 2016/9/21.
 * 数据库管理 --DataBaseManager
 */
public class DBM {
	private static LiteOrm liteOrmDefault;

	/**
	 * 获取默认的orm
	 *
	 * @return DefaultOrm
	 */
	public static LiteOrm getDefaultOrm() {
		if (liteOrmDefault == null) {
			liteOrmDefault = LiteOrm.newSingleInstance(JxscApp.getContext(), LocalConstant.DB_DEFAULT_NAME);
		}
		return liteOrmDefault;
	}



}
