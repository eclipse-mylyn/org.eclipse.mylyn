/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataExternalizer;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataExternalizer.Xml11InputStream;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.xml.sax.SAXParseException;

/**
 * @author Steffen Pingel
 */
public class TaskDataExternalizerTest extends TestCase {

	private TaskDataExternalizer externalizer;

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		TaskRepositoryManager taskRepositoryManager = new TaskRepositoryManager();
		taskRepositoryManager.addRepositoryConnector(new MockRepositoryConnector());
		repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL);
		taskRepositoryManager.addRepository(repository);
		externalizer = new TaskDataExternalizer(taskRepositoryManager);
	}

	public void testRead() throws Exception {
		ZipInputStream in = new ZipInputStream(CommonTestUtil.getResource(this, "testdata/taskdata-1.0-bug-219897.zip"));
		try {
			in.getNextEntry();
			@SuppressWarnings("unused")
			ITaskDataWorkingCopy state = externalizer.readState(in);
		} finally {
			in.close();
		}
	}

	public void testReadWrite() throws Exception {
		ZipInputStream in = new ZipInputStream(CommonTestUtil.getResource(this, "testdata/taskdata-1.0-bug-219897.zip"));
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

	public void testReadWriteInvalidCharacters() throws Exception {
		TaskData data = new TaskData(new TaskAttributeMapper(repository), repository.getConnectorKind(),
				repository.getRepositoryUrl(), "1");
		data.getRoot().createAttribute("attribute").setValue("\u0001\u001F");

		TaskDataState state = new TaskDataState(repository.getConnectorKind(), repository.getRepositoryUrl(), "1");
		state.setRepositoryData(data);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		externalizer.writeState(out, state);
		try {
			externalizer.readState(new ByteArrayInputStream(out.toByteArray()));
			fail("Expected SAXParseException");
		} catch (SAXParseException expected) {
		}

		TaskDataState state2 = externalizer.readState(new Xml11InputStream(new ByteArrayInputStream(out.toByteArray())));
		assertEquals(state.getRepositoryData().getRoot().toString(), state2.getRepositoryData().getRoot().toString());
		assertEquals("\u0001\u001F", state2.getRepositoryData().getRoot().getAttribute("attribute").getValue());
	}

}
