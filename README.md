# 预计作为一个工具库
...当然,现在只有一个工具


##beancopier
  对象复制器,效率是apache commons-beanutils的5倍以上,多次复制效率更高
 
 ***
 
* **step.1:**
  调用`BeanCopier.BuildMapperWith***()`方法来构建映射器(源对象与目标对象属性之间的映射关系)


* step.1-A:当源对象和目标对象是同一个类;
  使用`BeanCopier.buildMapperWithSametype(Class)`来构建映射器
```java
List<String> exps = new ArrayList<>();
//按照需求可以添加表达式,也可以为空
PropertyMapper mapper = BeanCopier.buildMapperWithSameBeantype(SAPUser.class,exps);
```

* step.1-B:当源对象和目标对象不是同一个类时,完全使用表达式来建立映射;
  使用`BeanCopier.buildMapperWithExpressions(Class,Class,List<String>)`来构建映射器
```java
List<String> exps = new ArrayList<>();
exps.add("src=name,dest=username");
exps.add("src=pwd,dest=password");
exps.add("src=role,dest=role,clz=int");
exps.add("src=lastLoginDate,dest=loginTime,clz=date");
PropertyMapper mapping = BeanCopier.buildMapperWithExpressions(SAPUser.class, SystemUser.class, exps);
```

* step.1-C:当源对象和目标对象不是同一个类时,根据源对象和目标对象的属性一致(属性名和属性类型一致)来建立映射,并使用表达式修正;
  使用使用`BeanCopier.buildMapperWithSameProperty(Class,Class,List<String>)`来构建映射器
```java
//TODO
```

* step.1-D::当源对象和目标对象不是同一个类时,根据源对象和目标对象的属性名一致(不考虑属性类型是否一致)来建立映射,并使用表达式修正;
  使用使用`BeanCopier.buildMapperWithSameNameProperty(Class,Class,List<String>)`来构建映射器
```java
//TODO
```

* **step.2:**
  调用`BeanCopier.copyBean(Object,Object,PropertyMapper)`来进行属性复制
```java
BeanCopier.copyBean(src,dest,mapper);
```


#####映射表达式
```
//TODO 
```
