/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

	public final static String EXTENSION_CHANGE_MONITORING = "changeMonitoring";

	public final static String ELEMENT_EXCLUDE = "exclude";

	public final static String ATTR_PATTERN = "pattern";

	private static Set<String> resourceExclusionPatterns = new HashSet<String>();

	private static boolean extensionsRead = false;

	public static Set<String> getDefaultResourceExclusions() {
		if (!extensionsRead) {
			readExtensions();
		}
		return resourceExclusionPatterns;
	}

	private static void readExtensions() {
		IExtensionPoint teamProvider = Platform.getExtensionRegistry().getExtensionPoint(
				ResourcesUiBridgePlugin.ID_PLUGIN + '.' + EXTENSION_CHANGE_MONITORING);
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
		String exclude = element.getAttribute(ATTR_PATTERN);
		if (exclude != null) {
			resourceExclusionPatterns.add(exclude);
		}
	}

}
