/*******************************************************************************
 * Copyright (c) 2011 GitHub Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     GitHub Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests of
 * {@link GerritConnectorUi#findHyperlinks(org.eclipse.mylyn.tasks.core.TaskRepository, org.eclipse.mylyn.tasks.core.ITask, String, int, int)}
 * 
 * @author Kevin Sawicki
 */
public class ChangeIdHyperlinkTest {

	private TaskRepository repo;

	private GerritConnectorUi connector;

	private void checkLink(IHyperlink link) {
		assertNotNull(link);
		assertNotNull(link.getHyperlinkRegion());
		assertTrue(link.getHyperlinkRegion().getLength() > 0);
	}

	@Before
	public void setUp() {
		connector = new GerritConnectorUi();
		repo = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://localhost"); //$NON-NLS-1$
	}

	/**
	 * Test with empty text
	 */
	@Test
	public void emptyText() {
		IHyperlink[] links = connector.findHyperlinks(repo, null, "", -1, 0); //$NON-NLS-1$
		assertNull(links);
	}

	/**
	 * Test with invalid hex segment of change id (ends with 't')
	 */
	@Test
	public void invalidHexPortion() {
		String changeId = "Change-Id: I9a2336216f0bc1256073bff692c087cfebff8ct"; //$NON-NLS-1$
		IHyperlink[] links = connector.findHyperlinks(repo, null, changeId, -1, 0);
		assertNull(links);
	}

	/**
	 * Test text with single hyperlink and no padding
	 */
	@Test
	public void singleHyperlinkNoPadding() {
		String changeId = "Change-Id: I9a2336216f0bc1256073bff692c087cfbebff8cc"; //$NON-NLS-1$
		IHyperlink[] links = connector.findHyperlinks(repo, null, changeId, -1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		checkLink(links[0]);
	}

	/**
	 * Test text with single hyperlink with padding prefix
	 */
	@Test
	public void singleHyperlinkWithPadding() {
		String changeId = "    Change-Id: I9a2336216f0bc1256073bff692c087cfbebff8cc"; //$NON-NLS-1$
		IHyperlink[] links = connector.findHyperlinks(repo, null, changeId, -1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		checkLink(links[0]);
		assertEquals(4, links[0].getHyperlinkRegion().getOffset());
	}

	/**
	 * Test text with two hyperlinks on different lines
	 */
	@Test
	public void twoHyperlinks() {
		String change1 = "Change-Id: I9a2336216f0bc1256073bff692c087cfbebff8cc"; //$NON-NLS-1$
		String change2 = "Change-Id: I9a2336216f0bc1256073bff692c087cfbebff8cf"; //$NON-NLS-1$
		IHyperlink[] links = connector.findHyperlinks(repo, null, change1 + "\n" + change2, -1, 0); //$NON-NLS-1$
		assertNotNull(links);
		assertEquals(2, links.length);
		for (IHyperlink link : links) {
			checkLink(link);
		}
		assertEquals(0, links[0].getHyperlinkRegion().getOffset());
		assertEquals(change1.length() + 1, links[1].getHyperlinkRegion().getOffset());
	}
}
