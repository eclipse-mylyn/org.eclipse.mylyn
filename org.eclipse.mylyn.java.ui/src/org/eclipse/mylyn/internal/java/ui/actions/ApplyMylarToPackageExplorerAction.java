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

package org.eclipse.mylar.internal.java.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.context.ui.InterestFilter;
import org.eclipse.mylar.internal.context.ui.actions.AbstractAutoApplyMylarAction;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class ApplyMylarToPackageExplorerAction extends AbstractAutoApplyMylarAction {

	public ApplyMylarToPackageExplorerAction() {
		super(new InterestFilter());
	}

	@Override
	public void run(IAction action) {
		super.run(action);
//		if (!super.isChecked()) {
			List<StructuredViewer> viewers = getViewers();
			if (viewers.size() == 1) {
				StructuredViewer viewer = getViewers().get(0);
				// bug 15933: work-around for viewer's auto-collapse
				Object element = null;
				IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
				if (activeEditor != null) {
					Object input = getElementOfInput(activeEditor.getEditorInput());
					if (input instanceof IFile) {
						element = JavaCore.create((IFile) input);
					}

					if (element == null) { // try a non Java resource
						element = input;
					}

					if (element != null) {
						ISelection newSelection = new StructuredSelection(element);
						if (viewer.getSelection().equals(newSelection)) {
							viewer.reveal(element);
						} else {
							viewer.setSelection(newSelection, true);
						}
					}
				}
			}
//		}
	}

	/**
	 * Copied from PackageExplorerPart
	 */
	private Object getElementOfInput(IEditorInput input) {
		if (input instanceof IClassFileEditorInput)
			return ((IClassFileEditorInput) input).getClassFile();
		else if (input instanceof IFileEditorInput)
			return ((IFileEditorInput) input).getFile();
		else if (input instanceof JarEntryEditorInput)
			return ((JarEntryEditorInput) input).getStorage();
		return null;
	}

	@Override
	public List<StructuredViewer> getViewers() {
		List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
		// TODO: get from super
		IViewPart part = super.getPartForAction();
		if (part instanceof PackageExplorerPart) {
			viewers.add(((PackageExplorerPart) part).getTreeViewer());
		}
		return viewers;
	}

	public void propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent event) {
		// ignore
	}

//	@Override
//	public List<Class> getPreservedFilters() {
//		List<Class> preserved = new ArrayList<Class>();
//		preserved.add(ImportDeclarationFilter.class);
//		preserved.add(PackageDeclarationFilter.class);
//		preserved.add(JavaDeclarationsFilter.class);
//		preserved.add(ClosedProjectFilter.class);
//		return preserved;
//	}
}
