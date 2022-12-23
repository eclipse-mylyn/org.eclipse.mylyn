/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.ui.activities.ActivityManagerEvent;
import org.eclipse.ui.activities.IActivityManagerListener;

/**
 * @author Mik Kersten
 */
public class ActivityChangeMonitor implements IActivityManagerListener {

	private static final String ACTIVITIES_CHANGED = "activities changed"; //$NON-NLS-1$

	public void activityManagerChanged(ActivityManagerEvent activityManagerEvent) {
		if (activityManagerEvent.haveEnabledActivityIdsChanged()) {
			String source = activityManagerEvent.getActivityManager().toString();
			String delta = activityManagerEvent.getActivityManager().getEnabledActivityIds().toString();
			InteractionEvent interactionEvent = InteractionEvent.makePreference(source, ACTIVITIES_CHANGED + ": " //$NON-NLS-1$
					+ delta);
			MonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
		}
	}
}
