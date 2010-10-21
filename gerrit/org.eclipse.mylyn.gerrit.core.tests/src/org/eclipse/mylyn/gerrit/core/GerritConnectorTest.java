/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.gerrit.core;

import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;

import junit.framework.TestCase;

/**
 * Testclass for GerritConnector.
 * @author Mikael Kober, Sony Ericsson
 *
 */
public class GerritConnectorTest extends TestCase {

	/**
	 * tests canCreateNewTask()
	 */
	public void testCanCreateNewTask() {
		GerritConnector connector = new GerritConnector();
		assertFalse("should not be able to create new task", connector.canCreateNewTask(null));
	}

	/**
	 * tests canCreateTaskFromKey()
	 */
	public void testCanCreateTaskFromKey() {
		GerritConnector connector = new GerritConnector();
		assertTrue("should be able to create a task from key", connector.canCreateTaskFromKey(null));
	}

	/**
	 * test getConnctorKind()
	 */
	public void testGetConnectorKind() {
		GerritConnector connector = new GerritConnector();
		assertEquals("wrong connector kind", GerritConnector.CONNECTOR_KIND, connector.getConnectorKind());
	}

	/**
	 * tests getRepositoryUrlFromTaskUrl()
	 */
	public void testGetRepositoryUrlFromTaskUrl() {
		GerritConnector connector = new GerritConnector();
		assertNull("should be null", connector.getRepositoryUrlFromTaskUrl(null));
		assertNull("should be null", connector.getRepositoryUrlFromTaskUrl(""));
		assertNull("should be null", connector.getRepositoryUrlFromTaskUrl("invalid repository url"));
		assertNull("should be null", connector.getRepositoryUrlFromTaskUrl("http://invalid.repository.url"));
		
		//assertEquals("wrong url", "http://review.source.android.com", connector.getRepositoryUrlFromTaskUrl("http://review.source.android.com/#change,13492"));
	}

	/**
	 * tests getTaskIdFromTaskUrl()
	 */
	public void testGetTaskIdFromTaskUrl() {
		GerritConnector connector = new GerritConnector();
		connector.getTaskIdFromTaskUrl("");
		assertNull("should be null", connector.getTaskIdFromTaskUrl(null));
		assertNull("should be null", connector.getTaskIdFromTaskUrl(""));
		assertNull("should be null", connector.getTaskIdFromTaskUrl("invalid repository url"));
		assertNull("should be null", connector.getTaskIdFromTaskUrl("http://invalid.repository.url"));
		assertEquals("wrong id", "13492", connector.getTaskIdFromTaskUrl("http://review.source.android.com/#change,13492"));
	}

	/**
	 * tests getTaskUrl()
	 */
	public void testGetTaskUrl() {
		GerritConnector connector = new GerritConnector();
		assertNull("should be null", connector.getTaskUrl(null, null));
		assertNull("should be null", connector.getTaskUrl("http://review.source.android.com", null));
		assertNull("should be null", connector.getTaskUrl(null, "13492"));
		assertEquals("wrong url", "http://review.source.android.com/#change,13492", connector.getTaskUrl("http://review.source.android.com", "13492"));
	}

}
