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

package org.eclipse.mylar.internal.bugzilla.ui.search;

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

import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Rob Elves
 */
public class RepositoryQueryFactory {

	public static final int RETURN_ALL_HITS = -1;
	
	private static RepositoryQueryFactory instance;

	private RepositoryQueryFactory() {
		// no initial setup needed
	}

	public static RepositoryQueryFactory getInstance() {
		if (instance == null) {
			instance = new RepositoryQueryFactory();
		}
		return instance;
	}

	public void performQuery(String repositoryUrl, IBugzillaSearchResultCollector collector, String queryUrlString,
			Proxy proxySettings, int maxHits, String characterEncoding) throws LoginException, KeyManagementException,
			NoSuchAlgorithmException, IOException {

		BufferedReader in = null;

		URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(new URL(queryUrlString), proxySettings);
		if (cntx == null || !(cntx instanceof HttpURLConnection)) {
			throw new IOException("Could not form URLConnection.");
		}

		HttpURLConnection connection = (HttpURLConnection) cntx;
		connection.connect();
		int responseCode = connection.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			String msg;
			if (responseCode == -1 || responseCode == HttpURLConnection.HTTP_FORBIDDEN)
				msg = repositoryUrl + " does not seem to be a valid Bugzilla server.  Check Bugzilla preferences.";
			else
				msg = "HTTP Error " + responseCode + " (" + connection.getResponseMessage()
						+ ") while querying Bugzilla Server.  Check Bugzilla preferences.";

			throw new IOException(msg);
		}

		// if (monitor.isCanceled()) {
		// throw new OperationCanceledException("Search cancelled");
		// }

		if (characterEncoding != null) {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), characterEncoding));
		} else {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		}

		// if (monitor.isCanceled()) {
		// throw new OperationCanceledException("Search cancelled");
		// }

		SaxBugzillaQueryContentHandler contentHandler = new SaxBugzillaQueryContentHandler(repositoryUrl, collector,
				maxHits);

		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(contentHandler);
			reader.setErrorHandler(new ErrorHandler() {

				public void error(SAXParseException exception) throws SAXException {
					MylarStatusHandler.fail(exception, "Mylar: BugzillaSearchEngine Sax parser error", false);
				}

				public void fatalError(SAXParseException arg0) throws SAXException {
					// ignore

				}

				public void warning(SAXParseException exception) throws SAXException {
					// ignore

				}
			});
			reader.parse(new InputSource(in));

			// if (contentHandler.errorOccurred()) {
			// throw new IOException(contentHandler.getErrorMessage());
			// }

		} catch (SAXException e) {
			throw new IOException(e.getMessage());
		}
	}
}
