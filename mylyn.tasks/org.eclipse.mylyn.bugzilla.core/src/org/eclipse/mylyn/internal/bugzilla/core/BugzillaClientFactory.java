/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.net.MalformedURLException;

import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;

/**
 * @author Steffen Pingel
 * @author Robert Elves (adaption for Bugzilla)
 */
public class BugzillaClientFactory {

	protected static TaskRepositoryLocationFactory taskRepositoryLocationFactory = new TaskRepositoryLocationFactory();

	public static BugzillaClient createClient(TaskRepository taskRepository, BugzillaRepositoryConnector connector)
			throws MalformedURLException {
		// to fix bug#349633
		// (BugzillaRepositoryConnector.getTaskData() fails when repository URL has trailing slash and no path)
		String repositoryURL = taskRepository.getRepositoryUrl();
		if (repositoryURL.endsWith("/")) { //$NON-NLS-1$
			StringBuilder sb = new StringBuilder(repositoryURL.trim());
			while (sb.length() > 0 && sb.charAt(sb.length() - 1) == '/') {
				sb.setLength(sb.length() - 1);
			}
			taskRepository.setRepositoryUrl(sb.toString());
		}
		AbstractWebLocation location = taskRepositoryLocationFactory.createWebLocation(taskRepository);

		BugzillaClient client = new BugzillaClient(location, taskRepository, connector);

		return client;

	}
}
