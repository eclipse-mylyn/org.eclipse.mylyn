/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Fabio Zadrozny - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

/**
 * Helper to get extension point contributors
 * 
 * @author Fabio Zadrozny
 */
public class ResourcesUiExtensionPointReader {

	public final static String EXTENSION_CHANGE_MONITORING = "changeMonitoring"; //$NON-NLS-1$

	public final static String ELEMENT_EXCLUDE = "exclude"; //$NON-NLS-1$

	public final static String ATTR_PATTERN = "pattern"; //$NON-NLS-1$

	public final static String ATTR_ANT_PATTERN = "antPattern"; //$NON-NLS-1$

	private static Set<String> resourceExclusionPatterns = new HashSet<>();

	private static boolean extensionsRead = false;

	public static Set<String> getDefaultResourceExclusions() {
		if (!extensionsRead) {
			readExtensions();
		}
		return resourceExclusionPatterns;
	}

	private static void readExtensions() {
		IExtensionPoint teamProvider = Platform.getExtensionRegistry()
				.getExtensionPoint(ResourcesUiBridgePlugin.ID_PLUGIN + '.' + EXTENSION_CHANGE_MONITORING);
		IExtension[] extensions = teamProvider.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();

			for (IConfigurationElement element : elements) {
				if (ELEMENT_EXCLUDE.equals(element.getName())) {
					readLinkProvider(element);
				}
			}
		}
		extensionsRead = true;
	}

	private static void readLinkProvider(IConfigurationElement element) {
		String antPatternExclusion = element.getAttribute(ATTR_ANT_PATTERN);
		if (antPatternExclusion != null) {
			resourceExclusionPatterns.add(antPatternExclusion);
		}
		String exclude = element.getAttribute(ATTR_PATTERN);
		if (exclude != null) {
			resourceExclusionPatterns.addAll(ResourcePatternExclusionStrategy.convertToAntPattern(exclude));
		}
	}

}
