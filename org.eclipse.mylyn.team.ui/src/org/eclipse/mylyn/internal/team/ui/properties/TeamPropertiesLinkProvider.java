/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Retrieves team settings from project properties.
 * 
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public class TeamPropertiesLinkProvider {

	private static final String PROJECT_COMMIT_COMMENT_TEMPLATE = "commit.comment.template";

	public TeamPropertiesLinkProvider() {
	}

	public boolean canAccessProperties(IResource resource) {
		IProject project = resource.getProject();
		return project != null && project.isAccessible();
	}

	public String getCommitCommentTemplate(IResource resource) {
		if (!canAccessProperties(resource)) {
			return null;
		}

		IScopeContext projectScope = new ProjectScope(resource.getProject());
		IEclipsePreferences projectNode = projectScope.getNode(FocusedTeamUiPlugin.PLUGIN_ID);
		if (projectNode != null) {
			return projectNode.get(PROJECT_COMMIT_COMMENT_TEMPLATE, null);
		}
		return null;
	}

	public boolean setCommitCommentTemplate(IResource resource, String commitCommentTemplate) {
		if (!canAccessProperties(resource)) {
			return false;
		}

		IScopeContext projectScope = new ProjectScope(resource.getProject());
		IEclipsePreferences projectNode = projectScope.getNode(FocusedTeamUiPlugin.PLUGIN_ID);
		if (projectNode != null) {
			if (commitCommentTemplate != null) {
				projectNode.put(PROJECT_COMMIT_COMMENT_TEMPLATE, commitCommentTemplate);
			} else {
				projectNode.remove(PROJECT_COMMIT_COMMENT_TEMPLATE);
			}
			try {
				projectNode.flush();
				return true;
			} catch (BackingStoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, FocusedTeamUiPlugin.PLUGIN_ID,
						"Failed to save commit comment template for project", e));
			}
		}
		return false;
	}

}