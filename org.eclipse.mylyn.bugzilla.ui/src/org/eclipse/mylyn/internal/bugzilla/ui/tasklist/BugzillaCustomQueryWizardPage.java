/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Rob Elves
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class BugzillaCustomQueryWizardPage extends AbstractRepositoryQueryPage {

	private static final String LABEL_CUSTOM_TITLE = "&Query Title:";

	private static final String LABEL_CUSTOM_QUERY = "Query &URL";

	private static final String TITLE = "Create query from URL";

	private static final String DESCRIPTION = "Enter the title and URL for the query";

	private Text queryText;

	private BugzillaRepositoryQuery query;

	private Text queryTitle;

	public BugzillaCustomQueryWizardPage(TaskRepository repository, BugzillaRepositoryQuery query) {
		super(TITLE, repository);
		this.query = query;
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	public BugzillaCustomQueryWizardPage(TaskRepository repository) {
		super(TITLE, repository);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		setControl(composite);

		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setPageComplete(isPageComplete());
			}
		};

		final Label queryTitleLabel = new Label(composite, SWT.NONE);
		queryTitleLabel.setText(LABEL_CUSTOM_TITLE);

		queryTitle = new Text(composite, SWT.BORDER);
		queryTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		queryTitle.addModifyListener(modifyListener);
		queryTitle.setFocus();
		
		final Label queryUrlLabel = new Label(composite, SWT.NONE);
		queryUrlLabel.setText(LABEL_CUSTOM_QUERY);

		queryText = new Text(composite, SWT.BORDER);
		final GridData gd_queryText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_queryText.widthHint = 300;
		queryText.setLayoutData(gd_queryText);
		queryText.addModifyListener(modifyListener);

		if(query!=null) {
			queryTitle.setText(query.getSummary());
			queryText.setText(query.getUrl());
		}
	}

	@Override
	public String getQueryTitle() {
		return queryTitle.getText();
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public boolean isPageComplete() {
		if(super.isPageComplete()) {
			if(queryText.getText().length() > 0) {
				return true;
			}
			setErrorMessage("Please specify Query URL");
		}
		return false;
	}

	@Override
	public BugzillaRepositoryQuery getQuery() {
		if (query == null) {
			query = new BugzillaRepositoryQuery(getTaskRepository().getRepositoryUrl(), queryText.getText(), this.getQueryTitle());
			query.setCustomQuery(true);
		} else {
			query.setHandleIdentifier(this.getQueryTitle());
			query.setUrl(queryText.getText());
		}
		return query;
	}

}
