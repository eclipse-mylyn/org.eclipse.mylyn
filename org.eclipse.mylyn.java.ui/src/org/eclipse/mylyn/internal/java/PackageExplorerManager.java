/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.java;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.java.ui.JavaDeclarationsFilter;
import org.eclipse.mylar.internal.ui.actions.AbstractApplyMylarAction;
import org.eclipse.mylar.provisional.core.IMylarContext;
import org.eclipse.mylar.provisional.core.IMylarContextListener;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;

/**
 * Sets member selections on the Package Explorer when appropriate, and manages
 * tree expansion state.
 * 
 * @author Mik Kersten
 */
public class PackageExplorerManager implements IMylarContextListener, ISelectionListener {

	public void selectionChanged(IWorkbenchPart part, ISelection changedSelection) {
		if (!(part instanceof PackageExplorerPart)) {
			return;
		}		
		AbstractApplyMylarAction applyAction = AbstractApplyMylarAction.getActionForPart((PackageExplorerPart)part);		
		if (!MylarPlugin.getContextManager().isContextActive()
			|| (applyAction != null && !applyAction.isChecked())) {
			return;
		}
		try {
			Object elementToSelect = null;
			if (changedSelection instanceof TextSelection && part instanceof JavaEditor) {
				TextSelection textSelection = (TextSelection) changedSelection;
				IJavaElement javaElement = SelectionConverter.resolveEnclosingElement((JavaEditor) part, textSelection);
				if (javaElement != null)
					elementToSelect = javaElement;
			} else if (changedSelection instanceof TextSelection) {
				if (part instanceof EditorPart) {
					elementToSelect = ((EditorPart) part).getEditorInput().getAdapter(IResource.class);
				}
			} else {
				return;
			}

			if (elementToSelect != null) {
				PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
				if (packageExplorer != null) {
					TreeViewer viewer = packageExplorer.getTreeViewer();
					StructuredSelection currentSelection = (StructuredSelection)viewer.getSelection();
					if (currentSelection.size() <= 1) {
						boolean membersFilteredMode = false;
						for (ViewerFilter filter : Arrays.asList(viewer.getFilters())) {
							if (filter instanceof JavaDeclarationsFilter)
								membersFilteredMode = true;
						}
						if (membersFilteredMode) {
							if (elementToSelect instanceof IMember) {
								ICompilationUnit toSelect = ((IMember) elementToSelect).getCompilationUnit();
								if (toSelect != null) {
									viewer.setSelection(new StructuredSelection(toSelect), true);
								}
							}
						} else if (elementToSelect != null) {
							if (!elementToSelect.equals(currentSelection.getFirstElement())) {
								viewer.setSelection(new StructuredSelection(elementToSelect), true);
							}
						}
					}
//						if (elementToSelect != null
//								&& MylarJavaPlugin.getDefault().getPluginPreferences().getBoolean(
//										MylarJavaPrefConstants.PACKAGE_EXPLORER_AUTO_EXPAND)) {
//							viewer.expandAll();
//						}
				}
			}
		} catch (Throwable t) {
			MylarStatusHandler.log(t, "Could not update package explorer");
		}
	}

	public void contextActivated(IMylarContext taskscape) {
//		try {
//			if (MylarPlugin.getContextManager().isContextActive()
//					&& ApplyMylarToPackageExplorerAction.getDefault() != null
//					&& ApplyMylarToPackageExplorerAction.getDefault().isChecked()) {
//
//				PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
//				if (packageExplorer != null) { 
//					packageExplorer.setLinkingEnabled(false);
//					packageExplorer.getTreeViewer().expandAll();
//				}
//			}
//		} catch (Throwable t) {
//			MylarStatusHandler.log(t, "Could not update package explorer");
//		}
	}

	public void contextDeactivated(IMylarContext taskscape) {
		PackageExplorerPart packageExplorer = PackageExplorerPart.getFromActivePerspective();
		if (packageExplorer != null) {
			packageExplorer.getTreeViewer().collapseAll();
		}
	}

//	private boolean isInLinkToEditorMode(PackageExplorerPart packageExplorer) {
//		return JavaPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.LINK_PACKAGES_TO_EDITOR);
//	}
	
	public void interestChanged(List<IMylarElement> nodes) {
		// ignore
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
