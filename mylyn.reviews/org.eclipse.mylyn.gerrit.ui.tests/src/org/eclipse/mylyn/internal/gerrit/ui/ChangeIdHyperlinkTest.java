/*******************************************************************************
 * Copyright (c) 2011, 2012 GitHub Inc. and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     GitHub Inc. - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/
package org.eclipse.mylyn.internal.gerrit.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests of
 * {@link GerritConnectorUi#findHyperlinks(org.eclipse.mylyn.tasks.core.TaskRepository, org.eclipse.mylyn.tasks.core.ITask, String, int, int)}
 * 
 * @author Kevin Sawicki
 * @author Steffen Pingel
 */
public class ChangeIdHyperlinkTest {

	private TaskRepository repository;

	private GerritConnectorUi connector;

	private void checkLink(IHyperlink link) {
		assertNotNull(link);
		assertNotNull(link.getHyperlinkRegion());
		assertTrue(link.getHyperlinkRegion().getLength() > 0);
	}

	private String getHyperlinkedText(String text, IHyperlink hyperlink) {
		return text.substring(hyperlink.getHyperlinkRegion().getOffset(),
				hyperlink.getHyperlinkRegion().getOffset() + hyperlink.getHyperlinkRegion().getLength());
	}

	@Before
	public void setUp() {
		connector = new GerritConnectorUi();
		repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://localhost"); //$NON-NLS-1$
	}

	/**
	 * Test with empty text
	 */
	@Test
	public void emptyText() {
		IHyperlink[] links = connector.findHyperlinks(repository, null, "", -1, 0); //$NON-NLS-1$
		assertNull(links);
	}

	/**
	 * Test with invalid hex segment of change id (ends with 't')
	 */
	@Test
	public void invalidHexPortionInTail() {
		String changeId = "Change-Id: I9a2336216f0bc1256073bff692c087cfebff8ct"; //$NON-NLS-1$
		IHyperlink[] links = connector.findHyperlinks(repository, null, changeId, -1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		checkLink(links[0]);
		assertEquals("I9a233621", getHyperlinkedText(changeId, links[0])); //$NON-NLS-1$
	}

	@Test
	public void invalidHexPortionInBeginning() {
		String changeId = "Change-Id: I9az336216f0bc1256073bff692c087cfebff8ct"; //$NON-NLS-1$
		IHyperlink[] links = connector.findHyperlinks(repository, null, changeId, -1, 0);
		assertNull(links);
	}

	/**
	 * Test text with single hyperlink and no padding
	 */
	@Test
	public void singleHyperlinkNoPadding() {
		String changeId = "Change-Id: I9a2336216f0bc1256073bff692c087cfbebff8cc"; //$NON-NLS-1$
		IHyperlink[] links = connector.findHyperlinks(repository, null, changeId, -1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		checkLink(links[0]);
		assertEquals("I9a2336216f0bc1256073bff692c087cfbebff8cc", getHyperlinkedText(changeId, links[0])); //$NON-NLS-1$
	}

	/**
	 * Test text with single hyperlink with padding prefix
	 */
	@Test
	public void singleHyperlinkWithPadding() {
		String changeId = "    Change-Id: I9a2336216f0bc1256073bff692c087cfbebff8cc"; //$NON-NLS-1$
		IHyperlink[] links = connector.findHyperlinks(repository, null, changeId, -1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		checkLink(links[0]);
		assertEquals(15, links[0].getHyperlinkRegion().getOffset());
	}

	/**
	 * Test text with two hyperlinks on different lines
	 */
	@Test
	public void twoHyperlinks() {
		String change1 = "Change-Id: I9a2336216f0bc1256073bff692c087cfbebff8cc"; //$NON-NLS-1$
		String change2 = "Change-Id: I9a2336216f0bc1256073bff692c087cfbebff8cf"; //$NON-NLS-1$
		IHyperlink[] links = connector.findHyperlinks(repository, null, change1 + "\n" + change2, -1, 0); //$NON-NLS-1$
		assertNotNull(links);
		assertEquals(2, links.length);
		for (IHyperlink link : links) {
			checkLink(link);
		}
		assertEquals(11, links[0].getHyperlinkRegion().getOffset());
		assertEquals(change1.length() + 12, links[1].getHyperlinkRegion().getOffset());
	}

	@Test
	public void shortHyperlink() {
		String changeId = "  I12345678 Iabc A01234567"; //$NON-NLS-1$
		IHyperlink[] links = connector.findHyperlinks(repository, null, changeId, -1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		checkLink(links[0]);
		assertEquals("I12345678", getHyperlinkedText(changeId, links[0])); //$NON-NLS-1$
	}

	@Test
	public void shortHyperlinkLocalTask() {
		String changeId = "  I12345678 Iabc A01234567"; //$NON-NLS-1$
		IHyperlink[] links = connector.findHyperlinks(repository, new LocalTask("1", ""), changeId, -1, 0);
		assertNotNull(links);
		assertEquals(1, links.length);
		checkLink(links[0]);
		assertEquals("I12345678", getHyperlinkedText(changeId, links[0])); //$NON-NLS-1$
	}

	@Test
	public void shortHyperlinkLocalTaskMatchingKey() {
		String changeId = "  I12345678 Iabc A01234567"; //$NON-NLS-1$
		TaskTask task = new TaskTask("1", "http://repository", "");
		task.setTaskKey("I12345678");
		IHyperlink[] links = connector.findHyperlinks(repository, task, changeId, -1, 0);
		assertNull(links);
	}

}
