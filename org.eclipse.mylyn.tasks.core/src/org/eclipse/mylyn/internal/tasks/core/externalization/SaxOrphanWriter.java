/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.externalization;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class SaxOrphanWriter {

	private final ContentHandlerWrapper handler;

	public SaxOrphanWriter(ContentHandlerWrapper handler) {
		this.handler = handler;
	}

	public void writeOrphans(NodeList orphanNodes) throws SAXException {
		for (int i = 0; i < orphanNodes.getLength(); i++) {
			Node orphanNode = orphanNodes.item(i);
			if (orphanNode instanceof Element) {
				Element orphanElement = (Element) orphanNode;
				AttributesWrapper saxAttributes = getAttributes(orphanElement);
				handler.startElement(orphanElement.getNodeName(), saxAttributes);
				writeOrphans(orphanElement.getChildNodes());
				handler.endElement(orphanElement.getNodeName());
			} else if (orphanNode instanceof Text) {
				Text orphanText = (Text) orphanNode;
				handler.characters(orphanText.getData());
			}
		}
	}

	private AttributesWrapper getAttributes(Element orphanElement) {
		AttributesWrapper saxAttributes = new AttributesWrapper();
		NamedNodeMap domAttributes = orphanElement.getAttributes();
		for (int i = 0; i < domAttributes.getLength(); i++) {
			Node attribute = domAttributes.item(i);
			saxAttributes.addAttribute(attribute.getNodeName(), attribute.getNodeValue());
		}
		return saxAttributes;
	}

}
