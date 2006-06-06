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
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.security.auth.login.LoginException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Rob Elves
 */
public class AbstractReportFactory {

	private static final int COM_TIME_OUT = 10000;

	private static final String CONTENT_TYPE_TEXT_HTML = "text/html";

	private static final String CONTENT_TYPE_APP_RDF_XML = "application/rdf+xml";

	private static final String CONTENT_TYPE_APP_XML = "application/xml";

	private static final String CONTENT_TYPE_TEXT_XML = "text/xml";

	public static final int RETURN_ALL_HITS = -1;

	private BufferedReader in = null;

	private boolean clean = false;
	
	protected void collectResults(URL url, Proxy proxySettings, String characterEncoding, DefaultHandler contentHandler)
			throws IOException, LoginException, KeyManagementException, NoSuchAlgorithmException {
		URLConnection cntx = BugzillaPlugin.getUrlConnection(url, proxySettings);
		if (cntx == null || !(cntx instanceof HttpURLConnection)) {
			throw new IOException("Could not form URLConnection.");
		}

		HttpURLConnection connection = (HttpURLConnection) cntx;
		connection.setConnectTimeout(COM_TIME_OUT);
		connection.setReadTimeout(COM_TIME_OUT);
		connection.connect();
		int responseCode = connection.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			String msg;
			if (responseCode == -1 || responseCode == HttpURLConnection.HTTP_FORBIDDEN)
				msg = "Repository does not seem to be a valid Bugzilla server.  Check Bugzilla preferences.";
			else
				msg = "HTTP Error " + responseCode + " (" + connection.getResponseMessage()
						+ ") while querying Bugzilla Server.  Check Bugzilla preferences.";

			throw new IOException(msg);
		}

		if (characterEncoding != null) {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), characterEncoding));
		} else {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		}

		
		
		if(clean) {
			 StringBuffer result = XmlCleaner.clean(in);
			 StringReader strReader = new StringReader(result.toString());
			 in = new BufferedReader(strReader);
		}
		
		if (connection.getContentType().contains(CONTENT_TYPE_APP_RDF_XML)
				|| connection.getContentType().contains(CONTENT_TYPE_APP_XML)
				|| connection.getContentType().contains(CONTENT_TYPE_TEXT_XML)) {

			try {
				final XMLReader reader = XMLReaderFactory.createXMLReader();
				reader.setContentHandler(contentHandler);
				reader.setErrorHandler(new ErrorHandler() {

					public void error(SAXParseException exception) throws SAXException {
						throw exception;
					}

					public void fatalError(SAXParseException exception) throws SAXException {
						throw exception;
					}

					public void warning(SAXParseException exception) throws SAXException {
						throw exception;
					}
				});
				reader.parse(new InputSource(in));
			} catch (SAXException e) {
				if (e.getMessage().equals(IBugzillaConstants.ERROR_INVALID_USERNAME_OR_PASSWORD)) {
					throw new LoginException(e.getMessage());
				} else {
					throw new IOException(e.getMessage());
				}
			}
		} else if (connection.getContentType().contains(CONTENT_TYPE_TEXT_HTML)) {
			in.mark(0);
			BugzillaRepositoryUtil.parseHtmlError(in);
			in.reset();
			String message = "";
			String newLine = in.readLine();
			while (newLine != null) {
				message += newLine;
				newLine = in.readLine();
			}
			throw new UnrecognizedBugzillaError(message);

		} else {
			throw new IOException("Unrecognized content type: " + connection.getContentType());
		}
	}

	public class UnrecognizedBugzillaError extends IOException {
		private static final long serialVersionUID = 8419167415822022988L;

		public UnrecognizedBugzillaError(String message) {
			super(message);
		}
	}

	protected void setClean(boolean clean) {
		this.clean = clean;
	}

}
