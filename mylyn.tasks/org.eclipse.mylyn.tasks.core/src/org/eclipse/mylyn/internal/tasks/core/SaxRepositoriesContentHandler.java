/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core;

import static org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.Strings;

/**
 * Adapted from SaxContextContentHandler
 *
 * @author Rob Elves
 */
public class SaxRepositoriesContentHandler extends DefaultHandler {

	static final String ATTRIBUTE_INTERACTION_EVENT = "InteractionEvent"; //$NON-NLS-1$

	private final Set<TaskRepository> taskRepositories = new HashSet<TaskRepository>();

	private TaskRepository currentRepository;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		try {
			if (localName.equals(TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY)) {
				handleRepositoryElement(attributes);
			} else if (localName.equals(TaskRepositoriesExternalizer.ELEMENT_PROPERTY) && currentRepository != null) {
				// properties are stored as attributes on the repository node as well as children property nodes
				handleProperty(attributes);
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Could not read repositories" //$NON-NLS-1$
					, e));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (currentRepository != null && localName.equals(TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY)) {
			taskRepositories.add(currentRepository);
			currentRepository = null;
		}
	}

	@SuppressWarnings({ "deprecation", "restriction" })
	private void handleRepositoryElement(Attributes attributes) throws SAXException {
		String kind = convertXmlToString(attributes.getValue(IRepositoryConstants.PROPERTY_CONNECTOR_KIND));
		String url = convertXmlToString(attributes.getValue(IRepositoryConstants.PROPERTY_URL));
		if (!Strings.isNullOrEmpty(kind) && !Strings.isNullOrEmpty(url)) {
			currentRepository = new TaskRepository(kind, url);
			// properties are stored as attributes on the repository node as well as children property nodes
			for (int index = 0; index < attributes.getLength(); index++) {
				String key = convertXmlToString(attributes.getLocalName(index));
				String value = convertXmlToString(attributes.getValue(index));
				currentRepository.setProperty(key, value);
			}
		}
	}

	private void handleProperty(Attributes attributes) throws SAXException {
		String key = attributes.getValue(TaskRepositoriesExternalizer.PROPERTY_KEY);
		String value = attributes.getValue(TaskRepositoriesExternalizer.PROPERTY_VALUE);
		if (key != null && value != null) {
			currentRepository.setProperty(key, value);
		}
	}

	public Set<TaskRepository> getRepositories() {
		return taskRepositories;
	}
}
