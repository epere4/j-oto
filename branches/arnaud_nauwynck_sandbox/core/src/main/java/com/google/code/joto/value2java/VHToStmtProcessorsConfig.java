package com.google.code.joto.value2java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * used defined + built-ins VHToStmtProcessor to use in ValueHolderToBeanASTStmt
 */
public class VHToStmtProcessorsConfig {

	private List<ObjectVHToStmtProcessor> objProcessors = 
		new ArrayList<ObjectVHToStmtProcessor>();

	private List<RefObjectVHToStmtProcessor> refObjProcessors =
		new ArrayList<RefObjectVHToStmtProcessor>();
	
	/** lazy computed field, from objProcessors */
	private Map<Class,List<ObjectVHToStmtProcessor>> cachedClassToObjProcessors = 
		new HashMap<Class,List<ObjectVHToStmtProcessor>>(); 
	
	/** lazy computed field, from objProcessors */
	private Map<RefClassToClassKey,List<RefObjectVHToStmtProcessor>> cachedRefKeyToRefObjProcessors = 
		new HashMap<RefClassToClassKey,List<RefObjectVHToStmtProcessor>>(); 

	
	//-------------------------------------------------------------------------

	public VHToStmtProcessorsConfig() {
	}

	//-------------------------------------------------------------------------
	
	public void addObjProcessor(ObjectVHToStmtProcessor p) { 
		objProcessors.add(p);
		cachedClassToObjProcessors.clear(); // clear corresponding cache results
	}

	public void addAllObjProcessors(Collection<ObjectVHToStmtProcessor> p) { 
		objProcessors.addAll(p);
		cachedClassToObjProcessors.clear(); // clear corresponding cache results
	}

	public void addRefObjProcessor(RefObjectVHToStmtProcessor p) { 
		refObjProcessors.add(p);
		cachedRefKeyToRefObjProcessors.clear(); // clear corresponding cache results
	}

	public void addAllRefObjProcessors(Collection<RefObjectVHToStmtProcessor> p) { 
		refObjProcessors.addAll(p);
		cachedRefKeyToRefObjProcessors.clear(); // clear corresponding cache results
	}

	// -------------------------------------------------------------------------

	/**
	 * internal
	 */
	private static class RefClassToClassKey {
		Class fromClass;
		// link?
		Class toClass;
	
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((fromClass == null) ? 0 : fromClass.hashCode());
			result = prime * result
					+ ((toClass == null) ? 0 : toClass.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RefClassToClassKey other = (RefClassToClassKey) obj;
			if (fromClass == null) {
				if (other.fromClass != null)
					return false;
			} else if (!fromClass.equals(other.fromClass))
				return false;
			if (toClass == null) {
				if (other.toClass != null)
					return false;
			} else if (!toClass.equals(other.toClass))
				return false;
			return true;
		}
		
	}

}
