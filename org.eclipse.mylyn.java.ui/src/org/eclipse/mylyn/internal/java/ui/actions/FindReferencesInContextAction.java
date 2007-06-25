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

package org.eclipse.mylyn.internal.java.ui.actions;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.search.JavaSearchQuery;
import org.eclipse.jdt.internal.ui.search.JavaSearchScopeFactory;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.context.ui.TaskContextWorkingSetManager;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Shawn Minto
 */
public class FindReferencesInContextAction extends Action implements IWorkbenchWindowActionDelegate {

	public void run(IAction action) {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor != null && editor instanceof JavaEditor) {
			IJavaElement[] resolved;
			try {
				resolved = SelectionConverter.codeResolve((JavaEditor) editor);
				if (resolved != null && resolved.length == 1 && resolved[0] != null) {
					IJavaElement element = resolved[0];

					TaskContextWorkingSetManager updater = TaskContextWorkingSetManager.getDefault()
							.getWorkingSetUpdater();
					if (updater != null && updater.getWorkingSet() != null) {
						IJavaSearchScope scope = JavaSearchScopeFactory.getInstance().createJavaSearchScope(
								updater.getWorkingSet(), false);
						JavaSearchQuery query = new JavaSearchQuery(new ElementQuerySpecification(element,
								IJavaSearchConstants.REFERENCES, scope, "Mylar Current Task Context"));
						NewSearchUI.activateSearchResultView();

						if (query.canRunInBackground()) {
							NewSearchUI.runQueryInBackground(query);
						} else {
							IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
							NewSearchUI.runQueryInForeground(progressService, query);
						}
					}
				}
			} catch (JavaModelException e) {
				// ignore search if can't resolve
			}

		}

	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
