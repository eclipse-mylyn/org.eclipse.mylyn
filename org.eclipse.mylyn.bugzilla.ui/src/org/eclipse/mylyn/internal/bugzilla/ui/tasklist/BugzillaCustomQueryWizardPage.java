/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final Pattern URL_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]*/buglist.cgi?[a-zA-Z0-9%_~!$&?#'(*+;:@/=-])"); //$NON-NLS-1$

	private Text queryText;

	private final IRepositoryQuery query;

	private Text queryTitle;

	public BugzillaCustomQueryWizardPage(TaskRepository repository, IRepositoryQuery query) {
		super(Messages.BugzillaCustomQueryWizardPage_Create_query_from_URL, repository, query);
		this.query = query;
		setTitle(Messages.BugzillaCustomQueryWizardPage_Create_query_from_URL);
		setDescription(Messages.BugzillaCustomQueryWizardPage_Enter_the_title_and_URL_for_the_query);
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
		queryTitleLabel.setText(Messages.BugzillaCustomQueryWizardPage_Query_Title);

		queryTitle = new Text(composite, SWT.BORDER);
		queryTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		queryTitle.addModifyListener(modifyListener);
		queryTitle.setFocus();

		final Label queryUrlLabel = new Label(composite, SWT.NONE);
		queryUrlLabel.setText(Messages.BugzillaCustomQueryWizardPage_Query_URL);

		queryText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		final GridData gd_queryText = new GridData(GridData.FILL_BOTH);
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
				Matcher m = URL_PATTERN.matcher(queryText.getText());
				if (m.find()) {
					return true;
				} else {
					setErrorMessage(Messages.BugzillaCustomQueryWizardPage_No_Valid_Buglist_URL);
					return false;
				}
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
