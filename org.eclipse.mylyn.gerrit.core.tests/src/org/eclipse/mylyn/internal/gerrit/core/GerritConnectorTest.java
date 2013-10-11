/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Sascha Scholz (SAP) - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Test;

import com.google.gerrit.reviewdb.Project;
import com.google.gerrit.reviewdb.Project.NameKey;

/**
 * @author Mikael Kober
 */
public class GerritConnectorTest {

	private final GerritConnector connector = new GerritConnector();

	@Test
	public void testCanCreateNewTask() {
		assertFalse(connector.canCreateNewTask(null));
	}

	@Test
	public void testGetConnectorKind() {
		assertEquals(GerritConnector.CONNECTOR_KIND, connector.getConnectorKind());
	}

	@Test
	public void testGetRepositoryUrlFromTaskUrlInvalid() {
		assertNull(connector.getRepositoryUrlFromTaskUrl(null));
		assertNull(connector.getRepositoryUrlFromTaskUrl("")); //$NON-NLS-1$
		assertNull(connector.getRepositoryUrlFromTaskUrl("invalid repository url")); //$NON-NLS-1$
		assertNull(connector.getRepositoryUrlFromTaskUrl("http://invalid.repository.url")); //$NON-NLS-1$
	}

	@Test
	public void testGetRepositoryUrlFromTaskUrlOld() {
		assertEquals(
				"http://review.source.android.com", connector.getRepositoryUrlFromTaskUrl("http://review.source.android.com/#change,13492")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testGetRepositoryUrlFromTaskUrlNew() {
		assertEquals(
				"http://review.source.android.com", connector.getRepositoryUrlFromTaskUrl("http://review.source.android.com/#/c/13492")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(
				"http://review.source.android.com", connector.getRepositoryUrlFromTaskUrl("http://review.source.android.com/#/c/13492/")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(
				"http://review.source.android.com", connector.getRepositoryUrlFromTaskUrl("http://review.source.android.com/#/c/13492/1")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(
				"http://review.source.android.com", connector.getRepositoryUrlFromTaskUrl("http://review.source.android.com/#/c/13492/1/2")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testGetTaskIdFromTaskUrlOld() {
		assertEquals("13492", connector.getTaskIdFromTaskUrl("http://review.source.android.com/#change,13492")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testGetTaskIdFromTaskUrlInvalid() {
		assertNull(connector.getTaskIdFromTaskUrl(null));
		assertNull(connector.getTaskIdFromTaskUrl("")); //$NON-NLS-1$
		assertNull(connector.getTaskIdFromTaskUrl("invalid repository url")); //$NON-NLS-1$
		assertNull(connector.getTaskIdFromTaskUrl("http://invalid.repository.url")); //$NON-NLS-1$
	}

	public void testGetTaskIdFromTaskUrlNew() {
		assertEquals("13492", connector.getTaskIdFromTaskUrl("http://review.source.android.com/#/c/13492")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("13492", connector.getTaskIdFromTaskUrl("http://review.source.android.com/#/c/13492/")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("13492", connector.getTaskIdFromTaskUrl("http://review.source.android.com/#/c/13492/1")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("13492", connector.getTaskIdFromTaskUrl("http://review.source.android.com/#/c/13492/1/2")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testGetTaskUrl() {
		assertEquals("http://review.source.android.com/#/c/13492/", //$NON-NLS-1$
				connector.getTaskUrl("http://review.source.android.com", "13492")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testGetTaskUrlTrailingSlash() {
		assertEquals("http://review.mylyn.org/#/c/4698/", //$NON-NLS-1$
				connector.getTaskUrl("http://review.mylyn.org/", "4698")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void createReviewClient() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"); //$NON-NLS-1$
		GerritClient client = connector.createReviewClient(repository, true);
		assertNull(client.getConfiguration());
		assertNull(client.getGerritConfig());
	}

	@Test
	public void createReviewClientWithConfiguration() {
		Project project = new Project(new NameKey("name")); //$NON-NLS-1$
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"); //$NON-NLS-1$
		GerritConfiguration configuration = new GerritConfiguration(new GerritConfigX(),
				Collections.singletonList(project), null);
		connector.saveConfiguration(repository, configuration);

		GerritClient client = connector.createReviewClient(repository, true);
		List<Project> projects = client.getConfiguration().getProjects();
		assertNotNull(projects);
		assertEquals(1, projects.size());
		assertEquals(project.getNameKey(), projects.get(0).getNameKey());
		assertNotNull(client.getGerritConfig());
	}

	@Test
	public void createTransientReviewClient() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://repository"); //$NON-NLS-1$
		GerritClient client = connector.createTransientReviewClient(repository);
		assertNull(client.getConfiguration());
		assertNull(client.getGerritConfig());
	}

}
