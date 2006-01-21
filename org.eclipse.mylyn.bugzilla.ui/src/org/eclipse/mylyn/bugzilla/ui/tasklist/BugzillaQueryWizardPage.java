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

package org.eclipse.mylar.bugzilla.ui.tasklist;

import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractNewQueryPage;
import org.eclipse.mylar.tasklist.TaskRepository;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Mik Kersten
 */
public class BugzillaQueryWizardPage extends AbstractNewQueryPage {

	private static final String TITLE = "New Bugzilla Query";

	private static final String DESCRIPTION = "Enter the parameters for this query.";
	
	private BugzillaQueryDialog queryDialog;

	public BugzillaQueryWizardPage(TaskRepository repository) {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		queryDialog = new BugzillaQueryDialog(repository);
	}

	public void createControl(Composite parent) {
		queryDialog.createContents(parent);
		setControl(parent);
	}

	@Override
	public void addQuery() {
		queryDialog.okPressed();
	}
}
