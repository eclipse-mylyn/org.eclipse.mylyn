/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.bugs.AbstractTaskContributor;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class TaskContributorManager {

	private static final String ELEMENT_CLASS = "class";

	private static final String ELEMENT_TASK_CONTRIBUTOR = "contributor";

	private static final String EXTENSION_ID_TASK_CONTRIBUTORS = "org.eclipse.mylyn.tasks.bugs.taskContributors";

	private final DefaultTaskContributor defaultTaskContributor = new DefaultTaskContributor();

	private boolean readExtensions;

	private final List<AbstractTaskContributor> taskContributors = new CopyOnWriteArrayList<AbstractTaskContributor>();

	public void addErrorReporter(AbstractTaskContributor taskContributor) {
		taskContributors.add(taskContributor);
	}

	public String getEditorId(IStatus status) {
		readExtensions();

		for (AbstractTaskContributor contributor : taskContributors) {
			String editorId = contributor.getEditorId(status);
			if (editorId != null) {
				return editorId;
			}
		}

		return defaultTaskContributor.getEditorId(status);
	}

	private synchronized void readExtensions() {
		if (readExtensions) {
			return;
		}
		readExtensions = true;

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_TASK_CONTRIBUTORS);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELEMENT_TASK_CONTRIBUTOR)) {
					readTaskContributor(element);
				}
			}
		}
	}

	private void readTaskContributor(IConfigurationElement element) {
		try {
			Object object = element.createExecutableExtension(ELEMENT_CLASS);
			if (object instanceof AbstractTaskContributor) {
				taskContributors.add((AbstractTaskContributor) object);
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN,
						"Could not load task contributor extenstion: \"" + object.getClass().getCanonicalName() + "\""
								+ " does not implement \"" + AbstractTaskContributor.class.getCanonicalName() + "\""));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN,
					"Could not load task contributor extension", e));
		}
	}

	public void removeErrorReporter(AbstractTaskContributor taskContributor) {
		taskContributors.remove(taskContributor);
	}

	public void postProcess(final IStatus status, final TaskData taskData) {
		readExtensions();

		for (final AbstractTaskContributor contributor : taskContributors) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, "Task contributor failed", e));
				}

				public void run() throws Exception {
					contributor.postProcess(status, taskData);
				}
			});
		}
	}

	public void preProcess(final IStatus status, final Map<String, String> attributes) {
		readExtensions();

		final boolean[] handled = new boolean[1];
		for (final AbstractTaskContributor contributor : taskContributors) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, "Task contributor failed", e));
				}

				public void run() throws Exception {
					Map<String, String> contributorAttributes = contributor.getAttributes(status);
					if (contributorAttributes != null) {
						handled[0] = true;
						attributes.putAll(contributorAttributes);
					}
				}
			});
		}
		if (!handled[0]) {
			attributes.putAll(defaultTaskContributor.getAttributes(status));
		}
	}

}
