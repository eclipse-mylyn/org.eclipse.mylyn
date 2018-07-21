/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.provisional.bugzilla.rest.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConfiguration;
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
			BugzillaRestConfiguration config = connectorREST.getRepositoryConfiguration(repository);
			if (config != null) {
				config.updateProductOptions(taskData);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new BugzillaRestSearchQueryPage(Messages.BugzillaRestUiUtil_CreateQueryFromURL, repository, null,
				SimpleURLQueryPageSchema.getInstance(), taskData,
				new QueryPageDetails(true, "buglist.cgi?", Messages.BugzillaRestUiUtil_EnterQueryParameter, //$NON-NLS-1$
						Messages.BugzillaRestUiUtil_EnterTitleAndURL,
						"([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]+)", "SimpleURLQueryPage")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected static BugzillaRestSearchQueryPage createBugzillaRestSearchQueryPage(TaskData taskData,
			BugzillaRestConnector connectorREST, TaskRepository repository) {
		try {
			BugzillaRestSearchQueryPageSchema.getInstance().initialize(taskData);
			BugzillaRestConfiguration config = connectorREST.getRepositoryConfiguration(repository);
			if (config != null) {
				config.updateProductOptions(taskData);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new BugzillaRestSearchQueryPage(Messages.BugzillaRestUiUtil_CreateQueryFromForm, repository, null,
				BugzillaRestSearchQueryPageSchema.getInstance(), taskData,
				new QueryPageDetails(true, "buglist.cgi?", Messages.BugzillaRestUiUtil_FillForm, //$NON-NLS-1$
						Messages.BugzillaRestUiUtil_enterTitleAndFillForm,
						"([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]+)", null)); //$NON-NLS-1$
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
		return new BugzillaRestSearchQueryPage(Messages.BugzillaRestUiUtil_CreateQueryFromURL, repository, query,
				SimpleURLQueryPageSchema.getInstance(), taskData,
				new QueryPageDetails(true, "buglist.cgi?", Messages.BugzillaRestUiUtil_EnterQueryParameters, //$NON-NLS-1$
						Messages.BugzillaRestUiUtil_EnterTitleAndURL1,
						"([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]+)", "SimpleURLQueryPage")); //$NON-NLS-1$ //$NON-NLS-2$
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
		return new BugzillaRestSearchQueryPage(Messages.BugzillaRestUiUtil_CreateQueryFromForm, repository, query,
				BugzillaRestSearchQueryPageSchema.getInstance(), taskData,
				new QueryPageDetails(true, "buglist.cgi?", Messages.BugzillaRestUiUtil_fillForm, //$NON-NLS-1$
						Messages.BugzillaRestUiUtil_EnterTitleAndFillForm,
						"([a-zA-Z][a-zA-Z+.-]{0,10}://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]+)", null)); //$NON-NLS-1$
	}

}
