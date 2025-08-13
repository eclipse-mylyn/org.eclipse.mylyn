/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
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
		preferencesStore.setDefault(BuildsUiInternal.PREF_REFRESH_ON_FOCUS, true);
	}

}
