/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/**
 * @author Shawn Minto
 */
public class TaskEditorContributionExtensionReader {

	private static final String ATTR_ID = "id"; //$NON-NLS-1$

	public static final String EXTENSION_TASK_EDITOR_PAGE_CONTRIBUTION = "org.eclipse.mylyn.tasks.ui.taskEditorPageContribution"; //$NON-NLS-1$

	private static final String REPOSITORY_TASK_EDITOR_CONTRIBUTION = "repositoryPart"; //$NON-NLS-1$

	private static final String LOCAL_TASK_EDITOR_CONTRIBUTION = "localPart"; //$NON-NLS-1$

	private static Collection<TaskEditorPartDescriptor> repositoryEditorContributions;

	private static Collection<LocalTaskEditorContributionDescriptor> localEditorContributions;

	public static Collection<LocalTaskEditorContributionDescriptor> getLocalEditorContributions() {
		if (localEditorContributions == null) {
			initExtensions();
		}
		return Collections.unmodifiableCollection(localEditorContributions);
	}

	public static Collection<TaskEditorPartDescriptor> getRepositoryEditorContributions() {
		if (repositoryEditorContributions == null) {
			initExtensions();
		}
		return Collections.unmodifiableCollection(repositoryEditorContributions);
	}

	private static void initExtensions() {
		Collection<TaskEditorPartDescriptor> repositoryContributions = new ArrayList<TaskEditorPartDescriptor>();
		Collection<LocalTaskEditorContributionDescriptor> localContributions = new ArrayList<LocalTaskEditorContributionDescriptor>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();

		IExtensionPoint editorExtensionPoint = registry.getExtensionPoint(EXTENSION_TASK_EDITOR_PAGE_CONTRIBUTION);
		IExtension[] editorExtensions = editorExtensionPoint.getExtensions();
		for (IExtension extension : editorExtensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(REPOSITORY_TASK_EDITOR_CONTRIBUTION)) {
					readRepositoryEditorContributionExtension(element, repositoryContributions);
				} else if (element.getName().equals(LOCAL_TASK_EDITOR_CONTRIBUTION)) {
					readLocalEditorContributionExtension(element, localContributions);
				}
			}
		}
		repositoryEditorContributions = repositoryContributions;
		localEditorContributions = localContributions;
	}

	private static void readRepositoryEditorContributionExtension(IConfigurationElement element,
			Collection<TaskEditorPartDescriptor> contributions) {

		try {
			String id = element.getAttribute(ATTR_ID);
			TaskEditorExtensionPartDescriptor descriptor = new TaskEditorExtensionPartDescriptor(id, element);
			contributions.add(descriptor);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Unable to read repository editor contribution", e)); //$NON-NLS-1$
		}

	}

	private static void readLocalEditorContributionExtension(IConfigurationElement element,
			Collection<LocalTaskEditorContributionDescriptor> localContributions) {

		try {
			localContributions.add(new LocalTaskEditorContributionDescriptor(element));
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Unable to read local editor contribution", e)); //$NON-NLS-1$
		}

	}
}
