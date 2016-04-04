package me.lianej.common.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static final Log log = LogFactory.getLog(CopyableProperty.class);
	private String srcPropName;
	private String destPropName;
	private String propValueString;
	private SupportedClass destPropClass = SupportedClass.STRING;
	private Class<?> destPropType;
	private Class<?> srcPropType;
	private Method srcPropGetter;
	private Method destPropSetter;
	private Format privateFormat = format;
	//是否为同类型属性,如果是则不走propValueString来转换
	private boolean sametype = false;
	//是否为排除复制的属性
	private boolean excluded = false;
	//映射中包含了源属性的read method和目标属性的write method时为true
	private boolean prepared = false;
	//通过表达式解析的映射优先级更改高
	private boolean prior = false;
	//是否复制空属性
	private boolean includeNullValue = false;
	
	/**
	 * 根据表达式来解析属性复制映射
	 * @param exp
	 */
	public CopyableProperty(String exp){
		this(new ExpressionRule(exp));
	}
	/**
	 * 根据规则对象来构建属性复制映射
	 * @param rule
	 */
	private CopyableProperty(ExpressionRule rule){
		this.destPropName = rule.getDestPropName();
		this.srcPropName = rule.getSrcPropName();
		this.excluded = rule.isExcluded();
		if(rule.getPrivateFormat()!=null){
			this.privateFormat = new SimpleDateFormat(rule.getPrivateFormat());
		}
		if(rule.getDestPropType()!=null){
			this.destPropClass = SupportedClass.valueOf(rule.getDestPropType().toUpperCase());
		}
		this.prepared=false;
		this.sametype=false;
		this.prior = true;
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
		this.srcPropName = pd.getName();
		this.destPropName = pd.getName();
		this.destPropClass = SupportedClass.valueOf(pd.getPropertyType());
		this.sametype = true;
		this.prepared = true;
	}
	public String getSrcPropName() {
		return srcPropName;
	}

	public String getDestPropName() {
		return destPropName;
	}

	public void setSrcPropGetter(Method srcPropGetter,Class<?> srcPropType) {
		Assert.notNull(srcPropGetter);
		Assert.notNull(srcPropType);
		this.srcPropGetter = srcPropGetter;
		this.srcPropType = srcPropType;
		this.checkPrepared();
	}

	public void setDestPropSetter(Method destPropSetter,Class<?> destPropType) {
		Assert.notNull(destPropSetter);
		Assert.notNull(destPropType);
		this.destPropSetter = destPropSetter;
		this.destPropType = destPropType;
		this.checkPrepared();
	}
	
	private void checkPrepared(){
		this.prepared = this.srcPropGetter !=null && this.destPropSetter!=null;
		if(prepared){
			this.sametype = this.srcPropType == this.destPropType;
		}
	}

	public void setValue(Object val) {
		if(val==null){
			propValueString = null;
		}else if(val instanceof Date){
			propValueString = privateFormat.format((Date)val);
		}else if(val instanceof Number || val instanceof String){
			propValueString = val.toString();
		}else{
			throw new RuntimeException("暂不支持其他类型的映射");
		}
	}
	public Object getValue() throws ParseException{
		if(propValueString==null || destPropClass==null) return null;
		return destPropClass.parse(propValueString, privateFormat);
	}
	public void copyProperty(Object srcBean,Object destBean){
		if(!prepared) throw new RuntimeException("["+srcPropName+"->"+destPropName+"] mapping is not prepared!"); 
		if(excluded){
			log.debug("属性["+srcPropName+"->"+destPropName+"]被排除,不复制");
			return;
		}
		try {
			Object temp = srcPropGetter.invoke(srcBean);
			if(temp==null && !includeNullValue){
				return;
			}
			Object value;
			if(sametype){//同类型属性直接调用setter
				value = temp;
			}else{//不同类型属性通过字符串转换一次
				this.setValue(temp);
				value = this.getValue();
			}
			destPropSetter.invoke(destBean, value);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | ParseException e) {
			throw new RuntimeException("复制属性["+srcPropName+"->"+destPropName+"]的值时出现异常",e);
		}
	}
	
//	public boolean isExcluded() {
//		return excluded;
//	}
	/**
	 * 是否优先于另一条映射<p>
	 * 当前映射为排除映射时,优先级最高,返回true<p>
	 * 当前映射为优先映射,并且比较对象不是排除映射时,返回true<p>
	 * 当前映射和比较对象都 既不是排除映射也不是优先映射时,也返回true<p>
	 * 否则返回false<p>
	 * 即:当优先级大于等于比较对象时,返回true,否则返回false<p>
	 * @param other
	 * @return
	 */
	public boolean isPriorTo(CopyableProperty other) {
		return this.excluded || (this.prior && !other.excluded) || (!other.excluded && !other.prior);
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
	private String getPropTypeName(Class<?> clz){
		return clz==null?"not perpared":clz.getSimpleName();
	}
	@Override
	public String toString() {
		return "[" + srcPropName + "(" + getPropTypeName(srcPropType) + ")->" + destPropName + "("
				+ getPropTypeName(destPropType) + "),prior=" + prior + ",excluded=" + excluded + "]";
	}
	public void setIncludeNullValue(boolean includeNullValue) {
		this.includeNullValue = includeNullValue;
	}
	
	
}
