package org.okou.lippen.delayed.dao;

import java.io.Serializable;

public interface ISqlMapDelayedInsertable extends Serializable{
	/**
	 * ��ȡsql map�����insert�ڵ��id �����ӳٸ�������������insert
	 * 
	 * @return sql map�����insert�ڵ��id
	 * @author EXvision
	 * @since 2012-11-28
	 */
	public String insertId();
}
