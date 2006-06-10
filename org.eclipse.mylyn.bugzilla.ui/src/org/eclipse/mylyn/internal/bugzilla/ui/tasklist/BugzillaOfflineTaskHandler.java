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

import java.io.FileNotFoundException;
import java.net.Proxy;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaAttributeFactory;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.AbstractReportFactory.UnrecognizedBugzillaError;
import org.eclipse.mylar.internal.tasklist.AbstractAttributeFactory;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.IOfflineTaskHandler;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaOfflineTaskHandler implements IOfflineTaskHandler {

	private AbstractAttributeFactory attributeFactory = new BugzillaAttributeFactory();
	
	public RepositoryTaskData downloadTaskData(final AbstractRepositoryTask bugzillaTask) throws CoreException {
		TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
				BugzillaPlugin.REPOSITORY_KIND, bugzillaTask.getRepositoryUrl());
		Proxy proxySettings = MylarTaskListPlugin.getDefault().getProxySettings();
		try {
			return BugzillaRepositoryUtil.getBug(repository.getUrl(), repository.getUserName(), repository
					.getPassword(), proxySettings, repository.getCharacterEncoding(), AbstractRepositoryTask
					.getTaskIdAsInt(bugzillaTask.getHandleIdentifier()));
		} catch (final LoginException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, 0, "Report download failed. Ensure proper repository configuration of " + bugzillaTask.getRepositoryUrl() + " in "
					+ TaskRepositoriesView.NAME + ".", e ));
		} catch (final UnrecognizedBugzillaError e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, 0, "Report download failed. Unrecognized response from " + bugzillaTask.getRepositoryUrl() + ".", e ));
		} catch (final FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, 0, "Report download from " + bugzillaTask.getRepositoryUrl() + " failed. File not found: "+e.getMessage(), e ));
		} catch (final Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, 0, "Report download from " + bugzillaTask.getRepositoryUrl() + " failed, please see details.", e ));
		}
	}

	public AbstractAttributeFactory getAttributeFactory() {
		return attributeFactory;
	}
}
