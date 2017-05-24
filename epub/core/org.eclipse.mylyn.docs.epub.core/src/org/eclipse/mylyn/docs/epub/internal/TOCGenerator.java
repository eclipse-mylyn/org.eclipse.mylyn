/*******************************************************************************
 * Copyright (c) 2011-2014 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.internal;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.mylyn.docs.epub.ncx.Content;
import org.eclipse.mylyn.docs.epub.ncx.NCXFactory;
import org.eclipse.mylyn.docs.epub.ncx.NavLabel;
import org.eclipse.mylyn.docs.epub.ncx.NavPoint;
import org.eclipse.mylyn.docs.epub.ncx.Ncx;
import org.eclipse.mylyn.docs.epub.ncx.Text;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This type is a SAX parser that will read a XHTML file, locate header text (<b>H1</b> trough <b>H6</b>) and create NCX
 * items for the EPUB table of contents.
 * 
 * @author Torkild U. Resheim
 */
public class TOCGenerator extends AbstractXHTMLScanner {

	private String currentId = null;

	private NavPoint[] headers = null;

	private final Ncx ncx;

	private int playOrder;

	public int getPlayOrder() {
		return playOrder;
	}

	public TOCGenerator(String href, Ncx ncx, int playOrder) {
		super();
		buffer = new StringBuilder();
		currentHref = href;
		headers = new NavPoint[6];
		this.ncx = ncx;
		this.playOrder = playOrder;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		int level = isHeader(qName);
		if (level > 0) {
			recording = false;
			NavPoint np = createNavPoint(buffer.toString());
			// Determine the parent header
			NavPoint h = headers[level - 1];
			while (level > 1 && h == null) {
				level--;
				if (level == 1) {
					h = headers[0];
					break;
				}
				h = headers[level - 1];
			}
			// Add to the parent header or to the root
			if (level > 1) {
				h.getNavPoints().add(np);
			} else {
				ncx.getNavMap().getNavPoints().add(np);
			}
			headers[level] = np;
			buffer.setLength(0);
		}
	}

	private NavPoint createNavPoint(String title) {
		NavPoint np = NCXFactory.eINSTANCE.createNavPoint();
		NavLabel nl = NCXFactory.eINSTANCE.createNavLabel();
		Content c = NCXFactory.eINSTANCE.createContent();
		c.setSrc(currentId == null ? currentHref : currentHref + "#" + currentId); //$NON-NLS-1$
		Text text = NCXFactory.eINSTANCE.createText();
		FeatureMapUtil.addText(text.getMixed(), title);
		nl.setText(text);
		np.getNavLabels().add(nl);
		np.setPlayOrder(++playOrder);
		np.setId("navpoint" + playOrder); //$NON-NLS-1$
		np.setContent(c);
		return np;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (isHeader(qName) > 0) {
			recording = true;
			if (attributes.getValue("id") != null) { //$NON-NLS-1$
				currentId = attributes.getValue("id"); //$NON-NLS-1$
			} else {
				currentId = null;
			}
		}
	}

	/**
	 * Parses an XHTML file, representing a publication chapter, and generates a table of contents for this chapter.
	 * 
	 * @param file
	 *            the XHTML file to parse
	 * @param href
	 *            the XHTML file referencing this file
	 * @param ncx
	 *            the NCX to add headers to
	 * @param playOrder
	 *            initial play order
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static int parse(InputSource file, String href, Ncx ncx, int playOrder) throws ParserConfigurationException,
			SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		SAXParser parser = factory.newSAXParser();
		TOCGenerator tocGenerator = new TOCGenerator(href, ncx, playOrder);
		try {
			parser.parse(file, tocGenerator);
		} catch (SAXException e) {
			System.err.println("Could not parse " + href); //$NON-NLS-1$
			e.printStackTrace();
		}
		return tocGenerator.getPlayOrder();
	}

}