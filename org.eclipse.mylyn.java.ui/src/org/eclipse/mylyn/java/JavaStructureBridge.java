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
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.mylar.core.AbstractRelationshipProvider;
import org.eclipse.mylar.core.IDegreeOfSeparation;
import org.eclipse.mylar.core.IMylarContextNode;
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

    public final static String EXTENSION = "java";
    
    public List<AbstractRelationshipProvider> providers;
    
    public JavaStructureBridge(){
    	providers = new ArrayList<AbstractRelationshipProvider>();
    	providers.add(new JavaReferencesProvider());
    	providers.add(new JavaImplementorsProvider());
    	providers.add(new JavaReadAccessProvider());
    	providers.add(new JavaWriteAccessProvider()); 
    	providers.add(new JUnitReferencesProvider());
    }
    
    public String getResourceExtension() {
        return EXTENSION;
    }
    
    public String getParentHandle(String handle) {
        IJavaElement javaElement = JavaCore.create(handle);
        if (javaElement != null && javaElement.getParent() != null) {            
            return getHandleIdentifier(javaElement.getParent());
        } else {
            return null;
        } 
    }

    public Object getObjectForHandle(String handle) {
        return JavaCore.create(handle);
    }
    
    /**
     * Uses resource-compatible path for projects
     */
    public String getHandleIdentifier(Object object) {
        if (object == null || !(object instanceof IJavaElement)) return null;
        return ((IJavaElement)object).getHandleIdentifier();
    }

    public String getName(Object object) {
        if (object instanceof IJavaElement) {
            return ((IJavaElement)object).getElementName();
        } else {
            return "";
        }
    }
    
    public boolean canBeLandmark(Object element) {
        if (element instanceof IMember || element instanceof IType) {
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
            || object instanceof IJavaProject; // TODO: redundant?
        return accepts;
    }

    /**
     * Uses special rules for classpath containers since these do not have an associated interest,
     * i.e. they're not IJavaElement(s).
     */
    public boolean canFilter(Object object) {
        if (object instanceof ClassPathContainer.RequiredProjectWrapper) {
            return true;
        }
        if (object instanceof ClassPathContainer) { // HACK: check if it has anything interesting
            ClassPathContainer container = (ClassPathContainer)object;
            
            Object[] children = container.getChildren(container);
            for (int i = 0; i < children.length; i++) {
                if (children[i] instanceof JarPackageFragmentRoot) {
                    JarPackageFragmentRoot element = (JarPackageFragmentRoot)children[i];
                    IMylarContextNode node = MylarPlugin.getContextManager().getNode(element.getHandleIdentifier());
                    if (node != null && node.getDegreeOfInterest().isInteresting()) {
                        return false;
                    } 
                } 
            } 
            return true; 
        } else {
            return true;
        }
    }

    public boolean isDocument(String handle) {
        IJavaElement element = (IJavaElement)getObjectForHandle(handle);
        return element instanceof ICompilationUnit || element instanceof IClassFile;
    }

    public String getHandleForMarker(ProblemMarker marker) {
        if (marker == null) return null;
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

    public String getResourceExtension(String elementHandle) {
        return getResourceExtension();
    }

	public List<AbstractRelationshipProvider> getProviders() {
		return providers;
	}
	
	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		List <IDegreeOfSeparation> separations = new ArrayList<IDegreeOfSeparation>();
		separations.add(new DegreeOfSeparation("disabled", 0));
		separations.add(new DegreeOfSeparation("landmark files", 1));
		separations.add(new DegreeOfSeparation("interesting files", 2));
		separations.add(new DegreeOfSeparation("interesting project", 3));
		separations.add(new DegreeOfSeparation("project dependancies", 4));
		separations.add(new DegreeOfSeparation("entire workspace", 5));

		return separations;
	}
}
