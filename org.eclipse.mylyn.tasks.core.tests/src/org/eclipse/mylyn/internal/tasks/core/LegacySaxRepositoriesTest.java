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

package org.eclipse.mylyn.internal.tasks.core;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.ImmutableSet;

public class LegacySaxRepositoriesTest {

	private class SaxRepositoriesContentHandlerVersion1 extends DefaultHandler {

		private final Set<TaskRepository> taskRepositories = new HashSet<TaskRepository>();

		@SuppressWarnings({ "deprecation", "restriction" })
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			try {
				if (localName.equals(TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY) && attributes != null) {
					String kind = org.eclipse.mylyn.internal.commons.core.XmlStringConverter
							.convertXmlToString(attributes.getValue(IRepositoryConstants.PROPERTY_CONNECTOR_KIND));
					String url = org.eclipse.mylyn.internal.commons.core.XmlStringConverter
							.convertXmlToString(attributes.getValue(IRepositoryConstants.PROPERTY_URL));
					if (kind != null && kind.length() > 0 && url != null && url.length() > 0) {
						TaskRepository repository = new TaskRepository(kind, url);
						for (int index = 0; index < attributes.getLength(); index++) {
							String key = org.eclipse.mylyn.internal.commons.core.XmlStringConverter
									.convertXmlToString(attributes.getLocalName(index));
							String value = org.eclipse.mylyn.internal.commons.core.XmlStringConverter
									.convertXmlToString(attributes.getValue(index));
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

	private static final String kind = "connector.kind";

	private static final String firstUrl = "http://first.url";

	private static final String secondUrl = "http://second.url";

	private static final String labelPropertyValue = "test repository";

	private static final String labelPropertyValueAlternate = "test repository alternate";

	private static final String labelPropertyKey = "label";

	private final String repositoryXmlVersion2 = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
			+ "<TaskRepositories OutputVersion=\"2\">" //
			+ "<TaskRepository url=\"%s\" kind=\"%s\" %s=\"%s\"><Property key=\"%s\" value=\"%s\"/></TaskRepository>" //
			+ "<TaskRepository url=\"%s\" kind=\"%s\" %s=\"%s\"><Property key=\"%s\" value=\"%s\"/></TaskRepository>" //
			+ "</TaskRepositories>", //
			firstUrl, kind, labelPropertyKey, labelPropertyValue, labelPropertyKey, labelPropertyValueAlternate,
			secondUrl, kind, labelPropertyKey, labelPropertyValue, labelPropertyKey, labelPropertyValueAlternate);

	@Test
	public void version1ReaderCanReadVersion2Xml() throws Exception {
		Set<TaskRepository> expectedRepositories = ImmutableSet.of(
				createTestRepository(kind, firstUrl, labelPropertyKey, labelPropertyValue),
				createTestRepository(kind, secondUrl, labelPropertyKey, labelPropertyValue));
		Set<TaskRepository> repositories = parseRepositoriesWithVersion1Parser(repositoryXmlVersion2);
		assertEquals(expectedRepositories, repositories);
		for (TaskRepository repository : repositories) {
			assertEquals(labelPropertyValue, repository.getProperty(labelPropertyKey));
		}
	}

	@Test
	public void version1CanReadLatestOutput() throws Exception {
		TaskRepository initialRepository = createTestRepository(kind, firstUrl, labelPropertyKey, labelPropertyValue);
		String xml = writeToXmlWithCurrentVersion(ImmutableSet.of(initialRepository));
		Set<TaskRepository> parsed = parseRepositoriesWithVersion1Parser(xml);
		assertEquals(1, parsed.size());
		TaskRepository parsedRepository = parsed.iterator().next();
		assertEquals(initialRepository, parsedRepository);
		assertEquals(labelPropertyValue, parsedRepository.getProperty(labelPropertyKey));

	}

	private String writeToXmlWithCurrentVersion(Set<TaskRepository> repositories) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream(1000 * repositories.size());
		SaxRepositoriesWriter writer = new SaxRepositoriesWriter();
		writer.setOutputStream(output);
		writer.writeRepositoriesToStream(repositories);
		return output.toString("UTF-8");
	}

	private Set<TaskRepository> parseRepositoriesWithVersion1Parser(String xml) throws Exception {
		SaxRepositoriesContentHandlerVersion1 handler = new SaxRepositoriesContentHandlerVersion1();
		XMLReader reader = XmlReaderUtil.createXmlReader();
		reader.setContentHandler(handler);
		reader.parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));
		return handler.getRepositories();
	}

	private TaskRepository createTestRepository(String kind, String url, String labelPropertyKey,
			String labelProperty) {
		TaskRepository repository = new TaskRepository(kind, url);
		repository.setProperty(labelPropertyKey, labelProperty);
		return repository;
	}

}
