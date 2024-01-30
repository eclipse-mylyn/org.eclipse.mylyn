/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.activity.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.mylyn.commons.activity.ui.spi.AbstractUserActivityMonitor;
import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.internal.commons.activity.ui.ActivityUiPlugin;
import org.eclipse.mylyn.internal.commons.activity.ui.DefaultUserActivityMonitor;
import org.eclipse.mylyn.internal.commons.activity.ui.IActivityUiConstants;
import org.eclipse.mylyn.internal.commons.activity.ui.UserActivityManager;

/**
 * Entry point for registering listener to monitor user activity.
 * 
 * @author Steffen Pingel
 * @since 3.7
 */
public class ActivityUi {

	private final static IPropertyChangeListener PROPERTY_LISTENER = event -> {
		if (event.getProperty().equals(IActivityUiConstants.ACTIVITY_TIMEOUT)
				|| event.getProperty().equals(IActivityUiConstants.ACTIVITY_TIMEOUT_ENABLED)
				|| event.getProperty().equals(IActivityUiConstants.ACTIVITY_TRACKING_ENABLED)) {
			updateUserActivityMonitor();
		}
	};

	private static UserActivityManager userActivityManager;

	/**
	 * Registers a user activity listener.
	 * 
	 * @since 3.7
	 * @param listener
	 *            listener to add
	 */
	public static void addUserAttentionListener(UserActivityListener listener) {
		getUserActivityMonitor().addAttentionListener(listener);
	}

	/**
	 * Unregisters a user activity listener.
	 * 
	 * @since 3.7
	 * @param listener
	 *            listener to remote
	 */
	public static void removeUserAttentionListener(UserActivityListener listener) {
		getUserActivityMonitor().removeAttentionListener(listener);
	}

	private static IPreferenceStore getPreferenceStore() {
		return ActivityUiPlugin.getDefault().getPreferenceStore();
	}

	synchronized static UserActivityManager getUserActivityMonitor() {
		if (userActivityManager == null) {
			// create default activity monitor
			List<AbstractUserActivityMonitor> items = new ArrayList<>();
			items.add(new DefaultUserActivityMonitor());

			// read contributed activity monitors
			ExtensionPointReader<AbstractUserActivityMonitor> reader = new ExtensionPointReader<>(
					IActivityUiConstants.ID_PLUGIN, "userActivityMonitors", "osActivityTimer", //$NON-NLS-1$ //$NON-NLS-2$
					AbstractUserActivityMonitor.class);
			reader.read();

			// rank by highest priority first
			items.addAll(reader.getItems());
			Collections.sort(items, (o1, o2) -> o2.getPriority() - o1.getPriority());

			getPreferenceStore().addPropertyChangeListener(PROPERTY_LISTENER);

			userActivityManager = new UserActivityManager(items);
			updateUserActivityMonitor();

			// TODO only start if enabled
			userActivityManager.start();
		}
		return userActivityManager;
	}

	synchronized static void updateUserActivityMonitor() {
		if (userActivityManager == null) {
			return;
		}
		if (getPreferenceStore().getBoolean(IActivityUiConstants.ACTIVITY_TIMEOUT_ENABLED)) {
			userActivityManager
					.setInactivityTimeout(getPreferenceStore().getInt(IActivityUiConstants.ACTIVITY_TIMEOUT));
		} else {
			userActivityManager.setInactivityTimeout(0);
		}
		// TODO stop if disabled
	}

	public static boolean isActivityTrackingEnabled() {
		return getPreferenceStore().getBoolean(IActivityUiConstants.ACTIVITY_TRACKING_ENABLED);
	}

	public synchronized static boolean isTrackingOsTime() {
		if (userActivityManager == null) {
			return false;
		}
		return userActivityManager.isTrackingOsTime();
	}

}
