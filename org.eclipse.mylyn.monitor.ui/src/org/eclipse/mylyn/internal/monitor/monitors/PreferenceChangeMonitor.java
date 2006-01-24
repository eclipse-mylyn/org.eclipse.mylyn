/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.monitor.monitors;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.monitor.MylarMonitorPlugin;

/**
 * @author Mik Kersten
 */
public class PreferenceChangeMonitor implements IPropertyChangeListener {

	public void propertyChange(PropertyChangeEvent event) {
		String newValue = obfuscateValueIfContainsPath(event.getNewValue().toString());
		InteractionEvent interactionEvent = InteractionEvent.makePreference(event.getProperty(), newValue);
		if (MylarPlugin.getDefault() != null) {
			MylarPlugin.getDefault().notifyInteractionObserved(interactionEvent);
		}
	}

	private String obfuscateValueIfContainsPath(String preferenceValue) {
		if (preferenceValue.indexOf(java.io.File.separator) != -1 || preferenceValue.indexOf('/') != -1) {
			return MylarMonitorPlugin.OBFUSCATED_LABEL;
		} else {
			return preferenceValue;
		}
	}
}
