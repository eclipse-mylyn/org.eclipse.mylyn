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

package org.eclipse.mylyn.internal.tasks.ui;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Test;

public class OpenRepositoryTaskJobTest {

	@Test
	public void setsNameFromTaskId() {
		assertEquals("Opening repository task 123", new OpenRepositoryTaskJob("kind", "http://mock", "123",
				"http://mock/123", null).getName());
	}

	@Test
	public void setsNameFromTaskKey() {
		assertEquals("Opening repository task 123", new OpenRepositoryTaskJob(
				new TaskRepository("kind", "http://mock"), "123", "http://mock/123", null).getName());
	}

}
