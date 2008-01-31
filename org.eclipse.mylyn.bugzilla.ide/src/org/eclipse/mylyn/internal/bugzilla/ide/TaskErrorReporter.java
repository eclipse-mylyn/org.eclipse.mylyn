/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ide;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.monitor.core.AbstractErrorReporter;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Steffen Pingel
 */
public class TaskErrorReporter extends AbstractErrorReporter {

	private static final String EXTENSION_ID_PLUGIN_REPOSITORY_MAPPING = "org.eclipse.mylyn.bugzilla.ide.pluginRepositoryMappings";

	private static final String ELEMENT_MAPPING = "mapping";

	private static final String ELEMENT_REPOSITORY_ATTRIBUTES = "repositoryAttributes";

	private static final String ELEMENT_PLUGID_ID_PREFIX = "pluginIdPrefix";

	private TreeMap<String, PluginRepositoryMapping> mappingByPrefix;

	private boolean readExtensions;

	@Override
	public int getPriority(IStatus status) {
		Assert.isNotNull(status);

		readExtensions();

		String pluginId = status.getPlugin();
		for (int i = 0; i <= pluginId.length(); i++) {
			if (mappingByPrefix.containsKey(pluginId.substring(0, i))) {
				return PRIORITY_DEFAULT;
			}
		}

		return PRIORITY_NONE;
	}

	@Override
	public void handle(IStatus status) {
		Assert.isNotNull(status);

		readExtensions();

		Map<String, String> attributes = new HashMap<String, String>();
		String pluginId = status.getPlugin();
		for (int i = 0; i <= pluginId.length(); i++) {
			PluginRepositoryMapping mapping = mappingByPrefix.get(pluginId.substring(0, i));
			if (mapping != null) {
				attributes.putAll(mapping.getAttributes());
			}
		}

		openNewTaskEditor(status, attributes);
	}

	private void openNewTaskEditor(IStatus status, Map<String, String> attributes) {
		TaskSelection taskSelection = createTaskSelection(status, attributes);

		TaskRepository taskRepository = null;
		String repositoryUrl = attributes.get(IRepositoryConstants.REPOSITORY);
		if (repositoryUrl != null) {
			String repositoryKind = attributes.get(IRepositoryConstants.REPOSITORY_KIND);
			if (repositoryKind != null) {
				taskRepository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryUrl);
			} else {
				taskRepository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryUrl, repositoryKind);
			}
		}

		if (taskRepository != null) {
			RepositoryTaskData taskData = createTaskData(taskRepository, taskSelection);
			if (taskData != null) {
				// open taskData in editor
			} else {
				TasksUiUtil.openNewTaskEditor(null, taskSelection, taskRepository);
			}
		} else {
			TasksUiUtil.openNewTaskEditor(null, taskSelection, null);
		}
	}

	private RepositoryTaskData createTaskData(TaskRepository taskRepository, TaskSelection taskSelection) {
		// ignore
		return null;
	}

	private TaskSelection createTaskSelection(IStatus status, Map<String, String> attributes) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n-- Error Details --\n");
		if (status.getException() != null) {
			sb.append("\nStack Trace:\n");
			StringWriter writer = new StringWriter();
			status.getException().printStackTrace(new PrintWriter(writer));
			sb.append(writer.getBuffer());
		}

		TaskSelection taskSelection = new TaskSelection(status.getMessage(), sb.toString());
//		addAttribute(taskSelection, "product", RepositoryTaskAttribute.PRODUCT, attributes);
//		taskSelection.getTaskData().addAttribute(, )

		return taskSelection;
	}

	private synchronized void readExtensions() {
		if (readExtensions) {
			Assert.isNotNull(mappingByPrefix);
			return;
		}

		mappingByPrefix = new TreeMap<String, PluginRepositoryMapping>();
		readExtensions = true;

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_PLUGIN_REPOSITORY_MAPPING);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELEMENT_MAPPING)) {
					readMapping(element);
				}
			}
		}

	}

	private void readMapping(IConfigurationElement element) {
		String repositoryAttributes = element.getAttribute(ELEMENT_REPOSITORY_ATTRIBUTES);
		String pluginIdPrefix = element.getAttribute(ELEMENT_PLUGID_ID_PREFIX);

		Map<String, String> attributes;
		try {
			KeyValueParser parser = new KeyValueParser(repositoryAttributes);
			attributes = parser.parse();
		} catch (ParseException e) {
			StatusHandler.log(new Status(IStatus.WARNING, IBugzillaIdeConstants.ID_PLUGIN,
					"Invalid repositoryAttributes in extension.", e));
			return;
		}

		PluginRepositoryMapping pluginRepositoryMapping = new PluginRepositoryMapping();
		pluginRepositoryMapping.addAttributes(attributes);
		pluginRepositoryMapping.addPrefix(pluginIdPrefix);

		addPluginRepositoryMapping(pluginRepositoryMapping);
	}

	private void addPluginRepositoryMapping(PluginRepositoryMapping pluginRepositoryMapping) {
		List<String> prefixs = pluginRepositoryMapping.getPrefixes();
		for (String prefix : prefixs) {
			mappingByPrefix.put(prefix, pluginRepositoryMapping);
		}
	}

}
