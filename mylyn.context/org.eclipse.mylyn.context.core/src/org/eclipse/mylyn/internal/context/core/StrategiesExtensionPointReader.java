/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextComputationStrategy;
import org.eclipse.osgi.util.NLS;

/**
 * Read the strategies extension point
 * 
 * @author David Green
 */
public class StrategiesExtensionPointReader {

	private static final String CONTEXT_COMPUTATION_STRATEGY = "contextComputationStrategy"; //$NON-NLS-1$

	private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$

	private static final String STRATEGIES_EXTENSION_POINT_ID = ContextCorePlugin.ID_PLUGIN + ".strategies"; //$NON-NLS-1$

	public static List<ContextComputationStrategy> readContextComputationStrategies() {
		List<ContextComputationStrategy> strategies = new ArrayList<ContextComputationStrategy>();

		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(STRATEGIES_EXTENSION_POINT_ID);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] configurationElements = extension.getConfigurationElements();
			for (IConfigurationElement element : configurationElements) {
				if (element.getName().equals(CONTEXT_COMPUTATION_STRATEGY)) {
					try {
						ContextComputationStrategy strategy = (ContextComputationStrategy) element
								.createExecutableExtension(ATTRIBUTE_CLASS);
						strategies.add(strategy);
					} catch (Throwable t) {
						StatusHandler
								.log(new Status(
										IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, NLS
												.bind("Cannot instantiate {0} from bundle {1}: {2}", //$NON-NLS-1$
														new Object[] { element.getAttribute(ATTRIBUTE_CLASS),
																extension.getContributor().getName(), t.getMessage() }),
										t));
					}
				}
			}
		}
		return strategies;
	}
}
