/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskHyperlinkDetector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.AbstractTaskHyperlinkDetector;

/**
 * @author Steffen Pingel
 */
public class TaskHyperlinkDetectorTest extends TestCase {

	protected MockRepositoryConnectorUi connectorUi;

	protected TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		repository = TaskTestUtil.createMockRepository();
		connectorUi = new MockRepositoryConnectorUi();
	}

	protected IHyperlink[] detect(final String text, int start, int length) {
		AbstractTaskHyperlinkDetector detector = createHyperlinkDetector();
		return detector.detectHyperlinks(new TextViewer() {
			@Override
			public IDocument getDocument() {
				return new Document(text);
			}
		}, new Region(start, length), true);
	}

	protected AbstractTaskHyperlinkDetector createHyperlinkDetector() {
		TaskHyperlinkDetector detector = new TaskHyperlinkDetector() {
			@Override
			protected TaskRepository getTaskRepository(ITextViewer textViewer) {
				return repository;
			}

			@Override
			protected AbstractRepositoryConnectorUi getConnectorUi(TaskRepository repository) {
				return connectorUi;
			}
		};
		return detector;
	}

	public void testMultiple() {
		IHyperlink[] links = detect("123 456 789", 4, 5);
		assertNotNull(links);
		assertEquals(2, links.length);
		assertEquals(new Region(4, 3), links[0].getHyperlinkRegion());
		assertEquals(new Region(8, 1), links[1].getHyperlinkRegion());
	}

	public void testMultipleFullRegion() {
		IHyperlink[] links = detect("123 456 789", 0, 11);
		assertEquals(3, links.length);
		assertEquals(new Region(0, 3), links[0].getHyperlinkRegion());
		assertEquals(new Region(4, 3), links[1].getHyperlinkRegion());
		assertEquals(new Region(8, 3), links[2].getHyperlinkRegion());
	}

	public void testSingleZeroLenghtRegion() {
		IHyperlink[] links = detect("123 456 789", 5, 0);
		assertEquals(1, links.length);
	}

	public void testSpaceZeroLengthRegion() {
		IHyperlink[] links = detect("1234  789", 5, 0);
		assertNull(links);
	}

}
