# 预计作为一个工具库
...当然,现在只有一个工具


##beancopier 0.10.1
  对象属性复制器,通过构造一个可重复使用的属性映射器(PropertyMapper)提升效率,速度是apache commons-beanutils的5倍以上,多次复制时更高  
  可以通过书写 [映射表达式](#映射表达式) 来定制不同类型bean之间的属性复制  
  **在同时使用反射和表达式来构建映射器的场合(例如`BeanCopier.buildMapperWithSameProperty(Class,Class,List<String>)`),表达式具有更高的优先级**
 ***
 
* **step.1:** 调用`BeanCopier.BuildMapperWith***()`方法来构建映射器(源对象与目标对象属性之间的映射关系)


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

* **step.2:**  调用`BeanCopier.copyBean(Object,Object,PropertyMapper)`
```java
//将源对象映射过的属性复制到目标对象,会覆盖目标对象上的属性
BeanCopier.copyProperties(src,dest,mapper); 
```
  或
```java
//将创建一个新的目标对象,并复制属性,需要无参公共构造器
SystemUser dest = BeanCopier.copyBean(src,SystemUser.class,mapper);
```
来进行属性复制

----

##映射表达式

> 通过字符串表达式定义两个类型之间属性的映射关系  
> 一条完整的表达式形如:src=prop1,dest=prop2,clz=date,format=yyyyMMdd  
> 或:name=prop,clz=date,format=yyyyMMdd  
> 或:!prop(否定表达式,表示不复制这个属性)  
> 或:!name=prop(否定表达式,表示不复制这个属性)  
> 或:!src=prop(否定表达式,表示不复制源对象中名为prop的属性)  
> 或:!dest=prop(否定表达式,表示不复制目标对象中名为prop的属性) 

> src:    源对象属性名  
> dest:   目标对象属性名  
> name:   当源对象和目标对象要copy的属性名一致时,可以使用name=prop的形式来为src和dest同时写入一致的属性名,当填写了src或dest时,此key无效  
> clz:    目标对象属性类型(可省略,默认为string,支持long,int,integer,date,double,decimal,char,character等,详见SupportedClass类),不区分大小写  
> format: 如果源属性是日期字符串,而目标属性是日期,则需要填写源属性字符串格式化为日期的格式(可省略,默认为yyyy-MM-dd HH:mm:ss)  

