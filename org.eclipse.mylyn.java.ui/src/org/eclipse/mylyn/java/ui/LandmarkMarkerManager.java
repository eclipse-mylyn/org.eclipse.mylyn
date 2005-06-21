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
 * Created on Feb 16, 2005
  */
package org.eclipse.mylar.java.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.java.JavaStructureBridge;


/**
 * @author Mik Kersten
 */
public class LandmarkMarkerManager implements ITaskscapeListener {

    private Map<ITaskscapeNode, Long> markerMap = new HashMap<ITaskscapeNode, Long>();
    
    public LandmarkMarkerManager() {
        super();
    }

    public void taskscapeActivated(ITaskscape taskscape) {
        modelUpdated();
    }

    public void taskscapeDeactivated(ITaskscape taskscape) {
        modelUpdated();
    }
    
    private void modelUpdated() {
        for (ITaskscapeNode node : markerMap.keySet()) {
            landmarkRemoved(node);
        }
        markerMap.clear();
        for (ITaskscapeNode node : MylarPlugin.getTaskscapeManager().getActiveTaskscape().getLandmarks()) {
            landmarkAdded(node);
        }
    } 

    public void interestChanged(ITaskscapeNode element) {
    	// don't care when the interest changes
    }
    
    public void interestChanged(List<ITaskscapeNode> nodes) {
    	// don't care when the interest changes
    }

    public void landmarkAdded(final ITaskscapeNode node) {
        if (node == null || node.getStructureKind() == null) return;
        if (node.getStructureKind().equals(JavaStructureBridge.EXTENSION)) {
            final IJavaElement element = JavaCore.create(node.getElementHandle());
            if (!element.exists()) return;
            if (element instanceof IMember) {
                try {
                    final ISourceRange range = ((IMember)element).getNameRange();
                    final IResource resource = element.getUnderlyingResource();
                    
                    IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
                        public void run(IProgressMonitor monitor) throws CoreException {
                            IMarker marker = resource.createMarker("org.eclipse.mylar.ui.landmark");//MylarUiPlugin.MARKER_LANDMARK);
                            marker.setAttribute(IMarker.CHAR_START, range.getOffset());
                            marker.setAttribute(IMarker.CHAR_END, range.getOffset() + range.getLength());
                            marker.setAttribute(IMarker.MESSAGE, "Mylar Landmark"); 
                            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO); 
                            markerMap.put(node, marker.getId());
                        }
                    };
                    if (resource != null) resource.getWorkspace().run(runnable, null);
                } catch (JavaModelException e) {
                    MylarPlugin.fail(e, "couldn't update marker", false);
                }catch (CoreException e) {
                    MylarPlugin.fail(e, "couldn't update marker", false);
                }
            }
        }
    }
    
    public void landmarkRemoved(final ITaskscapeNode node) {
        if (node == null) return;
        if (node.getStructureKind().equals(JavaStructureBridge.EXTENSION)) {
            IJavaElement element = JavaCore.create(node.getElementHandle());
            if (!element.exists()) return;
            if (element.getAncestor(IJavaElement.COMPILATION_UNIT) != null  // stuff from .class files
               && element instanceof ISourceReference) {
                try { 
                    final IResource resource = element.getUnderlyingResource();
                    IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
                        public void run(IProgressMonitor monitor) throws CoreException {
                            if (resource != null) {
                                try {
                                	if (markerMap.containsKey(node)) {
                                		long id = markerMap.get(node);	
                                		IMarker marker = resource.getMarker(id); 
                                		if (marker != null) marker.delete();
                                	}
                                } catch (NullPointerException e) {
                                	MylarPlugin.log(e, "could not update markers");
                                }
                            }
                        }
                    };
                    resource.getWorkspace().run(runnable, null);
                } catch (JavaModelException e) {
                    MylarPlugin.fail(e, "couldn't update landmark marker", false);
                } catch (CoreException e) {
                    MylarPlugin.fail(e, "couldn't update landmark marker", false);
                }
            }
        }
    }

    public void relationshipsChanged() {
    	// don't care when the relationships changed
    }

    public void presentationSettingsChanging(UpdateKind kind) {
    	// don't care when there is a presentations setting change
    }

    public void presentationSettingsChanged(UpdateKind kind) {
    	// don't care when there is a presentation setting change
    }

    public void nodeDeleted(ITaskscapeNode node) {
    	// don't care when a node is deleted
    }

}
