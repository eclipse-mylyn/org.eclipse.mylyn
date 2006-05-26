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

package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.ITask;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class OpenWithBrowserAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.open.browser";

	public OpenWithBrowserAction() {
		setText("Open with Browser");
		setToolTipText("Open with Browser");
		setId(ID);
	}

	@Override
	public void run() {
		ISelection selection = TaskListView.getFromActivePerspective().getViewer().getSelection();
		for (Object selectedObject : ((IStructuredSelection) selection).toList()) {
			ITask task = null;
			if (selectedObject instanceof ITask) {
				task = (ITask) selectedObject;
			} else if (selectedObject instanceof AbstractQueryHit) {
				AbstractQueryHit hit = (AbstractQueryHit) selectedObject;
				task = hit.getOrCreateCorrespondingTask();
			}
			String urlString = null;
			if (task != null && task.hasValidUrl()) {
				urlString = task.getUrl();
			} else if (selectedObject instanceof AbstractRepositoryQuery) {
				AbstractRepositoryQuery query = (AbstractRepositoryQuery)selectedObject;
				urlString = query.getQueryUrl();
			}
			if (urlString != null) {
				TaskUiUtil.openUrl(urlString);
			}
		}
	}
}
