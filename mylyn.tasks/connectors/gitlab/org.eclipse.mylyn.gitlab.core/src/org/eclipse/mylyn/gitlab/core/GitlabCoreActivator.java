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
 *******************************************************************************/

package org.eclipse.mylyn.gitlab.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public final class GitlabCoreActivator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.mylyn.gitlab.core"; //$NON-NLS-1$

    public static final String CONNECTOR_KIND = "org.eclipse.mylyn.gitlab";
    public static final String GROUPS = "gitlab.groups";
    public static final String PROJECTS = "gitlab.projects";
    public static final String AVANTAR = "gitlab.avantar";
    public static final String SHOW_ACTIVITY_ICONS = "gitlab.show.activity.icons";
    public static final String USE_PERSONAL_ACCESS_TOKEN = "gitlab.use.personal.access.token";
    public static final String PERSONAL_ACCESS_TOKEN = "gitlab.personal.access.token";
    public static final String API_VERSION = "/api/v4";
    public static final String ATTRIBUTE_TYPE_ACTIVITY = "activity"; //$NON-NLS-1$
    public static final String ATTRIBUTE_TYPE_ACTIVITY_STYLE = "activity.style"; //$NON-NLS-1$
    public static final String ID_PART_ACTIVITY = "org.eclipse.mylyn.tasks.ui.editors.parts.activity"; //$NON-NLS-1$
    public static final String PREFIX_ACTIVITY = "task.gitlab.activity-"; //$NON-NLS-1$
    public static final String GITLAB_ACTIVITY_TYPE = "org.eclipse.mylyn.gitlab.activity.type";

    public static enum ActivityType {
	UNKNOWN, PERSON, PENCIL, UNLOCK, LOCK, CLOSED, REOPEN, LABEL, CALENDAR, DESIGN;
    };

    // The shared instance
    private static GitlabCoreActivator plugin;

    /**
     * The constructor
     */
    public GitlabCoreActivator() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
	plugin = null;
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
