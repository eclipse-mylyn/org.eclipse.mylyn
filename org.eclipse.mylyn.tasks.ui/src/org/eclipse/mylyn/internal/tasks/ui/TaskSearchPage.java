/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.tasks.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.internal.tasks.ui.search.AbstractRepositoryQueryPage;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

/**
 * @author Rob Elves
 */
public class TaskSearchPage extends DialogPage implements ISearchPage {

	private static final String TITLE_REPOSITORY_SEARCH = "Repository Search";

	private static final String PAGE_NAME = "TaskSearchPage";

	private static final String STORE_REPO_ID = PAGE_NAME + ".REPO";

	private Combo repositoryCombo;

	private TaskRepository repository;

	private Composite fParentComposite;

	private IDialogSettings fDialogSettings;

	private int currentPageIndex = -1;

	private boolean firstView = true;

	private WizardPage[] queryPages;

	private ISearchPageContainer pageContainer;

	public boolean performAction() {
		saveDialogSettings();
		return ((ISearchPage) queryPages[currentPageIndex]).performAction();
	}

	public void setContainer(ISearchPageContainer container) {
		this.pageContainer = container;
	}

	public void createControl(Composite parent) {
		fParentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		fParentComposite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		fParentComposite.setLayoutData(gd);
		createRepositoryGroup(fParentComposite);
		this.setControl(fParentComposite);
	}

	private void createRepositoryGroup(Composite control) {
		Group group = new Group(control, SWT.NONE);
		group.setText("Repository");
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		group.setLayoutData(gd);

		repositoryCombo = new Combo(group, SWT.SINGLE | SWT.BORDER);
		repositoryCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				displayQueryPage(repositoryCombo.getSelectionIndex());
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		repositoryCombo.setLayoutData(gd);
	}

	private WizardPage createPage(TaskRepository repository) {
		if (repository != null) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(
					repository.getKind());
			if (connectorUi != null) {
				WizardPage searchPage = connectorUi.getSearchPage(repository, null);
				if (searchPage != null) {
					((ISearchPage) searchPage).setContainer(pageContainer);
					searchPage.createControl(fParentComposite);
					return searchPage;
				} else {
					// Search page not available
				}
			}
		}
		return null;
	}

	private void displayQueryPage(int pageIndex) {
		if (currentPageIndex == pageIndex || pageIndex < 0)
			return;

		// TODO: if repository == null display invalid page?
		if (currentPageIndex != -1 && queryPages[currentPageIndex] != null) {
			queryPages[currentPageIndex].setVisible(false);
			GridData data = (GridData) queryPages[currentPageIndex].getControl().getLayoutData();
			data.exclude = true;
			queryPages[currentPageIndex].getControl().setLayoutData(data);
		}

		if (queryPages[pageIndex] == null) {
			String repositoryUrl = repositoryCombo.getItem(pageIndex);
			repository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryUrl);
			if (repository != null) {
				queryPages[pageIndex] = createPage(repository);
			}
		}

		if (queryPages[pageIndex] != null) {
			GridData data = (GridData) queryPages[pageIndex].getControl().getLayoutData();
			if (data == null) {
				data = new GridData();
			}
			data.exclude = false;
			queryPages[pageIndex].getControl().setLayoutData(data);
			queryPages[pageIndex].setVisible(true);
		}

		currentPageIndex = pageIndex;
		fParentComposite.getParent().layout(true, true);
	}

	@Override
	public void setVisible(boolean visible) {
		if (firstView) {
			List<TaskRepository> repositories = TasksUiPlugin.getRepositoryManager().getAllRepositories();
			List<TaskRepository> searchableRepositories = new ArrayList<TaskRepository>();
			for (TaskRepository repository : repositories) {
				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(
						repository.getKind());
				if (connectorUi != null && connectorUi.hasSearchPage()) {
					searchableRepositories.add(repository);
				}
			}

			String[] repositoryUrls = new String[searchableRepositories.size()];
			int i = 0;
			int indexToSelect = 0;
			for (Iterator<TaskRepository> iter = searchableRepositories.iterator(); iter.hasNext();) {
				TaskRepository currRepsitory = iter.next();
				if (repository != null && repository.equals(currRepsitory)) {
					indexToSelect = i;
				}
				repositoryUrls[i] = currRepsitory.getUrl();
				i++;
			}

			IDialogSettings settings = getDialogSettings();
			if (repositoryCombo != null) {
				repositoryCombo.setItems(repositoryUrls);
				if (repositoryUrls.length == 0) {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), TITLE_REPOSITORY_SEARCH,
							TaskRepositoryManager.MESSAGE_NO_REPOSITORY);
				} else {
					String selectRepo = settings.get(STORE_REPO_ID);
					if (selectRepo != null && repositoryCombo.indexOf(selectRepo) > -1) {
						repositoryCombo.select(repositoryCombo.indexOf(selectRepo));
						repository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryCombo.getText());
						if (repository == null) {

							// TODO: Display no repository error

							// repository =
							// TasksUiPlugin.getRepositoryManager().getDefaultRepository(
							// BugzillaCorePlugin.REPOSITORY_KIND);
						}
					} else {
						repositoryCombo.select(indexToSelect);
					}

					// TODO: Create one page per connector and repopulate based
					// on
					// repository
					queryPages = new AbstractRepositoryQueryPage[repositoryUrls.length];
					displayQueryPage(repositoryCombo.getSelectionIndex());
					// updateAttributesFromRepository(repositoryCombo.getText(),
					// null, false);
				}
			}
			firstView = false;			
		}

		if(queryPages == null) {
			pageContainer.setPerformActionEnabled(false);
		}
		
		super.setVisible(visible);
	}

	public IDialogSettings getDialogSettings() {
		IDialogSettings settings = TasksUiPlugin.getDefault().getDialogSettings();
		fDialogSettings = settings.getSection(PAGE_NAME);
		if (fDialogSettings == null)
			fDialogSettings = settings.addNewSection(PAGE_NAME);
		return fDialogSettings;
	}

	private void saveDialogSettings() {
		IDialogSettings settings = getDialogSettings();
		settings.put(STORE_REPO_ID, repositoryCombo.getText());
	}

	@Override
	public void dispose() {
		if (queryPages != null) {
			for (WizardPage page : queryPages) {
				if (page != null) {
					page.dispose();
				}
			}
		}
		super.dispose();
	}
}
