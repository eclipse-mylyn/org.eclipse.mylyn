/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * This class is responsible for creating, storing and retrieving the values for the default context in the preference
 * store. It is registered as an initializer class for the 'org.eclipse.core.runtime.preferences' extension point.
 * 
 * @author Fabio (bug 178931)
 * @author Mik Kersten
 */
public class ResourcesUiPreferenceInitializer extends AbstractPreferenceInitializer {

	public static final String PREF_DEFAULT_SCOPE = "org.eclipse.mylyn.ide.resources"; //$NON-NLS-1$

	@Deprecated
	private static final String PREF_STORE_DELIM = ", "; //$NON-NLS-1$

	@Deprecated
	private static final String PREF_RESOURCES_IGNORED = PREF_DEFAULT_SCOPE + ".ignored.pattern"; //$NON-NLS-1$

	public static final String PREF_RESOURCE_MONITOR_ENABLED = PREF_DEFAULT_SCOPE + ".resource.monitor.enabled"; //$NON-NLS-1$

	private static final String PREF_RESOURCES_IGNORED_ANT = PREF_DEFAULT_SCOPE + ".ignored.pattern.ant"; //$NON-NLS-1$

	public static final String PREF_MODIFIED_DATE_EXCLUSIONS = PREF_DEFAULT_SCOPE + ".date.modified.exclusion"; //$NON-NLS-1$

	private static final String KEY_RESOURCE_EXCLUSIONS = "resourceExclusions"; //$NON-NLS-1$

	private static final String KEY_EXCLUSION = "exclusion"; //$NON-NLS-1$

	private static final String KEY_ANT_PATTERN = "antPattern"; //$NON-NLS-1$

	public static Set<String> forcedExclusionPatterns = new HashSet<String>();

	public static Set<String> cachedExclusionPatterns = null;

	@Override
	public void initializeDefaultPreferences() {
		// most defaults come from extension points
		Set<String> defaultPatterns = new HashSet<String>();

		defaultPatterns.addAll(ResourcePatternExclusionStrategy.convertToAntPattern(".*")); //$NON-NLS-1$

		ResourcesUiBridgePlugin.getDefault().getPreferenceStore().setDefault(PREF_RESOURCE_MONITOR_ENABLED, true);
		ResourcesUiBridgePlugin.getDefault()
				.getPreferenceStore()
				.setDefault(PREF_RESOURCES_IGNORED_ANT, createResourceExclusionMemento(defaultPatterns));
		ResourcesUiBridgePlugin.getDefault().getPreferenceStore().setDefault(PREF_MODIFIED_DATE_EXCLUSIONS, true);
	}

	/**
	 * Restores the default values for the patterns to ignore.
	 */
	public static void restoreDefaultExcludedResourcePatterns() {
		setExcludedResourcePatterns(ResourcesUiExtensionPointReader.getDefaultResourceExclusions());
	}

	public static synchronized void setExcludedResourcePatterns(Set<String> patterns) {
		cachedExclusionPatterns = new HashSet<String>(patterns);

		String memento = createResourceExclusionMemento(patterns);
		if (memento != null) {
			ResourcesUiBridgePlugin.getDefault().getPreferenceStore().setValue(PREF_RESOURCES_IGNORED_ANT, memento);
		}
	}

	private static String createResourceExclusionMemento(Set<String> patterns) {
		XMLMemento rootMemento = XMLMemento.createWriteRoot(KEY_RESOURCE_EXCLUSIONS);
		for (String string : patterns) {
			IMemento memento = rootMemento.createChild(KEY_EXCLUSION);
			memento.putString(KEY_ANT_PATTERN, string);
		}
		StringWriter writer = new StringWriter();
		String memento = null;
		try {
			rootMemento.save(writer);
			memento = writer.getBuffer().toString();

		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ResourcesUiBridgePlugin.ID_PLUGIN,
					"Could not store resource exclusions", e)); //$NON-NLS-1$
		}
		return memento;
	}

	private static Set<String> getResourceExclusionsFromMemento(String mementoString) {
		Set<String> exclusions = new HashSet<String>();
		try {
			XMLMemento rootMemento = XMLMemento.createReadRoot(new StringReader(mementoString));

			for (IMemento memento : rootMemento.getChildren(KEY_EXCLUSION)) {
				String pattern = memento.getString(KEY_ANT_PATTERN);
				if (pattern != null) {
					exclusions.add(pattern);
				}
			}
		} catch (WorkbenchException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ResourcesUiBridgePlugin.ID_PLUGIN,
					"Could not load resource exclusions", e)); //$NON-NLS-1$
		}
		return exclusions;

	}

	public static synchronized Set<String> getExcludedResourcePatterns() {

		if (cachedExclusionPatterns != null) {
			return new HashSet<String>(cachedExclusionPatterns);
		}

		Set<String> exclusions = new HashSet<String>();
		if (ResourcesUiBridgePlugin.getDefault().getPreferenceStore().contains(PREF_RESOURCES_IGNORED_ANT)
				&& !ResourcesUiBridgePlugin.getDefault().getPreferenceStore().isDefault(PREF_RESOURCES_IGNORED_ANT)) {
			// we are using the new ant patterns
			exclusions = getResourceExclusionsFromMemento(ResourcesUiBridgePlugin.getDefault()
					.getPreferenceStore()
					.getString(PREF_RESOURCES_IGNORED_ANT));
		} else if (ResourcesUiBridgePlugin.getDefault().getPreferenceStore().contains(PREF_RESOURCES_IGNORED)) {
			//we are using the old patterns
			String read = ResourcesUiBridgePlugin.getDefault().getPreferenceStore().getString(PREF_RESOURCES_IGNORED);
			if (read != null) {
				StringTokenizer st = new StringTokenizer(read, PREF_STORE_DELIM);
				while (st.hasMoreTokens()) {
					exclusions.addAll(ResourcePatternExclusionStrategy.convertToAntPattern(st.nextToken()));
				}
			}

			setExcludedResourcePatterns(exclusions);
		} else {
			// make sure that we load the default preferences if it is a clean workspace
			exclusions = getResourceExclusionsFromMemento(ResourcesUiBridgePlugin.getDefault()
					.getPreferenceStore()
					.getString(PREF_RESOURCES_IGNORED_ANT));
		}

		cachedExclusionPatterns = new HashSet<String>(exclusions);
		return cachedExclusionPatterns;
	}

	public static Set<String> getForcedExcludedResourcePatterns() {
		return forcedExclusionPatterns;
	}

	/**
	 * TODO: move and consider for API
	 */
	public static void addForcedExclusionPattern(String pattern) {
		forcedExclusionPatterns.add(pattern);
	}

	public static void removeForcedExclusionPattern(String pattern) {
		forcedExclusionPatterns.remove(pattern);
	}
}
