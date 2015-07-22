/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.bugzilla.rest.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.QueryPageDetails;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.SimpleURLQueryPageSchema;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class BugzillaRestUiUtil {

	protected static BugzillaRestSearchQueryPage createSimpleURLQueryPage(TaskData taskData,
			BugzillaRestConnector connectorREST, TaskRepository repository) {
		try {
			SimpleURLQueryPageSchema.getInstance().initialize(taskData);
			connectorREST.getRepositoryConfiguration(repository).updateProductOptions(taskData);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new BugzillaRestSearchQueryPage("Create query from URL", repository, null,
				SimpleURLQueryPageSchema.getInstance(), taskData,
				new QueryPageDetails(true, "buglist.cgi?", "Enter query parameters", "Please anter a title and an URL",
						"([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]+)", "SimpleURLQueryPage"));
	}

	protected static BugzillaRestSearchQueryPage createBugzillaRestSearchQueryPage(TaskData taskData,
			BugzillaRestConnector connectorREST, TaskRepository repository) {
		try {
			BugzillaRestSearchQueryPageSchema.getInstance().initialize(taskData);
			connectorREST.getRepositoryConfiguration(repository).updateProductOptions(taskData);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new BugzillaRestSearchQueryPage("Create query from a form", repository, null,
				BugzillaRestSearchQueryPageSchema.getInstance(), taskData,
				new QueryPageDetails(true, "buglist.cgi?", "fill the form", "Please enter a title and fill the form",
						"([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]+)", null));
	}

	public static BugzillaRestSearchQueryPage createBugzillaRestSearchPage(boolean simplePage, boolean updateMode,
			TaskData taskData, BugzillaRestConnector connectorREST, TaskRepository repository, IRepositoryQuery query) {
		BugzillaRestSearchQueryPage result = null;
		if (simplePage && !updateMode) {
			result = createSimpleURLQueryPage(taskData, connectorREST, repository);
		}
		if (!simplePage && !updateMode) {
			result = createBugzillaRestSearchQueryPage(taskData, connectorREST, repository);
		}
		if (simplePage && updateMode) {
			result = updateSimpleURLQueryPage(taskData, connectorREST, repository, query);
		}
		if (!simplePage && updateMode) {
			result = updateBugzillaRestSearchQueryPage(taskData, connectorREST, repository, query);
		}
		return result;
	}

	protected static BugzillaRestSearchQueryPage updateSimpleURLQueryPage(TaskData taskData,
			BugzillaRestConnector connectorREST, TaskRepository repository, IRepositoryQuery query) {
		try {
			SimpleURLQueryPageSchema.getInstance().initialize(taskData);
			connectorREST.getRepositoryConfiguration(repository).updateProductOptions(taskData);
			BugzillaRestSearchQueryPageSchema.getInstance().initialize(taskData);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new BugzillaRestSearchQueryPage("Create query from URL", repository, query,
				SimpleURLQueryPageSchema.getInstance(), taskData,
				new QueryPageDetails(true, "buglist.cgi?", "Enter query parameters", "Please anter a title and an URL",
						"([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]+)", "SimpleURLQueryPage"));
	}

	protected static BugzillaRestSearchQueryPage updateBugzillaRestSearchQueryPage(TaskData taskData,
			BugzillaRestConnector connectorREST, TaskRepository repository, IRepositoryQuery query) {
		try {
			BugzillaRestSearchQueryPageSchema.getInstance().initialize(taskData);
			connectorREST.getTaskDataHandler().initializeTaskData(repository, taskData, null,
					new NullProgressMonitor());
			BugzillaRestSearchQueryPageSchema.getInstance().initialize(taskData);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new BugzillaRestSearchQueryPage("Create query from a form", repository, query,
				BugzillaRestSearchQueryPageSchema.getInstance(), taskData,
				new QueryPageDetails(true, "buglist.cgi?", "fill the form", "Please enter a title and fill the form",
						"([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]+)", null));
	}

}
