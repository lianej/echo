package me.lianej.common.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.util.Date;
/**
 * 支持字符串从中间量获取值的类型
 * @author lianej
 *
 */
enum SupportedClass {
	STRING(String.class){
		@Override
		public Object valueOf(String srcValue, Format format) throws ParseException {
			return srcValue;
		}
	},
	INT(Integer.class),
	INTEGER(Integer.class),
	DATE(Date.class){
		@Override
		public Object valueOf(String srcValue, Format format) throws ParseException {
			return ((DateFormat) format).parse(srcValue);
		}
	},
	LONG(Long.class),
	DECIMAL(BigDecimal.class){
		@Override
		public Object valueOf(String srcValue, Format format) throws ParseException {
			return new BigDecimal(srcValue);
		}
	},
	BOOL(Boolean.class),
	BOOLEAN(Boolean.class),
	DOUBLE(Double.class);
	private Class<?> clazz;
	private Method valueOfMethod = null;
	private SupportedClass(Class<?> clazz){
		this.clazz = clazz;
		try {
			this.valueOfMethod = clazz.getMethod("valueOf", String.class);
		} catch (NoSuchMethodException | SecurityException e) {
		}
	}
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	public static SupportedClass valueOf(Class<?> clazz){
		for (SupportedClass sc : SupportedClass.values()) {
			if(sc.clazz == clazz) return sc;
		}
		throw new RuntimeException("尚未支持属性类型["+clazz.getName()+"]");
	}
	public Object valueOf(String srcValue,Format format) throws ParseException{
		try {
			if(valueOfMethod!=null)
				return valueOfMethod.invoke(null,srcValue);
			else
				throw new RuntimeException("操作不被支持:["+clazz.getName()+"]没有静态的valueOf(String)方法");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("操作不被支持",e);
		}
	}
}
