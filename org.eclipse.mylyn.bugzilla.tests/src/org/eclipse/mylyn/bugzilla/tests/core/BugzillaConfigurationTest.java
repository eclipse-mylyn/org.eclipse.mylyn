/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.bugzilla.tests.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.bugzilla.core.SaxConfigurationContentHandler;
import org.eclipse.mylyn.internal.bugzilla.core.XmlCleaner;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

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
		assertEquals(1, configuration1.getOptionValues(BugzillaAttribute.PRODUCT).size());

		RepositoryConfiguration configuration2 = new RepositoryConfiguration();
		configuration1.setRepositoryUrl("url2");
		configuration2.addProduct("Test Product 2");
		assertEquals(1, configuration2.getOptionValues(BugzillaAttribute.PRODUCT).size());

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
		assertEquals(1, testLoadedConfig.getOptionValues(BugzillaAttribute.PRODUCT).size());
		assertEquals(configuration1.getOptionValues(BugzillaAttribute.PRODUCT).get(0),
				testLoadedConfig.getOptionValues(BugzillaAttribute.PRODUCT).get(0));
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
		BufferedReader inCleaned = null;
		try {
			File tempFile = File.createTempFile("XmlCleaner-", "tmp");
			tempFile.deleteOnExit();

			InputStream stream = BugzillaFixture.getResource("testdata/configuration/rdfconfig218.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(stream));
			try {
				inCleaned = XmlCleaner.clean(in, tempFile);
				if (tempFile != null) {
					tempFile.delete();
				}
			} finally {
				in.close();
			}

			SaxConfigurationContentHandler contentHandler = new SaxConfigurationContentHandler();
			final XMLReader reader = CoreUtil.newXmlReader();
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
			reader.parse(new InputSource(inCleaned));

			RepositoryConfiguration config = contentHandler.getConfiguration();
			assertNotNull(config);

			assertTrue(config.getOptionValues(BugzillaAttribute.PRODUCT).contains(
					"Test-Long-Named-Product-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		} finally {
			if (inCleaned != null) {
				inCleaned.close();
			}
		}
	}
}
