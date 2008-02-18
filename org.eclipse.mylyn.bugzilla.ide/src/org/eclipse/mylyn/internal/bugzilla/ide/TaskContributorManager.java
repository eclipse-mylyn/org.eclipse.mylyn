/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ide;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.bugzilla.ide.AbstractTaskContributor;
import org.eclipse.mylyn.internal.monitor.core.IMonitorCoreConstants;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;

/**
 * @author Steffen Pingel
 */
public class TaskContributorManager {

	private static final String EXTENSION_ID_TASK_CONTRIBUTORS = "org.eclipse.mylyn.bugzilla.ide.taskContributors";

	private static final String ELEMENT_TASK_CONTRIBUTOR = "taskContributor";

	private final List<AbstractTaskContributor> taskContributors = new CopyOnWriteArrayList<AbstractTaskContributor>();

	private boolean readExtensions;

	private static final String ELEMENT_CLASS = "class";

	public void addErrorReporter(AbstractTaskContributor taskContributor) {
		taskContributors.add(taskContributor);
	}

	public void removeErrorReporter(AbstractTaskContributor taskContributor) {
		taskContributors.remove(taskContributor);
	}

	public void updateAttributes(RepositoryTaskData taskData, IStatus status) {
		readExtensions();

		for (AbstractTaskContributor contributor : taskContributors) {
			String description = contributor.getDescription(status);
			if (description != null) {
				taskData.setDescription(description);
				return;
			}
		}
		
		DefaultTaskContributor contributor = new DefaultTaskContributor();
		taskData.setDescription(contributor.getDescription(status));
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
				StatusHandler.log(new Status(IStatus.WARNING, IMonitorCoreConstants.ID_PLUGIN,
						"Could not load task contributor extenstion: \"" + object.getClass().getCanonicalName() + "\""
						+ " does not implement \"" + AbstractTaskContributor.class.getCanonicalName() + "\""));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.WARNING, IMonitorCoreConstants.ID_PLUGIN,
					"Could not load task contributor extension", e));
		}
	}

}
