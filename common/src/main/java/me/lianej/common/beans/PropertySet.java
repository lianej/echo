package me.lianej.common.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 不可重复的属性列表
 * @author lianej
 *
 */
class PropertySet implements Collection<CopyableProperty> {

//	private static final long serialVersionUID = 3039077769541753751L;
	private Log log = LogFactory.getLog(getClass());
	private List<CopyableProperty> list = new ArrayList<>();
	private Set<String> srcPropNameSet = new HashSet<>();
	private Set<String> destPropNameSet = new HashSet<>();
	@Override
	public boolean add(CopyableProperty e) {
		int index = list.indexOf(e);
		if(index!=-1){
			CopyableProperty src = list.get(index);
			//比较优先级,当已有映射优先级大于等于要添加的映射时,添加失败
			if(src.isPriorTo(e)){
				log.info("映射:"+e+"没有覆盖映射:"+src);
				return false;
			}
		}
		if(list.add(e)){
			srcPropNameSet.add(e.getSrcPropName());
			destPropNameSet.add(e.getDestPropName());
			return true;
		}
		return false;
	}
	@Override
	public boolean addAll(Collection<? extends CopyableProperty> c) {
		boolean modified = false;
		for (CopyableProperty cp : c) {
			if(list.add(cp)){
				modified = true;
			}
		}
		return modified;
	}
	@Override
	public int size() {
		return list.size();
	}
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}
	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}
	@Override
	public Iterator<CopyableProperty> iterator() {
		return list.iterator();
	}
	@Override
	public Object[] toArray() {
		return list.toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}
	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}
	@Override
	public void clear() {
		list.clear();
	}
	public boolean hasSrcPropName(String srcPropName){
		return srcPropNameSet.contains(srcPropName);
	}
	public boolean hasDestPropName(String destPropName){
		return destPropNameSet.contains(destPropName);
	}
}
