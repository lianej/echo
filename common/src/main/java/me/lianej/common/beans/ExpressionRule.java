package me.lianej.common.beans;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;



class ExpressionRule {
	private static final Pattern expResolver = Pattern.compile("(\\w+)=(\\w+),?");
	private String srcPropName;
	private String destPropName;
	private boolean excluded = false;
	private String destPropType;
	private String privateFormat = null;
	/**
	 * 通过字符串表达式初始化对象<p>
	 * 表达式形如:<code>src=prop1,dest=prop2,clz=date,format=yyyyMMdd</code><p>
	 * 或:<code>name=prop,clz=date,format=yyyyMMdd</code><p>
	 * 或:<code>!prop</code>(否定表达式,表示不复制这个属性)<p>
	 * 或:<code>!name=prop</code>(否定表达式,表示不复制这个属性)<p>
	 * 或:<code>!src=prop</code>(否定表达式,表示不复制源对象中名为prop的属性)<p>
	 * 或:<code>!dest=prop</code>(否定表达式,表示不复制目标对象中名为prop的属性)<p>
	 * 
	 * src: 源对象属性名<p>
	 * dest: 目标对象属性名<p>
	 * name: 当源对象和目标对象要copy的属性名一致时,可以使用name=prop的形式来为src和dest同时写入一致的属性名,<b>当填写了src或dest时,此key无效</b><p>
	 * clz: 目标对象属性类型<b>(可省略,默认为string,支持long,int,integer,date,double,decimal)</b><p>
	 * 
	 * format: 如果源属性是日期字符串,而目标属性是日期,则需要填写源属性字符串格式化为日期的格式<b>(可省略,默认为yyyy-MM-dd HH:mm:ss)</b><p>
	 * @param exp 表达式 
	 */
	ExpressionRule(String exp){
		Assert.hasText(exp);
		Assert.isTrue(!exp.contains(";"),"表达式不能包含分隔符';',多条表达式请分隔后再解析");
		this.excluded = exp.startsWith("!");//记录否定表达式
		//否定表达式
		if(this.excluded){
			Assert.isTrue(!exp.contains(","), "否定表达式不能包含逗号");
			exp = exp.substring(1);
			if(!exp.contains("=")){//否定表达式有简略写法,这里将简略表达式转换成完整表达式来进行下一步解析
				exp = "name="+exp;
			}
		}
		Matcher m = expResolver.matcher(exp);
		String propName = null;
		while(m.find()){
			String value = m.group(2);
			switch(m.group(1).toLowerCase()){
			case "name":
				propName = value;
				break;
			case "src":
				this.srcPropName = value;
				break;
			case "dest":
				this.destPropName = value;
				break;
			case "format":
				this.privateFormat = value;
				break;
			case "clz":
				this.destPropType = value;
				break;
			}
		}
		if(this.srcPropName==null && this.destPropName==null){
			this.srcPropName = this.destPropName = propName;
		}
		Assert.hasText(this.srcPropName,"表达式解析失败:"+exp);
		Assert.hasText(this.destPropName,"表达式解析失败:"+exp);
	}

	public String getSrcPropName() {
		return srcPropName;
	}

	public String getDestPropName() {
		return destPropName;
	}

	public boolean isExcluded() {
		return excluded;
	}

	public String getDestPropType() {
		return destPropType;
	}

	public String getPrivateFormat() {
		return privateFormat;
	}
	
}
