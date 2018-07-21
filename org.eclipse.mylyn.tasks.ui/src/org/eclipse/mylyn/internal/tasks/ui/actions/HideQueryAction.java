/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class HideQueryAction extends BaseSelectionListenerAction {

	public HideQueryAction() {
		super(Messages.HideQueryAction_Hidden_Label);
		setChecked(false);
		setEnabled(false);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection.isEmpty()) {
			return false;
		}
		boolean hidden = true;
		for (Object element : selection.toList()) {
			if (element instanceof IRepositoryQuery) {
				hidden &= Boolean.parseBoolean((((IRepositoryQuery) element).getAttribute(ITasksCoreConstants.ATTRIBUTE_HIDDEN)));
			} else {
				return false;
			}
		}
		setChecked(hidden);
		return true;
	}

	@Override
	public void run() {
		for (Object element : getStructuredSelection().toList()) {
			if (element instanceof IRepositoryQuery) {
				try {
					final IRepositoryQuery query = ((IRepositoryQuery) element);
					TasksUiPlugin.getTaskList().run(new ITaskListRunnable() {
						public void execute(IProgressMonitor monitor) throws CoreException {
							query.setAttribute(ITasksCoreConstants.ATTRIBUTE_HIDDEN, Boolean.toString(isChecked()));
						}
					});
					TasksUiPlugin.getTaskList()
							.notifyElementsChanged(Collections.singleton((IRepositoryElement) query));
				} catch (CoreException e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Failed to set hidden status for query", e)); //$NON-NLS-1$
				}
			}
		}
	}

}
