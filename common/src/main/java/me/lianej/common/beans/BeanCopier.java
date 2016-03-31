package me.lianej.common.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.Assert;


public class BeanCopier {
	/**
	 * 还没写好...
	 * @param sources
	 * @param destClass
	 * @param mapper
	 * @return
	 */
	@Deprecated
	public static <T> List<T> copyBeanCollection(List<Object> sources,Class<T> destClass,PropertyMapper mapper){
		Assert.notEmpty(sources,"源对象集合为空");
		List<T> result = new ArrayList<>();
		Class<?> srcClass = sources.get(0).getClass();
		if(!mapper.isPrepared()){
			mapper = prepareMapping(srcClass,destClass,mapper);
		}
		try {
			for (Object src : sources) {
					T dest = destClass.newInstance();
					copyBean(src,dest,mapper);
					result.add(dest);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	/**
	 * 构建同类型bean的属性映射器,使用不定参数
	 * @param clazz
	 * @param specialPropertyExpressions
	 * @return
	 * @throws IntrospectionException
	 */
	public static PropertyMapper buildMapperWithSametype(Class<?> clazz,String...specialPropertyExpressions) throws IntrospectionException{
		return buildMapperWithSametype(clazz,Arrays.asList(specialPropertyExpressions));
	}
	/**
	 * 构建同类型bean的属性映射器,使用list参数
	 * @param clazz
	 * @param specialPropertyExpressions 
	 * @return
	 * @throws IntrospectionException
	 */
	public static PropertyMapper buildMapperWithSametype(Class<?> clazz,List<String> specialPropertyExpressions) throws IntrospectionException{
		return new PropertyMapper(clazz,specialPropertyExpressions);
	}
	/**
	 * 完全根据映射表达式来构建属性映射器
	 * @param srcClass
	 * @param destClass
	 * @param expressions
	 * @return
	 * @throws IntrospectionException
	 */
	public static PropertyMapper buildMapperWithExpressions(Class<?> srcClass,Class<?> destClass,List<String> expressions) throws IntrospectionException{
		return prepareMapping(srcClass, destClass, new PropertyMapper(expressions));
	}

	/**
	 * 根据属性类型和表达式来构建属性映射器,表达式构建的映射会覆盖同类型属性之间建立的映射(如果他们冲突了)
	 * @param srcClass
	 * @param destClass
	 * @param specialPropertyExpressions
	 * @return
	 * @throws IntrospectionException
	 */
	public static PropertyMapper buildMapperWithSamepropAndExps(Class<?> srcClass,Class<?> destClass,List<String> specialPropertyExpressions) throws IntrospectionException{
		//TODO 暂时修复该bug
		/*
		 * 由于PropertyMapper的构造器中对表达式生成的映射没有做相应处理,导致单条属性not perpared
		 * 同时,由于在获取源对象与目标对象的相同属性时,只返回了源对象的属性描述符集合,因此现在的版本,经过构造器生成的映射器实际上是不可用的
		 * 必须调用prepareMapping将读写方法填入映射器,覆盖原有读写方法
		 */
		return prepareMapping(srcClass, destClass, new PropertyMapper(srcClass, destClass, specialPropertyExpressions));
	}
	
	/**
	 * 根据映射器规则复制对象属性
	 * @param src
	 * @param dest
	 * @param mapper
	 */
	public static void copyBean(Object src,Object dest,PropertyMapper mapper){
		if(!mapper.isPrepared()){
			mapper = prepareMapping(src.getClass(),dest.getClass(),mapper);
		}
		for (CopyableProperty cp : mapper.getCopyablePropertySet()) {
			cp.copyProperty(src, dest);
		}
	}
	
	
	
	//*********************************************private**************************************************
	
	
	private static PropertyMapper prepareMapping(Class<?> srcClass,Class<?> destClass,PropertyMapper mapper){
		PropertyDescriptor[] srcPds = ReflectUtils.getBeanProperties(srcClass);
		for (PropertyDescriptor pd : srcPds) {
			String srcPropName = pd.getName();
			if(mapper.hasPropInSrc(srcPropName)){
				Method method = pd.getReadMethod();
				Class<?> type = pd.getPropertyType();
				Assert.notNull(method,"源对象已映射的属性["+srcPropName+"]没有对应的getter");
				mapper.setReadMethod(srcPropName, method, type);
			}
		}
		PropertyDescriptor[] destPds = ReflectUtils.getBeanProperties(destClass);
		for (PropertyDescriptor pd : destPds) {
			String destPropName = pd.getName();
			if(mapper.hasPropInDest(destPropName)){
				Method method = pd.getWriteMethod();
				Class<?> type = pd.getPropertyType();
				Assert.notNull(method,"目标对象已映射的属性["+destPropName+"]没有对应的getter");
				mapper.setWriteMethod(destPropName, method, type);
			}
		}
		mapper.setPrepared(true);
		return mapper;
	}
}
