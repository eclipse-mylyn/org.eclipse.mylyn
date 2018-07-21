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
