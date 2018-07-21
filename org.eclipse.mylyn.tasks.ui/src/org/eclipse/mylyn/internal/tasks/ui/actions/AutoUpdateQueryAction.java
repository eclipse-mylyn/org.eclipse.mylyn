/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class AutoUpdateQueryAction extends BaseSelectionListenerAction {

	public AutoUpdateQueryAction() {
		super(Messages.AutoUpdateQueryAction_Synchronize_Automatically_Label);
		setChecked(false);
		setEnabled(false);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection.size() == 1) {
			Object element = selection.getFirstElement();
			if (element instanceof RepositoryQuery) {
				setChecked(Boolean.valueOf(((RepositoryQuery) element).getAutoUpdate()));
				return true;
			}
		}
		setChecked(false);
		return false;
	}

	@Override
	public void run() {
		final Object element = getStructuredSelection().getFirstElement();
		if (element instanceof RepositoryQuery) {
			try {
				final RepositoryQuery query = ((RepositoryQuery) element);
				TasksUiPlugin.getTaskList().run(new ITaskListRunnable() {
					public void execute(IProgressMonitor monitor) throws CoreException {
						query.setAutoUpdate(isChecked());
					}
				});
				TasksUiPlugin.getTaskList().notifyElementsChanged(Collections.singleton(query));
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Failed to set offline status for query", e)); //$NON-NLS-1$
			}
		}
	}

}
