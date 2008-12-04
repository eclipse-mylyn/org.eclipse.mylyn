/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.search.SearchHitCollector;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
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

	private ITaskSearchPageContainer searchContainer;

	private final TaskRepository taskRepository;

	private final IRepositoryQuery query;

	public AbstractRepositoryQueryPage(String pageName, TaskRepository taskRepository, IRepositoryQuery query) {
		super(pageName);
		Assert.isNotNull(taskRepository);
		this.taskRepository = taskRepository;
		this.query = query;
		setTitle(Messages.AbstractRepositoryQueryPage_Enter_query_parameters);
		setDescription(Messages.AbstractRepositoryQueryPage_If_attributes_are_blank_or_stale_press_the_Update_button);
		setImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		setPageComplete(false);
	}

	public AbstractRepositoryQueryPage(String pageName, TaskRepository taskRepository) {
		this(pageName, taskRepository, null);
	}

	public IRepositoryQuery getQuery() {
		return query;
	}

	public abstract String getQueryTitle();

	@Override
	public boolean isPageComplete() {
		String queryTitle = getQueryTitle();
		if (queryTitle == null || queryTitle.equals("")) { //$NON-NLS-1$
			setErrorMessage(Messages.AbstractRepositoryQueryPage_Please_specify_a_title_for_the_query);
			return false;
		} else {
			Set<RepositoryQuery> queries = TasksUiInternal.getTaskList().getQueries();
			Set<AbstractTaskCategory> categories = TasksUiInternal.getTaskList().getCategories();
			String oldSummary = null;
			if (query != null) {
				oldSummary = query.getSummary();
			}
			if (oldSummary != null && queryTitle.equals(oldSummary)) {
				setErrorMessage(null);
				return true;
			}

			for (AbstractTaskCategory category : categories) {
				if (queryTitle.equals(category.getSummary())) {
					setErrorMessage(Messages.AbstractRepositoryQueryPage_A_category_with_this_name_already_exists);
					return false;
				}
			}
			for (RepositoryQuery repositoryQuery : queries) {
				if (query == null || !query.equals(repositoryQuery)) {
					if (queryTitle.equals(repositoryQuery.getSummary())) {
						setErrorMessage(Messages.AbstractRepositoryQueryPage_A_query_with_this_name_already_exists);
						return false;
					}
				}
			}
		}
		setErrorMessage(null);
		return true;
	}

	public IRepositoryQuery createQuery() {
		IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(getTaskRepository());
		applyTo(query);
		return query;
	}

	public abstract void applyTo(IRepositoryQuery query);

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
