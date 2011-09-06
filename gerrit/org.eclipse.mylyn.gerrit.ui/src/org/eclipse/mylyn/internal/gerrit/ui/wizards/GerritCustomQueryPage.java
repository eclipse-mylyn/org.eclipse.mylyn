/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Sascha Scholz (SAP) - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui.wizards;

import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mikael Kober
 * @author Thomas Westling
 */
public class GerritCustomQueryPage extends AbstractRepositoryQueryPage {

	private final IRepositoryQuery query;

	private Button myChangesButton;

	private Button watchedChangesButton;

	private Button allOpenChangesButton;

	private Button byProjectButton;

	private Button customQueryButton;

	private Text titleText;

	private Text projectText;

	private Text queryText;

	public GerritCustomQueryPage(TaskRepository repository, String pageName, IRepositoryQuery query) {
		super(pageName, repository, query);
		this.query = query;
		setDescription("Enter title and type of the query.");
	}

	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		control.setLayoutData(gd);
		GridLayout layout = new GridLayout(3, false);
		control.setLayout(layout);

		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateButtons();
			}
		};

		if (getSearchContainer() == null) {
			Label titleLabel = new Label(control, SWT.NONE);
			titleLabel.setText("Query Title:");

			titleText = new Text(control, SWT.BORDER);
			GridData gd2 = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
			gd2.horizontalSpan = 2;
			titleText.setLayoutData(gd2);
			titleText.addModifyListener(modifyListener);
		}

		Label typeLabel = new Label(control, SWT.NONE);
		typeLabel.setText("Query type:");

		// radio button to select query type
		myChangesButton = new Button(control, SWT.RADIO);
		myChangesButton.setText("My changes");
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd2.horizontalSpan = 2;
		myChangesButton.setLayoutData(gd2);

		new Label(control, SWT.NONE);
		watchedChangesButton = new Button(control, SWT.RADIO);
		watchedChangesButton.setText("My watched changes");
		watchedChangesButton.setLayoutData(gd2);

		new Label(control, SWT.NONE);
		allOpenChangesButton = new Button(control, SWT.RADIO);
		allOpenChangesButton.setText("All open changes");
		allOpenChangesButton.setLayoutData(gd2);

		new Label(control, SWT.NONE);
		byProjectButton = new Button(control, SWT.RADIO);
		byProjectButton.setText("Open changes by project");

		projectText = new Text(control, SWT.BORDER);
		projectText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		projectText.addModifyListener(modifyListener);

		new Label(control, SWT.NONE);
		customQueryButton = new Button(control, SWT.RADIO);
		customQueryButton.setText("Custom query");

		queryText = new Text(control, SWT.BORDER);
		queryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		queryText.addModifyListener(modifyListener);

		if (query != null) {
			if (titleText != null) {
				titleText.setText(query.getSummary());
			}
			if (GerritQuery.MY_CHANGES.equals(query.getAttribute(GerritQuery.TYPE))) {
				myChangesButton.setSelection(true);
			} else if (GerritQuery.MY_WATCHED_CHANGES.equals(query.getAttribute(GerritQuery.TYPE))) {
				watchedChangesButton.setSelection(true);
			} else if (GerritQuery.OPEN_CHANGES_BY_PROJECT.equals(query.getAttribute(GerritQuery.TYPE))) {
				byProjectButton.setSelection(true);
			} else if (GerritQuery.CUSTOM.equals(query.getAttribute(GerritQuery.TYPE))) {
				customQueryButton.setSelection(true);
			} else {
				allOpenChangesButton.setSelection(true);
			}
			if (query.getAttribute(GerritQuery.PROJECT) != null) {
				projectText.setText(query.getAttribute(GerritQuery.PROJECT));
			}
			if (query.getAttribute(GerritQuery.QUERY_STRING) != null) {
				queryText.setText(query.getAttribute(GerritQuery.QUERY_STRING));
			}
		} else {
			myChangesButton.setSelection(true);
		}

		SelectionListener buttonSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectText.setEnabled(byProjectButton.getSelection());
				queryText.setEnabled(customQueryButton.getSelection());
				updateButtons();
			}
		};
		buttonSelectionListener.widgetSelected(null);
		byProjectButton.addSelectionListener(buttonSelectionListener);
		customQueryButton.addSelectionListener(buttonSelectionListener);

		setControl(control);
	}

	protected void updateButtons() {
		IWizardContainer c = getContainer();
		if (c != null && c.getCurrentPage() != null) {
			c.updateButtons();
		}
	}

	@Override
	public boolean isPageComplete() {
		boolean ret = (titleText != null && titleText.getText().length() > 0);
		if (byProjectButton != null && byProjectButton.getSelection()) {
			ret &= (projectText != null && projectText.getText().length() > 0);
		}
		if (customQueryButton != null && customQueryButton.getSelection()) {
			ret &= (queryText != null && queryText.getText().length() > 0);
		}
		return ret;
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		// TODO: set URL ????
		// query.setUrl(getQueryUrl());
		query.setSummary(getTitleText());
		if (myChangesButton.getSelection()) {
			query.setAttribute(GerritQuery.TYPE, GerritQuery.MY_CHANGES);
		} else if (watchedChangesButton.getSelection()) {
			query.setAttribute(GerritQuery.TYPE, GerritQuery.MY_WATCHED_CHANGES);
		} else if (byProjectButton.getSelection()) {
			query.setAttribute(GerritQuery.TYPE, GerritQuery.OPEN_CHANGES_BY_PROJECT);
		} else if (customQueryButton.getSelection()) {
			query.setAttribute(GerritQuery.TYPE, GerritQuery.CUSTOM);
		} else {
			query.setAttribute(GerritQuery.TYPE, GerritQuery.ALL_OPEN_CHANGES);
		}
		query.setAttribute(GerritQuery.PROJECT, projectText.getText());
		query.setAttribute(GerritQuery.QUERY_STRING, queryText.getText());
	}

	private String getTitleText() {
		return (titleText != null) ? titleText.getText() : "<search>";
	}

	@Override
	public String getQueryTitle() {
		return "Gerrit Query";
	}

}
