/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.notifications;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class ServiceMessageXmlHandler extends DefaultHandler {

	private static final String TAG_MESSAGE = "ServiceMessage"; //$NON-NLS-1$

	private StringBuilder characters;

	private final List<ServiceMessage> messages = new ArrayList<ServiceMessage>();

	private ServiceMessage message;

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		characters.append(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (TAG_MESSAGE.equals(qName.trim())) {
			message = new ServiceMessage();
		}
		characters = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (message == null) {
			return;
		}
		if (TAG_MESSAGE.equals(qName.trim())) {
			if (message.isValid()) {
				messages.add(message);
			}
		} else {
			String parsedText = characters.toString();
			ServiceMessage.Element element;
			try {
				element = ServiceMessage.Element.valueOf(qName.trim().toUpperCase());
				switch (element) {
				case ID:
					message.setId(parsedText);
					break;
				case DESCRIPTION:
					message.setDescription(parsedText);
					break;
				case TITLE:
					message.setTitle(parsedText);
					break;
				case URL:
					message.setUrl(parsedText);
					break;
				case IMAGE:
					message.setImage(parsedText);
					break;
				case VERSION:
					message.setVersion(parsedText);
					break;
				}
			} catch (IllegalArgumentException e) {
				// ignore unrecognized elements
			}
		}
	}

	public List<ServiceMessage> getMessages() {
		return new ArrayList<ServiceMessage>(messages);
	}

}
