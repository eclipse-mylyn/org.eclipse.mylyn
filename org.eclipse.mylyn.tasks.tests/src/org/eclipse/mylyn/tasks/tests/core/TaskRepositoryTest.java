/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Shawn Minto
 */
public class TaskRepositoryTest extends TestCase {

	private TaskRepository taskRepository;

	@Override
	protected void setUp() throws Exception {
		taskRepository = new TaskRepository("kind", "url");
	}

	public void testSetTaskRepositoryProperty() {
		String key = "key";
		String value = "value";
		taskRepository.setProperty(key, value);
		assertEquals(value, taskRepository.getProperty(key));
	}

	public void testResetTaskRepositoryProperty() {
		String key = "key";
		String value = "value";
		taskRepository.setProperty(key, value);
		assertEquals(value, taskRepository.getProperty(key));
		value = "newValue";
		taskRepository.setProperty(key, value);
		assertEquals(value, taskRepository.getProperty(key));
	}

	public void testSetTaskRepositoryPropertyWithSpace() {
		String key = "key 1";
		String value = "value";
		boolean caughtException = false;
		try {
			taskRepository.setProperty(key, value);
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}
		assertTrue(caughtException);
	}

	public void testSetTaskRepositoryPropertyWithTab() {
		String key = "key\t1";
		String value = "value";
		boolean caughtException = false;
		try {
			taskRepository.setProperty(key, value);
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}
		assertTrue(caughtException);
	}

	public void testSetTaskRepositoryPropertyWithNewline() {
		String key = "key\n1";
		String value = "value";
		boolean caughtException = false;
		try {
			taskRepository.setProperty(key, value);
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}
		assertTrue(caughtException);
	}

	public void testGetCategory() {
		assertNull(taskRepository.getCategory());
	}

	public void testSetCategory() {
		taskRepository.setCategory(TaskRepository.CATEGORY_TASKS);
		assertEquals(TaskRepository.CATEGORY_TASKS, taskRepository.getCategory());
		taskRepository.setCategory("any");
		assertEquals("any", taskRepository.getCategory());
		taskRepository.setCategory(null);
		assertEquals(null, taskRepository.getCategory());
	}

}
