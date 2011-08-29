/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Get;
import org.eclipse.mylyn.wikitext.core.util.IgnoreDtdEntityResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

class MediaWikiApiImageFetchingStrategy extends ImageFetchingStrategy {

	private final Pattern imageTitlePattern = Pattern.compile("(?:Image|File):(.+)"); //$NON-NLS-1$

	private URL url;

	private String pageName;

	@Override
	public Set<String> fetchImages() {
		if (pageName == null || pageName.length() == 0) {
			throw new BuildException("please specify @pageName"); //$NON-NLS-1$
		}
		if (!pageName.equals(pageName.trim())) {
			throw new BuildException("@pageName must not have leading or training whitespace"); //$NON-NLS-1$
		}

		String base;
		try {
			base = url.toURI().toString();
		} catch (URISyntaxException e) {
			throw new BuildException(e);
		}
		if (!base.endsWith("/")) { //$NON-NLS-1$
			base += "/"; //$NON-NLS-1$
		}

		URL apiUrl;
		try {
			String queryString = String.format(
					"action=query&titles=%s&generator=images&prop=imageinfo&iiprop=url&format=xml", URLEncoder.encode(pageName, "UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$
			apiUrl = new URL(base + "api.php?" + queryString); //$NON-NLS-1$
		} catch (Exception e) {
			throw new BuildException("Cannot compose API URL", e); //$NON-NLS-1$
		}

		Set<String> filenames = new HashSet<String>();

		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		parserFactory.setValidating(false);

		Reader input;
		try {
			input = new InputStreamReader(new BufferedInputStream(apiUrl.openStream()), "UTF-8"); //$NON-NLS-1$
		} catch (IOException e) {
			throw new BuildException(String.format("Cannot contact %s: %s", apiUrl, e.getMessage()), e); //$NON-NLS-1$
		}
		try {
			SAXParser saxParser = parserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setEntityResolver(IgnoreDtdEntityResolver.getInstance());

			ImageFetchingContentHandler contentHandler = new ImageFetchingContentHandler();
			xmlReader.setContentHandler(contentHandler);

			try {
				xmlReader.parse(new InputSource(input));
			} catch (IOException e) {
				throw new BuildException(String.format("Unexpected exception retrieving data from %s", apiUrl), e); //$NON-NLS-1$
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					// ignore
				}
			}
			int fileCount = 0;
			for (Map.Entry<String, String> ent : contentHandler.imageTitleToUrl.entrySet()) {
				String title = ent.getKey();
				String imageUrl = ent.getValue();
				Matcher titleMatcher = imageTitlePattern.matcher(title);
				if (titleMatcher.matches()) {
					String name = titleMatcher.group(1);
					name = name.replace(' ', '_');
					String qualifiedUrl = base;
					if (imageUrl.matches("https?://.*")) { //$NON-NLS-1$
						qualifiedUrl = imageUrl;
					} else {
						if (imageUrl.startsWith("/")) { //$NON-NLS-1$
							qualifiedUrl += imageUrl.substring(0);
						} else {
							qualifiedUrl += imageUrl;
						}
					}

					log("Fetching " + qualifiedUrl, Project.MSG_INFO); //$NON-NLS-1$
					Get get = new Get();
					get.setProject(getProject());
					get.setLocation(getLocation());
					try {
						get.setSrc(new URL(qualifiedUrl));
					} catch (MalformedURLException e) {
						log("Skipping " + url + ": " + e.getMessage(), Project.MSG_WARN); //$NON-NLS-1$ //$NON-NLS-2$
						continue;
					}
					get.setDest(new File(dest, name));
					get.execute();

					filenames.add(name);
					++fileCount;
				} else {
					log(String.format("Unexpected title format: %s", title), Project.MSG_WARN); //$NON-NLS-1$
				}
			}
			log("Fetched " + fileCount + " image files for " + pageName, Project.MSG_INFO); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (SAXException e) {
			throw new BuildException("Unexpected error in XML content", e); //$NON-NLS-1$
		} catch (ParserConfigurationException e) {
			throw new BuildException("Cannot configure SAX parser", e); //$NON-NLS-1$
		}
		return filenames;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	private class ImageFetchingContentHandler implements ContentHandler {

		final Map<String, String> imageTitleToUrl = new HashMap<String, String>();

		private String currentPage = null;

		private boolean inImageInfo = false;

		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			if ("page".equals(localName)) { //$NON-NLS-1$
				currentPage = atts.getValue("title"); //$NON-NLS-1$
			} else if ("imageinfo".equals(localName)) { //$NON-NLS-1$
				inImageInfo = true;
			} else if (inImageInfo && "ii".equals(localName)) { //$NON-NLS-1$
				imageTitleToUrl.put(currentPage, atts.getValue("url")); //$NON-NLS-1$
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ("page".equals(localName)) { //$NON-NLS-1$
				currentPage = null;
			} else if ("imageinfo".equals(localName)) { //$NON-NLS-1$
				inImageInfo = false;
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
		}

		public void endDocument() throws SAXException {
		}

		public void endPrefixMapping(String prefix) throws SAXException {
		}

		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		}

		public void processingInstruction(String target, String data) throws SAXException {
		}

		public void setDocumentLocator(Locator locator) {
		}

		public void skippedEntity(String name) throws SAXException {
		}

		public void startDocument() throws SAXException {
		}

		public void startPrefixMapping(String prefix, String uri) throws SAXException {
		}

	}

}
