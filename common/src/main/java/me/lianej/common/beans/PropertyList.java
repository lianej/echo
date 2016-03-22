package me.lianej.common.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyList implements Collection<CopyableProperty> {

//	private static final long serialVersionUID = 3039077769541753751L;
	private Log log = LogFactory.getLog(getClass());
	private List<CopyableProperty> list = new ArrayList<>();
	@Override
	public boolean add(CopyableProperty e) {
		int index = list.indexOf(e);
		if(index!=-1){
			CopyableProperty src = list.get(index);
			if(src.isExcluded()){
				log.info("映射:"+e+"没有覆盖映射:"+src);
				return false;
			}
		}
		return list.add(e); 
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
	
}
