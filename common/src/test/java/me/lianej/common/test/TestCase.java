package me.lianej.common.test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import me.lianej.common.beans.BeanCopier;
import me.lianej.common.beans.PropertyMapper;

public class TestCase {
//	private Log log = LogFactory.getLog(getClass());
	private int outLoop = 2;
	private int innerLoop = 1000;
	@Test
	public void test1() throws Exception{
		System.out.println("测试1:同构对象复制");
		Date d1 = new Date();
		SAPUser src = new SAPUser("zhangsan","111111",1,d1,d1);
		PropertyMapper mapper = BeanCopier.buildMapperWithSameBeantype(SAPUser.class);
		copy(src,SAPUser.class,mapper);
	}
	
	@Test
	public void test2() throws Exception{
		System.out.println("测试2:异构对象属性复制,完全基于表达式");
		SAPUser src = new SAPUser("zhangsan","111111",1,new Date(),new Date());
		List<String> exps = new ArrayList<>();
		exps.add("src=name,dest=username");
		exps.add("src=pwd,dest=password");
		exps.add("src=role,dest=role,clz=int");
		exps.add("src=lastLoginDate,dest=loginTime,clz=date");
		PropertyMapper mapping = BeanCopier.buildMapperWithExpressions(SAPUser.class, SystemUser.class, exps);
		copy(src,SystemUser.class,mapping);
	}
	
	@Test
	public void test3() throws Exception {
		System.out.println("测试3:根据反射和表达式共同决定映射关系");
		SAPUser src = new SAPUser("zhangsan","111111",1,new Date(),new Date());
		src.setId(1L);
		List<String> exps = new ArrayList<>();
		exps.add("src=name,dest=username");
		exps.add("src=pwd,dest=password");
		exps.add("src=role,dest=role,clz=int");
		exps.add("src=lastLoginDate,dest=loginTime,clz=date");
		PropertyMapper mapper = BeanCopier.buildMapperWithSameProperty(SAPUser.class, SystemUser.class, exps);
		
		List<SystemUser> list = copy(src,SystemUser.class,mapper);
		System.out.println(list.get(0));
		
	}
	
	
	private <T> List<T> copy(Object src,Class<T> destClass,PropertyMapper mapper) throws Exception{
		long begin = System.currentTimeMillis();
		List<T> result = new ArrayList<>();
		for (int i = 0; i < outLoop; i++) {
			long begin2 = System.currentTimeMillis();
			for (int j = 0; j < innerLoop; j++) {
				T dest = destClass.newInstance();
				BeanCopier.copyBean(src, dest, mapper);//异构拷贝
				result.add(dest);
			}
			System.out.println("第"+i+"次小循环:"+(System.currentTimeMillis()-begin2));
		}
		System.out.println(System.currentTimeMillis()-begin);
		return result;
	}
	
}
