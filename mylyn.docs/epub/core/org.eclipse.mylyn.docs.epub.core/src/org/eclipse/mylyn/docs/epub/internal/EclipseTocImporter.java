/*******************************************************************************
 * Copyright (c) 2014 Torkild U. Resheim
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.docs.epub.internal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.mylyn.docs.epub.core.Publication;
import org.eclipse.mylyn.docs.epub.opf.Item;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This type will parse an <i>Eclipse Help Table of Contents</i> and add all referenced HTML files to the publication.
 *
 * @author Torkild U. Resheim
 */
public class EclipseTocImporter {

	private static void importTocs(Publication oebps, File rootFile, Node root)
			throws ParserConfigurationException, SAXException, IOException, DOMException, URISyntaxException {
		NodeList childNodes = root.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			// Link to another table of contents
			NamedNodeMap attributes = node.getAttributes();
			if ("link".equals(node.getNodeName())) { //$NON-NLS-1$
				if (attributes != null) {
					Node toc = attributes.getNamedItem("toc"); //$NON-NLS-1$
					File tocFile = new File(rootFile.getParentFile(), toc.getNodeValue());
					importFile(oebps, rootFile, tocFile);
				}
			}
			if ("topic".equals(node.getNodeName())) { //$NON-NLS-1$
				if (attributes != null) {
					Node href = attributes.getNamedItem("href"); //$NON-NLS-1$
					if (href != null) {
						String nodeValue = href.getNodeValue();
						if (nodeValue.contains("#")) { //$NON-NLS-1$
							nodeValue = nodeValue.substring(0, nodeValue.lastIndexOf("#")); //$NON-NLS-1$
						}
						File hrefFile = new File(rootFile.getParentFile(), nodeValue);
						// Determine whether or not the file is already
						// present. We expect there to be no other files with
						// the same name already in the manifest.
						EList<Item> items = oebps.getPackage().getManifest().getItems();
						boolean found = false;
						for (Item item : items) {
							if (item.getFile() != null) {
								File t = new File(item.getFile());
								if (t.getName().equals(hrefFile.getName())) {
									found = true;
								}
							}
						}
						if (!found) {
							oebps.addItem(hrefFile);
						}
					}
				}
			}
			importTocs(oebps, rootFile, childNodes.item(i));
		}
	}

	private static void importFile(Publication oebps, File rootFile, File file)
			throws ParserConfigurationException, SAXException, IOException, DOMException, URISyntaxException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		importTocs(oebps, rootFile, doc);

	}

	/**
	 * Imports the specified Eclipse Help table of contents and adds referenced HTML files to the publication. Each file
	 * will only be added once, in the order discovered.
	 *
	 * @param file
	 *            the table of contents file to import.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws DOMException
	 * @throws URISyntaxException
	 */
	public static void importFile(Publication oebps, File file)
			throws ParserConfigurationException, SAXException, IOException, DOMException, URISyntaxException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		importTocs(oebps, file, doc);
	}
}
