package me.lianej.common.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Objects;

/**
 * 工具集
 * @author lianej
 *
 */
final class CopierTools {
	
	private CopierTools(){}
	private static final Log log = LogFactory.getLog(CopierTools.class);
	private static final int TYPE_SRC = 0;
	private static final int TYPE_DEST = 1;
	
	private static final List<String> EMPTY_LIST = Collections.emptyList();
	public static boolean hasText(String str){
		boolean blank = str!=null && !"".equals(str);
		if(!blank) return false;
		for (char c : str.toCharArray()) {
			if (!Character.isWhitespace(c)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 将表达式分解为
	 * @param exps
	 * @return
	 */
	public static PropertySet resolveExp(List<String> exps){
		List<String> result = new ArrayList<>();
		PropertySet set = new PropertySet();
		for (String exp : exps) {
			if(exp.contains(";")){
				String[] splitedExp = exp.split(";");
				for (String se : splitedExp) {
					result.add(se);
				}
			}else{
				result.add(exp);
			}
		}
		for (String exp : result) {
			if(!CopierTools.hasText(exp)){
				continue;
			}
			CopyableProperty cp = new CopyableProperty(exp.trim());
			if(!set.add(cp)){
				log.debug("属性表达式重复:["+exp+"]");
			}
		}
		return set;
	}
	
	public static Set<PropertyDescriptor> findSrcPropertyDescriptors(Class<?> clz,List<String> excludeProp) throws IntrospectionException{
		return findPropertyDescriptors(clz,excludeProp,TYPE_SRC);
	}
	public static Set<PropertyDescriptor> findDestPropertyDescriptors(Class<?> clz,List<String> excludeProp) throws IntrospectionException{
		return findPropertyDescriptors(clz,excludeProp,TYPE_DEST);
	}
	public static Set<PropertyDescriptor> findPropertyDescriptors(Class<?> clz) throws IntrospectionException{
		return findPropertyDescriptors(clz,EMPTY_LIST,TYPE_SRC);
	}
	private static Set<PropertyDescriptor> findPropertyDescriptors(Class<?> clz,List<String> excludeProp,int type) throws IntrospectionException{
		PropertySet set = CopierTools.resolveExp(excludeProp);
		BeanInfo bi = Introspector.getBeanInfo(clz,Object.class);
		Set<PropertyDescriptor> result = new HashSet<>();
		PropertyDescriptor[] pds = bi.getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			String propName = pd.getName();
			if(type == TYPE_SRC	 && set.hasSrcPropName(propName) )	continue;
			if(type == TYPE_DEST && set.hasDestPropName(propName))	continue;
			result.add(pd);
		}
		return result;
	}
	/**
	 * 当源属性和目标属性类型与属性名都相同时返回true,否则返回false
	 * @param srcPd
	 * @param destPd
	 * @return
	 */
	public static boolean isSameProperty(PropertyDescriptor srcPd,PropertyDescriptor destPd){
		Class<?> srcPropType = srcPd.getPropertyType();
		Class<?> destPropType = destPd.getPropertyType();
		return  Objects.equal(srcPropType, destPropType) && isSameNameProperty(srcPd,destPd);
	}
	/**
	 * 当源属性和目标属性属性名相同时返回true,否则返回false
	 * @param srcPd
	 * @param destPd
	 * @return
	 */
	public static boolean isSameNameProperty(PropertyDescriptor srcPd,PropertyDescriptor destPd){
		String srcPropName = srcPd.getName();
		String destPropName = destPd.getName();
		return Objects.equal(srcPropName, destPropName);
	}
	
}
