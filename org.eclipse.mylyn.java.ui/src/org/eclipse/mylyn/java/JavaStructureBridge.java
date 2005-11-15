/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Apr 7, 2005
  */
package org.eclipse.mylar.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.mylar.core.AbstractRelationProvider;
import org.eclipse.mylar.core.IDegreeOfSeparation;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.DegreeOfSeparation;
import org.eclipse.mylar.java.search.JUnitReferencesProvider;
import org.eclipse.mylar.java.search.JavaImplementorsProvider;
import org.eclipse.mylar.java.search.JavaReadAccessProvider;
import org.eclipse.mylar.java.search.JavaReferencesProvider;
import org.eclipse.mylar.java.search.JavaWriteAccessProvider;
import org.eclipse.ui.views.markers.internal.ProblemMarker;

/**
 * @author Mik Kersten
 */
public class JavaStructureBridge implements IMylarStructureBridge {

    public final static String CONTENT_TYPE = "java";
    
    public List<AbstractRelationProvider> providers;
    
    public JavaStructureBridge() {
    	providers = new ArrayList<AbstractRelationProvider>();
    	providers.add(new JavaReferencesProvider());
    	providers.add(new JavaImplementorsProvider());
    	providers.add(new JavaReadAccessProvider());
    	providers.add(new JavaWriteAccessProvider()); 
    	providers.add(new JUnitReferencesProvider());
    }
    
    public String getContentType() {
        return CONTENT_TYPE;
    }
    
    public String getParentHandle(String handle) {
        IJavaElement javaElement = (IJavaElement)getObjectForHandle(handle);
        if (javaElement != null && javaElement.getParent() != null) {            
            return getHandleIdentifier(javaElement.getParent());
        } else {
            return null;
        } 
    }

    public Object getObjectForHandle(String handle) {
    	try {
    		return JavaCore.create(handle);
    	} catch (Throwable t) {
    		MylarPlugin.log("Could not create java element for handle: " + handle, this);
    		return null;
    	}
    }
    
    /**
     * Uses resource-compatible path for projects
     */
    public String getHandleIdentifier(Object object) {
        if (object == null || !(object instanceof IJavaElement)) {
        	return null;
        } else {
        	return ((IJavaElement)object).getHandleIdentifier();
        }
    }

    public String getName(Object object) {
        if (object instanceof IJavaElement) {
            return ((IJavaElement)object).getElementName();
        } else {
            return "";
        }
    }
    
