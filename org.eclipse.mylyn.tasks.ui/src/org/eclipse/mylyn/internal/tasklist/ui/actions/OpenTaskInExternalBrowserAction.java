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

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class OpenTaskInExternalBrowserAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.open.external";

	public OpenTaskInExternalBrowserAction() {
		setText("Open in External Browser");
		setToolTipText("Open in External Browser");
		setId(ID);
	}

	@Override
	public void run() {
		ISelection selection = TaskListView.getDefault().getViewer().getSelection();
		for (Object selectedObject : ((IStructuredSelection) selection).toList()) {
			ITask task = null;
			if (selectedObject instanceof ITask) {
				task = (ITask) selectedObject;
			} else if (selectedObject instanceof AbstractQueryHit) {
				AbstractQueryHit hit = (AbstractQueryHit) selectedObject;
				task = hit.getOrCreateCorrespondingTask();
			}
			if (task != null) {
				String urlString = task.getUrl();
				if (task.hasValidUrl()) {
					URL url;
					try {
						url = new URL(urlString);
						IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
						support.getExternalBrowser().openURL(url);
					} catch (Exception e) {
						MylarStatusHandler.fail(e, "could not open task url", true);
					}
				}
			}
		}
	}
}
