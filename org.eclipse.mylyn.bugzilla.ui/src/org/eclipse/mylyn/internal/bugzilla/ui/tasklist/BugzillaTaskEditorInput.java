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
/*
 * Created on 1-Feb-2005
 */
package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaServerFacade;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylar.internal.tasks.ui.editors.ExistingBugEditorInput;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Eric Booth
 * @author Mik Kersten
 */
public class BugzillaTaskEditorInput extends ExistingBugEditorInput {

	private String bugTitle;

	private BugzillaTask bugTask;

	public BugzillaTaskEditorInput(TaskRepository repository, BugzillaTask bugTask, boolean offline)
			throws IOException, GeneralSecurityException {
		super(repository, bugTask.getTaskData(), AbstractRepositoryTask.getTaskId(bugTask.getHandleIdentifier()));
		this.bugTask = bugTask;
		migrateDescToReadOnly(bugTask);
		updateOptions(bugTask.getTaskData());
		id = AbstractRepositoryTask.getTaskId(bugTask.getHandleIdentifier());
		bugTitle = "";
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
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return bugTask.getDescription();
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

	// TODO: migration code 0.6.1 -> 0.6.2
	private void migrateDescToReadOnly(BugzillaTask task) {
		if (task != null && task.getTaskData() != null) {
			RepositoryTaskAttribute attrib = task.getTaskData().getDescriptionAttribute();
			if (attrib != null) {
				attrib.setReadOnly(true);
			}
		}
	}

	// TODO: repository configuration update (remove at some point)
	private void updateOptions(RepositoryTaskData taskData) {
		try {
			if (taskData != null) {
				BugzillaServerFacade.updateBugAttributeOptions(taskData.getRepositoryUrl(), TasksUiPlugin.getDefault()
						.getProxySettings(), repository.getUserName(), repository.getPassword(), taskData, repository
						.getCharacterEncoding());
			}
		} catch (Exception e) {
			// ignore
		}
	}
}
