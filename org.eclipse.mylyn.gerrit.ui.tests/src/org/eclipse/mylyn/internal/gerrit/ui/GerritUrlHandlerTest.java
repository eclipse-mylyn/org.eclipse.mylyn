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

package org.eclipse.mylyn.internal.gerrit.ui;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class GerritUrlHandlerTest {

	private final GerritUrlHandler handler = new GerritUrlHandler();

	@Test
	public void testGetTaskId() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://review.mylyn.org");
		assertEquals("123", handler.getTaskId(repository, "http://review.mylyn.org/123"));
	}

	@Test
	public void testGetTaskIdTrailingSlashAfterId() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://review.mylyn.org");
		assertEquals("123", handler.getTaskId(repository, "http://review.mylyn.org/123/foo/bar"));
	}

	@Test
	public void testGetTaskIdInvalidId() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://mylyn.org/reviews");
		assertEquals(null, handler.getTaskId(repository, "http://mylyn.org/reviews/ab123"));
	}

	@Test
	public void testGetTaskIdRepositoryMismatch() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://review.mylyn.org/");
		assertEquals(null, handler.getTaskId(repository, "http://mylyn.org/reviews/123"));
	}

	@Test
	public void testGetTaskIdSubPath() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://mylyn.org/reviews");
		assertEquals("123", handler.getTaskId(repository, "http://mylyn.org/reviews/123"));
	}

	@Test
	public void testGetTaskIdTrailingSlash() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://review.mylyn.org/");
		assertEquals("123", handler.getTaskId(repository, "http://review.mylyn.org/123"));
	}

	@Test
	public void testGetTaskIdAbsolute() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://review.mylyn.org/");
		assertEquals("123", handler.getTaskId(repository, "http://review.mylyn.org/#/c/123"));
	}

	@Test
	public void testGetTaskIdLetters() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://review.mylyn.org");
		assertEquals(null, handler.getTaskId(repository, "http://review.mylyn.org/#/c/abc/"));
	}

	@Test
	public void testGetTaskIdEmpty() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://review.mylyn.org");
		assertEquals(null, handler.getTaskId(repository, "http://review.mylyn.org/#/c//"));
	}

	@Test
	public void testGetTaskIdAbsoluteTrailingSlash() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://review.mylyn.org/");
		assertEquals("123", handler.getTaskId(repository, "http://review.mylyn.org/#/c/123/"));
	}

	@Test
	public void testGetTaskIdPatchSet() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://review.mylyn.org/");
		assertEquals("4698", handler.getTaskId(repository, "http://review.mylyn.org/#/c/4698/5"));
	}

	@Test
	public void testGetTaskIdFile() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://review.mylyn.org/");
		assertEquals("4698", handler.getTaskId(repository, "http://review.mylyn.org/#/c/4698/5/foo/bar"));
	}

}
