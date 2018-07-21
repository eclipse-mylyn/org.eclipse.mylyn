/*******************************************************************************
 * Copyright (c) 2004, 2016 David Green and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IPluginContribution;

import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;

/**
 * @author David Green
 */
public class TaskEditorExtensions {

	public static final String REPOSITORY_PROPERTY_EDITOR_EXTENSION = "editorExtension"; //$NON-NLS-1$

	public static final String REPOSITORY_PROPERTY_AVATAR_SUPPORT = "avatarSupport"; //$NON-NLS-1$

	private static final String MARKUP_KEY = "markup"; //$NON-NLS-1$

	private static final String BASE_MARKUP_KEY = "base-markup"; //$NON-NLS-1$

	private static Map<String, RegisteredTaskEditorExtension> extensionsById = new HashMap<String, RegisteredTaskEditorExtension>();

	private static Map<String, String> associationByConnectorKind = new HashMap<String, String>();

	private static boolean initialized;

	public static SortedSet<RegisteredTaskEditorExtension> getTaskEditorExtensions() {
		init();
		return new TreeSet<RegisteredTaskEditorExtension>(extensionsById.values());
	}

	/**
	 * Contributes an extension to the {@link TaskEditor}.
	 * 
	 * @param pluginId
	 *            the id of the contributing plug-in, may be <code>null</code>
	 * @param id
	 *            the id of the extension, may not be <code>null</code>
	 * @param name
	 *            the name of the extension that is displayed in the settings page
	 * @param extension
	 *            the extension implementation
	 */
	public static void addTaskEditorExtension(String pluginId, String id, String name,
			AbstractTaskEditorExtension extension) {
		Assert.isNotNull(id);
		RegisteredTaskEditorExtension descriptor = new RegisteredTaskEditorExtension(extension, id, name);
		descriptor.setPluginId(pluginId);
		RegisteredTaskEditorExtension previous = extensionsById.put(id, descriptor);
		if (previous != null) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Duplicate taskEditorExtension id=" //$NON-NLS-1$
					+ id, null));
		}
	}

	public static void addRepositoryAssociation(String connectorKind, String extensionId) {
		if (connectorKind == null || extensionId == null) {
			throw new IllegalArgumentException();
		}
		String previous = associationByConnectorKind.put(connectorKind, extensionId);
		if (previous != null) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, String.format(
					"Duplicate association for repository %s: %s replaces %s", connectorKind, extensionId, previous), //$NON-NLS-1$
					null));
		}
	}

	/**
	 * get a task editor extension for a specific repository
	 * 
	 * @param taskRepository
	 * @return the extension, or null if there is none
	 * @see #getDefaultTaskEditorExtension(TaskRepository)
	 */
	public static AbstractTaskEditorExtension getTaskEditorExtension(TaskRepository taskRepository) {
		init();
		String extensionId = getTaskEditorExtensionId(taskRepository);
		if (extensionId != null) {
			RegisteredTaskEditorExtension taskEditorExtension = extensionsById.get(extensionId);
			return taskEditorExtension == null ? null : taskEditorExtension.getExtension();
		}
		return null;
	}

	/**
	 * get a task editor extension for a specific task attribute
	 * 
	 * @param taskRepository
	 * @param taskAttribute
	 * @return the extension, or null if there is none
	 * @see #getTaskEditorExtension(TaskRepository);
	 * @since 3.11
	 */
	public static AbstractTaskEditorExtension getTaskEditorExtension(TaskRepository taskRepository,
			TaskAttribute taskAttribute) {
		init();
		String input = taskAttribute.getMetaData().getMediaType();
		if (input != null) {
			try {
				MediaType media = MediaType.parse(input);
				Multimap<String, String> parameters = media.parameters();
				if (parameters.containsKey(MARKUP_KEY)) {
					Iterator<String> iter = parameters.get(MARKUP_KEY).iterator();
					String markup = iter.next();
					Iterator<String> baseMarkupIterator = parameters.get(BASE_MARKUP_KEY).iterator();
					String baseMarkup = (baseMarkupIterator.hasNext() ? baseMarkupIterator.next() : ""); //$NON-NLS-1$

					SortedSet<RegisteredTaskEditorExtension> extensions = getTaskEditorExtensions();
					for (RegisteredTaskEditorExtension extension : extensions) {
						if (markup.equals(extension.getName()) || baseMarkup.equals(extension.getName())) {
							return extension.getExtension();
						}
					}
				}
			} catch (IllegalArgumentException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, String.format(
						"Unable to parse markup type for attribute %s", taskAttribute.toString()), e)); //$NON-NLS-1$
			}
		}
		return getTaskEditorExtension(taskRepository);
	}

	public static String getTaskEditorExtensionId(TaskRepository taskRepository) {
		init();
		String id = taskRepository.getProperty(REPOSITORY_PROPERTY_EDITOR_EXTENSION);
		if (id == null) {
			id = getDefaultTaskEditorExtensionId(taskRepository);
		}
		return id;
	}

	public static void setTaskEditorExtensionId(TaskRepository repository, String editorExtensionId) {
		repository.setProperty(REPOSITORY_PROPERTY_EDITOR_EXTENSION, editorExtensionId);
	}

	/**
	 * Get the default task editor extension id for the given task repository
	 * 
	 * @param taskRepository
	 * @return the default task editor extension id or null if there is no default
	 */
	public static String getDefaultTaskEditorExtensionId(TaskRepository taskRepository) {
		return getDefaultTaskEditorExtensionId(taskRepository.getConnectorKind());
	}

	/**
	 * Get the default task editor extension id for the given kind of connector
	 * 
	 * @param connectorKind
	 *            the kind of connector
	 * @return the default task editor extension id or null if there is no default
	 */
	public static String getDefaultTaskEditorExtensionId(String connectorKind) {
		init();
		return associationByConnectorKind.get(connectorKind);
	}

	/**
	 * get a default task editor extension for a specific repository
	 * 
	 * @param taskRepository
	 * @return the extension, or null if there is none
	 * @see #getTaskEditorExtension(TaskRepository)
	 */
	public static AbstractTaskEditorExtension getDefaultTaskEditorExtension(TaskRepository taskRepository) {
		init();
		String extensionId = getDefaultTaskEditorExtensionId(taskRepository);
		if (extensionId != null) {
			RegisteredTaskEditorExtension taskEditorExtension = extensionsById.get(extensionId);
			return taskEditorExtension == null ? null : taskEditorExtension.getExtension();
		}
		return null;
	}

	private static void init() {
		if (!initialized) {
			initialized = true;
			TaskEditorExtensionReader.initExtensions();
		}
	}

	public static class RegisteredTaskEditorExtension implements Comparable<RegisteredTaskEditorExtension>,
			IPluginContribution {

		private final String id;

		private final String name;

		private final AbstractTaskEditorExtension extension;

		private String pluginId;

		private RegisteredTaskEditorExtension(AbstractTaskEditorExtension extension, String id, String name) {
			this.extension = extension;
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public AbstractTaskEditorExtension getExtension() {
			return extension;
		}

		public int compareTo(RegisteredTaskEditorExtension o) {
			if (o == this) {
				return 0;
			}
			int i = name.compareTo(o.name);
			if (i == 0) {
				i = id.compareTo(o.id);
			}
			return i;
		}

		public String getLocalId() {
			return id;
		}

		public String getPluginId() {
			return pluginId;
		}

		public void setPluginId(String pluginId) {
			this.pluginId = pluginId;
		}

	}

}
