/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
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
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.ITaskRepositoryClient;
import org.eclipse.mylar.tasklist.TaskRepository;

/**
 * @author Mik Kersten
 * @author Brock Janiczak
 */
public class AddExistingTaskWizard extends MultiRepositoryAwareWizard {

	public AddExistingTaskWizard() {
		super(new SelectRepositoryPage() {

			@Override
			protected IWizard createWizard(TaskRepository taskRepository) {
				ITaskRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(
						taskRepository.getKind());
				return client.getAddExistingTaskWizard(taskRepository);
			}

		});
	}
}
