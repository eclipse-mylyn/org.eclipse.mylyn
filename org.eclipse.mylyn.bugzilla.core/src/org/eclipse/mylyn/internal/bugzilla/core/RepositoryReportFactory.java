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

package org.eclipse.mylar.internal.bugzilla.core;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.eclipse.mylar.tasks.core.RepositoryTaskData;

/**
 * Reads bug reports from repository.
 * 
 * @author Rob Elves
 */
public class RepositoryReportFactory extends AbstractReportFactory {

	private static BugzillaAttributeFactory bugzillaAttributeFactory = new BugzillaAttributeFactory();

	private static final String SHOW_BUG_CGI_XML = "/show_bug.cgi?ctype=xml&id=";

	public void populateReport(RepositoryTaskData bugReport, String repositoryUrl, Proxy proxySettings,
			String userName, String password, String characterEncoding) throws GeneralSecurityException, KeyManagementException,
			NoSuchAlgorithmException, IOException, BugzillaException {

		SaxBugReportContentHandler contentHandler = new SaxBugReportContentHandler(bugzillaAttributeFactory, bugReport);

		String xmlBugReportUrl = repositoryUrl + SHOW_BUG_CGI_XML + bugReport.getId();
		xmlBugReportUrl = BugzillaServerFacade.addCredentials(xmlBugReportUrl, userName, password);
		URL serverURL = new URL(xmlBugReportUrl);

		collectResults(serverURL, proxySettings, characterEncoding, contentHandler, false);

		if (contentHandler.errorOccurred()) {
			throw new IOException(contentHandler.getErrorMessage());
		}

	}
}