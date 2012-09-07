/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
	public void testGetReviewersEmpty() {
		dialog.setText("");
		assertEquals(Collections.emptyList(), dialog.getReviewers());
		dialog.setText("   ");
		assertEquals(Collections.emptyList(), dialog.getReviewers());
	}

	@Test
	public void testGetReviewersOne() {
		dialog.setText("  one reviewer  ");
		assertEquals(Arrays.asList("one reviewer"), dialog.getReviewers());
	}

}
