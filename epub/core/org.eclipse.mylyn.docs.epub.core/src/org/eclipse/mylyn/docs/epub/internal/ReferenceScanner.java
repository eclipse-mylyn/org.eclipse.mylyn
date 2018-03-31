/*******************************************************************************
 * Copyright (c) 2011-2014 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.internal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.mylyn.docs.epub.opf.Item;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This type is a SAX parser that will read a XHTML file and create a list of all images, content and CSS-files that are
 * referenced either through an <i>img<i/> tag or a link.
 *
 * @author Torkild U. Resheim
 */
public class ReferenceScanner extends AbstractXHTMLScanner {

	public static List<File> parse(Item item) throws ParserConfigurationException, SAXException, IOException {
		FileReader fr = new FileReader(item.getFile());
		InputSource file = new InputSource(fr);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		SAXParser parser = factory.newSAXParser();
		String href = item.getHref();
		ReferenceScanner scanner = new ReferenceScanner(item);
		try {
			parser.parse(file, scanner);
			return scanner.files;
		} catch (SAXException e) {
			System.err.println("Could not parse " + href); //$NON-NLS-1$
			e.printStackTrace();
		}
		return null;
	}

	Item currentItem;

	ArrayList<File> files;

	public ReferenceScanner(Item item) {
		super();
		files = new ArrayList<File>();
		currentItem = item;
	}

	/**
	 * Case-insensitive method for obtaining an attribute.
	 *
	 * @param attributes
	 *            SAX attributes
	 * @param name
	 *            the attribute name
	 * @return the attribute value or <code>null</code>
	 */
	private String getAttribute(Attributes attributes, String name) {
		for (int i = 0; i < attributes.getLength(); i++) {
			String aname = attributes.getQName(i);
			if (aname.equalsIgnoreCase(name)) {
				return attributes.getValue(i);
			}
		}
		return null;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		// Handle inline image files
		if (qName.equalsIgnoreCase("img")) { //$NON-NLS-1$
			String ref = getAttribute(attributes, "src"); //$NON-NLS-1$
			if (ref != null) {
				String t = ref.toLowerCase();
				if (t.startsWith("http://") || t.startsWith("https://")) { //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
				// If the reference is absolute it will kept as it is, otherwise
				// me must make it absolute using the source file as a
				// reference.
				String source = currentItem.getSourcePath();
				if (source == null) {
					source = currentItem.getFile();
				}
				File sourceFile = new File(source);
				File file = new File(ref);
				if (!file.isAbsolute()) {
					file = new File(sourceFile.getParentFile().getAbsolutePath() + File.separator + ref);
				}
				files.add(file);
			}
		}

		// Also handle links to files
		if (qName.equalsIgnoreCase("a")) { //$NON-NLS-1$
			String ref = getAttribute(attributes, "href"); //$NON-NLS-1$
			if (ref != null) {
				includeRef(ref);
			}
		}
		// Also handle links to CSS style sheets
		if (qName.equalsIgnoreCase("link")) { //$NON-NLS-1$
			String ref = getAttribute(attributes, "href"); //$NON-NLS-1$
			String rel = getAttribute(attributes, "rel"); //$NON-NLS-1$
			if (ref != null && rel != null && rel.equals("stylesheet")) { //$NON-NLS-1$
				includeRef(ref);
			}
		}
	}

	private void includeRef(String ref) {
		String t = ref.toLowerCase();
		if (t.startsWith("#") || t.startsWith("http://") || t.startsWith("https://")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return;
		}
		// Handle links to anchors within resources (bug 375795).
		if (ref.indexOf('#') > -1) {
			ref = ref.substring(0, ref.indexOf('#'));
		}
		// If the item was generated for instance by WikiText we need to
		// use the original path. Otherwise we use the item path.
		String source = currentItem.getSourcePath();
		if (source == null) {
			source = currentItem.getFile();
		}
		File sourceFile = new File(source);
		File file = new File(sourceFile.getParentFile().getAbsolutePath() + File.separator + ref);
		if (!file.isDirectory()) {
			files.add(file);
		}
	}
}
