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

import java.util.Iterator;

import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class OpenWithBrowserAction extends BaseSelectionListenerAction {

	private static final String LABEL = "Open with Browser";

	public static final String ID = "org.eclipse.mylar.tasklist.actions.open.browser";

	public OpenWithBrowserAction() {
		super(LABEL);
		setToolTipText(LABEL);
		setId(ID);
	}

	@Override
	public void run() {
		if (super.getStructuredSelection() != null) {
			for (Iterator iter = super.getStructuredSelection().iterator(); iter.hasNext();) {
				runWithSelection(iter.next());
			}
		}
	}

	private void runWithSelection(Object selectedObject) {
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
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) selectedObject;
			urlString = query.getQueryUrl();
		}
		if (urlString != null) {
			TaskUiUtil.openUrl(urlString);
		}
	}
}
