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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Reads bugzilla product configuration from config.cgi on server in RDF format.
 * 
 * @author Rob Elves
 */
public class ServerConfigurationFactory  {

	private static final String CONFIG_RDF_URL = "/config.cgi?ctype=rdf";

	private static ServerConfigurationFactory instance;

	private ServerConfigurationFactory() {
		// no initial setup needed
	}

	public static ServerConfigurationFactory getInstance() {
		if (instance == null) {
			instance = new ServerConfigurationFactory();
		}
		return instance;
	}

	// public ProductConfiguration getConfiguration(String server) throws
	// IOException {
	// URL serverURL = new URL(server + "/config.cgi?ctype=rdf");
	// URLConnection c = serverURL.openConnection();
	// BufferedReader in = new BufferedReader(new
	// InputStreamReader(c.getInputStream()));
	// Document document;
	// DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	// factory.setValidating(false);
	// factory.setNamespaceAware(false);
	// try {
	// DocumentBuilder builder = factory.newDocumentBuilder();
	// document = builder.parse( new InputSource(in) );
	//
	// } catch (SAXParseException spe) {
	// System.err.println("Sax parse exception!");
	// } catch (ParserConfigurationException e) {
	// e.printStackTrace();
	// } catch (SAXException e) {
	// e.printStackTrace();
	// }
	// return null;
	//
	// }

	public RepositoryConfiguration getConfiguration(String server) throws IOException {
		URL serverURL = new URL(server + CONFIG_RDF_URL);
		URLConnection c = serverURL.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

		SaxConfigurationContentHandler contentHandler = new SaxConfigurationContentHandler();

		try {
			StringBuffer result = XmlCleaner.clean(in);
			StringReader strReader = new StringReader(result.toString());
			XMLReader reader = XMLReaderFactory.createXMLReader();
			// reader.setErrorHandler(new SaxErrorHandler())
			reader.setContentHandler(contentHandler);
			reader.parse(new InputSource(strReader));
		} catch (SAXException e) {
			throw new IOException("Unable to read server configuration.");
		}
		return contentHandler.getConfiguration();

	}

	// class SaxErrorHandler implements ErrorHandler {
	//
	// public void error(SAXParseException exception) throws SAXException {
	// System.err.println("Error:
	// "+exception.getLineNumber()+"\n"+exception.getLocalizedMessage());
	//			
	// }
	//
	// public void fatalError(SAXParseException exception) throws SAXException {
	// System.err.println("Fatal Error:
	// "+exception.getLineNumber()+"\n"+exception.getLocalizedMessage());
	//			
	// }
	//
	// public void warning(SAXParseException exception) throws SAXException {
	// System.err.println("Warning:
	// "+exception.getLineNumber()+"\n"+exception.getLocalizedMessage());
	//			
	// }
	//		
	// }

}
