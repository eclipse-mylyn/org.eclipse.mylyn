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

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Mik Kersten
 * @author Brock Janiczak
 */
public class AddExistingTaskWizard extends MultiRepositoryAwareWizard {

	public AddExistingTaskWizard() {
		super(new SelectRepositoryPage() {

			@Override
			protected IWizard createWizard(TaskRepository taskRepository) {
				AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(
						taskRepository.getKind());
				if (connector.canCreateTaskFromId()) {
					return connector.getAddExistingTaskWizard(taskRepository);
				} else {
					return null;
				}
			}
		});
	}
}
