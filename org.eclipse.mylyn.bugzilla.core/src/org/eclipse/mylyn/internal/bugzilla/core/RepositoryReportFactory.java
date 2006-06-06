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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.security.auth.login.LoginException;

import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;

/**
 * Reads bug reports from repository.
 * 
 * @author Rob Elves
 */
public class RepositoryReportFactory extends AbstractReportFactory {

	private static RepositoryReportFactory instance;
	
	private static BugzillaAttributeFactory bugzillaAttributeFactory = new BugzillaAttributeFactory();

	private static final String SHOW_BUG_CGI_XML = "/show_bug.cgi?ctype=xml&id=";

	private RepositoryReportFactory() {
		// no initial setup needed
	}

	public static RepositoryReportFactory getInstance() {
		if (instance == null) {
			instance = new RepositoryReportFactory();
		}
		return instance;
	}

	public void populateReport(RepositoryTaskData bugReport, String repositoryUrl, Proxy proxySettings, String userName,
			String password, String characterEncoding) throws LoginException, KeyManagementException,
			NoSuchAlgorithmException, IOException {

		SaxBugReportContentHandler contentHandler = new SaxBugReportContentHandler(bugzillaAttributeFactory, bugReport);

		String xmlBugReportUrl = repositoryUrl + SHOW_BUG_CGI_XML + bugReport.getId();
		xmlBugReportUrl = BugzillaRepositoryUtil.addCredentials(xmlBugReportUrl, userName, password);
		URL serverURL = new URL(xmlBugReportUrl);

		collectResults(serverURL, proxySettings, characterEncoding, contentHandler);

		if (contentHandler.errorOccurred()) {
			throw new IOException(contentHandler.getErrorMessage());
		}

	}

//	public class BugzillaReportParseException extends IOException {
//		private static final long serialVersionUID = 1609566799047500866L;
//
//		public BugzillaReportParseException(String message) {
//			super(message);
//		}
//	}
}

// URLConnection connection =
// BugzillaPlugin.getDefault().getUrlConnection(serverURL, proxySettings);
// if (connection == null || !(connection instanceof HttpURLConnection)) {
// return;
// }
//
// // String contentEncoding = connection.getContentEncoding();
// // if (contentEncoding != null) {
// // String charsetFromContentType =
// // BugzillaRepositoryUtil.getCharsetFromString(contentEncoding);
// // if (charsetFromContentType != null) {
// // bugReport.setCharset(charsetFromContentType);
// // }
// // } else {
// // bugReport.setCharset(BugzillaPlugin.ENCODING_UTF_8);
// // }
//
// try {
//
// if (characterEncoding != null) {
// in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
// characterEncoding));
// } else {
// in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
// }
//
// XMLReader reader = XMLReaderFactory.createXMLReader();
// reader.setContentHandler(contentHandler);
// reader.setErrorHandler(new SaxErrorHandler());
// reader.parse(new InputSource(in));
//
// if (contentHandler.errorOccurred()) {
// throw new BugzillaReportParseException(contentHandler.getErrorMessage());
// }
//
// } catch (SAXException e) {
// if
// (e.getMessage().equals(IBugzillaConstants.ERROR_INVALID_USERNAME_OR_PASSWORD))
// {
// throw new LoginException(e.getMessage());
// } else {
// throw new IOException(e.getMessage());
// }
// } finally {
// try {
// if (in != null)
// in.close();
// } catch (IOException e) {
// BugzillaPlugin.log(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID,
// IStatus.ERROR,
// "Problem closing the stream", e));
// }
// }

// class SaxErrorHandler implements ErrorHandler {
//
// public void error(SAXParseException exception) throws SAXException {
// throw exception;
// // MylarStatusHandler.fail(exception, "Mylar:
// // RepositoryReportFactory Sax parser error", false);
// // System.err.println("Error: " + exception.getLineNumber() + "\n" +
// // exception.getLocalizedMessage());
//
// }
//
// public void fatalError(SAXParseException exception) throws SAXException {
// // System.err.println("Fatal Error: " + exception.getLineNumber() +
// // "\n" + exception.getLocalizedMessage());
// // TODO: Need to determine actual error from html
// throw new
// SAXException(IBugzillaConstants.ERROR_INVALID_USERNAME_OR_PASSWORD);
// }
//
// public void warning(SAXParseException exception) throws SAXException {
// // System.err.println("Warning: " + exception.getLineNumber() + "\n"
// // + exception.getLocalizedMessage());
// }
//
// }
//