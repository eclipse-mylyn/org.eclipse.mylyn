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

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Mik Kersten
 */
public class BugzillaQueryWizardPage extends WizardPage {

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
		Control control = queryDialog.createDialogArea(parent);
		setControl(control);
	}

	public BugzillaQueryDialog getQueryDialog() {
		return queryDialog;
	}
}
