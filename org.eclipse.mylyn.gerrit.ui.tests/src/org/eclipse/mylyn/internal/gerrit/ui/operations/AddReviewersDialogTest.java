/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.operations;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class AddReviewersDialogTest {

	private Shell shell;

	private AddReviewersDialog dialog;

	@Before
	public void setUp() {
		shell = new Shell();

		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "url");
		TasksUi.getRepositoryManager().addRepository(repository);
		ITask task = TasksUi.getRepositoryModel().createTask(repository, "1");
		dialog = new AddReviewersDialog(shell, task);
		dialog.create();
	}

	@After
	public void tearDown() {
		shell.dispose();
	}

	@Test
	public void testGetReviewers() {
		dialog.setText("a, b, c");
		assertEquals(Arrays.asList("a", "b", "c"), dialog.getReviewers());
	}

	@Test
	public void testGetReviewersTwo() {
		dialog.setText("b,a");
		assertEquals(Arrays.asList("b", "a"), dialog.getReviewers());
	}

	@Test
	public void testGetReviewersWithComma() {
		dialog.setText("a,b,");
		assertEquals(Arrays.asList("a", "b"), dialog.getReviewers());
	}

	@Test
	public void testGetReviewersEmpty() {
		dialog.setText("");
		assertEquals(Collections.emptyList(), dialog.getReviewers());
	}

	@Test
	public void testGetReviewersBlank() {
		dialog.setText("   ");
		assertEquals(Collections.emptyList(), dialog.getReviewers());
	}

	@Test
	public void testGetReviewersCommasOnly() {
		dialog.setText(",,,");
		assertEquals(Collections.emptyList(), dialog.getReviewers());
	}

	@Test
	public void testGetReviewersOne() {
		dialog.setText("  one reviewer  ");
		assertEquals(Arrays.asList("one reviewer"), dialog.getReviewers());
	}

}
