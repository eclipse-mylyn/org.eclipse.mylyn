/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.internal.commons.core.XmlStringConverter;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Adapted from SaxContextContentHandler
 * 
 * @author Rob Elves
 */
public class SaxRepositoriesContentHandler extends DefaultHandler {

	static final String ATTRIBUTE_INTERACTION_EVENT = "InteractionEvent";

	private final Set<TaskRepository> taskRepositories = new HashSet<TaskRepository>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		try {
			if (localName.equals(TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY) && attributes != null) {
				String kind = XmlStringConverter.convertXmlToString(attributes.getValue(IRepositoryConstants.PROPERTY_CONNECTOR_KIND));
				String url = XmlStringConverter.convertXmlToString(attributes.getValue(IRepositoryConstants.PROPERTY_URL));
				if (kind != null && kind.length() > 0 && url != null && url.length() > 0) {
					TaskRepository repository = new TaskRepository(kind, url);
					for (int index = 0; index < attributes.getLength(); index++) {
						String key = XmlStringConverter.convertXmlToString(attributes.getLocalName(index));
						String value = XmlStringConverter.convertXmlToString(attributes.getValue(index));
						repository.setProperty(key, value);
					}
					taskRepositories.add(repository);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Set<TaskRepository> getRepositories() {
		return taskRepositories;
	}
}
