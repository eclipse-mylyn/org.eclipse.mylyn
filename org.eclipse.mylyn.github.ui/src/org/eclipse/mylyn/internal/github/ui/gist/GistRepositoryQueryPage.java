/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.github.core.gist.IGistQueryConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Gist repository query page class.
 */
public class GistRepositoryQueryPage extends AbstractRepositoryQueryPage {

	private Text titleText;
	private Text userText;

	/**
	 * @param taskRepository
	 * @param query
	 */
	public GistRepositoryQueryPage(TaskRepository taskRepository,
			IRepositoryQuery query) {
		this("gistQueryPage", taskRepository, query); //$NON-NLS-1$
	}

	/**
	 * @param pageName
	 * @param taskRepository
	 * @param query
	 */
	public GistRepositoryQueryPage(String pageName,
			TaskRepository taskRepository, IRepositoryQuery query) {
		super(pageName, taskRepository, query);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite displayArea = new Composite(parent, SWT.NONE);
		initializeDialogUnits(displayArea);

		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false)
				.applyTo(displayArea);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(displayArea);

		ModifyListener completeListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setPageComplete(isPageComplete());
			}
		};

		if (!inSearchContainer()) {
			new Label(displayArea, SWT.NONE)
					.setText(Messages.GistRepositoryQueryPage_LabelTitle);
			titleText = new Text(displayArea, SWT.SINGLE | SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(titleText);
			titleText.addModifyListener(completeListener);
			titleText.setFocus();
		}

		new Label(displayArea, SWT.NONE)
				.setText(Messages.GistRepositoryQueryPage_LabelUser);
		userText = new Text(displayArea, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(userText);
		userText.addModifyListener(completeListener);

		IRepositoryQuery query = getQuery();
		if (query != null) {
			if (titleText != null && query.getSummary() != null)
				titleText.setText(query.getSummary());
			if (query.getAttribute(IGistQueryConstants.USER) != null)
				userText.setText(query.getAttribute(IGistQueryConstants.USER));
		} else if (!inSearchContainer() && !hasQueries()) {
			titleText.setText(Messages.GistRepositoryQueryPage_TitleDefault);
			AuthenticationCredentials credentials = getTaskRepository()
					.getCredentials(AuthenticationType.REPOSITORY);
			if (credentials != null)
				userText.setText(credentials.getUserName());
		}

		Dialog.applyDialogFont(displayArea);
		setControl(displayArea);
		setPageComplete(isPageComplete());
	}

	/**
	 * @return true if queries existing for this repository, false otherwise
	 */
	protected boolean hasQueries() {
		String url = getTaskRepository().getRepositoryUrl();
		return !TasksUiPlugin.getTaskList().getRepositoryQueries(url).isEmpty();
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#getQueryTitle()
	 */
	public String getQueryTitle() {
		return titleText != null ? titleText.getText() : null;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#isPageComplete()
	 */
	public boolean isPageComplete() {
		boolean complete = inSearchContainer() ? true : super.isPageComplete();
		if (complete)
			complete = userText.getText().trim().length() > 0;
		return complete;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#applyTo(org.eclipse.mylyn.tasks.core.IRepositoryQuery)
	 */
	public void applyTo(IRepositoryQuery query) {
		query.setSummary(getQueryTitle());
		query.setAttribute(IGistQueryConstants.USER, userText.getText().trim());
	}
}
