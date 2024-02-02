/*********************************************************************
 * Copyright (c) 2010, 2015 Sony Ericsson/ST Ericsson and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Sascha Scholz (SAP) - improvements
 *      Tasktop Technologies - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui.wizards;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.mylyn.commons.workbench.forms.SectionComposite;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage2;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

/**
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Sascha Scholz
 */
public class GerritCustomQueryPage extends AbstractRepositoryQueryPage2 {

	private Button myChangesButton;

	private Button watchedChangesButton;

	private Button allOpenChangesButton;

	private Button byProjectButton;

	private Button customQueryButton;

	private Text projectText;

	private Text queryText;

	public GerritCustomQueryPage(TaskRepository repository, String pageName, IRepositoryQuery query) {
		super(pageName, repository, query);
		setDescription(Messages.GerritCustomQueryPage_Enter_title_and_select_query_type);
		setNeedsClear(true);
		setNeedsRefresh(true);
	}

	@Override
	protected void createPageContent(SectionComposite parent) {
		Composite composite = parent.getContent();
		composite.setLayout(new GridLayout(2, false));

		ModifyListener modifyListener = e -> updateButtons();

		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.GerritCustomQueryPage_Query_type);
		GridLayoutFactory.swtDefaults().numColumns(2).spacing(7, 5).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(group);

		// radio button to select query type
		myChangesButton = new Button(group, SWT.RADIO);
		myChangesButton.setText(Messages.GerritCustomQueryPage_My_changes);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(myChangesButton);

		watchedChangesButton = new Button(group, SWT.RADIO);
		watchedChangesButton.setText(Messages.GerritCustomQueryPage_My_watched_changes);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(watchedChangesButton);

		allOpenChangesButton = new Button(group, SWT.RADIO);
		allOpenChangesButton.setText(Messages.GerritCustomQueryPage_All_open_changes);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(allOpenChangesButton);

		byProjectButton = new Button(group, SWT.RADIO);
		byProjectButton.setText(Messages.GerritCustomQueryPage_Open_changes_by_project);

		projectText = new Text(group, SWT.BORDER);
		projectText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		projectText.addModifyListener(modifyListener);
		addProjectNameContentProposal(projectText);

		new Label(composite, SWT.NONE);
		customQueryButton = new Button(group, SWT.RADIO);
		customQueryButton.setText(Messages.GerritCustomQueryPage_Custom_query);

		queryText = new Text(group, SWT.BORDER);
		queryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		queryText.addModifyListener(modifyListener);

		SelectionListener buttonSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		};
		myChangesButton.addSelectionListener(buttonSelectionListener);
		watchedChangesButton.addSelectionListener(buttonSelectionListener);
		allOpenChangesButton.addSelectionListener(buttonSelectionListener);
		byProjectButton.addSelectionListener(buttonSelectionListener);
		customQueryButton.addSelectionListener(buttonSelectionListener);
	}

	private void addProjectNameContentProposal(Text text) {
		IContentProposalProvider proposalProvider = new ProjectNameContentProposalProvider(
				GerritCorePlugin.getDefault().getConnector(), getTaskRepository());
		ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(text, new TextContentAdapter(),
				proposalProvider, null, new char[0], true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
	}

	private GerritClient getGerritClient() {
		GerritConnector connector = GerritCorePlugin.getDefault().getConnector();
		return connector.getClient(getTaskRepository());
	}

	protected void updateButtons() {
		projectText.setEnabled(byProjectButton.getSelection());
		queryText.setEnabled(customQueryButton.getSelection());

		IWizardContainer c = getContainer();
		if (c != null && c.getCurrentPage() != null) {
			c.updateButtons();
		}
	}

	@Override
	public boolean isPageComplete() {
		boolean ret = getQueryTitle() != null && getQueryTitle().trim().length() > 0;
		if (byProjectButton != null && byProjectButton.getSelection()) {
			ret &= projectText != null && projectText.getText().length() > 0;
		}
		if (customQueryButton != null && customQueryButton.getSelection()) {
			ret &= queryText != null && queryText.getText().length() > 0;
		}
		return ret;
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		query.setSummary(getQueryTitle());
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

	@Override
	protected void doRefreshControls() {
		// nothing to do, only the content assist uses the configuration
	}

	@Override
	protected boolean hasRepositoryConfiguration() {
		return getGerritClient().getConfiguration() != null;
	}

	@Override
	protected void doClearControls() {
		restoreState(null);
	}

	@Override
	protected boolean restoreState(IRepositoryQuery query) {
		if (query != null) {
			setQueryTitle(query.getSummary());
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
			watchedChangesButton.setSelection(false);
			byProjectButton.setSelection(false);
			customQueryButton.setSelection(false);
			allOpenChangesButton.setSelection(false);
			projectText.setText(""); //$NON-NLS-1$
			queryText.setText(""); //$NON-NLS-1$
		}
		updateButtons();
		return true;
	}

	@Override
	protected String suggestQueryTitle() {
		if (myChangesButton.isDisposed()) {
			return ""; //$NON-NLS-1$
		} else if (myChangesButton.getSelection()) {
			return Messages.GerritCustomQueryPage_My_changes;
		} else if (watchedChangesButton.getSelection()) {
			return Messages.GerritCustomQueryPage_My_watched_changes;
		} else if (allOpenChangesButton.getSelection()) {
			return Messages.GerritCustomQueryPage_All_open_changes;
		} else if (byProjectButton.getSelection()) {
			return NLS.bind(Messages.GerritCustomQueryPage_Open_Changes_in_X, projectText.getText());
		}
		return ""; //$NON-NLS-1$
	}

}
