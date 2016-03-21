package me.lianej.common.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

abstract class IntroceptionUtils {
	public static List<PropertyDescriptor> findPropertyDescriptors(Class<?> clz,List<String> excludeProp) throws IntrospectionException{
		BeanInfo bi = Introspector.getBeanInfo(clz,Object.class);
		List<PropertyDescriptor> result = new ArrayList<>();
		PropertyDescriptor[] pds = bi.getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			String propName = pd.getName();
			if(excludeProp.contains(propName)){
				continue;
			}
			result.add(pd);
		}
		return result;
	}
}
