/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.internal.github.core.QueryUtils;
import org.eclipse.mylyn.internal.github.ui.GitHubRepositoryQueryPage;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * GitHub pull request repository query page class.
 */
public class PullRequestRepositoryQueryPage extends GitHubRepositoryQueryPage {

	private Button openButton;
	private Button closedButton;
	private Text titleText;

	private SelectionListener completeListener = new SelectionAdapter() {

		public void widgetSelected(SelectionEvent e) {
			setPageComplete(isPageComplete());
		}

	};

	/**
	 * @param pageName
	 * @param taskRepository
	 * @param query
	 */
	public PullRequestRepositoryQueryPage(String pageName,
			TaskRepository taskRepository, IRepositoryQuery query) {
		super(pageName, taskRepository, query);
		setDescription(Messages.PullRequestRepositoryQueryPage_Description);
		setPageComplete(false);
	}

	/**
	 * @param taskRepository
	 * @param query
	 */
	public PullRequestRepositoryQueryPage(TaskRepository taskRepository,
			IRepositoryQuery query) {
		this("prQueryPage", taskRepository, query); //$NON-NLS-1$
	}

	private void createOptionsArea(Composite parent) {
		Composite optionsArea = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(optionsArea);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(optionsArea);

		Composite statusArea = new Composite(optionsArea, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false)
				.applyTo(statusArea);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
				.applyTo(statusArea);

		new Label(statusArea, SWT.NONE)
				.setText(Messages.PullRequestRepositoryQueryPage_LabelStatus);

		openButton = new Button(statusArea, SWT.CHECK);
		openButton.setSelection(true);
		openButton.setText(Messages.PullRequestRepositoryQueryPage_StatusOpen);
		openButton.addSelectionListener(this.completeListener);

		closedButton = new Button(statusArea, SWT.CHECK);
		closedButton.setSelection(true);
		closedButton
				.setText(Messages.PullRequestRepositoryQueryPage_StatusClosed);
		closedButton.addSelectionListener(this.completeListener);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite displayArea = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true)
				.applyTo(displayArea);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(displayArea);

		if (!inSearchContainer()) {
			Composite titleArea = new Composite(displayArea, SWT.NONE);
			GridLayoutFactory.fillDefaults().numColumns(2).applyTo(titleArea);
			GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
					.applyTo(titleArea);

			new Label(titleArea, SWT.NONE)
					.setText(Messages.PullRequestRepositoryQueryPage_LabelTitle);
			titleText = new Text(titleArea, SWT.SINGLE | SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(titleText);
			titleText.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					setPageComplete(isPageComplete());
				}
			});
		}

		createOptionsArea(displayArea);

		initialize();
		setControl(displayArea);
	}

	private void initialize() {
		IRepositoryQuery query = getQuery();
		if (query == null)
			return;

		titleText.setText(query.getSummary());
		List<String> status = QueryUtils.getAttributes(
				IssueService.FILTER_STATE, query);
		closedButton.setSelection(status.contains(IssueService.STATE_CLOSED));
		openButton.setSelection(status.contains(IssueService.STATE_OPEN));
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#isPageComplete()
	 */
	public boolean isPageComplete() {
		boolean complete = super.isPageComplete();
		if (complete) {
			String message = null;
			if (!openButton.getSelection() && !closedButton.getSelection())
				message = Messages.PullRequestRepositoryQueryPage_MessageSelectStatus;

			setErrorMessage(message);
			complete = message == null;
		}
		return complete;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#getQueryTitle()
	 */
	public String getQueryTitle() {
		return this.titleText != null ? this.titleText.getText() : null;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#applyTo(org.eclipse.mylyn.tasks.core.IRepositoryQuery)
	 */
	public void applyTo(IRepositoryQuery query) {
		query.setSummary(getQueryTitle());

		List<String> statuses = new LinkedList<String>();
		if (openButton.getSelection())
			statuses.add(IssueService.STATE_OPEN);
		if (closedButton.getSelection())
			statuses.add(IssueService.STATE_CLOSED);
		QueryUtils.setAttribute(IssueService.FILTER_STATE, statuses, query);
	}
}
