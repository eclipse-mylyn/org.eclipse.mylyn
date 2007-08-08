/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenRepositoryTask;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.search.AbstractRepositoryQueryPage;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskSearchPage extends DialogPage implements ISearchPage {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.search.page";

	private static final String PAGE_KEY = "page";

	private static final String TITLE_REPOSITORY_SEARCH = "Repository Search";

	private static final String PAGE_NAME = "TaskSearchPage";

	private static final String STORE_REPO_ID = PAGE_NAME + ".REPO";

	private Combo repositoryCombo;

	private TaskRepository repository;

	private Composite fParentComposite;

	private IDialogSettings fDialogSettings;

	private int currentPageIndex = -1;

	private boolean firstView = true;

	private Control[] queryPages;

	private ISearchPageContainer pageContainer;

	public boolean performAction() {
		saveDialogSettings();
		ISearchPage page = (ISearchPage) queryPages[currentPageIndex].getData(PAGE_KEY);
		return page.performAction();
	}

	public void setContainer(ISearchPageContainer container) {
		this.pageContainer = container;
	}

	public void createControl(Composite parent) {
		fParentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		// layout.marginHeight = 0;
		// layout.marginWidth = 0;
		fParentComposite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		fParentComposite.setLayoutData(gd);

		createRepositoryGroup(fParentComposite);

		createSeparator(fParentComposite);

		this.setControl(fParentComposite);
	}

	private void createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.NONE);
		separator.setVisible(false);
		GridData data = new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1);
		data.heightHint = convertHeightInCharsToPixels(1) / 3;
		separator.setLayoutData(data);
	}

	private void createRepositoryGroup(Composite control) {
		Composite group = new Composite(control, SWT.NONE);
//		 group.setText("Repository");
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		group.setLayoutData(gd);

		Label label = new Label(group, SWT.NONE);
		label.setText("Se&lect Repository: ");

		repositoryCombo = new Combo(group, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		repositoryCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				displayQueryPage(repositoryCombo.getSelectionIndex());
			}
		});
		repositoryCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		label = new Label(group, SWT.NONE);
		label.setText("  ");

		ImageHyperlink openHyperlink = new ImageHyperlink(group, SWT.NONE);
		openHyperlink.setText("Open Repository Task by Key/ID...");
		openHyperlink.setForeground(TaskListColorsAndFonts.COLOR_HYPERLINK_WIDGET);
		openHyperlink.setUnderlined(true);
		openHyperlink.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				getShell().close();
				new OpenRepositoryTask().run(null);
			}

			public void linkEntered(HyperlinkEvent e) {
				// ignore
			}

			public void linkExited(HyperlinkEvent e) {
				// ignore
			}

		});
//		openHyperlink.set
//		openHyperlink.setImage(TasksUiImages.getImage(TasksUiImages.QUERY));
	}

	private Control createPage(TaskRepository repository, ISearchPage searchPage) {
		// Page wrapper
		final Composite pageWrapper = new Composite(fParentComposite, SWT.NONE);
		pageWrapper.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		pageWrapper.setLayout(layout);

		searchPage.setContainer(pageContainer);
		try {
			searchPage.createControl(pageWrapper);
		} catch (Exception e) {
			pageWrapper.dispose();
			searchPage.dispose();

			searchPage = new DeadSearchPage(repository);
			searchPage.setContainer(pageContainer);
			searchPage.createControl(fParentComposite);
			StatusHandler.log(e, "Error occurred while constructing search page for " + repository.getUrl() + " ["
					+ repository.getConnectorKind() + "]");
			searchPage.getControl().setData(PAGE_KEY, searchPage);
			return searchPage.getControl();
		}

		pageWrapper.setData(PAGE_KEY, searchPage);
		return pageWrapper;
	}

	private void displayQueryPage(int pageIndex) {
		if (currentPageIndex == pageIndex || pageIndex < 0)
			return;

		// TODO: if repository == null display invalid page?
		if (currentPageIndex != -1 && queryPages[currentPageIndex] != null) {
			queryPages[currentPageIndex].setVisible(false);
			ISearchPage page = (ISearchPage) queryPages[currentPageIndex].getData(PAGE_KEY);
			page.setVisible(false);
			GridData data = (GridData) queryPages[currentPageIndex].getLayoutData();
			data.exclude = true;
			queryPages[currentPageIndex].setLayoutData(data);
		}

		if (queryPages[pageIndex] == null) {
			String repositoryUrl = repositoryCombo.getItem(pageIndex);
			repository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryUrl);
			if (repository != null) {
				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
				if (connectorUi != null) {
					WizardPage searchPage = connectorUi.getSearchPage(repository, null);
					if (searchPage != null && searchPage instanceof ISearchPage) {
						queryPages[pageIndex] = createPage(repository, (ISearchPage) searchPage);
					}
				}
			}
		}

		if (queryPages[pageIndex] != null) {
			GridData data = (GridData) queryPages[pageIndex].getLayoutData();
			if (data == null) {
				data = new GridData();
			}
			data.exclude = false;
			queryPages[pageIndex].setLayoutData(data);
			queryPages[pageIndex].setVisible(true);
			ISearchPage page = (ISearchPage) queryPages[pageIndex].getData(PAGE_KEY);
			page.setVisible(true);
		}

		currentPageIndex = pageIndex;
		fParentComposite.getParent().layout(true, true);
	}

	@Override
	public void setVisible(boolean visible) {
		if (firstView) {
			getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

			List<TaskRepository> repositories = TasksUiPlugin.getRepositoryManager().getAllRepositories();
			List<TaskRepository> searchableRepositories = new ArrayList<TaskRepository>();
			for (TaskRepository repository : repositories) {
				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
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
						}
					} else {
						repositoryCombo.select(indexToSelect);
					}

					// TODO: Create one page per connector and repopulate based
					// on repository
					queryPages = new Control[repositoryUrls.length];
					displayQueryPage(repositoryCombo.getSelectionIndex());
					// updateAttributesFromRepository(repositoryCombo.getText(),
					// null, false);
				}
			}
			firstView = false;
		}

		if (queryPages == null) {
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
			for (Control control : queryPages) {
				if (control != null) {
					ISearchPage page = (ISearchPage) control.getData(PAGE_KEY);
					page.dispose();
				}
			}
		}
		super.dispose();
	}

	private class DeadSearchPage extends AbstractRepositoryQueryPage {

		private TaskRepository taskRepository;

		public DeadSearchPage(TaskRepository rep) {
			super("Search page error");
			this.taskRepository = rep;
		}

		@Override
		public void createControl(Composite parent) {
			Hyperlink hyperlink = new Hyperlink(parent, SWT.NONE);
			hyperlink.setText("ERROR: Unable to present query page, ensure repository configuration is valid and retry");
			hyperlink.setUnderlined(true);
			hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					TaskSearchPage.this.getControl().getShell().close();
					TasksUiUtil.openEditRepositoryWizard(getRepository());
					// TODO: Re-construct this page with new
					// repository data
				}

			});

			GridDataFactory.fillDefaults().applyTo(hyperlink);
			setControl(hyperlink);
		}

		@Override
		public AbstractRepositoryQuery getQuery() {
			return null;
		}

		@Override
		public boolean isPageComplete() {
			return false;
		}

		public TaskRepository getRepository() {
			return taskRepository;
		}

		@Override
		public void setVisible(boolean visible) {
			super.setVisible(visible);

			if (visible) {
				scontainer.setPerformActionEnabled(false);
			}
		}

	}
}
