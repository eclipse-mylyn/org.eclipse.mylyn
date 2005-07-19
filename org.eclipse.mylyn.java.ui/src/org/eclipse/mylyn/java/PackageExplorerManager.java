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
package org.eclipse.mylar.java;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.ui.actions.ApplyMylarToPackageExplorerAction;

/**
 * @author Mik Kersten
 * 
 * TODO: get rid of the old delta-based code
 */
public class PackageExplorerManager implements IMylarContextListener {
	
//    private enum ChangeKind { ADDED, REMOVED, CHANGED }
//	private boolean firstExplorerRefresh = true;
    
    public void contextActivated(IMylarContext taskscape) {
    	try {
	    	if (MylarPlugin.getContextManager().hasActiveContext()
	    		&& ApplyMylarToPackageExplorerAction.getDefault() != null
	        	&& ApplyMylarToPackageExplorerAction.getDefault().isChecked()) {
				PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
				if (packageExplorer != null) { 
					packageExplorer.getTreeViewer().expandAll();
				}
	    	}	
    	} catch (Throwable t) {
    		MylarPlugin.log(t, "Could not update package explorer");
    	}
    }

    public void contextDeactivated(IMylarContext taskscape) {
//        refreshPackageExplorer(null);
    }
    
    public void revealInteresting() {
//        refreshPackageExplorer(null);       
    }
    
    /**
     * TODO: should be more lazy
     */
    public void presentationSettingsChanging(UpdateKind kind) {
//        refreshPackageExplorer(null);
    }

    public void presentationSettingsChanged(UpdateKind kind) {
//        if (kind == ITaskscapeListener.UpdateKind.FILTER) {
//            IJavaElement selected = JavaCore.create(MylarPlugin.getTaskscapeManager().getActiveNode().getElementHandle());
//            
//            PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
//            if (packageExplorer != null && selected!= null) packageExplorer.getTreeViewer().expandToLevel(selected, 1);
//            ITaskscapeNode currentNode = MylarPlugin.getTaskscapeManager().getActiveNode();
//            
//            IJavaElement activeElement = JavaCore.create(currentNode.getElementHandle());
//            if (activeElement != null && activeElement.exists()) refreshPackageExplorer(activeElement);
//        } else { 
//            refreshPackageExplorer(null);
//        }
    }

    public void landmarkAdded(IMylarContextNode node) {
//        PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
//        IJavaElement element = JavaCore.create(node.getElementHandle());
//        if (packageExplorer != null) {
//            packageExplorer.getTreeViewer().getControl().setRedraw(false);
//            packageExplorer.getTreeViewer().refresh(element, true);
//            packageExplorer.getTreeViewer().getControl().setRedraw(true);
//        }
    } 

    public void landmarkRemoved(IMylarContextNode node) {
//        PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
//        IJavaElement element = JavaCore.create(node.getElementHandle());
//        if (packageExplorer != null) packageExplorer.getTreeViewer().refresh(element, true);
    }
    
    /**
     * Lazy update policy of the package explorer.
     * 
     * TODO: currently punts if there was something temporarily raised
     */
    public void interestChanged(List<IMylarContextNode> nodes) {
    	if (nodes.size() == 0) return;
    	IMylarContextNode lastNode = nodes.get(nodes.size()-1);
    	interestChanged(lastNode);
//        if (MylarPlugin.getTaskscapeManager().getTempRaisedHandle() != null) {
//            final IJavaElement raisedElement = JavaCore.create(MylarPlugin.getTaskscapeManager().getTempRaisedHandle());
//            final PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
//            if (packageExplorer != null) {
//              Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
//                  public void run() { 
//                      packageExplorer.getTreeViewer().refresh(raisedElement.getParent());
//                  }
//              });
//            }
//        } else {
//            ITaskscapeNode lastNode = nodes.get(nodes.size()-1);
//            IJavaElement lastElement = JavaCore.create(lastNode.getElementHandle());            
            
//            PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();

//            for (ITaskscapeNode node : nodes) {
//            	IJavaElement element = JavaCore.create(node.getElementHandle());
//            	packageExplorer.getTreeViewer().refresh(element, false);
//            }
//            revealInPackageExplorer(lastElement); 
            
//            if (!suppressJavaModelAddition(lastElement, packageExplorer)) {
//	            for (ITaskscapeNode node : nodes) {
//	                IJavaElement element = JavaCore.create(node.getElementHandle());
//	                if (element != null && element.exists()) {
//	                    if (node.getDegreeOfInterest().isInteresting()) {
//	                        fireModelUpdate(element, ChangeKind.ADDED);
//	                    } 
//	                }
//	            }
//            } else if (lastElement != null) {
//            	if (!lastNode.getDegreeOfInterest().isInteresting()) {
//            		if (Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getActivePart() instanceof PackageExplorerPart) {
//            			if (FilterPackageExplorerAction.getDefault() != null && FilterPackageExplorerAction.getDefault().isChecked()) {
//            				fireModelUpdate(lastElement, ChangeKind.REMOVED);
//            			}
//            		} else {
//            			fireModelUpdate(lastElement, ChangeKind.REMOVED);
//            		}
//                }
//            }
//            if (lastElement != null && packageExplorer != null && packageExplorer.getTreeViewer().getControl().isVisible()) {
//            	revealInPackageExplorer(lastElement);
//            }
//        }
    }
    
//    private boolean suppressJavaModelAddition(IJavaElement lastElement, PackageExplorerPart explorer) {
//    	return lastElement != null && explorer != null && explorer.getTreeViewer().testFindItem(lastElement) != null; // HACK: use more sensible method
//    }
    
