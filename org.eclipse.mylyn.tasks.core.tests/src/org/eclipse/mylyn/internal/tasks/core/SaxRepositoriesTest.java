/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

import static org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SaxRepositoriesTest {

	private static final String kind = "connector.kind";

	private static final String kindCharacters = "connector.kind-`~!@#$%^&*()_+-=[{}]\\|'\";:/?.>,<";

	private static final String firstUrl = "http://first.url";

	private static final String secondUrl = "http://second.url";

	private static final String urlCharacters = "http://some.url--`~!@#$%^&*()_+-=[{}]\\|'\";:/?.>,<";

	private static final String labelPropertyValue = "test repository";

	private static final String labelPropertyValueAlternate = "test repository alternate";

	private static final String labelPropertyKey = "label";

	private static final String labelPropertyCharacters = "`~!@#$%^&*()_+-=[{}]\\|'\";:/?.>,<";

	private static final String labelPropertyKeyCharacters = "label-`~!@#$%^&*()_+-=[{}]\\|'\";:/?.>,<";

	private SaxRepositoriesContentHandler handler;

	private final String version1RepositoryXml = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
			+ "<TaskRepositories OutputVersion=\"1\">" //
			+ "<TaskRepository url=\"%s\" kind=\"%s\" %s=\"%s\"/>" //
			+ "</TaskRepositories>", firstUrl, kind, labelPropertyKey, labelPropertyValue);

	private final String version1RepositoryXmlMultiple = String.format(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
					+ "<TaskRepositories OutputVersion=\"1\">" //
					+ "<TaskRepository url=\"%s\" kind=\"%s\" %s=\"%s\"/>" //
					+ "<TaskRepository url=\"%s\" kind=\"%s\" %s=\"%s\"/>" //
					+ "</TaskRepositories>",
			firstUrl, kind, labelPropertyKey, labelPropertyValue, secondUrl, kind, labelPropertyKey,
			labelPropertyValue);

	/**
	 * The old xml is escaped twice: once by the xml library and once within mylyn
	 */
	private final String version1RepositoryXmlSpecialCharacters = String.format(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
					+ "<TaskRepositories OutputVersion=\"1\">" //
					+ "<TaskRepository url=\"%s\" kind=\"%s\" %s=\"%s\"/>" //
					+ "</TaskRepositories>",
			escapeXml(escapeXml(urlCharacters)), escapeXml(escapeXml(kindCharacters)), labelPropertyKey,
			escapeXml(escapeXml(labelPropertyCharacters)));

	private final String version2RepositoryXml = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
			+ "<TaskRepositories OutputVersion=\"2\">" //
			+ "<TaskRepository url=\"%s\" kind=\"%s\"><Property key=\"%s\" value=\"%s\"/></TaskRepository>" //
			+ "</TaskRepositories>" //
			, firstUrl, kind, labelPropertyKey, labelPropertyValue);

	private final String version2RepositoryXmlMultiple = String.format(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
					+ "<TaskRepositories OutputVersion=\"2\">" //
					+ "<TaskRepository url=\"%s\" kind=\"%s\"><Property key=\"%s\" value=\"%s\"/></TaskRepository>" //
					+ "<TaskRepository url=\"%s\" kind=\"%s\"><Property key=\"%s\" value=\"%s\"/></TaskRepository>" //
					+ "</TaskRepositories>",
			firstUrl, kind, labelPropertyKey, labelPropertyValue, secondUrl, kind, labelPropertyKey,
			labelPropertyValue);

	private final String version2RepositoryXmlSpecialCharacters = String.format(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
					+ "<TaskRepositories OutputVersion=\"2\">" //
					+ "<TaskRepository url=\"%s\" kind=\"%s\"><Property key=\"%s\" value=\"%s\"/></TaskRepository>" //
					+ "</TaskRepositories>",
			escapeXml(escapeXml(urlCharacters)), escapeXml(escapeXml(kindCharacters)),
			escapeXml(labelPropertyKeyCharacters), escapeXml(labelPropertyCharacters));

	private final String version1AndVersion2RepositoryXmlMultiple = String.format(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
					+ "<TaskRepositories OutputVersion=\"2\">" //
					+ "<TaskRepository url=\"%s\" kind=\"%s\" %s=\"%s\"><Property key=\"%s\" value=\"%s\"/></TaskRepository>" //
					+ "<TaskRepository url=\"%s\" kind=\"%s\" %s=\"%s\"><Property key=\"%s\" value=\"%s\"/></TaskRepository>" //
					+ "</TaskRepositories>", //
			firstUrl, kind, labelPropertyKey, labelPropertyValueAlternate, labelPropertyKey, labelPropertyValue,
			secondUrl, kind, labelPropertyKey, labelPropertyValueAlternate, labelPropertyKey, labelPropertyValue);

	@Before
	public void setUp() {
		handler = new SaxRepositoriesContentHandler();
	}

	@Test
	public void readVersion1() throws SAXException, IOException {
		assertRead(version1RepositoryXml, Sets.newHashSet(createTestRepository(firstUrl)));
	}

	@Test
	public void readVersion1Multiple() throws SAXException, IOException {
		assertRead(version1RepositoryXmlMultiple,
				Sets.newHashSet(createTestRepository(firstUrl), createTestRepository(secondUrl)));
	}

	@Test
	public void readVersion1Characters() throws SAXException, IOException {
		assertRead(version1RepositoryXmlSpecialCharacters,
				Sets.newHashSet(
						createTestRepository(kindCharacters, urlCharacters, labelPropertyKey, labelPropertyCharacters)),
				labelPropertyKey, labelPropertyCharacters);
	}

	@Test
	public void readVersion2() throws SAXException, IOException {
		assertRead(version2RepositoryXml, Sets.newHashSet(createTestRepository(firstUrl)));
	}

	@Test
	public void readVersion2Multiple() throws SAXException, IOException {
		assertRead(version2RepositoryXmlMultiple,
				Sets.newHashSet(createTestRepository(firstUrl), createTestRepository(secondUrl)));
	}

	@Test
	public void readVersion2Characters() throws SAXException, IOException {
		assertRead(
				version2RepositoryXmlSpecialCharacters, Sets.newHashSet(createTestRepository(kindCharacters,
						urlCharacters, labelPropertyKeyCharacters, labelPropertyCharacters)),
				labelPropertyKeyCharacters, labelPropertyCharacters);
	}

	@Test
	public void write() throws SAXException, IOException {
		OutputStream output = new ByteArrayOutputStream();
		write(output, ImmutableList.of(createTestRepository(firstUrl)));
		assertRead(output.toString(), Sets.newHashSet(createTestRepository(firstUrl)));
	}

	@Test
	public void writeMultiple() throws SAXException, IOException {
		OutputStream output = new ByteArrayOutputStream();
		write(output, ImmutableList.of(createTestRepository(firstUrl), createTestRepository(secondUrl)));
		assertRead(output.toString(), Sets.newHashSet(createTestRepository(firstUrl), createTestRepository(secondUrl)));
	}

	@Test
	public void readMixed() throws Exception {
		assertRead(version1AndVersion2RepositoryXmlMultiple,
				Sets.newHashSet(createTestRepository(firstUrl), createTestRepository(secondUrl)));
	}

	@Test
	public void writeMixedBadCharacters() throws Exception {
		OutputStream output = new ByteArrayOutputStream();
		write(output, Lists
				.newArrayList(createTestRepository(kind, firstUrl, labelPropertyKeyCharacters, labelPropertyValue)));
		String serialized = output.toString();
		// the Mylyn escaping utility turns ' into apos, but this is not done during the actual serialization
		assertTrue(
				serialized.contains(("key=\"" + escapeXml(labelPropertyKeyCharacters) + "\"").replace("&apos;", "'")));
		assertTrue(serialized.contains("value=\"" + escapeXml(labelPropertyValue) + "\""));
		// the property should have only been written once
		assertEquals(serialized.lastIndexOf(labelPropertyValue), serialized.indexOf(labelPropertyValue));
	}

	private void assertRead(String xml, Set<TaskRepository> expectedRepositories) throws SAXException, IOException {
		assertRead(xml, expectedRepositories, labelPropertyKey, labelPropertyValue);
	}

	private void assertRead(String xml, Set<TaskRepository> expectedRepositories, String labelPropertyKey,
			String labelPropertyValue) throws SAXException, IOException {
		parse(xml);
		Set<TaskRepository> repositories = handler.getRepositories();
		assertEquals(expectedRepositories, repositories);
		for (TaskRepository repository : repositories) {
			assertEquals(labelPropertyValue, repository.getProperty(labelPropertyKey));
		}
	}

	private void parse(String xml) throws SAXException, IOException {
		XMLReader reader = XmlReaderUtil.createXmlReader();
		reader.setContentHandler(handler);
		reader.parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));
	}

	private void write(OutputStream output, List<TaskRepository> repositories) throws IOException {
		SaxRepositoriesWriter writer = new SaxRepositoriesWriter();
		writer.setOutputStream(output);
		writer.writeRepositoriesToStream(repositories);
	}

	private TaskRepository createTestRepository(String url) {
		return createTestRepository(kind, url, labelPropertyKey, labelPropertyValue);
	}

	private TaskRepository createTestRepository(String kind, String url, String labelPropertyKey,
			String labelProperty) {
		TaskRepository repository = new TaskRepository(kind, url);
		repository.setProperty(labelPropertyKey, labelProperty);
		return repository;
	}

	@SuppressWarnings({ "restriction", "deprecation" })
	private String escapeXml(String stringToEscape) {
		return convertToXmlString(stringToEscape);
	}

}
