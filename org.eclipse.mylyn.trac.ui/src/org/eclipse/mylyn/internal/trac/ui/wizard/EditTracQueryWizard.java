/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui.wizard;

import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractEditQueryWizard;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class EditTracQueryWizard extends AbstractEditQueryWizard {

	private TracQueryWizardPage queryPage;

	public EditTracQueryWizard(TaskRepository repository, AbstractRepositoryQuery query) {
		super(repository, query);
	}

	@Override
	public void addPages() {
		queryPage = new TracQueryWizardPage(repository, query);
		queryPage.setWizard(this);
		addPage(queryPage);
	}

	@Override
	public boolean canFinish() {
		if (queryPage.getNextPage() == null) {
			return queryPage.isPageComplete();
		}
		return queryPage.getNextPage().isPageComplete();
	}

	@Override
	public boolean performFinish() {
		AbstractRepositoryQuery q = queryPage.getQuery();
		if (q != null) {
			MylarTaskListPlugin.getTaskListManager().getTaskList().deleteQuery(query);
			MylarTaskListPlugin.getTaskListManager().getTaskList().addQuery(q);

			AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(
					repository.getKind());
			if (connector != null) {
				connector.synchronize(q, null);
			}
		}

		return true;
	}

}
