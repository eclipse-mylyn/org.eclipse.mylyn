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
import static org.junit.Assert.assertNull;

import org.junit.Test;

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

}
