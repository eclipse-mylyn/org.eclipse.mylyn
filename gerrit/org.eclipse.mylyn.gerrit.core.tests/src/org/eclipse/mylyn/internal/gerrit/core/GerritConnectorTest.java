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

import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.junit.Test;

/**
 * @author Mikael Kober
 */
public class GerritConnectorTest {

	@Test
	public void testCanCreateNewTask() {
		GerritConnector connector = new GerritConnector();
		assertFalse(connector.canCreateNewTask(null));
	}

	@Test
	public void testGetConnectorKind() {
		GerritConnector connector = new GerritConnector();
		assertEquals(GerritConnector.CONNECTOR_KIND, connector.getConnectorKind());
	}

	@Test
	public void testGetRepositoryUrlFromTaskUrl() {
		GerritConnector connector = new GerritConnector();
		assertNull(connector.getRepositoryUrlFromTaskUrl(null));
		assertNull(connector.getRepositoryUrlFromTaskUrl("")); //$NON-NLS-1$
		assertNull(connector.getRepositoryUrlFromTaskUrl("invalid repository url")); //$NON-NLS-1$
		assertNull(connector.getRepositoryUrlFromTaskUrl("http://invalid.repository.url")); //$NON-NLS-1$
	}

	@Test
	public void testGetTaskIdFromTaskUrl() {
		GerritConnector connector = new GerritConnector();
		assertNull(connector.getTaskIdFromTaskUrl(null));
		assertNull(connector.getTaskIdFromTaskUrl("")); //$NON-NLS-1$
		assertNull(connector.getTaskIdFromTaskUrl("invalid repository url")); //$NON-NLS-1$
		assertNull(connector.getTaskIdFromTaskUrl("http://invalid.repository.url")); //$NON-NLS-1$
		assertEquals("13492", connector.getTaskIdFromTaskUrl("http://review.source.android.com/#change,13492")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testGetTaskUrl() {
		GerritConnector connector = new GerritConnector();
		assertNull(connector.getTaskUrl(null, null));
		assertNull(connector.getTaskUrl("http://review.source.android.com", null)); //$NON-NLS-1$
		assertNull(connector.getTaskUrl(null, "13492")); //$NON-NLS-1$
		assertEquals("http://review.source.android.com/#change,13492", //$NON-NLS-1$
				connector.getTaskUrl("http://review.source.android.com", "13492")); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
