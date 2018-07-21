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
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import java.beans.Beans;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.search.SearchUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Extend to provide repository-specific query page to the Workbench search dialog.
 * <p>
 * It is recommended that clients extend {@link AbstractRepositoryQueryPage2} instead.
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
		// see http://ekkescorner.wordpress.com/2009/07/07/galileo-enter-the-twilight-zone-between-target-platform-runtime-and-ide-development/
		if (!Beans.isDesignTime()) {
			Assert.isNotNull(taskRepository);
		}
		this.taskRepository = taskRepository;
		this.query = query;
		setTitle(Messages.AbstractRepositoryQueryPage_Enter_query_parameters);
		setDescription(Messages.AbstractRepositoryQueryPage_If_attributes_are_blank_or_stale_press_the_Update_button);
		// see http://ekkescorner.wordpress.com/2009/07/07/galileo-enter-the-twilight-zone-between-target-platform-runtime-and-ide-development/
		if (!Beans.isDesignTime()) {
			setImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
		}
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
		// reset error message to maintain backward compatibility: bug 288892
		setErrorMessage(null);

		String queryTitle = getQueryTitle();
		if (queryTitle == null || queryTitle.equals("")) { //$NON-NLS-1$
			setMessage(Messages.AbstractRepositoryQueryPage_Please_specify_a_title_for_the_query);
			return false;
		} else {
			Set<RepositoryQuery> queries = TasksUiInternal.getTaskList().getQueries();
			Set<AbstractTaskCategory> categories = TasksUiInternal.getTaskList().getCategories();
			String oldSummary = null;
			if (query != null) {
				oldSummary = query.getSummary();
			}
			if (oldSummary == null || !queryTitle.equals(oldSummary)) {
				for (AbstractTaskCategory category : categories) {
					if (queryTitle.equals(category.getSummary())) {
						setMessage(Messages.AbstractRepositoryQueryPage_A_category_with_this_name_already_exists,
								IMessageProvider.ERROR);
						return false;
					}
				}
				for (RepositoryQuery repositoryQuery : queries) {
					if (query == null || !query.equals(repositoryQuery)) {
						if (queryTitle.equals(repositoryQuery.getSummary())
								&& repositoryQuery.getRepositoryUrl().equals(getTaskRepository().getRepositoryUrl())) {
							setMessage(Messages.AbstractRepositoryQueryPage_A_query_with_this_name_already_exists,
									IMessageProvider.WARNING);
							return true;
						}
					}
				}
			}
		}
		setMessage(null);
		return true;
	}

	public IRepositoryQuery createQuery() {
		IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(getTaskRepository());
		applyTo(query);
		return query;
	}

	public abstract void applyTo(@NonNull IRepositoryQuery query);

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
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				taskRepository.getConnectorKind());
		if (connector != null) {
			try {
				SearchUtil.runSearchQuery(TasksUiInternal.getTaskList(), taskRepository, createQuery(), true);
			} catch (UnsupportedOperationException e) {
				SearchUtil.runSearchQuery(TasksUiInternal.getTaskList(), taskRepository, getQuery(), true);
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
