/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class OpenWithBrowserAction extends BaseSelectionListenerAction {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.open.browser"; //$NON-NLS-1$

	public OpenWithBrowserAction() {
		super(Messages.OpenWithBrowserAction_Open_with_Browser);
		setToolTipText(Messages.OpenWithBrowserAction_Open_with_Browser);
		setId(ID);
	}

	@Override
	public void run() {
		if (super.getStructuredSelection() != null) {
			for (Iterator<?> iter = super.getStructuredSelection().iterator(); iter.hasNext();) {
				runWithSelection(iter.next());
			}
		}
	}

	private void runWithSelection(Object selectedObject) {
		if (selectedObject instanceof IRepositoryElement) {
			TasksUiUtil.openWithBrowser((IRepositoryElement) selectedObject);
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (!selection.isEmpty()) {
			for (Object element : selection.toList()) {
				if (element instanceof IRepositoryElement) {
					TaskRepository repository = TasksUiInternal.getRepository((IRepositoryElement) element);
					if (repository != null) {
						String url = TasksUiInternal.getAuthenticatedUrl(repository, (IRepositoryElement) element);
						if (TasksUiInternal.isValidUrl(url)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
