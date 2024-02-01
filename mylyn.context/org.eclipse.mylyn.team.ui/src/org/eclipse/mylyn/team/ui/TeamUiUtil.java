/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.team.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.internal.team.ui.Messages;
import org.eclipse.mylyn.internal.team.ui.properties.TeamPropertiesLinkProvider;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Display;

/**
 * Provides static helper method for change sets.
 * 
 * @author Steffen Pingel
 * @since 3.5
 */
public class TeamUiUtil {

	/**
	 * Returns a commit comment specific to <code>task</code> and <code>resources</code>. If <code>resources</code> is null or the
	 * associated projects do not specify a custom commit comment template the global template is used.
	 * <p>
	 * This method must be invoked on the UI thread.
	 * 
	 * @param checkTaskRepository
	 *            if true, a warning dialog is displayed in case <code>task</code> is associated with a different repository than any of the
	 *            <code>resources</code>
	 * @param task
	 *            the task to generate the commit comment for
	 * @param resources
	 *            that are being committed or null
	 * @return a commit comment or an empty string if the user opted to abort generating the commit message
	 * @since 3.5
	 */
	public static String getComment(boolean checkTaskRepository, ITask task, IResource[] resources) {
		// lookup project specific template
		String template = null;
		Set<IProject> projects = new HashSet<>();
		if (resources != null) {
			for (IResource resource : resources) {
				IProject project = resource.getProject();
				if (project != null && project.isAccessible() && !projects.contains(project)) {
					TeamPropertiesLinkProvider provider = new TeamPropertiesLinkProvider();
					template = provider.getCommitCommentTemplate(project);
					if (template != null) {
						break;
					}
					projects.add(project);
				}
			}
		}

		boolean proceed = true;

		// prompt if resources do not match task
		if (checkTaskRepository) {
			boolean unmatchedRepositoryFound = false;
			for (IProject project : projects) {
				TaskRepository repository = TasksUiPlugin.getDefault().getRepositoryForResource(project);
				if (repository != null) {
					if (!repository.getRepositoryUrl().equals(task.getRepositoryUrl())) {
						unmatchedRepositoryFound = true;
					}
				}
			}

			if (unmatchedRepositoryFound) {
				if (Display.getCurrent() != null) {
					proceed = MessageDialog.openQuestion(WorkbenchUtil.getShell(),
							Messages.ContextChangeSet_Mylyn_Change_Set_Management,
							Messages.ContextChangeSet_ATTEMPTING_TO_COMMIT_RESOURCE);
				} else {
					proceed = false;
				}
			}
		}

		if (proceed) {
			if (template == null) {
				template = FocusedTeamUiPlugin.getDefault()
						.getPreferenceStore()
						.getString(FocusedTeamUiPlugin.COMMIT_TEMPLATE);
			}
			return FocusedTeamUiPlugin.getDefault().getCommitTemplateManager().generateComment(task, template);
		} else {
			return ""; //$NON-NLS-1$
		}
	}

}
