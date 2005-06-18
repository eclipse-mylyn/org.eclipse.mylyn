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
 * Created on Apr 13, 2005
  */
package org.eclipse.mylar.java;

import java.util.List;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaElementDelta;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscape;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.ui.InterestFilter;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class JavaModelUiUpdateBridge implements ITaskscapeListener {

    private enum ChangeKind { ADDED, REMOVED, CHANGED }
    
    public void taskscapeActivated(ITaskscape taskscape) {
        refreshPackageExplorer(null);
    }

    public void taskscapeDeactivated(ITaskscape taskscape) {
        refreshPackageExplorer(null);
    }
    
    public void revealInteresting() {
        refreshPackageExplorer(null);       
    }
    
    /**
     * TODO: should be more lazy
     */
    public void presentationSettingsChanging(UpdateKind kind) {
        refreshPackageExplorer(null);
    }

    public void presentationSettingsChanged(UpdateKind kind) {
        if (!MylarUiPlugin.getDefault().isGlobalFilteringEnabled()) {
            refreshPackageExplorer(null);
        } else if (kind == ITaskscapeListener.UpdateKind.FILTER) {
            IJavaElement selected = JavaCore.create(MylarPlugin.getTaskscapeManager().getActiveNode().getElementHandle());
            
            PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
            if (packageExplorer != null && selected!= null) packageExplorer.getTreeViewer().expandToLevel(selected, 1);
            ITaskscapeNode currentNode = MylarPlugin.getTaskscapeManager().getActiveNode();
            
            IJavaElement activeElement = JavaCore.create(currentNode.getElementHandle());
            if (activeElement != null && activeElement.exists()) refreshPackageExplorer(activeElement);
        } else { 
            refreshPackageExplorer(null);
        }
    }

    public void landmarkAdded(ITaskscapeNode node) {
        PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
        IJavaElement element = JavaCore.create(node.getElementHandle());
        if (packageExplorer != null) {
            packageExplorer.getTreeViewer().getControl().setRedraw(false);
            packageExplorer.getTreeViewer().refresh(element, true);
            packageExplorer.getTreeViewer().getControl().setRedraw(true);
        }
    } 

    public void landmarkRemoved(ITaskscapeNode node) {
        PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
        IJavaElement element = JavaCore.create(node.getElementHandle());
        if (packageExplorer != null) packageExplorer.getTreeViewer().refresh(element, true);
    }
    
    /**
     * Lazy update policy of the package explorer.
     * 
     * TODO: currently punts if there was something temporarily raised
     */
    public void interestChanged(List<ITaskscapeNode> nodes) {
        if (!MylarUiPlugin.getDefault().isGlobalFilteringEnabled()) return;
        if (MylarPlugin.getTaskscapeManager().getTempRaisedHandle() != null) {
            final IJavaElement raisedElement = JavaCore.create(MylarPlugin.getTaskscapeManager().getTempRaisedHandle());
            final PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
            if (packageExplorer != null) {
              Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
                  public void run() { 
                      packageExplorer.getTreeViewer().refresh(raisedElement.getParent());
                  }
              });
            }
        } else {
            ITaskscapeNode lastNode = nodes.get(nodes.size()-1);
            IJavaElement lastElement = JavaCore.create(lastNode.getElementHandle());            
            
            PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
        	if (packageExplorer != null && lastElement != null) { // HACK: use a more reasoanble method
                if (packageExplorer.getTreeViewer().testFindItem(lastElement) == null) {
		            for (ITaskscapeNode node : nodes) {
		                IJavaElement element = JavaCore.create(node.getElementHandle());
		                if (element != null && element.exists()) {
		                    if (node.getDegreeOfInterest().isInteresting()) {
		                        fireModelUpdate(element, ChangeKind.ADDED);
		                    } else {
		                        fireModelUpdate(element, ChangeKind.REMOVED);
		                    }
		                }
		            }
                }
        	}
//            System.err.println(lastElement);
            if (lastElement != null) {
            	revealInPackageExplorer(lastElement);
            }
        }
    }
    
    public void interestChanged(ITaskscapeNode node) {
        IJavaElement element = JavaCore.create(node.getElementHandle()); 
        if(element == null) { 
        	IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
        	Object object = bridge.getObjectForHandle(node.getElementHandle());
//        	System.err.println("> full refresh for: " + object.getClass());
        	if(object != null) refreshPackageExplorer(object);
//        		revealInPackageExplorer(object);
//        	return;
        } else {
	        if (node.getDegreeOfInterest().isInteresting()) {
	            fireModelUpdate(element, ChangeKind.ADDED);
	        } else {
	            fireModelUpdate(element, ChangeKind.REMOVED);
	        }
	        revealInPackageExplorer(element);
        }
    }
    
    public void nodeDeleted(ITaskscapeNode node) {
        IJavaElement element = JavaCore.create(node.getElementHandle());
        fireModelUpdate(element, ChangeKind.REMOVED);
    }
    
    /**
     * TODO: could use pakcage explorer's tryToReveal method to prompt for filter removal
     * 
     * @see{JavaElementContentProvider}
     */
    private void fireModelUpdate(final IJavaElement element, ChangeKind changeKind) {
        if (element == null) return;
        JavaElementDelta mylarUpdateDelta = new JavaElementDelta(element);
        switch(changeKind) {
            case ADDED: mylarUpdateDelta.added(); break;
            case REMOVED: mylarUpdateDelta.removed(); break;
//            case CHANGED: mylarUpdateDelta.changed(element, IJavaElementDelta.F_CLOSED); break;
        }
            
        IElementChangedListener[] listeners = JavaModelManager.getJavaModelManager().deltaState.elementChangedListeners;
        for (int i = 0; i < listeners.length; i++) {
            IElementChangedListener listener = listeners[i];
            
            if (listener != null) {
                if (listener instanceof StandardJavaElementContentProvider) {
                    listener.elementChanged(new ElementChangedEvent(mylarUpdateDelta, ElementChangedEvent.POST_CHANGE));
                } 
//                else {  
//                    Class enclosingClass = listener.getClass().getEnclosingClass();
//                    if (enclosingClass != null && enclosingClass.getSimpleName().equals("JavaOutlinePage")) {
//                        IJavaElement compilationUnit = element.getAncestor(IJavaElement.COMPILATION_UNIT);
//                        if (compilationUnit != null) {
//                            JavaElementDelta outlineViewDelta = new JavaElementDelta(compilationUnit);
//                            outlineViewDelta.changed(IJavaElementDelta.F_CONTENT | IJavaElementDelta.F_REORDER);
//                            listener.elementChanged(new ElementChangedEvent(outlineViewDelta, ElementChangedEvent.POST_CHANGE));
//                        }
//                    }
//                }
            }
        }
    }

    private void revealInPackageExplorer(final Object element) {
         Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
            public void run() {
            	PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
		        if (packageExplorer != null && MylarUiPlugin.getDefault().isGlobalFilteringEnabled()) {
		        	packageExplorer.selectAndReveal(element);
                }
            }
        });
    }	
    
    public void relationshipsChanged() {
    	// don't care when the relationships are changed
    }
    
    private void refreshPackageExplorer(Object element) {         
        final PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
        if (packageExplorer != null && packageExplorer.getTreeViewer() != null) {
            if (element == null) {
                packageExplorer.getTreeViewer().setInput(packageExplorer.getTreeViewer().getInput()); 
                packageExplorer.getTreeViewer().refresh();
                if (MylarUiPlugin.getDefault().isGlobalFilteringEnabled()
                    && containsMylarInterestFilter(packageExplorer.getTreeViewer())) packageExplorer.getTreeViewer().expandAll();
            } else {
                packageExplorer.getTreeViewer().refresh(element);
            }
        }
    }
    private boolean containsMylarInterestFilter(TreeViewer viewer) {
        boolean found = false;
        for (int i = 0; i < viewer.getFilters().length; i++) {
            ViewerFilter filter = viewer.getFilters()[i];
            if (filter instanceof InterestFilter) found = true;
        }
        return found;
    }

