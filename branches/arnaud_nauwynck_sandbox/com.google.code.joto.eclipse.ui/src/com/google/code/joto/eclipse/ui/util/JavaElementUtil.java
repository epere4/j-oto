package com.google.code.joto.eclipse.ui.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.internal.corext.util.SuperTypeHierarchyCache;

/**
 * 
 */
public final class JavaElementUtil {

	/** private to force all static */
	private JavaElementUtil() {
	}
	
	// -------------------------------------------------------------------------

	public static ICompilationUnit findCompilationUnit(ITypeBinding classType) {
		String classQN = classType.getQualifiedName();
		IJavaProject jproject = classType.getJavaElement().getJavaProject();
		return findCompilationUnit(jproject, classQN);
	}
	
	public static ICompilationUnit findCompilationUnit(IJavaProject jproject, String fullyQualifiedName) {
		ICompilationUnit res = null;
		try {
			IType type = jproject.findType(fullyQualifiedName);
			res = type.getCompilationUnit();
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}
	
	
	public static boolean containsErrorProblemsMarkers(ICompilationUnit cu) {
		boolean res = false;
		
		try {
			IResource resource = cu.getCorrespondingResource();
			IMarker[] problems = null;
			int depth = IResource.DEPTH_ONE;
			try {
				problems = resource.findMarkers(IMarker.PROBLEM, true, depth);
			} catch (CoreException e) {
				// something went wrong
			}

			if (problems == null || problems.length == 0) {
				res = false;
			} else {
				// found some markers...
				boolean foundPb = false;
				for (IMarker pb : problems) {
					Object severity = pb.getAttribute(IMarker.SEVERITY);
					if (severity.equals(IMarker.SEVERITY_ERROR)) {
						foundPb = true;
						break;
					}
				}
				res = foundPb;
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}


    public static Set<IType> getAllSuperTypes(IJavaProject jproject, String fullyQualifiedName, IProgressMonitor monitor) {
        Set<IType> allSuperTypes = new HashSet<IType>(); 
        
        try {
            IType type = jproject.findType(fullyQualifiedName);
            
            if (type == null) {
                throw new RuntimeException("Impossible to retrieve the IType : " + fullyQualifiedName);
            }
            
            allSuperTypes.addAll(Arrays.asList(getTypeHierarchy(type, monitor).getAllSuperclasses(type)));
            allSuperTypes.addAll(Arrays.asList(getTypeHierarchy(type, monitor).getAllSuperInterfaces(type)));
        } catch (JavaModelException ex) {
            throw new RuntimeException(ex);
        }
        
        return allSuperTypes;
    }
    
    @SuppressWarnings("restriction")
	public static ITypeHierarchy getTypeHierarchy(IType type, IProgressMonitor monitor) throws JavaModelException {
        //Commented because is calculate always the typeHierarchy
        //return type.newSupertypeHierarchy(monitor);
        
        return SuperTypeHierarchyCache.getTypeHierarchy(type);
    }
}
