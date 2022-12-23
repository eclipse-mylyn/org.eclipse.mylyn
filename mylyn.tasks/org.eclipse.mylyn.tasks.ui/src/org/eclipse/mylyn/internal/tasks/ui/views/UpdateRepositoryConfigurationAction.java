/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.actions.AbstractTaskRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;
import org.eclipse.ui.progress.IProgressConstants2;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class UpdateRepositoryConfigurationAction extends AbstractTaskRepositoryAction {

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.reset"; //$NON-NLS-1$

	public UpdateRepositoryConfigurationAction() {
		super(Messages.UpdateRepositoryConfigurationAction_Update_Repository_Configuration);
		setId(ID);
		setEnabled(false);
	}

	@Override
	public void run() {
		IStructuredSelection selection = getStructuredSelection();
		for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
			final TaskRepository repository = getTaskRepository(iter.next());
			if (repository != null) {
				TaskJob job = TasksUiInternal.updateRepositoryConfiguration(repository);
				// show the progress in the system task bar if this is a user job (i.e. forced)
				job.setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, Boolean.TRUE);
			}
		}
	}

}
