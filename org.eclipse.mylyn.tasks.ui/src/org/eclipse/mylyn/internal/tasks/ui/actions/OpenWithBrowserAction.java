/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.open.browser"; //$NON-NLS-1$

	public OpenWithBrowserAction() {
		super(Messages.OpenWithBrowserAction_Open_with_Browser);
		setToolTipText(Messages.OpenWithBrowserAction_Open_with_Browser);
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
