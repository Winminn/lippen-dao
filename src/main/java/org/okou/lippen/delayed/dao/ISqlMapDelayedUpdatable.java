package org.okou.lippen.delayed.dao;

import java.io.Serializable;

public interface ISqlMapDelayedUpdatable extends Serializable
{
	/**
	 * ��ȡsql map�����update�ڵ��id �����ӳٸ�������������update
	 * 
	 * @return sql map�����update�ڵ��id
	 * @author EXvision
	 * @since 2012-11-28
	 */
	public String updateId();
}