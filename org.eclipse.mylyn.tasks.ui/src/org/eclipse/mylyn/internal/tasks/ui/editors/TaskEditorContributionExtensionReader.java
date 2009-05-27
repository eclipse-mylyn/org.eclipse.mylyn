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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/**
 * @author Shawn Minto
 */
public class TaskEditorContributionExtensionReader {

	private static final String ATTR_ID = "id"; //$NON-NLS-1$

	public static final String EXTENSION_TASK_EDITOR_PAGE_CONTRIBUTION = "org.eclipse.mylyn.tasks.ui.taskEditorPageContribution"; //$NON-NLS-1$

	private static final String REPOSITORY_TASK_EDITOR_CONTRIBUTION = "repositoryTaskEditorPageContribution"; //$NON-NLS-1$

	private static Collection<TaskEditorPartDescriptor> editorContributions;

	public static Collection<TaskEditorPartDescriptor> getEditorContributions() {
		if (editorContributions == null) {
			editorContributions = initExtensions();
		}
		return Collections.unmodifiableCollection(editorContributions);
	}

	private static Collection<TaskEditorPartDescriptor> initExtensions() {
		Collection<TaskEditorPartDescriptor> contributions = new ArrayList<TaskEditorPartDescriptor>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		IExtensionPoint editorExtensionPoint = registry.getExtensionPoint(EXTENSION_TASK_EDITOR_PAGE_CONTRIBUTION);
		IExtension[] editorExtensions = editorExtensionPoint.getExtensions();
		for (IExtension extension : editorExtensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(REPOSITORY_TASK_EDITOR_CONTRIBUTION)) {
					readEditorContributionExtension(element, contributions);
				}
			}
		}
		return contributions;
	}

	private static void readEditorContributionExtension(IConfigurationElement element,
			Collection<TaskEditorPartDescriptor> contributions) {

		String id = element.getAttribute(ATTR_ID);
		TaskEditorExtensionPartDescriptor descriptor = new TaskEditorExtensionPartDescriptor(id, element);
		contributions.add(descriptor);

	}
}
