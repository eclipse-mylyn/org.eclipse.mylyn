/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager2;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataState;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataUtil;
import org.eclipse.mylyn.tasks.core.IdentityAttributeFactory;
import org.eclipse.mylyn.tasks.core.IdentityAttributeMapper;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskScheme;
import org.eclipse.mylyn.tasks.core.data.ITaskDataState;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;

public class TaskDataManagerTest extends TestCase {

	TaskDataManager2 taskDataManager;

	@Override
	protected void setUp() throws Exception {
		taskDataManager = new TaskDataManager2(null);
	}

	public void testRead() throws Exception {
		File file = TaskTestUtil.getFile("testdata/taskdata-1.0-bug-219897.zip");
		ZipInputStream in = new ZipInputStream(new FileInputStream(file));
		try {
			in.getNextEntry();
			ITaskDataState state = taskDataManager.readState(in);
		} finally {
			in.close();
		}
	}

	public void testReadWrite() throws Exception {
		File file = TaskTestUtil.getFile("testdata/taskdata-1.0-bug-219897.zip");
		ZipInputStream in = new ZipInputStream(new FileInputStream(file));
		ITaskDataState state;
		try {
			in.getNextEntry();
			state = taskDataManager.readState(in);
		} finally {
			in.close();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		taskDataManager.writeState(out, state);
		System.err.println(new String(out.toByteArray()));
		TaskDataState state2 = taskDataManager.readState(new ByteArrayInputStream(out.toByteArray()));
		assertEquals(state.getConnectorKind(), state2.getConnectorKind());
		assertEquals(state.getRepositoryUrl(), state2.getRepositoryUrl());
		assertEquals(state.getTaskId(), state2.getTaskId());

		assertEquals(state.getRepositoryData().getRoot().toString(), state2.getRepositoryData().getRoot().toString());

	}

	public void testMapFromLegacy() throws Exception {
		File file = TaskTestUtil.getFile("testdata/taskdata-1.0-bug-219897.zip");
		ZipInputStream in = new ZipInputStream(new FileInputStream(file));
		ITaskDataState state;
		try {
			in.getNextEntry();
			state = taskDataManager.readState(in);
		} finally {
			in.close();
		}

		TaskData taskData = state.getRepositoryData();
		TaskScheme taskScheme = new TaskScheme(taskData);

		RepositoryTaskData legacyData = TaskDataUtil.toLegacyData(taskData, IdentityAttributeFactory.getInstance());
		assertEquals(taskData.getConnectorKind(), legacyData.getConnectorKind());
		assertEquals(taskData.getRepositoryUrl(), legacyData.getRepositoryUrl());
		assertEquals(taskData.getTaskId(), legacyData.getTaskId());
		assertEquals(taskScheme.getTaskKind(), legacyData.getTaskKind());
		assertEquals(taskScheme.getComments().length, legacyData.getComments().size());
		assertEquals(taskScheme.getAttachments().length, legacyData.getAttachments().size());
		assertEquals(taskScheme.getOperations().length, legacyData.getOperations().size());

		TaskData taskData2 = TaskDataUtil.toTaskData(legacyData, IdentityAttributeMapper.getInstance());
		assertEquals(taskData.getConnectorKind(), taskData2.getConnectorKind());
		assertEquals(taskData.getRepositoryUrl(), taskData2.getRepositoryUrl());
		assertEquals(taskData.getTaskId(), taskData2.getTaskId());

		assertEquals(taskData.getRoot().toString(), taskData2.getRoot().toString());
	}
}