    public boolean canBeLandmark(String handle) {
    	IJavaElement element = (IJavaElement)getObjectForHandle(handle);
    	if ((element instanceof IMember || element instanceof IType) && element.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * TODO: figure out if the non IJavaElement stuff is needed
     */
    public boolean acceptsObject(Object object) {
        boolean accepts = object instanceof IJavaElement 
            || object instanceof ClassPathContainer
            || object instanceof ClassPathContainer.RequiredProjectWrapper
            || object instanceof JarEntryFile
            || object instanceof IPackageFragment
            || object instanceof IJavaProject; // TODO: redundant?
        return accepts;
    }

    /**
     * Uses special rules for classpath containers since these do not have an associated interest,
     * i.e. they're not IJavaElement(s).
     */
    public boolean canFilter(Object object) {
//    	if (object instanceof IJavaElement) {
//    		try {
//	    		IJavaElement element = (IJavaElement)object;
//	            IResource resource = element.getCorrespondingResource();
//	            boolean hasError = false; 
//	            if (resource != null) {
//		            IMarker[] markers = resource.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, 2);
//		            for (int j = 0; j < markers.length; j++) {
//		                if (markers[j] != null
//		                	&& markers[j].getAttribute(IMarker.SEVERITY) != null
//		                	&& markers[j].getAttribute(IMarker.SEVERITY).equals(IMarker.SEVERITY_ERROR)) {
//		                    hasError = true;
//		                } 
//		            } 
//		            if (hasError) return false;
//	            }
//			} catch (CoreException e) {
//				// ignore
//			}
//    	}  
    	
        if (object instanceof ClassPathContainer.RequiredProjectWrapper) {
            return true;
        }
        if (object instanceof ClassPathContainer) { // HACK: check if it has anything interesting
            ClassPathContainer container = (ClassPathContainer)object;
            
            Object[] children = container.getChildren(container);
            for (int i = 0; i < children.length; i++) {
                if (children[i] instanceof JarPackageFragmentRoot) {
                    JarPackageFragmentRoot element = (JarPackageFragmentRoot)children[i];
                    IMylarElement node = MylarPlugin.getContextManager().getElement(element.getHandleIdentifier());
                    if (node != null && node.getInterest().isInteresting()) {
                    	return false;
                    } 
                } 
            }
        }
        return true;
    }

    public boolean isDocument(String handle) {
        IJavaElement element = (IJavaElement)getObjectForHandle(handle);
        return element instanceof ICompilationUnit || element instanceof IClassFile;
    }

	public String getHandleForOffsetInObject(Object resource, int offset) {
        if (resource == null || !(resource instanceof ProblemMarker)) return null;
    	ProblemMarker marker = (ProblemMarker)resource;
        try {
            IResource res= marker.getResource();
            ICompilationUnit compilationUnit = null;
            if (res instanceof IFile) {
                IFile file = (IFile)res; 
                if (file.getFileExtension().equals("java")) { // TODO: instanceof instead?
                    compilationUnit = JavaCore.createCompilationUnitFrom(file);
                } else {
                    return null;
                }
            }
            if (compilationUnit != null) {  
                IJavaElement javaElement= compilationUnit.getElementAt(marker.getMarker().getAttribute(IMarker.CHAR_START, 0));
                if (javaElement != null) {
                    if (javaElement instanceof IImportDeclaration) javaElement = javaElement.getParent().getParent();
                    return javaElement.getHandleIdentifier();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (JavaModelException ex) {
            if (!ex.isDoesNotExist())
                ExceptionHandler.handle(ex, "error", "could not find java element"); //$NON-NLS-2$ //$NON-NLS-1$
            return null;  
        } catch (Throwable t) {
            MylarPlugin.fail(t, "Could not find element for: " + marker, false);
            return null;
        }
    }

	public IProject getProjectForObject(Object object) {
		if(object instanceof IJavaElement){
			if(((IJavaElement)object).getJavaProject() == null)
				return null;
			return ((IJavaElement)object).getJavaProject().getProject();
		}else if(object instanceof IResource){
			return ((IResource)object).getProject();
		}
		return null;
	}

    public String getContentType(String elementHandle) {
        return getContentType();
    }

	public List<AbstractRelationProvider> getRelationshipProviders() {
		return providers;
	}
	
	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		List <IDegreeOfSeparation> separations = new ArrayList<IDegreeOfSeparation>();
		separations.add(new DegreeOfSeparation(DOS_0_LABEL, 0));
		separations.add(new DegreeOfSeparation(DOS_1_LABEL, 1));
		separations.add(new DegreeOfSeparation(DOS_2_LABEL, 2));
		separations.add(new DegreeOfSeparation(DOS_3_LABEL, 3));
		separations.add(new DegreeOfSeparation(DOS_4_LABEL, 4));
		separations.add(new DegreeOfSeparation(DOS_5_LABEL, 5));
		return separations;
	}

	public void setParentBridge(IMylarStructureBridge bridge) {
		// TODO Auto-generated method stub
	}

	/**
	 * Some copying from:
	 * @see org.eclipse.jdt.ui.ProblemsLabelDecorator
	 */
	public boolean containsProblem(IMylarElement node) {
		try {
			IJavaElement element = (IJavaElement)getObjectForHandle(node.getHandleIdentifier());
			switch (element.getElementType()) {
			case IJavaElement.JAVA_PROJECT:
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				return getErrorTicksFromMarkers(element.getResource(), IResource.DEPTH_INFINITE, null);
			case IJavaElement.PACKAGE_FRAGMENT:
			case IJavaElement.COMPILATION_UNIT:
			case IJavaElement.CLASS_FILE:
				return getErrorTicksFromMarkers(element.getResource(), IResource.DEPTH_ONE, null);
			case IJavaElement.PACKAGE_DECLARATION:
			case IJavaElement.IMPORT_DECLARATION:
			case IJavaElement.IMPORT_CONTAINER:
			case IJavaElement.TYPE:
			case IJavaElement.INITIALIZER:
			case IJavaElement.METHOD:
			case IJavaElement.FIELD:
			case IJavaElement.LOCAL_VARIABLE:
				ICompilationUnit cu= (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
				if (cu != null) return getErrorTicksFromMarkers(element.getResource(), IResource.DEPTH_ONE, null);	
			}
		} catch (CoreException e) {
			// ignore
		}
		return false;
	}
			
	private boolean getErrorTicksFromMarkers(IResource res, int depth, ISourceReference sourceElement) throws CoreException {
		if (res == null || !res.isAccessible()) return false;
		IMarker[] markers= res.findMarkers(IMarker.PROBLEM, true, depth);
		if (markers != null) {
			for (int i= 0; i < markers.length; i++) {
				IMarker curr= markers[i];
				if (sourceElement == null) {
					int priority= curr.getAttribute(IMarker.SEVERITY, -1);
					if (priority == IMarker.SEVERITY_ERROR) {
						return true;
					} 
				}
			}			
		}
		return false;
	}
}
