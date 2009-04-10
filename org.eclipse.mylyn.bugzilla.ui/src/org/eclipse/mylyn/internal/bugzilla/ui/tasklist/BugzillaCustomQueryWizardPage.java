/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
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

	private static final String LABEL_CUSTOM_TITLE = Messages.BugzillaCustomQueryWizardPage_Query_Title;

	private static final String LABEL_CUSTOM_QUERY = Messages.BugzillaCustomQueryWizardPage_Query_URL;

	private static final String TITLE = Messages.BugzillaCustomQueryWizardPage_Create_query_from_URL;

	private static final String DESCRIPTION = Messages.BugzillaCustomQueryWizardPage_Enter_the_title_and_URL_for_the_query;

	private Text queryText;

	private final IRepositoryQuery query;

	private Text queryTitle;

	public BugzillaCustomQueryWizardPage(TaskRepository repository, IRepositoryQuery query) {
		super(TITLE, repository, query);
		this.query = query;
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	public BugzillaCustomQueryWizardPage(TaskRepository repository) {
		this(repository, null);
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

		if (query != null) {
			queryTitle.setText(query.getSummary());
			queryText.setText(query.getUrl());
		}
		Dialog.applyDialogFont(composite);
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
		if (super.isPageComplete()) {
			if (queryText.getText().length() > 0) {
				return true;
			}
			setErrorMessage(Messages.BugzillaCustomQueryWizardPage_Please_specify_Query_URL);
		}
		return false;
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		query.setSummary(this.getQueryTitle());
		query.setUrl(queryText.getText());
		query.setAttribute(IBugzillaConstants.ATTRIBUTE_BUGZILLA_QUERY_CUSTOM, Boolean.TRUE.toString());
	}

}
