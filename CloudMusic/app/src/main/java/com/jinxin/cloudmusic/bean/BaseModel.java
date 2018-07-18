package com.jinxin.cloudmusic.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * 基类
 */
public abstract class BaseModel implements Serializable {

	@PrimaryKey(AssignType.AUTO_INCREMENT)
	@Column("_id")
	public int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BaseModel() {
	}

}
