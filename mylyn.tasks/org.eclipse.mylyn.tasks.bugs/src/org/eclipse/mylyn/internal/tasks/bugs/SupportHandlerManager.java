/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.bugs.AbstractSupportHandler;
import org.eclipse.mylyn.tasks.bugs.ISupportResponse;
import org.eclipse.mylyn.tasks.bugs.ITaskContribution;

/**
 * @author Steffen Pingel
 */
public class SupportHandlerManager {

	private static final String ELEMENT_CLASS = "class"; //$NON-NLS-1$

	private static final String ELEMENT_TASK_HANDLER = "handler"; //$NON-NLS-1$

	private static final String EXTENSION_ID_TASK_CONTRIBUTORS = "support"; //$NON-NLS-1$

	private final DefaultSupportHandler defaultSupportHandler = new DefaultSupportHandler();

	private boolean readExtensions;

	private final List<AbstractSupportHandler> taskContributors = new CopyOnWriteArrayList<>();

	public SupportHandlerManager() {
	}

	public void addErrorReporter(AbstractSupportHandler taskContributor) {
		taskContributors.add(taskContributor);
	}

	public void process(final ITaskContribution contribution, final IProgressMonitor monitor) {
		readExtensions();

		for (final AbstractSupportHandler contributor : taskContributors) {
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable e) {
					StatusHandler
							.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, "Task contributor failed", e)); //$NON-NLS-1$
				}

				@Override
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
				@Override
				public void handleException(Throwable e) {
					StatusHandler
							.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, "Task contributor failed", e)); //$NON-NLS-1$
				}

				@Override
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
				@Override
				public void handleException(Throwable e) {
					StatusHandler
							.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, "Task contributor failed", e)); //$NON-NLS-1$
				}

				@Override
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

		ExtensionPointReader<AbstractSupportHandler> reader = new ExtensionPointReader<>(
				TasksBugsPlugin.ID_PLUGIN, EXTENSION_ID_TASK_CONTRIBUTORS, ELEMENT_TASK_HANDLER,
				AbstractSupportHandler.class) {
			@Override
			protected AbstractSupportHandler readElement(IConfigurationElement element,
					org.eclipse.core.runtime.MultiStatus result) {
				return readTaskContributor(element, result);
			}
		};
		reader.read();
		taskContributors.addAll(reader.getItems());
	}

	private AbstractSupportHandler readTaskContributor(IConfigurationElement element, MultiStatus result) {
		try {
			Object object = element.createExecutableExtension(ELEMENT_CLASS);
			if (object instanceof AbstractSupportHandler) {
				return (AbstractSupportHandler) object;
			} else {
				result.add(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN,
						"Could not load task contributor extenstion: \"" + object.getClass().getCanonicalName() + "\"" //$NON-NLS-1$ //$NON-NLS-2$
								+ " does not implement \"" + AbstractSupportHandler.class.getCanonicalName() + "\"")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (Throwable e) {
			result.add(new Status(IStatus.WARNING, TasksBugsPlugin.ID_PLUGIN,
					"Could not load task contributor extension", e)); //$NON-NLS-1$
		}
		return null;
	}

	public void removeErrorReporter(AbstractSupportHandler taskContributor) {
		taskContributors.remove(taskContributor);
	}

}
