/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.ui;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class PreferenceChangeMonitor implements IPropertyChangeListener {

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String newValue = obfuscateValueIfContainsPath(event.getNewValue().toString());
		InteractionEvent interactionEvent = InteractionEvent.makePreference(event.getProperty(), newValue);
		MonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}

	private String obfuscateValueIfContainsPath(String preferenceValue) {
		if (preferenceValue.indexOf(java.io.File.separator) != -1 || preferenceValue.indexOf('/') != -1) {
			return MonitorUiPlugin.OBFUSCATED_LABEL;
		} else {
			return preferenceValue;
		}
	}
}
