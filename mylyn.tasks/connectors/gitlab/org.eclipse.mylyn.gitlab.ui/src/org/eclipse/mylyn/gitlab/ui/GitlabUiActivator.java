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

package org.eclipse.mylyn.gitlab.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class GitlabUiActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.mylyn.gitlab.ui"; //$NON-NLS-1$

    // The shared instance
    private static GitlabUiActivator plugin;

    public static final String GITLAB_PICTURE_FILE = "icons/obj20/gitlab.png"; //$NON-NLS-1$
    public static final String GITLAB_PENCIL_FILE = "icons/obj12/pencil.png"; //$NON-NLS-1$
    public static final String GITLAB_LOCK_FILE = "icons/obj12/lock.png"; //$NON-NLS-1$
    public static final String GITLAB_UNLOCK_FILE = "icons/obj12/lock-open.png"; //$NON-NLS-1$
    public static final String GITLAB_CALENDAR_FILE = "icons/obj12/calendar.png"; //$NON-NLS-1$
    public static final String GITLAB_PERSON_FILE = "icons/obj12/person.png"; //$NON-NLS-1$
    public static final String GITLAB_CLOSED_FILE = "icons/obj12/closed.png"; //$NON-NLS-1$
    public static final String GITLAB_REOPEN_FILE = "icons/obj12/reopen.png"; //$NON-NLS-1$
    public static final String GITLAB_LABEL_FILE = "icons/obj12/label.png"; //$NON-NLS-1$

    /**
     * The constructor
     */
    public GitlabUiActivator() {
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
    public static GitlabUiActivator getDefault() {
	return plugin;
    }

    /**
     * @see AbstractUIPlugin#initializeImageRegistry(ImageRegistry)
     */
    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
	reg.put(GITLAB_PICTURE_FILE, getImageDescriptor(GITLAB_PICTURE_FILE));
	reg.put(GITLAB_PENCIL_FILE, getImageDescriptor(GITLAB_PENCIL_FILE));
	reg.put(GITLAB_UNLOCK_FILE, getImageDescriptor(GITLAB_UNLOCK_FILE));
	reg.put(GITLAB_LOCK_FILE, getImageDescriptor(GITLAB_LOCK_FILE));
	reg.put(GITLAB_CALENDAR_FILE, getImageDescriptor(GITLAB_CALENDAR_FILE));
	reg.put(GITLAB_PERSON_FILE, getImageDescriptor(GITLAB_PERSON_FILE));
	reg.put(GITLAB_REOPEN_FILE, getImageDescriptor(GITLAB_REOPEN_FILE));
	reg.put(GITLAB_CLOSED_FILE, getImageDescriptor(GITLAB_CLOSED_FILE));
	reg.put(GITLAB_LABEL_FILE, getImageDescriptor(GITLAB_LABEL_FILE));
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative
     * path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
	return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

}
