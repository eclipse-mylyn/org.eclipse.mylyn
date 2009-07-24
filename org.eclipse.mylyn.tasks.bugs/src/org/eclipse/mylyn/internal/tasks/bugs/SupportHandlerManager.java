/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.AbstractSupportHandler;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.ISupportResponse;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.ITaskContribution;

/**
 * @author Steffen Pingel
 */
public class SupportHandlerManager {

	private static final String ELEMENT_CLASS = "class"; //$NON-NLS-1$

	private static final String ELEMENT_TASK_HANDLER = "handler"; //$NON-NLS-1$

	private static final String EXTENSION_ID_TASK_CONTRIBUTORS = "org.eclipse.mylyn.tasks.bugs.support"; //$NON-NLS-1$

	private final DefaultSupportHandler defaultSupportHandler = new DefaultSupportHandler();

	private boolean readExtensions;

	private final List<AbstractSupportHandler> taskContributors = new CopyOnWriteArrayList<AbstractSupportHandler>();

	public SupportHandlerManager() {
	}

	public void addErrorReporter(AbstractSupportHandler taskContributor) {
		taskContributors.add(taskContributor);
	}

	public void process(final ITaskContribution contribution, final IProgressMonitor monitor) {
		readExtensions();

		for (final AbstractSupportHandler contributor : taskContributors) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, "Task contributor failed", e)); //$NON-NLS-1$
				}

				public void run() throws Exception {
					contributor.process(contribution, monitor);
				}
			});
			if (contribution.isHandled()) {
				break;
			}
		}
		if (!contribution.isHandled()) {
			defaultSupportHandler.process(contribution, monitor);
		}
	}

	public void postProcess(final ISupportResponse response, final IProgressMonitor monitor) {
		readExtensions();

		for (final AbstractSupportHandler contributor : taskContributors) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, "Task contributor failed", e)); //$NON-NLS-1$
				}

				public void run() throws Exception {
					contributor.postProcess(response, monitor);
				}
			});
		}
		defaultSupportHandler.postProcess(response, monitor);
	}

	public void preProcess(final SupportRequest request) {
		readExtensions();

		for (final AbstractSupportHandler contributor : taskContributors) {
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, "Task contributor failed", e)); //$NON-NLS-1$
				}

				public void run() throws Exception {
					contributor.preProcess(request);
				}
			});
		}
		defaultSupportHandler.preProcess(request);
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
				if (element.getName().equals(ELEMENT_TASK_HANDLER)) {
					readTaskContributor(element);
				}
			}
		}
	}

	private void readTaskContributor(IConfigurationElement element) {
		try {
			Object object = element.createExecutableExtension(ELEMENT_CLASS);
			if (object instanceof AbstractSupportHandler) {
				taskContributors.add((AbstractSupportHandler) object);
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN,
						"Could not load task contributor extenstion: \"" + object.getClass().getCanonicalName() + "\"" //$NON-NLS-1$ //$NON-NLS-2$
								+ " does not implement \"" + AbstractSupportHandler.class.getCanonicalName() + "\"")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN,
					"Could not load task contributor extension", e)); //$NON-NLS-1$
		}
	}

	public void removeErrorReporter(AbstractSupportHandler taskContributor) {
		taskContributors.remove(taskContributor);
	}

}
