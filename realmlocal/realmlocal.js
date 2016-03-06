~function(window,$){
	"use strict";

	var typePrefix = '__rl_type__';

	if(!window.localStorage){
		$.error('浏览器不支持localStorage');
	}

	function RealmLocal(){};
	/**
	 * 注意:object和array会作为json存储
	 */
	RealmLocal.prototype.put = function(key,value){
		var valueType = $.type(value);
		var processedValue = '';
		switch(valueType){
			case 'object':
			case 'array':
				var json2str = $.toJSON || (window.JSON ? window.JSON.stringify : undefined);
				if(!json2str){
					$.error('当前浏览器没有源生的json序列化支持,需要引入JSON.js');
				}
				processedValue = json2str(value);
				break;
			case 'regexp':				
			case 'number':
			case 'boolean':
			case 'string':
				processedValue = value;
				break;
			case 'date':
				processedValue = +value+'';
				break;
			case 'function':
				$.error('无法储存/获取函数对象');
			default : $.error('未知的value类型:'+valueType);
		}
		window.localStorage.setItem(key,processedValue);
		window.localStorage.setItem(typePrefix+key,valueType);
	};

	RealmLocal.prototype.get = function(key){
		var valueType = window.localStorage.getItem(typePrefix+key);
		var value = window.localStorage.getItem(key);
		switch(valueType){
			case 'string':
				return value;
			case 'object':
			case 'array':
				return $.parseJSON(value);
			case 'number':
				return parseFloat(value);
			case 'date':
				return new Date(+value);
			case 'regexp':	
				return eval(value);			
			case 'boolean':
				return value === 'true' ? true : false;
			case 'function':
				$.error('无法储存/获取函数对象');
			default : $.error('未知的value类型:'+valueType);
		}
	};
	RealmLocal.prototype.remove = function(key){
		window.localStorage.removeItem(key);
		window.localStorage.removeItem(typePrefix+key);
	};

	RealmLocal.prototype.clear = function(){
		window.localStorage.clear();
	};

	$.rl = new RealmLocal();

}(window,jQuery);