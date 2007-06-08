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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;

/**
 * Reads bug reports from repository.
 * 
 * @author Rob Elves
 */
public class MultiBugReportFactory extends AbstractReportFactory {

	public MultiBugReportFactory(InputStream inStream, String encoding) {
		super(inStream, encoding);
	}

	private static BugzillaAttributeFactory bugzillaAttributeFactory = new BugzillaAttributeFactory();

	public void populateReport(Map<String, RepositoryTaskData> bugMap) throws IOException, CoreException {

		SaxMultiBugReportContentHandler contentHandler = new SaxMultiBugReportContentHandler(bugzillaAttributeFactory, bugMap);
		collectResults(contentHandler, false);

		if (contentHandler.errorOccurred()) {
			String errorResponse = contentHandler.getErrorMessage().toLowerCase(Locale.ENGLISH);
			if (errorResponse.equals(IBugzillaConstants.XML_ERROR_NOTFOUND)
					|| errorResponse.equals(IBugzillaConstants.XML_ERROR_INVALIDBUGID)) {
				throw new CoreException(new BugzillaStatus(IStatus.WARNING, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_REPOSITORY, "",
						IBugzillaConstants.ERROR_MSG_INVALID_BUG_ID));
			}
			if (errorResponse.equals(IBugzillaConstants.XML_ERROR_NOTPERMITTED)) {
				throw new CoreException(new BugzillaStatus(IStatus.WARNING, BugzillaCorePlugin.PLUGIN_ID,
						RepositoryStatus.ERROR_REPOSITORY, "",
						IBugzillaConstants.ERROR_MSG_OP_NOT_PERMITTED));
			}
		}
	}
}
