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
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author Rob Elves
 */
public abstract class AbstractBugzillaQueryPage extends WizardPage {

	private static final String TITLE_QUERY_TITLE = "Query Title";

	private static final String TITLE = "Enter query parameters";

	private static final String DESCRIPTION = "If attributes are blank or stale press the Update button.";
	
	private Text title;

	private String titleString = "";

	public AbstractBugzillaQueryPage(String wizardTitle) {
		this(wizardTitle, "");
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setImageDescriptor(TaskListImages.BANNER_REPOSITORY);
		setPageComplete(false);
	}

	public AbstractBugzillaQueryPage(String wizardTitle, String queryTitle) {
		super(wizardTitle);
		titleString = queryTitle;
	}

	public void createControl(Composite parent) {
		createTitleGroup(parent);
		title.setFocus();
	}

	private void createTitleGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setText(TITLE_QUERY_TITLE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);

		title = new Text(group, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		title.setLayoutData(gd);
		title.setText(titleString);

		title.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				// ignore
			}

			public void keyReleased(KeyEvent e) {
				setPageComplete(canFlipToNextPage());
			}
		});
	}

	public boolean canFlipToNextPage() {
		if (getErrorMessage() != null || !isPageComplete())
			return false;

		return true;
	}

	@Override
	public boolean isPageComplete() {
		if (title != null && !title.getText().equals("")) {
			return true;
		}
		return false;
	}

	public String getQueryTitle() {
		return title.getText();
	}

	public abstract BugzillaRepositoryQuery getQuery();

}
