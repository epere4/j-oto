package com.google.code.joto.eclipse.ui.refactoring;



import java.util.Map;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.TextEdit;

import com.google.code.joto.eclipse.ui.Activator;
import com.google.code.joto.eclipse.ui.util.ConsoleUtil;
import com.google.code.joto.eclipse.ui.util.JavaASTUtil;
import com.google.code.joto.eclipse.ui.util.UiUtil;


/**
 * 
 */
public abstract class AbstractASTRewriteRefactoringHelper extends AbstractCompilationUnitRefactoringHelper {
		
	// -------------------------------------------------------------------------

	protected AbstractASTRewriteRefactoringHelper(IProgressMonitor monitor, Set<ICompilationUnit> compilationUnits) {
		super(monitor, compilationUnits);
	}

	// -------------------------------------------------------------------------

	protected abstract Object prepareRefactorUnit(CompilationUnit unit) throws Exception;
	
	protected abstract void doRefactorUnit(CompilationUnit unit, Object preparedParams) throws Exception;
	
	
	// -------------------------------------------------------------------------

	protected void handleCompilationUnit(ICompilationUnit cu) {
		monitor.worked(1);
		monitor.subTask(cu.getElementName());

		try {
			CompilationUnit unit = JavaASTUtil.parseCompilationUnit(cu, monitor);

			currentUnit = (ICompilationUnit) unit.getJavaElement();

			handleUnit(unit);

		} catch(Exception ex) {
			addErrorMsg("Failed to execute refactoring in unit " + cu.getElementName() + ":" + ex.toString());
			Activator.logWarning("Failed to execute refactoring in unit " + cu.getElementName(), ex);
		} finally {
			currentUnit = null;
		}
	}

	protected void handleUnit(CompilationUnit unit) throws Exception {
		Object preparedRefactoredParams;

		unit.recordModifications();

		try {
			// *** the Biggy : prepare refactoring ***
			preparedRefactoredParams = prepareRefactorUnit(unit);
		} catch(CancelRefactorUnitException ex) {
			return;
		}
		if (preparedRefactoredParams == null) {
			return;
		} else {
			countReplacement++;
			IPath path = unit.getJavaElement().getPath();
			ConsoleUtil.debug("writing changes to %s", path.toString());
	
			// *** The biggy ***
			refactorAndRewriteDocument(unit, preparedRefactoredParams);
		}		
	}


	private void refactorAndRewriteDocument(final CompilationUnit unit, final Object preparedRefactoredParams) throws CoreException {
		if (UiUtil.isSWTGraphicsThread()) {
			syncRefactorAndRewriteDocument(unit, preparedRefactoredParams);
        } else {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                	syncRefactorAndRewriteDocument(unit, preparedRefactoredParams);
                }
            });                            
        }
	}

	
	private void syncRefactorAndRewriteDocument(CompilationUnit unit, Object preparedRefactoredParams) {
		try {
			ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
			IPath path = unit.getJavaElement().getPath();
			bufferManager.connect(path, monitor);
			try {
		
				// *** the Biggy : do refactoring ***
				doRefactorUnit(unit, preparedRefactoredParams);
	
				rewriteASTDocument(unit, path, bufferManager);
	
			} catch(Exception ex) {
				Activator.logWarning("Failed to refactor compilation unit " + unit.getJavaElement().getElementName(), ex);
				// ignore.. no rethrow!!
			} finally {
				bufferManager.disconnect(path, monitor);
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}


	private void rewriteASTDocument(CompilationUnit unit, IPath path, ITextFileBufferManager bufferManager) {
		try {
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path);
			// retrieve the buffer
			IDocument document = textFileBuffer.getDocument();
	
			Map options = JavaCore.getOptions();
			TextEdit textEdit = unit.rewrite(document, options);
			textEdit.apply(document);
	
			// commit changes to underlying file
			textFileBuffer.commit(monitor, false);
		} catch(Exception ex) {
    		throw new RuntimeException(ex);
    	}
	}
	

	 
}
