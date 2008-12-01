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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.security.GeneralSecurityException;

import org.xml.sax.EntityResolver;
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

	public static final int RETURN_ALL_HITS = -1;

	private final InputStream inStream;

	private final String characterEncoding;

	public AbstractReportFactory(InputStream inStream, String encoding) {
		this.inStream = inStream;
		this.characterEncoding = encoding;
	}

	/**
	 * expects rdf returned from repository (ctype=rdf in url)
	 * 
	 * @throws GeneralSecurityException
	 */
	protected void collectResults(DefaultHandler contentHandler, boolean clean) throws IOException {
		File tempFile = null;

		if (inStream == null) {
			return;
		}

		final BufferedInputStream is = new BufferedInputStream(inStream, 1024);

		InputStream iis = new InputStream() {
			@SuppressWarnings( { "deprecation", "restriction" })
			@Override
			public int read() throws IOException {
				int c;
				while ((c = is.read()) != -1) {
					if (org.eclipse.mylyn.internal.commons.core.XmlStringConverter.isValid((char) c)) {
						return c;
					}
				}
				return -1;
			}
		};

		Reader in;
		if (characterEncoding != null) {
			in = new InputStreamReader(iis, characterEncoding);
		} else {
			in = new InputStreamReader(iis);
		}

		if (clean) {
			tempFile = File.createTempFile("XmlCleaner-", "tmp");
			tempFile.deleteOnExit();
			in = XmlCleaner.clean(in, tempFile);
		}

		try {
			final XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setFeature("http://xml.org/sax/features/validation", false);
			reader.setContentHandler(contentHandler);

			EntityResolver resolver = new EntityResolver() {

				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					// The default resolver will try to resolve the dtd via
					// URLConnection. Since we
					// don't have need of entity resolving
					// currently, we just supply a dummy (empty) resource for
					// each request...
					InputSource source = new InputSource();
					source.setCharacterStream(new StringReader(""));
					return source;
				}
			};

			reader.setEntityResolver(resolver);
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
			throw new IOException(e.getMessage());
		}

		finally {
			if (tempFile != null) {
				tempFile.delete();
			}
		}
	}
}
