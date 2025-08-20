/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Rob Elves
 */
public class TaskRepositoryUtil {

	/**
	 * Is auto add of template repository disabled for repositoryUrl
	 *
	 * @since 2.1
	 */
	public static boolean isAddAutomaticallyDisabled(String repositoryUrl) {
		String deletedTemplates = TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getString(ITasksUiPreferenceConstants.TEMPLATES_DELETED);
		String[] templateUrls = deletedTemplates.split("\\" + ITasksUiPreferenceConstants.TEMPLATES_DELETED_DELIM); //$NON-NLS-1$
		for (String deletedUrl : templateUrls) {
			if (deletedUrl.equalsIgnoreCase(repositoryUrl)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Disable template repository from being automatically added
	 *
	 * @since 2.1
	 */
	public static void disableAddAutomatically(String repositoryUrl) {
		if (!isAddAutomaticallyDisabled(repositoryUrl) && isAddAutomatically(repositoryUrl)) {
			String deletedTemplates = TasksUiPlugin.getDefault()
					.getPreferenceStore()
					.getString(ITasksUiPreferenceConstants.TEMPLATES_DELETED);
			deletedTemplates += ITasksUiPreferenceConstants.TEMPLATES_DELETED_DELIM + repositoryUrl;
			TasksUiPlugin.getDefault()
					.getPreferenceStore()
					.setValue(ITasksUiPreferenceConstants.TEMPLATES_DELETED, deletedTemplates);
			TasksUiPlugin.getDefault().savePluginPreferences();
		}
	}

	/**
	 * Template exists and is auto add enabled
	 */
	public static boolean isAddAutomatically(String repositoryUrl) {
		for (AbstractRepositoryConnector connector : TasksUi.getRepositoryManager().getRepositoryConnectors()) {
			for (RepositoryTemplate template : TasksUiPlugin.getRepositoryTemplateManager()
					.getTemplates(connector.getConnectorKind())) {
				if (template.repositoryUrl != null && template.repositoryUrl.equalsIgnoreCase(repositoryUrl)
						&& template.addAutomatically) {
					return true;
				}
			}
		}
		return false;
	}

}
