/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.ui.search.BugzillaSearchPage;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

/**
 * @author Rob Elves
 */
public class EditBugzillaQueryWizard extends RepositoryQueryWizard {

	private final IRepositoryQuery query;

	private AbstractRepositoryQueryPage page;

	public EditBugzillaQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		super(repository);
		this.query = query;
	}

	@Override
	public void addPages() {
		if (isCustomQuery(query)) {
			page = new BugzillaCustomQueryWizardPage(getTaskRepository(), query);
		} else {
			page = new BugzillaSearchPage(getTaskRepository(), query);
		}
		addPage(page);
	}

	private boolean isCustomQuery(IRepositoryQuery query2) {
		String custom = query2.getAttribute(IBugzillaConstants.ATTRIBUTE_BUGZILLA_QUERY_CUSTOM);
		return custom != null && custom.equals(Boolean.TRUE.toString());
	}

	@Override
	public boolean canFinish() {
		if (page != null && page.isPageComplete()) {
			return true;
		}
		return false;
	}
}
