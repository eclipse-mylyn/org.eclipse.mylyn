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
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.data.TaskDataExternalizer;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("deprecation")
public class TaskDataExternalizerTest extends TestCase {

	TaskDataExternalizer externalizer;

	@Override
	protected void setUp() throws Exception {
		externalizer = new TaskDataExternalizer(null);
	}

	public void testRead() throws Exception {
		File file = TaskTestUtil.getFile("testdata/taskdata-1.0-bug-219897.zip");
		ZipInputStream in = new ZipInputStream(new FileInputStream(file));
		try {
			in.getNextEntry();
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
		System.err.println(new String(out.toByteArray()));
		TaskDataState state2 = externalizer.readState(new ByteArrayInputStream(out.toByteArray()));
		assertEquals(state.getConnectorKind(), state2.getConnectorKind());
		assertEquals(state.getRepositoryUrl(), state2.getRepositoryUrl());
		assertEquals(state.getTaskId(), state2.getTaskId());

		assertEquals(state.getRepositoryData().getRoot().toString(), state2.getRepositoryData().getRoot().toString());

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
}
