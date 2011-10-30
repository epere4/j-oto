package com.google.code.joto.ui.tree;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import com.google.code.joto.util.CompoundEnumeration;
import com.google.code.joto.util.SortedListTreeMap;

/**
 *
 */
public class AggrRecordEventTemplateTreeNodeAST {

	/**
	 *
	 */
	public static abstract class AbstractAggrEventTreeNode implements TreeNode, Serializable {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private AbstractAggrEventTreeNode parent;
		private String name;
		
		protected AbstractAggrEventTreeNode(AbstractAggrEventTreeNode parent, String name) {
			this.parent = parent;
			this.name = name;
		}

		public AbstractAggrEventTreeNode getParent() {
			return parent;
		}

		public String getName() {
			return name;
		}
		
		@Override
		public boolean getAllowsChildren() {
			return true;
		}

		@Override
		public boolean isLeaf() {
			return false;
		}

		public String toString() {
			return name;
		}
	}
	
	/**
	 *
	 */
	public static class PackageAggrEventTreeNode extends AbstractAggrEventTreeNode {
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private String fullPackageName;

		private SortedListTreeMap<String,PackageAggrEventTreeNode> childPackageTreeNode = new SortedListTreeMap<String,PackageAggrEventTreeNode>();
		private SortedListTreeMap<String,ClassAggrEventTreeNode> childClassTreeNode = new SortedListTreeMap<String,ClassAggrEventTreeNode>();

		// ------------------------------------------------------------------------
		
		public PackageAggrEventTreeNode(PackageAggrEventTreeNode parent, String packageName) {
			super(parent, packageName);
			this.fullPackageName = (parent != null)? parent.getFullPackageName() + "." + packageName : "";
			
		}

		// ------------------------------------------------------------------------
		
		public String getFullPackageName() {
			return fullPackageName;
		}

		public String getChildPackageName() {
			return super.getName();
		}

		public PackageAggrEventTreeNode getOrCreateChildPackage(String childName) {
			PackageAggrEventTreeNode res = childPackageTreeNode.get(childName);
			if (res == null) {
				res = new PackageAggrEventTreeNode(this, childName); 
				childPackageTreeNode.put(childName, res);
			}
			return res;
		}

		public ClassAggrEventTreeNode getOrCreateChildClass(String simpleClassName) {
			ClassAggrEventTreeNode res = childClassTreeNode.get(simpleClassName);
			if (res == null) {
				res = new ClassAggrEventTreeNode(this, simpleClassName); 
				childClassTreeNode.put(simpleClassName, res);
			}
			return res;
		}

		// implements TreeNode
		// ------------------------------------------------------------------------
		
		@Override
		public TreeNode getChildAt(int childIndex) {
			TreeNode res;
			if (childIndex < childPackageTreeNode.size()) {
				res = childPackageTreeNode.getAt(childIndex);
			} else {
				int classIndex = childIndex - childPackageTreeNode.size();
				res = childClassTreeNode.getAt(classIndex);
			}
			return res;
		}

		@Override
		public int getChildCount() {
			return childPackageTreeNode.size() + childClassTreeNode.size();
		}

		@Override
		public int getIndex(TreeNode node) {
			int res = -1;
			if (node instanceof PackageAggrEventTreeNode) {
				res = childPackageTreeNode.indexOf(node);
			} else if (node instanceof ClassAggrEventTreeNode) {
				res = childClassTreeNode.indexOf(node);
			}
			return res;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Enumeration<AbstractAggrEventTreeNode> children() {
			return new CompoundEnumeration(
					childPackageTreeNode.enumeration(), 
					childClassTreeNode.enumeration());
		}
		
		
	}
	
	/**
	 *
	 */
	public static class RootPackageAggrEventTreeNode extends PackageAggrEventTreeNode {
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		public RootPackageAggrEventTreeNode() {
			super(null, "");
		}

		public String toString() {
			return "(root package)";
		}
	}
	
	/**
	 * 
	 */
	public static class ClassAggrEventTreeNode extends AbstractAggrEventTreeNode {
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		// private String className; ... implicit from parent fullPackageName + simpleClassName

		private SortedListTreeMap<String,MethodAggrEventTreeNode> childMethods = new SortedListTreeMap<String,MethodAggrEventTreeNode>();
		
		// ------------------------------------------------------------------------
		
		public ClassAggrEventTreeNode(PackageAggrEventTreeNode parentPackage, String simpleClassName) {
			super(parentPackage, simpleClassName);
		}

		// ------------------------------------------------------------------------
		
		public String getSimpleClassName() {
			return super.getName();
		}

		// implements TreeNode
		// ------------------------------------------------------------------------
		
		@Override
		public TreeNode getChildAt(int childIndex) {
			return childMethods.getAt(childIndex);
		}

		@Override
		public int getChildCount() {
			return childMethods.size();
		}

		@Override
		public int getIndex(TreeNode node) {
			return childMethods.indexOf(node);
		}

		@Override
		public Enumeration<MethodAggrEventTreeNode> children() {
			return childMethods.enumeration();
		}
		
	}
	
	/**
	 * 
	 */
	public static class MethodAggrEventTreeNode extends AbstractAggrEventTreeNode {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		// implicit from super.getName() ... private String methodSignature;
		
		private SortedListTreeMap<String,TemplateMethodCallAggrEventTreeNode> childTemplateCalls = new SortedListTreeMap<String,TemplateMethodCallAggrEventTreeNode>();
		
		// ------------------------------------------------------------------------
		
		public MethodAggrEventTreeNode(ClassAggrEventTreeNode parent, Method meth) {
			super(parent, meth.getName());
		}

		// ------------------------------------------------------------------------
		
		public String getMethodName() {
			return super.getName();
		}

		// implements TreeNode
		// ------------------------------------------------------------------------
		
		@Override
		public TreeNode getChildAt(int childIndex) {
			return childTemplateCalls.getAt(childIndex);
		}

		@Override
		public int getChildCount() {
			return childTemplateCalls.size();
		}

		@Override
		public int getIndex(TreeNode node) {
			return childTemplateCalls.indexOf(node);
		}

		@Override
		public Enumeration<TemplateMethodCallAggrEventTreeNode> children() {
			return childTemplateCalls.enumeration();
		}
		
	}
	
	/**
	 * 
	 */
	public static class TemplateMethodCallAggrEventTreeNode extends AbstractAggrEventTreeNode {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		// implicit from super.getName() ... private String methodSignature;
		
		private Object[] templateArgs;
		
		// ------------------------------------------------------------------------
		
		public TemplateMethodCallAggrEventTreeNode(MethodAggrEventTreeNode parent, String templateKey, Object[] templateArgs) {
			super(parent, templateKey);
		}

		// implements TreeNode
		// ------------------------------------------------------------------------

		@Override
		public boolean getAllowsChildren() {
			return false;
		}

		@Override
		public boolean isLeaf() {
			return true;
		}

		@Override
		public TreeNode getChildAt(int childIndex) {
			return null;
		}

		@Override
		public int getChildCount() {
			return 0;
		}

		@Override
		public int getIndex(TreeNode node) {
			return -1;
		}

		@Override
		public Enumeration<?> children() {
			return null;
		}
		
	}
	
}
