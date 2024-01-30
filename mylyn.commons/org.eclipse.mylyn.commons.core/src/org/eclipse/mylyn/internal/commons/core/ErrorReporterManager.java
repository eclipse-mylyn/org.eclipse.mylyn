/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.AbstractErrorReporter;
import org.eclipse.mylyn.commons.core.StatusHandler;

/**
 * @author Steffen Pingel
 */
public class ErrorReporterManager {

	private static final String EXTENSION_ID_ERROR_REPORTERS = "org.eclipse.mylyn.commons.core.errorReporters"; //$NON-NLS-1$

	private static final String ELEMENT_ERROR_REPORTER = "errorReporter"; //$NON-NLS-1$

	private final List<AbstractErrorReporter> errorReporters = new CopyOnWriteArrayList<>();

	private boolean readExtensions;

	private static final String ELEMENT_CLASS = "class"; //$NON-NLS-1$

	public ErrorReporterManager() {
	}

	public void addErrorReporter(AbstractErrorReporter errorReporter) {
		errorReporters.add(errorReporter);
	}

	public void removeErrorReporter(AbstractErrorReporter errorReporter) {
		errorReporters.remove(errorReporter);
	}

	public void fail(IStatus status) {
		readExtensions();

		int priority = AbstractErrorReporter.PRIORITY_NONE;
		List<AbstractErrorReporter> interestedReporters = new ArrayList<>();
		for (AbstractErrorReporter reporter : errorReporters) {
			int newPriority = reporter.getPriority(status);
			if (newPriority > AbstractErrorReporter.PRIORITY_NONE) {
				if (newPriority > priority) {
					interestedReporters.clear();
					interestedReporters.add(reporter);
					priority = newPriority;
				} else if (newPriority == priority) {
					interestedReporters.add(reporter);
				}
			}
		}

		AbstractErrorReporter reporter;
		if (interestedReporters.isEmpty()) {
			return;
		} else if (interestedReporters.size() > 1) {
			// TODO prompt user?
			reporter = interestedReporters.get(0);
		} else {
			reporter = interestedReporters.get(0);
		}

		reporter.handle(status);
	}

	private synchronized void readExtensions() {
		if (readExtensions) {
			return;
		}

		readExtensions = true;

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_ERROR_REPORTERS);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELEMENT_ERROR_REPORTER)) {
					readErrorReporter(element);
				}
			}
		}
	}

	private void readErrorReporter(IConfigurationElement element) {
		try {
			Object object = element.createExecutableExtension(ELEMENT_CLASS);
			if (object instanceof AbstractErrorReporter) {
				errorReporters.add((AbstractErrorReporter) object);
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, ICommonsCoreConstants.ID_PLUGIN,
						"Could not load error reporter extenstion: \"" + object.getClass().getCanonicalName() + "\"" //$NON-NLS-1$ //$NON-NLS-2$
								+ " does not implement \"" + AbstractErrorReporter.class.getCanonicalName() + "\"")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.WARNING, ICommonsCoreConstants.ID_PLUGIN,
					"Could not load error reporter extension", e)); //$NON-NLS-1$
		}
	}

}
