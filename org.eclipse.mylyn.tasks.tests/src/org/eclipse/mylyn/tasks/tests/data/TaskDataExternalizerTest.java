/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataExternalizer;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataExternalizer.Xml11InputStream;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
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
		//taskRepositoryManager.addRepositoryConnector(new BugzillaRepositoryConnector());
		repository = TaskTestUtil.createMockRepository();
		taskRepositoryManager.addRepository(repository);
		externalizer = new TaskDataExternalizer(taskRepositoryManager);
	}

//	public void testMapFromLegacy() throws Exception {
//		File file = TaskTestUtil.getFile("testdata/taskdata-1.0-bug-219897.zip");
//		ZipInputStream in = new ZipInputStream(new FileInputStream(file));
//		ITaskDataWorkingCopy state;
//		try {
//			in.getNextEntry();
//			state = externalizer.readState(in);
//		} finally {
//			in.close();
//		}
//
//		TaskData taskData = state.getRepositoryData();
//		@SuppressWarnings("unused")
//		TaskMapper taskScheme = new TaskMapper(taskData);
//
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
//	}
//
//	public void testRead() throws Exception {
//		File file = TaskTestUtil.getFile("testdata/taskdata-1.0-bug-219897.zip");
//		ZipInputStream in = new ZipInputStream(new FileInputStream(file));
//		try {
//			in.getNextEntry();
//			@SuppressWarnings("unused")
//			ITaskDataWorkingCopy state = externalizer.readState(in);
//		} finally {
//			in.close();
//		}
//	}
//
//	public void testReadWrite() throws Exception {
//		File file = TaskTestUtil.getFile("testdata/taskdata-1.0-bug-219897.zip");
//		ZipInputStream in = new ZipInputStream(new FileInputStream(file));
//		ITaskDataWorkingCopy state;
//		try {
//			in.getNextEntry();
//			state = externalizer.readState(in);
//		} finally {
//			in.close();
//		}
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		externalizer.writeState(out, state);
//		TaskDataState state2 = externalizer.readState(new ByteArrayInputStream(out.toByteArray()));
//		assertEquals(state.getConnectorKind(), state2.getConnectorKind());
//		assertEquals(state.getRepositoryUrl(), state2.getRepositoryUrl());
//		assertEquals(state.getTaskId(), state2.getTaskId());
//
//		assertEquals(state.getRepositoryData().getRoot().toString(), state2.getRepositoryData().getRoot().toString());
//	}

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
