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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.security.auth.login.LoginException;

import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Reads bug reports from repository.
 * 
 * @author Rob Elves
 */
public class RepositoryReportFactory {

	private static RepositoryReportFactory instance;

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

	// /**
	// * Bugzilla specific, to be generalized
	// * TODO: Based on repository kind use appropriate loader
	// */
	// public AbstractRepositoryReport readReport(int id, TaskRepository
	// repository)
	// throws IOException, LoginException {
	// BugReport bugReport = new BugReport(id, repository.getUrl());
	// SaxBugReportContentHandler contentHandler = new
	// SaxBugReportContentHandler(bugReport);
	//
	// String xmlBugReportUrl = repository.getUrl() + SHOW_BUG_CGI_XML + id;
	//
	// URL serverURL = new URL(BugzillaRepositoryUtil.addCredentials(repository,
	// xmlBugReportUrl));
	// URLConnection connection = serverURL.openConnection();
	// String contentType = connection.getContentType();
	// if (contentType != null) {
	// String charsetFromContentType = getCharsetFromString(contentType);
	// if (charsetFromContentType != null) {
	// bugReport.setCharset(charsetFromContentType);
	// }
	// }
	//		
	// BufferedReader in = new BufferedReader(new
	// InputStreamReader(connection.getInputStream()));
	//
	// try {
	// XMLReader reader = XMLReaderFactory.createXMLReader();
	// reader.setContentHandler(contentHandler);
	// reader.setErrorHandler(new SaxErrorHandler());
	// reader.parse(new InputSource(in));
	//			
	// if(contentHandler.errorOccurred()) {
	// throw new BugzillaReportParseException(contentHandler.getErrorMessage());
	// }
	//			
	// } catch (SAXException e) {
	// throw new IOException(e.getMessage());
	// }
	// return bugReport;
	// }

	/**
	 * Bugzilla specific, to be generalized TODO: Based on repository kind use
	 * appropriate loader
	 */
	public void populateReport(BugzillaReport bugReport, String repositoryUrl, String userName, String password) throws IOException, LoginException {

		SaxBugReportContentHandler contentHandler = new SaxBugReportContentHandler(bugReport);

		String xmlBugReportUrl = repositoryUrl + SHOW_BUG_CGI_XML + bugReport.getId();

		URL serverURL = new URL(BugzillaRepositoryUtil.addCredentials(xmlBugReportUrl, userName, password));
		URLConnection connection = serverURL.openConnection();
		String contentType = connection.getContentType();
		if (contentType != null) {
			String charsetFromContentType = BugzillaRepositoryUtil.getCharsetFromString(contentType);
			if (charsetFromContentType != null) {
				bugReport.setCharset(charsetFromContentType);
			}
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(contentHandler);
			reader.setErrorHandler(new SaxErrorHandler());
			reader.parse(new InputSource(in));

			if (contentHandler.errorOccurred()) {
				throw new BugzillaReportParseException(contentHandler.getErrorMessage());
			}

		} catch (SAXException e) {
			throw new IOException(e.getMessage());
		}
	}

	class SaxErrorHandler implements ErrorHandler {

		public void error(SAXParseException exception) throws SAXException {
			throw exception;
//			MylarStatusHandler.fail(exception, "Mylar: RepositoryReportFactory Sax parser error", false);
			// System.err.println("Error: " + exception.getLineNumber() + "\n" +
			// exception.getLocalizedMessage());

		}

		public void fatalError(SAXParseException exception) throws SAXException {
			// System.err.println("Fatal Error: " + exception.getLineNumber() +
			// "\n" + exception.getLocalizedMessage());
			// TODO: Need to determine actual error from html
			throw new SAXException(IBugzillaConstants.ERROR_INVALID_USERNAME_OR_PASSWORD);
		}

		public void warning(SAXParseException exception) throws SAXException {
			// System.err.println("Warning: " + exception.getLineNumber() + "\n"
			// + exception.getLocalizedMessage());
		}

	}

	public class BugzillaReportParseException extends IOException {

		private static final long serialVersionUID = 7269179766737288564L;

		public BugzillaReportParseException(String message) {
			super(message);
		}
	}

}
