/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	public static BugzillaClient createClient(TaskRepository taskRepository) throws MalformedURLException {
		String language = taskRepository.getProperty(IBugzillaConstants.BUGZILLA_LANGUAGE_SETTING);
		if (language == null || language.equals("")) { //$NON-NLS-1$
			language = IBugzillaConstants.DEFAULT_LANG;
		}
		BugzillaLanguageSettings languageSettings = BugzillaRepositoryConnector.getLanguageSetting(language);
		AbstractWebLocation location = taskRepositoryLocationFactory.createWebLocation(taskRepository);
		BugzillaClient client = new BugzillaClient(location, taskRepository.getCharacterEncoding(),
				taskRepository.getProperties(), languageSettings);
		return client;

	}
}
