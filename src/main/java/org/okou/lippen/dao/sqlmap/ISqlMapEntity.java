package org.okou.lippen.dao.sqlmap;

import java.io.Serializable;

import org.okou.lippen.delayed.dao.ISqlMapDelayedInsertable;
import org.okou.lippen.delayed.dao.ISqlMapDelayedUpdatable;

public interface ISqlMapEntity extends ISqlMapDelayedInsertable,
		ISqlMapDelayedUpdatable, Serializable
{
	/**
	 * ��ȡsql map�����select�ڵ��id
	 * 
	 * @return sql map�����select�ڵ��id
	 * @author EXvision
	 * @since 2013-2-22
	 */
	public String selectId();

	/**
	 * ��ȡsql map�����insert�ڵ��id
	 * 
	 * @return sql map�����insert�ڵ��id
	 * @author EXvision
	 * @since 2013-2-22
	 */
	public String insertId();

	/**
	 * ��ȡsql map�����update�ڵ��id
	 * 
	 * @return sql map�����update�ڵ��id
	 * @author EXvision
	 * @since 2012-11-28
	 */
	public String updateId();

	/**
	 * ��ȡsql map�����delete�ڵ��id
	 * 
	 * @return sql map�����delete�ڵ��id
	 * @author EXvision
	 * @since 2013-2-22
	 */
	public String deleteId();
}
