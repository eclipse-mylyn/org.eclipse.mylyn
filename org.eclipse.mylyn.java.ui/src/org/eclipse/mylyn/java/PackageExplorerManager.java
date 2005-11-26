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

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.ui.MembersFilter;
import org.eclipse.mylar.java.ui.actions.ApplyMylarToPackageExplorerAction;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;

/**
 * Sets member selections on the Package Explorer when appropriate, and manages tree expansion state.
 * 
 * @author Mik Kersten
 */
public class PackageExplorerManager implements IMylarContextListener, ISelectionListener {

	public void selectionChanged(IWorkbenchPart part, ISelection changedSelection) {
		if (MylarPlugin.getContextManager().isContextCapturePaused()) return;
	    try {
    		if (!(MylarPlugin.getContextManager().hasActiveContext()
		    		&& ApplyMylarToPackageExplorerAction.getDefault() != null
		    		&& ApplyMylarToPackageExplorerAction.getDefault().isChecked())) return; 
    		Object elementToSelect = null;
    		if (changedSelection instanceof TextSelection && part instanceof JavaEditor) {
                TextSelection textSelection = (TextSelection)changedSelection;
                IJavaElement javaElement = SelectionConverter.resolveEnclosingElement((JavaEditor)part, textSelection);
                if (javaElement != null) elementToSelect = javaElement;
            } else if (changedSelection instanceof TextSelection) {
            	if (part instanceof EditorPart) {
            		elementToSelect = ((EditorPart)part).getEditorInput().getAdapter(IResource.class);
            	}
            } else {
            	return;
            }
    		
            if (elementToSelect != null) {
				PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
				if (packageExplorer != null) { 
					TreeViewer viewer = packageExplorer.getTreeViewer();
					ISelection currentSelection = viewer.getSelection();
					boolean suppressSelection = false;
					boolean membersFilteredMode = false;
					if (currentSelection instanceof StructuredSelection) {
						if (((StructuredSelection)currentSelection).size() > 1) suppressSelection = true;
					}
					if (!isInLinkToEditorMode(packageExplorer)) suppressSelection = true;
					for (ViewerFilter filter : Arrays.asList(viewer.getFilters())) {
						if (filter instanceof MembersFilter) membersFilteredMode = true;
					}
					if (!suppressSelection) {  
						if (membersFilteredMode) {
							if (elementToSelect instanceof IMember) {
								ICompilationUnit toSelect = ((IMember)elementToSelect).getCompilationUnit();
								if (toSelect != null) {
									viewer.setSelection(new StructuredSelection(toSelect), true);
								}
							}
						} else if (elementToSelect != null) {
							viewer.setSelection(new StructuredSelection(elementToSelect), true);
						}
						
						if (elementToSelect != null && MylarJavaPlugin.getDefault().getPluginPreferences().getBoolean(MylarJavaPlugin.PACKAGE_EXPLORER_AUTO_EXPAND)) {
//							Object[] expanded = viewer.getExpandedElements();
//							boolean needsExpansion = false;
//							for (int i = 0; i < expanded.length; i++) {
//								if (elementToSelect.equals(expanded[i])) needsExpansion = false;
//							}
//							if (needsExpansion) {
//								viewer.getControl().setRedraw(false);		
								viewer.expandAll();
//								viewer.getControl().setRedraw(true);
//							}
						}
					}
				}
    		}
	    } catch (Throwable t) {
			MylarPlugin.log(t, "Could not update package explorer");
		}
	}
	
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
		PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
		if (packageExplorer != null) { 
			packageExplorer.getTreeViewer().collapseAll();
		}
    }
    
   public void interestChanged(List<IMylarElement> nodes) {
//    	if (nodes.size() == 0) return;
//    	IMylarElement lastNode = nodes.get(nodes.size()-1);
//    	interestChanged(lastNode);
    }
    
    public void interestChanged(IMylarElement node) {
//	    try {
//    		if (MylarPlugin.getContextManager().hasActiveContext()
//	    		&& ApplyMylarToPackageExplorerAction.getDefault() != null
//	    		&& ApplyMylarToPackageExplorerAction.getDefault().isChecked()) {
//    			
//    			IJavaElement lastElement = JavaCore.create(node.getHandleIdentifier()); 
//				PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
//				if (packageExplorer != null && lastElement != null) { 
//					ISelection selection = packageExplorer.getTreeViewer().getSelection();
//					boolean suppressSelection = false;
//					boolean membersFilteredMode = false;
//					if (selection instanceof StructuredSelection) {
//						if (((StructuredSelection)selection).size() > 1) suppressSelection = true;
//					}
//					if (!isInLinkToEditorMode(packageExplorer)) suppressSelection = true;
//					for (ViewerFilter filter : Arrays.asList(packageExplorer.getTreeViewer().getFilters())) {
//						if (filter instanceof MembersFilter) membersFilteredMode = true;
//					}
//					if (!suppressSelection) { 
//						if (membersFilteredMode) {
//							if (lastElement instanceof IMember) {
//								ICompilationUnit toSelect = ((IMember)lastElement).getCompilationUnit();
//								if (toSelect != null) {
//									packageExplorer.getTreeViewer().setSelection(new StructuredSelection(toSelect), true);
//								}
//							}
//						} else if (lastElement != null) {
//							packageExplorer.getTreeViewer().setSelection(new StructuredSelection(lastElement), true);
//						}
//					}
//				}
//    		}
//	    } catch (Throwable t) {
//			MylarPlugin.log(t, "Could not update package explorer");
//		}
    }
    
    private boolean isInLinkToEditorMode(PackageExplorerPart packageExplorer) {
		return JavaPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.LINK_PACKAGES_TO_EDITOR);
	}

	public void revealInteresting() {
    	// ignore     
    }
    
    public void presentationSettingsChanging(UpdateKind kind) {
    	// ignore
    }

    public void presentationSettingsChanged(UpdateKind kind) {
    	// ignore
    }

    public void landmarkAdded(IMylarElement node) {
    	// ignore
    } 

    public void landmarkRemoved(IMylarElement node) {
    	// ignore
    }
    
    public void nodeDeleted(IMylarElement node) {
    	// ignore
    }
      
    public void edgesChanged(IMylarElement node) {
    	// ignore
    }

}
