package me.lianej.common.beans;
//还没用到
@Deprecated
public class CopyableBeanPair {
	private Object src;
	private Object dest;
	private PropertyMapper mappings;
	public Object getSrc() {
		return src;
	}
	public Object getDest() {
		return dest;
	}
	public PropertyMapper getMappings() {
		return mappings;
	}
	public CopyableBeanPair(Object src, Object dest, PropertyMapper mappings) {
		super();
		this.src = src;
		this.dest = dest;
		this.mappings = mappings;
	}
}
