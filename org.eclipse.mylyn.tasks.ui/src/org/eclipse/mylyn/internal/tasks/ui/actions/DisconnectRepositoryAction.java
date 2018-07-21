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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta.Type;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.Messages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Steffen Pingel
 */
public class DisconnectRepositoryAction extends Action implements ISelectionChangedListener {

	public static final String LABEL = Messages.DisconnectRepositoryAction_Disconnected;

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.offline"; //$NON-NLS-1$

	private TaskRepository repository;

	public DisconnectRepositoryAction() {
		super(LABEL, IAction.AS_CHECK_BOX);
		setId(ID);
		setEnabled(false);
	}

	@Override
	public void run() {
		repository.setOffline(isChecked());
		TasksUiPlugin.getRepositoryManager().notifyRepositorySettingsChanged(repository,
				new TaskRepositoryDelta(Type.OFFLINE));
	}

	@Deprecated
	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection && ((IStructuredSelection) selection).size() == 1) {
			Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof TaskRepository) {
				AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
						((TaskRepository) selectedObject).getConnectorKind());
				if (connector.isUserManaged()) {
					this.repository = (TaskRepository) selectedObject;
					setChecked(this.repository.isOffline());
					setEnabled(true);
					return;
				}
			}
		}
		this.repository = null;
		setChecked(false);
		setEnabled(false);
	}

}
