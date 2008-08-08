/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * Reads bug reports from repository.
 * 
 * @author Rob Elves
 */
public class MultiBugReportFactory extends AbstractReportFactory {

	public MultiBugReportFactory(InputStream inStream, String encoding) {
		super(inStream, encoding);
	}

	public void populateReport(Map<String, TaskData> bugMap, TaskDataCollector collector, TaskAttributeMapper mapper,
			List<BugzillaCustomField> customFields) throws IOException, CoreException {

		SaxMultiBugReportContentHandler contentHandler = new SaxMultiBugReportContentHandler(mapper, collector, bugMap,
				customFields);
		collectResults(contentHandler, false);

		if (contentHandler.errorOccurred()) {
			String errorResponse = contentHandler.getErrorMessage().toLowerCase(Locale.ENGLISH);
			if (errorResponse.equals(IBugzillaConstants.XML_ERROR_NOTFOUND)
					|| errorResponse.equals(IBugzillaConstants.XML_ERROR_INVALIDBUGID)) {
				throw new CoreException(new BugzillaStatus(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY, "", IBugzillaConstants.ERROR_MSG_INVALID_BUG_ID));
			} else if (errorResponse.equals(IBugzillaConstants.XML_ERROR_NOTPERMITTED)) {
				BugzillaStatus status = new BugzillaStatus(IStatus.INFO, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, mapper.getTaskRepository().getRepositoryUrl(),
						IBugzillaConstants.ERROR_MSG_OP_NOT_PERMITTED);
				throw new CoreException(status);
			} else {
				throw new CoreException(new BugzillaStatus(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY, "", "Unexpected error occurred: " + errorResponse));
			}
		}
	}
}
