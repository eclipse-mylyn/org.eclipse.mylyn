/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Steffen Pingel
 */
public class BuildsPreferencesInitializer extends AbstractPreferenceInitializer {

	public BuildsPreferencesInitializer() {
		// ignore
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferencesStore = BuildsUiPlugin.getDefault().getPreferenceStore();
		preferencesStore.setDefault(BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED, false);
		preferencesStore.setDefault(BuildsUiInternal.PREF_AUTO_REFRESH_INTERVAL,
				BuildsUiInternal.DEFAULT_REFRESH_INTERVAL);
	}

}
