/*******************************************************************************
 * Copyright (c) 2004, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;

/**
 * @author David Green
 */
public class TaskEditorExtensionReader {

	private static final String CONNECTOR_KIND = "connectorKind";

	public static final String ATTR_ID = "id";

	public static final String ATTR_NAME = "name";

	public static final String EXTENSION_TASK_EDITOR_EXTENSIONS = "org.eclipse.mylyn.tasks.ui.taskEditorExtensions";

	private static final String REPOSITORY_ASSOCIATION = "repositoryAssociation";

	private static final String TASK_EDITOR_EXTENSION = "taskEditorExtension";

	public static void initExtensions() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		IExtensionPoint editorExtensionPoint = registry.getExtensionPoint(EXTENSION_TASK_EDITOR_EXTENSIONS);
		IExtension[] editorExtensions = editorExtensionPoint.getExtensions();
		for (IExtension extension : editorExtensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(TASK_EDITOR_EXTENSION)) {
					readEditorExtension(element);
				} else if (element.getName().equals(REPOSITORY_ASSOCIATION)) {
					readEditorExtensionAssociation(element);
				}
			}
		}
	}

	private static void readEditorExtension(IConfigurationElement element) {
		try {
			String id = element.getAttribute(ATTR_ID);
			String name = element.getAttribute(ATTR_NAME);
			AbstractTaskEditorExtension extension = (AbstractTaskEditorExtension) element.createExecutableExtension("class");
			TaskEditorExtensions.addTaskEditorExtension(id, name, extension);
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load taskEditorExtension",
					e));
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load taskEditorExtension",
					e));
		}
	}

	private static void readEditorExtensionAssociation(IConfigurationElement element) {
		try {
			String repository = element.getAttribute(CONNECTOR_KIND);
			String taskEditorExtension = element.getAttribute(TASK_EDITOR_EXTENSION);
			TaskEditorExtensions.addRepositoryAssociation(repository, taskEditorExtension);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not load repositoryAssociation", e));
		}
	}

}