    public void interestChanged(IMylarContextNode node) {
	    try {
    		if (MylarPlugin.getContextManager().hasActiveContext()
	    		&& ApplyMylarToPackageExplorerAction.getDefault() != null
	    		&& ApplyMylarToPackageExplorerAction.getDefault().isChecked()) {
		    	IJavaElement lastElement = JavaCore.create(node.getElementHandle()); 
				PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
				if (packageExplorer != null && lastElement != null) { 
					packageExplorer.getTreeViewer().setSelection(new StructuredSelection(lastElement), true);
				}
	    	}
	    } catch (Throwable t) {
			MylarPlugin.log(t, "Could not update package explorer");
		}
//        IJavaElement element = JavaCore.create(node.getElementHandle()); 
//        if(element == null) { 
//        	IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(node.getStructureKind());
//        	Object object = bridge.getObjectForHandle(node.getElementHandle());
//        	if(object != null) refreshPackageExplorer(object);
//        } else {
//	        if (node.getDegreeOfInterest().isInteresting()) {
//	            fireModelUpdate(element, ChangeKind.ADDED);
//	        } else {
//	            fireModelUpdate(element, ChangeKind.REMOVED);
//	        }
//	        revealInPackageExplorer(element);
//        }
    }
    
    public void nodeDeleted(IMylarContextNode node) {
//        IJavaElement element = JavaCore.create(node.getElementHandle());
//        fireModelUpdate(element, ChangeKind.REMOVED);
    }
    
    /**
     * TODO: could use pakcage explorer's tryToReveal method to prompt for filter removal
     * 
     * @see{JavaElementContentProvider}
     */
//    private void fireModelUpdate(final IJavaElement element, ChangeKind changeKind) {
//        if (element == null) return;
//        JavaElementDelta mylarUpdateDelta = new JavaElementDelta(element);
//        switch(changeKind) {
//            case ADDED: mylarUpdateDelta.added(); break;
//            case REMOVED: mylarUpdateDelta.removed(); break;
////            case CHANGED: mylarUpdateDelta.changed(element, IJavaElementDelta.F_CLOSED); break;
//        }
//            
//        IElementChangedListener[] listeners = JavaModelManager.getJavaModelManager().deltaState.elementChangedListeners;
//        for (int i = 0; i < listeners.length; i++) {
//            IElementChangedListener listener = listeners[i];
//            
//            if (listener != null) {
//                if (listener instanceof StandardJavaElementContentProvider) {
//                    listener.elementChanged(new ElementChangedEvent(mylarUpdateDelta, ElementChangedEvent.POST_CHANGE));
//                } 
////                else {  
////                    Class enclosingClass = listener.getClass().getEnclosingClass();
////                    if (enclosingClass != null && enclosingClass.getSimpleName().equals("JavaOutlinePage")) {
////                        IJavaElement compilationUnit = element.getAncestor(IJavaElement.COMPILATION_UNIT);
////                        if (compilationUnit != null) {
////                            JavaElementDelta outlineViewDelta = new JavaElementDelta(compilationUnit);
////                            outlineViewDelta.changed(IJavaElementDelta.F_CONTENT | IJavaElementDelta.F_REORDER);
////                            listener.elementChanged(new ElementChangedEvent(outlineViewDelta, ElementChangedEvent.POST_CHANGE));
////                        }
////                    }
////                }
//            }
//        }
//    }

//    private void revealInPackageExplorer(final Object element) {
//    	if (element == null) return;
//         Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
//            public void run() {
//
//            	PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
//		        if (packageExplorer != null 
//		        		&& FilterPackageExplorerAction.getDefault() != null 
//		        		&& FilterPackageExplorerAction.getDefault().isChecked()) {
//		        	packageExplorer.selectAndReveal(element);
//                }
//            }
//        });
//    }	
    
    public void relationshipsChanged() {
    	// don't care when the relationships are changed
    }
    
//    public void refreshPackageExplorer(Object element) {         
//        final PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
//        if (packageExplorer != null && packageExplorer.getTreeViewer() != null) {
//            if (firstExplorerRefresh) {
////            	installExplorerListeners(packageExplorer);
//            	firstExplorerRefresh = false;
//            }
//        	if (element == null) {
//                packageExplorer.getTreeViewer().setInput(packageExplorer.getTreeViewer().getInput()); 
//                packageExplorer.getTreeViewer().refresh();
//                if (ApplyMylarToPackageExplorerAction.getDefault() != null && ApplyMylarToPackageExplorerAction.getDefault().isChecked()) packageExplorer.getTreeViewer().expandAll();
//            } else {
//                packageExplorer.getTreeViewer().refresh(element);
//            }
//        }
//    }
}

//public void tryToReveal(List<IJavaElement> elements) {
//PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
//for (IJavaElement element : elements) {
//  if (packageExplorer != null) {
//      if (element != null &&
//          ((FilterPackageExplorerAction.getDefault() != null 
//            && FilterPackageExplorerAction.getDefault().isChecked()) ||
//          element.getElementType() <= IJavaElement.COMPILATION_UNIT)) {
//          packageExplorer.tryToReveal(element);
//      }
//  }
//}
//}

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
