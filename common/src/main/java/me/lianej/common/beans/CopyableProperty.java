package me.lianej.common.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

/**
 * 用于映射两个不同类型之间的属性,便于拷贝<p>
 * 可复制属性使用目标属性名(destPropName)来做hashcode和equals比较<p>
 * 具有相同的destPropName值的映射将被视为同一条映射
 * 创建人：yanweijin
 * 创建时间：2016-3-17 下午5:19:36   
 * @version V0.1
 */
class CopyableProperty {
	private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//表达式暂时不能包含空格
	private static final Pattern expResolver = Pattern.compile("(\\w+)=(\\w+),?");
	private String srcPropName;
	private String destPropName;
	private String propValueString;
	private SupportedClass destPropClass = SupportedClass.STRING;
//	private Class<?> destPropType = String.class;
	private Method srcPropGetter;
	private Method destPropSetter;
	private Format privateFormat = format;
	private Object propValueObject;
	private boolean sametype = false;
	
	
	/**
	 * 通过字符串表达式初始化对象<p>
	 * 表达式形如:<code>src=prop1,dest=prop2,clz=date,format=yyyyMMdd</code><br>
	 * 或:<code>name=prop,clz=date,format=yyyyMMdd</code><p>
	 * 
	 * src: 源对象属性名<p>
	 * dest: 目标对象属性名<p>
	 * name: 当源对象和目标对象要copy的属性名一致时,可以使用name=prop的形式来为src和dest同时写入一致的属性名,<b>当填写了src或dest时,此key无效</b><p>
	 * clz: 目标对象属性类型<b>(可省略,默认为string,支持long,int,integer,date,double,decimal)</b><p>
	 * 
	 * format: 如果源属性是日期字符串,而目标属性是日期,则需要填写源属性字符串格式化为日期的格式<b>(可省略,默认为yyyy-MM-dd HH:mm:ss)</b><p>
	 * @param exp 表达式 
	 */
	public CopyableProperty(String exp){
		Assert.hasLength(exp,"属性映射表达式为空字符串,不能初始化对象!");
		Matcher m = expResolver.matcher(exp);
		String propName = null;
		while(m.find()){
			String value = m.group(2);
			switch(m.group(1).toLowerCase()){
			case "name":
				propName = value;
				break;
			case "src":
				srcPropName = value;
				break;
			case "dest":
				destPropName = value;
				break;
			case "format":
				privateFormat = new SimpleDateFormat(value);
				break;
			case "clz":
				destPropClass = SupportedClass.valueOf(value.toUpperCase());
				break;
			}
		}
		if(srcPropName==null && destPropName==null){
			srcPropName = destPropName = propName;
		}
		Assert.hasText(srcPropName,"表达式解析失败:"+exp);
		Assert.hasText(destPropName,"表达式解析失败:"+exp);
	}
	/**
	 * 当源对象与目标对象属于同一类型时,可以根据属性描述符构造可复制属性<p>
	 * 该描述符的read方法将作为源属性的getter,write方法将作为目标属性的setter<p>
	 * 并且记录源属性与目标属性为sametype,便于直接copy object(默认情况下是通过字符串来进行中间转换)
	 * @param pd 属性描述符
	 */
	public CopyableProperty(PropertyDescriptor pd){
		Assert.notNull(pd);
		this.srcPropGetter = pd.getReadMethod();
		this.destPropSetter = pd.getWriteMethod();
		this.sametype = true;
		this.srcPropName = pd.getName();
		this.destPropName = pd.getName();
		this.destPropClass = SupportedClass.valueOf(pd.getPropertyType());
	}
	public String getSrcPropName() {
		return srcPropName;
	}

	public String getDestPropName() {
		return destPropName;
	}

	public void setSrcPropGetter(Method srcPropGetter) {
		this.srcPropGetter = srcPropGetter;
	}

	public void setDestPropSetter(Method destPropSetter) {
		this.destPropSetter = destPropSetter;
	}

	public void setValue(Object val) {
		if(sametype){
			propValueObject = val;
		}else if(val==null){
			propValueString = null;
		}else if(val instanceof Date){
			propValueString = format.format((Date)val);
		}else if(val instanceof Number || val instanceof String){
			propValueString = val.toString();
		}else{
			throw new RuntimeException("暂不支持其他类型的映射");
		}
	}
	public Object getValue() throws ParseException{
		if(propValueString==null) return null;
		return destPropClass.valueOf(propValueString, privateFormat);
	}
	public void copyProperty(Object srcBean,Object destBean){
		try {
			Object value;
			if(sametype){
				value = propValueObject;
			}else{
				Object val = srcPropGetter.invoke(srcBean);
				setValue(val);
				value = getValue();
			}
			destPropSetter.invoke(destBean, value);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | ParseException e) {
			throw new RuntimeException("复制属性["+srcPropName+"->"+destPropName+"]的值时出现异常",e);
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destPropName == null) ? 0 : destPropName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CopyableProperty other = (CopyableProperty) obj;
		if (destPropName == null) {
			if (other.destPropName != null)
				return false;
		} else if (!destPropName.equals(other.destPropName))
			return false;
		return true;
	}
	
}
