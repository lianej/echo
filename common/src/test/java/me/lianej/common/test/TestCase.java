package me.lianej.common.test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.util.Assert;

import me.lianej.common.beans.BeanCopier;
import me.lianej.common.beans.PropertyMapper;

public class TestCase {
	private Log log = LogFactory.getLog(getClass());
	private int outLoop = 15;
	private int innerLoop = 10;
	@Test
	public void test1() throws Exception{
		System.out.println("测试1:同构对象复制");
		Date d1 = new Date();
		SAPUser src = new SAPUser("zhangsan","111111",1,d1,d1);
		PropertyMapper mapper = BeanCopier.buildMapperWithSametype(SAPUser.class);
		copy(src,SystemUser.class,mapper);
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
		System.out.println("测试3:");
		
	}
	
	
	private void copy(Object src,Class<?> destClass,PropertyMapper mapper) throws Exception{
		long begin = System.currentTimeMillis();
		for (int i = 0; i < outLoop; i++) {
			long begin2 = System.currentTimeMillis();
			for (int j = 0; j < innerLoop; j++) {
				Object dest = destClass.newInstance();
				BeanCopier.copyBean(src, dest, mapper);//异构拷贝
			}
			System.out.println("第"+i+"次小循环:"+(System.currentTimeMillis()-begin2));
		}
		System.out.println(System.currentTimeMillis()-begin);
	}
	
}
