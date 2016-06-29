/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;

public class SaxOrphanBuilder {

	private final Document document;

	private Element currentElement;

	private StringBuilder currentStringContent;

	private final Element orphansElement;

	public SaxOrphanBuilder() throws CoreException {
		this.document = createDocument();
		this.orphansElement = document.getDocumentElement();
	}

	private Document createDocument() throws CoreException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document document = db.newDocument();
			Element root = document.createElement("orphans"); //$NON-NLS-1$
			document.appendChild(root);
			return document;
		} catch (ParserConfigurationException e) {
			throw new CoreException(
					new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Failed to create document", e)); //$NON-NLS-1$
		}
	}

	public void startElement(String localName, Attributes attributes) {
		Element element = document.createElement(localName);
		for (int i = 0; i < attributes.getLength(); i++) {
			String attributeName = attributes.getLocalName(i);
			String attributeValue = attributes.getValue(i);
			element.setAttribute(attributeName, attributeValue);
		}

		if (currentElement != null) {
			appendTextNode();
			currentElement.appendChild(element);
		}
		currentElement = element;

		currentStringContent = new StringBuilder();
	}

	public void acceptCharacters(char[] content, int start, int length) {
		if (currentStringContent != null) {
			currentStringContent.append(content, start, length);
		}
	}

	public void endElement() {
		if (currentElement != null) {
			appendTextNode();
			Node parentNode = currentElement.getParentNode();
			// do not set the current element to be the orphans element or all tasks will end up as orphans
			if (parentNode instanceof Element && parentNode != orphansElement) {
				currentElement = (Element) parentNode;
				currentStringContent = new StringBuilder();
			} else {
				currentElement = null;
				currentStringContent = null;
			}
		}
	}

	public void commitOrphan() {
		if (currentElement != null) {
			document.getDocumentElement().appendChild(currentElement);
		}
	}

	private void appendTextNode() {
		if (hasStringContnet()) {
			Text textNode = document.createTextNode(currentStringContent.toString());
			currentElement.appendChild(textNode);
		}
	}

	private boolean hasStringContnet() {
		return currentStringContent != null & currentStringContent.length() > 0;
	}

	public Document getOrphans() {
		return document;
	}

}
