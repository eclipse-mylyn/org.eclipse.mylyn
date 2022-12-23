/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.index.tests;

import static org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.createTempFolder;
import static org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.deleteFolderRecursively;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.internal.tasks.index.tests.util.MockTestContext;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

@Ignore
public abstract class AbstractTaskListIndexTest {

	protected MockTestContext context;

	protected TaskListIndex index;

	protected File tempDir;

	@Before
	public void setup() throws IOException {
		tempDir = createTempFolder(getClass().getSimpleName());

		context = new MockTestContext();
	}

	@After
	public void tearDown() {
		disposeIndex();
		if (tempDir != null) {
			deleteFolderRecursively(tempDir);
			assertFalse(tempDir.exists());
		}
		if (context != null) {
			context.dispose();
			context = null;
		}
	}

	protected void disposeIndex() {
		if (index != null) {
			try {
				index.waitUntilIdle();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			index.close();
			index = null;
		}
	}

	protected void setupIndex() {
		index = new TaskListIndex(context.getTaskList(), context.getDataManager(),
				(IRepositoryManager) context.getRepositoryManager(), tempDir, 0L);
		index.setDefaultField(TaskListIndex.FIELD_CONTENT);
		index.setReindexDelay(0L);
	}
}
