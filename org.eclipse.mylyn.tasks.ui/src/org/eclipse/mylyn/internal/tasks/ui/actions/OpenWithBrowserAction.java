/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Iterator;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class OpenWithBrowserAction extends BaseSelectionListenerAction {

	private static final String LABEL = "Open with Browser";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.open.browser";

	public OpenWithBrowserAction() {
		super(LABEL);
		setToolTipText(LABEL);
		setId(ID);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		if (super.getStructuredSelection() != null) {
			for (Iterator iter = super.getStructuredSelection().iterator(); iter.hasNext();) {
				runWithSelection(iter.next());
			}
		}
	}

	private void runWithSelection(Object selectedObject) {
		String urlString = null;
		if (selectedObject instanceof ITask) {
			AbstractTask task = (AbstractTask) selectedObject;
			if (TasksUiInternal.isValidUrl(task.getUrl())) {
				urlString = task.getUrl();
			}
		} else if (selectedObject instanceof IRepositoryElement) {
			IRepositoryElement query = (IRepositoryElement) selectedObject;
			urlString = query.getUrl();
		}

		if (urlString != null) {
			TasksUiUtil.openUrl(urlString);
		}
	}
}
