/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.zip.ZipInputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.data.ITaskDataConstants;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataExternalizer;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Steffen Pingel
 */
public class TaskDataExternalizerTest extends TestCase {

	private class SimpleCharacterReader extends DefaultHandler {

		private char ch;

		public SimpleCharacterReader() {
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			Assert.assertEquals(1, length);
			this.ch = ch[start];
		}

		public char getCharacter() {
			return ch;
		}

	}

	private final class SimpleCharacterWriter {
		private final TransformerHandler handler;

		public SimpleCharacterWriter(TransformerHandler handler) {
			this.handler = handler;
		}

		public void write(char character) throws SAXException {
			handler.startDocument();
			AttributesImpl atts = new AttributesImpl();
			handler.startElement("", "", ITaskDataConstants.ELEMENT_VALUE, atts); //$NON-NLS-1$ //$NON-NLS-2$
			///handler.startCDATA();
			handler.characters(new char[] { character }, 0, 1);
			//handler.endCDATA();
			handler.endElement("", "", ITaskDataConstants.ELEMENT_VALUE);
			handler.endDocument();
		}
	}

	TaskDataExternalizer externalizer;

	@Override
	protected void setUp() throws Exception {
		externalizer = new TaskDataExternalizer(null);
	}

	public void testMapFromLegacy() throws Exception {
		File file = TaskTestUtil.getFile("testdata/taskdata-1.0-bug-219897.zip");
		ZipInputStream in = new ZipInputStream(new FileInputStream(file));
		ITaskDataWorkingCopy state;
		try {
			in.getNextEntry();
			state = externalizer.readState(in);
		} finally {
			in.close();
		}

		TaskData taskData = state.getRepositoryData();
		@SuppressWarnings("unused")
		TaskMapper taskScheme = new TaskMapper(taskData);

		fail("fixme");
//		RepositoryTaskData legacyData = TaskDataUtil.toLegacyData(taskData, IdentityAttributeFactory.getInstance());
//		assertEquals(taskData.getConnectorKind(), legacyData.getConnectorKind());
//		assertEquals(taskData.getRepositoryUrl(), legacyData.getRepositoryUrl());
//		assertEquals(taskData.getTaskId(), legacyData.getTaskId());
//		assertEquals(taskScheme.getTaskKind(), legacyData.getTaskKind());
//		assertEquals(taskScheme.getComments().length, legacyData.getComments().size());
//		assertEquals(taskScheme.getAttachments().length, legacyData.getAttachments().size());
//
//		TaskData taskData2 = TaskDataUtil.toTaskData(legacyData, IdentityAttributeMapper.getInstance());
//		assertEquals(taskData.getConnectorKind(), taskData2.getConnectorKind());
//		assertEquals(taskData.getRepositoryUrl(), taskData2.getRepositoryUrl());
//		assertEquals(taskData.getTaskId(), taskData2.getTaskId());
//
//		assertEquals(taskData.getRoot().toString(), taskData2.getRoot().toString());
	}

	public void testRead() throws Exception {
		File file = TaskTestUtil.getFile("testdata/taskdata-1.0-bug-219897.zip");
		ZipInputStream in = new ZipInputStream(new FileInputStream(file));
		try {
			in.getNextEntry();
			@SuppressWarnings("unused")
			ITaskDataWorkingCopy state = externalizer.readState(in);
		} finally {
			in.close();
		}
	}

	public void testReadWrite() throws Exception {
		File file = TaskTestUtil.getFile("testdata/taskdata-1.0-bug-219897.zip");
		ZipInputStream in = new ZipInputStream(new FileInputStream(file));
		ITaskDataWorkingCopy state;
		try {
			in.getNextEntry();
			state = externalizer.readState(in);
		} finally {
			in.close();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		externalizer.writeState(out, state);
		TaskDataState state2 = externalizer.readState(new ByteArrayInputStream(out.toByteArray()));
		assertEquals(state.getConnectorKind(), state2.getConnectorKind());
		assertEquals(state.getRepositoryUrl(), state2.getRepositoryUrl());
		assertEquals(state.getTaskId(), state2.getTaskId());

		assertEquals(state.getRepositoryData().getRoot().toString(), state2.getRepositoryData().getRoot().toString());

	}

	public void testWriteandReadBadCharacter() throws Exception {
		for (int i = 0; i < 0xFFFF; i++) {
			char badChar = (char) i;

			StringWriter stringWriter = new StringWriter();
			SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
			TransformerHandler handler = transformerFactory.newTransformerHandler();
			Transformer serializer = handler.getTransformer();
			serializer.setOutputProperty(OutputKeys.VERSION, "1.0");
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			serializer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			handler.setResult(new StreamResult(stringWriter));
			SimpleCharacterWriter writer = new SimpleCharacterWriter(handler);
			writer.write(badChar);

			XMLReader parser = XMLReaderFactory.createXMLReader();
			SimpleCharacterReader readHandler = new SimpleCharacterReader();
			parser.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					System.out.println(publicId);
					System.out.println(systemId);
					return null;
				}
			});
			parser.setContentHandler(readHandler);
			parser.parse(new InputSource(new StringReader(stringWriter.getBuffer().toString())));
			char character = readHandler.getCharacter();
			assertEquals(badChar, character);
		}
	}
}
