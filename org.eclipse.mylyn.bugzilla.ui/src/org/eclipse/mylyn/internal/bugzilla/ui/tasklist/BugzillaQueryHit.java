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

import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.tasklist.AbstractQueryHit;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.swt.graphics.Color;

/**
 * @author Ken Sueda
 * @author Mik Kersten (hardening of prototype)
 */
public class BugzillaQueryHit extends AbstractQueryHit {

	private BugzillaTask task;

	private String status;

	public BugzillaQueryHit(String description, String priority, String repositoryUrl, int id, BugzillaTask task, String status) {
		super(repositoryUrl, description, ""+id);
		super.priority = priority;
		this.task = task;
		this.status = status;
	}

	public BugzillaTask getCorrespondingTask() {
		return task;
	}

	public void setCorrespondingTask(AbstractRepositoryTask task) {
		if (task instanceof BugzillaTask) {
			this.task = (BugzillaTask)task;
		}
	}

	public String getPriority() {
		if (task != null) {
			return task.getPriority();
		} else {
			return priority;
		}
	}

	public String getDescription() {
		// return HtmlStreamTokenizer.unescape(description);
		if (task != null) {
			return task.getDescription();
		} else {
			return description;
		}
	}

	public String getBugUrl() {
		Integer idInt = new Integer(id);
		return BugzillaRepositoryUtil.getBugUrlWithoutLogin(repositoryUrl, idInt);
	}

	public boolean isLocal() {
		return false;
	}

	public AbstractRepositoryTask getOrCreateCorrespondingTask() {
		if (task == null) {
			task = new BugzillaTask(this, true);
//			AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(BugzillaPlugin.REPOSITORY_KIND);
//			client.addTaskToArchive(task);
			MylarTaskListPlugin.getTaskListManager().getTaskList().addTaskToArchive(task);
		}
		return task;
	}

	public boolean isDragAndDropEnabled() {
		return true;
	}

	public Color getForeground() {
		if ((task != null && task.isCompleted())) {
			return TaskListImages.GRAY_LIGHT;
		} else {
			return null;
		}
	}

	public boolean isCompleted() {
		if (status != null
				&& (status.startsWith("RESO") || status.startsWith("CLO") || status.startsWith("VERI") || status
						.startsWith("FIXED"))) {
			return true;
		}
		return false;
	}

	public void setDescription(String description) {
		// can't set the description to anything
	}

	public String getStringForSortingDescription() {
		return getId() + "";
	}

	public void setHandleIdentifier(String id) {
		// can't change the handle
	}

}
