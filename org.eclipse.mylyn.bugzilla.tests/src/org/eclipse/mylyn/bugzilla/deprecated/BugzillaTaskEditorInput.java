/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eric Booth - contribution
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.deprecated;

import org.eclipse.mylyn.internal.tasks.ui.deprecated.RepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Eric Booth
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaTaskEditorInput extends RepositoryTaskEditorInput {

	private String bugTitle = "";

	private final BugzillaTask bugTask;

	public BugzillaTaskEditorInput(TaskRepository repository, BugzillaTask bugzillaTask, boolean offline) {
		super(repository, bugzillaTask.getTaskId(), bugzillaTask.getUrl());
		this.bugTask = bugzillaTask;
//		updateOptions(getTaskData());
//		updateOptions(getOldTaskData());
	}

	protected void setBugTitle(String str) {
		// 03-20-03 Allows editor to store title (once it is known)
		bugTitle = str;
	}

	@Override
	public boolean exists() {
		return true;
	}

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

//	private void updateOptions(TaskData taskData) {
//		try {
//			if (taskData != null) {
//				RepositoryConfiguration config = BugzillaCorePlugin.getRepositoryConfiguration(repository, false,
//						new NullProgressMonitor());
//				config.updateAttributeOptions(taskData);
//			}
//		} catch (Exception e) {
//			// ignore
//		}
//	}
}
