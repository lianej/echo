package me.lianej.common.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * 被映射属性的集合封装
 * 创建人：yanweijin
 * 创建时间：2016-3-17 下午1:33:53   
 * @version V0.1
 */
public class PropertyMapper{
	private Log log = LogFactory.getLog(getClass());
	static final int PROP_SAME_NAME = 0;//以属性名作为属性比较参考
	static final int PROP_SAME_TYPE = 1;//以属性名和属性类型作为属性比较参考
	//存在一个源属性对应多个目标属性的情况
	private Multimap<String,CopyableProperty> srcMapping = ArrayListMultimap.create();
	private Map<String,CopyableProperty> destMapping = new HashMap<>();
	private PropertySet propertySet = new PropertySet();
	private boolean prepared = false;
	/**
	 * 将一个表达式list解为析一个映射集合
	 * @see me.lianej.common.beans.CopyableProperty#CopyableProperty
	 * @param expressions 表达式集合
	 */
	PropertyMapper(List<String> expressions){
		Assert.notEmpty(expressions,"空表达式");
		PropertySet propertySet = CopierTools.resolveExp(expressions);
		for (CopyableProperty p : propertySet) {
			if(!this.propertySet.add(p))throw new RuntimeException("表达式解析错误,多个源属性映射到了同一个目标属性:"+p.getDestPropName());
			srcMapping.put(p.getSrcPropName(), p);
			destMapping.put(p.getDestPropName(), p);
		}
	}
//	public PropertyMapper(){
//		
//	}
	/**
	 * 建立同类型bean的属性拷贝映射
	 * @param clazz
	 * @param specialProppertyExpressions 排除属性及一般映射规则表达式<p>
	 * 排除属性使用叹号!开头,后面跟属性名,例如!name,表示不复制名为name的属性<p>
	 * 一般映射规则表达式即CopyableProerty的构造表达式,会根据destPropName来覆盖自动生成的映射关系<p>
	 * @throws IntrospectionException
	 */
	PropertyMapper(Class<?> clazz,List<String> specialProppertyExpressions) throws IntrospectionException{
		this(clazz,clazz,specialProppertyExpressions);
	}
	PropertyMapper(Class<?> srcClass,Class<?> destClass,List<String> specialProppertyExpressions) throws IntrospectionException{
		List<String> noCopyProps = new ArrayList<>();//排除复制的属性
		List<String> specialExps = new ArrayList<>();//优先复制的属性
		boolean samebeanType = srcClass == destClass;
		/*
		 * 以!开头的字符串表示不复制的属性
		 * 其余表达式视作常规映射表达式进行解析,将覆盖原有映射
		 */
		for (String exp : specialProppertyExpressions) {
			if(exp.startsWith("!")) {
				noCopyProps.add(exp);
			}else{
				specialExps.add(exp);
			}
		}
		
		//先解析表达式
		PropertySet specialProperties = CopierTools.resolveExp(specialExps);
		propertySet.addAll(specialProperties);
		
		Set<PropertyDescriptor> sameProperties = new HashSet<>();
		if(samebeanType){
			/*
			 * 如果是同类型bean,直接根据源对象来构建映射
			 * 即:sameProperties全部来源于源对象
			 */
			sameProperties = CopierTools.findSrcPropertyDescriptors(srcClass, noCopyProps);;
		}else{
			//非同类型bean,但依然要使用这种方式生成,说明有大部分属性相同,小部分属性需要经过表达式修正
			//寻找相同名称的属性,并
			Set<PropertyDescriptor> srcPds = CopierTools.findSrcPropertyDescriptors(srcClass, noCopyProps);
			Set<PropertyDescriptor> destPds = CopierTools.findDestPropertyDescriptors(destClass, noCopyProps);
			//取源对象和目标对象的属性交集
			sameProperties = PropertyMapper.findSameProperties(srcPds, destPds, PROP_SAME_TYPE);
			for (PropertyDescriptor pd : sameProperties) {
				CopyableProperty p = new CopyableProperty(pd);
				propertySet.add(p);
			}
		}
		//表达式具有更高的优先级,如果反射出的描述符生成的可复制属性与表达式生成的相同(只考虑destPropName),则使用由表达式生成的可复制属性对象
		/*
		 * 一个源属性可以被复制给多个目标属性
		 * 但是一个目标属性只能接受来自一个源属性的值
		 */
		for (PropertyDescriptor pd : sameProperties) {
			CopyableProperty p = new CopyableProperty(pd);
			propertySet.add(p);
		}
		for (CopyableProperty p : propertySet) {
			srcMapping.put(p.getSrcPropName(), p);
			destMapping.put(p.getDestPropName(), p);
		}
		//采用这种方式生成的映射器一定包含了读写方法
		this.prepared = true;
		
	}
	private static Set<PropertyDescriptor> findSameProperties(
			Set<PropertyDescriptor> srcPds,Set<PropertyDescriptor> destPds,int type){
		Set<PropertyDescriptor> result = new HashSet<>();
		switch (type) {
		case PROP_SAME_TYPE:
			for (PropertyDescriptor spd : srcPds) {
				if(destPds.contains(srcPds)){
					result.add(spd);
				}
			}
			break;
		case PROP_SAME_NAME:
			//TODO
		}
		return result;
	}
	
//	/**
//	 * 将一条表达式解析为一个属性映射,并添加到映射集合中
//	 * @param exp 
//	 * @author yanweijin
//	 * @date 2016-3-17
//	 */
//	public void addMapping(String exp){
//		CopyableProperty p = new CopyableProperty(exp);
//		srcMapping.put(p.getSrcPropName(), p);
//		destMapping.put(p.getDestPropName(), p);
//		mappingSet.add(p);
//	}
	
	void setValue(String srcPropName,Object propValue){
		Collection<CopyableProperty> props = srcMapping.get(srcPropName);
		if(props.isEmpty()){
			log.info("源对象属性["+srcPropName+"]没有配置映射规则,跳过.");
		}
		for (CopyableProperty prop : props) {
			prop.setValue(propValue);
		}
	}
	
	boolean hasPropInSrc(String srcPropName){
		boolean result = srcMapping.containsKey(srcPropName);
		if(!result){
			log.info("源对象属性["+srcPropName+"]没有配置映射规则,跳过.");
		}
		return result;
	}
	boolean hasPropInDest(String destPropName){
		boolean result = destMapping.containsKey(destPropName);
		if(!result){
			log.info("目标对象属性["+destPropName+"]没有配置映射规则,跳过.");
		}
		return result;
	}
	Object getValue(String destPropName){
		CopyableProperty prop = destMapping.get(destPropName);
		if(prop == null){
			throw new NullPointerException("没有为目标对象的属性["+destPropName+"]配置映射,请检查规则文件或源对象");
		}else{
			try {
				return destMapping.get(destPropName).getValue();
			} catch (ParseException e) {
				throw new RuntimeException("解析日期字符串时出现异常",e);
			}
		}
	}
	void setReadMethod(String srcPropName,Method m,Class<?> srcPropType){
		Collection<CopyableProperty> srcProps = srcMapping.get(srcPropName);
		for (CopyableProperty prop : srcProps) {
			prop.setSrcPropGetter(m,srcPropType);
		}
	}
	void setWriteMethod(String destPropName,Method m,Class<?> destPropType){
		destMapping.get(destPropName).setDestPropSetter(m,destPropType);
	}
	PropertySet getCopyablePropertySet() {
		return propertySet;
	}
	boolean isPrepared() {
		return prepared;
	}
	void setPrepared(boolean prepared) {
		this.prepared = prepared;
	}
	
}
