package com.google.code.joto.value2java;

import java.util.List;

import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;

/**
 * delegate class for implementing "object to stmt" part of the algorithm in 
 * class ValueHolderToBeanASTStmt.
 * 
 * implementations sub-classes must be thread safe, and stateless,
 * but the owner ValueHolderToBeanASTStmt is used as a context/callback for processing.
 */
public interface ObjectVHToStmtProcessor {

	public List<Class<?>> getTargetSubClassOrInterfaceForProcess(ValueHolderToBeanASTStmt owner);

	public boolean canProcess(ValueHolderToBeanASTStmt owner, 
				AbstractObjectValueHolder obj);
	
	public void process(ValueHolderToBeanASTStmt owner,
				ValueHolderToBeanASTStmt obj,
				Object2ASTInfo objInfo);
	
}
