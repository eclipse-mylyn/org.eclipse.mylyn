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

package org.eclipse.mylyn.internal.commons.ui.notifications;

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
