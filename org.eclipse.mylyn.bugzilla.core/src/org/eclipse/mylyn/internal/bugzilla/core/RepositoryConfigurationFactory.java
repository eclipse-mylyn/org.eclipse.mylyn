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
import java.io.InputStream;
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

	public RepositoryConfigurationFactory(InputStream inStream, String encoding) {
		super(inStream, encoding);		
	}

	public RepositoryConfiguration getConfiguration() throws IOException, BugzillaException, GeneralSecurityException {		
		SaxConfigurationContentHandler contentHandler = new SaxConfigurationContentHandler();
		collectResults(contentHandler, true);
		RepositoryConfiguration config = contentHandler.getConfiguration();
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
