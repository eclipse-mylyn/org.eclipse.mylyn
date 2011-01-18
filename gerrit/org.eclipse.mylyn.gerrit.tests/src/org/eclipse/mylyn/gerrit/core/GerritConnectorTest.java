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

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;

/**
 * @author Mikael Kober
 */
public class GerritConnectorTest extends TestCase {

	public void testCanCreateNewTask() {
		GerritConnector connector = new GerritConnector();
		assertFalse("should not be able to create new task", connector.canCreateNewTask(null));
	}

	public void testGetConnectorKind() {
		GerritConnector connector = new GerritConnector();
		assertEquals("wrong connector kind", GerritConnector.CONNECTOR_KIND, connector.getConnectorKind());
	}

	public void testGetRepositoryUrlFromTaskUrl() {
		GerritConnector connector = new GerritConnector();
		assertNull("should be null", connector.getRepositoryUrlFromTaskUrl(null));
		assertNull("should be null", connector.getRepositoryUrlFromTaskUrl(""));
		assertNull("should be null", connector.getRepositoryUrlFromTaskUrl("invalid repository url"));
		assertNull("should be null", connector.getRepositoryUrlFromTaskUrl("http://invalid.repository.url"));

		//assertEquals("wrong url", "http://review.source.android.com", connector.getRepositoryUrlFromTaskUrl("http://review.source.android.com/#change,13492"));
	}

	public void testGetTaskIdFromTaskUrl() {
		GerritConnector connector = new GerritConnector();
		connector.getTaskIdFromTaskUrl("");
		assertNull("should be null", connector.getTaskIdFromTaskUrl(null));
		assertNull("should be null", connector.getTaskIdFromTaskUrl(""));
		assertNull("should be null", connector.getTaskIdFromTaskUrl("invalid repository url"));
		assertNull("should be null", connector.getTaskIdFromTaskUrl("http://invalid.repository.url"));
		assertEquals("wrong id", "13492",
				connector.getTaskIdFromTaskUrl("http://review.source.android.com/#change,13492"));
	}

	public void testGetTaskUrl() {
		GerritConnector connector = new GerritConnector();
		assertNull("should be null", connector.getTaskUrl(null, null));
		assertNull("should be null", connector.getTaskUrl("http://review.source.android.com", null));
		assertNull("should be null", connector.getTaskUrl(null, "13492"));
		assertEquals("wrong url", "http://review.source.android.com/#change,13492",
				connector.getTaskUrl("http://review.source.android.com", "13492"));
	}

}
