/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
 *     David Green - fix for bug 244442
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylyn.internal.tasks.ui.Messages;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskSearchPage;
import org.eclipse.mylyn.tasks.ui.wizards.TaskAttachmentPage;

/**
 * Extend to provide connector-specific UI extensions.
 * 
 * TODO: consider refactoring into extension points
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractRepositoryConnectorUi {

	private static final String LABEL_TASK_DEFAULT = Messages.AbstractRepositoryConnectorUi_Task;

	private final boolean customNotificationHandling = false;

	/**
	 * @return the unique type of the repository, e.g. "bugzilla"
	 */
	public abstract String getConnectorKind();

	/**
	 * @since 3.0
	 */
	public abstract ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository);

	/**
	 * @param repository
	 * @param queryToEdit
	 *            can be null
	 * @since 3.0
	 */
	public abstract IWizard getQueryWizard(TaskRepository taskRepository, IRepositoryQuery queryToEdit);

	/**
	 * @since 3.0
	 */
	public abstract IWizard getNewTaskWizard(TaskRepository taskRepository, ITaskMapping selection);

	/**
	 * Override to return a custom task editor ID. If overriding this method the connector becomes responsible for
	 * showing the additional pages handled by the default task editor. As of Mylyn 2.0M2 these are the Planning and
	 * Context pages.
	 * 
	 * @since 3.0
	 */
	public String getTaskEditorId(ITask repositoryTask) {
		return TaskEditor.ID_EDITOR;
	}

	public abstract boolean hasSearchPage();

	/**
	 * Contributions to the UI legend.
	 * 
	 * @deprecated use {@link #getLegendElements()} instead
	 */
	@Deprecated
	public List<ITask> getLegendItems() {
		return Collections.emptyList();
	}

	/**
	 * Contributions to the UI legend.
	 * 
	 * @since 3.0
	 */
	public List<LegendElement> getLegendElements() {
		return Collections.emptyList();
	}

	/**
	 * @param repositoryTask
	 *            can be null
	 * @since 3.0
	 */
	public String getTaskKindLabel(ITask task) {
		return LABEL_TASK_DEFAULT;
	}

	/**
	 * Connector-specific task icons. Not recommended to override unless providing custom icons and kind overlays.
	 * 
	 * For connectors that have a decorator that they want to reuse, the connector can maintain a reference to the label
	 * provider and get the descriptor from the images it returns.
	 * 
	 * @since 3.0
	 */
	public ImageDescriptor getImageDescriptor(IRepositoryElement element) {
		if (element instanceof IRepositoryQuery) {
			return TasksUiImages.QUERY;
		} else if (element instanceof ITask) {
			return TasksUiImages.TASK;
		} else {
			return null;
		}
	}

	/**
	 * Task kind overlay, recommended to override with connector-specific overlay.
	 * 
	 * @since 3.0
	 */
	public ImageDescriptor getTaskKindOverlay(ITask task) {
		return null;
	}

	/**
	 * Connector-specific priority icons. Not recommended to override since priority icons are used elsewhere in the
	 * Task List UI (e.g. filter selection in view menu).
	 * 
	 * @since 3.0
	 */
	public ImageDescriptor getTaskPriorityOverlay(ITask task) {
		return TasksUiInternal.getPriorityImage(task);
	}

	/**
	 * This method is not used anymore.
	 * 
	 * @return returns null
	 */
	@Deprecated
	public IWizard getAddExistingTaskWizard(TaskRepository repository) {
		return null;
	}

	/**
	 * @since 3.0
	 */
	public ITaskSearchPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return null;
	}

	/**
	 * Override to return a URL that provides the user with an account creation page for the repository
	 * 
	 * @param taskRepository
	 *            TODO
	 */
	public String getAccountCreationUrl(TaskRepository taskRepository) {
		return null;
	}

	/**
	 * Override to return a URL that provides the user with an account management page for the repository
	 * 
	 * @param taskRepository
	 *            TODO
	 */
	public String getAccountManagementUrl(TaskRepository taskRepository) {
		return null;
	}

	/**
	 * Override to return a URL that provides the user with a history page for the task.
	 * 
	 * @return a url of a page for the history of the task; null, if no history url is available
	 * @since 3.0
	 */
	public String getTaskHistoryUrl(TaskRepository taskRepository, ITask task) {
		return null;
	}

	/**
	 * Override to return a textual reference to a comment, e.g. for Bugzilla this method returns <code>#12</code> for
	 * comment 12. This reference is used when generating replies to comments
	 * 
	 * @return a reference to <code>comment</code>; null, if no reference is available
	 * @since 3.0
	 */
	public String getReplyText(TaskRepository taskRepository, ITask task, ITaskComment taskComment, boolean includeTask) {
		return null;
	}

	/**
	 * Returns an array of hyperlinks that link to tasks within <code>text</code>. If <code>index</code> is != -1
	 * clients may limit the results to hyperlinks found at <code>index</code>. It is legal for clients to always return
	 * all results.
	 * 
	 * @param repository
	 *            the task repository, never <code>null</code>
	 * @param text
	 *            the line of text
	 * @param index
	 *            the index within <code>text</code>, if -1 return all hyperlinks found in text
	 * @param textOffset
	 *            the offset of <code>text</code>
	 * @return an array of hyperlinks, or null if no hyperlinks were found
	 * @since 2.0
	 */
	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int index, int textOffset) {
		return null;
	}

	/**
	 * @since 3.0
	 */
	public boolean hasCustomNotifications() {
		return customNotificationHandling;
	}

	/**
	 * @since 3.0
	 * @return true if connector doesn't support non-grouping (flattening) of subtasks
	 */
	public boolean hasStrictSubtaskHierarchy() {
		return false;
	}

	/**
	 * @since 3.0
	 */
	public IWizardPage getTaskAttachmentPage(TaskAttachmentModel model) {
		return new TaskAttachmentPage(model);
	}

}
