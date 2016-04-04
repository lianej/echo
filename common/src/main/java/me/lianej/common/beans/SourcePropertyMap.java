package me.lianej.common.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 简陋的multimap,为了避开对guava的引用
 * @author lianej
 *
 */
class SourcePropertyMap{

	private Map<String,Set<CopyableProperty>> map = new HashMap<>();
	
	public void put(String key,CopyableProperty value){
		Set<CopyableProperty> set = map.get(key);
		if(set==null){
			set = new HashSet<>();
			map.put(key, set);
		}
		set.add(value);
	}
	
	public Set<CopyableProperty> get(String key){
		return map.get(key);
	}
	
	public boolean containsKey(String key){
		Set<CopyableProperty> set = map.get(key);
		return set!=null && set.size()>0;
	}
}
