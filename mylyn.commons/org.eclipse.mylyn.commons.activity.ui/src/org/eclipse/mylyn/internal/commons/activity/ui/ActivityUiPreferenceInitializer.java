/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.activity.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Steffen Pingel
 */
public class ActivityUiPreferenceInitializer extends AbstractPreferenceInitializer {

	private static final String ACTIVITY_PREFS_MIGRATED = "org.eclipse.mylyn.activity.ui.migrated"; //$NON-NLS-1$

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore prefs = ActivityUiPlugin.getDefault().getPreferenceStore();
		prefs.setDefault(IActivityUiConstants.ACTIVITY_TIMEOUT, IActivityUiConstants.DEFAULT_ACTIVITY_TIMEOUT);
		prefs.setDefault(IActivityUiConstants.ACTIVITY_TIMEOUT_ENABLED, true);
		prefs.setDefault(IActivityUiConstants.ACTIVITY_TRACKING_ENABLED, false);

		if (!prefs.getBoolean(ACTIVITY_PREFS_MIGRATED)) {
			prefs.setValue(ACTIVITY_PREFS_MIGRATED, true);

			// migrate values from preferences store in org.eclipse.mylyn.monitor.ui
			@SuppressWarnings("deprecation")
			ScopedPreferenceStore oldPrefs = new ScopedPreferenceStore(new InstanceScope(),
					"org.eclipse.mylyn.monitor.ui"); //$NON-NLS-1$
			int intValue = oldPrefs.getInt("org.eclipse.mylyn.monitor.ui.activity.timeout"); //$NON-NLS-1$
			if (intValue != 180000) {
				prefs.setValue(IActivityUiConstants.ACTIVITY_TIMEOUT, intValue);
			}

			boolean booleanValue = oldPrefs.getBoolean("org.eclipse.mylyn.monitor.ui.activity.timeout.enabled"); //$NON-NLS-1$
			if (booleanValue != true) {
				prefs.setValue(IActivityUiConstants.ACTIVITY_TIMEOUT_ENABLED, booleanValue);
			}

			booleanValue = oldPrefs.getBoolean("org.eclipse.mylyn.monitor.activity.tracking.enabled"); //$NON-NLS-1$
			if (booleanValue != false) {
				prefs.setValue(IActivityUiConstants.ACTIVITY_TRACKING_ENABLED, booleanValue);
			}
		}
	}

}
