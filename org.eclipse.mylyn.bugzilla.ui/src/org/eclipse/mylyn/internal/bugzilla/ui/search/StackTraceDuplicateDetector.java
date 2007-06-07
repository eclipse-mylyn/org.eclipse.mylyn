/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractDuplicateDetector;
import org.eclipse.mylar.tasks.ui.TaskFactory;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.editors.AbstractNewRepositoryTaskEditor;
import org.eclipse.mylar.tasks.ui.search.SearchHitCollector;

/**
 * @author Meghan Allen
 */
public class StackTraceDuplicateDetector extends AbstractDuplicateDetector {

	private static final String NO_STACK_MESSAGE = "Unable to locate a stack trace in the description text.";
	
	@Override
	public SearchHitCollector getSearchHitCollector(TaskRepository repository, RepositoryTaskData taskData) {
		String queryUrl = "";
		String searchString = AbstractNewRepositoryTaskEditor.getStackTraceFromDescription(taskData.getDescription());
		
		if (searchString == null) {
			MessageDialog.openWarning(null, "No Stack Trace Found", NO_STACK_MESSAGE);
			return null;
		}
		
		try {
			queryUrl = repository.getUrl() + "/buglist.cgi?long_desc_type=allwordssubstr&long_desc="
					+ URLEncoder.encode(searchString, repository.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			MylarStatusHandler.log(e, "Error during duplicate detection");
			return null;
		}

		queryUrl += "&product=" + taskData.getProduct();

		BugzillaRepositoryQuery bugzillaQuery = new BugzillaRepositoryQuery(repository.getUrl(), queryUrl, "search");

		SearchHitCollector collector = new SearchHitCollector(TasksUiPlugin.getTaskListManager().getTaskList(),
				repository, bugzillaQuery, new TaskFactory(repository));
		return collector;
	}

}
