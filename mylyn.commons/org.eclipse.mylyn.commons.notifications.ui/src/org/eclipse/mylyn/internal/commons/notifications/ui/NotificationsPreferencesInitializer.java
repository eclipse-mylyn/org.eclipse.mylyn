/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Steffen Pingel
 */
public class NotificationsPreferencesInitializer extends AbstractPreferenceInitializer {

	public NotificationsPreferencesInitializer() {
		// ignore
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferencesStore = NotificationsPlugin.getDefault().getPreferenceStore();
		preferencesStore.setDefault(NotificationsPlugin.PREF_NOTICATIONS_ENABLED, true);
	}

}
