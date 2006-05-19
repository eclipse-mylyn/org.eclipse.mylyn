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
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
	 * 
	 * @param proxyConfig
	 *            TODO
	 * @param characterEncoding
	 *            TODO
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public void populateReport(BugzillaReport bugReport, String repositoryUrl, Proxy proxySettings, String userName,
			String password, String characterEncoding) throws LoginException, KeyManagementException,
			NoSuchAlgorithmException, IOException {

		BufferedReader in = null;
		SaxBugReportContentHandler contentHandler = new SaxBugReportContentHandler(bugReport);

		String xmlBugReportUrl = repositoryUrl + SHOW_BUG_CGI_XML + bugReport.getId();
		URL serverURL = new URL(BugzillaRepositoryUtil.addCredentials(xmlBugReportUrl, userName, password));
		URLConnection connection = BugzillaPlugin.getDefault().getUrlConnection(serverURL, proxySettings);
		if (connection == null || !(connection instanceof HttpURLConnection)) {
			return;
		}

		// String contentEncoding = connection.getContentEncoding();
		// if (contentEncoding != null) {
		// String charsetFromContentType =
		// BugzillaRepositoryUtil.getCharsetFromString(contentEncoding);
		// if (charsetFromContentType != null) {
		// bugReport.setCharset(charsetFromContentType);
		// }
		// } else {
		// bugReport.setCharset(BugzillaPlugin.ENCODING_UTF_8);
		// }

		try {

			if (characterEncoding != null) {
				in = new BufferedReader(new InputStreamReader(connection.getInputStream(), characterEncoding));
			} else {
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			}

			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(contentHandler);
			reader.setErrorHandler(new SaxErrorHandler());
			reader.parse(new InputSource(in));

			if (contentHandler.errorOccurred()) {
				throw new BugzillaReportParseException(contentHandler.getErrorMessage());
			}

		} catch (SAXException e) {
			if (e.getMessage().equals(IBugzillaConstants.ERROR_INVALID_USERNAME_OR_PASSWORD)) {
				throw new LoginException(e.getMessage());
			} else {
				throw new IOException(e.getMessage());
			}
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				BugzillaPlugin.log(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, IStatus.ERROR,
						"Problem closing the stream", e));
			}
		}

	}

	class SaxErrorHandler implements ErrorHandler {

		public void error(SAXParseException exception) throws SAXException {
			throw exception;
			// MylarStatusHandler.fail(exception, "Mylar:
			// RepositoryReportFactory Sax parser error", false);
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
