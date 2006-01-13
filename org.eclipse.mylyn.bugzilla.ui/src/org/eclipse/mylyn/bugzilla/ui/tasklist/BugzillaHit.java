/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.bugzilla.ui.tasklist;

import org.eclipse.mylar.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.repositories.TaskRepositoryManager;
import org.eclipse.mylar.tasklist.ui.TaskListImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 * @author Mik Kersten (hardening of prototype)
 */
public class BugzillaHit implements IQueryHit {

	private String repositoryUrl;
	
	private String description;

	private String priority;

	private int id;

	private BugzillaTask task;

	private String status;

	public BugzillaHit(String description, String priority, String repositoryUrl, int id, BugzillaTask task, String status) {
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

	public void setCorrespondingTask(ITask task) {
		if (task instanceof BugzillaTask)
			setAssociatedTask((BugzillaTask) task);
	}

	private void setAssociatedTask(BugzillaTask task) {
		this.task = task;
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

	public String getDescription(boolean label) {
		if (label) {
			if (task != null) {
				return task.getDescription(label);
			} else {
				return HtmlStreamTokenizer.unescape(description);
			}
		} else {
			if (task != null) {
				return task.getDescription(label);
			} else {
				return description;
			}
		}
	}

	public String getHandleIdentifier() {
		return TaskRepositoryManager.getHandle(repositoryUrl, id);
//		return "Bugzilla" + "-" + getId();
	}

//	public String getServerName() {
//		// TODO need the right server name - get from the handle
//		return "Bugzilla";
//	}

	public int getId() {
		return id;
	}

	public String getBugUrl() {
		return BugzillaRepositoryUtil.getBugUrlWithoutLogin(repositoryUrl, id);
	}

	public boolean isLocal() {
		return false;
	}

	public ITask getOrCreateCorrespondingTask() {
		if (task == null) {
			task = new BugzillaTask(this, true);
			BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry(task);
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
//		if (status != null) {
//			return BugReport.isResolvedStatus(status);
//		} 
		// TODO: move to BugReport?
		if (status != null && (status.startsWith("RESO") || status.startsWith("CLO") 
				|| status.startsWith("VERI") || status.startsWith("FIXED"))) {
			return true;
		}
		return false;
	}

	public String getToolTipText() {
		if (task != null) {
			return task.getToolTipText();
		} else {
			return getDescription(true);
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
