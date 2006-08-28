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

package org.eclipse.mylar.internal.tasks.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryFilter;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class NewQueryWizard extends MultiRepositoryAwareWizard {

	private static final String TITLE = "New Repository Query";

	public NewQueryWizard() {
		super(new SelectRepositoryPageForNewQuery(), TITLE);
	}
	
	public NewQueryWizard(IStructuredSelection selection) {
		super(new SelectRepositoryPageForNewQuery().setSelection(selection), TITLE);
	}
	
	private static final class SelectRepositoryPageForNewQuery extends SelectRepositoryPage {
		public SelectRepositoryPageForNewQuery() {
			super(TaskRepositoryFilter.ALL);
		}

		@Override
		protected IWizard createWizard(TaskRepository taskRepository) {
			AbstractRepositoryConnectorUi repositoryUi = TasksUiPlugin.getRepositoryUi(taskRepository.getKind());
			return repositoryUi.getNewQueryWizard(taskRepository, getSelection());
		}
	}
	
}
