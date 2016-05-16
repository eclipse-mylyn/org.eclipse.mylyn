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

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ContentHandlerWrapper {

	private final ContentHandler handler;

	public ContentHandlerWrapper(ContentHandler handler) {
		this.handler = handler;
	}

	public void startElement(String elementName, AttributesWrapper attributes) throws SAXException {
		handler.startElement("", elementName, elementName, attributes.getAttributes()); //$NON-NLS-1$
	}

	public void endElement(String elementName) throws SAXException {
		handler.endElement("", elementName, elementName);//$NON-NLS-1$
	}

	public void characters(String value) throws SAXException {
		char[] chars = value.toCharArray();
		handler.characters(chars, 0, chars.length);
	}

	public ContentHandler getHandler() {
		return handler;
	}

}
