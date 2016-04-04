package me.lianej.common.test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.util.Assert;

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
		SAPUser src = new SAPUser("zhangsan","111111",d1,d1);
		PropertyMapper<SAPUser,SAPUser> mapper = BeanCopier.buildMapperWithSameBeantype(SAPUser.class);
		List<SAPUser> copy = copy(src,SAPUser.class,mapper);
		SAPUser dest = copy.get(0);
		System.out.println(copy.get(0));
		Assert.isTrue(src.getName().equals(dest.getName()));
		Assert.isTrue(src.getCreateDate().equals(dest.getCreateDate()));
		Assert.isTrue(src.getLastLoginDate().equals(dest.getLastLoginDate()));
		Assert.isTrue(src.getPwd().equals(dest.getPwd()));
		Assert.isTrue(src.getRole()==null);
	}
	
	@Test
	public void test2() throws Exception{
		System.out.println("测试2:异构对象属性复制,完全基于表达式");
		SAPUser src = new SAPUser("zhangsan","111111",new Date(),new Date());
		List<String> exps = new ArrayList<>();
		exps.add("src=name,dest=username");
		exps.add("src=pwd,dest=password");
		exps.add("src=role,dest=role,clz=int");
		exps.add("src=lastLoginDate,dest=loginTime,clz=date");
		PropertyMapper<SAPUser,SystemUser> mapper = BeanCopier.buildMapperWithExpressions(SAPUser.class, SystemUser.class, exps);
		List<SystemUser> copy = copy(src,SystemUser.class,mapper);
		System.out.println(copy.get(0));
	}
	
	@Test
	public void test3() throws Exception {
		System.out.println("测试3:根据反射和表达式共同决定映射关系");
		SAPUser src = new SAPUser("zhangsan","111111",new Date(),new Date());
		src.setId(1L);
		List<String> exps = new ArrayList<>();
		exps.add("src=name,dest=username");
		exps.add("src=pwd,dest=password");
		exps.add("src=role,dest=role,clz=int");
		exps.add("src=lastLoginDate,dest=loginTime,clz=date");
		PropertyMapper<SAPUser,SystemUser> mapper = BeanCopier.buildMapperWithSameProperty(SAPUser.class, SystemUser.class, exps);
		
		List<SystemUser> list = copy(src,SystemUser.class,mapper);
		System.out.println(list.get(0));
		
	}
	
	
	private <SRC,DEST> List<DEST> copy(SRC src,Class<DEST> destClass,PropertyMapper<SRC,DEST> mapper) throws Exception{
		long begin = System.currentTimeMillis();
		List<DEST> result = new ArrayList<>();
		for (int i = 0; i < outLoop; i++) {
			long begin2 = System.currentTimeMillis();
			for (int j = 0; j < innerLoop; j++) {
				result.add(BeanCopier.copyBean(src, destClass, mapper));
			}
			System.out.println("第"+i+"次小循环:"+(System.currentTimeMillis()-begin2));
		}
		System.out.println(System.currentTimeMillis()-begin);
		return result;
	}
	
}
