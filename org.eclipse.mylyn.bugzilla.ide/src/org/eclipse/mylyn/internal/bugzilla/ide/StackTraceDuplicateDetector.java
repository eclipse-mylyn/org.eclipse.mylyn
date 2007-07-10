/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.ide;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.ui.TaskFactory;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractNewRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.search.SearchHitCollector;

/**
 * @author Meghan Allen
 */
public class StackTraceDuplicateDetector extends AbstractDuplicateDetector {
	
	private static final int DESCRIPTION_MAX_CHARS = 6000;

	private static final String NO_STACK_MESSAGE = "Unable to locate a stack trace in the description text.";

	@Override
	public SearchHitCollector getSearchHitCollector(TaskRepository repository, RepositoryTaskData taskData) {
		String queryUrl = "";
		String searchString = AbstractNewRepositoryTaskEditor.getStackTraceFromDescription(taskData.getDescription());
		if (searchString != null && searchString.length() > DESCRIPTION_MAX_CHARS) {
			searchString = searchString.substring(0, DESCRIPTION_MAX_CHARS);
		}

		if (searchString == null) {
			MessageDialog.openWarning(null, "No Stack Trace Found", NO_STACK_MESSAGE);
			return null;
		}

		try {
			queryUrl = repository.getUrl() + "/buglist.cgi?long_desc_type=allwordssubstr&long_desc="
					+ URLEncoder.encode(searchString, repository.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			StatusHandler.log(e, "Error during duplicate detection");
			return null;
		}

		queryUrl += "&product=" + taskData.getProduct();

		BugzillaRepositoryQuery bugzillaQuery = new BugzillaRepositoryQuery(repository.getUrl(), queryUrl, "search");

		SearchHitCollector collector = new SearchHitCollector(TasksUiPlugin.getTaskListManager().getTaskList(),
				repository, bugzillaQuery, new TaskFactory(repository, false, false));
		return collector;
	}

}
