/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on 1-Feb-2005
 */
package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Eric Booth
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaTaskEditorInput extends RepositoryTaskEditorInput {

	private String bugTitle = "";

	private BugzillaTask bugTask;

	public BugzillaTaskEditorInput(TaskRepository repository, BugzillaTask bugzillaTask, boolean offline) {
		super(repository, bugzillaTask.getTaskId(), bugzillaTask.getUrl());
		this.bugTask = bugzillaTask;
		updateOptions(getTaskData());
		updateOptions(getOldTaskData());
	}

	protected void setBugTitle(String str) {
		// 03-20-03 Allows editor to store title (once it is known)
		bugTitle = str;
	}

	@Override
	public boolean exists() {
		return true;
	}

//	@Override
//	public ImageDescriptor getImageDescriptor() {
//		return null;
//	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return bugTitle;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * @return Returns the <code>BugzillaTask</code>
	 */
	public BugzillaTask getBugTask() {
		return bugTask;
	}

	// TODO: repository configuration update (remove at some point)
	private void updateOptions(RepositoryTaskData taskData) {
		try {
			if (taskData != null) {
				BugzillaRepositoryConnector bugzillaConnector = (BugzillaRepositoryConnector) TasksUiPlugin.getRepositoryManager()
						.getRepositoryConnector(BugzillaCorePlugin.REPOSITORY_KIND);
				((BugzillaTaskDataHandler)bugzillaConnector.getTaskDataHandler()).updateAttributeOptions(repository, taskData);
			}
		} catch (Exception e) {
			// ignore
		}
	}
}
