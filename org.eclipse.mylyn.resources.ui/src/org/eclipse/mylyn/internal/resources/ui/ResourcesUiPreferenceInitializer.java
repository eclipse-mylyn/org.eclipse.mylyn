/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.mylyn.resources.ResourcesUiBridgePlugin;

/**
 * This class is responsible for creating, storing and retrieving the values for the default context in the preference
 * store. It is registered as an initializer class for the 'org.eclipse.core.runtime.preferences' extension point.
 * 
 * @author Fabio (bug 178931)
 * @author Mik Kersten
 */
public class ResourcesUiPreferenceInitializer extends AbstractPreferenceInitializer {

	public static final String PREF_DEFAULT_SCOPE = "org.eclipse.mylyn.ide.resources";

	private static final String PREF_STORE_DELIM = ", ";

	public static final String PREF_RESOURCES_IGNORED = PREF_DEFAULT_SCOPE + ".ignored.pattern";

	@Override
	public void initializeDefaultPreferences() {
		// ignore, default comes from extension point
	}

	/**
	 * Restores the default values for the patterns to ignore.
	 */
	public static void restoreDefaultExcludedResourcePatterns() {
		setExcludedResourcePatterns(ResourcesUiExtensionPointReader.getDefaultResourceExclusions());
	}

	public static void setExcludedResourcePatterns(Set<String> patterns) {
		StringBuilder store = new StringBuilder();
		for (String string : patterns) {
			store.append(string);
			store.append(PREF_STORE_DELIM);
		}
		ResourcesUiBridgePlugin.getDefault().getPreferenceStore().setValue(PREF_RESOURCES_IGNORED, store.toString());
	}

	public static Set<String> getExcludedResourcePatterns() {
		Set<String> ignored = new HashSet<String>();
		String read = ResourcesUiBridgePlugin.getDefault().getPreferenceStore().getString(PREF_RESOURCES_IGNORED);
		if (read != null) {
			StringTokenizer st = new StringTokenizer(read, PREF_STORE_DELIM);
			while (st.hasMoreTokens()) {
				ignored.add(st.nextToken());
			}
		}
		return ignored;
	}
}
