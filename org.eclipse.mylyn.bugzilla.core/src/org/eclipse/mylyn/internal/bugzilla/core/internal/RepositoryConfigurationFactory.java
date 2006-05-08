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

package org.eclipse.mylar.internal.bugzilla.core.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Reads bugzilla product configuration from config.cgi on server in RDF format.
 * 
 * @author Rob Elves
 */
public class RepositoryConfigurationFactory {

	private static final String CONFIG_RDF_URL = "/config.cgi?ctype=rdf";

	private static RepositoryConfigurationFactory instance;

	private RepositoryConfigurationFactory() {
		// no initial setup needed
	}

	public static RepositoryConfigurationFactory getInstance() {
		if (instance == null) {
			instance = new RepositoryConfigurationFactory();
		}
		return instance;
	}

	public RepositoryConfiguration getConfiguration(TaskRepository repository) throws IOException {
		String configUrlStr = repository.getUrl() + CONFIG_RDF_URL;
		configUrlStr = BugzillaRepositoryUtil.addCredentials(repository, configUrlStr);
		URLConnection c = new URL(configUrlStr).openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

		SaxConfigurationContentHandler contentHandler = new SaxConfigurationContentHandler();

		try {
			StringBuffer result = XmlCleaner.clean(in);
			StringReader strReader = new StringReader(result.toString());
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setErrorHandler(new SaxErrorHandler());
			reader.setContentHandler(contentHandler);
			reader.parse(new InputSource(strReader));
		} catch (SAXException e) {
			throw new IOException("Unable to read server configuration.");
		}
		return contentHandler.getConfiguration();

	}

	// public RepositoryConfiguration getConfiguration(String server) throws
	// IOException {
	// URL serverURL = new URL(server + CONFIG_RDF_URL);
	// BugzillaRepositoryUtil.addCredentials(repository, serverURL)
	// URLConnection c = serverURL.openConnection();
	// BufferedReader in = new BufferedReader(new
	// InputStreamReader(c.getInputStream()));
	//
	// SaxConfigurationContentHandler contentHandler = new
	// SaxConfigurationContentHandler();
	//
	// try {
	// StringBuffer result = XmlCleaner.clean(in);
	// StringReader strReader = new StringReader(result.toString());
	// XMLReader reader = XMLReaderFactory.createXMLReader();
	// reader.setErrorHandler(new SaxErrorHandler());
	// reader.setContentHandler(contentHandler);
	// reader.parse(new InputSource(strReader));
	// } catch (SAXException e) {
	// throw new IOException("Unable to read server configuration.");
	// }
	// return contentHandler.getConfiguration();
	//
	// }

	class SaxErrorHandler implements ErrorHandler {

		public void error(SAXParseException exception) throws SAXException {
			MylarStatusHandler.fail(exception, "ServerConfigurationFactory: " + exception.getLocalizedMessage(), false);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			MylarStatusHandler.fail(exception, "ServerConfigurationFactory: " + exception.getLocalizedMessage(), false);

		}

		public void warning(SAXParseException exception) throws SAXException {
			// ignore
		}

	}

}
