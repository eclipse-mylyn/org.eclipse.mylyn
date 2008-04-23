/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class ElementHandler extends DefaultHandler {

	protected final StringBuilder currentElementText;

	private ElementHandler currentHandler;

	private final String elementName;

	private final Map<String, ElementHandler> handlers;

	private final ElementHandler parent;

	public ElementHandler(ElementHandler parent, String elementName) {
		this.parent = parent;
		this.elementName = elementName;
		this.handlers = new HashMap<String, ElementHandler>();
		this.currentElementText = new StringBuilder();
	}

	public void addElementHandler(ElementHandler handler) {
		handlers.put(handler.getElementName(), handler);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (currentHandler != null) {
			currentHandler.characters(ch, start, length);
		} else {
			currentElementText.append(ch, start, length);
		}
	}

	protected void done(ElementHandler elementHandler) {
		currentHandler = null;
	}

	protected void end(String uri, String localName, String name) {
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if (currentHandler != null) {
			currentHandler.endElement(uri, localName, name);
		} else if (elementName.equals(localName)) {
			end(uri, localName, name);
			if (parent != null) {
				parent.done(this);
			}
		}
	}

	protected void clearCurrentElementText() {
		currentElementText.setLength(0);
	}

	protected String getCurrentElementText() {
		return currentElementText.toString();
	}

	public String getElementName() {
		return elementName;
	}

	protected String getOptionalValue(Attributes attributes, String name) throws SAXException {
		String value = attributes.getValue(name);
		if (value == null) {
			return "";
		}
		return value;
	}

	public ElementHandler getParent() {
		return parent;
	}

	protected String getValue(Attributes attributes, String name) throws SAXException {
		String value = attributes.getValue(name);
		if (value == null) {
			throw new SAXException("Missing required attribute \"" + name + "\"");
		}
		return value;
	}

	public void removeElementHandler(ElementHandler handler) {
		handlers.remove(handler.getElementName());
	}

	protected void start(String uri, String localName, String name, Attributes attributes) throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		if (currentHandler == null) {
			ElementHandler handler = handlers.get(name);
			if (handler != null) {
				currentHandler = handler;
				currentHandler.start(uri, localName, name, attributes);
			}
		} else if (currentHandler != null) {
			currentHandler.startElement(uri, localName, name, attributes);
		}
	}
}