package org.okou.lippen.common.utils;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * ����jakarta commons BeanUtils
 * 
 * @author EXvision
 */
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils
{
	private static Logger logger = Logger.getLogger(BeanUtils.class);

	private static PropertyUtilsBean propertyUtilsBean;

	/**
	 * ����ʵ�����ֶ� ���Բ����趨���ֶ�
	 * 
	 * @param dest
	 *            Ŀ��ʵ��
	 * @param orig
	 *            ��Դʵ��
	 * @param ignoreProperties
	 *            ����Ҫ���Ե��ֶ�
	 */
	public static void copyProperties(Object dest, Object orig,
			String... ignoreProperties)
	{
		org.springframework.beans.BeanUtils.copyProperties(orig, dest,
				ignoreProperties);
	}

	/**
	 * ����ʵ�����ֶ� <b>����null�ֶ�</b>
	 * 
	 * @param dest
	 *            Ŀ��ʵ��
	 * @param orig
	 *            ��Դʵ��
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("rawtypes")
	public static void copyPropertiesIgnoreNull(Object dest, Object orig)
			throws IllegalAccessException, InvocationTargetException
	{
		// Validate existence of the specified beans
		if (dest == null)
		{
			throw new IllegalArgumentException("No destination bean specified");
		}
		if (orig == null)
		{
			throw new IllegalArgumentException("No origin bean specified");
		}
		debug("BeanUtils.copyProperties(" + dest + ", " + orig + ")");

		// Copy the properties, converting as necessary
		if (orig instanceof DynaBean)
		{
			DynaProperty[] origDescriptors = ((DynaBean) orig).getDynaClass()
					.getDynaProperties();
			for (int i = 0; i < origDescriptors.length; i++)
			{
				String name = origDescriptors[i].getName();
				// Need to check isReadable() for WrapDynaBean
				// (see Jira issue# BEANUTILS-61)
				if (getPropertyUtils().isReadable(orig, name)
						&& getPropertyUtils().isWriteable(dest, name))
				{
					Object value = ((DynaBean) orig).get(name);
					if (value != null)
					{
						copyProperty(dest, name, value);
					}
				}
			}
		}
		else
			if (orig instanceof Map)
			{
				Iterator names = ((Map) orig).keySet().iterator();
				while (names.hasNext())
				{
					String name = (String) names.next();
					if (getPropertyUtils().isWriteable(dest, name))
					{
						Object value = ((Map) orig).get(name);
						if (value != null)
						{
							copyProperty(dest, name, value);
						}
					}
				}
			}
			else
			/* if (orig is a standard JavaBean) */{
				PropertyDescriptor[] origDescriptors = getPropertyUtils()
						.getPropertyDescriptors(orig);
				for (int i = 0; i < origDescriptors.length; i++)
				{
					String name = origDescriptors[i].getName();
					if ("class".equals(name))
					{
						continue; // No point in trying to set an object's
						// class
					}
					if (getPropertyUtils().isReadable(orig, name)
							&& getPropertyUtils().isWriteable(dest, name))
					{
						try
						{
							Object value = getPropertyUtils()
									.getSimpleProperty(orig, name);
							if (value != null)
							{
								copyProperty(dest, name, value);
							}
						}
						catch (NoSuchMethodException e)
						{
							// Should not happen
						}
					}
				}
			}

	}

	/**
	 * Beanת����Map�ķ���<br>
	 * <ul>
	 * <li><b>value��ֵ</b>���ᱻ����</li>
	 * <li><b>key=class</b>������ᱻ����</li>
	 * </ul>
	 * �����뿴Դ����
	 * 
	 * @param bean
	 *            ��ת����pojo
	 * @return ת�����Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map describe(Object bean)
	{
		try
		{
			Map<?, ?> map = org.apache.commons.beanutils.BeanUtils
					.describe(bean);
			Map<Object, Object> reMap = new HashMap<Object, Object>();
			map.remove("class");
			Iterator<?> it = map.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<?, ?> entry = (Entry<?, ?>) it.next();
				if (entry.getValue() != null
						&& StringUtils.isNotBlank(String.valueOf(entry
								.getValue())))
				{
					reMap.put(entry.getKey(), entry.getValue());
				}
			}
			return reMap;
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * ͨ�������ȡ���� �쳣���׳�
	 * 
	 * @param bean
	 * @param name
	 * @return ֵ��Null
	 */
	public static String getPropertyQuietly(Object bean, String name)
	{
		try
		{
			return BeanUtils.getProperty(bean, name);
		}
		catch (IllegalAccessException e)
		{
			debug(e);
		}
		catch (InvocationTargetException e)
		{
			debug(e);
		}
		catch (NoSuchMethodException e)
		{
			debug(e);
		}
		return null;
	}

	/**
	 * ѭ������ת��,��ȡ�����DeclaredField.
	 * 
	 * @throws NoSuchFieldException
	 *             ���û�и�Fieldʱ�׳�.
	 */
	public static Field getDeclaredField(Object object, String propertyName)
			throws NoSuchFieldException
	{
		Assert.notNull(object);
		Assert.hasText(propertyName);
		return getDeclaredField(object.getClass(), propertyName);
	}

	/**
	 * ѭ������ת��,��ȡ�����DeclaredField.
	 * 
	 * @throws NoSuchFieldException
	 *             ���û�и�Fieldʱ�׳�.
	 */
	public static Field getDeclaredField(Class<?> clazz, String propertyName)
			throws NoSuchFieldException
	{
		Assert.notNull(clazz);
		Assert.hasText(propertyName);
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass())
		{
			try
			{
				return superClass.getDeclaredField(propertyName);
			}
			catch (NoSuchFieldException e)
			{
				// Field���ڵ�ǰ�ඨ��,��������ת��
			}
		}
		throw new NoSuchFieldException("No such field: " + clazz.getName()
				+ '.' + propertyName);
	}

	/**
	 * ������ȡ�������ֵ,����private,protected���η�������.
	 * 
	 * @throws NoSuchFieldException
	 *             ���û�и�Fieldʱ�׳�.
	 */
	public static Object forceGetProperty(Object object, String propertyName)
			throws NoSuchFieldException
	{
		Assert.notNull(object);
		Assert.hasText(propertyName);

		Field field = getDeclaredField(object, propertyName);

		boolean accessible = field.isAccessible();
		field.setAccessible(true);

		Object result = null;
		try
		{
			result = field.get(object);
		}
		catch (IllegalAccessException e)
		{
			logger.info("error wont' happen");
		}
		field.setAccessible(accessible);
		return result;
	}

	/**
	 * �������ö������ֵ,����private,protected���η�������.
	 * 
	 * @throws NoSuchFieldException
	 *             ���û�и�Fieldʱ�׳�.
	 */
	public static void forceSetProperty(Object object, String propertyName,
			Object newValue) throws NoSuchFieldException
	{
		Assert.notNull(object);
		Assert.hasText(propertyName);

		Field field = getDeclaredField(object, propertyName);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try
		{
			field.set(object, newValue);
		}
		catch (IllegalAccessException e)
		{
			logger.info("Error won't happen");
		}
		field.setAccessible(accessible);
	}

	/**
	 * �������ö�����,����private,protected���η�������.
	 * 
	 * @throws NoSuchMethodException
	 *             ���û�и�Methodʱ�׳�.
	 */
	public static Object invokePrivateMethod(Object object, String methodName,
			Object... params) throws NoSuchMethodException
	{
		Assert.notNull(object);
		Assert.hasText(methodName);
		Class<?>[] types = new Class[params.length];
		for (int i = 0; i < params.length; i++)
		{
			types[i] = params[i].getClass();
		}

		Class<?> clazz = object.getClass();
		Method method = null;
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass())
		{
			try
			{
				method = superClass.getDeclaredMethod(methodName, types);
				break;
			}
			catch (NoSuchMethodException e)
			{
				// �������ڵ�ǰ�ඨ��,��������ת��
			}
		}

		if (method == null)
			throw new NoSuchMethodException("No Such Method:"
					+ clazz.getSimpleName() + methodName);

		boolean accessible = method.isAccessible();
		method.setAccessible(true);
		Object result = null;
		try
		{
			result = method.invoke(object, params);
		}
		catch (Exception e)
		{
			ReflectionUtils.handleReflectionException(e);
		}
		method.setAccessible(accessible);
		return result;
	}

	/**
	 * ��Filed������ȡ��Field�б�.
	 */
	public static List<Field> getFieldsByType(Object object, Class<?> type)
	{
		List<Field> list = new ArrayList<Field>();
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields)
		{
			if (field.getType().isAssignableFrom(type))
			{
				list.add(field);
			}
		}
		return list;
	}

	/**
	 * ��FiledName���Field������.
	 */
	public static Class<?> getPropertyType(Class<?> type, String name)
			throws NoSuchFieldException
	{
		return getDeclaredField(type, name).getType();
	}

	/**
	 * ���field��getter��������.
	 */
	public static String getGetterName(Class<?> type, String fieldName)
	{
		Assert.notNull(type, "Type required");
		Assert.hasText(fieldName, "FieldName required");

		if (type.getName().equals("boolean"))
		{
			return "is" + StringUtils.capitalize(fieldName);
		}
		else
		{
			return "get" + StringUtils.capitalize(fieldName);
		}
	}

	/**
	 * ���field��getter����,����Ҳ����÷���,����null.
	 */
	public static Method getGetterMethod(Class<?> type, String fieldName)
	{
		try
		{
			return type.getMethod(getGetterName(type, fieldName));
		}
		catch (NoSuchMethodException e)
		{
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Gets the <code>PropertyUtilsBean</code> instance used to access
	 * properties.
	 * 
	 * @return The ConvertUtils bean instance
	 */
	public static PropertyUtilsBean getPropertyUtils()
	{
		if (propertyUtilsBean == null)
		{
			propertyUtilsBean = new PropertyUtilsBean();
		}
		return propertyUtilsBean;
	}

	private static void debug(Object message)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug(message);
		}
	}

	@Test
	public void testCopyPropertiesIgnoreBlank() throws Exception
	{

	}
}
