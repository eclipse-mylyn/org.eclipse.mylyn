/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Meghan Allen
 */
public class StackTraceDuplicateDetector extends AbstractDuplicateDetector {
	
	private static final int DESCRIPTION_MAX_CHARS = 6000;

	//private static final String NO_STACK_MESSAGE = "Unable to locate a stack trace in the description text.";

	@Override
	public AbstractRepositoryQuery getDuplicatesQuery(TaskRepository repository, RepositoryTaskData taskData) {
		String queryUrl = "";
		String searchString = AbstractDuplicateDetector.getStackTraceFromDescription(taskData.getDescription());
		if (searchString != null && searchString.length() > DESCRIPTION_MAX_CHARS) {
			searchString = searchString.substring(0, DESCRIPTION_MAX_CHARS);
		}

		if (searchString == null) {
			//MessageDialog.openWarning(null, "No Stack Trace Found", NO_STACK_MESSAGE);
			return null;
		}

		try {
			queryUrl = repository.getRepositoryUrl() + "/buglist.cgi?long_desc_type=allwordssubstr&long_desc="
					+ URLEncoder.encode(searchString, repository.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			StatusHandler.log(new Status(IStatus.WARNING, BugzillaCorePlugin.PLUGIN_ID, "Error during duplicate detection", e));
			return null;
		}

		queryUrl += "&product=" + taskData.getProduct();

		BugzillaRepositoryQuery bugzillaQuery = new BugzillaRepositoryQuery(repository.getRepositoryUrl(), queryUrl, "search");
		return bugzillaQuery;
	}

}
