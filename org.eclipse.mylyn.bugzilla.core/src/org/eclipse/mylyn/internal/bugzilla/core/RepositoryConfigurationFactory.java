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

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Reads bugzilla product configuration from config.cgi on server in RDF format.
 * 
 * @author Rob Elves
 */
public class RepositoryConfigurationFactory extends AbstractReportFactory {

	private static final String CONFIG_RDF_URL = "/config.cgi?ctype=rdf";

	public RepositoryConfiguration getConfiguration(String repositoryUrl, Proxy proxySettings, String userName,
			String password, String encoding) throws IOException, BugzillaException, GeneralSecurityException {
		String configUrlStr = repositoryUrl + CONFIG_RDF_URL;
		configUrlStr = BugzillaServerFacade.addCredentials(configUrlStr, userName, password);
		URL url = new URL(configUrlStr);
		SaxConfigurationContentHandler contentHandler = new SaxConfigurationContentHandler();
		collectResults(url, proxySettings, encoding, contentHandler, true);
		RepositoryConfiguration config = contentHandler.getConfiguration();
		if (config != null) {
			config.setRepositoryUrl(repositoryUrl);
		}
		return config;
	}

	class SaxErrorHandler implements ErrorHandler {

		public void error(SAXParseException exception) throws SAXException {
			throw exception;
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			throw exception;

		}

		public void warning(SAXParseException exception) throws SAXException {
			// ignore
		}

	}

}
