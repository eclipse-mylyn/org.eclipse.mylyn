/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Reads bugzilla product configuration from config.cgi on server in RDF format.
 * 
 * @author Rob Elves
 */
public class RepositoryConfigurationFactory extends AbstractReportFactory {

	@Deprecated
	public RepositoryConfigurationFactory(InputStream inStream, String encoding, BugzillaRepositoryConnector connector) {
		super(inStream, encoding);
	}

	public RepositoryConfigurationFactory(InputStream inStream, String encoding) {
		super(inStream, encoding);
	}

	public RepositoryConfiguration getConfiguration() throws IOException, CoreException {
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
