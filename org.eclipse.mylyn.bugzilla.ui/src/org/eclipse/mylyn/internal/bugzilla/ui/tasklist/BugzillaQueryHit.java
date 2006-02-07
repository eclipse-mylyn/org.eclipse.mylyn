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
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.internal.tasklist.IQueryHit;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 * @author Mik Kersten (hardening of prototype)
 */
public class BugzillaQueryHit implements IQueryHit {

	private String repositoryUrl;

	private String description;

	private String priority;

	private int id;

	private BugzillaTask task;

	private String status;

	public BugzillaQueryHit(String description, String priority, String repositoryUrl, int id, BugzillaTask task,
			String status) {
		this.description = description;
		this.priority = priority;
		this.repositoryUrl = repositoryUrl;
		this.id = id;
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

	public Image getIcon() {
		if (task != null) {
			return task.getIcon();
		} else {
			return BugzillaImages.getImage(BugzillaImages.BUGZILLA_HIT_INCOMMING);
		}
	}

	public Image getStatusIcon() {
		if (task != null) {
			return task.getStatusIcon();
		} else {
			return TaskListImages.getImage(TaskListImages.TASK_INACTIVE);
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

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public String getHandleIdentifier() {
		return TaskRepositoryManager.getHandle(repositoryUrl, id);
		// return "Bugzilla" + "-" + getId();
	}

	// public String getServerName() {
	// // TODO need the right server name - get from the handle
	// return "Bugzilla";
	// }

	public int getId() {
		return id;
	}

	public String getBugUrl() {
		return BugzillaRepositoryUtil.getBugUrlWithoutLogin(repositoryUrl, id);
	}

	public boolean isLocal() {
		return false;
	}

	public AbstractRepositoryTask getOrCreateCorrespondingTask() {
		if (task == null) {
			task = new BugzillaTask(this, true);
			AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(BugzillaPlugin.REPOSITORY_KIND);
			client.addTaskToArchive(task);
//			BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskArchive(task);
		}
		return task;
	}

	public boolean isActivatable() {
		return true;
	}

	public boolean isDragAndDropEnabled() {
		return true;
	}

	public Color getForeground() {
		if ((task != null && task.isCompleted()) || isCompleted()) {
			return TaskListImages.GRAY_LIGHT;
		} else {
			return null;
		}
	}

	public Font getFont() {
		if (task != null) {
			return task.getFont();
		}
		return null;
	}

	public boolean isCompleted() {
		// if (status != null) {
		// return BugReport.isResolvedStatus(status);
		// }
		// TODO: move to BugReport?
		if (status != null
				&& (status.startsWith("RESO") || status.startsWith("CLO") || status.startsWith("VERI") || status
						.startsWith("FIXED"))) {
			return true;
		}
		return false;
	}

	public String getToolTipText() {
		if (task != null) {
			return task.getToolTipText();
		} else {
			return getDescription();
		}
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

	public String getRepositoryUrl() {
		return repositoryUrl;
	}
}
