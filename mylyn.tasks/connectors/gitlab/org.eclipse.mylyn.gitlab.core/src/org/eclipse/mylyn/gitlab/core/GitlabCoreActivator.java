/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.gitlab.core;

import java.util.Hashtable;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugOptionsListener;
import org.eclipse.osgi.service.debug.DebugTrace;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The activator class controls the plug-in life cycle
 */
public final class GitlabCoreActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.mylyn.gitlab.core"; //$NON-NLS-1$

	public static final String CONNECTOR_KIND = "org.eclipse.mylyn.gitlab"; //$NON-NLS-1$

	public static final String GROUPS = "gitlab.groups"; //$NON-NLS-1$

	public static final String PROJECTS = "gitlab.projects"; //$NON-NLS-1$

	public static final String AVANTAR = "gitlab.avantar"; //$NON-NLS-1$

	public static final String SHOW_ACTIVITY_ICONS = "gitlab.show.activity.icons"; //$NON-NLS-1$

	public static final String USE_PERSONAL_ACCESS_TOKEN = "gitlab.use.personal.access.token"; //$NON-NLS-1$

	public static final String PERSONAL_ACCESS_TOKEN = "gitlab.personal.access.token"; //$NON-NLS-1$

	public static final String API_VERSION = "/api/v4"; //$NON-NLS-1$

	public static final String ATTRIBUTE_TYPE_ACTIVITY = "activity"; //$NON-NLS-1$

	public static final String ATTRIBUTE_TYPE_ACTIVITY_STYLE = "activity.style"; //$NON-NLS-1$

	public static final String ID_PART_ACTIVITY = "org.eclipse.mylyn.tasks.ui.editors.parts.activity"; //$NON-NLS-1$

	public static final String PREFIX_ACTIVITY = "task.gitlab.activity-"; //$NON-NLS-1$

	public static final String GITLAB_ACTIVITY_TYPE = "org.eclipse.mylyn.gitlab.activity.type"; //$NON-NLS-1$

	public enum ActivityType {
		UNKNOWN, PERSON, PENCIL, UNLOCK, LOCK, CLOSED, REOPEN, LABEL, CALENDAR, DESIGN;
	}

	// The shared instance
	private static GitlabCoreActivator plugin;

	private ServiceRegistration<DebugOptionsListener> DEBUG_REGISTRATION;

	/**
	 * The constructor
	 */
	public GitlabCoreActivator() {
	}

	public static final String DEBUG = "/debug"; //$NON-NLS-1$

	public static final String REPOSITORY_CONNECTOR = "/debug/repository/connector"; //$NON-NLS-1$

	public static final String REST_CLIENT = "/debug/rest/client"; //$NON-NLS-1$

	public static final String REST_CLIENT_TRACE = "/debug/rest/client/trace"; //$NON-NLS-1$

	public static boolean DEBUG_REPOSITORY_CONNECTOR = false;

	public static boolean DEBUG_REST_CLIENT = false;

	public static boolean DEBUG_REST_CLIENT_TRACE = false;

	public static DebugTrace DEBUG_TRACE;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		Hashtable<String, String> properties = new Hashtable<>(2);
		properties.put(DebugOptions.LISTENER_SYMBOLICNAME, PLUGIN_ID);
		DEBUG_REGISTRATION = context.registerService(DebugOptionsListener.class, options -> {
			boolean debugCore = options.getBooleanOption(PLUGIN_ID + DEBUG, false);
			DEBUG_TRACE = options.newDebugTrace(PLUGIN_ID);
			DEBUG_REPOSITORY_CONNECTOR = debugCore
					&& options.getBooleanOption(PLUGIN_ID + REPOSITORY_CONNECTOR, false);
			DEBUG_REST_CLIENT = debugCore && options.getBooleanOption(PLUGIN_ID + REST_CLIENT, false);
			DEBUG_REST_CLIENT_TRACE = debugCore && options.getBooleanOption(PLUGIN_ID + REST_CLIENT_TRACE, false);
		}, properties);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		if (DEBUG_REGISTRATION != null) {
			DEBUG_REGISTRATION.unregister();
			DEBUG_REGISTRATION = null;
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static GitlabCoreActivator getDefault() {
		return plugin;
	}

}
