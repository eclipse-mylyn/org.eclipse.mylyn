/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * Reads bug reports from repository.
 * 
 * @author Rob Elves
 */
public class MultiBugReportFactory extends AbstractReportFactory {

	private final BugzillaRepositoryConnector connector;

	public MultiBugReportFactory(InputStream inStream, String encoding, BugzillaRepositoryConnector connector) {
		super(inStream, encoding);
		this.connector = connector;
	}

	public void populateReport(Map<String, TaskData> bugMap, TaskDataCollector collector, TaskAttributeMapper mapper,
			List<BugzillaCustomField> customFields) throws IOException, CoreException {

		SaxMultiBugReportContentHandler contentHandler = new SaxMultiBugReportContentHandler(mapper, collector, bugMap,
				customFields, connector);
		collectResults(contentHandler, false);

		for (TaskData data : bugMap.values()) {
			TaskAttribute attrCreation = data.getRoot().getAttribute(BugzillaAttribute.CREATION_TS.getKey());
			if (attrCreation == null || attrCreation.getValue() == null || attrCreation.getValue().length() == 0) {
				collector.failed(data.getTaskId(), new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						IBugzillaConstants.ERROR_MSG_NO_DATA_RETRIEVED));
			}
		}

		if (bugMap.size() == 1 && contentHandler.errorOccurred()) {
			String errorResponse = contentHandler.getErrorMessage().toLowerCase(Locale.ENGLISH);
			if (errorResponse.equals(IBugzillaConstants.XML_ERROR_NOTFOUND)
					|| errorResponse.equals(IBugzillaConstants.XML_ERROR_INVALIDBUGID)) {
				throw new CoreException(new BugzillaStatus(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY, "", IBugzillaConstants.ERROR_MSG_INVALID_BUG_ID)); //$NON-NLS-1$
			} else if (errorResponse.equals(IBugzillaConstants.XML_ERROR_NOTPERMITTED)) {
				throw new CoreException(new BugzillaStatus(IStatus.INFO, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, mapper.getTaskRepository().getRepositoryUrl(),
						IBugzillaConstants.ERROR_MSG_OP_NOT_PERMITTED));
			} else {
				throw new CoreException(new BugzillaStatus(IStatus.WARNING, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY, "", "Unexpected error occurred: " + errorResponse)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
}
