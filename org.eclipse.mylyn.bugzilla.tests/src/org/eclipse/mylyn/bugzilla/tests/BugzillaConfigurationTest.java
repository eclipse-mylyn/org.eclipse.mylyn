/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClientManager;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.bugzilla.core.SaxConfigurationContentHandler;
import org.eclipse.mylyn.internal.bugzilla.core.XmlCleaner;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class BugzillaConfigurationTest extends TestCase {

	BugzillaClientManager bugzillaClientManager = new BugzillaClientManager();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private BugzillaClient createClient(String hostUrl, String username, String password, String htAuthUser,
			String htAuthPass, String encoding) throws MalformedURLException, CoreException {
		TaskRepository taskRepository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, hostUrl);

		AuthenticationCredentials credentials = new AuthenticationCredentials(username, password);
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, credentials, false);

		AuthenticationCredentials webCredentials = new AuthenticationCredentials(htAuthUser, htAuthPass);
		taskRepository.setCredentials(AuthenticationType.HTTP, webCredentials, false);
		taskRepository.setCharacterEncoding(encoding);
		return bugzillaClientManager.getClient(taskRepository, null);
	}

	public void test222RDFProductConfig() throws Exception {

		BugzillaClient client = createClient(IBugzillaConstants.TEST_BUGZILLA_222_URL, "", "", "", "", "UTF-8");
		RepositoryConfiguration config = client.getRepositoryConfiguration();
		assertNotNull(config);
		assertEquals("2.22.1", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(4, config.getPlatforms().size());
		assertEquals(6, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(3, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(1, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		assertEquals(0, config.getTargetMilestones("TestProduct").size());
	}

	public void test2201RDFProductConfig() throws Exception {
		BugzillaClient client = createClient(IBugzillaConstants.TEST_BUGZILLA_2201_URL, "", "", "", "", "UTF-8");
		RepositoryConfiguration config = client.getRepositoryConfiguration();
		assertNotNull(config);
		assertEquals("2.20.1", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(4, config.getPlatforms().size());
		assertEquals(5, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(1, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(2, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		// assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}

	public void test220RDFProductConfig() throws Exception {
		BugzillaClient client = createClient(IBugzillaConstants.TEST_BUGZILLA_220_URL, "", "", "", "", "UTF-8");
		RepositoryConfiguration config = client.getRepositoryConfiguration();
		assertNotNull(config);
		assertEquals("2.20.3", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(4, config.getPlatforms().size());
		assertEquals(5, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(2, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(2, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		// assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}

	public void test218RDFProductConfig() throws Exception {
		BugzillaClient client = createClient(IBugzillaConstants.TEST_BUGZILLA_218_URL, "", "", "", "", "UTF-8");
		RepositoryConfiguration config = client.getRepositoryConfiguration();
		assertNotNull(config);
		assertEquals("2.18.6", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(8, config.getPlatforms().size());
		assertEquals(36, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(1, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(1, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		// assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}

	public void testEclipseRDFProductConfig() throws Exception {
		BugzillaClient client = createClient(IBugzillaConstants.ECLIPSE_BUGZILLA_URL, "", "", "", "", "UTF-8");
		RepositoryConfiguration config = client.getRepositoryConfiguration();
		assertNotNull(config);
		assertEquals("3.0.4", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(9, config.getResolutions().size());
		assertEquals(6, config.getPlatforms().size());
		assertEquals(31, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertTrue(config.getProducts().size() > 50);
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(14, config.getComponents("Mylyn").size());
		assertEquals(27, config.getKeywords().size());
		// assertEquals(10, config.getComponents("Hyades").size());
		// assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}

	public void testRepositoryConfigurationCachePersistance() throws Exception {
		RepositoryConfiguration configuration1 = new RepositoryConfiguration();
		configuration1.setRepositoryUrl("url1");
		configuration1.addProduct("Test Product 1");
		assertEquals(1, configuration1.getProducts().size());

		RepositoryConfiguration configuration2 = new RepositoryConfiguration();
		configuration1.setRepositoryUrl("url2");
		configuration2.addProduct("Test Product 2");
		assertEquals(1, configuration2.getProducts().size());

		BugzillaCorePlugin.addRepositoryConfiguration(configuration1);
		BugzillaCorePlugin.addRepositoryConfiguration(configuration2);
		BugzillaCorePlugin.writeRepositoryConfigFile();
		BugzillaCorePlugin.removeConfiguration(configuration1);
		BugzillaCorePlugin.removeConfiguration(configuration2);
		assertNull(BugzillaCorePlugin.getRepositoryConfiguration(configuration1.getRepositoryUrl()));
		assertNull(BugzillaCorePlugin.getRepositoryConfiguration(configuration2.getRepositoryUrl()));
		BugzillaCorePlugin.readRepositoryConfigurationFile();
		assertNotNull(BugzillaCorePlugin.getRepositoryConfiguration(configuration1.getRepositoryUrl()));
		assertNotNull(BugzillaCorePlugin.getRepositoryConfiguration(configuration2.getRepositoryUrl()));
		RepositoryConfiguration testLoadedConfig = BugzillaCorePlugin.getRepositoryConfiguration(configuration1.getRepositoryUrl());
		assertEquals(1, testLoadedConfig.getProducts().size());
		assertEquals(configuration1.getProducts().get(0), testLoadedConfig.getProducts().get(0));
	}

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
//        System.err.println(result);
//	}

	/**
	 * Can use this to test config data submitted by users. Be sure not to commit user's config file though. The file
	 * included (rdfconfig218.txt) is from mylyn.eclipse.org/bugs218
	 */
	public void testRepositoryConfigurationFromFile() throws Exception {

		URL entryURL = BugzillaTestPlugin.getDefault().getBundle().getEntry("testdata/configuration/rdfconfig218.txt");
		assertNotNull(entryURL);
		URL fileURL = FileLocator.toFileURL(entryURL);
		assertNotNull(fileURL);

		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileURL.getFile())));

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
