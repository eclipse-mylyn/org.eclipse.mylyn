/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.bugzilla.core.SaxConfigurationContentHandler;
import org.eclipse.mylyn.internal.bugzilla.core.XmlCleaner;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Robert Elves
 */
public class BugzillaConfigurationTest extends TestCase {

	public void testRepositoryConfigurationCachePersistance() throws Exception {
		File file = File.createTempFile("bugzilla", null);
		file.deleteOnExit();

		BugzillaRepositoryConnector connector = new BugzillaRepositoryConnector(file);
		RepositoryConfiguration configuration1 = new RepositoryConfiguration();
		configuration1.setRepositoryUrl("url1");
		configuration1.addProduct("Test Product 1");
		assertEquals(1, configuration1.getProducts().size());

		RepositoryConfiguration configuration2 = new RepositoryConfiguration();
		configuration1.setRepositoryUrl("url2");
		configuration2.addProduct("Test Product 2");
		assertEquals(1, configuration2.getProducts().size());

		connector.addRepositoryConfiguration(configuration1);
		connector.addRepositoryConfiguration(configuration2);
		connector.writeRepositoryConfigFile();
		assertNotNull(connector.getRepositoryConfiguration(configuration1.getRepositoryUrl()));
		assertNotNull(connector.getRepositoryConfiguration(configuration2.getRepositoryUrl()));

		connector.removeConfiguration(configuration1);
		connector.removeConfiguration(configuration2);
		assertNull(connector.getRepositoryConfiguration(configuration1.getRepositoryUrl()));
		assertNull(connector.getRepositoryConfiguration(configuration2.getRepositoryUrl()));

		connector = new BugzillaRepositoryConnector(file);
		connector.readRepositoryConfigurationFile();
		assertNotNull(connector.getRepositoryConfiguration(configuration1.getRepositoryUrl()));
		assertNotNull(connector.getRepositoryConfiguration(configuration2.getRepositoryUrl()));
		RepositoryConfiguration testLoadedConfig = connector.getRepositoryConfiguration(configuration1.getRepositoryUrl());
		assertEquals(1, testLoadedConfig.getProducts().size());
		assertEquals(configuration1.getProducts().get(0), testLoadedConfig.getProducts().get(0));
	}

	public void testNullCacheFile() {
		BugzillaRepositoryConnector connector = new BugzillaRepositoryConnector();
		connector.readRepositoryConfigurationFile();
		RepositoryConfiguration configuration1 = new RepositoryConfiguration();
		configuration1.setRepositoryUrl("url1");
		configuration1.addProduct("Test Product 1");
		assertNull(connector.getRepositoryConfiguration(configuration1.getRepositoryUrl()));

		connector.addRepositoryConfiguration(configuration1);
		assertNotNull(connector.getRepositoryConfiguration(configuration1.getRepositoryUrl()));

		connector = new BugzillaRepositoryConnector();
		connector.readRepositoryConfigurationFile();
		assertNull(connector.getRepositoryConfiguration(configuration1.getRepositoryUrl()));
	}

	// FIXME re-enable?
//	@SuppressWarnings("deprecation")
//	public void testHtmlCleaner() throws IOException, BugzillaException, GeneralSecurityException {
//		StringBuffer incoming = new StringBuffer();
//		incoming.append("<RDF xmlns=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
//		incoming.append("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
//		incoming.append("xmlns:bz=\"http://www.bugzilla.org/rdf#\">");
//		incoming.append("<li>");
//        incoming.append("<bz:product
//        incoming.append("</bz:product>");
//        incoming.append("</li>");
//        incoming.append("</RDF>");
//
//        StringBuffer result = XmlCleaner.clean(new StringReader(incoming.toString()));
//	}

	/**
	 * Can use this to test config data submitted by users. Be sure not to commit user's config file though. The file
	 * included (rdfconfig218.txt) is from mylyn.eclipse.org/bugs218
	 */
	public void testRepositoryConfigurationFromFile() throws Exception {
		File file = BugzillaFixture.getFile("testdata/configuration/rdfconfig218.txt");
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

		if (true) {
			File tempFile = File.createTempFile("XmlCleaner-", "tmp");
			tempFile.deleteOnExit();
			in = XmlCleaner.clean(in, tempFile);
			if (tempFile != null) {
				tempFile.delete();
			}

		}

		SaxConfigurationContentHandler contentHandler = new SaxConfigurationContentHandler();
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

		RepositoryConfiguration config = contentHandler.getConfiguration();
		assertNotNull(config);

		assertTrue(config.getProducts().contains(
				"Test-Long-Named-Product-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

		// Add your additional checking for valid data here if necessary

	}

}
