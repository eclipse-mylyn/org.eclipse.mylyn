/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractRepositoryQueryWizard;
import org.eclipse.mylyn.internal.tasks.ui.search.SearchHitCollector;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Extend to provide repository-specific query page to the Workbench search dialog.
 * 
 * @author Rob Elves
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractRepositoryQueryPage extends WizardPage implements ITaskSearchPage {

	private static final String TITLE = "Enter query parameters";

	private static final String DESCRIPTION = "If attributes are blank or stale press the Update button.";

	private ITaskSearchPageContainer searchContainer;

	private final TaskRepository taskRepository;

	private final IRepositoryQuery query;

	public AbstractRepositoryQueryPage(String title, TaskRepository taskRepository, IRepositoryQuery query) {
		super(title);
		Assert.isNotNull(taskRepository);
		this.taskRepository = taskRepository;
		this.query = query;
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		setPageComplete(false);
	}

	public AbstractRepositoryQueryPage(String title, TaskRepository taskRepository) {
		this(title, taskRepository, null);
	}

	public IRepositoryQuery getQuery() {
		return query;
	}

	public abstract String getQueryTitle();

	@Override
	public boolean isPageComplete() {
		String queryTitle = getQueryTitle();
		if (queryTitle == null || queryTitle.equals("")) {
			setErrorMessage("Please specify a title for the query.");
			return false;
		} else {
			Set<RepositoryQuery> queries = TasksUiInternal.getTaskList().getQueries();
			Set<AbstractTaskCategory> categories = TasksUiInternal.getTaskList().getCategories();
			String oldSummary = null;
			if (query != null) {
				oldSummary = query.getSummary();
			} else if (getWizard() instanceof AbstractRepositoryQueryWizard) {
				oldSummary = ((AbstractRepositoryQueryWizard) getWizard()).getQuerySummary();
			}
			if (oldSummary != null && queryTitle.equals(oldSummary)) {
				setErrorMessage(null);
				return true;
			}

			for (AbstractTaskCategory category : categories) {
				if (queryTitle.equals(category.getSummary())) {
					setErrorMessage("A category with this name already exists, please choose another name.");
					return false;
				}
			}
			for (RepositoryQuery repositoryQuery : queries) {
				if (queryTitle.equals(repositoryQuery.getSummary()) && !query.equals(repositoryQuery)) {
					setErrorMessage("A query with this name already exists, please choose another name.");
					return false;
				}
			}
		}
		setErrorMessage(null);
		return true;
	}

	public IRepositoryQuery createQuery() {
		IRepositoryQuery query = TasksUi.getRepositoryModel().createQuery(getTaskRepository());
		applyTo(query);
		return query;
	}

	public void applyTo(IRepositoryQuery query) {
		throw new UnsupportedOperationException();
	}

	public void saveState() {
		// empty
	}

	public void setContainer(ITaskSearchPageContainer container) {
		searchContainer = container;
	}

	public boolean inSearchContainer() {
		return searchContainer != null;
	}

	public boolean performSearch() {
		NewSearchUI.activateSearchResultView();
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				taskRepository.getConnectorKind());
		if (connector != null) {
			try {
				SearchHitCollector collector = new SearchHitCollector(TasksUiInternal.getTaskList(), taskRepository,
						createQuery());
				NewSearchUI.runQueryInBackground(collector);
			} catch (UnsupportedOperationException e) {
				SearchHitCollector collector = new SearchHitCollector(TasksUiInternal.getTaskList(), taskRepository,
						getQuery());
				NewSearchUI.runQueryInBackground(collector);
			}
		}
		return true;
	}

	/**
	 * @since 2.1
	 */
	public void setControlsEnabled(boolean enabled) {
		setControlsEnabled(getControl(), enabled);
	}

	// TODO: make reusable or find better API, task editor has similar functionality
	private void setControlsEnabled(Control control, boolean enabled) {
		if (control instanceof Composite) {
			for (Control childControl : ((Composite) control).getChildren()) {
				childControl.setEnabled(enabled);
				setControlsEnabled(childControl, enabled);
			}
		}
		setPageComplete(isPageComplete());
	}

	public ITaskSearchPageContainer getSearchContainer() {
		return searchContainer;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

}
