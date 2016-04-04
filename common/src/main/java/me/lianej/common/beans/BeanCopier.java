package me.lianej.common.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.Assert;


public class BeanCopier {
	
	
	/*
	 * 应该提供的API:
	 * 	1,	源对象和目标对象类型一致,根据反射的属性构建映射,表达式补充
	 * 			buildMapperWithSameBeantype(Class,String...)
	 * 	2,	源对象和目标对象类型不一致
	 * 	  	2.1	纯表达式构建映射
	 * 			buildMapperWithExpressions(Class,Class,String...)
	 * 		2.2	根据反射的属性名构建映射,表达式补充
	 * 			buildMapperWithSamePropertyName(Class,Class,String...)
	 * 		2.3	根据反射的属性名和类型构建映射,表达式补充
	 * 			buildMapperWithSameProperty(Class,Class,String...)
	 */
	

	/**
	 * 构建同类型bean的属性映射器,使用不定参数
	 * @param clazz
	 * @param specialPropertyExpressions
	 * @return
	 * @throws IntrospectionException
	 */
	public static <S> PropertyMapper<S,S> buildMapperWithSameBeantype(
			Class<S> clazz,String...specialPropertyExpressions) throws IntrospectionException{
		return buildMapperWithSameBeantype(clazz,Arrays.asList(specialPropertyExpressions));
	}
	/**
	 * 构建同类型bean的属性映射器,使用list参数
	 * @param clazz
	 * @param specialPropertyExpressions 
	 * @return
	 * @throws IntrospectionException
	 */
	public static <S> PropertyMapper<S,S> buildMapperWithSameBeantype(
			Class<S> clazz,List<String> specialPropertyExpressions) throws IntrospectionException{
		return new PropertyMapper<S,S>(clazz,specialPropertyExpressions);
	}
	/**
	 * 完全根据映射表达式来构建属性映射器
	 * @param srcClass
	 * @param destClass
	 * @param expressions
	 * @return
	 * @throws IntrospectionException
	 */
	public static <S,D> PropertyMapper<S,D> buildMapperWithExpressions(
			Class<S> srcClass,Class<D> destClass,List<String> expressions) throws IntrospectionException{
		return prepareMapping(srcClass, destClass, new PropertyMapper<S,D>(expressions));
	}

	/**
	 * 根据属性名和属性类型和表达式来构建属性映射器,表达式构建的映射会覆盖同类型属性之间建立的映射(如果他们冲突了)<p>
	 * 属性名相同,并且属性类型也相同的属性,被认为是same property
	 * @param srcClass
	 * @param destClass
	 * @param specialPropertyExpressions
	 * @return
	 * @throws IntrospectionException
	 */
	public static <S,D> PropertyMapper<S,D> buildMapperWithSameProperty(
			Class<S> srcClass,Class<D> destClass,List<String> specialPropertyExpressions) throws IntrospectionException{
		//TODO 暂时修复该bug,****待优化****
		/*
		 * 由于PropertyMapper的构造器中对表达式生成的映射没有做相应处理,导致单条属性not perpared
		 * 同时,由于在获取源对象与目标对象的相同属性时,只返回了源对象的属性描述符集合,因此现在的版本,经过构造器生成的映射器实际上是不可用的
		 * 必须调用prepareMapping将读写方法填入映射器,覆盖原有读写方法
		 */
		return prepareMapping(srcClass, destClass, 
				new PropertyMapper<S,D>(srcClass, destClass, specialPropertyExpressions, PropertyMapper.PROP_SAME_TYPE));
	}
	
	/**
	 * 根据属性名和表达式来构建属性映射器,表达式构建的映射会覆盖同类型属性之间建立的映射(如果他们冲突了)
	 * @param srcClass
	 * @param destClass
	 * @param specialPropertyExpressions
	 * @return
	 * @throws IntrospectionException
	 */
	public static <S,D> PropertyMapper<S,D> buildMapperWithSameNameProperty(
			Class<S> srcClass,Class<D> destClass,List<String> specialPropertyExpressions) throws IntrospectionException{
		/*
		 * 与buildMapperWithSameProperty有相同的待优化问题
		 */
		return prepareMapping(srcClass, destClass, 
				new PropertyMapper<S,D>(srcClass, destClass, specialPropertyExpressions, PropertyMapper.PROP_SAME_NAME));
	}
	
	/**
	 * 根据映射器规则复制对象属性
	 * @param src		源对象
	 * @param dest		目标对象
	 * @param mapper	映射器
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unchecked")
	public static <S,D> D copyBean(S src,Class<D> destClass,PropertyMapper<S,D> mapper) 
			throws InstantiationException, IllegalAccessException{
		if(!mapper.isPrepared()){
			mapper = prepareMapping((Class<S>)src.getClass(),destClass,mapper);
		}
		D dest = destClass.newInstance();
		for (CopyableProperty cp : mapper.getCopyablePropertySet()) {
			cp.copyProperty(src, dest);
		}
		return dest;
	}
	
	@SuppressWarnings("unchecked")
	public static <S,D> void copyProperties(S src,D dest,PropertyMapper<S,D> mapper){
		if(!mapper.isPrepared()){
			mapper = prepareMapping((Class<S>)src.getClass(),(Class<D>)dest.getClass(),mapper);
		}
		for (CopyableProperty cp : mapper.getCopyablePropertySet()) {
			cp.copyProperty(src, dest);
		}
	}
	
	
	//*********************************************private**************************************************
	
	
	private static <S,D> PropertyMapper<S,D> prepareMapping(Class<S> srcClass,Class<D> destClass,PropertyMapper<S,D> mapper){
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
