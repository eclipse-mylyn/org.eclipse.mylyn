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

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;


import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.ITaskListElement;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.swt.graphics.Font;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class BugzillaRepositoryQuery extends AbstractRepositoryQuery {

	private boolean customQuery = false;

	public BugzillaRepositoryQuery(String repositoryUrl, String queryUrl, String label, String maxHits) {
		this.description = label;
		this.queryUrl = queryUrl;
		this.repositoryUrl = repositoryUrl;
		try {
			this.maxHits = Integer.parseInt(maxHits);
		} catch (Exception e) {
			this.maxHits = -1;
		}
	}

	public String getRepositoryKind() {
		return BugzillaPlugin.REPOSITORY_KIND;
	}
	
//	public boolean isActivatable() {
//		return false;
//	}

	public boolean isDragAndDropEnabled() {
		return false;
	}

	public Font getFont() {
		for (ITaskListElement child : getHits()) {
			if (child instanceof BugzillaQueryHit) {
				BugzillaQueryHit hit = (BugzillaQueryHit) child;
				BugzillaTask task = hit.getCorrespondingTask();
				if (task != null && task.isActive()) {
					return TaskListImages.BOLD;
				}
			}
		}
		return null;
	}

//	public Image getStatusIcon() {
//		return null;
//	}

	public boolean isCustomQuery() {
		return customQuery;
	}

	public void setCustomQuery(boolean customQuery) {
		this.customQuery = customQuery;
	}
}
