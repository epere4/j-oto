package com.google.code.joto.reflect;

import java.util.HashMap;
import java.util.Map;

public class ClassDictionaryJotoInfo {

	private Map<Class,ClassJotoInfo> classMap = new HashMap<Class,ClassJotoInfo>();
	
	// -------------------------------------------------------------------------
	
	public ClassDictionaryJotoInfo() {
	}

	// -------------------------------------------------------------------------
	
	public ClassJotoInfo getClassInfo(Class<?> p) {
		ClassJotoInfo res = classMap.get(p);
		if (res == null) {
			res = doGetClassInfo(p);
			classMap.put(p, res);
		}
		return res;
	}
	
	private ClassJotoInfo doGetClassInfo(Class<?> p) {
		ClassJotoInfo res = new ClassJotoInfo(p);
		
		// TODO 
		return res;
	}
	
}
