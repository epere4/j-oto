package com.google.code.joto.eclipse.ui.util;

import org.eclipse.jdt.core.ICompilationUnit;

/**
 * 
 */
public interface ICompilationUnitFilter {

	public boolean accept(ICompilationUnit cu);
	
}