//        Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
//            public void run() { 
//                try { 
//                    if (packageExplorer != null && packageExplorer.getTreeViewer() != null) { 
//                        packageExplorer.getTreeViewer().refresh();   
//                    }
//                } catch (Throwable t) {
//                    MylarPlugin.fail(t, "Could not update viewer", false);
//                }    
////            }
////        });
//    }
    
    public void tryToReveal(List<IJavaElement> elements) {
        PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
        for (IJavaElement element : elements) {
            if (packageExplorer != null) {
                if (element != null &&
                    (MylarUiPlugin.getDefault().isGlobalFilteringEnabled() ||
                    element.getElementType() <= IJavaElement.COMPILATION_UNIT)) {
                    packageExplorer.tryToReveal(element);
                }
            }
        }
    }
}

//PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
//IJavaElement element = JavaCore.create(node.getElementHandle());
//if (element == null) { // files, etc.
//  packageExplorer.getTreeViewer().refresh(); // TODO: make more lazy
//} else {
//  IJavaElement parent = element.getParent();
//  if (packageExplorer != null) {
//      if (parent != null) packageExplorer.getTreeViewer().refresh(element.getParent(), false);
//      boolean revealed = packageExplorer.tryToReveal(element);
//      if (!revealed) {
//          packageExplorer.getTreeViewer().refresh();
//          refreshPackageExplorer();
//          revealLandmarks();
//      } else {
//          if (parent != null) refreshOutline(parent, false);
//      }
//  }
//}
