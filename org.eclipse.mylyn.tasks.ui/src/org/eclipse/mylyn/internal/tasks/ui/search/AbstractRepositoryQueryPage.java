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

package org.eclipse.mylar.internal.tasks.ui.search;

import java.net.Proxy;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author Rob Elves
 */
public abstract class AbstractRepositoryQueryPage extends WizardPage implements ISearchPage {

	private static final String TITLE_QUERY_TITLE = "Query Title";

	private static final String TITLE = "Enter query parameters";

	private static final String DESCRIPTION = "If attributes are blank or stale press the Update button.";

	private Text title;

	private String titleString = "";

	protected ISearchPageContainer scontainer = null;

	protected TaskRepository repository;
	
	public AbstractRepositoryQueryPage(String wizardTitle) {
		this(wizardTitle, "");
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setImageDescriptor(TaskListImages.BANNER_REPOSITORY);
		setPageComplete(false);
	}

	public AbstractRepositoryQueryPage(String wizardTitle, String queryTitle) {
		super(wizardTitle);
		titleString = queryTitle;
	}

	public void createControl(Composite parent) {
		if (scontainer == null) {
			createTitleGroup(parent);
			title.setFocus();
		}
	}

	private void createTitleGroup(Composite composite) {
		if(scontainer != null) return;
		Group group = new Group(composite, SWT.NONE);
		group.setText(TITLE_QUERY_TITLE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
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
		return title != null ? title.getText() : "";
	}

	public abstract AbstractRepositoryQuery getQuery();

	public void saveWidgetValues() {
		// empty
	}
	
	public void setContainer(ISearchPageContainer container) {
		scontainer = container;
	}
	
	public boolean inSearchContainer() {
		return scontainer != null;
	}

	public boolean performAction() {
		if (repository == null) {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), TasksUiPlugin.TITLE_DIALOG,
					TaskRepositoryManager.MESSAGE_NO_REPOSITORY);
			return false;
		}

		NewSearchUI.activateSearchResultView();
		Proxy proxySettings = TasksUiPlugin.getDefault().getProxySettings();
		SearchHitCollector collector = new SearchHitCollector(TasksUiPlugin.getTaskListManager().getTaskList(),
				repository, getQuery(), proxySettings);
		NewSearchUI.runQueryInBackground(collector);
		return true;
	}

}
