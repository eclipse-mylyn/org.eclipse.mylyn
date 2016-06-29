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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.helpers.AttributesImpl;

public class SaxOrphanBuilderTest {

	private SaxOrphanBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new SaxOrphanBuilder();
	}

	@Test
	public void parseWithNoCommit() {
		prepareTask("123");
		builder.endElement();

		prepareTask("456");
		builder.endElement();

		assertOrphans(0);
	}

	@Test
	public void parseWithCommit() {
		prepareTask("123");
		builder.endElement();

		prepareTask("456");
		builder.commitOrphan();
		builder.endElement();

		assertOrphans(1, "456");

	}

	@Test
	public void parseWithMiddleCommit() {
		prepareTask("123");
		builder.endElement();

		prepareTask("345");
		builder.commitOrphan();
		builder.endElement();

		prepareTask("567");
		builder.endElement();

		assertOrphans(1, "345");
	}

	@Test
	public void parseWithMultipleCommit() {
		prepareTask("123");
		builder.endElement();

		prepareTask("o1");
		builder.commitOrphan();
		builder.endElement();

		prepareTask("o2");
		builder.commitOrphan();
		builder.endElement();

		prepareTask("o3");
		builder.commitOrphan();
		builder.endElement();

		prepareTask("567");
		builder.endElement();

		prepareTask("o4");
		builder.commitOrphan();
		builder.endElement();

		prepareTask("o5");
		builder.commitOrphan();
		builder.endElement();

		assertOrphans(5, "o1", "o2", "o3", "o4", "o5");
	}

	private void prepareTask(String attributeValue) {
		start("task");
		start("attribute");
		text(attributeValue);
		builder.endElement();
	}

	private void start(String name) {
		builder.startElement(name, new AttributesImpl());
	}

	private void text(String text) {
		builder.acceptCharacters(text.toCharArray(), 0, text.length());
	}

	private void assertOrphans(int numOrphans, String... attributeValue) {
		Document document = builder.getOrphans();
		assertEquals(1, document.getChildNodes().getLength());
		NodeList orphans = document.getDocumentElement().getChildNodes();
		assertEquals(numOrphans, orphans.getLength());
		for (int i = 0; i < attributeValue.length; i++) {
			Node text = orphans.item(i).getFirstChild().getFirstChild();
			assertTrue(text instanceof Text);
			assertEquals(attributeValue[i], ((Text) text).getTextContent());
		}
	}

}
