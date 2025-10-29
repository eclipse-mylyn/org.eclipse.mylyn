/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.sdk.util.ResourceTestUtil;
import org.eclipse.mylyn.commons.sdk.util.TestProject;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import junit.framework.TestCase;

/**
 * @author Rob Elves
 */
@SuppressWarnings("nls")
public class ProjectRepositoryAssociationTest extends TestCase {

	private static final String REPOSITORY_URL = "http://mylyn.eclipse.org/bugs222";

	private static final String REPOSITORY_KIND = "bugzilla";

	private TestProject projectWrapper;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		projectWrapper = new TestProject(this.getClass().getName());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		ResourceTestUtil.deleteProject(projectWrapper.getProject());
		TasksUiPlugin.getRepositoryManager().clearRepositories();
	}

	public void testRepositoryForProject() throws CoreException {
		assertNull(TasksUiPlugin.getDefault().getRepositoryForResource(projectWrapper.getProject()));
		TaskRepository repository = new TaskRepository(REPOSITORY_KIND, REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
		TasksUiPlugin.getDefault().setRepositoryForResource(projectWrapper.getProject(), repository);
		TaskRepository returnedRepository = TasksUiPlugin.getDefault()
				.getRepositoryForResource(projectWrapper.getProject());
		assertNotNull(returnedRepository);
		assertEquals(REPOSITORY_KIND, returnedRepository.getConnectorKind());
		assertEquals(REPOSITORY_URL, returnedRepository.getRepositoryUrl());

		TasksUiPlugin.getRepositoryManager().removeRepository(repository);
	}

	public void testRepositoryForFolder() throws CoreException {
		IFolder folder = projectWrapper.createFolder("testFolder");
		assertTrue(folder.exists());
		assertNull(TasksUiPlugin.getDefault().getRepositoryForResource(folder));
		TaskRepository repository = new TaskRepository(REPOSITORY_KIND, REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
		TasksUiPlugin.getDefault().setRepositoryForResource(folder, repository);
		TaskRepository returnedRepository = TasksUiPlugin.getDefault().getRepositoryForResource(folder);
		assertNotNull(returnedRepository);
		assertEquals(REPOSITORY_KIND, returnedRepository.getConnectorKind());
		assertEquals(REPOSITORY_URL, returnedRepository.getRepositoryUrl());

		TasksUiPlugin.getRepositoryManager().removeRepository(repository);
	}
}
