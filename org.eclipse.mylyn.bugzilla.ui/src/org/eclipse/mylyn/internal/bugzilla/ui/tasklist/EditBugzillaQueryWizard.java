/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylyn.internal.bugzilla.ui.search.BugzillaSearchPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractEditQueryWizard;

/**
 * @author Rob Elves
 */
public class EditBugzillaQueryWizard extends AbstractEditQueryWizard {

	public EditBugzillaQueryWizard(TaskRepository repository, BugzillaRepositoryQuery query) {
		super(repository, query);
	}

	@Override
	public void addPages() {
		if (((BugzillaRepositoryQuery) query).isCustomQuery()) {
			page = new BugzillaCustomQueryWizardPage(repository, (BugzillaRepositoryQuery) query);
		} else {
			page = new BugzillaSearchPage(repository, (BugzillaRepositoryQuery) query);
		}
		addPage(page);
	}

	@Override
	public boolean canFinish() {
		if (page != null && page.isPageComplete()) {
			return true;
		}
		return false;
	}
}
