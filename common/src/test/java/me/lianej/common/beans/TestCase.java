package me.lianej.common.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class TestCase {
	
	private int outLoop = 15;
	private int innerLoop = 100;
	
	@Test
	public void test1() throws Exception{
		System.out.println("test1---------------------begin");
		long begin = System.currentTimeMillis();
		Demo src = new Demo("1990-12-11 12:22:00",999,new Date());
		PropertyMapper mapper = new PropertyMapper(Demo.class);
		for (int i = 0; i < outLoop; i++) {
			long begin2 = System.currentTimeMillis();
			for (int j = 0; j < innerLoop; j++) {
				Demo dest = new Demo();
				BeanCopier.copyBean(src, dest, mapper);
			}
			System.out.println("第"+i+"次小循环:"+(System.currentTimeMillis()-begin2));
		}
		System.out.println(System.currentTimeMillis()-begin);
		System.out.println("test1---------------------end");
	}
	
	@Test
	public void test2() throws Exception{
		System.out.println("test2---------------------begin");
		long begin = System.currentTimeMillis();
		Demo src = new Demo("1990-12-11 12:22:00",999,new Date());
		List<String> exps = new ArrayList<>();
		exps.add("src=prop1,dest=arg1,clz=date");
		exps.add("src=prop1,dest=arg4,clz=date");
		exps.add("src=prop2,dest=arg2,clz=int");
		exps.add("src=prop3,dest=arg3,clz=string");
		PropertyMapper mapping = new PropertyMapper(exps);
		for (int i = 0; i < outLoop; i++) {
			long begin2 = System.currentTimeMillis();
			for (int j = 0; j < innerLoop; j++) {
				Demo2 dest = new Demo2();
				BeanCopier.copyBean(src, dest, mapping);//异构拷贝
//				System.out.println(dest);
			}
			System.out.println("第"+i+"次小循环:"+(System.currentTimeMillis()-begin2));
		}
		System.out.println(System.currentTimeMillis()-begin);
		System.out.println("test2---------------------end");
	}
	
}

class Demo{
	private String prop1;
	private Integer prop2;
	private Date prop3;
	public String getProp1() {
		return prop1;
	}
	public void setProp1(Object prop1) {
		this.prop1 = prop1.toString();
	}
	public void setProp1(String prop1) {
		this.prop1 = prop1;
	}
	public Integer getProp2() {
		return prop2;
	}
	public void setProp2(Integer prop2) {
		this.prop2 = prop2;
	}
	public Date getProp3() {
		return prop3;
	}
	public void setProp3(Date prop3) {
		this.prop3 = prop3;
	}
	@Override
	public String toString() {
		return "Demo [prop1=" + prop1 + ", prop2=" + prop2 + ", prop3=" + prop3
				+ "]";
	}
	public Demo(String prop1, Integer prop2, Date prop3) {
		super();
		this.prop1 = prop1;
		this.prop2 = prop2;
		this.prop3 = prop3;
	}
	public Demo(){}
}
class Demo2{
	private Date arg1;
	private Integer arg2;
	private String arg3;
	private Date arg4;
	public Date getArg1() {
		return arg1;
	}
	public void setArg1(Date arg1) {
		this.arg1 = arg1;
	}
	public Integer getArg2() {
		return arg2;
	}
	public void setArg2(Integer arg2) {
		this.arg2 = arg2;
	}
	public String getArg3() {
		return arg3;
	}
	public void setArg3(String arg3) {
		this.arg3 = arg3;
	}
	
	public Date getArg4() {
		return arg4;
	}
	public void setArg4(Date arg4) {
		this.arg4 = arg4;
	}
	@Override
	public String toString() {
		return "Demo2 [arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3 + ", arg4=" + arg4 + "]";
	}
	
}
