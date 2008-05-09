/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.wizard;

import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractRepositoryQueryWizard;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class EditTracQueryWizard extends AbstractRepositoryQueryWizard {

//	private TracCustomQueryPage queryPage;

	public EditTracQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		super(repository, query);
	}

	@Override
	public void addPages() {
		page = new TracCustomQueryPage(repository, query);
		page.setWizard(this);
		addPage(page);
	}

	@Override
	public boolean canFinish() {
		if (page.getNextPage() == null) {
			return page.isPageComplete();
		}
		return page.getNextPage().isPageComplete();
	}

//	@Override
//	public boolean performFinish() {
//		AbstractRepositoryQuery q = queryPage.getQuery();
//		if (q != null) {
//			TasksUiPlugin.getTaskList().deleteQuery(query);
//			TasksUiPlugin.getTaskList().addQuery(q);
//
//			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
//					repository.getKind());
//			if (connector != null) {
//				TasksUiPlugin.getSynchronizationManager().synchronize(connector, q, null);
//			}
//		}
//
//		return true;
//	}

}