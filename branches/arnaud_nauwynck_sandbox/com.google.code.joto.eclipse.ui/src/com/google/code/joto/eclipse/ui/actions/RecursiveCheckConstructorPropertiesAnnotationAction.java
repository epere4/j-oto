package com.google.code.joto.eclipse.ui.actions;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.ICompilationUnit;

import com.google.code.joto.eclipse.ui.refactoring.RecursiveCheckConstructorPropertiesAnnotationHelper;

import java.util.Set;

public class RecursiveCheckConstructorPropertiesAnnotationAction extends AbstractJavaSelectionAction {


	// -------------------------------------------------------------------------
	
	protected String getActionTitle() {
		return "Joto recursive check/add @ConstructorProperties annotations";
	}
	
	protected void doRun(MultiStatus status, IProgressMonitor monitor, StringBuffer resultMessage, Set<ICompilationUnit> compilationUnits) {
		RecursiveCheckConstructorPropertiesAnnotationHelper helper = null;
		try {
			helper = new RecursiveCheckConstructorPropertiesAnnotationHelper(monitor, compilationUnits);
			helper.run();
			
			resultMessage.append("done " + getActionTitle() + ": " 
					+ helper.getStringResult());
		} catch(Exception ex) {
			resultMessage.append("FAILED refactor " + getActionTitle() + ": " 
					+ "ERROR: " + ex.getMessage()
					+ "\n" + helper.getStringResult());
		}
	}

	
}
